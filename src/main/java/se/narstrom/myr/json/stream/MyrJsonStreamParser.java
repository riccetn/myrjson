package se.narstrom.myr.json.stream;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParsingException;

public final class MyrJsonStreamParser extends MyrJsonParserBase {
	private final MyrReader reader;

	private final Deque<State> stack = new ArrayDeque<>();

	private Event event = null;

	private JsonLocation location = null;

	private String stringValue = null;

	private BigDecimal numberValue = null;

	private State state = State.INIT;

	public MyrJsonStreamParser(final MyrReader reader) {
		this.reader = reader;
	}

	@Override
	public Event currentEvent() {
		return event;
	}

	@Override
	public boolean hasNext() {
		return state != State.END;
	}

	@Override
	public String getString() {
		if (stringValue == null)
			throw new IllegalStateException();
		return stringValue;
	}

	@Override
	public BigDecimal getBigDecimal() {
		if (numberValue == null)
			throw new IllegalStateException();
		return numberValue;
	}

	@Override
	public JsonLocation getLocation() {
		return location;
	}

	@Override
	public void close() {
		try {
			reader.close();
		} catch (final IOException ex) {
			throw new JsonParsingException(ex.getMessage(), ex, location);
		}
	}

	@Override
	public Event next() {
		try {
			event = null;
			location = null;
			stringValue = null;
			numberValue = null;

			while (event == null) {
				switch (state) {
					case INIT -> nextInit();
					case OBJECT_INIT -> nextObjectInit();
					case OBJECT_KEY -> nextObjectKey();
					case OBJECT_VALUE -> nextObjectValue();
					case OBJECT_COMMA -> nextObjectComma();
					case ARRAY_INIT -> nextArrayInit();
					case ARRAY_VALUE -> nextArrayValue();
					case ARRAY_COMMA -> nextArrayComma();
					case END -> throw new NoSuchElementException("End of stream");
				}
			}
			return event;
		} catch (final IOException ex) {
			throw new JsonParsingException(ex.getMessage(), ex, location);
		}
	}

	private void nextInit() throws IOException {
		reader.skipWhitespace();
		location = reader.getLocation();

		final char ch = reader.readChar();
		switch (ch) {
			case '{' -> startObject(State.END);
			case '[' -> startArray(State.END);
			default -> throw new JsonParsingException("Unexpected " + ch + ", state: " + state, location);
		}
	}

	private void nextObjectInit() throws IOException {
		reader.skipWhitespace();
		location = reader.getLocation();

		final char ch = reader.readChar();
		switch (ch) {
			case '}' -> endObject();
			case '\"' -> keyName();
			default -> throw new JsonParsingException("Unexpected " + ch + ", state: " + state, location);
		}
	}

	private void nextObjectKey() throws IOException {
		reader.skipWhitespace();
		location = reader.getLocation();

		final char ch = reader.readChar();
		if (ch != '\"')
			throw new JsonParsingException("Unexpected " + ch + ", sate: " + state, location);
		keyName();
	}

	private void nextObjectValue() throws IOException {
		nextValue(State.OBJECT_COMMA);
	}

	private void nextObjectComma() throws IOException {
		reader.skipWhitespace();
		location = reader.getLocation();

		final char ch = reader.readChar();
		switch (ch) {
			case '}' -> endObject();
			case ',' -> state = State.OBJECT_KEY;
			default -> throw new JsonParsingException("Unexpected " + ch + ", state: " + state, location);
		}
	}

	private void nextArrayInit() throws IOException {
		reader.skipWhitespace();
		location = reader.getLocation();

		if (reader.peekChar() == ']') {
			reader.readChar();
			endArray();
			return;
		}

		nextValue(State.ARRAY_COMMA);
	}

	private void nextArrayValue() throws IOException {
		nextValue(State.ARRAY_COMMA);
	}

	private void nextArrayComma() throws IOException {
		reader.skipWhitespace();
		location = reader.getLocation();

		final char ch = reader.readChar();
		switch (ch) {
			case ']' -> endArray();
			case ',' -> state = State.ARRAY_VALUE;
			default -> throw new JsonParsingException("Unexpected " + ch + ", state: " + state, location);
		}
	}

	private void nextValue(final State nextState) throws IOException {
		reader.skipWhitespace();
		location = reader.getLocation();

		final char ch = reader.readChar();
		switch (ch) {
			case '{' -> startObject(nextState);
			case '[' -> startArray(nextState);
			case '\"' -> valueString(nextState);
			case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> valueNumber(nextState, ch);
			default -> valueKeyword(nextState, ch);
		}
	}

	private void startObject(final State nextState) {
		stack.push(nextState);
		state = State.OBJECT_INIT;
		event = Event.START_OBJECT;
	}

	private void endObject() {
		state = stack.pop();
		event = Event.END_OBJECT;
	}

	private void keyName() throws IOException {
		parseString();
		reader.skipWhitespace();
		if (reader.readChar() != ':')
			throw new JsonParsingException("Expected ':' after key", location);
		state = State.OBJECT_VALUE;
		event = Event.KEY_NAME;
	}

	private void startArray(final State nextState) {
		stack.push(nextState);
		state = State.ARRAY_INIT;
		event = Event.START_ARRAY;
	}

	private void endArray() {
		state = stack.pop();
		event = Event.END_ARRAY;
	}

	private void valueString(final State nextState) throws IOException {
		parseString();
		state = nextState;
		event = Event.VALUE_STRING;
	}

	private void valueNumber(final State nextState, char ch) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(ch);

		if (ch == '-') {
			ch = reader.readChar();
			if (ch < '0' || ch > '9')
				throw new JsonParsingException("Not a number", location);
			sb.append(ch);
		}

		if (ch != '0') {
			readDigits(sb);
		}

		ch = reader.peekChar();

		if (ch == '.') {
			sb.append('.');
			reader.readChar();
			readDigits(sb);
			ch = reader.peekChar();
		}

		if (ch == 'e' || ch == 'E') {
			sb.append(ch);
			reader.readChar();
			ch = reader.readChar();
			if (ch != '+' && ch != '-' && (ch < '0' || ch > '9'))
				throw new JsonParsingException("Not a number", location);
			sb.append(ch);
			readDigits(sb);
		}

		numberValue = new BigDecimal(sb.toString());
		state = nextState;
		event = Event.VALUE_NUMBER;
	}

	private void valueKeyword(final State nextState, final char ch0) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(ch0);

		while (sb.length() < 5) {
			final char ch = reader.peekChar();
			if (ch < 'a' || ch > 'z')
				break;
			reader.readChar();
			sb.append(ch);
		}

		final String keyword = sb.toString();
		switch (keyword) {
			case "true" -> event = Event.VALUE_TRUE;
			case "false" -> event = Event.VALUE_FALSE;
			case "null" -> event = Event.VALUE_NULL;
			default -> throw new JsonParsingException("Unknown keyword: " + keyword, location);
		}

		state = nextState;
	}

	private void readDigits(final StringBuilder sb) throws IOException {
		while (true) {
			final char ch = reader.peekChar();
			if (ch < '0' || ch > '9')
				return;

			reader.readChar();
			sb.append(ch);
		}
	}

	private void parseString() throws IOException {
		final StringBuilder sb = new StringBuilder();
		char ch;
		while ((ch = reader.readChar()) != '"') {
			if (ch == '\\') {
				ch = reader.readChar();
				switch (ch) {
					case '\"' -> sb.append('\"');
					case '\\' -> sb.append('\\');
					case '/' -> sb.append('/');
					case 'b' -> sb.append('\b');
					case 'f' -> sb.append('\f');
					case 'n' -> sb.append('\n');
					case 'r' -> sb.append('\r');
					case 't' -> sb.append('\t');
					case 'u' -> {
						char[] chs = new char[4];
						reader.readChars(chs);
						sb.append((char) Integer.parseUnsignedInt(new String(chs), 16));
					}
					default -> throw new JsonParsingException("Unknown escape character " + ch, location);
				}
			} else {
				sb.append(ch);
			}
		}
		stringValue = sb.toString();
	}

	private enum State {
		INIT, OBJECT_INIT, OBJECT_KEY, OBJECT_VALUE, OBJECT_COMMA, ARRAY_INIT, ARRAY_VALUE, ARRAY_COMMA, END
	}
}

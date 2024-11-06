package se.narstrom.myr.json.parser;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Objects;

import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParsingException;
import se.narstrom.myr.json.MyrJsonContext;

public final class MyrJsonStreamParser extends MyrJsonParserBase {
	private final Deque<State> stack = new ArrayDeque<>();

	private final Reader in;

	private Event event = null;

	private JsonLocation location = null;

	private String stringValue = null;

	private BigDecimal numberValue = null;

	private State state = State.INIT;

	private char[] buf = new char[4096];

	private int buflen = 0;

	private int bufp = 0;

	private long lineNo = 1;

	private long columnNo = 1;

	private long offset = 0;

	public MyrJsonStreamParser(final Reader in, final MyrJsonContext context) {
		super(context);
		this.in = in;
	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (final IOException ex) {
			throw new JsonParsingException(ex.getMessage(), ex, location);
		}
	}

	@Override
	public Event currentEvent() {
		return event;
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
	public String getString() {
		if (stringValue == null)
			throw new IllegalStateException();
		return stringValue;
	}

	@Override
	public boolean hasNext() {
		return state != State.END;
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

	private JsonLocation createLocation() {
		return new MyrJsonLocation(lineNo, columnNo, offset);
	}

	private void endArray() {
		state = stack.pop();
		event = Event.END_ARRAY;
	}

	private void endObject() {
		state = stack.pop();
		event = Event.END_OBJECT;
	}

	private void keyName() throws IOException {
		parseString();
		skipWhitespace();
		if (readChar() != ':')
			throw new JsonParsingException("Expected ':' after key", location);
		state = State.OBJECT_VALUE;
		event = Event.KEY_NAME;
	}

	private void maybeFillBuffer() throws IOException {
		if (bufp >= buflen)
			buflen = in.read(buf);
	}

	private void nextArrayComma() throws IOException {
		skipWhitespace();
		location = createLocation();

		final char ch = readChar();
		switch (ch) {
			case ']' -> endArray();
			case ',' -> state = State.ARRAY_VALUE;
			default -> throw new JsonParsingException("Unexpected " + ch + ", state: " + state, location);
		}
	}

	private void nextArrayInit() throws IOException {
		skipWhitespace();
		location = createLocation();

		if (peekChar() == ']') {
			readChar();
			endArray();
			return;
		}

		nextValue(State.ARRAY_COMMA);
	}

	private void nextArrayValue() throws IOException {
		nextValue(State.ARRAY_COMMA);
	}

	private void nextInit() throws IOException {
		nextValue(State.END);
	}

	private void nextObjectComma() throws IOException {
		skipWhitespace();
		location = createLocation();

		final char ch = readChar();
		switch (ch) {
			case '}' -> endObject();
			case ',' -> state = State.OBJECT_KEY;
			default -> throw new JsonParsingException("Unexpected " + ch + ", state: " + state, location);
		}
	}

	private void nextObjectInit() throws IOException {
		skipWhitespace();
		location = createLocation();

		final char ch = readChar();
		switch (ch) {
			case '}' -> endObject();
			case '\"' -> keyName();
			default -> throw new JsonParsingException("Unexpected " + ch + ", state: " + state, location);
		}
	}

	private void nextObjectKey() throws IOException {
		skipWhitespace();
		location = createLocation();

		final char ch = readChar();
		if (ch != '\"')
			throw new JsonParsingException("Unexpected " + ch + ", sate: " + state, location);
		keyName();
	}

	private void nextObjectValue() throws IOException {
		nextValue(State.OBJECT_COMMA);
	}

	private void nextValue(final State nextState) throws IOException {
		skipWhitespace();
		location = createLocation();

		final char ch = readChar();
		switch (ch) {
			case '{' -> startObject(nextState);
			case '[' -> startArray(nextState);
			case '\"' -> valueString(nextState);
			case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> valueNumber(nextState, ch);
			default -> valueKeyword(nextState, ch);
		}
	}

	private void parseString() throws IOException {
		final StringBuilder sb = new StringBuilder();
		char ch;
		while ((ch = readChar()) != '"') {
			if (ch == '\\') {
				ch = readChar();
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
						readChars(chs);
						try {
							sb.append((char) Integer.parseUnsignedInt(new String(chs), 16));
						} catch (final NumberFormatException ex) {
							throw new JsonParsingException(ex.getMessage(), ex, location);
						}
					}
					default -> throw new JsonParsingException("Unknown escape character " + ch, location);
				}
			} else {
				sb.append(ch);
			}
		}
		stringValue = sb.toString();
	}

	private int peekChar() throws IOException {
		maybeFillBuffer();
		if (buflen == -1)
			return -1;
		return buf[bufp];
	}

	private int read() throws IOException {
		maybeFillBuffer();
		if (buflen == -1)
			return -1;

		final char ch = buf[bufp++];
		if (ch == '\n') {
			++lineNo;
			columnNo = 1;
		} else {
			++columnNo;
		}
		++offset;

		return ch;
	}

	private int read(final char[] cbuf, final int off, final int len) throws IOException {
		Objects.checkFromIndexSize(off, len, cbuf.length);
		maybeFillBuffer();
		if (buflen == -1)
			return -1;

		final int realLen = Math.min(len, buflen - bufp);
		System.arraycopy(buf, bufp, cbuf, off, realLen);

		for (int i = bufp; i < bufp + realLen; ++i) {
			if (buf[i] == '\n') {
				++lineNo;
				columnNo = 1;
			} else {
				++columnNo;
			}
		}
		bufp += realLen;
		offset += realLen;

		return realLen;
	}

	private char readChar() throws IOException {
		int ch = read();
		if (ch == -1)
			throw new EOFException();
		return (char) ch;
	}

	private void readChars(final char[] chs) throws IOException {
		int read = 0;
		while (read < chs.length) {
			int r = read(chs, read, chs.length - read);
			if (r == -1)
				throw new EOFException();
			read += r;
		}
	}

	private void readDigits(final StringBuilder sb) throws IOException {
		while (true) {
			final int ch = peekChar();
			if (ch < '0' || ch > '9')
				return;

			sb.append(readChar());
		}
	}

	private void skipWhitespace() throws IOException {
		while (true) {
			maybeFillBuffer();
			if (buflen == -1)
				throw new EOFException();
			while (bufp < buflen) {
				char ch = buf[bufp];
				if (ch == '\n') {
					++lineNo;
					columnNo = 1;
				} else {
					++columnNo;
				}
				++offset;

				if (ch != ' ' && ch != '\n' && ch != '\r' && ch != '\t')
					return;

				++bufp;
			}
		}
	}

	private void startArray(final State nextState) {
		stack.push(nextState);
		state = State.ARRAY_INIT;
		event = Event.START_ARRAY;
	}

	private void startObject(final State nextState) {
		stack.push(nextState);
		state = State.OBJECT_INIT;
		event = Event.START_OBJECT;
	}

	private void valueKeyword(final State nextState, final char ch0) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(ch0);

		while (sb.length() < 5) {
			final int ch = peekChar();
			if (ch < 'a' || ch > 'z')
				break;
			sb.append(readChar());
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

	private void valueNumber(final State nextState, final char ch0) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(ch0);

		int ch;
		if (ch0 == '-') {
			ch = readChar();
			if (ch < '0' || ch > '9')
				throw new JsonParsingException("Not a number", location);
			sb.append((char) ch);
		} else {
			ch = ch0;
		}

		if (ch != '0') {
			readDigits(sb);
		}

		ch = peekChar();

		if (ch == '.') {
			sb.append('.');
			readChar();
			readDigits(sb);
			ch = peekChar();
		}

		if (ch == 'e' || ch == 'E') {
			sb.append((char) ch);
			readChar();
			ch = readChar();
			if (ch != '+' && ch != '-' && (ch < '0' || ch > '9'))
				throw new JsonParsingException("Not a number", location);
			sb.append((char) ch);
			readDigits(sb);
		}

		try {
			numberValue = new BigDecimal(sb.toString());
		} catch (final NumberFormatException ex) {
			throw new JsonParsingException(ex.getMessage(), ex, location);
		}

		state = nextState;
		event = Event.VALUE_NUMBER;
	}

	private void valueString(final State nextState) throws IOException {
		parseString();
		state = nextState;
		event = Event.VALUE_STRING;
	}

	@Override
	protected boolean isInArray() {
		return switch (state) {
			case ARRAY_INIT, ARRAY_VALUE, ARRAY_COMMA -> true;
			default -> false;
		};
	}

	@Override
	protected boolean isInObject() {
		return switch (state) {
			case OBJECT_INIT, OBJECT_KEY, OBJECT_VALUE, OBJECT_COMMA -> true;
			default -> false;
		};
	}

	private enum State {
		INIT, OBJECT_INIT, OBJECT_KEY, OBJECT_VALUE, OBJECT_COMMA, ARRAY_INIT, ARRAY_VALUE, ARRAY_COMMA, END
	}
}

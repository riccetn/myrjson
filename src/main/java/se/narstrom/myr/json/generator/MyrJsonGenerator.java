package se.narstrom.myr.json.generator;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.json.stream.JsonGenerationException;
import jakarta.json.stream.JsonGenerator;

public final class MyrJsonGenerator implements JsonGenerator {
	private final Writer writer;

	private final Deque<ValueType> stack = new ArrayDeque<>();

	private State state = State.INIT;

	private boolean comma = false;

	public MyrJsonGenerator(final Writer writer) {
		this.writer = writer;
	}

	@Override
	public void close() {
		if(state != State.END)
			throw new JsonGenerationException("Wrong state: " + state);
		try {
			writer.close();
		} catch (final IOException ex) {
			throw new JsonGenerationException(ex.getMessage(), ex);
		}
	}

	@Override
	public void flush() {
		try {
			writer.flush();
		} catch (final IOException ex) {
			throw new JsonGenerationException(ex.getMessage(), ex);
		}
	}

	@Override
	public JsonGenerator write(final BigDecimal value) {
		beforeValue();
		return writeString(value.toString());
	}

	@Override
	public JsonGenerator write(final BigInteger value) {
		beforeValue();
		return writeString(value.toString());
	}

	@Override
	public JsonGenerator write(boolean value) {
		beforeValue();
		return writeString(value ? "true" : "false");
	}

	@Override
	public JsonGenerator write(final double value) {
		if(!Double.isFinite(value))
			throw new NumberFormatException();
		beforeValue();
		return writeString(Double.toString(value));
	}

	@Override
	public JsonGenerator write(final int value) {
		beforeValue();
		return writeString(Integer.toString(value));
	}

	@Override
	public JsonGenerator write(final JsonValue value) {
		return switch (value.getValueType()) {
			case OBJECT -> {
				writeStartObject();
				for (final Map.Entry<String, JsonValue> entry : ((JsonObject) value).entrySet()) {
					write(entry.getKey(), entry.getValue());
				}
				yield writeEnd();
			}
			case ARRAY -> {
				writeStartArray();
				for (final JsonValue val : (JsonArray) value) {
					write(val);
				}
				yield writeEnd();
			}
			case STRING -> write(((JsonString) value).getString());
			case NUMBER -> write(((JsonNumber) value).bigDecimalValue());
			case TRUE -> write(true);
			case FALSE -> write(false);
			case NULL -> writeNull();
		};
	}

	@Override
	public JsonGenerator write(final long value) {
		beforeValue();
		return writeString(Long.toString(value));
	}

	@Override
	public JsonGenerator write(final String value) {
		beforeValue();
		return writeStringValue(value);
	}

	@Override
	public JsonGenerator write(final String name, final BigDecimal value) {
		return writeKey(name).write(value);
	}

	@Override
	public JsonGenerator write(final String name, final BigInteger value) {
		return writeKey(name).write(value);
	}

	@Override
	public JsonGenerator write(final String name, final boolean value) {
		return writeKey(name).write(value);
	}

	@Override
	public JsonGenerator write(final String name, final double value) {
		return writeKey(name).write(value);
	}

	@Override
	public JsonGenerator write(final String name, final int value) {
		return writeKey(name).write(value);
	}

	@Override
	public JsonGenerator write(final String name, final JsonValue value) {
		return writeKey(name).write(value);
	}

	@Override
	public JsonGenerator write(final String name, final long value) {
		return writeKey(name).write(value);
	}

	@Override
	public JsonGenerator write(final String name, final String value) {
		return writeKey(name).write(value);
	}

	@Override
	public JsonGenerator writeEnd() {
		if (state != State.OBJECT_KEY && state != State.ARRAY_VALUE)
			throw new JsonGenerationException("Wrong state: " + state);

		final ValueType type = stack.pop();
		switch (type) {
			case OBJECT -> writeChar('}');
			case ARRAY -> writeChar(']');
			default -> throw new AssertionError();
		}

		if (stack.isEmpty()) {
			state = State.END;
		} else {
			comma = true;
			switch (stack.peek()) {
				case OBJECT -> state = State.OBJECT_KEY;
				case ARRAY -> state = State.ARRAY_VALUE;
				default -> throw new AssertionError();
			}
		}

		return this;
	}

	@Override
	public JsonGenerator writeKey(final String name) {
		if (state != State.OBJECT_KEY)
			throw new JsonGenerationException("Wrong state " + state);

		if (comma)
			writeChar(',');
		comma = false;

		writeStringValue(name).writeChar(':');
		state = State.OBJECT_VALUE;
		return this;
	}

	@Override
	public JsonGenerator writeNull() {
		beforeValue();
		return writeString("null");
	}

	@Override
	public JsonGenerator writeNull(final String name) {
		return writeKey(name).writeNull();
	}

	@Override
	public JsonGenerator writeStartArray() {
		if (state != State.INIT && state != State.OBJECT_VALUE && state != State.ARRAY_VALUE)
			throw new JsonGenerationException("Wrong state: " + state);

		if (comma)
			writeChar(',');
		comma = false;

		stack.push(ValueType.ARRAY);
		state = State.ARRAY_VALUE;
		writeChar('[');
		return this;
	}

	@Override
	public JsonGenerator writeStartArray(final String name) {
		return writeKey(name).writeStartArray();
	}

	@Override
	public JsonGenerator writeStartObject() {
		if (state != State.INIT && state != State.OBJECT_VALUE && state != State.ARRAY_VALUE)
			throw new JsonGenerationException("Wrong state: " + state);

		if (comma)
			writeChar(',');
		comma = false;

		stack.push(ValueType.OBJECT);
		state = State.OBJECT_KEY;
		writeChar('{');
		return this;
	}

	@Override
	public JsonGenerator writeStartObject(final String name) {
		return writeKey(name).writeStartObject();
	}

	private void beforeValue() {
		state = switch (state) {
			case INIT -> State.END;
			case OBJECT_VALUE -> State.OBJECT_KEY;
			case ARRAY_VALUE -> State.ARRAY_VALUE;
			default -> throw new JsonGenerationException("Wrong state: " + state);
		};

		if (comma)
			writeChar(',');
		comma = true;
	}

	private MyrJsonGenerator writeChar(final char ch) {
		try {
			writer.write(ch);
			return this;
		} catch (final IOException ex) {
			throw new JsonGenerationException(ex.getMessage(), ex);
		}
	}

	private MyrJsonGenerator writeString(final String str) {
		try {
			writer.write(str);
			return this;
		} catch (final IOException ex) {
			throw new JsonGenerationException(ex.getMessage(), ex);
		}
	}

	private MyrJsonGenerator writeStringValue(final String str) {
		writeChar('\"');
		for (int i = 0; i < str.length(); ++i) {
			final char ch = str.charAt(i);
			switch (ch) {
				case '\"' -> writeString("\\\"");
				case '\\' -> writeString("\\\\");
				case '\b' -> writeString("\\b");
				case '\f' -> writeString("\\f");
				case '\n' -> writeString("\\n");
				case '\r' -> writeString("\\r");
				case '\t' -> writeString("\\t");
				default -> {
					if (Character.isISOControl(ch)) {
						writeString(String.format("\\u%04x", (int) ch));
					} else {
						writeChar(ch);
					}
				}
			}
		}
		writeChar('\"');
		return this;
	}

	private enum State {
		INIT, OBJECT_KEY, OBJECT_VALUE, ARRAY_VALUE, END
	}
}

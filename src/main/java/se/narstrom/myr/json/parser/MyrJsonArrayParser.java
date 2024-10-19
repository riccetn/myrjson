package se.narstrom.myr.json.parser;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonLocation;

public final class MyrJsonArrayParser extends MyrJsonParserBase {
	private final Iterator<JsonValue> iterator;

	private State state = State.INIT;

	private JsonValue value;

	private MyrJsonParserBase subParser;

	public MyrJsonArrayParser(final JsonProvider provider, final JsonArray array) {
		super(provider);
		this.iterator = array.iterator();
	}

	@Override
	public void close() {
		/* Nothing */
	}

	@Override
	public BigDecimal getBigDecimal() {
		if (state != State.VALUE)
			throw new IllegalStateException();

		if (subParser != null)
			return subParser.getBigDecimal();

		if (value.getValueType() != ValueType.NUMBER)
			throw new IllegalStateException();
		return ((JsonNumber) value).bigDecimalValue();
	}

	@Override
	public JsonLocation getLocation() {
		return new MyrJsonLocation(-1, -1, -1);
	}

	@Override
	public String getString() {
		if (state != State.VALUE)
			throw new IllegalStateException();

		if (subParser != null)
			return subParser.getString();

		if (value.getValueType() != ValueType.STRING)
			throw new IllegalStateException();
		return ((JsonString) value).getString();
	}

	@Override
	public boolean hasNext() {
		return state != State.END;
	}

	@Override
	public Event next() {
		return switch (state) {
			case INIT -> nextInit();
			case START -> nextStart();
			case VALUE -> nextValue();
			case END -> throw new NoSuchElementException();
		};
	}

	private Event nextInit() {
		state = State.START;
		return Event.START_ARRAY;
	}

	private Event nextStart() {
		if (iterator.hasNext()) {
			value = iterator.next();
			state = State.VALUE;

			return valueToEvent();
		} else {
			state = State.END;
			return Event.END_ARRAY;
		}
	}

	private Event nextValue() {
		if (subParser != null && subParser.hasNext())
			return subParser.next();

		subParser = null;
		value = null;

		if (iterator.hasNext()) {
			value = iterator.next();
			return valueToEvent();
		}

		state = State.END;
		return Event.END_ARRAY;
	}

	private Event valueToEvent() {
		return switch (value.getValueType()) {
			case OBJECT -> {
				subParser = new MyrJsonObjectParser(provider, (JsonObject) value);
				yield subParser.next();
			}
			case ARRAY -> {
				subParser = new MyrJsonArrayParser(provider, (JsonArray) value);
				yield subParser.next();
			}
			case STRING -> Event.VALUE_STRING;
			case NUMBER -> Event.VALUE_NUMBER;
			case TRUE -> Event.VALUE_TRUE;
			case FALSE -> Event.VALUE_FALSE;
			case NULL -> Event.VALUE_NULL;
		};
	}

	@Override
	protected boolean isInArray() {
		if (subParser != null)
			return subParser.isInArray();
		return true;
	}

	@Override
	protected boolean isInObject() {
		if (subParser != null)
			return subParser.isInObject();
		return false;
	}

	private enum State {
		INIT, START, VALUE, END
	}
}

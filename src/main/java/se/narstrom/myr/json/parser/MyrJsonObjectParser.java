package se.narstrom.myr.json.parser;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.json.stream.JsonLocation;
import se.narstrom.myr.json.MyrJsonContext;

public final class MyrJsonObjectParser extends MyrJsonParserBase {
	private final Iterator<Map.Entry<String, JsonValue>> iterator;

	private State state = State.INIT;

	private Map.Entry<String, JsonValue> entry;

	private MyrJsonParserBase subParser;

	private Event event;

	public MyrJsonObjectParser(final JsonObject object, final MyrJsonContext context) {
		super(context);
		this.iterator = object.entrySet().iterator();
	}

	@Override
	public void close() {
		/* Nothing */
	}

	@Override
	public Event currentEvent() {
		return event;
	}

	@Override
	public BigDecimal getBigDecimal() {
		if (state != State.VALUE)
			throw new IllegalStateException();

		if (subParser != null)
			return subParser.getBigDecimal();

		final JsonValue value = entry.getValue();
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
		return switch (state) {
			case KEY -> entry.getKey();
			case VALUE -> getStringValue();
			default -> throw new IllegalStateException();
		};
	}

	@Override
	public boolean hasNext() {
		return state != State.END;
	}

	@Override
	public Event next() {
		event = switch (state) {
			case INIT -> nextInit();
			case START -> nextStart();
			case KEY -> nextKey();
			case VALUE -> nextValue();
			case END -> throw new NoSuchElementException();
		};
		return event;
	}

	private String getStringValue() {
		if (subParser != null)
			return subParser.getString();

		final JsonValue value = entry.getValue();
		if (value.getValueType() != ValueType.STRING)
			throw new IllegalStateException();

		return ((JsonString) value).getString();
	}

	private Event nextInit() {
		state = State.START;
		return Event.START_OBJECT;
	}

	private Event nextKey() {
		state = State.VALUE;

		final JsonValue value = entry.getValue();
		return switch (value.getValueType()) {
			case OBJECT -> {
				subParser = new MyrJsonObjectParser((JsonObject) entry.getValue(), context);
				yield subParser.next();
			}
			case ARRAY -> {
				subParser = new MyrJsonArrayParser((JsonArray) entry.getValue(), context);
				yield subParser.next();
			}
			case STRING -> Event.VALUE_STRING;
			case NUMBER -> Event.VALUE_NUMBER;
			case TRUE -> Event.VALUE_TRUE;
			case FALSE -> Event.VALUE_FALSE;
			case NULL -> Event.VALUE_NULL;
		};
	}

	private Event nextStart() {
		if (iterator.hasNext()) {
			entry = iterator.next();
			state = State.KEY;
			return Event.KEY_NAME;
		} else {
			state = State.END;
			return Event.END_OBJECT;
		}
	}

	private Event nextValue() {
		if (subParser != null && subParser.hasNext())
			return subParser.next();

		subParser = null;
		entry = null;

		if (iterator.hasNext()) {
			entry = iterator.next();
			state = State.KEY;
			return Event.KEY_NAME;
		}

		state = State.END;
		return Event.END_OBJECT;
	}

	@Override
	protected boolean isInArray() {
		if (subParser != null)
			return subParser.isInArray();
		return false;
	}

	@Override
	protected boolean isInObject() {
		if (subParser != null)
			return subParser.isInObject();
		return true;
	}

	private enum State {
		INIT, START, KEY, VALUE, END
	}
}

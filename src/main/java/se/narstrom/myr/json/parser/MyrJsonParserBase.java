package se.narstrom.myr.json.parser;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;
import se.narstrom.myr.json.MyrJsonContext;
import se.narstrom.myr.json.value.MyrJsonNumber;
import se.narstrom.myr.json.value.MyrJsonString;

public abstract class MyrJsonParserBase implements JsonParser {
	protected final MyrJsonContext context;

	protected MyrJsonParserBase(final MyrJsonContext context) {
		this.context = context;
	}

	@Override
	public JsonArray getArray() {
		if (currentEvent() != Event.START_ARRAY)
			throw new IllegalStateException();

		final JsonArrayBuilder builder = context.defaultBuilderFactory().createArrayBuilder();

		while (next() != Event.END_ARRAY) {
			builder.add(getValue());
		}
		return builder.build();
	}

	@Override
	public Stream<JsonValue> getArrayStream() {
		if (currentEvent() != Event.START_ARRAY)
			throw new IllegalStateException();

		final Iterator<JsonValue> iter = new ArrayIterator();
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
	}

	@Override
	public int getInt() {
		return getBigDecimal().intValue();
	}

	@Override
	public long getLong() {
		return getBigDecimal().longValue();
	}

	@Override
	public JsonObject getObject() {
		if (currentEvent() != Event.START_OBJECT)
			throw new IllegalStateException();

		final JsonObjectBuilder builder = context.defaultBuilderFactory().createObjectBuilder();

		while (next() != Event.END_OBJECT) {
			assert currentEvent() == Event.KEY_NAME;
			final String name = getString();
			next();

			final JsonValue value = getValue();
			builder.add(name, value);
		}

		return builder.build();
	}

	@Override
	public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
		if (currentEvent() != Event.START_OBJECT)
			throw new IllegalStateException();

		final Iterator<Map.Entry<String, JsonValue>> iter = new ObjectIterator();
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
	}

	@Override
	public JsonValue getValue() {
		return switch (currentEvent()) {
			case START_ARRAY -> getArray();
			case START_OBJECT -> getObject();
			case VALUE_STRING, KEY_NAME -> new MyrJsonString(getString());
			case VALUE_NUMBER -> new MyrJsonNumber(getBigDecimal());
			case VALUE_TRUE -> JsonValue.TRUE;
			case VALUE_FALSE -> JsonValue.FALSE;
			case VALUE_NULL -> JsonValue.NULL;
			default -> throw new IllegalStateException();
		};
	}

	@Override
	public Stream<JsonValue> getValueStream() {
		if (currentEvent() != null)
			throw new IllegalStateException();

		final Spliterator<JsonValue> split = new ValueSpliterator();
		return StreamSupport.stream(split, false);
	}

	@Override
	public boolean isIntegralNumber() {
		return getBigDecimal().scale() == 0;
	}

	@Override
	public void skipArray() {
		if (!isInArray())
			return;

		int nested = 1;

		while (nested > 0) {
			final Event ev = next();
			switch (ev) {
				case START_ARRAY -> ++nested;
				case END_ARRAY -> --nested;
				default -> {
					/* Nothing */
				}
			}
		}
	}

	@Override
	public void skipObject() {
		if (!isInObject())
			return;

		int nested = 1;

		while (nested > 0) {
			final Event ev = next();
			switch (ev) {
				case START_OBJECT -> ++nested;
				case END_OBJECT -> --nested;
				default -> {
					/* Nothing */
				}
			}
		}
	}

	protected abstract boolean isInArray();

	protected abstract boolean isInObject();

	private class ArrayIterator implements Iterator<JsonValue> {
		public ArrayIterator() {
			MyrJsonParserBase.this.next();
		}

		@Override
		public boolean hasNext() {
			return currentEvent() != Event.END_ARRAY;
		}

		@Override
		public JsonValue next() {
			if (!hasNext())
				throw new NoSuchElementException();

			final JsonValue value = getValue();
			MyrJsonParserBase.this.next();

			return value;
		}
	}

	private class ObjectIterator implements Iterator<Map.Entry<String, JsonValue>> {
		public ObjectIterator() {
			MyrJsonParserBase.this.next();
		}

		@Override
		public boolean hasNext() {
			return currentEvent() != Event.END_OBJECT;
		}

		@Override
		public Map.Entry<String, JsonValue> next() {
			if (!hasNext())
				throw new NoSuchElementException();

			assert currentEvent() == Event.KEY_NAME;
			final String name = getString();
			MyrJsonParserBase.this.next();

			final JsonValue value = getValue();
			MyrJsonParserBase.this.next();

			return Map.entry(name, value);
		}
	}

	private class ValueSpliterator implements Spliterator<JsonValue> {
		boolean advanced = false;

		@Override
		public int characteristics() {
			return ORDERED | SIZED;
		}

		@Override
		public long estimateSize() {
			return 1;
		}

		@Override
		public boolean tryAdvance(final Consumer<? super JsonValue> action) {
			if (advanced)
				return false;

			MyrJsonParserBase.this.next();
			action.accept(getValue());
			advanced = true;
			return true;
		}

		@Override
		public Spliterator<JsonValue> trySplit() {
			return null;
		}
	}
}

package se.narstrom.myr.json.io;

import java.util.logging.Logger;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;
import se.narstrom.myr.json.MyrJsonContext;

public final class MyrJsonReader implements JsonReader {
	private static final Logger LOG = Logger.getLogger(MyrJsonReader.class.getName());

	private final MyrJsonContext context;

	private final JsonParser parser;

	private boolean closed = false;

	public MyrJsonReader(final JsonParser parser, final MyrJsonContext context) {
		this.parser = parser;
		this.context = context;
	}

	@Override
	public void close() {
		if (closed)
			return;
		closed = true;
		parser.close();
	}

	@Override
	public JsonStructure read() {
		if (closed || !parser.hasNext())
			throw new IllegalStateException();

		final JsonParser.Event event = parser.next();
		return switch (event) {
			case START_ARRAY -> onStartArray();
			case START_OBJECT -> onStartObject();
			default -> throw new JsonException("Not a structure");
		};
	}

	@Override
	public JsonArray readArray() {
		if (closed || !parser.hasNext())
			throw new IllegalStateException();

		final JsonParser.Event event = parser.next();
		if (event != JsonParser.Event.START_ARRAY)
			throw new JsonException("Not an array");

		return onStartArray();
	}

	@Override
	public JsonObject readObject() {
		if (closed || !parser.hasNext())
			throw new IllegalStateException();

		final JsonParser.Event event = parser.next();
		if (event != JsonParser.Event.START_OBJECT)
			throw new JsonException("Not an object");

		return onStartObject();
	}

	@Override
	public JsonValue readValue() {
		if (closed || !parser.hasNext())
			throw new IllegalStateException();
		return readValue(parser.next());
	}

	private JsonArray onStartArray() {
		final JsonArrayBuilder builder = context.defaultBuilderFactory().createArrayBuilder();

		for (JsonParser.Event event = parser.next(); event != JsonParser.Event.END_ARRAY; event = parser.next()) {
			builder.add(readValue(event));
		}

		return builder.build();
	}

	private JsonObject onStartObject() {
		final JsonObjectBuilder builder = context.defaultBuilderFactory().createObjectBuilder();

		for (JsonParser.Event event = parser.next(); event != JsonParser.Event.END_OBJECT; event = parser.next()) {
			if (event != JsonParser.Event.KEY_NAME)
				throw new JsonParsingException("No key", parser.getLocation());

			final String key = parser.getString();
			LOG.info(() -> String.format("Key %s", key));

			final JsonValue value = readValue();
			LOG.info(() -> String.format("Value %s", value));

			builder.add(key, value);
		}

		return builder.build();
	}

	private JsonValue readValue(final JsonParser.Event event) {
		return switch (event) {
			case START_OBJECT -> onStartObject();
			case START_ARRAY -> onStartArray();
			case VALUE_STRING -> context.createValue(parser.getString());
			case VALUE_NUMBER -> context.createValue(parser.getBigDecimal());
			case VALUE_TRUE -> JsonValue.TRUE;
			case VALUE_FALSE -> JsonValue.FALSE;
			case VALUE_NULL -> JsonValue.NULL;
			default -> throw new JsonParsingException("Not a value, event: " + event, parser.getLocation());
		};
	}
}

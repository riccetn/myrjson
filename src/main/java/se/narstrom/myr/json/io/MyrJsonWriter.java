package se.narstrom.myr.json.io;

import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.stream.JsonGenerator;

public final class MyrJsonWriter implements JsonWriter {
	private final JsonGenerator generator;

	private boolean written;

	public MyrJsonWriter(final JsonGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void writeArray(final JsonArray array) {
		if (written)
			throw new IllegalStateException();
		written = true;
		writeArrayInternal(array);
	}

	@Override
	public void writeObject(final JsonObject object) {
		if (written)
			throw new IllegalStateException();
		written = true;
		writeObjectInternal(object);
	}

	@Override
	public void write(final JsonStructure value) {
		write((JsonValue) value);
	}

	@Override
	public void write(final JsonValue value) {
		if (written)
			throw new IllegalStateException();
		written = true;
		writeInternal(value);
	}

	@Override
	public void close() {
		generator.close();
	}

	private void writeArrayInternal(final JsonArray array) {
		generator.writeStartArray();
		for (final JsonValue value : array) {
			writeInternal(value);
		}
		generator.writeEnd();
	}

	private void writeObjectInternal(final JsonObject object) {
		generator.writeStartObject();
		for (final Map.Entry<String, JsonValue> entry : object.entrySet()) {
			generator.writeKey(entry.getKey());
			writeInternal(entry.getValue());
		}
		generator.writeEnd();
	}

	private void writeInternal(final JsonValue value) {
		switch (value.getValueType()) {
			case ARRAY -> writeArrayInternal((JsonArray) value);
			case OBJECT -> writeObjectInternal((JsonObject) value);
			case STRING -> generator.write(((JsonString) value).getString());
			case NUMBER -> generator.write(((JsonNumber) value).bigDecimalValue());
			case TRUE -> generator.write(true);
			case FALSE -> generator.write(false);
			case NULL -> generator.writeNull();
		}
	}
}

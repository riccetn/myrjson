package se.narstrom.myr.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import se.narstrom.myr.json.factory.MyrJsonReaderFactory;
import se.narstrom.myr.json.factory.MyrJsonWriterFactory;
import se.narstrom.myr.json.patch.MyrJsonPatch;
import se.narstrom.myr.json.patch.MyrJsonPatchBuilder;
import se.narstrom.myr.json.patch.MyrJsonMergePatch;
import se.narstrom.myr.json.patch.MyrJsonPointer;

public final class MyrJsonProvider extends JsonProvider {

	private final MyrJsonContext defaultContext = new MyrJsonContext(Map.of());

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		return defaultContext.defaultBuilderFactory().createArrayBuilder();
	}

	@Override
	public JsonArrayBuilder createArrayBuilder(final Collection<?> collection) {
		return defaultContext.defaultBuilderFactory().createArrayBuilder(collection);
	}

	@Override
	public JsonArrayBuilder createArrayBuilder(final JsonArray array) {
		return defaultContext.defaultBuilderFactory().createArrayBuilder(array);
	}

	@Override
	public JsonBuilderFactory createBuilderFactory(final Map<String, ?> config) {
		if (config == null)
			return defaultContext.createBuilderFactory();
		else
			return new MyrJsonContext(config).createBuilderFactory();
	}

	@Override
	public JsonPatch createDiff(final JsonStructure source, final JsonStructure target) {
		return new MyrJsonPatch(source, target, this, createBuilderFactory(Map.of()));
	}

	@Override
	public JsonGenerator createGenerator(final OutputStream out) {
		return createGeneratorFactory(Map.of()).createGenerator(out);
	}

	@Override
	public JsonGenerator createGenerator(final Writer writer) {
		return createGeneratorFactory(Map.of()).createGenerator(writer);
	}

	@Override
	public JsonGeneratorFactory createGeneratorFactory(final Map<String, ?> config) {
		if (config == null)
			return defaultContext.createGeneratorFactory();
		else
			return new MyrJsonContext(config).createGeneratorFactory();
	}

	@Override
	public JsonMergePatch createMergeDiff(final JsonValue source, final JsonValue target) {
		return new MyrJsonMergePatch(source, target, createBuilderFactory(Map.of()));
	}

	@Override
	public JsonMergePatch createMergePatch(final JsonValue patch) {
		return new MyrJsonMergePatch(patch, createBuilderFactory(Map.of()));
	}

	@Override
	public JsonObjectBuilder createObjectBuilder() {
		return defaultContext.defaultBuilderFactory().createObjectBuilder();
	}

	@Override
	public JsonObjectBuilder createObjectBuilder(final JsonObject object) {
		return defaultContext.defaultBuilderFactory().createObjectBuilder(object);
	}

	@Override
	public JsonObjectBuilder createObjectBuilder(final Map<String, ?> map) {
		@SuppressWarnings("unchecked")
		final Map<String, Object> fixedMap = (Map<String, Object>) map;
		return defaultContext.defaultBuilderFactory().createObjectBuilder(fixedMap);
	}

	@Override
	public JsonParser createParser(final InputStream in) {
		return defaultContext.defaultParserFactory().createParser(in);
	}

	@Override
	public JsonParser createParser(final Reader reader) {
		return defaultContext.defaultParserFactory().createParser(reader);
	}

	@Override
	public JsonParserFactory createParserFactory(final Map<String, ?> config) {
		if (config == null)
			return defaultContext.createParserFactory();
		else
			return new MyrJsonContext(config).createParserFactory();
	}

	@Override
	public JsonPatch createPatch(final JsonArray array) {
		return new MyrJsonPatch(array, this, createBuilderFactory(Map.of()));
	}

	@Override
	public JsonPatchBuilder createPatchBuilder() {
		return new MyrJsonPatchBuilder(this, createBuilderFactory(Map.of()));
	}

	@Override
	public JsonPointer createPointer(final String pointer) {
		return new MyrJsonPointer(pointer);
	}

	@Override
	public JsonReader createReader(final InputStream in) {
		return createReaderFactory(Map.of()).createReader(in);
	}

	@Override
	public JsonReader createReader(final Reader reader) {
		return createReaderFactory(Map.of()).createReader(reader);
	}

	@Override
	public JsonReaderFactory createReaderFactory(final Map<String, ?> config) {
		return new MyrJsonReaderFactory(this, createBuilderFactory(config), createParserFactory(config));
	}

	@Override
	public JsonNumber createValue(final BigDecimal value) {
		return defaultContext.createValue(value);
	}

	@Override
	public JsonNumber createValue(final BigInteger value) {
		return defaultContext.createValue(value);
	}

	@Override
	public JsonNumber createValue(final double value) {
		return defaultContext.createValue(value);
	}

	@Override
	public JsonNumber createValue(int value) {
		return defaultContext.createValue(value);
	}

	@Override
	public JsonNumber createValue(long value) {
		return defaultContext.createValue(value);
	}

	@Override
	public JsonNumber createValue(final Number value) {
		return defaultContext.createValue(value);
	}

	@Override
	public JsonString createValue(final String value) {
		return defaultContext.createValue(value);
	}

	@Override
	public JsonWriter createWriter(final OutputStream out) {
		return createWriterFactory(Map.of()).createWriter(out);
	}

	@Override
	public JsonWriter createWriter(final Writer writer) {
		return createWriterFactory(Map.of()).createWriter(writer);
	}

	@Override
	public JsonWriterFactory createWriterFactory(final Map<String, ?> config) {
		return new MyrJsonWriterFactory(createGeneratorFactory(config));
	}
}

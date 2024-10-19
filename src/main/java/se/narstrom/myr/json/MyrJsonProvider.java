package se.narstrom.myr.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import se.narstrom.myr.json.factory.MyrJsonBuilderFactory;
import se.narstrom.myr.json.factory.MyrJsonGeneratorFactory;
import se.narstrom.myr.json.factory.MyrJsonParserFactory;
import se.narstrom.myr.json.factory.MyrJsonReaderFactory;
import se.narstrom.myr.json.factory.MyrJsonWriterFactory;
import se.narstrom.myr.json.patch.MyrJsonMergePatch;
import se.narstrom.myr.json.patch.MyrJsonPointer;
import se.narstrom.myr.json.value.MyrJsonNumber;
import se.narstrom.myr.json.value.MyrJsonString;

public final class MyrJsonProvider extends JsonProvider {

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		return createBuilderFactory(Map.of()).createArrayBuilder();
	}

	@Override
	public JsonArrayBuilder createArrayBuilder(final Collection<?> collection) {
		return createBuilderFactory(Map.of()).createArrayBuilder(collection);
	}

	@Override
	public JsonArrayBuilder createArrayBuilder(final JsonArray array) {
		return createBuilderFactory(Map.of()).createArrayBuilder(array);
	}

	@Override
	public JsonBuilderFactory createBuilderFactory(final Map<String, ?> config) {
		return new MyrJsonBuilderFactory(this, (config != null) ? config : Map.of());
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
		return new MyrJsonGeneratorFactory(config);
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
		return createBuilderFactory(Map.of()).createObjectBuilder();
	}

	@Override
	public JsonObjectBuilder createObjectBuilder(final JsonObject object) {
		return createBuilderFactory(Map.of()).createObjectBuilder(object);
	}

	@Override
	public JsonObjectBuilder createObjectBuilder(final Map<String, ?> map) {
		return createBuilderFactory(Map.of()).createObjectBuilder((Map<String, Object>) map);
	}

	@Override
	public JsonParser createParser(final InputStream in) {
		return createParserFactory(Map.of()).createParser(in);
	}

	@Override
	public JsonParser createParser(final Reader reader) {
		return createParserFactory(Map.of()).createParser(reader);
	}

	@Override
	public JsonParserFactory createParserFactory(final Map<String, ?> config) {
		return new MyrJsonParserFactory(this);
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
		return new MyrJsonNumber(Objects.requireNonNull(value));
	}

	@Override
	public JsonNumber createValue(final BigInteger value) {
		return createValue(new BigDecimal(value));
	}

	@Override
	public JsonNumber createValue(final double value) {
		return createValue(BigDecimal.valueOf(value));
	}

	@Override
	public JsonNumber createValue(int value) {
		return createValue(BigDecimal.valueOf(value));
	}

	@Override
	public JsonNumber createValue(long value) {
		return createValue(BigDecimal.valueOf(value));
	}

	@Override
	public JsonNumber createValue(final Number number) {
		return switch (number) {
			case Byte num -> createValue(num.intValue());
			case Short num -> createValue(num.intValue());
			case Integer num -> createValue(num.intValue());
			case Long num -> createValue(num.longValue());
			case Float num -> createValue(num.doubleValue());
			case Double num -> createValue(num.doubleValue());
			case BigInteger num -> createValue(num);
			case BigDecimal num -> createValue(num);
			default -> createValue(number.longValue());
		};
	}

	@Override
	public JsonString createValue(final String value) {
		return new MyrJsonString(Objects.requireNonNull(value));
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

package se.narstrom.myr.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Map;

import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonString;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import se.narstrom.myr.json.factory.MyrJsonBuilderFactory;
import se.narstrom.myr.json.factory.MyrJsonParserFactory;
import se.narstrom.myr.json.factory.MyrJsonReaderFactory;
import se.narstrom.myr.json.value.MyrJsonNumber;
import se.narstrom.myr.json.value.MyrJsonString;

public final class MyrJsonProvider extends JsonProvider {

	@Override
	public JsonParser createParser(final Reader reader) {
		return createParserFactory(Map.of()).createParser(reader);
	}

	@Override
	public JsonParser createParser(final InputStream in) {
		return createParserFactory(Map.of()).createParser(in);
	}

	@Override
	public JsonParserFactory createParserFactory(final Map<String, ?> config) {
		return new MyrJsonParserFactory(config);
	}

	@Override
	public JsonGenerator createGenerator(final Writer writer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonGenerator createGenerator(final OutputStream out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonGeneratorFactory createGeneratorFactory(final Map<String, ?> config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonReader createReader(final Reader reader) {
		return createReaderFactory(Map.of()).createReader(reader);
	}

	@Override
	public JsonReader createReader(final InputStream in) {
		return createReaderFactory(Map.of()).createReader(in);
	}

	@Override
	public JsonWriter createWriter(final Writer writer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonWriter createWriter(final OutputStream out) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonWriterFactory createWriterFactory(final Map<String, ?> config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonReaderFactory createReaderFactory(final Map<String, ?> config) {
		return new MyrJsonReaderFactory(this, createParserFactory(config));
	}

	@Override
	public JsonObjectBuilder createObjectBuilder() {
		return createBuilderFactory(Map.of()).createObjectBuilder();
	}

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		return createBuilderFactory(Map.of()).createArrayBuilder();
	}

	@Override
	public JsonBuilderFactory createBuilderFactory(final Map<String, ?> config) {
		return new MyrJsonBuilderFactory(this, config);
	}

	@Override
	public JsonString createValue(final String value) {
		return new MyrJsonString(value);
	}

	@Override
	public JsonNumber createValue(final BigDecimal value) {
		return new MyrJsonNumber(value);
	}
}

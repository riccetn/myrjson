package se.narstrom.myr.json.benchmark;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;

/**
 * Parsing benchmarks: the streaming {@link JsonParser} API and the object model
 * {@link JsonReader} API, from both character and byte input, plus the
 * {@link JsonParser} that walks an existing object model.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class ParseBenchmark {

	@Param
	public Implementation implementation;

	@Param
	public TestData data;

	private String text;
	private byte[] bytes;
	private JsonParserFactory parserFactory;
	private JsonReaderFactory readerFactory;
	private JsonObject object;
	private JsonArray array;

	@Setup
	public void setup() {
		text = data.generate();
		bytes = text.getBytes(StandardCharsets.UTF_8);
		final var provider = implementation.newProvider();
		parserFactory = provider.createParserFactory(Map.of());
		readerFactory = provider.createReaderFactory(Map.of());
		try (JsonReader reader = readerFactory.createReader(new StringReader(text))) {
			object = reader.readObject();
		}
		array = object.getJsonArray("records");
	}

	/** Stream all events from a {@link java.io.Reader}, consuming every value. */
	@Benchmark
	public void streamParseReader(final Blackhole bh) {
		try (JsonParser parser = parserFactory.createParser(new StringReader(text))) {
			consume(parser, bh);
		}
	}

	/** Stream all events from a UTF-8 {@link java.io.InputStream}, consuming every value. */
	@Benchmark
	public void streamParseInputStream(final Blackhole bh) {
		try (JsonParser parser = parserFactory.createParser(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
			consume(parser, bh);
		}
	}

	/** Stream all events from a byte stream with charset auto-detection. */
	@Benchmark
	public void streamParseInputStreamDetect(final Blackhole bh) {
		try (JsonParser parser = parserFactory.createParser(new ByteArrayInputStream(bytes))) {
			consume(parser, bh);
		}
	}

	/**
	 * Stream all events from an existing {@link JsonObject} with
	 * {@link JsonParserFactory#createParser(JsonObject)}, consuming every value.
	 * The object model is built by the implementation under test during setup.
	 */
	@Benchmark
	public void streamParseJsonObject(final Blackhole bh) {
		try (JsonParser parser = parserFactory.createParser(object)) {
			consume(parser, bh);
		}
	}

	/**
	 * Stream all events from an existing {@link JsonArray} (the document's
	 * {@code records} array) with {@link JsonParserFactory#createParser(JsonArray)},
	 * consuming every value.
	 */
	@Benchmark
	public void streamParseJsonArray(final Blackhole bh) {
		try (JsonParser parser = parserFactory.createParser(array)) {
			consume(parser, bh);
		}
	}

	/** Read the document into the object model from a {@link java.io.Reader}. */
	@Benchmark
	public JsonValue readTreeReader() {
		try (JsonReader reader = readerFactory.createReader(new StringReader(text))) {
			return reader.readValue();
		}
	}

	/** Read the document into the object model from a UTF-8 {@link java.io.InputStream}. */
	@Benchmark
	public JsonValue readTreeInputStream() {
		try (JsonReader reader = readerFactory.createReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
			return reader.readValue();
		}
	}

	private static void consume(final JsonParser parser, final Blackhole bh) {
		while (parser.hasNext()) {
			final JsonParser.Event event = parser.next();
			switch (event) {
				case KEY_NAME, VALUE_STRING -> bh.consume(parser.getString());
				case VALUE_NUMBER -> {
					if (parser.isIntegralNumber())
						bh.consume(parser.getLong());
					else
						bh.consume(parser.getBigDecimal());
				}
				default -> bh.consume(event);
			}
		}
	}
}

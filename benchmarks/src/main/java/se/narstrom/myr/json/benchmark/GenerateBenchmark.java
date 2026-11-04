package se.narstrom.myr.json.benchmark;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
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

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;

/**
 * Generating benchmarks: the streaming {@link JsonGenerator} API, the object
 * model {@link JsonWriter} API, and building object models with
 * {@link JsonObjectBuilder}/{@link JsonArrayBuilder}.
 *
 * <p>
 * The input is the same document used by {@link ParseBenchmark}, read once
 * during setup into the object model of the implementation under test.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class GenerateBenchmark {

	@Param
	public Implementation implementation;

	@Param
	public TestData data;

	private int expectedSize;
	private JsonObject tree;
	private JsonGeneratorFactory generatorFactory;
	private JsonGeneratorFactory prettyGeneratorFactory;
	private JsonWriterFactory writerFactory;
	private JsonBuilderFactory builderFactory;

	@Setup
	public void setup() {
		final String text = data.generate();
		expectedSize = text.length() + 64;
		final JsonProvider provider = implementation.newProvider();
		try (JsonReader reader = provider.createReader(new StringReader(text))) {
			tree = reader.readObject();
		}
		generatorFactory = provider.createGeneratorFactory(Map.of());
		prettyGeneratorFactory = provider.createGeneratorFactory(Map.of(JsonGenerator.PRETTY_PRINTING, true));
		writerFactory = provider.createWriterFactory(Map.of());
		builderFactory = provider.createBuilderFactory(Map.of());
	}

	/** Emit the document event by event to a {@link java.io.Writer}. */
	@Benchmark
	public String streamGenerateWriter() {
		final StringWriter out = new StringWriter(expectedSize);
		try (JsonGenerator gen = generatorFactory.createGenerator(out)) {
			writeObject(gen, tree);
		}
		return out.toString();
	}

	/** Emit the document event by event to a UTF-8 {@link java.io.OutputStream}. */
	@Benchmark
	public byte[] streamGenerateOutputStream() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream(expectedSize);
		try (JsonGenerator gen = generatorFactory.createGenerator(out, StandardCharsets.UTF_8)) {
			writeObject(gen, tree);
		}
		return out.toByteArray();
	}

	/** Emit the document event by event with pretty printing enabled. */
	@Benchmark
	public String streamGeneratePretty() {
		final StringWriter out = new StringWriter(expectedSize * 2);
		try (JsonGenerator gen = prettyGeneratorFactory.createGenerator(out)) {
			writeObject(gen, tree);
		}
		return out.toString();
	}

	/** Write an existing object model to a {@link java.io.Writer}. */
	@Benchmark
	public String writeTreeWriter() {
		final StringWriter out = new StringWriter(expectedSize);
		try (JsonWriter writer = writerFactory.createWriter(out)) {
			writer.write(tree);
		}
		return out.toString();
	}

	/** Write an existing object model to a UTF-8 {@link java.io.OutputStream}. */
	@Benchmark
	public byte[] writeTreeOutputStream() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream(expectedSize);
		try (JsonWriter writer = writerFactory.createWriter(out, StandardCharsets.UTF_8)) {
			writer.write(tree);
		}
		return out.toByteArray();
	}

	/** Build a copy of the document with object and array builders. */
	@Benchmark
	public JsonObject buildTree() {
		return buildObject(tree).build();
	}

	/** Build a copy of the document with builders and write it out. */
	@Benchmark
	public String buildAndWriteTree() {
		final JsonObject built = buildObject(tree).build();
		final StringWriter out = new StringWriter(expectedSize);
		try (JsonWriter writer = writerFactory.createWriter(out)) {
			writer.write(built);
		}
		return out.toString();
	}

	private static void writeObject(final JsonGenerator gen, final JsonObject object) {
		gen.writeStartObject();
		writeMembers(gen, object);
		gen.writeEnd();
	}

	private static void writeMembers(final JsonGenerator gen, final JsonObject object) {
		for (final Map.Entry<String, JsonValue> entry : object.entrySet()) {
			final String name = entry.getKey();
			final JsonValue value = entry.getValue();
			switch (value.getValueType()) {
				case OBJECT -> {
					gen.writeStartObject(name);
					writeMembers(gen, value.asJsonObject());
					gen.writeEnd();
				}
				case ARRAY -> {
					gen.writeStartArray(name);
					writeElements(gen, value.asJsonArray());
					gen.writeEnd();
				}
				case STRING -> gen.write(name, ((JsonString) value).getString());
				case NUMBER -> {
					final JsonNumber number = (JsonNumber) value;
					if (number.isIntegral())
						gen.write(name, number.longValue());
					else
						gen.write(name, number.bigDecimalValue());
				}
				case TRUE -> gen.write(name, true);
				case FALSE -> gen.write(name, false);
				case NULL -> gen.writeNull(name);
			}
		}
	}

	private static void writeElements(final JsonGenerator gen, final JsonArray array) {
		for (final JsonValue value : array) {
			switch (value.getValueType()) {
				case OBJECT -> writeObject(gen, value.asJsonObject());
				case ARRAY -> {
					gen.writeStartArray();
					writeElements(gen, value.asJsonArray());
					gen.writeEnd();
				}
				case STRING -> gen.write(((JsonString) value).getString());
				case NUMBER -> {
					final JsonNumber number = (JsonNumber) value;
					if (number.isIntegral())
						gen.write(number.longValue());
					else
						gen.write(number.bigDecimalValue());
				}
				case TRUE -> gen.write(true);
				case FALSE -> gen.write(false);
				case NULL -> gen.writeNull();
			}
		}
	}

	private JsonObjectBuilder buildObject(final JsonObject object) {
		final JsonObjectBuilder builder = builderFactory.createObjectBuilder();
		for (final Map.Entry<String, JsonValue> entry : object.entrySet()) {
			final String name = entry.getKey();
			final JsonValue value = entry.getValue();
			switch (value.getValueType()) {
				case OBJECT -> builder.add(name, buildObject(value.asJsonObject()));
				case ARRAY -> builder.add(name, buildArray(value.asJsonArray()));
				case STRING -> builder.add(name, ((JsonString) value).getString());
				case NUMBER -> {
					final JsonNumber number = (JsonNumber) value;
					if (number.isIntegral())
						builder.add(name, number.longValue());
					else
						builder.add(name, number.bigDecimalValue());
				}
				case TRUE -> builder.add(name, true);
				case FALSE -> builder.add(name, false);
				case NULL -> builder.addNull(name);
			}
		}
		return builder;
	}

	private JsonArrayBuilder buildArray(final JsonArray array) {
		final JsonArrayBuilder builder = builderFactory.createArrayBuilder();
		for (final JsonValue value : array) {
			switch (value.getValueType()) {
				case OBJECT -> builder.add(buildObject(value.asJsonObject()));
				case ARRAY -> builder.add(buildArray(value.asJsonArray()));
				case STRING -> builder.add(((JsonString) value).getString());
				case NUMBER -> {
					final JsonNumber number = (JsonNumber) value;
					if (number.isIntegral())
						builder.add(number.longValue());
					else
						builder.add(number.bigDecimalValue());
				}
				case TRUE -> builder.add(true);
				case FALSE -> builder.add(false);
				case NULL -> builder.addNull();
			}
		}
		return builder;
	}
}

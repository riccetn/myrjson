package se.narstrom.myr.json.benchmark;

import java.io.StringWriter;
import java.util.Map;
import java.util.Random;

import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;

/**
 * Deterministically generated JSON documents of various sizes. The documents
 * are produced with a fixed seed so every run and every implementation sees
 * exactly the same input.
 */
public enum TestData {
	/** About 2 KB, 3 records. */
	SMALL(3),
	/** About 110 KB, 200 records. */
	MEDIUM(200),
	/** About 1.8 MB, 3200 records. */
	LARGE(3200);

	private static final String[] WORDS = { "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing",
			"elit", "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore", "magna", "aliqua",
			"åäö", "日本", "quote\"d", "back\\slash", "tab\tbed", "new\nline", "😀" };

	private final int records;

	TestData(final int records) {
		this.records = records;
	}

	/**
	 * Generate the document. Uses Parsson as a neutral reference generator; the
	 * produced text is plain JSON and independent of the implementation.
	 */
	public String generate() {
		final Random random = new Random(0x6d79726a736f6eL + records);
		final JsonGeneratorFactory factory = Implementation.PARSSON.newProvider().createGeneratorFactory(Map.of());
		final StringWriter out = new StringWriter();
		try (JsonGenerator gen = factory.createGenerator(out)) {
			gen.writeStartObject();
			gen.write("version", 1);
			gen.write("generator", "myrjson-benchmarks");
			gen.writeStartArray("records");
			for (int i = 0; i < records; ++i)
				writeRecord(gen, random, i);
			gen.writeEnd();
			gen.writeEnd();
		}
		return out.toString();
	}

	private static void writeRecord(final JsonGenerator gen, final Random random, final int id) {
		gen.writeStartObject();
		gen.write("id", id);
		gen.write("guid", Long.toHexString(random.nextLong()) + "-" + Long.toHexString(random.nextLong()));
		gen.write("active", random.nextBoolean());
		gen.write("balance", Math.round(random.nextDouble() * 1_000_000.0) / 100.0);
		gen.write("bigCounter", random.nextLong());
		gen.write("ratio", random.nextDouble());
		gen.write("name", sentence(random, 2));
		gen.write("about", sentence(random, 12));
		gen.writeNull("deletedAt");

		gen.writeStartArray("tags");
		for (int i = random.nextInt(6); i >= 0; --i)
			gen.write(WORDS[random.nextInt(WORDS.length)]);
		gen.writeEnd();

		gen.writeStartObject("location");
		gen.write("latitude", random.nextDouble() * 180.0 - 90.0);
		gen.write("longitude", random.nextDouble() * 360.0 - 180.0);
		gen.write("city", sentence(random, 1));
		gen.writeEnd();

		gen.writeStartArray("friends");
		for (int i = random.nextInt(4); i >= 0; --i) {
			gen.writeStartObject();
			gen.write("id", random.nextInt(10_000));
			gen.write("name", sentence(random, 2));
			gen.writeEnd();
		}
		gen.writeEnd();

		gen.writeStartArray("matrix");
		for (int i = 0; i < 3; ++i) {
			gen.writeStartArray();
			for (int j = 0; j < 3; ++j)
				gen.write(random.nextInt(1000) - 500);
			gen.writeEnd();
		}
		gen.writeEnd();

		gen.writeEnd();
	}

	private static String sentence(final Random random, final int words) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words; ++i) {
			if (i > 0)
				sb.append(' ');
			sb.append(WORDS[random.nextInt(WORDS.length)]);
		}
		return sb.toString();
	}
}

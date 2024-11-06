package se.narstrom.myr.json.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;

import jakarta.json.JsonException;
import jakarta.json.stream.JsonParsingException;
import se.narstrom.myr.json.MyrJsonContext;

final class MyrJsonStreamParserTest {

	@Test
	void invalidNumber() {
		final MyrJsonContext context = new MyrJsonContext(Map.of());
		final Reader reader = new StringReader("1e-lol");
		final MyrJsonStreamParser parser = new MyrJsonStreamParser(reader, context);
		assertThrows(JsonParsingException.class, () -> parser.next());
	}

	@Test
	void invalidStringUnicodeEscapeSequence() {
		final MyrJsonContext context = new MyrJsonContext(Map.of());
		final Reader reader = new StringReader("\"\\uloli");
		final MyrJsonStreamParser parser = new MyrJsonStreamParser(reader, context);
		assertThrows(JsonParsingException.class, () -> parser.next());
	}

	@FuzzTest
	void fuzzTest(final FuzzedDataProvider provider) {
		final MyrJsonContext context = new MyrJsonContext(Map.of());
		final Reader reader = new StringReader(provider.consumeRemainingAsString());
		try (final MyrJsonStreamParser parser = new MyrJsonStreamParser(reader, context)) {
			while (parser.hasNext())
				parser.next();
		} catch (final JsonException ex) {
		}
	}
}

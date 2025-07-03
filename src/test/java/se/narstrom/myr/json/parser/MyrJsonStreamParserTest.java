package se.narstrom.myr.json.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;

final class MyrJsonStreamParserTest {

	@Test
	void invalidNumber() {
		final JsonParser parser = Json.createParser(new StringReader("1e-lol"));
		assertThrows(JsonParsingException.class, () -> parser.next());
	}

	@Test
	void invalidStringUnicodeEscapeSequence() {
		final JsonParser parser = Json.createParser(new StringReader("\"\\uloli"));
		assertThrows(JsonParsingException.class, () -> parser.next());
	}
}

package se.narstrom.myr.json.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import jakarta.json.Json;
import jakarta.json.JsonArray;
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

	@Test
	void inputLargerThanBuffer() {
		final StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < 10_000; ++i) {
			if (i > 0)
				sb.append(',');
			sb.append("\"value").append(i).append('"');
		}
		sb.append(']');

		final JsonArray array = Json.createReader(new StringReader(sb.toString())).readArray();
		assertEquals(10_000, array.size());
		assertEquals("value9999", array.getString(9_999));
	}
}

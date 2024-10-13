package se.narstrom.myr.json.factory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import jakarta.json.stream.JsonParsingException;
import se.narstrom.myr.json.stream.MyrReader;
import se.narstrom.myr.json.stream.MyrJsonArrayParser;
import se.narstrom.myr.json.stream.MyrJsonLocation;
import se.narstrom.myr.json.stream.MyrJsonObjectParser;
import se.narstrom.myr.json.stream.MyrJsonStreamParser;

public final class MyrJsonParserFactory implements JsonParserFactory {
	private final Map<String, Object> config;

	public MyrJsonParserFactory(final Map<String, ?> config) {
		this.config = Map.copyOf(config);
	}

	@Override
	public JsonParser createParser(final Reader reader) {
		try {
			return new MyrJsonStreamParser(new MyrReader(reader));
		} catch (final IOException ex) {
			throw new JsonParsingException(ex.getMessage(), ex, new MyrJsonLocation(-1, -1, -1));
		}
	}

	@Override
	public JsonParser createParser(final InputStream in) {
		return createParser(new InputStreamReader(in, StandardCharsets.UTF_8));
	}

	@Override
	public JsonParser createParser(final InputStream in, final Charset charset) {
		return createParser(new InputStreamReader(in, charset));
	}

	@Override
	public JsonParser createParser(final JsonObject object) {
		return new MyrJsonObjectParser(object);
	}

	@Override
	public JsonParser createParser(final JsonArray array) {
		return new MyrJsonArrayParser(array);
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		return config;
	}
}

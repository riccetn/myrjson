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
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import jakarta.json.stream.JsonParsingException;
import se.narstrom.myr.json.parser.MyrJsonArrayParser;
import se.narstrom.myr.json.parser.MyrJsonLocation;
import se.narstrom.myr.json.parser.MyrJsonObjectParser;
import se.narstrom.myr.json.parser.MyrJsonStreamParser;
import se.narstrom.myr.json.parser.MyrReader;

public final class MyrJsonParserFactory implements JsonParserFactory {
	private final JsonProvider provider;

	private final Map<String, Object> config;

	public MyrJsonParserFactory(final JsonProvider provider, final Map<String, ?> config) {
		this.provider = provider;
		this.config = Map.copyOf(config);
	}

	@Override
	public JsonParser createParser(final Reader reader) {
		try {
			return new MyrJsonStreamParser(provider, new MyrReader(reader));
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
		return new MyrJsonObjectParser(provider, object);
	}

	@Override
	public JsonParser createParser(final JsonArray array) {
		return new MyrJsonArrayParser(provider, array);
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		return config;
	}
}

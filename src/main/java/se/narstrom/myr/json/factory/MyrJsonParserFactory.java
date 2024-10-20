package se.narstrom.myr.json.factory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
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

public final class MyrJsonParserFactory implements JsonParserFactory {
	private final JsonProvider provider;

	public MyrJsonParserFactory(final JsonProvider provider) {
		this.provider = provider;
	}

	@Override
	public JsonParser createParser(final InputStream in) {
		try {
			final PushbackInputStream pbin = new PushbackInputStream(in, 4);
			final byte[] bs = in.readNBytes(4);
			pbin.unread(bs);
			return createParser(new InputStreamReader(pbin, detectCharset(bs)));
		} catch (final IOException ex) {
			throw new JsonParsingException(ex.getMessage(), ex, new MyrJsonLocation(1, 1, 0));
		}
	}

	@Override
	public JsonParser createParser(final InputStream in, final Charset charset) {
		return createParser(new InputStreamReader(in, charset));
	}

	@Override
	public JsonParser createParser(final JsonArray array) {
		return new MyrJsonArrayParser(provider, array);
	}

	@Override
	public JsonParser createParser(final JsonObject object) {
		return new MyrJsonObjectParser(provider, object);
	}

	@Override
	public JsonParser createParser(final Reader reader) {
		return new MyrJsonStreamParser(provider, reader);
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		return Map.of();
	}

	private final Charset detectCharset(final byte[] bs) {
		if (bs.length < 4) {
			if (bs.length < 2)
				throw new JsonParsingException("Empty Stream", new MyrJsonLocation(1, 1, 0));

			if (bs[0] == 0 || bs[1] == 0)
				throw new JsonParsingException("Empty Stream", new MyrJsonLocation(1, 1, 0));

			return StandardCharsets.UTF_8;
		}

		if (bs[0] == 0) {
			if (bs[1] == 0)
				return Charset.forName("UTF-32BE");
			else
				return StandardCharsets.UTF_16BE;
		} else if (bs[1] == 0) {
			if (bs[2] == 0)
				return Charset.forName("UTF-32LE");
			else
				return StandardCharsets.UTF_16LE;
		}

		return StandardCharsets.UTF_8;
	}
}

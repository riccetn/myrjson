package se.narstrom.myr.json.factory;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParserFactory;
import se.narstrom.myr.json.io.MyrJsonReader;

public final class MyrJsonReaderFactory implements JsonReaderFactory {
	private final JsonProvider provider;

	private final JsonParserFactory parserFactory;

	public MyrJsonReaderFactory(final JsonProvider provider, final JsonParserFactory parserFactory) {
		this.provider = provider;
		this.parserFactory = parserFactory;
	}

	@Override
	public JsonReader createReader(final Reader reader) {
		return new MyrJsonReader(provider, parserFactory.createParser(reader));
	}

	@Override
	public JsonReader createReader(final InputStream in) {
		return new MyrJsonReader(provider, parserFactory.createParser(in));
	}

	@Override
	public JsonReader createReader(final InputStream in, final Charset charset) {
		return new MyrJsonReader(provider, parserFactory.createParser(in, charset));
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		return parserFactory.getConfigInUse();
	}

}
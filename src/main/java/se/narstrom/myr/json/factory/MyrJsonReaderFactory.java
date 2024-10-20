package se.narstrom.myr.json.factory;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import se.narstrom.myr.json.MyrJsonContext;
import se.narstrom.myr.json.io.MyrJsonReader;

public final class MyrJsonReaderFactory implements JsonReaderFactory {
	private final MyrJsonContext context;

	public MyrJsonReaderFactory(final MyrJsonContext context) {
		this.context = context;
	}

	@Override
	public JsonReader createReader(final InputStream in) {
		return new MyrJsonReader(context.defaultParserFactory().createParser(in), context);
	}

	@Override
	public JsonReader createReader(final InputStream in, final Charset charset) {
		return new MyrJsonReader(context.defaultParserFactory().createParser(in, charset), context);
	}

	@Override
	public JsonReader createReader(final Reader reader) {
		return new MyrJsonReader(context.defaultParserFactory().createParser(reader), context);
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		return context.defaultParserFactory().getConfigInUse();
	}
}

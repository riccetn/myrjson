package se.narstrom.myr.json.factory;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import se.narstrom.myr.json.MyrJsonContext;
import se.narstrom.myr.json.io.MyrJsonWriter;

public final class MyrJsonWriterFactory implements JsonWriterFactory {
	private final MyrJsonContext context;

	public MyrJsonWriterFactory(final MyrJsonContext context) {
		this.context = context;
	}

	@Override
	public JsonWriter createWriter(final OutputStream out) {
		return new MyrJsonWriter(context.defaultGeneratorFactory().createGenerator(out));
	}

	@Override
	public JsonWriter createWriter(final OutputStream out, final Charset charset) {
		return new MyrJsonWriter(context.defaultGeneratorFactory().createGenerator(out, charset));
	}

	@Override
	public JsonWriter createWriter(final Writer writer) {
		return new MyrJsonWriter(context.defaultGeneratorFactory().createGenerator(writer));
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		return context.defaultGeneratorFactory().getConfigInUse();
	}

}

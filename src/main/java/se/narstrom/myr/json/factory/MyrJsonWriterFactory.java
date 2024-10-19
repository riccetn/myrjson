package se.narstrom.myr.json.factory;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGeneratorFactory;
import se.narstrom.myr.json.io.MyrJsonWriter;

public final class MyrJsonWriterFactory implements JsonWriterFactory {
	private final JsonGeneratorFactory generatorFactory;

	public MyrJsonWriterFactory(final JsonGeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}

	@Override
	public JsonWriter createWriter(final OutputStream out) {
		return new MyrJsonWriter(generatorFactory.createGenerator(out));
	}

	@Override
	public JsonWriter createWriter(final OutputStream out, final Charset charset) {
		return new MyrJsonWriter(generatorFactory.createGenerator(out, charset));
	}

	@Override
	public JsonWriter createWriter(final Writer writer) {
		return new MyrJsonWriter(generatorFactory.createGenerator(writer));
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		return generatorFactory.getConfigInUse();
	}

}

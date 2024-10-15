package se.narstrom.myr.json.factory;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import se.narstrom.myr.json.generator.MyrJsonGenerator;

public final class MyrJsonGeneratorFactory implements JsonGeneratorFactory {

	@Override
	public JsonGenerator createGenerator(final Writer writer) {
		return new MyrJsonGenerator(writer);
	}

	@Override
	public JsonGenerator createGenerator(final OutputStream out) {
		return createGenerator(new OutputStreamWriter(out, StandardCharsets.UTF_8));
	}

	@Override
	public JsonGenerator createGenerator(final OutputStream out, final Charset charset) {
		return createGenerator(new OutputStreamWriter(out, charset));
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		return Map.of();
	}

}

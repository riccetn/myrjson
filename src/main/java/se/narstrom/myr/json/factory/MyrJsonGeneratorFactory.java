package se.narstrom.myr.json.factory;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import se.narstrom.myr.json.MyrJsonContext;
import se.narstrom.myr.json.generator.MyrJsonGenerator;

public final class MyrJsonGeneratorFactory implements JsonGeneratorFactory {
	private final MyrJsonContext context;

	public MyrJsonGeneratorFactory(final MyrJsonContext context) {
		this.context = context;
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
	public JsonGenerator createGenerator(final Writer writer) {
		return new MyrJsonGenerator(writer);
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		final Object prettyPrinting = context.getConfiguredPrettyPrinting();
		if (prettyPrinting != null)
			return Map.of(JsonGenerator.PRETTY_PRINTING, prettyPrinting);
		else
			return Map.of();
	}

}

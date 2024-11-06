package se.narstrom.myr.json;

import java.util.Map;

import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonConfig;
import jakarta.json.JsonConfig.KeyStrategy;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonParserFactory;
import se.narstrom.myr.json.factory.MyrJsonBuilderFactory;
import se.narstrom.myr.json.factory.MyrJsonGeneratorFactory;
import se.narstrom.myr.json.factory.MyrJsonParserFactory;
import se.narstrom.myr.json.factory.MyrJsonReaderFactory;
import se.narstrom.myr.json.factory.MyrJsonWriterFactory;

public final class MyrJsonContext {
	private final JsonBuilderFactory defaultBuilderFactory = new MyrJsonBuilderFactory(this);

	private final JsonGeneratorFactory defaultGeneratorFactory = new MyrJsonGeneratorFactory(this);

	private final JsonParserFactory defaultParserFactory = new MyrJsonParserFactory(this);

	private final JsonReaderFactory defaultReaderFactory = new MyrJsonReaderFactory(this);

	private final JsonWriterFactory defaultWriterFactory = new MyrJsonWriterFactory(this);

	private final KeyStrategy keyStrategy;

	private final Object prettyPrinting;

	public MyrJsonContext(final Map<String, ?> config) {
		this.keyStrategy = (KeyStrategy) config.get(JsonConfig.KEY_STRATEGY);
		this.prettyPrinting = config.get(JsonGenerator.PRETTY_PRINTING);
	}

	public JsonBuilderFactory defaultBuilderFactory() {
		return this.defaultBuilderFactory;
	}

	public JsonGeneratorFactory defaultGeneratorFactory() {
		return this.defaultGeneratorFactory;
	}

	public JsonParserFactory defaultParserFactory() {
		return this.defaultParserFactory;
	}

	public JsonReaderFactory defaultReaderFactory() {
		return this.defaultReaderFactory;
	}

	public JsonWriterFactory defaultWriterFactory() {
		return this.defaultWriterFactory;
	}

	public KeyStrategy getConfiguredKeyStrategy() {
		return keyStrategy;
	}

	public Object getConfiguredPrettyPrinting() {
		return prettyPrinting;
	}

	public KeyStrategy getKeyStrategy() {
		if (keyStrategy == null)
			return KeyStrategy.LAST;
		else
			return this.keyStrategy;
	}
}

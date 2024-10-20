package se.narstrom.myr.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;

import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonConfig;
import jakarta.json.JsonNumber;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonString;
import jakarta.json.JsonConfig.KeyStrategy;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonParserFactory;
import se.narstrom.myr.json.factory.MyrJsonBuilderFactory;
import se.narstrom.myr.json.factory.MyrJsonGeneratorFactory;
import se.narstrom.myr.json.factory.MyrJsonParserFactory;
import se.narstrom.myr.json.factory.MyrJsonReaderFactory;
import se.narstrom.myr.json.value.MyrJsonNumber;
import se.narstrom.myr.json.value.MyrJsonString;

public final class MyrJsonContext {
	private final JsonBuilderFactory defaultBuilderFactory = new MyrJsonBuilderFactory(this);

	private final JsonGeneratorFactory defaultGeneratorFactory = new MyrJsonGeneratorFactory(this);

	private final JsonParserFactory defaultParserFactory = new MyrJsonParserFactory(this);

	private final JsonReaderFactory defaultReaderFactory = new MyrJsonReaderFactory(this);

	private final KeyStrategy keyStrategy;

	private final Object prettyPrinting;

	MyrJsonContext(final Map<String, ?> config) {
		this.keyStrategy = (KeyStrategy) config.get(JsonConfig.KEY_STRATEGY);
		this.prettyPrinting = config.get(JsonGenerator.PRETTY_PRINTING);
	}

	public JsonBuilderFactory createBuilderFactory() {
		return new MyrJsonBuilderFactory(this);
	}

	public JsonGeneratorFactory createGeneratorFactory() {
		return new MyrJsonGeneratorFactory(this);
	}

	public JsonParserFactory createParserFactory() {
		return new MyrJsonParserFactory(this);
	}

	public JsonReaderFactory createReaderFactory() {
		return new MyrJsonReaderFactory(this);
	}

	public JsonNumber createValue(final BigDecimal value) {
		return new MyrJsonNumber(Objects.requireNonNull(value));
	}

	public JsonNumber createValue(final BigInteger value) {
		return createValue(new BigDecimal(value));
	}

	public JsonNumber createValue(final double value) {
		return createValue(BigDecimal.valueOf(value));
	}

	public JsonNumber createValue(int value) {
		return createValue(BigDecimal.valueOf(value));
	}

	public JsonNumber createValue(long value) {
		return createValue(BigDecimal.valueOf(value));
	}

	public JsonNumber createValue(final Number number) {
		return switch (number) {
			case Byte num -> createValue(num.intValue());
			case Short num -> createValue(num.intValue());
			case Integer num -> createValue(num.intValue());
			case Long num -> createValue(num.longValue());
			case Float num -> createValue(num.doubleValue());
			case Double num -> createValue(num.doubleValue());
			case BigInteger num -> createValue(num);
			case BigDecimal num -> createValue(num);
			default -> createValue(number.longValue());
		};
	}

	public JsonString createValue(final String value) {
		return new MyrJsonString(Objects.requireNonNull(value));
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

	public KeyStrategy getConfiguredKeyStrategy() {
		return keyStrategy;
	}

	public Object getConfiguredPrettyPrinting() {
		return prettyPrinting;
	}

	public KeyStrategy getKeyStrategy() {
		if (keyStrategy == null)
			return KeyStrategy.FIRST;
		else
			return this.keyStrategy;
	}
}

package se.narstrom.myr.json.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import jakarta.json.JsonNumber;

public final class MyrJsonNumber implements JsonNumber {
	private final BigDecimal value;

	public MyrJsonNumber(final BigDecimal value) {
		Objects.requireNonNull(value);
		this.value = value;
	}

	public MyrJsonNumber(final BigInteger value) {
		this.value = new BigDecimal(value);
	}

	public MyrJsonNumber(final double value) {
		this.value = BigDecimal.valueOf(value);
	}

	public MyrJsonNumber(final long value) {
		this.value = BigDecimal.valueOf(value);
	}

	@Override
	public BigDecimal bigDecimalValue() {
		return value;
	}

	@Override
	public BigInteger bigIntegerValue() {
		return value.toBigInteger();
	}

	@Override
	public BigInteger bigIntegerValueExact() {
		return value.toBigIntegerExact();
	}

	@Override
	public double doubleValue() {
		return value.doubleValue();
	}

	@Override
	public boolean equals(final Object otherObject) {
		return (otherObject instanceof JsonNumber other) && this.value.compareTo(other.bigDecimalValue()) == 0;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.NUMBER;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public int intValue() {
		return value.intValue();
	}

	@Override
	public int intValueExact() {
		return value.intValueExact();
	}

	@Override
	public boolean isIntegral() {
		return value.scale() == 0;
	}

	@Override
	public long longValue() {
		return value.longValue();
	}

	@Override
	public long longValueExact() {
		return value.longValueExact();
	}

	@Override
	public String toString() {
		return value.toString();
	}
}

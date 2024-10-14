package se.narstrom.myr.json.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import jakarta.json.JsonNumber;

public final class MyrJsonNumber implements JsonNumber {
	private final BigDecimal value;

	public MyrJsonNumber(final BigDecimal value) {
		this.value = value;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.NUMBER;
	}

	@Override
	public boolean isIntegral() {
		return value.scale() == 0;
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
	public long longValue() {
		return value.longValue();
	}

	@Override
	public long longValueExact() {
		return value.longValueExact();
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
	public BigDecimal bigDecimalValue() {
		return value;
	}

	@Override
	public boolean equals(final Object otherObject) {
		return (otherObject instanceof JsonNumber other) && Objects.equals(this.value, other.bigDecimalValue());
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		return value.toString();
	}
}

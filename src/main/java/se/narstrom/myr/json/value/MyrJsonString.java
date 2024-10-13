package se.narstrom.myr.json.value;

import java.util.Objects;

import jakarta.json.JsonString;

public final class MyrJsonString implements JsonString {
	private final String value;

	public MyrJsonString(final String value) {
		this.value = value;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.STRING;
	}

	@Override
	public String getString() {
		return value;
	}

	@Override
	public CharSequence getChars() {
		return value;
	}

	@Override
	public boolean equals(final Object otherObject) {
		return (otherObject instanceof JsonString other) && Objects.equals(this.value, other.getString());
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

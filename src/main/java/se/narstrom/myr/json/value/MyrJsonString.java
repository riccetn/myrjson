package se.narstrom.myr.json.value;

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

}

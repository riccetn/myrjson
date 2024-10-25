package se.narstrom.myr.json.value;

import java.util.Objects;

import jakarta.json.JsonString;

public final class MyrJsonString implements JsonString {
	private final String value;

	public MyrJsonString(final String value) {
		Objects.requireNonNull(value);
		this.value = value;
	}

	@Override
	public boolean equals(final Object otherObject) {
		return (otherObject instanceof JsonString other) && Objects.equals(this.value, other.getString());
	}

	@Override
	public CharSequence getChars() {
		return value;
	}

	@Override
	public String getString() {
		return value;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.STRING;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append('\"');
		for (int i = 0; i < value.length(); ++i) {
			final char ch = value.charAt(i);
			switch (ch) {
				case '\"' -> sb.append("\\\"");
				case '\\' -> sb.append("\\\\");
				case '\b' -> sb.append("\\b");
				case '\f' -> sb.append("\\f");
				case '\n' -> sb.append("\\n");
				case '\r' -> sb.append("\\r");
				case '\t' -> sb.append("\\t");
				default -> {
					if (Character.isISOControl(ch)) {
						sb.append(String.format("\\u%04x", (int) ch));
					} else {
						sb.append(ch);
					}
				}
			}
		}
		sb.append('\"');
		return sb.toString();
	}
}

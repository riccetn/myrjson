package se.narstrom.myr.json.patch;

import java.util.Map;

import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

public final class MyrJsonMergePatch implements JsonMergePatch {

	private final JsonBuilderFactory builderFacotry;

	private final JsonValue patch;

	public MyrJsonMergePatch(final JsonValue patch, final JsonBuilderFactory builderFactory) {
		this.patch = patch;
		this.builderFacotry = builderFactory;
	}

	@Override
	public JsonValue apply(final JsonValue target) {
		return apply(target, this.patch);
	}

	// https://datatracker.ietf.org/doc/html/rfc7396#section-2
	private JsonValue apply(final JsonValue target, final JsonValue patch) {
		if (patch.getValueType() == ValueType.OBJECT) {
			final JsonObjectBuilder targetBuilder;
			if (target.getValueType() != ValueType.OBJECT) {
				targetBuilder = builderFacotry.createObjectBuilder();
			} else {
				targetBuilder = builderFacotry.createObjectBuilder(target.asJsonObject());
			}
			for (final Map.Entry<String, JsonValue> entry : patch.asJsonObject().entrySet()) {
				if (entry.getValue() == JsonValue.NULL)
					targetBuilder.remove(entry.getKey());
				else
					targetBuilder.add(entry.getKey(),
							apply(target.asJsonObject().get(entry.getKey()), entry.getValue()));
			}
			return targetBuilder.build();
		} else {
			return patch;
		}
	}

	@Override
	public JsonValue toJsonValue() {
		return patch;
	}

}

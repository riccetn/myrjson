package se.narstrom.myr.json.patch;

import java.util.Map;
import java.util.Objects;

import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
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

	public MyrJsonMergePatch(final JsonValue source, final JsonValue target, final JsonBuilderFactory builderFactory) {
		this(mergeDiff(source, target, builderFactory), builderFactory);
	}

	@Override
	public JsonValue apply(final JsonValue target) {
		return apply(target, this.patch);
	}

	@Override
	public JsonValue toJsonValue() {
		return patch;
	}

	// https://datatracker.ietf.org/doc/html/rfc7396#section-2
	private JsonValue apply(final JsonValue target, final JsonValue patch) {
		if(patch.getValueType() != ValueType.OBJECT)
			return patch;

		final JsonObject targetObject;
		final JsonObjectBuilder targetBuilder;
		if (target.getValueType() != ValueType.OBJECT) {
			targetObject = builderFacotry.createObjectBuilder().build();
			targetBuilder = builderFacotry.createObjectBuilder();
		} else {
			targetObject = target.asJsonObject();
			targetBuilder = builderFacotry.createObjectBuilder(target.asJsonObject());
		}

		for (final Map.Entry<String, JsonValue> entry : patch.asJsonObject().entrySet()) {
			if (entry.getValue() == JsonValue.NULL)
				targetBuilder.remove(entry.getKey());
			else if(targetObject.containsKey(entry.getKey()))
				targetBuilder.add(entry.getKey(), apply(targetObject.get(entry.getKey()), entry.getValue()));
			else
				targetBuilder.add(entry.getKey(), entry.getValue());
		}
		return targetBuilder.build();
	}

	private static JsonValue mergeDiff(final JsonValue source, final JsonValue target, final JsonBuilderFactory builderFactory) {
		if (source.getValueType() != ValueType.OBJECT || target.getValueType() != ValueType.OBJECT)
			return target;

		final JsonObject sourceObject = source.asJsonObject();
		final JsonObject targetObject = target.asJsonObject();

		final JsonObjectBuilder resultBuilder = builderFactory.createObjectBuilder();
		for (final Map.Entry<String, JsonValue> entry : targetObject.entrySet()) {
			final JsonValue sourceValue = sourceObject.get(entry.getKey());
			if (sourceValue == null)
				resultBuilder.add(entry.getKey(), entry.getValue());
			else if(!Objects.equals(sourceValue, entry.getValue()))
				resultBuilder.add(entry.getKey(), mergeDiff(sourceValue, entry.getValue(), builderFactory));
		}

		for (final String key : sourceObject.keySet()) {
			if (!targetObject.containsKey(key))
				resultBuilder.add(key, JsonValue.NULL);
		}

		return resultBuilder.build();
	}
}

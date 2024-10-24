package se.narstrom.myr.json.patch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatch.Operation;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import se.narstrom.myr.json.patch.MyrJsonPatch.OperationData;
import se.narstrom.myr.json.value.MyrJsonString;

public final class MyrJsonPatchBuilder implements JsonPatchBuilder {

	private final JsonProvider provider;

	private final JsonBuilderFactory builderFactory;

	private final List<OperationData> operationList = new ArrayList<>();

	public MyrJsonPatchBuilder(final JsonProvider provider, final JsonBuilderFactory builderFactory) {
		this.provider = provider;
		this.builderFactory = builderFactory;
	}

	@Override
	public JsonPatchBuilder add(final String path, final boolean value) {
		return add(path, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonPatchBuilder add(final String path, final int value) {
		return add(path, provider.createValue(value));
	}

	@Override
	public JsonPatchBuilder add(final String path, final JsonValue value) {
		operationList.add(new OperationData(Operation.ADD, provider.createPointer(path), value, null));
		return this;
	}

	@Override
	public JsonPatchBuilder add(final String path, final String value) {
		return add(path, new MyrJsonString(value));
	}

	@Override
	public JsonPatch build() {
		return new MyrJsonPatch(Collections.unmodifiableList(operationList), provider, builderFactory);
	}

	@Override
	public JsonPatchBuilder copy(final String path, final String from) {
		operationList.add(new OperationData(Operation.COPY, provider.createPointer(path), null, provider.createPointer(from)));
		return this;
	}

	@Override
	public JsonPatchBuilder move(final String path, final String from) {
		operationList.add(new OperationData(Operation.MOVE, provider.createPointer(path), null, provider.createPointer(from)));
		return null;
	}

	@Override
	public JsonPatchBuilder remove(final String path) {
		operationList.add(new OperationData(Operation.REMOVE, provider.createPointer(path), null, null));
		return this;
	}

	@Override
	public JsonPatchBuilder replace(final String path, final boolean value) {
		return replace(path, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonPatchBuilder replace(final String path, final int value) {
		return replace(path, provider.createValue(value));
	}

	@Override
	public JsonPatchBuilder replace(String path, JsonValue value) {
		operationList.add(new OperationData(Operation.REPLACE, provider.createPointer(path), value, null));
		return this;
	}

	@Override
	public JsonPatchBuilder replace(final String path, final String value) {
		return replace(path, provider.createValue(value));
	}

	@Override
	public JsonPatchBuilder test(final String path, final boolean value) {
		return test(path, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonPatchBuilder test(final String path, final int value) {
		return test(path, provider.createValue(value));
	}

	@Override
	public JsonPatchBuilder test(final String path, final JsonValue value) {
		operationList.add(new OperationData(Operation.TEST, provider.createPointer(path), value, null));
		return this;
	}

	@Override
	public JsonPatchBuilder test(final String path, final String value) {
		return test(path, provider.createValue(value));
	}

}

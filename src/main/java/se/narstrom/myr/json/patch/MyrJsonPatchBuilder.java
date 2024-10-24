package se.narstrom.myr.json.patch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.json.JsonPatch;
import jakarta.json.JsonPatch.Operation;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonValue;
import se.narstrom.myr.json.MyrJsonContext;
import se.narstrom.myr.json.patch.MyrJsonPatch.OperationData;
import se.narstrom.myr.json.value.MyrJsonNumber;
import se.narstrom.myr.json.value.MyrJsonString;

public final class MyrJsonPatchBuilder implements JsonPatchBuilder {

	private final MyrJsonContext context;

	private final List<OperationData> operationList = new ArrayList<>();

	public MyrJsonPatchBuilder(final MyrJsonContext context) {
		this.context = context;
	}

	@Override
	public JsonPatchBuilder add(final String path, final boolean value) {
		return add(path, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonPatchBuilder add(final String path, final int value) {
		return add(path, new MyrJsonNumber(value));
	}

	@Override
	public JsonPatchBuilder add(final String path, final JsonValue value) {
		operationList.add(new OperationData(Operation.ADD, new MyrJsonPointer(path, context), value, null));
		return this;
	}

	@Override
	public JsonPatchBuilder add(final String path, final String value) {
		return add(path, new MyrJsonString(value));
	}

	@Override
	public JsonPatch build() {
		return new MyrJsonPatch(Collections.unmodifiableList(operationList), context);
	}

	@Override
	public JsonPatchBuilder copy(final String path, final String from) {
		operationList.add(new OperationData(Operation.COPY, new MyrJsonPointer(path, context), null, new MyrJsonPointer(from, context)));
		return this;
	}

	@Override
	public JsonPatchBuilder move(final String path, final String from) {
		operationList.add(new OperationData(Operation.MOVE, new MyrJsonPointer(path, context), null, new MyrJsonPointer(from, context)));
		return null;
	}

	@Override
	public JsonPatchBuilder remove(final String path) {
		operationList.add(new OperationData(Operation.REMOVE, new MyrJsonPointer(path, context), null, null));
		return this;
	}

	@Override
	public JsonPatchBuilder replace(final String path, final boolean value) {
		return replace(path, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonPatchBuilder replace(final String path, final int value) {
		return replace(path, new MyrJsonNumber(value));
	}

	@Override
	public JsonPatchBuilder replace(String path, JsonValue value) {
		operationList.add(new OperationData(Operation.REPLACE, new MyrJsonPointer(path, context), value, null));
		return this;
	}

	@Override
	public JsonPatchBuilder replace(final String path, final String value) {
		return replace(path, new MyrJsonString(value));
	}

	@Override
	public JsonPatchBuilder test(final String path, final boolean value) {
		return test(path, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonPatchBuilder test(final String path, final int value) {
		return test(path, new MyrJsonNumber(value));
	}

	@Override
	public JsonPatchBuilder test(final String path, final JsonValue value) {
		operationList.add(new OperationData(Operation.TEST, new MyrJsonPointer(path, context), value, null));
		return this;
	}

	@Override
	public JsonPatchBuilder test(final String path, final String value) {
		return test(path, new MyrJsonString(value));
	}

}

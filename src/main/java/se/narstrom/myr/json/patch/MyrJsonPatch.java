package se.narstrom.myr.json.patch;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPointer;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import se.narstrom.myr.json.MyrJsonContext;
import se.narstrom.myr.json.value.MyrJsonArrayBuilder;
import se.narstrom.myr.json.value.MyrJsonObjectBuilder;

public final class MyrJsonPatch implements JsonPatch {
	private final MyrJsonContext context;

	private final List<OperationData> operations;

	public MyrJsonPatch(final JsonArray json, final MyrJsonContext context) {
		this(json.stream().map(val -> operationFromObject((JsonObject) val, context)).toList(), context);
	}

	public MyrJsonPatch(final JsonStructure source, final JsonStructure target, final MyrJsonContext context) {
		// TODO Auto-generated constructor stub
		this(List.of(), context);
	}

	MyrJsonPatch(final List<OperationData> operations, final MyrJsonContext context) {
		this.operations = operations;
		this.context = context;
	}

	@Override
	public <T extends JsonStructure> T apply(final T target) {
		T t = target;
		for (final OperationData op : operations) {
			switch (op.op()) {
				case ADD -> t = op.path().add(t, op.value());
				case COPY -> t = op.path().add(t, op.from().getValue(t));
				case MOVE -> {
					final JsonValue newValue = op.from().getValue(t);
					t = op.from().remove(t);
					t = op.path().add(t, newValue);
				}
				case REMOVE -> t = op.path().remove(t);
				case REPLACE -> t = op.path.replace(t, op.value());
				case TEST -> {
					if (!Objects.equals(op.path().getValue(t), op.value()))
						throw new JsonException("TEST failed");
				}
			}
		}

		return t;
	}

	@Override
	public JsonArray toJsonArray() {
		return operations.stream().map(op -> objectFromOperation(op, context)).collect(Collector.of(() -> (JsonArrayBuilder) new MyrJsonArrayBuilder(context), JsonArrayBuilder::add, JsonArrayBuilder::add, JsonArrayBuilder::build));
	}

	private static JsonObject objectFromOperation(final OperationData operation, final MyrJsonContext context) {
		final JsonObjectBuilder objectBuilder = new MyrJsonObjectBuilder(context);
		objectBuilder.add("op", operation.op().operationName());
		objectBuilder.add("path", operation.path().toString());
		if (operation.value() != null)
			objectBuilder.add("value", operation.value());
		if (operation.from() != null)
			objectBuilder.add("from", operation.from().toString());
		return objectBuilder.build();
	}

	static OperationData operationFromObject(final JsonObject object, final MyrJsonContext context) {
		final Operation op = Operation.fromOperationName(object.getString("op"));
		final JsonPointer path = new MyrJsonPointer(object.getString("path"), context);
		final JsonValue value = switch (op) {
			case REMOVE -> null;
			default -> object.get("value");
		};
		final JsonPointer from = switch (op) {
			case MOVE, COPY -> new MyrJsonPointer(object.getString("from"), context);
			default -> null;
		};
		return new OperationData(op, path, value, from);
	}

	record OperationData(Operation op, JsonPointer path, JsonValue value, JsonPointer from) {
	}
}

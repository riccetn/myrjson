package se.narstrom.myr.json.patch;

import java.util.List;
import java.util.stream.Collector;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPointer;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

public final class MyrJsonPatch implements JsonPatch {
	private final JsonProvider provider;

	private final JsonBuilderFactory builderFactory;

	private final List<OperationData> operations;

	MyrJsonPatch(final List<OperationData> operations, final JsonProvider provider, final JsonBuilderFactory builderFactory) {
		this.operations = operations;
		this.provider = provider;
		this.builderFactory = builderFactory;
	}

	public MyrJsonPatch(final JsonArray json, final JsonProvider provider, final JsonBuilderFactory builderFactory) {
		this(json.stream().map(val -> operationFromObject((JsonObject) val, provider)).toList(), provider, builderFactory);
	}

	public MyrJsonPatch(final JsonStructure source, final JsonStructure target, final JsonProvider provider, final JsonBuilderFactory builderFactory) {
		// TODO Auto-generated constructor stub
		this(List.of(), provider, builderFactory);
	}

	@Override
	public <T extends JsonStructure> T apply(T target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonArray toJsonArray() {
		return operations.stream().map(op -> objectFromOperation(op, provider, builderFactory)).collect(Collector.of(builderFactory::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add, b -> b.build()));
	}

	private static OperationData operationFromObject(final JsonObject object, final JsonProvider provider) {
		final Operation op = Operation.fromOperationName(object.getString("op"));
		final JsonPointer path = provider.createPointer(object.getString("path"));
		final JsonValue value = switch (op) {
			case REMOVE -> null;
			default -> object.get("value");
		};
		final JsonPointer from = switch (op) {
			case MOVE, COPY -> provider.createPointer(object.getString("from"));
			default -> null;
		};
		return new OperationData(op, path, value, from);
	}

	private static JsonObject objectFromOperation(final OperationData operation, final JsonProvider provider, final JsonBuilderFactory builderFactory) {
		final JsonObjectBuilder objectBuilder = builderFactory.createObjectBuilder();
		objectBuilder.add("op", operation.op().operationName());
		objectBuilder.add("path", operation.path().toString());
		if (operation.value() != null)
			objectBuilder.add("value", operation.value());
		if (operation.from() != null)
			objectBuilder.add("from", operation.from().toString());
		return objectBuilder.build();
	}

	record OperationData(Operation op, JsonPointer path, JsonValue value, JsonPointer from) {
	}
}

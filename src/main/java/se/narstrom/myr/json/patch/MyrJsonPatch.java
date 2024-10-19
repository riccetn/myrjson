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
	
	private final List<Operation> operations;

	private MyrJsonPatch(final List<Operation> operations, final JsonProvider provider, final JsonBuilderFactory builderFactory) {
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
		return operations.stream().map(op -> objectFromOperation(op, provider, builderFactory))
				.collect(Collector.of(builderFactory::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add, b -> b.build()));
	}

	private static Operation operationFromObject(final JsonObject object, final JsonProvider provider) {
		final OperationType type = OperationType.valueOf(object.getString("op").toUpperCase());
		final JsonPointer path = provider.createPointer(object.getString("path"));
		final JsonValue value = switch (type) {
			case REMOVE -> null;
			default -> object.get("value");
		};
		final JsonPointer from = switch (type) {
			case MOVE, COPY -> provider.createPointer(object.getString("from"));
			default -> null;
		};
		return new Operation(type, path, value, from);
	}

	private static JsonObject objectFromOperation(final Operation operation, final JsonProvider provider, final JsonBuilderFactory builderFactory) {
		final JsonObjectBuilder objectBuilder = builderFactory.createObjectBuilder();
		objectBuilder.add("op", operation.type().name().toLowerCase());
		objectBuilder.add("path", operation.path().toString());
		if (operation.value() != null)
			objectBuilder.add("value", operation.value());
		if (operation.from() != null)
			objectBuilder.add("from", operation.from().toString());
		return objectBuilder.build();
	}

	private enum OperationType {
		ADD, REMOVE, REPLACE, MOVE, COPY, TEST
	}

	private record Operation(OperationType type, JsonPointer path, JsonValue value, JsonPointer from) {
	}
}

package se.narstrom.myr.json.stream;

import jakarta.json.stream.JsonLocation;

public record MyrJsonLocation(long lineNo, long columnNo, long offset) implements JsonLocation {

	@Override
	public long getLineNumber() {
		return lineNo;
	}

	@Override
	public long getColumnNumber() {
		return columnNo;
	}

	@Override
	public long getStreamOffset() {
		return offset;
	}
}

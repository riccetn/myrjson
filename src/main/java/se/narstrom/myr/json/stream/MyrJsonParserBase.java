package se.narstrom.myr.json.stream;

import jakarta.json.stream.JsonParser;

public abstract class MyrJsonParserBase implements JsonParser {

	@Override
	public boolean isIntegralNumber() {
		return getBigDecimal().scale() == 0;
	}

	@Override
	public int getInt() {
		return getBigDecimal().intValue();
	}

	@Override
	public long getLong() {
		return getBigDecimal().longValue();
	}
}

package se.narstrom.myr.json.parser;

import java.io.EOFException;
import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

import jakarta.json.stream.JsonLocation;

public final class MyrReader extends FilterReader {
	private char[] buf = new char[4096];

	private int buflen = 0;

	private int bufp = 0;

	private long lineNo = 1;

	private long columnNo = 1;

	private long offset = 0;

	public MyrReader(final Reader in) throws IOException {
		super(in);
		maybeFillBuffer();
	}

	@Override
	public int read() throws IOException {
		maybeFillBuffer();
		if (buflen == -1)
			return -1;

		final char ch = buf[bufp++];
		if (ch == '\n') {
			++lineNo;
			columnNo = 1;
		} else {
			++columnNo;
		}
		++offset;

		return ch;
	}

	public char readChar() throws IOException {
		int ch = read();
		if (ch == -1)
			throw new EOFException();
		return (char) ch;
	}

	public char peekChar() throws IOException {
		maybeFillBuffer();
		if (buflen == -1)
			throw new EOFException();
		return buf[bufp];
	}

	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException {
		Objects.checkFromIndexSize(off, len, cbuf.length);
		maybeFillBuffer();
		if (buflen == -1)
			return -1;

		final int realLen = Math.min(len, buflen - bufp);
		System.arraycopy(buf, bufp, cbuf, off, realLen);

		for (int i = bufp; i < bufp + realLen; ++i) {
			if (buf[i] == '\n') {
				++lineNo;
				columnNo = 1;
			} else {
				++columnNo;
			}
		}
		bufp += realLen;
		offset += realLen;

		return realLen;
	}

	public void readChars(final char[] chs) throws IOException {
		int read = 0;
		while (read < chs.length) {
			int r = read(chs, read, chs.length - read);
			if (r == -1)
				throw new EOFException();
			read += r;
		}
	}

	public void skipWhitespace() throws IOException {
		while (true) {
			maybeFillBuffer();
			if (buflen == -1)
				throw new EOFException();
			while (bufp < buflen) {
				char ch = buf[bufp];
				if (ch == '\n') {
					++lineNo;
					columnNo = 1;
				} else {
					++columnNo;
				}
				++offset;

				if (ch != ' ' && ch != '\n' && ch != '\r' && ch != '\t')
					return;

				++bufp;
			}
		}
	}

	public JsonLocation getLocation() {
		return new MyrJsonLocation(lineNo, columnNo, offset);
	}

	private void maybeFillBuffer() throws IOException {
		if (bufp >= buflen)
			buflen = in.read(buf);
	}
}

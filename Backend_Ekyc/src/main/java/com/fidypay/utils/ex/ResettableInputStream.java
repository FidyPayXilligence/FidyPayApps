package com.fidypay.utils.ex;

import java.io.IOException;
import java.io.InputStream;

public class ResettableInputStream extends InputStream {
	private final InputStream original;
	private long marked = 0;

	public ResettableInputStream(InputStream original) {
		this.original = original;
		this.marked = 0;
	}

	@Override
	public int read() throws IOException {
		int b = original.read();
		if (b != -1) {
			marked++;
		}
		return b;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int bytesRead = original.read(b, off, len);
		if (bytesRead != -1) {
			marked += bytesRead;
		}
		return bytesRead;
	}

	@Override
	public void mark(int readlimit) {
		original.mark(readlimit);
	}

	@Override
	public void reset() throws IOException {
		original.reset();
		marked = 0;
	}

	@Override
	public boolean markSupported() {
		return original.markSupported();
	}

	public long getMarked() {
		return marked;
	}
}

package com.daemonize.game.soundmanager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferBackedInputStream extends InputStream {

    ByteBuffer buf;
    long length;


    public long getLength() {
        return length;
    }

    public ByteBufferBackedInputStream(ByteBuffer buf) {
        this.buf = buf;
        this.length = buf.array().length;
    }

    @Override
    public int read() throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }
        return buf.get() & 0xFF;
    }

    @Override
    public int read(byte[] bytes, int off, int len)
            throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buf.remaining());
        buf.get(bytes, off, len);
        return len;
    }

    @Override
    public ByteBufferBackedInputStream clone() throws CloneNotSupportedException {
        ByteBuffer clone = ByteBuffer.allocate((int)length);
        clone.put(buf.array());
        return new ByteBufferBackedInputStream(clone);
    }
}

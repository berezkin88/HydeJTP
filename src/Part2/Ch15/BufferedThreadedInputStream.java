package Part2.Ch15;

import java.io.*;

public class BufferedThreadedInputStream extends FilterInputStream {

//    fixed class that does *not* have a synchronized close()
    private static class BISFix extends BufferedInputStream {
    public BISFix(InputStream rawIn, int buffSize) {
        super(rawIn, buffSize);
    }

    @Override
    public void close() throws IOException {
        if (in != null) {
            try {
                in.close();
            } finally {
                in = null;
            }
        }
    }
}

    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param rawIn the underlying input stream, or <code>null</code> if
     *           this instance is to be created without an underlying stream.
     */
    public BufferedThreadedInputStream(InputStream rawIn, int bufferSize) {

        super(rawIn); // super-class' "in" is set below

//        rawIn -> BufferedIS -> ThreadedIS -> BufferedIS -> read()

        BISFix bis = new BISFix(rawIn, bufferSize);
        ThreadedInputStream tis = new ThreadedInputStream(bis, bufferSize);

//        change the protected variable 'in' from the superclass from rawIn to the correct stream
        in = new BISFix(tis, bufferSize);
    }

    public BufferedThreadedInputStream(InputStream rawIn) {
        this(rawIn, 2048);
    }

//    override to show that InterruptedIOException might be thrown
    @Override
    public int read(byte[] b) throws IOException, InterruptedIOException {
        return in.read(b);
    }

//    override to show that InterruptedIOException might be thrown
    @Override
    public int read(byte[] b, int off, int len) throws IOException, InterruptedIOException {
        return in.read(b, off, len);
    }

//    override to show that InterruptedIOException might be thrown
    @Override
    public long skip(long n) throws IOException, InterruptedIOException {
        return in.skip(n);
    }

//    the remainder of the methods are directly inherited from FilterInputStream and access "in" in the much the same
//    way as the methods above do.
}

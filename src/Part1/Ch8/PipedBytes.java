package Part1.Ch8;

import java.io.*;

public class PipedBytes {
    public static void writeStuff (OutputStream rawOut) {
        try {
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(rawOut));

            int[] data = { 82, 105, 99, 104, 97, 114, 100, 32, 72, 121, 100, 101 };

            for (int i = 0; i < data.length; i++) {
                out.writeInt(data[i]);
            }

            out.flush();
            out.close();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    public static void readStuff(InputStream rawIn) {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(rawIn));

            boolean eof = false;
            while ( !eof ) {
                try {
                int i = in.readInt();
                System.out.println("just read: " + i);
                } catch (EOFException x) {
                    eof = true;
                }
            }

            System.out.println("Read all data from the pipe");
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            final PipedOutputStream out = new PipedOutputStream();
            final PipedInputStream in = new PipedInputStream(out);

            Runnable runA = () -> writeStuff(out);

            Thread threadA = new Thread(runA, "threadA");
            threadA.start();

            Runnable runB = () -> readStuff(in);

            Thread threadB = new Thread(runB, "threadB");
            threadB.start();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}

package Part2.Ch18;

import java.io.*;

public class ByteFIFOTest {
    private ByteFIFO fifo;
    private byte[] srcData;

    public ByteFIFOTest() throws IOException {
        fifo = new ByteFIFO(20);

        makeSrcData();
        System.out.println("srcData.length=" + srcData.length);

        Runnable srcRunnable = this::src;
        Thread srcThread = new Thread(srcRunnable);
        srcThread.start();

        Runnable dstRunnable = this::dst;

        Thread dstThread = new Thread(dstRunnable);
        dstThread.start();
    }

    private void makeSrcData() throws IOException{
        String[] list = {
                "The first string is right here",
                "The second string is a bit longer and also right here",
                "The third string",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "0123456789",
                "The last string in the list"
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(list);
        oos.flush();
        oos.close();

        srcData = baos.toByteArray();
    }

    private void src() {
        try {
            boolean justAddOne = true;
            int count = 0;

            while (count < srcData.length) {
                if (!justAddOne) {
                    int writeSize = (int) (40.0 * Math.random());
                    writeSize = Math.min(writeSize, srcData.length - count);

                    byte[] buf = new byte[writeSize];
                    System.arraycopy(srcData, count, buf, 0, writeSize);
                    fifo.add(buf);
                    count += writeSize;

                    System.out.println("just added " + writeSize + " bytes");
                } else {
                    fifo.add(srcData[count]);
                    count++;

                    System.out.println("just added exactly 1 byte");
                }

                justAddOne = !justAddOne;
            }
        } catch (InterruptedException x) {
            x.printStackTrace();
        }
    }

    private void dst() {
        try {
            boolean justAddOne = true;
            int count = 0;
            byte[] dstData = new byte[srcData.length];

            while (count < dstData.length) {
                if (!justAddOne) {
                    byte[] buf = fifo.removeAll();
                    if (buf.length > 0) {
                        System.arraycopy(buf, 0, dstData, count, buf.length);
                        count += buf.length;
                    }

                    System.out.println("just removed " + buf.length + " bytes");
                } else {
                    byte b = fifo.remove();
                    dstData[count] = b;
                    count++;

                    System.out.println("just removed exactly 1 byte");
                }

                justAddOne = !justAddOne;
            }

            System.out.println("received all data, count=" + count);

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(dstData));

            String[] line = (String[]) ois.readObject();

            for (int i = 0, k = line.length; i < k; i++) {
                System.out.println("line[" + i + "]=" + line[i]);
            }
        } catch (ClassNotFoundException | IOException | InterruptedException x) {
            x.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new ByteFIFOTest();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}

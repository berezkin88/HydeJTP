package Part2.Ch18;

public class ObjectFIFOTest {
    private static void fullCheck(ObjectFIFO fifo) {
        try {
//            sync'd to allow messages to print while condition is still true
            synchronized (fifo) {
                while (true) {
                    fifo.waitUntilFull();
                    print("FULL");
                    fifo.waitWhileFull();
                    print("NO LONGER FULL");
                }
            }
        } catch (InterruptedException x) {
            return;
        }
    }

    private static void emptyCheck(ObjectFIFO fifo) {
        try {
//            sync'd to allow messages to print while condition is still true
            synchronized (fifo) {
                while (true) {
                    fifo.waitUntilEmpty();
                    print("EMPTY");
                    fifo.waitWhileEmpty();
                    print("NO LONGER EMPTY");
                }
            }
        } catch (InterruptedException s) {
            return;
        }
    }

    private static void consumer(ObjectFIFO fifo) {
        try {
            print("just entered consumer()");

            for (int i = 0; i < 3; i++) {
                synchronized (fifo) {
                    Object obj = fifo.remove();
                    print("DATA-OUT - did remove(), obj=" + obj);
                }
                Thread.sleep(3000);
            }

            synchronized (fifo) {
                boolean resultOfWait = fifo.waitUntilEmpty(500);
                print("did waitUntilEmpty(500), resultOfWait=" + resultOfWait + ", getSize()="
                        + fifo.getSize());
            }

            for (int i = 0; i < 3; i++) {
                synchronized (fifo) {
                    Object[] list = fifo.removeAll();
                    print("did removeAll(), list.length=" + list.length);

                    for (int j = 0; j < list.length; j++) {
                        print("DATA-OUT - list[" + j + "]=" + list[j]);
                    }
                }
                Thread.sleep(100);
            }

            for (int i = 0; i < 3; i++) {
                synchronized (fifo) {
                    Object[] list = fifo.removeAtLeastOne();
                    print("did removeAtLeastOne(), list.length=" + list.length);

                    for (int j = 0, k = list.length; j < k; j++) {
                        print("DATA-OUT - list[" + j + "]=" + list[j]);
                    }
                }
                Thread.sleep(1000);
            }

            while (!fifo.isEmpty()) {
                synchronized (fifo) {
                    Object obj = fifo.remove();
                    print("DATA-OUT - did remove(), obj=" + obj);
                }
                Thread.sleep(1000);
            }

            print("leaving consumer()");
        } catch (InterruptedException ix) {
            return;
        }
    }

    private static void producer(ObjectFIFO fifo) {
        try {
            print("just entered producer()");
            int count = 0;

            Object obj0 = count;
            count++;
            synchronized (fifo) {
                fifo.add(obj0);
                print("DATA-IN - did add(), obj0=" + obj0);

                boolean resultOfWait = fifo.waitUntilEmpty(500);
                print("did waitUntilEmpty(500), resultOfWait=" + resultOfWait + ", getSize()="
                        + fifo.getSize());
            }

            for (int i = 0; i < 10; i++) {
                Object obj = count;
                count++;
                synchronized (fifo) {
                    fifo.add(obj);
                    print("DATA-IN - did add(), obj=" + obj);
                }
                Thread.sleep(1000);
            }

            Thread.sleep(2000);

            Object obj = count;
            count++;
            synchronized (fifo) {
                fifo.add(obj);
                print("DATA-IN - did add(), obj=" + obj);
            }
            Thread.sleep(500);

            Integer[] list1 = new Integer[3];
            for (int i = 0; i < list1.length; i++) {
                list1[i] = count;
                count++;
            }

            synchronized (fifo) {
                fifo.addEach(list1);
                print("did addEach(), list1.length=" + list1.length);
            }

            Integer[] list2 = new Integer[8];
            for (int i = 0; i < list1.length; i++) {
                list2[i] = count;
                count++;
            }

            synchronized (fifo) {
                fifo.addEach(list2);
                print("did addEach(), list21.length=" + list2.length);
            }

            synchronized (fifo) {
                fifo.waitUntilEmpty();
                print("fifo.isEmpty()=" + fifo.isEmpty());
            }

            print("leaving producer()");
        } catch (InterruptedException x) {
            return;
        }
    }

    private static void print(String s) {
        System.out.println(Thread.currentThread().getName() + ": " + s);
    }

    public static void main(String[] args) {
        final ObjectFIFO fifo = new ObjectFIFO(5);

        Runnable fullCheckRunnable = () -> {
            fullCheck(fifo);
        };

        Thread fullCheckThread = new Thread(fullCheckRunnable, "fchk");
        fullCheckThread.setPriority(9);
        fullCheckThread.setDaemon(true); // die automatically
        fullCheckThread.start();

        Runnable emptyCheckRunnable = () -> {
            emptyCheck(fifo);
        };

        Thread emptyCheckThread = new Thread(emptyCheckRunnable, "cons");
        emptyCheckThread.setPriority(8);
        emptyCheckThread.setDaemon(true); // die automatically
        emptyCheckThread.start();

        Runnable consumerRunnable = () -> {
            consumer(fifo);
        };

        Thread consumerThread = new Thread(consumerRunnable, "prod");
        consumerThread.setPriority(7);
        consumerThread.start();

        Runnable producerRunnable = () -> {
            producer(fifo);
        };

        Thread producerThread = new Thread(producerRunnable, "echk");
        producerThread.setPriority(6);
        producerThread.start();
    }
}

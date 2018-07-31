package Part1.Ch8;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EarlyNotify {
    private List <String> list;

    public EarlyNotify() {
        list = Collections.synchronizedList(new LinkedList<>());
    }

    public String removeItem() throws InterruptedException {
        print("in removeItem() - entering");

        synchronized (list) {
            if (list.isEmpty()) { //dangerous to use 'if'
                print("in removeItem() - about to wait()");
                list.wait();
                print("in removeItem() - done with wait()");
            }

//            extract the new first item
            String item = list.remove(0);

            print("in removeItem() - leaving");
            return item;
        } // sync
    }

    public void addItem(String item) {
        print("in addItem() - entering");

        synchronized (list) {
//            there'll always be room to add to this list because it expends as needed
            list.add(item);
            print("in addItem() - just added: '" + item + "'");

//            after adding, notify any and all waiting threads that the list has changed
            list.notifyAll();
            print("in addItem() - just notified");
        } // sync

        print("in addItem() - leaving");
    }

    private static void print(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }

    public static void main(String[] args) {
        final EarlyNotify en = new EarlyNotify();

        Runnable runA = () -> {
            try {
                String item = en.removeItem();
                print("in run() - returned: '" + item + "'");
            } catch (InterruptedException x) {
                print("interrupted");
            } catch (Exception e) {
                print("threw an Exception!!!\n" + e);
            }
        };

        Runnable runB = () -> {
            en.addItem("Hello!");
        };

        try {
            Thread threadA1 = new Thread(runA, "threadA1");
            threadA1.start();

            Thread.sleep(500);

//            start a *second* thread trying to remove
            Thread threadA2 = new Thread(runA, "threadA2");
            threadA2.start();

            Thread.sleep(500);

            Thread threadB = new Thread(runB, "threadB");
            threadB.start();

            Thread.sleep(10000);

            threadA1.interrupt();
            threadA2.interrupt();
        } catch (InterruptedException x) {
            // ignore
        }
    }
}

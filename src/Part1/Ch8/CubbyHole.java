package Part1.Ch8;

public class CubbyHole {
    private Object slot;

    public CubbyHole() {
        slot = null; // null indicate empty
    }

    public synchronized void putIn(Object obj) throws InterruptedException {
        print("in putIn() - entering");

        while (slot != null) {
            print("in putIn() - occupied, about to wait()");
            wait(); // wait while slot is occupied
            print("in putIn() - notified, back from wait()");
        }

        slot = obj; //put obj into slot
        print("in putIn() - filled slot, about to notifyAll()");
        notifyAll(); // signal that slot has been filled

        print("in putIn() - leaving");
    }

    public synchronized Object takeOut() throws InterruptedException{
        print("in takeOut() - entering");

        while (slot == null) {
            print("in takeOut() - empty, about to wait");
            wait(); // wait while slot is empty
            print("in takeOut() - notified, back from wait");
        }

        Object obj = slot;
        slot = null; //mark slot as empty
        print("in takeOut() - emptied slot, about to notifyAll()");
        notifyAll(); //signal that slot is empty

        print("in takeOut() - leaving");
        return obj;
    }

    private static void print(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }
}

package Part1.Ch7;

public class CleanRead extends Object {
    private String fname;
    private String lname;

    public synchronized String getNames() {
        return lname + ", " + fname;
    }

    public synchronized void setNames (String firstName, String lastName) {
        print("entering setNames()");
        fname = firstName;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException x) { /**/ }

        lname = lastName;

        print("leaving setNames - " + lname + ", " + fname);
    }

    public static void print(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }

    public static void main(String[] args) {
        final CleanRead cr = new CleanRead();
        cr.setNames("George", "Washington");

        Runnable runA = () -> {
            cr.setNames("Abe", "Lincoln");
        };

        Thread threadA = new Thread(runA, "threadA");
        threadA.start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException x) { /**/ }

        Runnable runB = () -> {
            print("getNames()=" + cr.getNames());
        };

        Thread threadB = new Thread(runB, "threadB");
        threadB.start();
    }
}

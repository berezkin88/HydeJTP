package Part1.Ch8;

public class CubbyHoleMain {
    private static void print(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }

    public static void main(String[] args) {
        final CubbyHole ch = new CubbyHole();

        Runnable runA = () -> {
            try {
                String str;
                Thread.sleep(500);

                str = "multithreaded";
                ch.putIn(str);
                print("in run() - just put in: '" + str + "'");

                str = "programming";
                ch.putIn(str);
                print("in run() - just put in: '" + str + "'");

                str = "with Java";
                ch.putIn(str);
                print("in run() - just put in: '" + str + "'");
            } catch (InterruptedException x) {
                x.printStackTrace();
            }
        };

        Runnable runB = () -> {
            try {
                Object obj;

                obj = ch.takeOut();
                print("in run() - just took out: '" + obj + "'");

                Thread.sleep(500);

                obj = ch.takeOut();
                print("in run() - just took out: '" + obj + "'");

                obj = ch.takeOut();
                print("in run() - just took out: '" + obj + "'");
            } catch (InterruptedException x) {
                x.printStackTrace();
            }
        };

        Thread threadA = new Thread(runA, "threadA");
        threadA.start();

        Thread threadB = new Thread(runB, "threadB");
        threadB.start();
    }
}

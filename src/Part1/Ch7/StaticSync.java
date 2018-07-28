package Part1.Ch7;

public class StaticSync extends Object {
    private static int nextSerialNum = 10001;

    public synchronized static int getNextSerialNum() {
        int sn = nextSerialNum;

//        Simulate a delay that is possible if the thread scheduler chooses to swap this thread
//        off the processor at this point. The delay is exaggerated for demonstration purposes
        try {
            Thread.sleep(1000);
        } catch (InterruptedException x) { /* */ }

        nextSerialNum++;
        return sn;
    }

    public static void print(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }

    public static void main(String[] args) {
        try {
            Runnable r = () -> {
                print("getSerialNumber()=" + getNextSerialNum());
            };

            Thread threadA = new Thread(r, "threadA");
            threadA.start();

            Thread.sleep(1500);

            Thread threadB = new Thread(r, "threadB");
            threadB.start();

            Thread.sleep(500);

            Thread threadC = new Thread(r, "threadC");
            threadC.start();

            Thread.sleep(2500);

            Thread threadD = new Thread(r, "threadD");
            threadD.start();
        } catch (InterruptedException x) { /**/ }
    }
}

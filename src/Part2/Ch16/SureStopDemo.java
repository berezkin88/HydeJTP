package Part2.Ch16;

public class SureStopDemo {
    private static Thread launch(final String name, long lifetime) {

        final int loopCount = (int) (lifetime / 1000);

        Runnable r = () -> {
            try {
                for (int i = 0; i < loopCount; i++) {
                    Thread.sleep(1000);
                    System.out.println(" -> Running - " + name);
                }
            } catch (InterruptedException e) { /**/ }
        };

        Thread t = new Thread(r);
        t.setName(name);
        t.start();

        return t;
    }

    public static void main(String[] args) {
        Thread t0 = launch("T0", 1000);
        Thread t1 = launch("T1", 5000);
        Thread t2 = launch("T2", 15000);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) { /**/ }

        SureStopVerbose.ensureStop(t0, 9000);
        SureStopVerbose.ensureStop(t1, 10000);
        SureStopVerbose.ensureStop(t2, 12000);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) { /**/ }

        Thread t3 = launch("T3", 15000);
        SureStopVerbose.ensureStop(t3, 5000);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) { /**/ }

        Thread t4 = launch("T4", 15000);
        SureStopVerbose.ensureStop(t4, 3000);
    }
}

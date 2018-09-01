package Part2.Ch14;

public class EarlyReturn {
    private volatile int value;

    public EarlyReturn(int value) {
        this.value = value;
    }

    public synchronized void setValue(int newValue) {
        if (value != newValue) {
            value = newValue;
            notifyAll();
        }
    }

    public synchronized boolean waitUntilAtLeast(int minValue, long msTimeout) throws InterruptedException {
        System.out.println("entering waitUntilAtLeast() - value=" + value + ", minValue=" + minValue);

        if (value < minValue) {
            wait(msTimeout);
        }

        System.out.println("leaving waitUntilAtLeast() - value=" + value + ", minValue=" + minValue);

//        may have timed out, or may have met value, calc return value
        return (value >= minValue);
    }

    public static void main(String[] args) {
        try {
            final EarlyReturn er = new EarlyReturn(0);

            Runnable r = () -> {
                try {
                    Thread.sleep(1500);
                    er.setValue(2);
                    Thread.sleep(500);
                    er.setValue(3);
                    Thread.sleep(500);
                    er.setValue(4);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            };

            Thread t = new Thread(r);
            t.start();

            System.out.println("about to: waitUntilAtLeast(5, 3000)");
            long startTime = System.currentTimeMillis();
            boolean retVal = er.waitUntilAtLeast(5, 3000);
            long elapsedTime = System.currentTimeMillis() - startTime;

            System.out.println("after " + elapsedTime + " ms, retVal=" + retVal);
        } catch (InterruptedException x) {
            x.printStackTrace();
        }
    }
}

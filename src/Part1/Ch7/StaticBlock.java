package Part1.Ch7;

public class StaticBlock extends Object {
    public static synchronized void staticA() {
        System.out.println("entering staticA()");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException x) { /**/ }

        System.out.println("leaving staticA");
    }

    public static void staticB() {
        System.out.println("entering staticB()");

        synchronized (StaticBlock.class) {
            System.out.println("in staticB() - inside sync block");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException x) { /**/ }
        }

        System.out.println("leaving staticB");
    }

    public static void main(String[] args) {
        Runnable runA = StaticBlock::staticA;

        Thread threadA = new Thread(runA, "threadA");
        threadA.start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException x) { /**/ }

        Runnable runB = StaticBlock::staticB;

        Thread threadB = new Thread(runB, "threadB");
        threadB.start();
    }
}

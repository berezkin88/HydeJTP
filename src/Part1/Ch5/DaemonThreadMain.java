package Part1.Ch5;

public class DaemonThreadMain extends Object {
    public static void main(String[] args) {
        System.out.println("entering main()");

        Thread t = new Thread(new DaemonThread());
        t.setDaemon(true);
        t.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {}

        System.out.println("leaving main()");
    }
}

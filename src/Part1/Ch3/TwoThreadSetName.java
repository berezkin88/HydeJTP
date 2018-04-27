package Part1.Ch3;

public class TwoThreadSetName extends Thread {
    @Override
    public void run() {

        for (int i = 0; i < 10; i++) {
            printMsg();
        }
    }

    private void printMsg() {

//        get a reference to the thread running this
        Thread t = Thread.currentThread();
        String name = t.getName();
        System.out.println("name=" + name);
    }

    public static void main(String[] args) {
        TwoThreadSetName tt = new TwoThreadSetName();
        tt.setName("My worker thread");
        tt.start();

        for (int i = 0; i < 10; i++) {
            tt.printMsg();
        }
    }
}

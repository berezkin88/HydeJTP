package Part1.Ch3;

public class TwoThread extends Thread {

    private Thread creatorThread;

    public TwoThread () {
//        make a note of a thread that constructed me
        creatorThread = Thread.currentThread();
    }

    public void printMsg () {
        Thread t = Thread.currentThread();

//        get a reference of the thread running this
        if (t == creatorThread) {
            System.out.println("Creator Thread");
        } else if (t == this) {
            System.out.println("New thread");
        } else {
            System.out.println("Unexpected thread - Mystery!");
        }
    }

    @Override
    public void run() {

        for (int i=0; i<100; i++) {
            printMsg();
        }
    }

    public static void main(String[] args) {
        TwoThread tt = new TwoThread();
        tt.start();

        for (int i=0; i<100; i++) {
            tt.printMsg();
        }
    }
}

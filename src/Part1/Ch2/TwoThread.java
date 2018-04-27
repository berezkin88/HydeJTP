package Part1.Ch2;

public class TwoThread extends Thread {

    @Override
    public void run() {

        for (int i=0; i<100; i++) {
            System.out.println("New Thread");
        }
    }

    public static void main(String[] args) {
        TwoThread tt = new TwoThread();
//        spawning a new thread
        tt.start();

        for (int i=0; i<100; i++) {
            System.out.println("Main Thread");
        }
    }
}

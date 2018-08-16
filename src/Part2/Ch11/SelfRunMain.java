package Part2.Ch11;

public class SelfRunMain {
    public static void main(String[] args) {
        SelfRun sr = new SelfRun();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException x) { /**/ }

        sr.stopRequest();
    }
}

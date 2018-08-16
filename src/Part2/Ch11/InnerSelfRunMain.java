package Part2.Ch11;

public class InnerSelfRunMain {
    public static void main(String[] args) {
        InnerSelfRun sr = new InnerSelfRun();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException x) { /**/ }

        sr.stopRequest();
    }
}

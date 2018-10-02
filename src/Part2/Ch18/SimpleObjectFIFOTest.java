package Part2.Ch18;

public class SimpleObjectFIFOTest {
    public static void main(String[] args) {
        try {
            SimpleObjectFIFO fifo = new SimpleObjectFIFO(5);
            fifo.printState();

            fifo.add("S01");
            fifo.printState();

            fifo.add("S02");
            fifo.printState();

            fifo.add("S03");
            fifo.printState();

            Object obj = fifo.remove();
            System.out.println("just removed obj=" + obj);
            fifo.printState();

            fifo.add("S04");
            fifo.printState();

            fifo.add("S05");
            fifo.printState();

            fifo.add("S06");
            fifo.printState();
        } catch (InterruptedException c) {
            c.printStackTrace();
        }
    }
}

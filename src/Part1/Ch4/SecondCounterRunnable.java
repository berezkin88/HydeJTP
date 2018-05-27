package Part1.Ch4;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class SecondCounterRunnable extends JComponent implements Runnable {
    private volatile boolean keepRunning;
    private Font paintFont;
    private volatile String timeMsg;
    private volatile int arcLen;

    public SecondCounterRunnable() {
        paintFont = new Font("SansSerif", Font.BOLD, 14);
        timeMsg = "Never started";
        arcLen = 0;
    }

    @Override
    public void run() {
        runClock();
    }

    public void runClock() {

        DecimalFormat fmt = new DecimalFormat("0.000");
        long normalSleepTime = 100;

        int counter = 0;
        keepRunning = true;

        while (keepRunning) {
            try {
                Thread.sleep(normalSleepTime);
            } catch (InterruptedException e) {
//                ignore
            }

            counter++;
            double counterCecs = counter/10.0;

            timeMsg = fmt.format(counterCecs);

            arcLen = (((int) counterCecs) % 60) * 360 / 60;
            repaint();
        }
    }

    public void stopClock() {
        keepRunning = false;
    }

    @Override
    public void paint(Graphics g) {

        g.setColor(Color.BLACK);
        g.setFont(paintFont);
        g.drawString(timeMsg, 0, 15);

        g.fillOval(0, 20, 100, 100); // black border

        g.setColor(Color.white);
        g.fillOval(3, 23, 94, 94); // white for unused portion

        g.setColor(Color.BLUE); // blue for used portion
        g.fillArc(2, 22, 96, 90, 90, -arcLen);
    }
}

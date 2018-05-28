package Part1.Ch4;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class SecondCounter extends JComponent implements Runnable {

    private volatile boolean keepRunning;
    private Font paintFont;
    private volatile String timeMsg;
    private volatile int arcLen;

    public SecondCounter() {
        paintFont = new Font("SansSerif", Font.BOLD, 14);
        timeMsg = "never started";
        arcLen = 0;
    }

    @Override
    public void run() {
        runClock();
    }

    private void runClock() {
        DecimalFormat fmt = new DecimalFormat("0.000");
        long normalSleepTime = 100;
        long nextSleepTime = normalSleepTime;

        int counter = 0;
        long startTime = System.currentTimeMillis();
        keepRunning = true;

        while (keepRunning) {
            try {
                Thread.sleep(nextSleepTime);
            } catch (InterruptedException e) {
                // ignore
            }

            counter++;
            double counterSecs = counter / 10.0;
            double elapsedSecs = (System.currentTimeMillis() - startTime) / 1000.0;

            double diffSecs = counterSecs - elapsedSecs;

            nextSleepTime = normalSleepTime + ((long) (diffSecs * 1000.0));

            if (nextSleepTime < 0) {
                nextSleepTime = 0;
            }

            timeMsg = fmt.format(counterSecs) + " - " + fmt.format(elapsedSecs) + " = " + fmt.format(diffSecs);

            arcLen = (( (int) counterSecs) % 60) * 360 / 60;
            repaint();
        }
    }

    public void stopClock() {
        keepRunning = false;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.setFont(paintFont);
        g.drawString(timeMsg, 0, 15);

        g.fillOval(0, 20, 100, 100); //black border

        g.setColor(Color.white);
        g.fillOval(3, 23, 94, 94); //white for unused portion

        g.setColor(Color.blue); //blue for used portion
        g.fillArc(2, 22, 96, 96, 90, -arcLen);
    }
}

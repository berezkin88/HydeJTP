package Part1.Ch9;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

public class DigitalTimer extends JLabel {
    private volatile String timeText;

    private Thread internalThread;
    private volatile  boolean noStopRequested;

    public DigitalTimer() {
        setBorder(BorderFactory.createLineBorder(Color.black));
        setHorizontalAlignment(SwingConstants.RIGHT);
        setFont(new Font("SansSerif", Font.BOLD,16));
        setText("00000.0"); // use to size component
        setMaximumSize(getPreferredSize());
        setPreferredSize(getPreferredSize());
        setSize(getPreferredSize());

        timeText = "0.0";
        setText(timeText);

        noStopRequested = true;
        Runnable r = () -> {
            try {
                runWork();
            } catch (Exception x ) {
                x.printStackTrace();
            }
        };

        internalThread = new Thread(r, "DigitalTimer");
        internalThread.start();
    }

    private void runWork() {
        long startTime = System.currentTimeMillis();
        int tenths = 0;
        long normalSleepTime = 100;
        long nextSleepTime = 100;
        DecimalFormat fmt = new DecimalFormat("0.0");

        Runnable updateText = () -> setText(timeText);

        while (noStopRequested) {
            try {
                Thread.sleep(nextSleepTime);

                tenths++;

                long currTime = System.currentTimeMillis();
                long elapsedTime = currTime - startTime;

                nextSleepTime = normalSleepTime + ((tenths * 100) - elapsedTime);

                if (nextSleepTime < 0) {
                    nextSleepTime = 0;
                }

                timeText = fmt.format(elapsedTime / 1000.0);
                SwingUtilities.invokeAndWait(updateText);
            } catch (InterruptedException x) {
                // stop running
                return;
            } catch (InvocationTargetException x) {
//                if an exception was thrown inside the run() method of the updateText Runnable
                x.printStackTrace();
            }
        }
    }

    public void stopRequest() {
        noStopRequested = false;
        internalThread.interrupt();
    }

    public boolean isAlive() {
        return internalThread.isAlive();
    }

    public static void main(String[] args) {
        DigitalTimer dt = new DigitalTimer();

        JPanel p = new JPanel(new FlowLayout());
        p.add(dt);

        JFrame f = new JFrame("DigitalTimer Demo");
        f.setContentPane(p);
        f.setSize(250,100);
        f.setVisible(true);
    }
}

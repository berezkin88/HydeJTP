package Part2.Ch11;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Squish extends JComponent {
    private Image[] frameList;
    private long msPerFrame;
    private volatile int currFrame;

    private Thread internalThread;
    private volatile boolean noStopRequested;

    public Squish(int width, int height, long msPerCycle, int framesPerSec, Color fgColor) {
        setPreferredSize(new Dimension(width, height));

        int framesPerCycle = (int) ((framesPerSec * msPerCycle) / 1000);
        msPerFrame = 1000L / framesPerSec;

        frameList = buildImage(width, height, fgColor, framesPerCycle);
        currFrame = 0;

        noStopRequested = true;
        Runnable r = () -> {
            try {
                runWork();
            } catch (Exception x) {
//                in case any exception slips through
                x.printStackTrace();
            }
        };

        internalThread = new Thread(r);
        internalThread.start();
    }

    private Image[] buildImage(int width, int height, Color fgColor, int framesPerCycle) {
        BufferedImage[] im = new BufferedImage[framesPerCycle];

        for (int i = 0; i < framesPerCycle; i++) {
            im[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            double xShape = 0.0;
            double yShape = ((double) (i * height)) / (double) framesPerCycle;

            double wShape = width;
            double hShape = 2.0 * (height - yShape);
            Ellipse2D shape = new Ellipse2D.Double(xShape, yShape, wShape, hShape);

            Graphics2D g2 = im[i].createGraphics();
            g2.setColor(fgColor);
            g2.fill(shape);
            g2.dispose();
        }

        return im;
    }

    private void runWork() {
        while (noStopRequested) {
            currFrame = (currFrame + 1) % frameList.length;
            repaint();

            try {
                Thread.sleep(msPerFrame);
            } catch (InterruptedException x) {
//                reassert interrupt
                Thread.currentThread().interrupt();
//                continue on as if sleep completed normally
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

    public void paint(Graphics g) {
        g.drawImage(frameList[currFrame], 0, 0, this);
    }
}

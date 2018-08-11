package Part1.Ch9;

import javax.swing.*;
import java.awt.*;

public class CompMover {
    private Component comp;
    private int initX;
    private int initY;
    private int offsetX;
    private int offsetY;
    private boolean firstTime;
    private Runnable updatePositionRun;

    private Thread internalThread;
    private volatile boolean noStopRequested;

    public CompMover(Component comp, int initX, int initY, int offsetX, int offsetY) {
        this.comp = comp;
        this.initX = initX;
        this.initY = initY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        firstTime = true;

        updatePositionRun = this::updatePosition;

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

    private void runWork() {
        while (noStopRequested) {
            try {
                Thread.sleep(200);
                SwingUtilities.invokeAndWait(updatePositionRun);
            } catch (Exception x) {
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

    private void updatePosition() {
//        should only be called by the *event* thread

        if (!comp.isVisible()) {
            return;
        }

        Component parent = comp.getParent();
        if (parent == null) {
            return;
        }

        Dimension parentSize = parent.getSize();
        if ((parentSize == null) && (parentSize.width < 1) && (parentSize.height < 1)) {
            return;
        }

        int newX = 0;
        int newY = 0;

        if (firstTime) {
            firstTime = false;
            newX = initX;
            newY = initY;
        } else {
            Point loc = comp.getLocation();
            newX = loc.x + offsetX;
            newY = loc.y + offsetY;
        }

        newX = newX % parentSize.width;
        newY = newY % parentSize.height;

        if (newX < 0) {
//            wrap around other side
            newX += parentSize.width;
        }

        if (newY < 0) {
//            wrap around other side
            newY += parentSize.height;
        }

        comp.setLocation(newX, newY);
        parent.repaint();
    }

    public static void main(String[] args) {
        Component[] comp = new Component[6];

        comp[0] = new ScrollText("Scrolling Text");
        comp[1] = new ScrollText("Java Threads");
        comp[2] = new SlideShow();
        comp[3] = new SlideShow();
        comp[4] = new DigitalTimer();
        comp[5] = new DigitalTimer();

        JPanel p = new JPanel();
        p.setLayout(null); //no layout manager

        for (Component aComp : comp) {
            p.add(aComp);

            int x = (int) (300 * Math.random());
            int y = (int) (200 * Math.random());
            int xOff = 2 - (int) (5 * Math.random());
            int yOff = 2 - (int) (5 * Math.random());

            new CompMover(aComp, x, y, xOff, yOff);
        }

        JFrame f = new JFrame("CompMover Demo");
        f.setContentPane(p);
        f.setSize(400, 300);
        f.setVisible(true);
    }
}

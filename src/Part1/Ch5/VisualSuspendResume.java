package Part1.Ch5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VisualSuspendResume extends JPanel implements Runnable {

    private static final String[] symbolList = { "¦", "/", "-", "\\", "¦", "/", "-", "\\"};
    private Thread runThread;
    private JTextField symbolITF;

    public VisualSuspendResume() {
        symbolITF = new JTextField();
        symbolITF.setEditable(false);
        symbolITF.setFont(new Font("Monospaced", Font.BOLD, 26));
        symbolITF.setHorizontalAlignment(JTextField.CENTER);

        final JButton suspendB = new JButton("Suspend");
        final JButton resumeB = new JButton("Resume");

        suspendB.addActionListener(e -> {
            suspendNow();
        });

        resumeB.addActionListener(e -> {
            resumeNow();
        });

        JPanel innerStackP = new JPanel();
        innerStackP.setLayout(new GridLayout(0, 1, 3, 3));
        innerStackP.add(symbolITF);
        innerStackP.add(suspendB);
        innerStackP.add(resumeB);

        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.add(innerStackP);
    }

    private void resumeNow() {
        if (runThread != null) runThread.resume();
    }

    private void suspendNow() {
        if (runThread != null) runThread.suspend();
    }

    @Override
    public void run() {
        try {
//            Store this for the suspendNow() and resumeNow() methods to use
            runThread = Thread.currentThread();
            int count = 0;

            while (true) {
//                each time through, show the next symbol
                symbolITF.setText(symbolList[ count % symbolList.length]);
                Thread.sleep(200);
                count++;
            }
        } catch (InterruptedException e) {
//            ignore
        } finally {
//            The thread is about to die, make sure the reference to it is also lost
            runThread = null;
        }
    }

    public static void main(String[] args) {
        VisualSuspendResume vsr = new VisualSuspendResume();
        Thread t = new Thread(vsr);
        t.start();

        JFrame f = new JFrame(" Visual Suspend Resume");
        f.setContentPane(vsr);
        f.setSize(320, 200);
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}

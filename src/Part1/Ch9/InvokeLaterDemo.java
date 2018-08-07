package Part1.Ch9;

import javax.swing.*;
import java.awt.*;

public class InvokeLaterDemo {
    private static void print(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }

    public static void main(String[] args) {
        final JLabel label = new JLabel("----");

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(label);

        JFrame f = new JFrame("InvokeAndWaitDemo");
        f.setContentPane(panel);
        f.setSize(300, 100);
        f.setVisible(true);

        try {
            print("sleeping for 3 seconds");
            Thread.sleep(3000);
        } catch (InterruptedException x) {
            print("Interrupted while sleeping");
        }

        print("creating code block for event thread");
        Runnable setTextRun = () -> {
            try {
                Thread.sleep(100); // for emphasis
                print("about to do setText()");
                label.setText("New text!");
            } catch (Exception x) {
                x.printStackTrace();
            }
        };

        print("about to involeLater()");
        SwingUtilities.invokeLater(setTextRun);
        print("back form invokeLater()");
    }
}

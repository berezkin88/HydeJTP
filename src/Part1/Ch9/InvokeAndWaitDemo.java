package Part1.Ch9;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class InvokeAndWaitDemo {
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

            print("creating code block for event thread");
            Runnable setTextRun = () -> {
                print("about to do setText()");
                label.setText("New text!");
            };

            print("about to invokeAndWait()");
            SwingUtilities.invokeAndWait(setTextRun);
            print("back from invokeAndWait()");
        } catch (InterruptedException | InvocationTargetException x) {
            x.printStackTrace();
        }
    }
}

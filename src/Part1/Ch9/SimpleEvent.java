package Part1.Ch9;

import javax.swing.*;
import java.awt.*;

public class SimpleEvent {
    private static void print(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }

    public static void main(String[] args) {
        final JLabel label = new JLabel("----");
        JButton button = new JButton("Click here");

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(button);
        panel.add(label);

        button.addActionListener(e -> {
            print("in actionPerformer");
            label.setText("CLICKED!");
        } );

        JFrame f = new JFrame("SimpleEvent");
        f.setContentPane(panel);
        f.setSize(300,100);
        f.setVisible(true);
    }
}

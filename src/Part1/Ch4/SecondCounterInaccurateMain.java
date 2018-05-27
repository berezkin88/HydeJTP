package Part1.Ch4;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SecondCounterInaccurateMain extends JPanel {
    private SecondCounterInaccurate sc;
    private JButton startB;
    private JButton stopB;

    public SecondCounterInaccurateMain() {
        sc = new SecondCounterInaccurate();
        startB = new JButton("Start");
        stopB = new JButton("Stop");

        stopB.setEnabled(false); //begin with this disabled

        startB.addActionListener((actionEvent) -> {
//            disable to stop more "start" requests
            startB.setEnabled(false);

//            run the counter. watch out, trouble here
            Thread counterThread = new Thread(sc, "SecondCounter");
            counterThread.start();

            stopB.setEnabled(true);
            stopB.requestFocus();
        });

        stopB.addActionListener((actionEvent) -> {
            stopB.setEnabled(false);
            sc.stopClock();
            startB.setEnabled(true);
            startB.requestFocus();
        });

        JPanel innerButtonP = new JPanel();
        innerButtonP.setLayout(new GridLayout(0,1,0,3));
        innerButtonP.add(startB);
        innerButtonP.add(stopB);

        JPanel buttonP = new JPanel();
        buttonP.setLayout(new BorderLayout());
        buttonP.add(innerButtonP, BorderLayout.NORTH);

        this.setLayout(new BorderLayout(10,10));
        this.setBorder(new EmptyBorder(20,20,20,20));
        this.add(buttonP, BorderLayout.WEST);
        this.add(sc, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SecondCounterInaccurateMain scm = new SecondCounterInaccurateMain();

        JFrame f = new JFrame("Second Counter Inaccurate");
        f.setContentPane(scm);
        f.setSize(320,200);
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}

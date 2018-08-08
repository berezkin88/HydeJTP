package Part1.Ch9;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BalanceLookup extends JPanel {
    private JTextField acctTF;
    private JTextField pinTF;
    private JButton searchB;
    private JButton cancelB;
    private JLabel balanceL;

    private volatile Thread lookupThread;

    public BalanceLookup() {
        buildGUI();
        hookupEvents();
    }

    private void buildGUI() {
        JLabel acctL = new JLabel("Account Number:");
        JLabel pinL = new JLabel("PIN:");
        acctTF = new JTextField(12);
        pinTF = new JTextField(4);

        JPanel dataEntryP = new JPanel();
        dataEntryP.setLayout(new FlowLayout(FlowLayout.CENTER));
        dataEntryP.add(acctL);
        dataEntryP.add(acctTF);
        dataEntryP.add(pinL);
        dataEntryP.add(pinTF);

        searchB = new JButton("Search");
        cancelB = new JButton("Cancel Search");
        cancelB.setEnabled(false);

        JPanel innerButtonP = new JPanel();
        innerButtonP.setLayout(new GridLayout(1, -1, 5, 5));
        innerButtonP.add(searchB);
        innerButtonP.add(cancelB);

        JPanel buttonP = new JPanel();
        buttonP.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonP.add(innerButtonP);

        JLabel balancePrefixL = new JLabel("Account Balance:");
        balanceL = new JLabel("BALANCE UNKNOWN");

        JPanel balanceP = new JPanel();
        balanceP.setLayout(new FlowLayout(FlowLayout.CENTER));
        balanceP.add(balancePrefixL);
        balanceP.add(balanceL);

        JPanel northP = new JPanel();
        northP.setLayout(new GridLayout(-1, 1, 5, 5));
        northP.add(dataEntryP);
        northP.add(buttonP);
        northP.add(balanceP);

        setLayout(new BorderLayout());
        add(northP, BorderLayout.NORTH);
    }

    private void hookupEvents() {
        searchB.addActionListener(e -> search());
        cancelB.addActionListener(e -> cancelSearch());
    }

    private void search() {
//        better be called by event thread!
        searchB.setEnabled(false);
        cancelB.setEnabled(true);
        balanceL.setText("SEARCHING...");

//        get a snapshot of this info in case it changes
        String acct = acctTF.getText();
        String pin = pinTF.getText();

        lookupAsync(acct, pin);
    }

    private void lookupAsync(String acct, String pin) {
//        called by event thread, but can be safely called by any thread
        final String acctNum = acct;
        final String pinNum = pin;

        Runnable lookupRun = () -> {
            String bal = lookupBalance(acctNum, pinNum);
            setBalanceSafely(bal);
        };

        lookupThread = new Thread(lookupRun, "lookupThread");
        lookupThread.start();
    }

    private String lookupBalance(String acct, String pin) {
        //        called by lookupThread, but can be safely called by any thread
        try {
//            simulate a lengthy search that takes 5 seconds to communicate over the network
            Thread.sleep(5000);

//            result "retrieved", return it
            return "1,234.56";
        } catch (InterruptedException x) {
            return "SEARCH CANCELLED";
        }
    }

    private void setBalanceSafely(String bal) {
        //        called by lookupThread, but can be safely called by any thread
        final String newBalance = bal;

        Runnable r = () -> {
            try {
                setBalance(newBalance);
            } catch (Exception x) {
                x.printStackTrace();
            }
        };

        SwingUtilities.invokeLater(r);
    }

    private void setBalance(String bal) {
//        better be called by event thread!
        ensureEventThread();

        balanceL.setText(bal);
        cancelB.setEnabled(false);
        searchB.setEnabled(true);
    }

    private void cancelSearch() {
        //        better be called by event thread!
        ensureEventThread();

        cancelB.setEnabled(false); //prevent additional requests

        if (lookupThread != null) {
            lookupThread.interrupt();
        }
    }

    private void ensureEventThread() {
//        throws an exception if not invoked by the event thread
        if (SwingUtilities.isEventDispatchThread()) {
            return;
        }

        throw new RuntimeException("only the event thread should invoke this method");
    }

    public static void main(String[] args) {
        BalanceLookup bl = new BalanceLookup();

        JFrame f = new JFrame("Balance Lookup");
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        f.setContentPane(bl);
        f.setSize(400, 150);
        f.setVisible(true);
    }
}

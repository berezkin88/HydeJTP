package Part1.Ch9;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BalanceLookupCantCancel extends JPanel {
    private JTextField acctTF;
    private JTextField pinTF;
    private JButton searchB;
    private JButton cancelB;
    private JLabel balanceL;

    public BalanceLookupCantCancel() {
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
        northP.setLayout(new GridLayout( -1, 1, 5, 5));
        northP.add(dataEntryP);
        northP.add(buttonP);
        northP.add(balanceP);

        setLayout(new BorderLayout());
        add(northP, BorderLayout.NORTH);
    }

    private void hookupEvents() {
        searchB.addActionListener( e -> search());
        cancelB.addActionListener( e -> cancelSearch());
    }

    private void search() {
//        better be called by event thread!
        searchB.setEnabled(false);
        cancelB.setEnabled(true);
        balanceL.setText("SEARCHING...");

//        get a snapshot of this info in case it changes
        String acct = acctTF.getText();
        String pin = pinTF.getText();

        String bal = lookupBalance(acct, pin);
        setBalance(bal);
    }

    private String lookupBalance(String acct, String pin) {
        try {
//            simulate a lengthy search that takes 5 seconds to communicate over the network
            Thread.sleep(5000);

//            result "retrieved", return it
            return "1,234.56";
        } catch (InterruptedException x) {
            return "SEARCH CANCELLED";
        }
    }

    private void setBalance(String bal) {
//        better be called by event thread!
        balanceL.setText(bal);
        cancelB.setEnabled(false);
        searchB.setEnabled(true);
    }

    private void cancelSearch() {
        System.out.println("in cancelSearch()");
//        Here's where the code to cancel would go if this could ever be called!
    }

    public static void main(String[] args) {
        BalanceLookupCantCancel bl = new BalanceLookupCantCancel();

        JFrame f = new JFrame("Balance Lookup - Can't Cancel");
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

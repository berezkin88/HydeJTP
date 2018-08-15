package Part1.Ch10;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.lang.reflect.InvocationTargetException;

public class ThreadViewerTableModel extends AbstractTableModel {
    private Object dataLock;
    private int rowCount;
    private Object[][] cellData;
    private Object[][] pendingCellData;

    //    the column information remains constant
    private final int columnCount;
    private final String[] columnName;
    private final Class[] columnClass;

    //    self-running object control variables
    private Thread internalThread;
    private volatile boolean noStopRequested;

    public ThreadViewerTableModel() {
        rowCount = 0;
        cellData = new Object[0][0];

//        JTable uses this information for the column headers
        String[] names = {
                "Priority", "Alive", "Daemon", "Interrupted", "ThreadGroup", "Thread Name"
        };
        columnName = names;

//        JTable uses this information for cell rendering
        Class[] classes = {
                Integer.class, Boolean.class, Boolean.class, Boolean.class, String.class, String.class
        };
        columnClass = classes;

        columnCount = columnName.length;

//        used to control concurrent access
        dataLock = new Object();

        noStopRequested = true;
        Runnable r = () -> {
            try {
                runWork();
            } catch (Exception x) {
//                in case any exception slips through
                x.printStackTrace();
            }
        };

        internalThread = new Thread(r, "ThreadViewer");
        internalThread.setPriority(Thread.MAX_PRIORITY - 2);
        internalThread.setDaemon(true);
        internalThread.start();
    }

    private void runWork() {
//        the run() method of transferPending is called by the event handling thread for safe concurrency
        Runnable transferPending = () -> {
            transferPendingCellData();

//            method of AbstractTableModel that causes the table to be updated
            fireTableDataChanged();
        };

        while (noStopRequested) {
            try {
                createPendingCellData();
                SwingUtilities.invokeAndWait(transferPending);
                Thread.sleep(5000);
            } catch (InvocationTargetException x) {
                x.printStackTrace();
                stopRequest();
            } catch (InterruptedException x) {
                Thread.currentThread().interrupt();
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

    private void createPendingCellData() {
//        this method is called by the internal thread
        Thread[] thread = findAllThreads();
        Object[][] cell = new Object[thread.length][columnCount];

        for (int i = 0; i < thread.length; i++) {
            Thread t = thread[i];
            Object[] rowCell = cell[i];

            rowCell[0] = t.getPriority();
            rowCell[1] = t.isAlive();
            rowCell[2] = t.isDaemon();
            rowCell[3] = t.isInterrupted();
            rowCell[4] = t.getThreadGroup().getName();
            rowCell[5] = t.getName();
        }

        synchronized (dataLock) {
            pendingCellData = cell;
        }
    }

    private void transferPendingCellData() {
//        this method is called by the event thread
        synchronized (dataLock) {
            cellData = pendingCellData;
            rowCount = cellData.length;
        }
    }

    @Override
    public int getRowCount() {
//        this method is called by the event thread
        return rowCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        //        this method is called by the event thread
        return cellData[rowIndex][columnIndex];
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    public Class getColumnClass(int columnIdx) {
        return columnClass[columnIdx];
    }

    public String getColumnName (int columnIdx) {
        return columnName[columnIdx];
    }

    private Thread[] findAllThreads() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();

        ThreadGroup topGroup = group;

//        traverse the ThreadGroup tree to the top
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }

//        create a destination array that is about twice as big as needed to be very confident that none are clipped
        int estimateSize = topGroup.activeCount() * 2;
        Thread[] slackList = new Thread[estimateSize];

//        load the thread references info the oversized array. The actual number of threads loaded is returned
        int actualSize = topGroup.enumerate(slackList);

//        copy into a list that is the exact size
        Thread[] list = new Thread[actualSize];
        System.arraycopy(slackList, 0, list, 0, actualSize);

        return list;
    }
}
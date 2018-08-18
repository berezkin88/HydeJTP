package Part2.Ch12;

import java.beans.ExceptionListener;

public class ExceptionCallbackMain implements ExceptionListener {
    private int exceptionCount;

    public ExceptionCallbackMain() {
        exceptionCount = 0;
    }

    @Override
    public void exceptionThrown(Exception e) {
        exceptionCount++;
        System.err.println("EXCEPTION #" + exceptionCount + ", source" + e.getClass().getName());
        e.printStackTrace();
    }

    public static void main(String[] args) {
        ExceptionListener xListener = new ExceptionCallbackMain();
        ExceptionCallback ec = new ExceptionCallback(xListener);
    }
}

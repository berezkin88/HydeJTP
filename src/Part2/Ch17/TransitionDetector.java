package Part2.Ch17;

public class TransitionDetector {
    private boolean value;
    private Object valueLock;
    private Object falseToTrueLock;
    private Object trueToFalseLock;

    public TransitionDetector(boolean initialValue) {
        value = initialValue;
        valueLock = new Object();
        falseToTrueLock = new Object();
        trueToFalseLock = new Object();
    }

    public void setValue(boolean newValue) {
        synchronized (valueLock) {
            if (newValue != value) {
                value = newValue;

                if (value) {
                    notifyFalseToTrueWaiters();
                } else {
                    notifyTrueToFalseWaiters();
                }
            }
        }
    }

    public void pulseValue() {
//        sync on valueLock to be sure that no other threads get into setValue() between these two
//        setValue() calls
        synchronized (valueLock) {
            setValue(!value);
            setValue(!value);
        }
    }

    public boolean isTrue() {
        synchronized (valueLock) {
            return value;
        }
    }

    public void waitForFalseToTrueTransition() throws InterruptedException {
        synchronized (falseToTrueLock) {
            falseToTrueLock.wait();
        }
    }

    private void notifyTrueToFalseWaiters() {
        synchronized (trueToFalseLock) {
            trueToFalseLock.notifyAll();
        }
    }

    private void notifyFalseToTrueWaiters() {
        synchronized (falseToTrueLock) {
            falseToTrueLock.notifyAll();
        }
    }

    public void waitForTrueToFalseTransition() throws InterruptedException{
        synchronized (trueToFalseLock) {
            trueToFalseLock.wait();
        }
    }

    public String toString() {
        return String.valueOf(isTrue());
    }
}

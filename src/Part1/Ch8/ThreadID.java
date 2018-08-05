package Part1.Ch8;

public class ThreadID extends ThreadLocal<Integer>{
    private int nextID;

    public ThreadID() {
        nextID = 10001;
    }

    private synchronized Integer getNewID() {
        Integer id = nextID;
        nextID++;
        return id;
    }

//    override ThreadLocal's version
    @Override
    protected Integer initialValue() {
        print("in initialValue()");
        return getNewID();
    }

    public int getThreadID() {
//        call get() in ThreadLocal to get the calling thread's unique ID
        return get();
    }

    private static void print(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }
}

package Part1.Ch8;

public class InheritableThreadID {
    public final static int UNIQUE = 101;
    public final static int INHERIT = 102;
    public final static int SUFFIX = 103;

    private ThreadLocal threadLocal;
    private int nextID;

    public InheritableThreadID(int type) {
        nextID = 201;

        switch (type) {
            case UNIQUE:
                //                    override from ThreadLocal
                threadLocal = ThreadLocal.withInitial(() -> {
                    print("in initialValue()");
                    return getNewID();
                });
                break;

            case INHERIT:
                threadLocal = new InheritableThreadLocal() {
                    //                    override from ThreadLocal
                    protected Object initialValue() {
                        print("in initialValue()");
                        return getNewID();
                    }
                };
                break;
            case SUFFIX:
                threadLocal = new InheritableThreadLocal() {
                    //                    override from ThreadLocal
                    protected Object initialValue() {
                        print("in initialValue()");
                        return getNewID();
                    }

                    //                    override form InheritableThreadLocal
                    protected Object childValue(Object parentValue) {
                        print("in childValue() - " + "parentValue=" + parentValue);

                        return parentValue + "-CH";
                    }
                };
                break;
            default:
                break;
        }
    }

    private synchronized String getNewID() {
        String id = "ID" + nextID;
        nextID++;
        return id;
    }

    public String getID() {
        return (String) threadLocal.get();
    }

    private static void print(String s) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + s);
    }

    public static Runnable createTarget(InheritableThreadID id) {
        final InheritableThreadID var = id;

        return () -> {
            print("var.getID()=" + var.getID());
            print("var.getID()=" + var.getID());
            print("var.getID()=" + var.getID());

            Runnable childRun = () -> {
                print("var.getID()=" + var.getID());
                print("var.getID()=" + var.getID());
                print("var.getID()=" + var.getID());
            };

            Thread parentT = Thread.currentThread();
            String parentName = parentT.getName();
            print("creating a child thread of " + parentName);

            Thread childT = new Thread(childRun, parentName + "-child");
            childT.start();
        };
    }

    public static void main(String[] args) {
        try {
            System.out.println("======= ThreadLocal =======");
            InheritableThreadID varA = new InheritableThreadID(UNIQUE);

            Runnable targetA = createTarget(varA);
            Thread threadA = new Thread(targetA, "threadA");
            threadA.start();

            Thread.sleep(2500);
            System.out.println("\n======= InheritableThreadLocal =======");

            InheritableThreadID varB = new InheritableThreadID(INHERIT);

            Runnable targetB = createTarget(varB);
            Thread threadB = new Thread(targetB, "threadB");
            threadB.start();

            Thread.sleep(2500);
            System.out.println("\n======= InheritableThreadLocal - custom childValue =======");

            InheritableThreadID varC = new InheritableThreadID(SUFFIX);

            Runnable targetC = createTarget(varC);
            Thread threadC = new Thread(targetC, "threadC");
            threadC.start();
        } catch (InterruptedException x) { /**/ }
    }
}

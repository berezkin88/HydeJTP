package Part2.Ch18;

public class ByteFIFO {
    private byte[] queue;
    private int capacity;
    private int size;
    private int head;
    private int tail;

    public ByteFIFO(int bufferSize) {
        capacity = (bufferSize > 0) ? bufferSize : 1; // at least 1
        queue = new byte[capacity];
        head = 0;
        tail = 0;
        size = 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public synchronized boolean isFull() {
        return (size == capacity);
    }

    public synchronized void add(byte b) throws InterruptedException {
        waitWhileFull();

        queue[head] = b;
        head = (head + 1) % capacity;
        size++;

        notifyAll(); // let any waiting threads know about change
    }

    public synchronized void add(byte[] list) throws InterruptedException {
//        for efficiency, the bytes are copied in blocks instead of one at a time. As space becomes available,
//        more bytes are copied until all of them have been added

        int ptr = 0;

        while (ptr < list.length) {
//            if full, the lock will be released to allow another thread to come in and remove bytes
            waitWhileFull();

            int space = capacity - size;
            int distToEnd = capacity - head;
            int blockLen = Math.min(space, distToEnd);

            int bytesRemaining = list.length - ptr;
            int copyLen = Math.min(blockLen, bytesRemaining);

            System.arraycopy(list, ptr, queue, head, copyLen);
            head = (head + copyLen) % capacity;
            size += copyLen;
            ptr += copyLen;

//            keep the lock, but let any waiting threads know that something was changed
            notifyAll();
        }
    }

    public synchronized byte remove() throws InterruptedException {
        waitWhileEmpty();

        byte b = queue[tail];
        tail = (tail + 1) % capacity;
        size--;

        notifyAll(); //let any waiting threads know about change

        return b;
    }

    public synchronized byte[] removeAll() {
//        for efficiency, the bytes are copied in blocks instead of one at a time.

        if (isEmpty()) {
//            nothing to remove, return a zero-length array and do not bother with notification
//            since nothing was removed
            return new byte[0];
        }

//        based on the current size
        byte[] list = new byte[size];

//        copy in the block from the tail to the end
        int distToEnd = capacity - tail;
        int copyLen = Math.min(size, distToEnd);
        System.arraycopy(queue, tail, list, 0, copyLen);

//        if data wraps around, copy the remaining data from the front of the array
        if (size > copyLen) {
            System.arraycopy(queue, 0, list, copyLen, size - copyLen);
        }

        tail = (tail + size) % capacity;
        size = 0; // everything has been removed

//        signal any and all waiting threads that something has changed
        notifyAll();

        return list;
    }

    public synchronized byte[] removeAtLeastOne() throws InterruptedException {

        waitWhileEmpty(); // wait for at least one to be in FIFO
        return removeAll();
    }

    public synchronized boolean waitUntilEmpty(long msTimeout) throws InterruptedException {

        if (msTimeout == 0L) {
            waitUntilEmpty(); // use other method
            return true;
        }

//        wait only for the specified amount of time
        long endTime = System.currentTimeMillis() + msTimeout;
        long msRemaining = msTimeout;

        while (!isEmpty() && (msRemaining > 0L)) {
            wait(msRemaining);
            msRemaining = endTime - System.currentTimeMillis();
        }

//        may have timed out, or may have met condition, calc return value
        return isEmpty();
    }

    public synchronized void waitUntilEmpty() throws InterruptedException {
        while (!isEmpty()) {
            wait();
        }
    }

    private synchronized void waitWhileEmpty() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
    }

    public synchronized void waitUntilFull() throws InterruptedException {
        while (!isFull()) {
            wait();
        }
    }

    private synchronized void waitWhileFull() throws InterruptedException {
        while (isFull()) {
            wait();
        }
    }
}

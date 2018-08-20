package Part2.Ch13;

import Part2.Ch18.ObjectFIFO;

public class ThreadPool {
    private ObjectFIFO idleWorkers;
    private ThreadPoolWorker[] workerList;

    public ThreadPool(int numberOfThreads) {
//        make sure that it's at least one
        numberOfThreads = Math.max(1, numberOfThreads);

        idleWorkers = new ObjectFIFO(numberOfThreads);
        workerList = new ThreadPoolWorker[numberOfThreads];

        for (int i = 0; i < workerList.length; i++) {
            workerList[i] = new ThreadPoolWorker(idleWorkers);
        }
    }

    public void execute(Runnable target) throws InterruptedException{
//        block (forever) until a worker is available
        ThreadPoolWorker worker = (ThreadPoolWorker) idleWorkers.remove();
        worker.process(target);
    }

    public void stopRequestIdleWorkers() {
        try {
            Object[] idle = idleWorkers.removeAll();
            for (int i = 0; i < idle.length; i++) {
                ((ThreadPoolWorker) idle[i]).stopRequest();
            }
        } catch (InterruptedException x) {
            Thread.currentThread().interrupt(); // re-assert
        }
    }

    public void stopRequestAllWorkers() {
//        stop the idle one's first productive
        stopRequestIdleWorkers();

//        give the idle workers a quick chance to die
        try {
            Thread.sleep(250);
        } catch (InterruptedException x) { /**/ }

//        step through the list of ALL workers
        for (ThreadPoolWorker worker : workerList){
            if (worker.isAlive()) {
                worker.stopRequest();
            }
        }
    }
}

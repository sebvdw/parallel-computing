package nl.saxion.parcomp.asign2;

import java.util.concurrent.locks.*;


// WHERE IVE LEFT OFF:
// CURRENTLY I just got it to work to allow vips in again after all the waiting visitors have entered
// and left. Another thing to take into account is only one VIP visiting at a time.
public class ArtFair {

    // This will run for each individual thread working.
    // An external thread will manage the syncronisation of the entire project.
    private final int maxVisitors, consecutiveVips;
    private Lock lock;
    private Condition spaceAvailable, finished, lowPriority, highPriority, vipVisiting;

    public ArtFair(int maxVisitors, int consecutiveVips) {
        this.maxVisitors = maxVisitors;
        this.consecutiveVips = consecutiveVips;

        //Establishing fair lock to ensure that the longest wait time will go in first.
        lock = new ReentrantLock(true);
        spaceAvailable = lock.newCondition();
        lowPriority = lock.newCondition();
        highPriority = lock.newCondition();
        vipVisiting = lock.newCondition();
        finished = lock.newCondition();
    }

    private int highPriorityQueue, lowPriorityQueue, nrOfVips = 0, nrOfVisitors = 0, vipCounter = 0,nrOfVisitorsVisited = 0;
    private boolean isVisiting,
    //When priority is true, the visitors have to wait.
    //When priority is false, the vips have to wait.
    isHighPriority,
    isLowPriority,
    isPriority = true;

    private boolean noSpaceAvailable() {
        return (nrOfVips+nrOfVisitors) == maxVisitors;
    };
    private boolean highPriorityWaiting() {
        return highPriorityQueue > 0;
    };
    private boolean consecutiveVips() {
        return vipCounter == 3;
    };
    private boolean vipVisiting() {
        return nrOfVips == 1;
    };
    private boolean lowPriorQueueEmpty() {return lowPriorityQueue == 0; };

    public void visitFair(String name, Boolean isVip) throws InterruptedException {
        lock.lock();
        try {

            // TRY creating two more functions and splitting VIPS and VISITORS into
            // two different queues

            // Current issue to solve:
            // - If nothing but VIPS enter. Do a check for this.
            // - If Nothing but VISITORS enter. Do a check for this.
            // - If new VISITORS enter the queue at a later time, they need to wait. How?
            // - If all visitors and vips are done, priority needs to be made true;
            // - Give the Queue solution a try, this will eliminate a lot of boolean checks within this code. Less
            // if statements yay!

            // ALL enter

            // Cheat sheet:
            // || -> one value is true, all are true
            // && -> one value is false, all are false


            if(isVip) {
                highPriorityQueue++;
            } else {
                lowPriorityQueue++;
            }

            //VISITORS WAIT
            while(!highPriorityWaiting() && isPriority) {
                lowPriority.await();
            }

            //VIPS WAITING FOR OTHER VIPS
            while(vipVisiting() && isVip) {
                vipVisiting.await();
            }

            //VIPS WAIT
            while(consecutiveVips() || !isPriority && isVip) {
                highPriority.await();
            }


            while(noSpaceAvailable()) {
                //For this to be called, above needs to be true.
                //This is almost like whether we ignore the below condition or not
                spaceAvailable.await();
            }


            System.out.println(name + " Enters Fair");
            isVisiting = true;

            if(isVip) {
                nrOfVips++;
                vipCounter++;
                highPriorityQueue--;
            } else {
                nrOfVisitors++;
                lowPriorityQueue--;
            }

            //When not visiting, wait till finished.
            while(!isVisiting) {
                finished.await();
            }
            // EXIT single

        } finally {
            lock.unlock();
        }
    }


    private void vipEnter() throws InterruptedException {
        lock.lock();
        nrOfVips++;


        //When not visiting, wait till finished.
        while(!isVisiting) {
            finished.await();
        }
        // EXIT single
        try {

        } finally {
            lock.unlock();
        }
    }

    private void visitorEnter() throws InterruptedException {
        lock.lock();
        nrOfVisitors++;


        //When not visiting, wait till finished.
        while(!isVisiting) {
            finished.await();
        }
        try {

        } finally {
            lock.unlock();
        }
    }


    public void privateExit(String name) {
        //THIS SPECIFIC exit is only for VIP's

        lock.lock();
        try {
            System.out.println(name + " Exits Fair");
            isVisiting = false;
            nrOfVips--;
            vipVisiting.signal();
            spaceAvailable.signal();
            finished.signal();

            if(consecutiveVips()) {
                System.out.println(" ========== Low Priority ========== ");
                isPriority = false;
                lowPriority.signalAll();
                vipCounter = 0;
            }

        } finally {
            lock.unlock();
        }
    }


    public void publicExit(String name) {
        //THIS SPECIFIC exit is only for VISITORS

        lock.lock();
        try {
            System.out.println(name + " Exits Fair");
            isVisiting = false;
            nrOfVisitors--;
            spaceAvailable.signal();
            finished.signal();

            if(lowPriorQueueEmpty()) {
                System.out.println(" ========== High Priority ========== ");
                isPriority = true;
                highPriority.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

}
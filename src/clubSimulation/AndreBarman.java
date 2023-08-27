package clubSimulation;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class AndreBarman extends Thread{
    
    private boolean isBarmanMoving = false;
    private GridBlock currentBlock;
    private ClubGrid workingSpace;
    private int movingSpeed;
    private PeopleLocation myLocation;
    public int stepsTaken = 0;
    private int stepsTakenByBarman = -1;
    public static ClubGrid club;
    public static AtomicBoolean paused = new AtomicBoolean(false);
    public static CountDownLatch startSignal = new CountDownLatch(1);

    AndreBarman(PeopleLocation loc, int speed, ClubGrid club, AtomicBoolean paused, CountDownLatch startSignal) throws InterruptedException{
        this.myLocation = loc;
        this.movingSpeed = speed;
        this.club = club;
        this.paused = paused;
        this.startSignal = startSignal;

    }
    
    public synchronized boolean patronInCounter() throws InterruptedException{
        
        if (club.whichBlock(currentBlock.getX(), club.getBar_y() + 1).occupied()){
            return true;
        }
        return false;
    }

    public void serve() throws InterruptedException{
        if (patronInCounter()){
            currentBlock = club.getBarmanCounter(myLocation);
            currentBlock.release();
        }
    }

    public void checkPause() throws InterruptedException{
        synchronized (paused){
            try{
                while (paused.get()){
                    paused.wait();
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            
        }
    }

    private synchronized void startSimulation() throws InterruptedException{
        synchronized(startSignal){
            try{
                startSignal.await();
                isBarmanMoving = true;
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void moveBarmanAcross() throws InterruptedException{
        
        synchronized ((currentBlock)){
            if (currentBlock.getX() + 1 >= club.getMaxX()){
                stepsTakenByBarman = -1;
            }
            else if (currentBlock.getX() - 1 <= -1){
                stepsTakenByBarman = 1;
            }

            if (patronInCounter()){
                Thread.sleep(1000);
            }
            currentBlock = club.servingDrinks(currentBlock, stepsTakenByBarman, 0, myLocation);
        }
    }
    
    public void run(){
        try{
            startSimulation();
            while (isBarmanMoving){
                checkPause();
                serve();
                moveBarmanAcross();
                Thread.sleep(movingSpeed);
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

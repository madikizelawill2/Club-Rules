package clubSimulation;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class AndreBarman extends Thread{
    
    private boolean isBarmanMoving = false;
    private GridBlock currentBlock;
    private GridBlock workingSpace;
    private int movingSpeed;
    private PeopleLocation myLocation;
    public int stepsTaken = 0;
    public static ClubGrid club;
    public static AtomicBoolean paused = new AtomicBoolean(false);
    public static CountDownLatch startSignal = new CountDownLatch(1);

    AndreBarman(PeopleLocation loc, int speed, ClubGrid club, AtomicBoolean paused){
        this.myLocation = loc;
        this.movingSpeed = speed;
        this.club = club;
        this.paused = paused;

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

    public synchronized void checkPause() throws InterruptedException{
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
        
    }



    // public void move() throws InterruptedException{
    //     if (isBarmanMoving){
    //         if (currentBlock.getX() == workingSpace.getX() && currentBlock.getY() == workingSpace.getY()){
    //             isBarmanMoving = false;
    //             serve();
    //         }
    //         else{
    //             if (currentBlock.getX() < workingSpace.getX()){
    //                 currentBlock = club.whichBlock(currentBlock.getX() + 1, currentBlock.getY());
    //             }
    //             else if (currentBlock.getX() > workingSpace.getX()){
    //                 currentBlock = club.whichBlock(currentBlock.getX() - 1, currentBlock.getY());
    //             }
    //             else if (currentBlock.getY() < workingSpace.getY()){
    //                 currentBlock = club.whichBlock(currentBlock.getX(), currentBlock.getY() + 1);
    //             }
    //             else if (currentBlock.getY() > workingSpace.getY()){
    //                 currentBlock = club.whichBlock(currentBlock.getX(), currentBlock.getY() - 1);
    //             }
    //             stepsTaken++;
    //         }
    //     }
    // }   

}

package clubSimulation;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for the barman's movement and serving the patrons
 * @version 1.0
 * @since 2023
 * @author Will
 */
public class AndreBarman extends Thread {
    private boolean isBarmanMoving = false;
    GridBlock currentBlock = new GridBlock();
    public PeopleLocation myLocation;
    public int barmanMovingSpeed = 0;
    public int stepsTaken = 0;
    Random rand = new Random();
    int stepsTakenByBarman = -1;
    public static ClubGrid club; 
    public static AtomicBoolean paused;
    public static CountDownLatch startSignal;

   /**
    * This constructor initialises the barman's location, speed, club, paused
    * @param loc
    * @param speed
    * @param club
    * @param paused
    * @param startSignal
    * @throws InterruptedException
    */
    AndreBarman(PeopleLocation loc, int speed, ClubGrid club, AtomicBoolean paused,
            CountDownLatch startSignal)
            throws InterruptedException {
        this.myLocation = loc;
        this.barmanMovingSpeed = speed;
        this.club = club;
        this.paused = paused;
        this.startSignal = startSignal;
    }

    /**
     * This method checks if there is a patron in the block
     * @return
     * @throws InterruptedException
     */
    public synchronized boolean personToBeServed() throws InterruptedException {

        if (club.whichBlock(currentBlock.getX(), club.getBar_y()).occupied()) {
            return true;
        }
        return false;

    }

    /**
     * This method is responsible for the barman's movement
     * @throws InterruptedException
     */
    public void servingPeople() throws InterruptedException {
        currentBlock = club.startBar(myLocation);
        sleep(barmanMovingSpeed / 2);
    }

    /**
     * This function is responsible for the movement of the barman
     */
    private void moveBarmanAcross() throws InterruptedException {
        synchronized (currentBlock) {
            if (currentBlock.getX() + 1 >= club.getMaxX()) {
                stepsTakenByBarman = -1;
            } else if (currentBlock.getX() - 1 <= -1) {
                stepsTakenByBarman = 1;
            }
            if (personToBeServed()) {
                Thread.sleep(1000);
            }
            currentBlock = club.serveDrinks(currentBlock, stepsTakenByBarman, 0, myLocation);
            sleep(barmanMovingSpeed);
        }
    }

    /**
     * This function checks if the game is paused
     */
    private void checkGamePaused() {
        synchronized (paused) {
            try {
                while (paused.get()) {
                    paused.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startSimulation() {
        synchronized (startSignal) {
            try {
                startSignal.await();
                isBarmanMoving = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void run() {
        startSimulation();
        checkGamePaused();
        try {
            servingPeople();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (isBarmanMoving) {
            try {
                checkGamePaused();
                moveBarmanAcross();
                if (club.getCounter().getLeft() == myLocation.getID()) {
                    isBarmanMoving = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Barman has finished serving");

    }
}

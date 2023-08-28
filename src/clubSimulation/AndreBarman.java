package clubSimulation;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for the barman's movement
 * @version 1.0
 * @since 2023
 * @authour Will
 */
public class AndreBarman extends Thread {

    /*
     * isBarmanMoving - is the barman moving?
     * currentBlock - the current block
     * myLocation - the location of the barman
     * barmanMovingSpeed - the speed of the barman
     * stepsTaken - the number of steps taken by the barman
     * rand - random number generator
     * stepsTakenByBarman - the number of steps taken by the barman
     * club - the club
     * paused - is the game paused?
     * startSignal - the start signal
     */
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
    */
    AndreBarman(PeopleLocation loc, int speed, ClubGrid club, AtomicBoolean paused,CountDownLatch startSignal)throws InterruptedException {
        this.myLocation = loc;
        this.barmanMovingSpeed = speed;
        this.club = club;
        this.paused = paused;
        this.startSignal = startSignal;
    }

   
    /**
     * This method is responsible for checking if the person is in the room and needs to be served
     * @return whether the person is in the room or not
     * @throws InterruptedException
     * @param in - status of the persons position
     */
    public synchronized boolean personToBeServed() throws InterruptedException {

        if (club.whichBlock(currentBlock.getX(), club.getBar_y()).occupied()) {return true;}
        return false;
    }

    
    /**
     * This method is responsible for serving people
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

    /**
     * This function starts the simulation
     */
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

    /**
     * This function runs the barman thread
     */
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

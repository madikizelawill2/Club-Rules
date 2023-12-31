package clubSimulation;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for the clubgoer's movement
 * @version 1.0
 * @since 2023
 * @authour Will
 */
public class Clubgoer extends Thread {
	/*
	 * ID - each person has an ID
	 * myColor - colour of the person
	 * inRoom - are they in the club?
	 * arrived - have they arrived at the club?
	 * location - which GridBlock are they on?
	 * club - the club
	 * paused - is the game paused?
	 * startSignal - the start signal
	 * currentBlock - the current block
	 * rand - random number generator
	 * clubGoerMovingSpeed - the speed of the clubgoer
	 * myLocation - the location of the clubgoer
	 * inRoom - is the clubgoer in the room?
	 * thirsty - is the clubgoer thirsty?
	 * wantToLeave - does the clubgoer want to leave?
	 * ID - the ID of the clubgoer
	 */

	public static ClubGrid club;
	public static AtomicBoolean paused;
	public static CountDownLatch startSignal;
	GridBlock currentBlock;
	private Random rand;
	private int clubGoerMovingSpeed;
	private PeopleLocation myLocation;
	private boolean inRoom;
	private boolean thirsty;
	private boolean wantToLeave;
	private int ID;

	/**
	 * This constructor initialises the clubgoer's location, speed, club, paused
	 * @param ID
	 * @param loc
	 * @param speed
	 * @throws InterruptedException
	 */
	Clubgoer(int ID, PeopleLocation loc, int speed) {
		this.ID = ID;
		clubGoerMovingSpeed = speed; 
		this.myLocation = loc; 
		inRoom = false; 
		thirsty = true; 
		wantToLeave = false;
		rand = new Random();
	}

	/**
	 * These methods is responsible for the clubgoer's movement
	 * @return 
	 * @throws InterruptedException
	 */
	public boolean inRoom() {return inRoom;}
	public int getX() {return currentBlock.getX();}
	public int getY() {return currentBlock.getY();}
	public int getSpeed() {return clubGoerMovingSpeed;}
	public PeopleLocation getClubgoerLocation() {return myLocation;}

	/**
	 * This method is responsible for checking if the game is paused
	 * @throws InterruptedException
	 */
	private void checkPause() {
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
	 * This method is responsible for starting the game (simulation)
	 * @throws InterruptedException
	 */
	private void startSimulation() {
		synchronized (startSignal) {
			try {
				startSignal.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * This method is responsible for checking if the barman is in the block
	 * @throws InterruptedException
	 */
	public synchronized boolean BarmanInBlock() throws InterruptedException {

		if (club.whichBlock(currentBlock.getX(), club.getBar_y() + 1).occupied()) {
			return true;
		}
		return false;

	}
	
	/**
	 * This method is responsible for getting the drink
	 * @throws InterruptedException
	 */
	private void getDrink() throws InterruptedException {
		synchronized (this) {
			while (!BarmanInBlock()) {
				this.wait(1000);
			}
			this.wait(3000);
		}
		thirsty = false;
		System.out.println(
				"Thread " + this.ID + " got drink at bar position: " + currentBlock.getX() + " " + currentBlock.getY());
		sleep(clubGoerMovingSpeed * 2);
	}

	// --------------------------------------------------------
	// DO NOT CHANGE THE CODE BELOW HERE - it is not necessary
	// clubgoer enters club
	public void enterClub() throws InterruptedException {

		currentBlock = club.enterClub(myLocation); // enter through entrance
		inRoom = true;
		System.out.println(
				"Thread " + this.ID + " entered club at position: " + currentBlock.getX() + " " + currentBlock.getY());
		sleep(clubGoerMovingSpeed / 2); // wait a bit at door
	}

	// go to bar
	private void headToBar() throws InterruptedException {
		int x_mv = rand.nextInt(3) - 1; // -1,0 or 1
		int y_mv = Integer.signum(club.getBar_y() - currentBlock.getY());// -1,0 or 1
		currentBlock = club.move(currentBlock, x_mv, y_mv, myLocation); // head toward bar
		System.out.println("Thread " + this.ID + " moved toward bar to position: " + currentBlock.getX() + " "
				+ currentBlock.getY());
		sleep(clubGoerMovingSpeed / 2); // wait a bit
	}

	// go head towards exit
	private void headTowardsExit() throws InterruptedException {
		GridBlock exit = club.getExit();
		int x_mv = Integer.signum(exit.getX() - currentBlock.getX());// x_mv is -1,0 or 1
		int y_mv = Integer.signum(exit.getY() - currentBlock.getY());// -1,0 or 1
		currentBlock = club.move(currentBlock, x_mv, y_mv, myLocation);
		System.out.println(
				"Thread " + this.ID + " moved to towards exit: " + currentBlock.getX() + " " + currentBlock.getY());
		sleep(clubGoerMovingSpeed); // wait a bit
	}

	// dancing in the club
	private void dance() throws InterruptedException {
		for (int i = 0; i < 3; i++) { // sequence of 3

			int x_mv = rand.nextInt(3) - 1; // -1,0 or 1
			int y_mv = Integer.signum(1 - x_mv);

			for (int j = 0; j < 4; j++) { // do four fast dance steps
				currentBlock = club.move(currentBlock, x_mv, y_mv, myLocation);
				sleep(clubGoerMovingSpeed / 5);
				x_mv *= -1;
				y_mv *= -1;
			}
			checkPause();
		}
	}

	// wandering about in the club
	private void wander() throws InterruptedException {
		for (int i = 0; i < 2; i++) { //// wander for two steps
			int x_mv = rand.nextInt(3) - 1; // -1,0 or 1
			int y_mv = Integer.signum(-rand.nextInt(4) + 1); // -1,0 or 1 (more likely to head away from bar)
			currentBlock = club.move(currentBlock, x_mv, y_mv, myLocation);
			sleep(clubGoerMovingSpeed);
		}
	}

	// leave club
	private void leave() throws InterruptedException {
		club.leaveClub(currentBlock, myLocation);
		inRoom = false;
	}

	public void run() {
		try {
			startSimulation();
			checkPause();
			sleep(clubGoerMovingSpeed * (rand.nextInt(100) + 1)); // arriving takes a while
			checkPause();
			myLocation.setArrived();
			System.out.println("Thread " + this.ID + " arrived at club"); // output for checking
			checkPause(); // check whether have been asked to pause
			enterClub();

			while (inRoom) {
				checkPause(); // check every step
				if ((!thirsty) && (!wantToLeave)) {
					if (rand.nextInt(100) > 95)
						thirsty = true; // thirsty every now and then
					else if (rand.nextInt(100) > 98)
						wantToLeave = true; // at some point want to leave
				}

				if (wantToLeave) { // leaving overides thirsty
					sleep(clubGoerMovingSpeed / 5); // wait a bit
					if (currentBlock.isExit()) {
						leave();
						System.out.println("Thread " + this.ID + " left club");
					} else {
						System.out.println("Thread " + this.ID + " going to exit");
						headTowardsExit();
					}
				} else if (thirsty) {
					sleep(clubGoerMovingSpeed / 5); // wait a bit
					if (currentBlock.isBar()) {
						getDrink();
						System.out.println("Thread " + this.ID + " got drink ");
					} else {
						System.out.println("Thread " + this.ID + " going to getDrink ");
						headToBar();
					}
				} else {
					if (currentBlock.isDanceFloor()) {
						dance();
						System.out.println("Thread " + this.ID + " dancing ");
					}
					wander();
					// System.out.println("Thread " + this.ID + " wandering about ");
				}

			}
			System.out.println("Thread " + this.ID + " is done");

		} catch (

		InterruptedException e1) { // do nothing
		}
	}

}

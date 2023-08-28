package clubSimulation;

import java.util.concurrent.atomic.AtomicBoolean;
/*
 * This class is responsible for the location of the people in the club
 * Only one thread has access to it at a time to the grid block
 * @version 1.0
 * @since 2023
 * @authour Will
 */
public class ClubGrid {

	/*
	 * Blocks - the grid of blocks
	 * x - the x coordinate of the block
	 * y - the y coordinate of the block
	 * bar_y - the y coordinate of the bar
	 * exit - the exit block
	 * entrance - the entrance block
	 * barmanCounter - the barman block
	 * minX - the minimum x coordinate
	 * minY - the minimum y coordinate
	 * counter - the people counter
	 */
	private GridBlock[][] Blocks;
	private final int x;
	private final int y;
	public final int bar_y;
	private GridBlock exit;
	private GridBlock entrance; 
	private GridBlock barmanCounter;
	private final static int minX = 5;
	private final static int minY = 5;
	private PeopleCounter counter;

	/*
	 * This constructor initialises the grid block
	 * @param x - x coordinate of the block
	 * @param y - y coordinate of the block
	 * @param exitBlocks - the exit block
	 * @param c - the people counter
	 * @throws InterruptedException
	 */
	ClubGrid(int x, int y, int[] exitBlocks, PeopleCounter c) throws InterruptedException {
		if (x < minX) x = minX;
		if (y < minY) y = minY; 
		this.x = x;
		this.y = y;
		this.bar_y = y - 3;
		Blocks = new GridBlock[x][y];
		this.initGrid(exitBlocks);
		entrance = Blocks[getMaxX() / 2][0];
		barmanCounter = Blocks[getMaxX() - 1][getBar_y() + 1];
		counter = c;
	}

	/*
	 * This method initialises the grid block
	 * @param exitBlocks - the exit block
	 * @throws InterruptedException
	 */
	private void initGrid(int[] exitBlocks) throws InterruptedException {
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				boolean exit_block = false;
				boolean bar = false;
				boolean dance_block = false;
				if ((i == exitBlocks[0]) && (j == exitBlocks[1])) {
					exit_block = true;
				} else if (j >= (y - 3))
					bar = true;
				else if ((i > x / 2) && (j > 3) && (j < (y - 5)))
					dance_block = true;
				Blocks[i][j] = new GridBlock(i, j, exit_block, bar, dance_block);
				if (exit_block) {
					this.exit = Blocks[i][j];
				}
			}
		}
	}
	
	/*
	 * This method is responsible for getting the maximum x coordinates of the block
	 * @return the maximum x coordinate of the block
	 */
	public synchronized int getMaxX() {
		return x;
	}

	/*
	 * This method is responsible for getting the maximum y coordinates of the block
	 * @return the maximum y coordinate of the block
	 */
	public synchronized int getMaxY() {
		return y;
	}

	/*
	 * This method is responsible for getting the entrance block
	 * @return the entrance block
	 */
	public synchronized GridBlock whereEntrance() {
		return entrance;
	}

	/*
	 * This method is responsible for checking if the block is in the grid
	 * @param i - the x coordinate of the block
	 * @param j - the y coordinate of the block
	 * @return true if the block is in the grid
	 */
	public synchronized boolean inGrid(int i, int j) {
		if ((i >= x) || (j >= y) || (i < 0) || (j < 0))
			return false;
		return true;
	}

	/*
	 * This method is responsible for checking if the block is in the patron area
	 * @param i - the x coordinate of the block
	 * @param j - the y coordinate of the block
	 * @return true if the block is in the patron area
	 */
	public synchronized boolean inPatronArea(int i, int j) {
		if ((i >= x) || (j > bar_y) || (i < 0) || (j < 0))
			return false;
		return true;
	}

	/*
	 * This method is responsible for ensuring person enters the club
	 * @param i - the x coordinate of the block
	 * @param j - the y coordinate of the block
	 * @return true if the block is in the dance floor
	 */
	public GridBlock enterClub(PeopleLocation myLocation) throws InterruptedException {

		synchronized (counter) {
			counter.personArrived();
			try {
				while (counter.overCapacity())
					counter.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (entrance) {
				while (!entrance.get(myLocation.getID())) {
					entrance.wait();
				}
			}
			counter.personEntered();
			myLocation.setLocation(entrance);
			myLocation.setInRoom(true);

		}
		return entrance;

	}

	/*
	 * This method is responsible for checking if the block is in the bar
	 * @param myLocation - the location of the person
	 * @return the barman block
	 */
	public synchronized GridBlock startBar(PeopleLocation myLocation) throws InterruptedException {
		myLocation.setLocation(barmanCounter);
		myLocation.setInRoom(true);
		return barmanCounter;

	}

	/*
	 * This method is responsible for moving the person
	 * @param currentBlock - the current block
	 * @param step_x - the x coordinate of the block
	 * @param step_y - the y coordinate of the block
	 * @param myLocation - the location of the person
	 * @return the new block
	 */
	public GridBlock move(GridBlock currentBlock, int step_x, int step_y, PeopleLocation myLocation)
			throws InterruptedException { // try to move in
		synchronized (entrance) {
			if (!entrance.occupied()) {
				entrance.notifyAll();
			}
		}

		int c_x = currentBlock.getX();
		int c_y = currentBlock.getY();

		int new_x = c_x + step_x; // new block x coordinates
		int new_y = c_y + step_y; // new block y coordinates

		// restrict i an j to grid
		if (!inPatronArea(new_x, new_y)) {
			// Invalid move to outside - ignore
			return currentBlock;
		}

		if ((new_x == currentBlock.getX()) && (new_y == currentBlock.getY())) // not actually moving
			return currentBlock;

		GridBlock newBlock = Blocks[new_x][new_y];

		if (!newBlock.get(myLocation.getID()))
			return currentBlock; // stay where you are

		currentBlock.release(); // must release current block
		myLocation.setLocation(newBlock);
		return newBlock;
	}

	/*
	 * This function is responsible for the movement of the barman
	 * @param currentBlock
	 * @param step_x
	 * @param step_y
	 * @param myLocation
	 * @return the new block
	 */
	public GridBlock serveDrinks(GridBlock currentBlock, int step_x, int step_y, PeopleLocation myLocation)
			throws InterruptedException {

		int c_x = currentBlock.getX();
		int c_y = currentBlock.getY();

		int new_x = c_x + step_x; // new block x coordinates
		int new_y = c_y + step_y; // new block y coordinates
		if (new_x <= -1 || new_x >= 20) {
			return currentBlock;
		}

		if ((new_x == currentBlock.getX()) && (new_y == currentBlock.getY())) // not actually moving
			return currentBlock;

		GridBlock newBlock = Blocks[new_x][new_y];

		if (!newBlock.get(myLocation.getID()))
			return currentBlock; // stay where you are

		currentBlock.release(); // must release current block
		myLocation.setLocation(newBlock);
		return newBlock;
	}
	/*
	 * This method is responsible for ensuring person leaves the club
	 * @param currentBlock - the current block
	 * @param myLocation - the location of the person
	 * @return the new block
	 */
	public void leaveClub(GridBlock currentBlock, PeopleLocation myLocation) {
		synchronized (counter) {
			currentBlock.release();
			counter.personLeft(); 
			myLocation.setInRoom(false);
			counter.notifyAll();
		}

	}

	/*
	 * This method is responsible for getting the exit block
	 * @return the exit block
	 */
	public synchronized GridBlock getExit() {
		return exit;
	}

	/*
	 * This method is responsible for getting the block
	 * @param xPos - the x coordinate of the block
	 * @param yPos - the y coordinate of the block
	 * @return the block
	 */
	public GridBlock whichBlock(int xPos, int yPos) {
		if (inGrid(xPos, yPos)) {
			return Blocks[xPos][yPos];
		}
		System.out.println("block " + xPos + " " + yPos + "  not found");
		return null;
	}

	/*
	 * This method is responsible for getting the exit block
	 * @param xPos - the x coordinate of the block
	 * @param yPos - the y coordinate of the block
	 * @return the exit block
	 */
	public void setExit(GridBlock exit) {
		this.exit = exit;
	}

	/*
	 * This method is responsible for getting the x coordinate of the bar
	 * @return the x coordinate of the bar
	 */
	public int getBar_y() {
		return bar_y;
	}

	/*
	 * This method is responsible for getting the people counter
	 * @return the people counter
	 */
	public synchronized PeopleCounter getCounter() {
		return counter;
	}

	/*
	 * This method is responsible for getting the grid block
	 * @return the grid block
	 */
	public synchronized GridBlock[][] getBlocks() {
		return Blocks;
	}

}

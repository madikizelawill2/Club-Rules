package clubSimulation;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class is responsible for the location of the people in the club
 * Only one thread has access to it at a time to the grid block
 */
public class GridBlock {

	/*
	 * isOccupied - is the block occupied? 
	 * isExit - is this the exit door? isBar
	 * isBar - is it a bar block?
	 * isDance - is it the dance area?
	 * coords - the coordinate of the block.
	 */
	private int isOccupied;
	private final boolean isExit;
	private final boolean isBar;
	private final boolean isDance;
	private int[] coords;

	/*
	 * This constructor initialises the grid block
	 * @param exitBlock - is it the exit block?
	 * @param barBlock - is it the bar block?
	 * @param danceBlock - is it the dance block?
	 * @throws InterruptedException
	 */
	GridBlock(boolean exitBlock, boolean barBlock, boolean danceBlock) throws InterruptedException {
		isExit = exitBlock;
		isBar = barBlock;
		isDance = danceBlock;
		isOccupied = -1;
	}

	/*
	 * This constructor initialises the grid block
	 * @param x - x coordinate of the block
	 * @param y - y coordinate of the block
	 * @param refreshBlock - is it the refresh block?
	 * @param danceBlock - is it the dance block?
	 * @throws InterruptedException
	 */
	GridBlock(int x, int y, boolean exitBlock, boolean refreshBlock, boolean danceBlock) throws InterruptedException {
		this(exitBlock, refreshBlock, danceBlock);
		coords = new int[] { x, y };
	}
	
	/*
	 * This constructor initialises the grid block
	 * This is a default constructor
	 * @throws InterruptedException
	 */
	GridBlock() {
		isBar = false;
		isDance = false;
		isExit = false;
		coords = new int[] { 0, 0 };
	}

	/*
	 * This method is responsible for getting the x  coordinates of the block
	 * @return the x coordinate of the block
	 */
	public int getX() {
		return coords[0];
	}
	
	/*
	 * This method is responsible for getting the y  coordinates of the block
	 * @return the y coordinate of the block
	 */
	public int getY() {
		return coords[1];
	}

	/*
	 * This method is responsible for getting the block
	 * @param threadID - the thread ID
	 * @throws InterruptedException
	 * @return true if the block is occupied by the threadID
	 * @return false if the block is not occupied by the threadID
	 */
	public synchronized boolean get(int threadID) throws InterruptedException {
		if (isOccupied == threadID)
			return true;
		if (isOccupied >= 0)
			return false;
		isOccupied = threadID;
		return true;
	}

	/*
	 * This method is responsible for releasing the block
	 */
	public synchronized void release() {
		isOccupied = -1;
	}

	/*
	 * This method is responsible for checking if the block is occupied
	 * @return true if the block is occupied
	 */
	public synchronized boolean occupied() {
		if (isOccupied == -1)
			return false;
		return true;
	}

	/*
	 * This method is responsible for checking if the block is the exit block
	 * @return true if the block is the exit block
	 */
	public synchronized boolean isExit() {
		return isExit;
	}

	/*
	 * This method is responsible for checking if the block is the bar block
	 * @return true if the block is the bar block
	 */
	public boolean isBar() {
		return isBar;
	}

	/*
	 * This method is responsible for checking if the block is the dance floor
	 * @return true if the block is the dance floor
	 */
	public boolean isDanceFloor() {
		return isDance;
	}

}
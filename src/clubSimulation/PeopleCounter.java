package clubSimulation;

import java.util.concurrent.atomic.*;

/*
 * This class is responsible for counting the number of people in the club
 * Outside the club and the number of people who have left the club
 * @version 1.0
 * @since 2023
 * @authour Will
 */

public class PeopleCounter {

	/*
	 * peopleOutSide - counter for people arrived but not yet in the building
	 * peopleInside - counter for patrons inside club
	 * peopleLeft - counter for patrons who have left the club
	 * maxPeople - maximum patrons allowed in the club at one time
	 */
	private AtomicInteger peopleOutSide;
	private AtomicInteger peopleInside;
	private AtomicInteger peopleLeft;
	private AtomicInteger maxPeople;

	/*
	 * This constructor initialises the people counters
	 * @param max - maximum number of people allowed in the club
	 */

	PeopleCounter(int max) {
		peopleOutSide = new AtomicInteger(0);
		peopleInside = new AtomicInteger(0);
		peopleLeft = new AtomicInteger(0);
		maxPeople = new AtomicInteger(max);
	}

	/*
	 * This methods are responsible for getting the number of people waiting in the club
	 * @return the number of people waiting in the club
	 */
	public int getWaiting() {
		return peopleOutSide.get();
	}

	/*
	 * This methods are responsible for getting the number of people inside the club
	 * @return the number of people inside the club
	 */
	public int getInside() {
		return peopleInside.get();
	}

	/*
	 * This methods are responsible for getting the total number of people in the club
	 * @return the total number of people in the club
	 */
	public int getTotal() {
		return (peopleOutSide.get() + peopleInside.get() + peopleLeft.get());
	}

	/*
	 * This methods are responsible for getting the number of people who have left the club
	 * @return the number of people who have left the club
	 */
	public int getLeft() {
		return peopleLeft.get();
	}

	/*
	 * This methods are responsible for getting the maximum number of people allowed in the club
	 * @return the maximum number of people allowed in the club
	 */
	public int getMax() {
		return maxPeople.get();
	}

	/*
	 * This method is responsible for incrementing the number of people waiting outside the club
	 */
	public void personArrived() {
		peopleOutSide.getAndIncrement();
	}

	/*
	 * This method is responsible for decrementing the number of people waiting outside the club
	 * and incrementing the number of people inside the club
	 */
	synchronized public void personEntered() {
		peopleOutSide.getAndDecrement();
		peopleInside.getAndIncrement();
	}

	/*
	 * This method is responsible for decrementing the number of people inside the club
	 * Indicating that someone has left the club
	 */
	synchronized public void personLeft() {
		peopleInside.getAndDecrement();
		peopleLeft.getAndIncrement();

	}

	/*
	 * This method is responsible for checking if the club is over capacity
	 * @return whether the club is over capacity or not
	 */
	synchronized public boolean overCapacity() {
		if (peopleInside.get() >= maxPeople.get())
			return true;
		return false;
	}

	/*
	 * This method is responsible for resetting the people counters
	 * @return all the counters are set to 0
	 */
	synchronized public void resetScore() {
		peopleInside.set(0);
		peopleOutSide.set(0);
		peopleLeft.set(0);
	}

}

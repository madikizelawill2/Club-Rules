package clubSimulation;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.atomic.*;

/*
 * This class is responsible for the location of the people
 * this is a separate class so don't have to access thread
 * @version 1.0
 * @since 2023
 * @authour Will
 */

public class PeopleLocation { 
	
	/*
	 * ID - each person has an ID
	 * myColor - colour of the person
	 * inRoom - are they in the club?
	 * arrived - have they arrived at the club?
	 * location - which GridBlock are they on?
	 */

	private final int ID;
	private Color myColor; 
	private AtomicBoolean inRoom;
	private AtomicBoolean arrived; 
	private GridBlock location;

	/*
	 * This constructor initialises the person's location, speed, club, paused
	 * @param ID
	 */

	PeopleLocation(int ID) {
		Random rand = new Random();
		float c = rand.nextFloat(); // bit of a hack to get different colours
		myColor = new Color(c, rand.nextFloat(), c); // only set at beginning by thread
		inRoom = new AtomicBoolean(false); // not in club
		arrived = new AtomicBoolean(false); // have not arrive outside
		this.ID = ID;
	}

	/*
	 * This method is responsible for checking if the person is in the room
	 * @return whether the person is in the room or not
	 * @throws InterruptedException
	 * @param in - status of the persons position
	 */
	public void setInRoom(boolean in) {
		this.inRoom.set(in);
	}

	/*
	 * This method is responsible for checking if the person has arrived
	 * @return whether the person has arrived or not
	 */
	public boolean getArrived() {
		return arrived.get();
	}

	/*
	 * This method is responsible for setting the person's arrival
	 * @retrun arrived - status of the persons arrival set to arrived (true)
	 */
	public void setArrived() {
		this.arrived.set(true);
	}

	/*
	 * This method is responsible for setting the person's location
	 * @return location - the location of the person
	 */
	public GridBlock getLocation() {
		return location;
	}

	/*
	 * This method is responsible for setting the person's location
	 * @param location - the location of the person
	 * @retrun newLocation of the person
	 */
	public void setLocation(GridBlock location) {
		this.location = location;
	}

	/*
	 * This method is responsible for getting the persons's x coordinate of thier location
	 * @return x coordinate of the person's location
	 */
	public int getX() {
		return location.getX();
	}

	/*
	 * This method is responsible for getting the persons's y coordinate of thier location
	 * @return y coordinate of the person's location
	 */
	public int getY() {
		return location.getY();
	}

	/*
	 * This method is responsible for getting the persons's ID
	 * @return ID - the ID of the person
	 */
	public int getID() {
		return ID;
	}

	/*
	 * This method is responsible for checking if the person is in the room
	 * @return whether the person is in the room or not
	 */
	public synchronized boolean inRoom() {
		return inRoom.get();
	}

	/*
	 * This method is responsible for getting the persons's colour
	 * @return myColor - the colour of the person
	 */
	public synchronized Color getColor() {
		return myColor;
	}
	/*
	 * This method is responsible for setting the persons's colour
	 * @param myColor - the colour of the person
	 */
	public synchronized void setColor(Color myColor) {
		this.myColor = myColor;
	}
}
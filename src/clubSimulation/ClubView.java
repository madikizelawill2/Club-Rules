package clubSimulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import javax.swing.JPanel;

/*
 * This class is responsible for displaying the club
 * @version 1.0
 * @since 2023
 * @authour Will
 */
public class ClubView extends JPanel implements Runnable {

	/*
	 * patronLocations - array of the locations of the patrons barmanLocation
	 * barmanLocation - barmans location in the club or the counter? 
	 * noPatrons - total number of patrons in the simulation
	 * exits - where is the exit?
	 * wIncr - width of each block
	 * hIncr - height of each block
	 * maxY - maximum Y for the grid
	 * grid - the shared grid
	 */
	private PeopleLocation[] patronLocations;
	private PeopleLocation barmanLocation;
	private int noPatrons; 
	private int[] exits;
	private int wIncr;
	private int hIncr;
	private int maxY;
	private int maxX;
	ClubGrid grid;

	/*
	 * This constructor initialises the club view
	 * @param custs - array of the locations of the patrons
	 * @param barmanLocation - barmans location in the club or the counter?
	 * @param grid - the shared grid
	 * @param exits - where is the exit?
	 * @throws InterruptedException
	 */
	ClubView(PeopleLocation[] custs, PeopleLocation barmanLocation, ClubGrid grid, int[] exits) throws InterruptedException { // constructor
		this.patronLocations = custs;
		noPatrons = custs.length;
		this.barmanLocation = barmanLocation;
		this.grid = grid;
		this.exits = exits;
		this.maxY = grid.getMaxY();
		this.maxX = grid.getMaxX();
		int width = getWidth();
		int height = getHeight();
		wIncr = width / (maxX + 2);
		hIncr = height / (maxY + 2);
	}

	/*
	 * This method is responsible for painting the club
	 * @param g - graphics
	 */
	public void paintComponent(Graphics g) {

		int width = getWidth();
		int height = getHeight();
		wIncr = width / (maxX + 2);
		hIncr = height / (maxY + 2);
		g.clearRect(0, 0, width, height);
		g.setColor(Color.black);

		/*
		 * draw the entrance block
		 */
		g.setColor(Color.gray);
		GridBlock entrance = grid.whereEntrance();
		g.fillRect(entrance.getX() * wIncr + wIncr, entrance.getY() * hIncr, wIncr, hIncr);
		g.drawString("Enter", entrance.getX() * wIncr + wIncr, entrance.getY() * hIncr + hIncr);

		/*
		 * draw the exit block
		 */
		g.setFont(new Font("Helvetica", Font.BOLD, hIncr / 2));
		g.setColor(Color.pink);
		g.fillRect(exits[0] * wIncr + wIncr, exits[1] * hIncr, wIncr, hIncr);
		g.setColor(Color.red);
		g.drawString("Exit", exits[0] * wIncr + wIncr, exits[1] * hIncr + hIncr);

		/*
		 * draw the bar block
		 */
		g.setColor(Color.lightGray);
		g.fillRect(wIncr, (grid.bar_y) * hIncr, wIncr * (maxX), hIncr * 1);
		g.setColor(Color.black);
		g.setFont(new Font("Helvetica", Font.BOLD, hIncr));
		g.drawString("Bar", (maxX - 1) * wIncr / 2, grid.bar_y * hIncr + hIncr);

		/*
		 * draw a dance floor block
		 */
		g.setColor(Color.yellow);
		g.fillRect(wIncr * maxX / 2, (3) * hIncr, wIncr * (maxX / 2), hIncr * (maxY - 8));
		g.setColor(Color.black);

		/*
		 * draw the grid lines
		 */
		for (int i = 1; i <= (maxX + 1); i++)
			g.drawLine(i * wIncr, 0, i * wIncr, maxY * hIncr); 
		for (int i = 0; i <= maxY; i++) 
			g.drawLine(wIncr, i * hIncr, (maxX + 1) * wIncr, i * hIncr);

		/* 
		 * draw the ovals representing people in middle of grid block
		 * and their ID number in the top left corner of the block
		 */
		int x, y;
		g.setFont(new Font("Helvetica", Font.BOLD, hIncr / 2));

		/*
		 * draw the barman oval
		 */
		if (barmanLocation.inRoom()) {
			g.setColor(Color.BLACK);
			x = (barmanLocation.getX() + 1) * wIncr;
			y = barmanLocation.getY() * hIncr;
			g.fillOval(x + wIncr / 4, y + hIncr / 4, wIncr / 2, hIncr / 2);
			g.drawString("barman", x + wIncr / 4, y + wIncr / 4);
		}

		/*
		 * draw the patron ovals
		 */
		for (int i = 0; i < noPatrons; i++) {
			if (patronLocations[i].inRoom()) {
				g.setColor(patronLocations[i].getColor());
				x = (patronLocations[i].getX() + 1) * wIncr;
				y = patronLocations[i].getY() * hIncr;
				g.fillOval(x + wIncr / 4, y + hIncr / 4, wIncr / 2, hIncr / 2);
				g.drawString(patronLocations[i].getID() + "", x + wIncr / 4, y + wIncr / 4);
			} else {
				// if (patronLocations[i].getArrived())
				// System.out.println("customer " + i + "waiting outside");
			}
		}
	}

	/*
	 * This method is responsible for getting the maximum Y of the grid
	 * @return maxY - the maximum Y of the grid
	 */
	public int getMaxX() {
		return maxX;
	}

	/*
	 * This method is responsible for getting the maximum Y of the grid
	 */
	public void run() {
		while (true) {
			repaint();
		}
	}

}

package clubSimulation;

import java.awt.Color;
import javax.swing.JLabel;

/*
 * This class is responsible for displaying the counters
 * @version 1.0
 * @since 2023
 * @authour Will
 */
public class CounterDisplay implements Runnable {

	/*
	 * score - the people counter
	 * waiting - number of people waiting to get in the club inside
	 * inside - number of people inside the club left 
	 * left - number of people who have left the club score
	 */

	private PeopleCounter score;
	JLabel waiting;
	JLabel inside;
	JLabel left;
	
	/*
	 * This constructor initialises the counter display
	 * @param w - number of people waiting to get in the club inside
	 * @param i - number of people inside the club left
	 * @param l - number of people who have left the club score
	 */
	CounterDisplay(JLabel w, JLabel i, JLabel l, PeopleCounter score) {
		this.waiting = w;
		this.inside = i;
		this.left = l;
		this.score = score;
	}

	/*
	 * This method is responsible for updating the counter display
	 * changes color when at limit and over limit of people inside
	 * @throws InterruptedException
	 */
	public void run() { 
		while (true) {
			if (score.getMax() < score.getInside()) {
				inside.setForeground(Color.RED);
			} else if (score.getMax() == score.getInside()) {
				inside.setForeground(Color.ORANGE);
			} else
				inside.setForeground(Color.BLACK);
			inside.setText("Inside: " + score.getInside() + "    ");
			waiting.setText("Waiting:" + score.getWaiting() + "    ");
			left.setText("Left:" + score.getLeft() + "    ");
		}
	}
}
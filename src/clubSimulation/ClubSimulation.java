package clubSimulation;

// the main class, starts all threads
import javax.swing.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * This is the main class, responsible for starting all the threads
 * @version 1.0
 * @since 2023
 * @authour Will
 */
public class ClubSimulation {

	/*
	 * noClubgoers - number of customers
	 * frameX - width of frame
	 * frameY - height of frame
	 * yLimit - how far up the screen the club is
	 * gridX - number of x grids in club
	 * gridY - number of y grids in club
	 * max - max number of customers
	 * patrons - array of customer threads
	 * peopleLocations - array of customer locations
	 * andreBarman - barman thread
	 * barmanLocation - barman location
	 * tallys - counters for number of people inside and outside club
	 * clubView - threaded panel to display terrain
	 * clubGrid - club grid
	 * counterDisplay - threaded display of counters
	 * maxWait - for the slowest customer
	 * minWait - for the fastest cutomer
	 */
	static int noClubgoers = 50;
	static int frameX = 650;
	static int frameY = 650;
	static int yLimit = 50;
	static int gridX = 20;
	static int gridY = 20;
	static int max = 20;
	static Clubgoer[] patrons;
	static PeopleLocation[] peopleLocations;
	static AndreBarman andreBarman;
	static PeopleLocation barmanLocation;
	static PeopleCounter tallys; 
	static ClubView clubView;
	static ClubGrid clubGrid;
	static CounterDisplay counterDisplay;
	private static int maxWait = 1200;
	private static int minWait = 500;

	/*
	 * This method is responsible for setting up the GUI
	 * @param frameX - width of frame
	 * @param frameY - height of frame
	 * @param exits - where is the exit?
	 * @throws InterruptedException
	 */
	public static void setupGUI(int frameX, int frameY, int[] exits) throws InterruptedException {

		/*
		 * frame - initialise the frame dimensions
		 */
		JFrame frame = new JFrame("club animation");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(frameX, frameY);

		JPanel g = new JPanel();
		g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));
		g.setSize(frameX, frameY);

		clubView = new ClubView(peopleLocations, barmanLocation, clubGrid, exits);
		clubView.setSize(frameX, frameY);
		g.add(clubView);

		
		/*
		 * Add all the counters to the panel
		 */
		JPanel txt = new JPanel();
		txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS));
		JLabel maxAllowed = new JLabel("Max: " + tallys.getMax() + "    ");
		JLabel caught = new JLabel("Inside: " + tallys.getInside() + "    ");
		JLabel missed = new JLabel("Waiting:" + tallys.getWaiting() + "    ");
		JLabel scr = new JLabel("Left club:" + tallys.getLeft() + "    ");
		txt.add(maxAllowed);
		txt.add(caught);
		txt.add(missed);
		txt.add(scr);
		g.add(txt);
		counterDisplay = new CounterDisplay(caught, missed, scr, tallys); // thread to update score

		/*
		 * Add all the buttons to the panel
		 * including the start, pause and quit buttons
		 */
		JPanel b = new JPanel();
		b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));
		final JButton startB = new JButton("Start");

		// add the listener to the jbutton to handle the "pressed" event
		startB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Clubgoer.startSignal.countDown();
				startB.setEnabled(false);

			}
		});

		final JButton pauseB = new JButton("Pause ");
		/*
		 * This method is responsible for pausing and resuming the game
		 * @param e - action event
		 */
		pauseB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (Clubgoer.paused) {
					if (Clubgoer.paused.get() == false) {
						Clubgoer.paused.set(true);
						pauseB.setText("Resume");
					} else {
						Clubgoer.paused.set(false);
						Clubgoer.paused.notifyAll();
						pauseB.setText("Pause");
					}
				}
			}
		});

		JButton endB = new JButton("Quit");
		/*
		 * This method is responsible for quitting the game
		 * @param e - action event
		 */
		endB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		b.add(startB);
		b.add(pauseB);
		b.add(endB);

		g.add(b);

		frame.setLocationRelativeTo(null); // Center window on screen.
		frame.add(g); // add contents to window
		frame.setContentPane(g);
		frame.setVisible(true);
	}

	public static void main(String[] args) throws InterruptedException {
		Clubgoer.paused = new AtomicBoolean(false);// initialising the paused variable to false
		Clubgoer.startSignal = new CountDownLatch(1);// initialising the countdownlatch to 1

		// deal with command line arguments if provided
		if (args.length == 4) {
			noClubgoers = Integer.parseInt(args[0]); // total people to enter room
			gridX = Integer.parseInt(args[1]); // No. of X grid cells
			gridY = Integer.parseInt(args[2]); // No. of Y grid cells
			max = Integer.parseInt(args[3]); // max people allowed in club
		}

		int[] exit = { 0, (int) gridY / 2 - 1 }; 
		tallys = new PeopleCounter(max); 
		clubGrid = new ClubGrid(gridX, gridY, exit, tallys); 
		Clubgoer.club = clubGrid;
		peopleLocations = new PeopleLocation[noClubgoers];
		patrons = new Clubgoer[noClubgoers];
		int movingSpeed = (int) (Math.random() * (maxWait - minWait) + minWait);
		barmanLocation = new PeopleLocation(noClubgoers);
		andreBarman = new AndreBarman(barmanLocation, movingSpeed, clubGrid, Clubgoer.paused,Clubgoer.startSignal);

		for (int i = 0; i < noClubgoers; i++) {
			peopleLocations[i] = new PeopleLocation(i);
			movingSpeed = (int) (Math.random() * (maxWait - minWait) + minWait); // range of speeds for customers
			patrons[i] = new Clubgoer(i, peopleLocations[i], movingSpeed);
		}

		setupGUI(frameX, frameY, exit); // Start Panel thread - for drawing animation
		// start all the threads
		Thread t = new Thread(clubView);
		t.start();
		// Start counter thread - for updating counters
		Thread s = new Thread(counterDisplay);
		s.start();
		/*
		 * Start the barman thread
		 */
		andreBarman.start();
		for (int i = 0; i < noClubgoers; i++) {
			patrons[i].start();
		}

	}

}

package com.codingheaven.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class PipesHandler {

	public static final short kDISTANCE = 200; // distance between each pipe
	public static final short AMOUNT = Game.WIDTH / kDISTANCE + 1; // amount of pipes to generate in memory to keep game
																	// smooth

	private ArrayList<PipeSet> pipes; // array of pipes, array list so we can add and remove

	/**
	 * Constructor
	 */
	public PipesHandler() {
		initializeList();
	}

	/**
	 * A set of pipes, top and bottom
	 * 
	 * @author Abd-El-Aziz Zayed
	 *
	 */
	private class PipeSet {

		/*
		 * spacing between top and bottom pipe
		 */
		public static final int MAX_SPACING = 150;
		public static final int MIN_SPACING = 100;

		private Rectangle topPipe, bottomPipe; // pipes
		private int pipeWidth = 55; // width
		private boolean passedBird = false; // true if the current pipe set passed the bird
		private float velocity = -5.0f;

		/**
		 * Constructor
		 * 
		 * @param x, starting position
		 */
		public PipeSet(int x) {
			init(x);
		}

		/**
		 * @return the passedBird boolean
		 */
		public boolean isPassedBird() {
			return passedBird;
		}

		/**
		 * Create the pipe set
		 * 
		 * @param x, where to create the pipe set
		 */
		public void init(int x) {
			// initialize rectangles;
			Random random = new Random();

			// random number between min (inclusive) and max spacing
			int spacing = random.nextInt(MAX_SPACING - MIN_SPACING) + MIN_SPACING;
			int topRectHeight = random.nextInt(200) + 50; // between 50 and 250

			topPipe = new Rectangle(x, -1, pipeWidth, topRectHeight);

			int bottomRectX = topRectHeight + spacing;
			bottomPipe = new Rectangle(x, bottomRectX - 1, pipeWidth, Game.HEIGHT - bottomRectX);

			passedBird = false;
		}

		/**
		 * test if hit the bird
		 * 
		 * @param bird, bird to test on
		 * @return true if bird was hit
		 */
		public boolean hitBird(Bird bird) {
			Rectangle birdRect = bird.getBounds();

			return (topPipe.intersects(birdRect) || bottomPipe.intersects(birdRect));
		}

		/**
		 * @return x position of pipes
		 */
		public int getX() {
			return (int) topPipe.getX();
		}

		/**
		 * @return width of pipes
		 */
		public int getWidth() {
			return pipeWidth;
		}

		/**
		 * check if the pipes passed the bird
		 * 
		 * @param bird, bird to test on
		 */
		public void checkIfPassed(Bird bird) {
			if (passedBird)
				return;

			passedBird = (bird.getBounds().getX() > topPipe.getX() + pipeWidth);

			if (passedBird)
				bird.addPoint();
		}

		/**
		 * Draw pipe set
		 * 
		 * @param g, tool to draw
		 */
		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			g.setColor(Color.GREEN);
			g2d.fill(topPipe);
			g2d.fill(bottomPipe);

			g2d.setStroke(new BasicStroke(3));
			g.setColor(Color.BLACK);
			g2d.draw(topPipe);
			g2d.draw(bottomPipe);

			short h = 30;
			short l = 4;
			Rectangle topTip = new Rectangle((int) topPipe.getX() - l, (int) topPipe.getHeight() - h, pipeWidth + l * 2,
					h);
			Rectangle botTip = new Rectangle((int) bottomPipe.getX() - l, (int) bottomPipe.getY(), pipeWidth + l * 2,
					h);

			g.setColor(Color.GREEN);
			g2d.fill(topTip);
			g2d.fill(botTip);
			g.setColor(Color.BLACK);
			g2d.draw(topTip);
			g2d.draw(botTip);

		}

		/**
		 * update movement and physics, do collision test
		 */
		public void update() {
			double x, y;
			x = topPipe.getX();
			x += velocity;

			y = topPipe.getY();
			topPipe.setLocation((int) x, (int) y);

			y = bottomPipe.getY();
			bottomPipe.setLocation((int) x, (int) y);

		}
	}

	/**
	 * setup the list of initial pipe sets
	 */
	private void initializeList() {
		pipes = new ArrayList<PipeSet>();
		int x = Game.WIDTH + 200;

		for (int i = 0; i < AMOUNT; i++) {
			pipes.add(new PipeSet(x + kDISTANCE * i));
		}
	}

	/**
	 * test the collision with the bird
	 * 
	 * @param bird, bird to test collision with
	 */
	private void testCollisions(Bird bird) {

		boolean tested = true;
		int i = -1;

		while (tested && i < AMOUNT - 1) { // find the first one that hasn't past the bird
			i++;
			tested = pipes.get(i).isPassedBird();
		}

		PipeSet pipe = pipes.get(i);

		if (pipe.hitBird(bird))
			bird.dyingAnimation();

		pipe.checkIfPassed(bird);

	}

	/**
	 * draw all the pipe sets
	 * 
	 * @param g, tool to draw
	 */
	public void drawPipes(Graphics g) {

		for (PipeSet pipe : pipes) {
			pipe.draw(g);
		}

	}

	/**
	 * Update the game, the movements and test for collisions
	 * 
	 * @param bird, bird to test collisions with
	 */
	public void update(Bird bird) {
		if (bird.isDying())
			return;

		PipeSet pipe;

		for (int i = 0; i < AMOUNT; i++) {
			pipe = pipes.get(i);
			pipe.update();
		}

		pipe = pipes.get(0);
		if (pipe.getX() + pipe.getWidth() <= 0) {
			pipes.remove(0);
			int x = pipes.get(pipes.size() - 1).getX();
			pipes.add(new PipeSet(x + kDISTANCE));
		}

		testCollisions(bird);

	}

}

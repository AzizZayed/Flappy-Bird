package com.codingheaven.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class PipesHandler {

	public static final short kDISTANCE = 200;
	public static final short AMOUNT = Game.WIDTH / kDISTANCE + 1;

	private ArrayList<PipeSet> pipes;

	public PipesHandler() {
		initializeList();
	}

	private class PipeSet {

		public static final int MAX_SPACING = 150;
		public static final int MIN_SPACING = 100;

		private Rectangle topPipe, bottomPipe;
		private int pipeWidth = 55;
		private boolean passedBird = false;
		private float velocity = -5.0f;

		public PipeSet(int x) {
			init(x);
		}

		/**
		 * @return the passedBird boolean
		 */
		public boolean isPassedBird() {
			return passedBird;
		}

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

		public boolean hitBird(Bird bird) {
			Rectangle birdRect = bird.getBounds();

			return (topPipe.intersects(birdRect) || bottomPipe.intersects(birdRect));
		}

		public int getX() {
			return (int) topPipe.getX();
		}

		public int getWidth() {
			return pipeWidth;
		}

		public void checkIfPassed(Bird bird) {
			if (passedBird)
				return;

			passedBird = (bird.getBounds().getX() > topPipe.getX() + pipeWidth);

			if (passedBird)
				bird.addPoint();
		}

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

	private void initializeList() {
		pipes = new ArrayList<PipeSet>();
		int x = Game.WIDTH + 200;

		for (int i = 0; i < AMOUNT; i++) {
			pipes.add(new PipeSet(x + kDISTANCE * i));
		}
	}
	
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

	public void drawPipes(Graphics g) {

		for (PipeSet pipe : pipes) {
			pipe.draw(g);
		}

	}

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

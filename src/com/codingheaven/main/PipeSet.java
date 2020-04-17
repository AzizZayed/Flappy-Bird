package com.codingheaven.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

/**
 * A set of pipes, top and bottom
 * 
 * @author Zayed
 *
 */
public class PipeSet {

	/*
	 * spacing between top and bottom pipe
	 */
	public static final int MAX_SPACING = 150;
	public static final int MIN_SPACING = 100;

	private Rectangle topPipe, bottomPipe; // pipes
	private int pipeWidth = 55; // width
	private boolean passedBird = false; // true if the current pipe set passed the bird
	private float scrollSpeed = -5.0f; // scrolling speed

	/**
	 * Constructor
	 * 
	 * @param x      - starting position
	 * @param height - height of canvas
	 */
	public PipeSet(int x, int height) {
		// initialize rectangles;
		Random random = new Random();

		int spacing = random.nextInt(MAX_SPACING - MIN_SPACING) + MIN_SPACING;
		int topRectHeight = random.nextInt(200) + 50; // between 50 and 250

		topPipe = new Rectangle(x, -1, pipeWidth, topRectHeight);

		int bottomRectX = topRectHeight + spacing;
		bottomPipe = new Rectangle(x, bottomRectX - 1, pipeWidth, height - bottomRectX);
	}

	/**
	 * @return the passedBird boolean
	 */
	public boolean isPassedBird() {
		return passedBird;
	}

	/**
	 * test if hit the bird
	 * 
	 * @param bird - bird to test on
	 * @return true if bird was hit, false otherwise
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
	 * @param bird - bird to test on
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
	 * @param g - tool to draw
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
		Rectangle topTip = new Rectangle((int) topPipe.getX() - l, (int) topPipe.getHeight() - h, pipeWidth + l * 2, h);
		Rectangle botTip = new Rectangle((int) bottomPipe.getX() - l, (int) bottomPipe.getY(), pipeWidth + l * 2, h);

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
		x += scrollSpeed;

		y = topPipe.getY();
		topPipe.setLocation((int) x, (int) y);

		y = bottomPipe.getY();
		bottomPipe.setLocation((int) x, (int) y);

	}
}

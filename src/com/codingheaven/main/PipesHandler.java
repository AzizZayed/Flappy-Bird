package com.codingheaven.main;

import java.awt.Graphics;
import java.util.ArrayList;

/**
 * class to handle all the pipes
 * 
 * @author Zayed
 *
 */
public class PipesHandler {

	public static final int kDISTANCE = 200; // distance between each pipe
	public int nPipes; // amount of pipes to generate in memory to keep game
						// smooth
	private ArrayList<PipeSet> pipes; // array of pipes, array list so we can add and remove

	/**
	 * Constructor
	 * 
	 * @param width  - width of canvas
	 * @param height - height of canvas
	 */
	public PipesHandler(int width, int height) {
		pipes = new ArrayList<PipeSet>();
		int x = width + 200;

		nPipes = width / kDISTANCE + 1;

		for (int i = 0; i < nPipes; i++) {
			pipes.add(new PipeSet(x + kDISTANCE * i, height));
		}
	}

	/**
	 * test the collision with the bird
	 * 
	 * @param bird - bird to test collision with
	 */
	private void testCollisions(Bird bird) {

		boolean tested = true;
		int i = -1;

		while (tested && i < nPipes - 1) { // find the first one that hasn't past the bird
			i++;
			tested = pipes.get(i).isPassedBird();
		}

		PipeSet pipe = pipes.get(i);

		if (pipe.hitBird(bird))
			bird.setupDyingAnimation();

		pipe.checkIfPassed(bird);

	}

	/**
	 * draw all the pipe sets
	 * 
	 * @param g - tool to draw
	 */
	public void drawPipes(Graphics g) {
		for (PipeSet pipe : pipes) {
			pipe.draw(g);
		}
	}

	/**
	 * Update the game, the movements and test for collisions
	 * 
	 * @param bird   - bird to test collisions with
	 * @param height - height of canvas
	 */
	public void update(Bird bird, int height) {
		if (bird.isDying())
			return;

		PipeSet pipe;

		for (int i = 0; i < nPipes; i++) {
			pipe = pipes.get(i);
			pipe.update();
		}

		pipe = pipes.get(0);
		if (pipe.getX() + pipe.getWidth() <= 0) {
			pipes.remove(0);
			int x = pipes.get(pipes.size() - 1).getX();
			pipes.add(new PipeSet(x + kDISTANCE, height));
		}

		testCollisions(bird);
	}

}

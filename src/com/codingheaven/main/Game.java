package com.codingheaven.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

/**
 * main class, manages thread, drawing and updating physics/movement
 * 
 * @author Zayed
 *
 */
public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	/**
	 * Game dimensions, in pixels
	 */
	private final static int WIDTH = 350;
	private final static int HEIGHT = 500;

	public final static float kGRAVITY = -4.0f; // gravity constant

	private boolean running = false; // true if the game loop is running
	private Thread gameThread; // thread where the game is updated AND rendered (single thread game)

	private boolean gameStarted = false; // true if game has started

	// Game properties...
	private Bird bird;
	private PipesHandler pipes;

	/**
	 * Constructor
	 */
	public Game() {

		canvasSetup();
		initialize();

		newWindow();

	}

	/**
	 * Setup JFrame where the canvas will be in
	 */
	private void newWindow() {
		JFrame frame = new JFrame("Flappy Bird");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		start();
	}

	/**
	 * initialize all our game objects
	 */
	private void initialize() {
		// Initialize
		bird = new Bird(WIDTH, HEIGHT);
		pipes = new PipesHandler(WIDTH, HEIGHT);
	}

	/**
	 * restart the game
	 */
	public void restart() {
		gameStarted = false;
		initialize();
	}

	/**
	 * just to setup the canvas to our desired settings and sizes, setup events
	 */
	private void canvasSetup() {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();

				if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
					bird.jump(-5.5f * kGRAVITY); // go against gravity
					if (!gameStarted)
						gameStarted = true;
				}

			}

		});

		this.setFocusable(true);
	}

	/**
	 * Game loop
	 */
	@Override
	public void run() {
		// so you can keep your sanity, I won't explain the game loop... you're welcome
		// I do have a video on it though

		this.requestFocus();

		final double MAX_FRAMES_PER_SECOND = 60.0;
		final double MAX_UPDATES_PER_SECOND = 20.0;

		long startTime = System.nanoTime();
		final double uOptimalTime = 1000000000 / MAX_UPDATES_PER_SECOND;
		final double fOptimalTime = 1000000000 / MAX_FRAMES_PER_SECOND;
		double uDeltaTime = 0, fDeltaTime = 0;
		int frames = 0, updates = 0;
		long timer = System.currentTimeMillis();

		while (running) {

			long currentTime = System.nanoTime();
			uDeltaTime += (currentTime - startTime) / uOptimalTime;
			fDeltaTime += (currentTime - startTime) / fOptimalTime;
			startTime = currentTime;

			while (uDeltaTime >= 1) {
				update(uDeltaTime);
				updates++;
				uDeltaTime--;
			}

			if (fDeltaTime >= 1) {
				render();
				frames++;
				fDeltaTime--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {

				System.out.println("UPS: " + updates + ", FPS: " + frames);

				frames = 0;
				updates = 0;
				timer += 1000;
			}
		}

		stop();
	}

	/**
	 * start the thread and the game
	 */
	public synchronized void start() {
		gameThread = new Thread(this);
		/*
		 * since "this" is the "Game" Class you are in right now and it implements the
		 * Runnable Interface we can give it to a thread constructor. That thread will
		 * call it's "run" method which this class inherited (it's directly above)
		 */
		gameThread.start(); // start thread
		running = true;
	}

	/**
	 * Stop the thread and the game
	 */
	public void stop() {
		try {
			gameThread.join();
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * render the back and all the objects
	 */
	public void render() {
		// Initialize drawing tools first before drawing

		BufferStrategy buffer = this.getBufferStrategy(); // extract buffer so we can use them
		// a buffer is basically like a blank canvas we can draw on

		if (buffer == null) { // if it does not exist, we can't draw! So create it please
			this.createBufferStrategy(3); // Creating a Triple Buffer
			/*
			 * triple buffering basically means we have 3 different canvases this is used to
			 * improve performance but the drawbacks are the more buffers, the more memory
			 * needed so if you get like a memory error or something, put 2 instead of 3 or
			 * even 1...if you run a computer from 2002...
			 * 
			 * BufferStrategy:
			 * https://docs.oracle.com/javase/7/docs/api/java/awt/image/BufferStrategy.html
			 */

			return;
		}

		Graphics g = buffer.getDrawGraphics(); // extract drawing tool from the buffers
		/*
		 * Graphics is class used to draw rectangles, ovals and all sorts of shapes and
		 * pictures so it's a tool used to draw on a buffer
		 * 
		 * Graphics: https://docs.oracle.com/javase/7/docs/api/java/awt/Graphics.html
		 */

		// draw background
		drawBackground(g);

		// draw Game Objects here
		if (gameStarted)
			pipes.drawPipes(g);

		bird.draw(g, gameStarted, WIDTH, HEIGHT);

		// actually draw
		g.dispose();
		buffer.show();

	}

	/**
	 * Draw background
	 * 
	 * @param g - Graphics used to draw on the Canvas
	 */
	private void drawBackground(Graphics g) {
		// black background
		g.setColor(new Color(155, 201, 234));
		// g.fillRect(0, 0, WIDTH, HEIGHT);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * update settings and move all objects
	 * 
	 * @param uDeltaTime - time between updates
	 */
	public void update(double uDeltaTime) {

		// update Game Objects here
		bird.update(uDeltaTime, gameStarted);
		if (gameStarted)
			pipes.update(bird, HEIGHT);

		if (bird.hitFloor(HEIGHT))
			restart();
	}

	/**
	 * start of the program
	 */
	public static void main(String[] args) {
		new Game();
	}

}

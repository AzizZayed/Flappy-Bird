package com.codingheaven.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bird {

	private int x, y; // position in 2D space
	private int width, height; // Dimensions of the bird
	private float yVelocity; // vertical velocity
	private double rotationAngle = Math.PI / 4; // 30 degrees rotation for the bird
	private boolean dying = false; // for dying animation
	private int score = 0; // number of pipes past
	private static int record = 0;

	private final static short kANIMATION_IMAGES = 3; // number of pictures in the flying bird animation
	private short imgWidth, imgHeight; // dimensions of the bird images
	private final static short kSCALE = 2; // scale at which to draw the images
	private short nAnimation = 0; // value between 0 and 2, defines which image to render from the 3 images we use
									// for the animation
	private BufferedImage animation[]; // images of the animation

	/**
	 * Constructor
	 */
	public Bird() {
		x = (int) (Game.WIDTH / 4);
		y = (int) (Game.HEIGHT / 2.2);

		setupAnimation();
	}

	/**
	 * @return if the bird is dying
	 */
	public boolean isDying() {
		return dying;
	}

	/**
	 * Setup the flapping bird animation, 3 images
	 */
	private void setupAnimation() {
		animation = new BufferedImage[kANIMATION_IMAGES];

		for (int i = 0; i < kANIMATION_IMAGES; i++) {
			try {
				animation[i] = ImageIO.read(new File("res/BirdAnim" + Integer.toString(i + 1) + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		imgWidth = (short) animation[0].getWidth();
		imgHeight = (short) animation[0].getHeight();

		width = imgWidth * kSCALE;
		height = imgHeight * kSCALE;
	}

	/**
	 * Setup dying animation
	 */
	public void dyingAnimation() {
		dying = true;

		rotationAngle = Math.PI / 2;
	}

	/**
	 * make the bird jump, called when the space bar is pressed
	 * 
	 * @param jumpForce, upwards velocity
	 */
	public void jump(float jumpForce) {
		if (!dying)
			yVelocity = jumpForce;
	}

	/**
	 * @return the rectangle bounding the bird
	 */
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	/**
	 * increase score
	 */
	public void addPoint() {
		score++;

		if (score > record)
			record = score;
	}

	/**
	 * switch animation picture
	 */
	public void animate() {
		nAnimation = (short) ((nAnimation + 1) % kANIMATION_IMAGES); // animate
	}

	/**
	 * @return if the bird hit the floor or not
	 */
	public boolean hitFloor() {
		return (y + height >= Game.HEIGHT);
	}

	/**
	 * Draw the game
	 * 
	 * @param g,       tool to draw
	 * @param started, draw according to if the game started or not
	 */
	public void draw(Graphics g, boolean started) {

		if (!started) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Calibri", Font.PLAIN, 20));

			g.drawString("Space Bar to jump.", x, y + height * 2);

			// draw Record:
			g.setFont(new Font("Broadway", Font.BOLD, 30));
			g.setColor(Color.WHITE);

			g.drawString("Record: " + record, 10, Game.HEIGHT - 10);
		} else {
			String scoreText = Integer.toString(score);
			Font font = new Font("Broadway", Font.BOLD, 65);

			g.setColor(Color.WHITE);
			g.setFont(font);

			int strWidth = g.getFontMetrics(font).stringWidth(scoreText);

			g.drawString(scoreText, Game.WIDTH / 2 - strWidth / 2, Game.HEIGHT / 5);
		}

		float damper = Game.kGRAVITY * 1.2f;

		if (yVelocity > damper && yVelocity < -damper)
			drawBird(g);
		else {
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform rotated = AffineTransform.getRotateInstance(rotationAngle * -Math.signum(yVelocity),
					x + imgWidth / 2, y + imgHeight / 2);

			g2d.setTransform(rotated);

			drawBird(g);
		}

	}

	/**
	 * Draw the bird
	 * 
	 * @param g, tool to draw
	 */
	private void drawBird(Graphics g) {
		g.drawImage(animation[nAnimation], x, y, width, height, null);
	}

	/**
	 * Update the bird movement and physics
	 * 
	 * @param dt, delta time
	 */
	public void update(double dt) {
		yVelocity += Game.kGRAVITY * dt;
		y -= yVelocity;
	}

}

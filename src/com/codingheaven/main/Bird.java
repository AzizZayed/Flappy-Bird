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

/**
 * class to handle bird functionality
 * 
 * @author Zayed
 *
 */
public class Bird {

	private int x, y; // position in 2D space
	private int width, height; // Dimensions of the bird
	private float yVelocity; // vertical velocity
	private double rotationAngle = Math.PI / 4; // 30 degrees rotation for the bird
	private boolean dying = false; // for dying animation
	private int score = 0; // number of pipes past
	private static int record = 0;

	private final static short kANIMATION_IMAGES = 3; // number of pictures in the flying bird animation
	private int imgWidth, imgHeight; // dimensions of the bird images
	private short nAnimation = 0; // value between 0 and 2, which image to render from the 3 image animation
	private BufferedImage animation[]; // images of the animation

	/**
	 * Constructor
	 * 
	 * @param w - width of canvas
	 * @param h - height of canvas
	 */
	public Bird(int w, int h) {
		x = (int) (w / 4);
		y = (int) (h / 2.2);

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

		imgWidth = animation[0].getWidth();
		imgHeight = animation[0].getHeight();

		final int scale = 2;
		width = imgWidth * scale;
		height = imgHeight * scale;
	}

	/**
	 * Setup dying animation
	 */
	public void setupDyingAnimation() {
		dying = true;
		rotationAngle = Math.PI / 2;
	}

	/**
	 * make the bird jump, called when the space bar is pressed
	 * 
	 * @param jumpForce - upwards velocity
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
	 * check if bird his the floor
	 * 
	 * @param h - height of canvas
	 * @return if the bird hit the floor or not
	 */
	public boolean hitFloor(int h) {
		return (y + height >= h);
	}

	/**
	 * Draw the bird, the text for score and record and info under bird
	 * 
	 * @param g       - tool to draw
	 * @param started - draw according to if the game started or not
	 * @param w       - width of canvas
	 * @param h       - height of canvas
	 */
	public void draw(Graphics g, boolean started, int w, int h) {

		if (!started) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Calibri", Font.PLAIN, 20));

			g.drawString("Space Bar to jump.", x, y + height * 2);

			// draw Record:
			g.setFont(new Font("Broadway", Font.BOLD, 30));
			g.setColor(Color.WHITE);

			g.drawString("Record: " + record, 10, h - 10);
		} else {
			String scoreText = Integer.toString(score);
			Font font = new Font("Broadway", Font.BOLD, 65);

			g.setColor(Color.WHITE);
			g.setFont(font);

			int strWidth = g.getFontMetrics(font).stringWidth(scoreText);

			g.drawString(scoreText, w / 2 - strWidth / 2, h / 5);
		}

		float damper = Game.kGRAVITY * 1.2f;

		if (yVelocity < damper || yVelocity > -damper) {
			Graphics2D g2d = (Graphics2D) g;

			double angle = rotationAngle * -Math.signum(yVelocity);
			g2d.setTransform(AffineTransform.getRotateInstance(angle, x + imgWidth / 2, y + imgHeight / 2));
		}

		g.drawImage(animation[nAnimation], x, y, width, height, null);
	}

	/**
	 * Update the bird movement and physics
	 * 
	 * @param dt      - time between updates
	 * @param started - true if the game started, false if otherwise
	 */
	public void update(double dt, boolean started) {
		nAnimation = (short) ((nAnimation + 1) % kANIMATION_IMAGES); // animate

		if (started) {
			yVelocity += Game.kGRAVITY * dt;
			y -= yVelocity;
		}
	}

}

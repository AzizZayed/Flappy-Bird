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

	private int x, y;
	private int width, height;
	private float yVelocity;
	private double rotationAngle = Math.PI / 4; // 30 degrees
	private boolean dying = false;
	private int score = 0;
	private static int record = 0;

	private final static short kANIMATION_IMAGES = 3;
	private short imgWidth, imgHeight;
	private final static short kSCALE = 2;
	private short nAnimation = 0; // value between 0 and 2, defines which image to render from the 3 images we use
									// for the animation
	private BufferedImage animation[];

	public Bird(Game game) {
		x = (int) (Game.WIDTH / 4);
		y = (int) (Game.HEIGHT / 2.2);

		setupAnimation();
	}

	/**
	 * @return the dying
	 */
	public boolean isDying() {
		return dying;
	}

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
	
	public void dyingAnimation() {
		dying = true;

		rotationAngle = Math.PI / 2;
	}

	public void jump(float jumpForce) {
		if (!dying)
			yVelocity = jumpForce;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	public void addPoint() {
		score++;

		if (score > record)
			record = score;
	}

	public void animate() {
		nAnimation = (short) ((nAnimation + 1) % kANIMATION_IMAGES); // animate
	}

	public boolean hitFloor() {
		return (y + height >= Game.HEIGHT);
	}

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

	private void drawBird(Graphics g) {
		g.drawImage(animation[nAnimation], x, y, width, height, null);
	}

	public void update(double dt) {
		yVelocity += Game.kGRAVITY * dt;
		y -= yVelocity;
	}

}

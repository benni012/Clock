package eu.misselwitz;

import org.lwjgl.input.Keyboard;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.Sys;
import java.io.File;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import java.util.Calendar;

public class Clock {

	long lastFrameTime;
	float hAngle;
	float vAngle;
	Calendar c = Calendar.getInstance();
	boolean hTurn = false;
	boolean vTurn = false;

	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		getDelta();
		init();

		while (!Display.isCloseRequested()) {

			poll();
			update(getDelta());
			render();

			Display.update();
		}

		Display.destroy();
	}

	private void drawCiphers() {
		double angle;
		for (int i = 1; i <= 60; i++) {
			GL11.glBegin(GL11.GL_QUADS); 
				float x1, x2, y1, y2;
				angle = i * (360 / 60);
				angle = fixAngle(angle);

				if (i % 5 == 0) {
					x1 = ((float) Math.cos(degToRad(angle - 1)));
					y1 = ((float) Math.sin(degToRad(angle - 1)));

					x2 = ((float) Math.cos(degToRad(angle + 1)));
					y2 = ((float) Math.sin(degToRad(angle + 1)));

					GL11.glColor3f(0f, 0f, 0f);				
					GL11.glVertex3f(x1 * 17f, y1 * 17f, 0.05f);
					GL11.glVertex3f(x2 * 17f, y2 * 17f, 0.05f);
					GL11.glVertex3f(x2 * 15f, y2 * 15f, 0.05f);
					GL11.glVertex3f(x1 * 15f, y1 * 15f, 0.05f);
				} else {
					x1 = ((float) Math.cos(degToRad(angle - 0.3f)));
					y1 = ((float) Math.sin(degToRad(angle - 0.3f)));

					x2 = ((float) Math.cos(degToRad(angle + 0.3f)));
					y2 = ((float) Math.sin(degToRad(angle + 0.3f)));

					GL11.glColor3f(0.2f, 0.2f, 0.2f);				
					GL11.glVertex3f(x1 * 17f, y1 * 17f, 0.05f);
					GL11.glVertex3f(x2 * 17f, y2 * 17f, 0.05f);
					GL11.glVertex3f(x2 * 16f, y2 * 16f, 0.05f);
					GL11.glVertex3f(x1 * 16f, y1 * 16f, 0.05f);
				}
			GL11.glEnd();
		}
	}

	private void drawHull(float radius) {
		boolean front = false;
		int numCircleVertices = 200;
		double angle;

		GL11.glBegin(GL11.GL_TRIANGLE_STRIP); 
			for (int i = 0; i <= numCircleVertices; i++) {
				angle = (i / (float) (numCircleVertices - 2)) * 2 * Math.PI;
				float x = ((float) Math.cos(angle) * radius);
				float y = ((float) Math.sin(angle) * radius);
				//System.out.println(inner + ": (" + x + ", " + y + ")");
				
				if (front) {
					GL11.glVertex3f(x, y, -1.0f);
				} else {
					GL11.glVertex3f(x, y, 1.0f);
				}
				
				front = !front;
			}
		GL11.glEnd();
	}

	private void drawRing(float innerRadius, float outerRadius, float depth) {
		int numCircleVertices = 200;
		boolean inner = true;
		float radius;
		double angle;

		GL11.glBegin(GL11.GL_TRIANGLE_STRIP); 
			for (int i = 0; i <= numCircleVertices; i++) {
				angle = (i / (float) (numCircleVertices - 2)) * 2 * Math.PI;
				if (inner) {
					radius = innerRadius;
				} else {t
					radius = outerRadius;
				}
				float x = ((float) Math.cos(angle) * radius);
				float y = ((float) Math.sin(angle) * radius);
				//System.out.println(inner + ": (" + x + ", " + y + ")");
				GL11.glVertex3f(x, y, depth);
				inner = !inner;
			}
		GL11.glEnd();
	}

	private double fixAngle(double angle) {
		return 360 - angle + 90;
	}

	private void drawPointer(float radius, double angle, float depth, float sRadius) {
		GL11.glBegin(GL11.GL_QUADS); 
			angle = fixAngle(angle);
			float x = ((float) Math.cos(degToRad(angle)) * radius);
			float y = ((float) Math.sin(degToRad(angle)) * radius);
			GL11.glVertex3f(x, y, depth);
			GL11.glVertex3f((float) Math.cos(degToRad(angle - 45)) * sRadius, (float) Math.sin(degToRad(angle - 45)) * sRadius, depth);
			GL11.glVertex3f(0f, 0f, depth);
			GL11.glVertex3f((float) Math.cos(degToRad(angle + 45)) * sRadius, (float) Math.sin(degToRad(angle + 45)) * sRadius, depth);
		GL11.glEnd();
	}

	private double degToRad(double deg) {
		return deg / 360 * (2 * Math.PI);
	}

	private double radToDeg(double rad) {
		return rad / (2 * Math.PI) * 360;
	}

	private void drawCircle(float radius, float depth) {
		int numCircleVertices = 200;
		double angle;

		GL11.glBegin(GL11.GL_TRIANGLE_STRIP); 
			for (int i = 0; i <= numCircleVertices; i++) {
				angle = (i / (float) (numCircleVertices - 2)) * 2 * Math.PI;
				float x = ((float) Math.cos(angle) * radius);
				float y = ((float) Math.sin(angle) * radius);
				//System.out.println(inner + ": (" + x + ", " + y + ")");
				GL11.glVertex3f(x, y, depth);
				GL11.glVertex3f(0f, 0f, depth);
			}
		GL11.glEnd();
	}

	private void drawPointers(float hours, int minutes, int seconds) {
		GL11.glColor3f(0.6f, 0f, 0.f);
		drawPointer(17f, 360 / 60 * seconds, 0.14f, 0.75f);
		GL11.glColor3f(0.3f, 0.3f, 0.3f);
		drawPointer(14f, 360 / 60 * minutes, 0.10f, 0.85f);
		GL11.glColor3f(0.2f, 0.2f, 0.2f);
		drawPointer(10f, 360 / 12 * hours, 0.12f, 1f);
	}

	private void init() {
		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();

		
		GL11.glViewport(0, 0, width, height);
		Display.setTitle("Clock");
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(45.0f, ((float) width / (float) height), 0.1f, 100.0f); // Calculate The Aspect Ratio Of The Window
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glShadeModel(GL11.GL_SMOOTH); // Enables Smooth Shading
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
		GL11.glClearDepth(1.0f); // Depth Buffer Setup
		GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
		GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Test To Do
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); // Really Nice Perspective Calculations
	}

	private void poll() {}

	private String fillUp(String s, int n, char c, boolean front) {
		while(s.length() < n) {
			if (front) {
				s = c + s;
			} else {
				s = s + c;
			}
		}
		return s;
	}

	private void update(int delta) {
		c.setTimeInMillis(System.currentTimeMillis());
		
		String title = "It's " + fillUp(""+c.get(Calendar.HOUR_OF_DAY), 2, '0', true) + ":" + fillUp(""+c.get(Calendar.MINUTE), 2, '0', true) + ":" + fillUp(""+c.get(Calendar.SECOND), 2, '0', true);
		if (!Display.getTitle().equals(title)) {
			Display.setTitle(title);
		}

		if (c.get(Calendar.MINUTE) % 60 == 0 && c.get(Calendar.SECOND) % 60 == 0) {
			vTurn = true;
		} else if (c.get(Calendar.SECOND) % 60 == 0) {
			hTurn = true;
		}

		if (hTurn) {
			hAngle += 0.1f * delta;
			if (hAngle >= 360) {
				hTurn = false;
				hAngle = 0;
			}
		}

		if (vTurn) {
			vAngle += 0.1f * delta;
			if (vAngle >= 360) {
				vTurn = false;
				vAngle = 0;
			}
		}
	}

	private void render() {
		//Clear the screen
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//Reset the view
		GL11.glLoadIdentity();


		//Set the origin to that point
		GL11.glTranslatef(0.0f, 0.0f, -60.0f);
		GL11.glRotatef(hAngle, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(vAngle, 1.0f, 0.0f, 0.0f);
		//GL11.glRotatef(10f, 0.0f, 1.0f, 0.0f);
		

		//Draw the inner circle plane
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		drawCircle(18f, 0f);
		GL11.glColor3f(0.6f, 0.6f, 0.6f);
		drawCircle(18f, -1f);

		//Draw the ciphers
		drawCiphers();

		//Draw the pointers
		long secs = c.get(Calendar.HOUR) * 3600 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);
		drawPointers(secs / 3600f, c.get(Calendar.MINUTE), c.get(Calendar.SECOND));

		//Draw the point in the middle
		GL11.glColor3f(0.1f, 0.1f, 0.1f);
		drawCircle(0.5f, +0.06f);

		//Draw the front and the back ring
		GL11.glColor3f(0.6f, 0.6f, 0.6f);
		drawRing(18f, 20f, -1f);
		drawRing(18f, 20f, +1f);

		//Draw both the hulls
		GL11.glColor3f(0.5f, 0.5f, 0.5f);
		drawHull(18f);
		drawHull(20f);

		GL11.glLoadIdentity();

		//Limit the frame rate to 60fps
		Display.sync(60);
	}

	public static void main(String[] argv) {
		System.setProperty("java.library.path", "../library");
		System.setProperty("org.lwjgl.librarypath", new File("../library/natives").getAbsolutePath());

		Clock clock = new Clock();
		clock.start();
	}

	public int getDelta() {
	    long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
	    int delta = (int) (time - lastFrameTime);
	    lastFrameTime = time;
	 
	    return delta;
	}
}
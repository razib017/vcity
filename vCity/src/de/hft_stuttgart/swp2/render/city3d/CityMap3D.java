package de.hft_stuttgart.swp2.render.city3d;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.Toolkit;
import java.nio.IntBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.opencl.SunPositionCalculator;
import de.hft_stuttgart.swp2.parser.Parser;
import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.options.PanelCityInfo;
import de.hft_stuttgart.swp2.render.options.PanelSettings;
import de.hft_stuttgart.swp2.render.threads.StartShadowCalculationRunnable;

/**
 * 
 * 
 * 
 * Die Funktion glDepthFunc legt fest, wann ein Fragment den Tiefentest im
 * Tiefenpuffer besteht. Der Parameter func legt die Tiefenvergleichsfunktion
 * fest. Die Tiefenvergleichsfunktion ist eine Bedingung, die erf�llt sein muss,
 * damit das entsprechende Pixel/Fragment gezeichnet wird. Folgende Funktionen
 * existieren: GL_NEVER (Neue Fragmente bestehen niemals den Vergleich) GL_LESS
 * (Neue Fragmente bestehen den Vergleich, wenn sie einen geringeren Tiefenwert
 * haben) und GL_EQUAL, GL_LEQUAL, GL_GREATER, GL_NOTEQUAL, GL_GEQUAL und
 * GL_ALWAYS. Voreingestellt ist GL_LESS
 * 
 * 
 * @author 21ruma1bif
 * 
 */
public class CityMap3D extends JFrame implements GLEventListener {

	private static final long serialVersionUID = 6681486095144440340L;
	private static final int FPS = 40;
	private GL2 gl;
	private GLU glu;
	private GLUT glut = new GLUT();
	public Camera camera;
	public Robot robot;
	public int halfScreenHeight;
	public int halfScreenWidth;
	private float r = 10000;
	private MouseEventListener mouseEvent;
	public boolean enableDrawCenters = false;
	private boolean isShadowCalc = false;
	private boolean isVolumeCalc = true;
	private boolean isShowGrid = true, isStartCalculation = true;
	private static int selectedBuildingId = -1;

	//if the checkbox for shadow isSelected and 
	//if is isShadowCalcViaCheckBoxLock=false
	//then it will be calculated immediately with
	//the shadow default settings, else not,
	//then it would be calculated, only when 
	//the button for recalculate shadow would be pressed
	private boolean isShadowCalcViaCheckBoxLock = true; 
	private boolean isRecalculateShadow = false; 



	private int splitAzimuth = Main.getSplitAzimuth();
	private int splitHeight = Main.getSplitHeight();
	private boolean isPerformance = false;
	private FPSAnimator animator;

	private SunPositionCalculator[] sunPositions;
	// Sunposition vars
	public Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	private SunPositionCalculator sunPos;
	private boolean isShowVolumeAmount = true;
	public int month = 0;
	private boolean isPolygon = false;
	private boolean isCalculating = false;
	private GLBuildingEntity[] glBuildings;
	private boolean isFirstTimeShadowCalc = false;
	private GLCapabilities caps;
	private GLCanvas canvas;
	
	private boolean isFirstTimeVolumeCalc = false;
	private boolean isVolumeChange = true;
	private boolean isShadowChange = true;
	public boolean drawPolygons = true;
	
	public int ray = 0;
	
	public static int getSelectedBuildingId() {
		return selectedBuildingId;
	}
	
	public GLBuildingEntity getGlBuildingsViaID(int id) {
		return glBuildings[id];
	}

	
	public GLCanvas getCanvas() {
		return canvas;
	}

	public boolean isShowVolumeAmount() {
		return isShowVolumeAmount;
	}

	public void setShowVolumeAmount(boolean isShowVolumeAmount) {
		this.isShowVolumeAmount = isShowVolumeAmount;
	}

	public void setRecalculateShadow(boolean isRecalculateShadow) {
		this.isRecalculateShadow = isRecalculateShadow;
	}

	public void resetValues(){
		isShadowCalc = false;
		isVolumeCalc = true;
		isStartCalculation = true;
		isRecalculateShadow = false; 
		isCalculating = false;
		isFirstTimeShadowCalc = false;
		isFirstTimeVolumeCalc = false;
		isVolumeChange = true;
		isShadowChange = true;
	}

	public boolean isFirstTimeShadowCalc() {
		return isFirstTimeShadowCalc;
	}


	
	
	public boolean isFirstTimeVolumeCalc() {
		return isFirstTimeVolumeCalc;
	}


	public SunPositionCalculator[] getSunPositions() {
		return sunPositions;
	}

	public SunPositionCalculator getSunPos() {
		return sunPos;
	}


	public void setIsStartCalculation(Boolean isStartCalculation) {
		this.isStartCalculation = isStartCalculation;
	}
	
	public void setIsFirstTimeShadowCalculation(boolean isFirstTimeShadowCalc) {
		this.isFirstTimeShadowCalc= isFirstTimeShadowCalc;
	}

	public void setShowGrid(boolean isShowGrid) {
		this.isShowGrid = isShowGrid;
	}
	
	public boolean isShowGrid() {
		return isShowGrid;
	}

	
	public CityMap3D(int width, int height) {
		super("vCity - 3D Stadtansicht");
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date());
		this.setLayout(new BorderLayout());
		this.setSize(width, height);
		this.requestFocus();
		// setGround(minGroundSize, maxGroundSize);

		//
		try {
			robot = new Robot();
		} catch (AWTException e) {
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// set up the drawing canvas
		caps = new GLCapabilities(GLProfile.getDefault());
		caps.setSampleBuffers(true);
	    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	    DisplayMode mode = device.getDisplayMode();
		final int bitDepth = mode.getBitDepth();
		caps.setNumSamples(16); 
		caps.setStencilBits(bitDepth);
		caps.setAccumAlphaBits(bitDepth);
		caps.setAccumBlueBits(bitDepth);
		caps.setAccumGreenBits(bitDepth);
		caps.setAccumRedBits(bitDepth);
		caps.setDoubleBuffered(true);
		caps.setHardwareAccelerated(true);


		canvas = new GLCanvas(caps);
		canvas.setPreferredSize(new Dimension(width, height));
		add(canvas, BorderLayout.CENTER);

		// draw the scene at FPS fps
		animator = new FPSAnimator(canvas, FPS);
		animator.start();

		addListeners(canvas);
		this.setLocationRelativeTo(null);
		halfScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
		halfScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
		robot.mouseMove(halfScreenWidth, halfScreenHeight);
		pack();
	}

	/**
	 * initializes the sun position by month and day
	 * @param month
	 * @param day
	 */
	public void initialSunPosition(int month, int day) {
		sunPositions = new SunPositionCalculator[24];
		for (int j = 0; j < 24; ++j) {
			utcCal.set(2014, month, day, j, 0, 0);
			while (Parser.getInstance().getEPSG() == null){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sunPositions[j] = new SunPositionCalculator(utcCal.getTime(),
					Parser.getInstance());
		}
		setSunPosition(Main.getTimeForSunPosition());
	}

	public void setSunPosition(GregorianCalendar cal) {
		Calendar calendar = cal;
		month = calendar.get(Calendar.MONTH);
		sunPos = new SunPositionCalculator(calendar.getTime(),
				Parser.getInstance());
		ray = sunPos.getSunPosition(splitAzimuth, splitHeight);
	}

	private void addListeners(GLCanvas canvas) {
		canvas.addGLEventListener(this);
		KeyEventListener keyEvent = new KeyEventListener(this);
		this.addKeyListener(keyEvent);
		canvas.addKeyListener(keyEvent);
		mouseEvent = new MouseEventListener(this);
		canvas.addMouseListener(mouseEvent);
		canvas.addMouseMotionListener(mouseEvent);
		canvas.addMouseWheelListener(mouseEvent);
	}

	/**
	 * Starts the threads and checks whether a new calculation is needed
	 * isVolumeCalc: Gives the information of the checkboxes in the
	 * PanelSettings if it is selected or not isFirstTimeVolumeCalc: gives the
	 * information if the volume calculation was started or not isVolumeChange:
	 * gives the information if the value of isVolumeCalc has changed or not
	 * isStartCalculation: gives the information if the calculation starts for
	 * the first time or several times with the same parsed city
	 */
	public void calculation() {
		isCalculating = true;
		if (Main.isParserSuccess()) {
			if (isVolumeCalc && isFirstTimeVolumeCalc
					&& (isVolumeChange || isStartCalculation)) {
				Main.executor.execute(Main.startVolumeCalculationRunnable);
			}
			if (isShadowCalc && isFirstTimeShadowCalc
					&& (isShadowChange || isStartCalculation)) {
				int day = Main.getTimeForSunPosition().get(Calendar.DAY_OF_MONTH);
				int month = Main.getTimeForSunPosition().get(Calendar.MONTH);
				initialSunPosition(month, day);
				setSunPosition(Main.getTimeForSunPosition());
				Main.executor.execute(Main.startShadowCalculationRunnable);
			}
		}

	}

	public static float[] RGBToOpenGL(int r1, int g1, int b1) {
		float[] tmp = new float[3];
		tmp[0] = r1 / 255f;
		tmp[1] = g1 / 255f;
		tmp[2] = b1 / 255f;
		return tmp;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		switch (cmd) {
		case UPDATE:
			drawScene(gl);
			break;
		case SELECT:
			if(Main.isCityAndBuildingsCalculated()){
				pickBuildingArea(gl);
			}else{
				cmd = UPDATE;
			}
			break;
		}
	}
	
	public int getCmd() {
		return cmd;
	}

	private static final int UPDATE = 1, SELECT = 2;
	public static int getSelect() {
		return SELECT;
	}

	private int cmd = UPDATE;
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
	
	public int viewPortWidth(GL2 gl) {
		int[] viewPort = new int[4];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);
		return viewPort[2];
	}

	public int viewPortHeight(GL2 gl) {
		int[] viewPort = new int[4];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);
		return viewPort[3];
	}

	private void pickBuildingArea(GL2 gl){
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_NORMALIZE);
		int buffsize = 1024;
		double x = mouseEvent.getMouse_x();
		double y = mouseEvent.getMouse_y();
		int[] viewPort = new int[4];
		IntBuffer selectBuffer = Buffers.newDirectIntBuffer(buffsize);
		int hits = 0;


		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);
		gl.glSelectBuffer(buffsize, selectBuffer);
		gl.glRenderMode(GL2.GL_SELECT);
		gl.glInitNames();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		glu.gluPickMatrix(x, (double) viewPort[3] - y, 5.0d, 5.0d,
				viewPort, 0);
//		camera.setPerspective((int)x, (int)y);
		glu.gluPerspective(60, (double) x / y, 0.1, 20000);
		drawScene(gl);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glFlush();
		hits = gl.glRenderMode(GL2.GL_RENDER);
		//unexpected value -1, if error in glRenderMode
		if(hits >= 0){
			processHits(hits, selectBuffer);
		}else{
			PanelCityInfo.updateCityInfo();	
		}
		cmd = UPDATE;
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
//		camera = tempCamera[0];
	}

	public void processHits(int hits, IntBuffer buffer) {
		System.out.println("---------------------------------");
		System.out.println(" HITS: " + hits);
		int [] buildingIds = new int [hits]; 
		int buildingCounter = 0;
		int offset = 0;
		int names = -1;
		int finalBuildingId;
		float z1, z2;
		names = buffer.get(offset);
		for (int i = 0; i < hits; i++) {
			System.out.println("- - - - - - - - - - - -");
			System.out.println(" hit: " + (i + 1));
			names = buffer.get(offset);
			offset++;
//			System.out.println("0xffffffffL " + 0xffffffffL);
//			System.out.println("offset " + offset);
//			System.out.println("buffer.get(offset) " + buffer.get(offset));
			z1 = (float) (buffer.get(offset) & 0xffffffffL) / 0x7fffffff;
			offset++;
			z2 = (float) (buffer.get(offset) & 0xffffffffL) / 0x7fffffff;
			System.out.println(" z1: " + z1 + ", z2: " + z2);
			offset++;
			for (int j = 0; j < names; j++) {
//				if (j == (names - 1))
//					System.out.println("<-");
//				else
//					System.out.println();
				offset++;
			}
			System.out.println("- - - - - - - - - - - -");
			System.out.println("Building id: " + (names - 1));
			buildingIds[buildingCounter] = names - 1;
			buildingCounter++;
		}
		
		if(hits != 0 && names != -1){
			finalBuildingId = buildingIds[0]; // buildingIds[buildingCounter/2] is not really better
			System.out.println("- - - - - - - - - - - -");
			System.out.println("Selected Building id: " + finalBuildingId);
			CityMap3D.selectedBuildingId = finalBuildingId;
			PanelCityInfo.appendCityInfoOneBuilding(glBuildings[finalBuildingId].building);
		}else{
			PanelCityInfo.updateCityInfo();
		}
		System.out.println("---------------------------------");
	}

	private void drawScene(GL2 gl) {
		splitAzimuth = Main.getSplitAzimuth();
		splitHeight = Main.getSplitHeight();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		// apply camera modifications
		if(cmd == UPDATE){
			gl.glLoadIdentity();
			camera.lookAt();
			// drawing building 0
			drawHemisphere(gl);
			drawAxis(gl);
			drawSkyModel(gl);
			// setGround(minGroundSize, maxGroundSize);
			if (enableDrawCenters) {
				drawCentersOfHemisphere(gl);
			}
			
			gl.glColor3f(1f, 1f, 1f);
			


			// TODO Wenn sich der Pfad �ndert alles neuberechnen, wenn nicht city
			// Objekte speichern
			if (isStartCalculation && Main.isParserSuccess()) {
				StartShadowCalculationRunnable.setShadowCalculated(false);
				if (Main.isCalculateShadow()) {
					isFirstTimeShadowCalc = true;
				} else {
					isFirstTimeShadowCalc = false;
				}
				if (Main.isCalculateVolume()) {
					isFirstTimeVolumeCalc = true;
				} else {
					isFirstTimeVolumeCalc = false;
				}
				calculation();
				// Berechnung der GL-Buildings
				if (City.getInstance().getBuildings() != null) {
					glBuildings = new GLBuildingEntity[City.getInstance()
							.getBuildings().size()];
					GLBuildingEntity glBuilding;
					int buildingCounter = 0;
					for (Building building : City.getInstance().getBuildings()) {
						glBuilding = new GLBuildingEntity(gl, glu, glut, building,
								isVolumeCalc, isShadowCalc, isPolygon, isShowGrid,
								isShowVolumeAmount);
						glBuildings[buildingCounter] = glBuilding;
						buildingCounter++;
					}
				}
				isStartCalculation = false;
			}
			changeView();
			if (glBuildings != null && Main.isParserSuccess()) {
				setZBuffer(gl);
				boolean isViewShadow = Main.getOptionGUI().isShadowViewSelected();
				boolean isViewVolume = Main.getOptionGUI().isVolumeViewSelected();
				// DRAW BUILDINGS
				if (!isChangeValue(isShadowCalc, isVolumeCalc,
						Main.getOptionGUI().isCalculateShadow(), 
						Main.getOptionGUI().isCalculateVolume())
						&& !isRecalculateShadow) {
					for (GLBuildingEntity glBuilding : glBuildings) {
						glBuilding.setShadowCalc(isViewShadow);
						glBuilding.setVolumeCalc(isViewVolume);
						glBuilding.draw();
					}
				} else {
					isShadowCalc = Main.getOptionGUI().isCalculateShadow();
					isVolumeCalc = Main.getOptionGUI().isCalculateVolume();
					isRecalculateShadow = false;
					if (isFirstTimeShadowCalc == false && isShadowCalc) {
						// notice the query: if(isFirstTimeVolumeCalc == false &&
						// isVolumeCalc)
						// is not needed because you can't press so fast the two
						// checkboxes
						// as the display-Method runs
						isFirstTimeShadowCalc = true;
						calculation();
					} else if (isFirstTimeVolumeCalc == false && isVolumeCalc) {
						isFirstTimeVolumeCalc = true;
						calculation();
					}
					//View boolean values
					for (GLBuildingEntity glBuilding : glBuildings) {
						glBuilding.setShadowCalc(isViewShadow);
						glBuilding.setVolumeCalc(isViewVolume);
						glBuilding.draw();
					}
				}
				disableZBuffer(gl);
			}


			
			isShadowCalc = Main.getOptionGUI().isCalculateShadow();
			isVolumeCalc = Main.getOptionGUI().isCalculateVolume();
			isCalculating = false; //Variable must stand on the end
			
//		    gl.glStencilFunc(GL.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1
//		    gl.glStencilMask(0x00); // Don't write anything to stencil buffer
		    gl.glDepthMask(true); // Write to depth buffer
		    gl.glFlush();
		}else{
			if (glBuildings != null && Main.isParserSuccess()) {
				boolean isViewShadow = Main.getOptionGUI().isShadowViewSelected();
				boolean isViewVolume = Main.getOptionGUI().isVolumeViewSelected();
				// DRAW BUILDINGS
				for (GLBuildingEntity glBuilding : glBuildings) {
					glBuilding.setShadowCalc(isViewShadow);
					glBuilding.setVolumeCalc(isViewVolume);
					glBuilding.draw();
				}
			    gl.glFlush();
			}
		}

	}

	private void drawSkyModel(GL2 gl2) {
		if (sunPositions == null) {
			return;
		}
		gl.glColor3f(1f, 0, 1f);
		gl.glBegin(GL2.GL_LINE_LOOP);
		for (int sunPositionIdx = 0; sunPositionIdx < sunPositions.length; sunPositionIdx++) {
			gl.glVertex3d(sunPositions[sunPositionIdx].getX(),
					sunPositions[sunPositionIdx].getY(),
					sunPositions[sunPositionIdx].getZ());
		}
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(sunPos.getX(), sunPos.getY(),
				sunPos.getZ());
		gl.glVertex3d(0, 0, 0);
		gl.glEnd();		
	}

	private boolean isChangeValue(boolean isShadowCalcOld,
			boolean isVolumeCalcOld, Boolean calculateShadow,
			Boolean calculateVolume) {
		
		if (isShadowCalcOld == calculateShadow) {
			isShadowChange = false;
			if (isVolumeCalcOld == calculateVolume) {
				isVolumeChange = false;
				return false;
			} else {
				isVolumeChange = true;
				return true;
			}
		} else {
			if(isShadowCalcViaCheckBoxLock){
				isShadowChange = true;
				if(isFirstTimeShadowCalc){
					return true;
				}else{
					return false;
				}
			}else{
				isShadowChange = true;
				return true;
			}
		}
	}

	private void drawCentersOfHemisphere(GL2 gl) {
		if (ray == -1) {
			return;
		}
		gl.glColor3f(1f, 1f, 0);
		float dv = (float) (Math.PI / splitHeight / 2);
		float dh = (float) (2 * Math.PI / splitAzimuth);
		float v = dv * (ray / splitAzimuth) + dv / 2;
		float h = dh * ((ray % splitAzimuth) - (splitAzimuth / 2f)) + dh / 2;
		double sinH = Math.sin(h);
		double sinV = Math.sin(v);
		double cosH = Math.cos(h);
		double cosV = Math.cos(v);

		double posX = cosV * sinH * r;
		double posY = sinV * r;
		double posZ = cosV * cosH * r;
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(0d, 0d, 0d);
		gl.glVertex3d(posX, posY, posZ);
		gl.glEnd();

	}

	private void drawHemisphere(GL2 gl) {
		gl.glColor3f(1, 1, 1);
		float dv = (float) (Math.PI / splitHeight / 2);
		float dh = (float) (2 * Math.PI / splitAzimuth);
		for (int i = 0; i < splitHeight; i++) {
			for (int j = 0; j < splitAzimuth; j++) {
				float v = dv * i;
				float h = dh * (j - (splitAzimuth / 2f));
				float newV = v + dv;
				float newH = h + dh;
				double sinH = Math.sin(h);
				double sinV = Math.sin(v);
				double cosH = Math.cos(h);
				double cosV = Math.cos(v);
				double sinNewV = Math.sin(newV);
				double sinNewH = Math.sin(newH);
				double cosNewV = Math.cos(newV);
				double cosNewH = Math.cos(newH);

				double posX = cosV * sinH * r;
				double posY = sinV * r;
				double posZ = cosV * cosH * r;

				double newVertPosX = cosNewV * sinH * r;
				double newVertPosY = sinNewV * r;
				double newVertPosZ = cosNewV * cosH * r;

				double newHorPosX = cosV * sinNewH * r;
				double newHorPosY = sinV * r;
				double newHorPosZ = cosV * cosNewH * r;

				double newPosX = cosNewV * sinNewH * r;
				double newPosY = sinNewV * r;
				double newPosZ = cosNewV * cosNewH * r;

				gl.glBegin(GL2.GL_LINE_LOOP);
				{
					gl.glVertex3d(posX, posY, posZ);
					gl.glVertex3d(newVertPosX, newVertPosY, newVertPosZ);
					gl.glVertex3d(newPosX, newPosY, newPosZ);
					gl.glVertex3d(newHorPosX, newHorPosY, newHorPosZ);
				}
				gl.glEnd();
			}
		}
	}

	private void drawAxis(GL2 gl) {
		gl.glColor3f(0f, 0f, 1f);
		// x Axis in blue
		gl.glEnable(GL.GL_TRUE);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glBegin(GL2.GL_LINES);

		gl.glLineWidth(5.0f);
		{
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(10000f, 0f, 0f);
		}
		gl.glEnd();
		// y Axis in green
		gl.glColor3f(0f, 1f, 0f);
		gl.glBegin(GL2.GL_LINES);
		{
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(0, 10000f, 0f);
		}
		gl.glEnd();

		// z Axis in red
		gl.glColor3f(1f, 0f, 0f);
		gl.glBegin(GL2.GL_LINES);
		{
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(0f, 0f, 10000f);
		}
		gl.glEnd();

		gl.glColor3f(1f, 1f, 1f);
		gl.glDisable(GL.GL_TRUE);
		gl.glDisable(GL.GL_LINE_SMOOTH);

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}
	
	public void setZBuffer(GL2 gl){
		gl.glEnable(GL2.GL_MULTISAMPLE);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL );
		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
//		gl.glEnable(GL2.GL_PERSPECTIVE_CORRECTION_HINT);
//		gl.glDepthRangef(-50.0f, 50.0f);
		gl.glClearDepth(1.0f);
//		gl.glClearColor(0.3333f, 0.3961f, 0.4941f, 0.0f);
//
		gl.glClearColor(0.3333f, 0.3961f, 0.4941f, 0.0f);


		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);
//		
		//--------------Stencil
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
		
		
//		  gl.glEnable(GL2.GL_STENCIL_TEST);
//		  gl.glStencilFunc(GL2.GL_LESS, 2, 0xFF); // Set any stencil to 1
//		  gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_REPLACE);
//		  gl.glStencilMask(0xFF); // Write to stencil buffer
//		  gl.glDepthMask(true); // Don't write to depth buffer
//		  gl.glClear(GL2.GL_STENCIL_BUFFER_BIT); // Clear stencil buffer (0 by default)
//		gl.glPolygonOffset(-20.0f, 20.0f) ;
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);      
//	    gl.glEnable(GL2.GL_POLYGON_SMOOTH);
//		gl.glPolygonMode( GL2.GL_BACK, GL2.GL_FILL );

		
		if (isPerformance) {
			gl.glEnable(GL2.GL_PERSPECTIVE_CORRECTION_HINT);
			gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_FASTEST);
		} else {
//			gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
			gl.glEnable(GL2.GL_POLYGON_SMOOTH_HINT);
			gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		}
	}
	
	public void disableZBuffer(GL2 gl){
		gl.glDisable(GL2.GL_DEPTH_TEST);
//		gl.glDisable(GL2.GL_PERSPECTIVE_CORRECTION_HINT);
//		gl.glDisable(GL2.GL_CULL_FACE);
//		gl.glDisable(GL2.GL_POLYGON_SMOOTH);
		gl.glDisable(GL2.GL_BLEND);
//		  gl.glDisable(GL2.GL_STENCIL_TEST);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		glu = new GLU();
		camera = new Camera(glu);
		camera.turnRight(-1.2);
		camera.turnDown(0.3);
		setZBuffer(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		gl = drawable.getGL().getGL2();

		if (height == 0) {
			height = 1;
		}

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matches viewport
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity(); // reset projection matrix
		camera.setPerspective(width, height);
		// Enable the model-view transform
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void stopAnimator() {
		if (animator != null) {
			animator.pause();
		}
	}

	public void startAnimator() {
		if (animator != null) {
			animator.resume();
		}
	}
	
	public void changeView(){
		if(isFirstTimeShadowCalc && isFirstTimeVolumeCalc && Main.isParserSuccess()){
			if(StartShadowCalculationRunnable.isShadowCalculated()){
				Main.getOptionGUI().setShadowViewEnabled(true);
			}
			Main.getOptionGUI().setVolumeViewEnabled(true);
			if(isCalculating){
				Main.getOptionGUI().getBtnRecalculateShadow().setText(PanelSettings.getStrRecalculate());
				Main.getOptionGUI().setSelectShadowView(true);
			}
		}else{
			if(isFirstTimeShadowCalc && Main.isParserSuccess()){
				Main.getOptionGUI().setShadowViewEnabled(true);
				if(isCalculating){
					Main.getOptionGUI().setSelectShadowView(true);
				}
			}else{
				Main.getOptionGUI().setShadowViewEnabled(false);
			}
			if(isFirstTimeVolumeCalc && Main.isParserSuccess()){
				Main.getOptionGUI().setVolumeViewEnabled(true);
				if(isCalculating){
					Main.getOptionGUI().setSelectVolumeView(true);
				}
			}else{
				Main.getOptionGUI().setVolumeViewEnabled(false);
			}
		}

	}

	public void setStartShadowCalculationRunnable(
			ShadowPrecision defaultShadowPrecision, int splitAzimuth,
			int splitHeight) {
		Main.startShadowCalculationRunnable = new StartShadowCalculationRunnable(
				defaultShadowPrecision, splitAzimuth, splitHeight);
	}

}

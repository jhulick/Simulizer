package simulizer.ui.components.highlevel;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.Stack;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;
import simulizer.highlevel.models.HanoiModel;
import simulizer.ui.windows.HighLevelVisualisation;

public class TowerOfHanoiVisualiser extends DataStructureVisualiser {
	private Canvas canvas = new Canvas();
	private HanoiModel model;
	private Color[] colorGradient = { Color.RED, Color.ORANGE, Color.BLUE, Color.GREEN, Color.YELLOW };

	private final Queue<Pair<Integer, Integer>> moves = new LinkedList<>();
	private boolean animating = false;
	private int animatedDiscIndex;
	private DoubleProperty animatedDiscX = new SimpleDoubleProperty();
	private DoubleProperty animatedDiscY = new SimpleDoubleProperty();
	private double animatedDiscWidth;
	private int FRAME_RATE = 45;
	private AnimationTimer timer = new AnimationTimer() {
		long lastTime = -1;

		@Override
		public void handle(long now) {
			// 30 FPS
			if (lastTime == -1 || now - lastTime > 1e9 / FRAME_RATE) {
				lastTime = now;
				repaint();
			}
		}
	};

	// Dimensions used for calculations
	private double xOffset;
	private double pegY0;
	private double pegHeight;
	private double platformWidth;
	private double pegWidth;
	private double discHeight;
	private double maxDiscWidth;
	private double discWidthDelta;

	public TowerOfHanoiVisualiser(HanoiModel model, HighLevelVisualisation vis) {
		super(model, vis);
		this.model = model;
		getChildren().add(canvas);

		canvas.widthProperty().bind(super.widthProperty());
		canvas.heightProperty().bind(super.heightProperty());

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(2);
		gc.setStroke(Color.BLACK);

		repaint();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object obj) {
		super.update(o, obj);

		// If a new move has been given,
		// add it to the queue and try to run it
		if (obj != null) {
			moves.add((Pair<Integer, Integer>) obj);
			runAnimations();
		}

		repaint();
	}

	private void runAnimations() {
		if (animating)
			return;
		else {
			Pair<Integer, Integer> move = moves.poll();

			int startPeg = move.getKey();
			int numDiscs = model.getNumDiscs();
			int endPeg = move.getValue();

			List<Stack<Integer>> pegs = model.getPegs();
			int numDiscsOnStart = pegs.get(startPeg).size();
			int numDiscsOnEnd = pegs.get(endPeg).size();

			animatedDiscIndex = pegs.get(move.getKey()).peek();
			this.animatedDiscWidth = getDiscWidth(animatedDiscIndex, numDiscs);

			double startX = getPegX(startPeg) - animatedDiscWidth / 2;
			double startY = getDiscY(numDiscsOnStart);

			double upX = startX;
			double upY = pegY0 - canvas.getHeight() / 10;

			double shiftX = getPegX(endPeg) - animatedDiscWidth / 2;
			double shiftY = upY;

			double endX = shiftX;
			double endY = getDiscY(numDiscsOnEnd);

			animatedDiscX.set(startX);
			animatedDiscY.set(startY);

			// @formatter:off
			Timeline timeline = new Timeline(
				new KeyFrame(Duration.seconds(0),
					new KeyValue(animatedDiscX, startX),
					new KeyValue(animatedDiscY, startY)
				),
				new KeyFrame(Duration.seconds(0.5),
					new KeyValue(animatedDiscX, upX),
					new KeyValue(animatedDiscY, upY)
				),
				new KeyFrame(Duration.seconds(0.8),
					new KeyValue(animatedDiscX, shiftX),
					new KeyValue(animatedDiscY, shiftY)
				),
				new KeyFrame(Duration.seconds(1.3),
					e -> {
						model.step(); // tell the model to move forward one move
						animating = false;
						repaint();

						if (moves.isEmpty()) {
							timer.stop();
							System.out.println("Hanoi animation timer stopped");
						}
						else runAnimations();
					},
					new KeyValue(animatedDiscX, endX),
					new KeyValue(animatedDiscY, endY)
				)
			);
			// @formatter:on
			timeline.setCycleCount(1);
			timeline.setRate(moves.size() + 1); // TODO: Be more accurate

			timer.start();
			timeline.play();

			animating = true;
		}
	}

	@Override
	public void repaint() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		final double width = canvas.getWidth();
		final double height = canvas.getHeight();

		calculateDimensions(gc, width, height);

		// TODO Platform.runLater() ?
		gc.clearRect(0, 0, width, height);
		drawBase(gc);
		drawStaticDiscs(gc);
		if (animating) {
			drawBorderedRectangle(gc, colorGradient[animatedDiscIndex % colorGradient.length], animatedDiscX.doubleValue(), animatedDiscY.doubleValue(), animatedDiscWidth, discHeight);
		}
	}

	private void drawBase(GraphicsContext gc) {
		gc.setFill(Color.BLACK);

		// Draw the platform
		gc.fillRect(xOffset, pegY0 + pegHeight, platformWidth, pegWidth);

		// Draw the pegs
		for (int i = 0; i < 3; ++i)
			gc.fillRect(getPegX(i) - pegWidth / 2, pegY0, pegWidth, pegHeight + 2); // + 2 to avoid gap between peg and platform
	}

	private void drawStaticDiscs(GraphicsContext gc) {
		List<Stack<Integer>> pegs = model.getPegs();
		int numDiscs = model.getNumDiscs();

		for (int pegIndex = 0; pegIndex < pegs.size(); ++pegIndex) {
			Stack<Integer> peg = pegs.get(pegIndex);

			for (int i = 0; i < peg.size(); ++i) {
				// n will go from the disc at the bottom to the top
				// 0 means it's the smallest disc
				int n = peg.get(i);

				// Don't draw the animated disc
				if (animating && n == animatedDiscIndex)
					continue;

				double discWidth = getDiscWidth(n, numDiscs);
				double discY = getDiscY(i);
				double discX = getPegX(pegIndex) - discWidth / 2;

				drawBorderedRectangle(gc, colorGradient[n % colorGradient.length], discX, discY, discWidth, discHeight);

				gc.setFill(colorGradient[n % colorGradient.length]);
				gc.fillRect(discX, discY, discWidth, discHeight);
				gc.strokeRect(discX, discY, discWidth, discHeight);
			}
		}
	}

	private void drawBorderedRectangle(GraphicsContext gc, Color fill, double x, double y, double w, double h) {
		gc.setFill(fill);
		gc.fillRect(x, y, w, h);
		gc.strokeRect(x, y, w, h);
	}

	/**
	 * @param pegIndex
	 *            the peg whose x coordinate will be calculated
	 * @return the x coordinate of the specified peg
	 */
	private double getPegX(int pegIndex) {
		return (int) (xOffset + (pegIndex + 0.5) * platformWidth / 3);
	}

	private double getDiscWidth(int discIndex, int numDiscs) {
		return maxDiscWidth - discWidthDelta * (numDiscs - 1 - discIndex);
	}

	private double getDiscY(int fromBottom) {
		return pegY0 + pegHeight - discHeight * (fromBottom + 1);
	}

	private void calculateDimensions(GraphicsContext gc, double width, double height) {
		this.platformWidth = (4 * width) / 5;
		this.xOffset = (width - platformWidth) / 2;
		this.pegY0 = height / 3;
		this.pegHeight = height / 2;
		this.pegWidth = width / 40;

		this.discHeight = Math.min(height / 14, pegHeight / model.getNumDiscs());
		this.maxDiscWidth = platformWidth / 3 - width / 120;
		this.discWidthDelta = Math.min(width / 30, (maxDiscWidth - pegWidth - width / 120) / (model.getNumDiscs() - 1));
	}

	@Override
	public String getName() {
		return "Towers of Hanoi";
	}

}

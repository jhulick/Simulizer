package simulizer.ui.windows;

import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import simulizer.BuildInfo;
import simulizer.GuiMode;
import simulizer.settings.Settings;
import simulizer.utils.FileUtils;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

/**
 * A splash screen component to be displayed before the main application opens.
 * It displays the names of the authors, the current version, and the
 * remote repository for the project.
 *
 * @author mbway
 * @author Kelsey McKenna
 *         with thanks from https://gist.github.com/jewelsea/2305098
 *
 */
public class SplashScreen {
	private int width, height;
	public String image;
	private Pane layout;
	private long startTime;
	private long delay;

	public SplashScreen(Settings settings) {
		ImageView splash;

		width = (int) settings.get("splash-screen.width");
		height = (int) settings.get("splash-screen.height");

		image = FileUtils.getResourcePath("/img/SimulizerLogo.png");
		splash = new ImageView(new Image(image, width, height, true, true));

		Label progressText = new Label("Authors: Charlie Street, Kelsey McKenna, Matthew Broadway, Michael Oultram, Theo Styles\n"
			+ "Version: " + BuildInfo.getInstance().VERSION_STRING + "\n" + BuildInfo.getInstance().REPO);

		layout = new VBox();
		layout.getChildren().addAll(splash, progressText);
		progressText.setPadding(new Insets(5, 5, 5, 5));
		progressText.setAlignment(Pos.BASELINE_CENTER);

		layout.getStyleClass().add("splashscreen");
		layout.setEffect(new DropShadow());

		delay = (int) settings.get("splash-screen.delay");
	}

	/**
	 * Shows the splash screen
	 *
	 * @param primaryStage
	 *            the stage used to for the main application
	 */
	public void show(Stage primaryStage) {

		Stage stage = new Stage(StageStyle.DECORATED);
		stage.initOwner(primaryStage);
		stage.setTitle("Simulizer Splash Screen");
		stage.getIcons().add(GuiMode.getIcon());

		Task<Boolean> startupTask = new Task<Boolean>() {
			@Override
			public Boolean call() {
				try {
					ThreadUtils.platformRunAndWait(() -> GuiMode.launchWindowManager(primaryStage));
					updateMessage("Authors: Charlie Street, Kelsey McKenna, Matthew Broadway, Michael Oultram, Theo Styles . . .");
					long offset = delay - (System.currentTimeMillis() - startTime);
					if (offset > 0) Thread.sleep(offset);
					return true;
				} catch (Throwable e) {
					UIUtils.showExceptionDialog(e);
					return false;
				}
			}
		};

		startupTask.stateProperty().addListener((observableValue, oldState, newState) -> {
            // SUCCEEDED once finished, regardless of whether the task returned true or false
			if (newState == Worker.State.SUCCEEDED) {
				FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), layout);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0.0);
				fadeSplash.setOnFinished(actionEvent -> stage.hide());

                if(startupTask.getValue()) { // true if succeeded
					GuiMode.wm.show();
				} else {
					UIUtils.showErrorDialog("Startup Error", "Since an exception was thrown during\nlaunchWindowManager, Simulizer cannot start. (CMD mode may still work)");
				}

				// remove the splash screen regardless of whether there was an exception
				fadeSplash.play();
			}
		});

		Scene splashScene = new Scene(layout);

		splashScene.getStylesheets().add(FileUtils.getResourcePath("/splash.css"));

		stage.initStyle(StageStyle.UNDECORATED);
		final Rectangle2D bounds = Screen.getPrimary().getBounds();
		stage.setScene(splashScene);
		stage.setX(bounds.getMinX() + (bounds.getWidth() / 2) - (width / 2.0));
		stage.setY(bounds.getMinY() + (bounds.getHeight() / 2) - (height / 2.0));
		stage.setWidth(width);
		stage.setHeight(height);
		stage.setAlwaysOnTop(true);
		startTime = System.currentTimeMillis();
		stage.show();

		Thread startupThread = new Thread(startupTask, "Startup-Thread");
		startupThread.setDaemon(true);
		startupThread.start();
	}
}

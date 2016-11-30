package net.filebot.ui;

import static net.filebot.Settings.*;

import java.awt.Desktop;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.filebot.ResourceManager;
import net.filebot.Settings;

public class GettingStartedStage {

	public static void start() {
		// initialize JavaFX
		new javafx.embed.swing.JFXPanel();
		javafx.application.Platform.setImplicitExit(false);

		Platform.runLater(() -> {
			Stage stage = new Stage();
			stage.setResizable(false);

			if (isMacApp()) {
				// Mac OS X specific configuration
				stage.initStyle(StageStyle.DECORATED);
				stage.initModality(Modality.NONE);
			} else {
				// Windows / Linux specific configuration
				stage.initStyle(StageStyle.UTILITY);
				stage.initModality(Modality.NONE);
				stage.getIcons().addAll(ResourceManager.getApplicationIconURLs().stream().map((url) -> new Image(url.toString())).collect(Collectors.toList()));
			}

			GettingStartedStage view = new GettingStartedStage(stage);
			view.show();
		});
	}

	private Stage stage;

	public GettingStartedStage(Stage stage) {
		this.stage = stage;

		WebView webview = new WebView();
		webview.getEngine().load(Settings.getEmbeddedHelpURI());
		webview.setPrefSize(750, 480);

		// intercept target _blank click events and open links in a new browser window
		webview.getEngine().setCreatePopupHandler((config) -> onPopup(webview));

		stage.setTitle("Getting Started");
		stage.setScene(new Scene(webview, webview.getPrefWidth(), webview.getPrefHeight(), Color.BLACK));

		// force black background while page is loading
		setBackground(webview.getEngine(), 0xFF000000);
	}

	public void show() {
		stage.show();
	}

	protected void setBackground(WebEngine engine, int color) {
		try {
			// use reflection to retrieve the WebEngine's private 'page' field
			Field f = engine.getClass().getDeclaredField("page");
			f.setAccessible(true);
			com.sun.webkit.WebPage page = (com.sun.webkit.WebPage) f.get(engine);
			page.setBackgroundColor(color);
		} catch (Exception e) {
			Logger.getLogger(GettingStartedStage.class.getName()).log(Level.WARNING, "Failed to set background", e);
		}
	}

	protected WebEngine onPopup(WebView webview) {
		// get currently select image via Galleria API
		Object link = webview.getEngine().executeScript("$('.galleria').data('galleria').getData().link");

		try {
			Desktop.getDesktop().browse(new URI(link.toString()));
		} catch (Exception e) {
			Logger.getLogger(GettingStartedStage.class.getName()).log(Level.WARNING, "Failed to browse URI", e);
		}

		// prevent current web view from opening the link
		return null;
	}

}

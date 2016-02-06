package simulizer.ui.theme;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class Themes implements Iterable<Theme> {
	private final String defaultTheme = "Default";
	private final Path folder = Paths.get("themes");
	private Set<Theme> themes = new HashSet<Theme>();
	private Theme theme = null;

	public Themes() {
		reload();
	}

	public void reload() {
		themes.clear();
		Gson g = new Gson();

		// Check all folders in the theme folder
		for (File themeFolder : folder.toFile().listFiles()) {
			if (themeFolder.isDirectory()) {
				// Check for a theme.json file
				File[] themeJSONs = themeFolder.listFiles((e) -> e.getName().toLowerCase().equals("theme.json"));
				if (themeJSONs.length == 1) {
					File themeJSON = themeJSONs[0];
					try (InputStream in = Files.newInputStream(themeJSON.toPath()); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
						Theme t = g.fromJson(new JsonReader(reader), Theme.class);
						t.location = themeFolder.toURI().toString();
						// @formatter:off
						try {
							// Selects the theme to start with (either default, or last selected) 
							if ((theme == null && t.getName().equals(defaultTheme)) || 
								(theme != null && t.getName().equals(theme.getName()))) 
									theme = t;
						// @formatter:on
							themes.add(t);
						} catch (NullPointerException e) {
							e.printStackTrace();
						}

					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public Iterator<Theme> iterator() {
		return themes.iterator();
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		for (Theme t : themes)
			if (t.getName().equals(theme)) this.theme = t;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

}

package demonstration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MainController {
	/*** Model ***/
	private InputTextModel i_model;
	private OntologyModel o_model;

	/*** View ***/
	private MainView view;

	public MainController(InputTextModel i, OntologyModel o) {
		i_model = i;
		o_model = o;

		// JavaFX試験運用
		// fxView = new MainFXView(this);
	}

	public void setMainView(MainView v) {
		this.view = v;
	}
	public InputTextModel getI_model() {
		return i_model;
	}
	public OntologyModel getO_model() {
		return o_model;
	}


	/** ActionListener **/
	/* RunGeneratorボタンの実装 */
	private ActionListener generateAction = (event -> {
		String text = view.getIptTextArea().getText();
		List<String[]> triples = i_model.runGenerator(text);
		o_model.addAllTriples(triples);
	});

	/* RandomTextボタンの実装 */
	private ActionListener importTextAction = (event -> {
		List<String> writings = new LinkedList<String>();

		Path path = Paths.get("writings/gooText生物-動物名-あ.txt");
		// List<String> strs = Files.readAllLines(path,
		// StandardCharsets.UTF_8); // ファイルが小さければこれでもいい
		try (Stream<String> stream = Files.lines(path, Charset.forName("UTF-8"))) {
			stream.forEach(line -> writings.add(line));
		} catch (IOException e) {
			System.out.println(e);
		}

		String text = writings.get(new Random().nextInt(writings.size()));
		view.getIptTextArea().setText(text);
	});

	public ActionListener getGenerateAction() {
		return generateAction;
	}
	public ActionListener getImportTextAction() {
		return importTextAction;
	}
}
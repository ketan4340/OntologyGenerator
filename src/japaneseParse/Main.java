package japaneseParse;

import demonstration.DocumentModel;
import demonstration.InputTextModel;
import demonstration.MainController;
import demonstration.MainView;
import demonstration.OntologyModel;

public class Main{
	public static void main(String[] args) {
		// JavaFX未使用
		new MainView(new MainController(new InputTextModel(), new OntologyModel(), new DocumentModel()));

		// JavaFX使用
		//MainFXView.launch(args);
	}
}
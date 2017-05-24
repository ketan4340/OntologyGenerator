package japaneseParse;

import demonstration.DocumentModel;
import demonstration.InputTextModel;
import demonstration.MainController;
import demonstration.MainView;
import demonstration.OntologyModel;

public class Main{
	public static void main(String[] args) {
		// FX未使用
		new MainView(new MainController(new InputTextModel(), new OntologyModel(), new DocumentModel()));

		// FX使用
		//MainFXView.launch(args);
	}
}
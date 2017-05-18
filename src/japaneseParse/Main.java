package japaneseParse;

import java.util.List;
import demonstration.*;

public class Main{
	public static void main(String[] args) {
		// FX未使用
		new MainView(new MainController(new InputTextModel(), new OntologyModel()));

		// FX使用
		//new MainFXView(new MainController()).launch(args);
	}
}
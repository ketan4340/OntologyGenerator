package demonstration;

import java.util.List;
import java.util.Observable;

import grammar.Sentence;
import japaneseParse.GenerateProcess;

public class MainModel extends Observable{
	private GenerateProcess process;
	private InputModel i_model;
	private OutputModel o_model;

	public MainModel() {
		process = new GenerateProcess();
		i_model = new InputModel();
		o_model = new OutputModel();
	}

	public InputModel getI_model() {
		return i_model;
	}
	public void setI_model(InputModel i_model) {
		this.i_model = i_model;
	}
	public OutputModel getO_model() {
		return o_model;
	}
	public void setO_model(OutputModel o_model) {
		this.o_model = o_model;
	}

// Generator実行
	public void runGenerator(String text) {
		process.run(text);
		setChanged();
        notifyObservers();
	}
}

package demonstration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Observer;

public class GenerateController implements ActionListener{
	private GenerateModel model;
	private GenerateView view;

	public GenerateController(GenerateModel model) {
		this.model = model;
	}

	public GenerateModel getModel() {
		return model;
	}
	public void setModel(GenerateModel model) {
		this.model = model;
	}

	public void setView(GenerateView view) {
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String text = view.getTxArea1().getText();
		model.runGenerator(text);
//		view.update(model, null);
	}
}

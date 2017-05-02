package demonstration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Observer;

import javax.swing.JTextArea;

public class GenerateController implements ActionListener{
	private GenerateModel model;
	private GenerateView view;
<<<<<<< HEAD

	private String inputText;
=======
>>>>>>> 814156a82d053888839f5ccbb3e816ec1889f266

	public GenerateController(GenerateModel model) {
		this.model = model;
		this.view = new GenerateView(this);
	}

	private String passText2Model(JTextArea ta) {
		return ta.getText();
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
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
<<<<<<< HEAD
		String text = "";
		System.out.println("text:\t"+text);
=======
		String text = view.getTxArea1().getText();
>>>>>>> 814156a82d053888839f5ccbb3e816ec1889f266
		model.runGenerator(text);
//		view.update(model, null);
	}
}

package demonstration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

public class GenerateController implements ActionListener{
	private GenerateModel model;
	private GenerateView view;

	private String inputText;

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

	@Override
	public void actionPerformed(ActionEvent e) {
		String text = "";
		System.out.println("text:\t"+text);
		model.runGenerator(text);
	}
}

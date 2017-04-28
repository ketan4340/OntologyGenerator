package demonstration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GenerateController implements ActionListener{
	private GenerateModel model;

	public GenerateController(GenerateModel model) {
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String text = e.getActionCommand();
		System.out.println("text:\t"+text);
		model.runGenerator(text);
	}
}

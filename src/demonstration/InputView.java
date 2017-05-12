package demonstration;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class InputView extends JFrame implements Observer {
	private InputModel i_model;
	private OutputModel o_model;

	private MainController controller;

	private JLabel l;
	private JButton runGeneratorBt, randomTextBt;
	private JPanel inputPanel, outputPanel;
	private JScrollPane scrollpane;
	private JTextArea txtArea;

	public InputView(MainController controller) {
		this.controller = controller;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO 自動生成されたメソッド・スタブ

	}

}

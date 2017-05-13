package demonstration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

public class MainView extends JFrame implements Observer{
	/* Model */
	private InputModel i_model;
	private OutputModel o_model;
	/* Controller */
	private MainController controller;

	/* Input parts */
	private JScrollPane i_scrollpane;
	private JTextArea txtArea;
	private JButton runGeneratorBt, randomTextBt;
	/* Output parts */
	private JScrollPane o_scrollpane;
	private JTable tb;

	public MainView(MainController controller) {
		super("OntologyGenerator");

		this.controller = controller;
		controller.setView(this);
		i_model = controller.getI_model();
		o_model = controller.getO_model();

		designWholeFrame();
	    setVisible(true);	// 表示
	}

	private void designWholeFrame() {
		//this.setSize(1200,800);
		setExtendedState(JFrame.MAXIMIZED_BOTH);		// 画面全体の半分のサイズ
		setLocationRelativeTo(null);					// フレームを中央に表示
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// ウインドウを閉じたら終了
		Container contentPane = getContentPane();

		designInputFrame();
		designOutputFrame();

	    runGeneratorBt = new JButton("オントロジー構築");
	    runGeneratorBt.addActionListener(controller);
	    randomTextBt = new JButton("ランダムテキスト");
	    randomTextBt.addActionListener(controller);

	    contentPane.add(runGeneratorBt, BorderLayout.WEST);
	    contentPane.add(randomTextBt, BorderLayout.EAST);
	}

	private void designInputFrame() {
		/*
		inputPanel = new JPanel();
		inputPanel.setBackground(Color.WHITE);
	    inputPanel.setSize(inputPanel.getMaximumSize());
		 */
	    txtArea = new JTextArea();
	    txtArea.setLineWrap(true);
	    i_scrollpane = new JScrollPane(txtArea);
		//i_scrollpane.setPreferredSize(i_scrollpane.getMaximumSize());

	    add(i_scrollpane, BorderLayout.NORTH);
	}
	private void designOutputFrame() {
		tb = new JTable(o_model.getTableModel());
		o_scrollpane = new JScrollPane(tb);
		//o_scrollpane.setPreferredSize(o_scrollpane.getMaximumSize());

		//outputPanel.add(o_scrollpane);
	    add(o_scrollpane, BorderLayout.SOUTH);
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


	@Override
	public void update(Observable obs, Object arg) {
		o_model = (OutputModel) obs;

		for(String[] triple: o_model.getTriples()) {
			for(String concept: triple) {
				designOutputFrame();
			}
		}
	}

	public JButton getRunGeneratorBt() {
		return runGeneratorBt;
	}
	public JTextArea getTxArea1() {
		return txtArea;
	}
	public JButton getRandomTextBt() {
		return randomTextBt;
	}
}

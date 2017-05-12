package demonstration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
	private JPanel inputPanel;
	private JTextArea txtArea;
	private JButton runGeneratorBt, randomTextBt;
	/* Output parts */
	private JPanel outputPanel;
	private JScrollPane scrollpane;
	private JTable tb;

	public MainView(MainController controller) {
		super("OntologyGenerator");
		this.controller = controller;
		controller.setView(this);
		i_model = controller.getI_model();
		o_model = controller.getO_model();

		designWholeFrame();
	    this.setVisible(true);
	}

	private void designWholeFrame() {
		//this.setSize(1200,800);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH / 2);	// 画面全体の半分のサイズ
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		designInputFrame();
		designOutputFrame();

	    runGeneratorBt = new JButton("オントロジー構築");
	    runGeneratorBt.addActionListener(controller);
	    randomTextBt = new JButton("ランダムテキスト");
	    randomTextBt.addActionListener(controller);

	    this.add(runGeneratorBt, BorderLayout.WEST);
	    this.add(randomTextBt, BorderLayout.EAST);
	}

	private void designInputFrame() {
		inputPanel = new JPanel();
		inputPanel.setSize(1200, 300);
	    inputPanel.setBackground(Color.WHITE);
	    txtArea = new JTextArea(10, 100);
	    txtArea.setLineWrap(true);
	    inputPanel.add(txtArea);

	    this.add(inputPanel, BorderLayout.NORTH);
	}
	private void designOutputFrame() {
		outputPanel = new JPanel();
		outputPanel.setLayout(new GridLayout(0, 3));
		outputPanel.setSize(1200, 300);
		outputPanel.setBackground(Color.WHITE);
		scrollpane = new JScrollPane(outputPanel);
		scrollpane.setPreferredSize(new Dimension(1200, 500));

		LineBorder border = new LineBorder(Color.BLACK, 2, true);
		JLabel s = new JLabel("Subject");
		s.setForeground(Color.CYAN);
		s.setBorder(border);
		s.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		JLabel p = new JLabel("Predicate");
		p.setForeground(Color.RED);
		p.setBorder(border);
		p.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		JLabel o = new JLabel("Object");
		o.setForeground(Color.CYAN);
		o.setBorder(border);
		o.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		outputPanel.add(s);
		outputPanel.add(p);
	    outputPanel.add(o);

	    this.add(scrollpane, BorderLayout.SOUTH);
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
	public JButton getRunGeneratorBt() {
		return runGeneratorBt;
	}
	public void setRunGeneratorBt(JButton runGeneratorBt) {
		this.runGeneratorBt = runGeneratorBt;
	}
	public JButton getRandomTextBt() {
		return randomTextBt;
	}
	public void setRandomTextBt(JButton randomTextBt) {
		this.randomTextBt = randomTextBt;
	}
	public JPanel getPn1() {
		return inputPanel;
	}
	public void setPn1(JPanel pn1) {
		this.inputPanel = pn1;
	}
	public JPanel getPn2() {
		return outputPanel;
	}
	public void setPn2(JPanel pn2) {
		this.outputPanel = pn2;
	}
	public JTextArea getTxArea1() {
		return txtArea;
	}
	public void setTxArea1(JTextArea txArea1) {
		this.txtArea = txArea1;
	}

	@Override
	public void update(Observable obs, Object arg) {
		o_model = (OutputModel) obs;

		for(String[] triple: o_model.getTriples()) {
			for(String concept: triple) {
				JLabel c = new JLabel(concept);
			    c.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
			    outputPanel.add(c);
			}
		}
	}
}

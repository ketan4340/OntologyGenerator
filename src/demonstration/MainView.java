package demonstration;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class MainView extends JFrame implements Observer{
	/*** Model ***/
	private InputModel i_model;
	private OutputModel o_model;

	/*** Controller ***/
	private MainController controller;

	/*** View ***/
	/** Whole part **/
	private JSplitPane splitpane;

	/* InputText parts */
	private JPanel pn_ipt;
	private JScrollPane scrollpane_ipt;
	private JTextArea txtArea;
	private JButton runGeneratorBt, randomTextBt;
	/* Ontology parts */
	private JPanel pn_ont;
	private JScrollPane scrollpane_ont;
	private JTable tb;
	/* Document parts */
	private JPanel pn_doc;
	private JScrollPane scrollpane_doc;
	private JEditorPane editorpane;

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
		//setSize(1200,800);
		setExtendedState(JFrame.MAXIMIZED_BOTH);		// 画面全体の半分のサイズ
		setLocationRelativeTo(null);					// フレームを中央に表示
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// ウインドウを閉じたら終了
		setLayout(new GridLayout(2, 1));

		designInputFrame();
		designOutputFrame();

	    //contentPane.add(runGeneratorBt, BorderLayout.WEST);
	    //contentPane.add(randomTextBt, BorderLayout.EAST);
	}

	private void designInputFrame() {
		JPanel pn_menu = new JPanel();
		pn_menu.setLayout(new BoxLayout(pn_menu, BoxLayout.LINE_AXIS));	// 配置順を左から右に
	    randomTextBt = new JButton("インポート");
	    randomTextBt.addActionListener(controller);
		pn_menu.add(randomTextBt);
		pn_menu.add(new JLabel("設定"));
		pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		pn_menu.add(new JLabel("クリア"));

	    txtArea = new JTextArea();
	    txtArea.setLineWrap(true);
	    scrollpane_ipt = new JScrollPane(txtArea);

	    pn_ipt = new JPanel(new BorderLayout());
	    pn_ipt.add(pn_menu, BorderLayout.NORTH);
	    pn_ipt.add(scrollpane_ipt, BorderLayout.CENTER);

	    add(pn_ipt);
	}
	private void designOutputFrame() {
		JPanel pn_menu = new JPanel();
		pn_menu.setLayout(new BoxLayout(pn_menu, BoxLayout.LINE_AXIS));	// 配置順を左から右に
	    runGeneratorBt = new JButton("オントロジー構築");
	    runGeneratorBt.addActionListener(controller);
	    pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		pn_menu.add(runGeneratorBt);
		pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		pn_menu.add(new JLabel("クリア"));

		tb = new JTable(o_model.getTableModel());
		scrollpane_ont = new JScrollPane(tb);

		pn_ont = new JPanel(new BorderLayout());
		pn_ont.add(pn_menu, BorderLayout.NORTH);
		pn_ont.add(scrollpane_ont, BorderLayout.CENTER);

	    add(pn_ont);
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

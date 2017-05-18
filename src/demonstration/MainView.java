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
	private InputTextModel inputTextModel;
	private OntologyModel ontologyModel;

	/*** Controller ***/
	//private MainController controller;

	/*** View ***/
	/** Whole part **/
	private JSplitPane splitpane;

	/* InputText parts */
	private JPanel iptPanel;
	private JScrollPane iptScrollpane;
	private JTextArea iptTextArea;
	private JButton generateBt, importBt;
	/* Ontology parts */
	private JPanel ontPanel;
	private JScrollPane ontScrollpane;
	private JTable ontTable;
	/* Document parts */
	private JPanel docPanel;
	private JScrollPane docScrollpane;
	private JEditorPane editorpane;

	public MainView(MainController controller) {
		super("OntologyGenerator");
		// Modelを参照のために保持する
		inputTextModel = controller.getI_model();
		ontologyModel = controller.getO_model();

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
	}

	private void designInputFrame() {
		JPanel pn_menu = new JPanel();
		pn_menu.setLayout(new BoxLayout(pn_menu, BoxLayout.LINE_AXIS));	// 配置順を左から右に
	    importBt = new JButton("インポート");
		pn_menu.add(importBt);
		pn_menu.add(new JLabel("設定"));
		pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		pn_menu.add(new JLabel("クリア"));

	    iptTextArea = new JTextArea();
	    iptTextArea.setLineWrap(true);
	    iptScrollpane = new JScrollPane(iptTextArea);

	    iptPanel = new JPanel(new BorderLayout());
	    iptPanel.add(pn_menu, BorderLayout.NORTH);
	    iptPanel.add(iptScrollpane, BorderLayout.CENTER);

	    add(iptPanel);
	}
	private void designOutputFrame() {
		JPanel pn_menu = new JPanel();
		pn_menu.setLayout(new BoxLayout(pn_menu, BoxLayout.LINE_AXIS));	// 配置順を左から右に
	    generateBt = new JButton("オントロジー構築");
	    pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		pn_menu.add(generateBt);
		pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		pn_menu.add(new JLabel("クリア"));

		ontTable = new JTable(ontologyModel);
		ontScrollpane = new JScrollPane(ontTable);

		ontPanel = new JPanel(new BorderLayout());
		ontPanel.add(pn_menu, BorderLayout.NORTH);
		ontPanel.add(ontScrollpane, BorderLayout.CENTER);

	    add(ontPanel);
	}

	public void setInputTextModel(InputTextModel i_model) {
		this.inputTextModel = i_model;
	}
	public void setOntologyModel(OntologyModel o_model) {
		this.ontologyModel = o_model;
	}
	public void setModels(InputTextModel i_model, OntologyModel o_model) {
		setInputTextModel(i_model);
		setOntologyModel(o_model);
	}

	@Override
	public void update(Observable obs, Object arg) {

	}

	public JButton getGenerateBt() {
		return generateBt;
	}
	public JTextArea getIptTextArea() {
		return iptTextArea;
	}
	public JButton getImportBt() {
		return importBt;
	}
}

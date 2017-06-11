package demo;

import javax.jws.soap.SOAPBinding.Use;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SubOntologyView extends JPanel{
	/**** Controller ****/
	//private UseOntologyController useController;

	/**** View ****/
	private JTabbedPane tabbedpane;

	private OntologyTableView ontTable;
	private OntologyGraphView ontGraph;

	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	public SubOntologyView(UseOntologyController uCtrl) {
		super();

		// タブペインの初期化
		tabbedpane = new JTabbedPane();

		// 表の初期化
		ontTable = new OntologyTableView(uCtrl);

		// グラフの初期化
		ontGraph = new OntologyGraphView(uCtrl);

		tabbedpane.addTab("table", ontTable);
		tabbedpane.addTab("graph", ontGraph);

		add(tabbedpane);
	}
}

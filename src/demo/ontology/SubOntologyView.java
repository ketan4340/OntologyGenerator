package demo.ontology;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import demo.controller.UseOntologyController;
import demo.graph.OntologyGraphView;

public class SubOntologyView extends JPanel{
	private static final long serialVersionUID = 8844843627880177575L;


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

		// 表の初期化
		ontTable = new OntologyTableView(uCtrl);

		// グラフの初期化
		ontGraph = new OntologyGraphView(uCtrl);

		// タブペインの初期化
		tabbedpane = new JTabbedPane();
		tabbedpane.addTab("表", ontTable);
		tabbedpane.addTab("グラフ", ontGraph);
		add(tabbedpane);
	}
}
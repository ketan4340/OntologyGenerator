package demo.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import demo.controller.UseOntologyController;
import demo.ontology.OntologyModel;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class OntologyGraphView extends JPanel{
	/**** Model ****/
	private OntologyModel ontModel;

	/**** Controller ****/
	//private UseOntologyController useController;

	/**** View ****/
	private MyGraph graphPanel;


	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	public OntologyGraphView(UseOntologyController uCtrl) {
		super(new BorderLayout());
		// controllerにこのviewインスタンスを持たせる
		uCtrl.setSubOntologyGraph(this);

		// Modelを参照のために保持する
		ontModel = uCtrl.getSubOntlogyModel();

		// グラフ部分のサイズ指定
		Dimension area = new Dimension(300, 300);
		graphPanel = new MyGraph(ontModel, area);

		this.add(new JScrollPane(graphPanel), BorderLayout.CENTER);
	}
}

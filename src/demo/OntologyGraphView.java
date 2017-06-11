package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
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

		Dimension area = new Dimension(300, 300);

		graphPanel = new MyGraph(ontModel, area);

		//this.add(new JScrollPane(graphPanel), BorderLayout.CENTER);
		this.add(graphPanel, BorderLayout.CENTER);
	}

	public void resetGraph() {
		graphPanel.setGraphLayout(graphPanel.layout);
	}
}

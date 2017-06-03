package demonstration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
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
	private UseOntologyController controller;

	/**** View ****/


	public OntologyGraphView(UseOntologyController ctrl) {
		this.controller = ctrl;
		// controllerにこのviewインスタンスを持たせる
		//controller.setMainView(this);

		// Modelを参照のために保持する
		ontModel = controller.getOntModel();
		// modelのオブザーバーにこのviewを追加

		String clickedWord = "";

		List<String[]> relationList = new LinkedList<String[]>();	// クリックした単語をSかOに含むトリプルを集める
		relationList.addAll(ontModel.getPO(clickedWord));
		relationList.addAll(ontModel.getSP(clickedWord));

		// 選び出したオントロジーだけでモデル化
		OntologyModel selectedOnt = new OntologyModel();
		selectedOnt.addAllTriples(relationList);

		// グラフ表示
		Graph<MyNode, MyEdge> graph = selectedOnt.createGraph();

		Layout<MyNode, MyEdge> layout = new KKLayout<MyNode, MyEdge>(graph);

		BasicVisualizationServer<MyNode, MyEdge> graphPanel =
				new BasicVisualizationServer<MyNode, MyEdge>(layout,new Dimension(500, 500));

		graphPanel.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<MyNode>());
		graphPanel.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<MyEdge>());
		// エッジを直線に
		graphPanel.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<MyNode, MyEdge>());




		this.add(new JScrollPane(graphPanel));
	}

}

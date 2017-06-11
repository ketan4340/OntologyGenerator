package demo;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;


/** TableModelクラス(3列限定)をモデルとして受け取り、グラフを生成する **/
public class MyGraph extends BasicVisualizationServer<MyNode, MyEdge> implements TableModelListener{
	/**** Model ****/
	private final OntologyModel ontModel;

	Graph<MyNode, MyEdge> graphModel;
	Layout<MyNode, MyEdge> layout;

	public MyGraph(OntologyModel om, Dimension area) {
		// とりあえず空のグラフで初期化した後
		//super(new KKLayout<MyNode, MyEdge>(new DirectedSparseGraph<MyNode, MyEdge>()), area);
		super(new KKLayout<MyNode, MyEdge>(ontology2Graph(om)), area);

		this.ontModel = om;
		// ontModelに私のこと見て(ハート)って伝える
		om.addTableModelListener(this);

		/*
		// OntologyModelに基づいて生成したグラフを渡す
		this.graphModel = ontology2Graph(om);
		this.layout = new KKLayout<MyNode, MyEdge>(graphModel);
		this.setGraphLayout(layout);
		*/

		this.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<MyNode>());
		this.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<MyEdge>());
		// エッジを直線に
		this.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<MyNode, MyEdge>());
	}

	public static Graph<MyNode, MyEdge> ontology2Graph(OntologyModel ontology) {
		Graph<MyNode, MyEdge> g = new DirectedSparseGraph<MyNode, MyEdge>();
		List<String[]> table = ontology.getAllTable();
		Map<String, MyNode> nodeMap = new HashMap<String, MyNode>();	// ノードの重複を許さない
		for(String[] triple : table) {
			String subject = triple[0], object = triple[2];
			MyNode node_s = (nodeMap.containsKey(subject))
					? nodeMap.get(subject)	// すでにノードが作られていればそれを使い
					: new MyNode(subject);	// 無ければ新しく作る
			MyNode node_o = (nodeMap.containsKey(object))
					? nodeMap.get(object)
					: new MyNode(object);

			g.addEdge(new MyEdge(triple[1]), node_s, node_o);

			nodeMap.put(triple[0], node_s);
			nodeMap.put(triple[2], node_o);

			System.out.println(triple[0] + triple[1] + triple[2]);
		}
		return g;
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		// グラフ再構築
		// 毎回ゼロから作り直す
		this.graphModel = ontology2Graph(ontModel);
		System.out.println("tableChanged");
	}
}

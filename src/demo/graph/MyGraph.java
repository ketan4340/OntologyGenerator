package demo.graph;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import demo.OntologyModel;
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

	public MyGraph(OntologyModel om, Dimension area) {
		super(new KKLayout<MyNode, MyEdge>(ontology2Graph(om)), area);

		this.ontModel = om;

		om.addTableModelListener(this);	// ontModelに「何かあったら私に教えてよ」って伝える

		// ノードのラベルを表示
		this.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<MyNode>());
		// エッジのラベルを表示
		this.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<MyEdge>());
		// エッジを直線に
		this.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<MyNode, MyEdge>());
	}

	public static Graph<MyNode, MyEdge> ontology2Graph(OntologyModel ontology) {
		Graph<MyNode, MyEdge> g = new DirectedSparseGraph<MyNode, MyEdge>();
		List<String[]> table = ontology.getAllTable();
		Map<String, MyNode> nodeMap = new HashMap<String, MyNode>();	// ノードの重複を許さない
		for(String[] triple : table) {
			String subject = triple[0], predicate = triple[1], object = triple[2];
			MyNode node_s = (nodeMap.containsKey(subject))
					? nodeMap.get(subject)	// すでにノードが作られていればそれを使い
					: new MyNode(subject);	// 無ければ新しく作る
			MyNode node_o = (nodeMap.containsKey(object))
					? nodeMap.get(object)
					: new MyNode(object);

			g.addEdge(new MyEdge(predicate), node_s, node_o);

			nodeMap.put(subject, node_s);	// 同じものがあっても上書きなのでOK
			nodeMap.put(object, node_o);
		}
		return g;
	}

	// OntologyModel(extends TableModel)に変更があったとき呼び出される
	@Override
	public void tableChanged(TableModelEvent e) {
		// 再構築したグラフをセットし直す
		Layout<MyNode, MyEdge> newLayout = new KKLayout<MyNode, MyEdge>(ontology2Graph(ontModel));
		this.setGraphLayout(newLayout);
		System.out.println("tableChanged");
	}
}

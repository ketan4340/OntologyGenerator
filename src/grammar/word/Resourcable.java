package grammar.word;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import data.RDF.rule.JassModelizable;
import data.RDF.vocabulary.JASS;

public interface Resourcable extends JassModelizable {
	Model CATEGOREM_RSRC_POOL = ModelFactory.createDefaultModel();

	/**
	 * 自立語リソースのURI.
	 * @return 自立語リソースのURI
	 */
	String resourceURI();
	/**
	 * このオブジェクトを表すリソースを指定のモデルによって生成する. 
	 * @param m 生成するリソースが属するモデル
	 * @return このオブジェクトを表すリソース
	 */
	Resource createResource(Model m);
	
	/**
	 * 代理ノードのURI.
	 * @return 代理ノードのURI
	 */
	String proxyNodeURI();
	/**
	 * 指定のモデルに属する代理ノードを得る. 
	 * すでに代理ノードが存在すればそれをモデルに追加してから返し、
	 * なければ代理ノードに連なる具体化した自立語リソースの生成も行う.
	 * @param jassModel 代理ノードが属するモデル
	 * @return 代理ノード
	 */
	default Bag createProxyNode(Model jassModel) {
		Bag proxyNode = CATEGOREM_RSRC_POOL.createBag(proxyNodeURI());
		// すでに存在するかは、(代理ノード, rdf:type, JASS:Meaning)で確認
		if (CATEGOREM_RSRC_POOL.contains(proxyNode, RDF.type, JASS.Meaning))
			return proxyNode;

		Model crModel = ModelFactory.createDefaultModel();
		Resource cr = createResource(crModel);
		
		crModel.listStatements().toList().stream()	// 自立語リソースの全ステートメントを
		.map(CATEGOREM_RSRC_POOL::createReifiedStatement)	// 具体化し
		.forEach(proxyNode::add);					// RDFコンテナになっている代理ノードに追加する
		proxyNode.addProperty(JASS.coreNode, cr).addProperty(RDF.type, JASS.Meaning);
		crModel.close();
		return proxyNode;
	}

	String FORMER_QUERY_STRING = 
			"PREFIX rdf: <"+ RDF.getURI() +"> " +
			"PREFIX rdfs: <"+ RDFS.getURI() +"> " +
			"CONSTRUCT {?s ?p ?o .}" +
			"WHERE {"; 
	String LATTER_QUERY_STRING = 
			" rdf:type rdf:Bag ; " + 
			" rdfs:member [rdf:subject ?s ; rdf:predicate ?p ; rdf:object ?o].}";
	
	static Model categoremResourcesOf(Resource proxyNode) {
		Query query = QueryFactory.create(FORMER_QUERY_STRING + '<'+proxyNode.getURI()+'>' + LATTER_QUERY_STRING);
		QueryExecution qexec = QueryExecutionFactory.create(query, CATEGOREM_RSRC_POOL);
		return qexec.execConstruct();
	}
	static Resource coreNodeOf(Resource proxyNode) {
		return CATEGOREM_RSRC_POOL.listObjectsOfProperty(proxyNode, JASS.coreNode)
				.next().asResource();
	}
	
}

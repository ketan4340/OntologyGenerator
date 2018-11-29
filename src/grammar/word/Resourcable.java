package grammar.word;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import data.RDF.rule.JassModelizable;
import data.RDF.vocabulary.JASS;

public interface Resourcable extends JassModelizable {
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
	 * 指定のモデル内に代理ノードを生成する.
	 * @param m 生成に使用するモデル
	 * @return 代理ノード
	 */
	default Resource createProxyNode(Model m) {
		return m.createResource(proxyNodeURI());
	}

	Model CATEGOREM_RESOURCES = ModelFactory.createDefaultModel();
	String FORMER_QUERY_STRING = 
			"PREFIX rdf: <"+ RDF.getURI() +'>' +
			"PREFIX rdfs: <"+ RDFS.getURI() +'>' +
			"CONSTRUCT {?s ?p ?o .}" +
			"WHERE {<"; 
	String LATTER_QUERY_STRING = 
			"> a rdf:Bag ; " + 
			"rdfs:member " +
			"[rdf:subject ?s ; rdf:predicate ?p ; rdf:object ?o].}";
	
	default Model findCategoremResourceFromNode() {
		String nodeString = proxyNodeURI();
		Query query = QueryFactory.create(FORMER_QUERY_STRING + nodeString + LATTER_QUERY_STRING);
		QueryExecution qexec = QueryExecutionFactory.create(query, CATEGOREM_RESOURCES);
		Model resultModel = qexec.execConstruct();
		
		return resultModel;
	}
	
	static Bag createReifiedStatementsBag(StmtIterator iter) {
		Model model = ModelFactory.createDefaultModel();
		Bag reifiedBag = model.createBag();
		while (iter.hasNext()) {
			Statement s = iter.nextStatement(); 
			ReifiedStatement rs = s.createReifiedStatement();
			reifiedBag.add(rs);
		}
		return reifiedBag;
	}
	
	static void replaceProxy2CategoremResource(Model m) {
		StmtIterator iter = m.listStatements(null, JASS.means, (RDFNode) null);
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource s = stmt.getSubject();
			Resource pnode = stmt.getObject().asResource();
		}
	}
	
	default Resource getProxyNode(Model m) {
		Resource n = createProxyNode(m);
		Model rm = ModelFactory.createDefaultModel();
		Resource r = createResource(rm);
		Bag bag = createReifiedStatementsBag(rm.listStatements());
		CATEGOREM_RESOURCES.add(n, RDFS.seeAlso, r);
		return n;
	}
}

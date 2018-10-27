package modules.RDFConvert;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class EntityLinker {
	private String sparqlEndpoint;
	
	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public EntityLinker(String sparqlEndpoint) {
		this.sparqlEndpoint = sparqlEndpoint;
	}
	
	/* ================================================== */
	/* =================== Member Method ================ */
	/* ================================================== */
	public void linkEntity(Model m) {
		// rdfs:labelを持つリソース
		StmtIterator rsc_itr = m.listStatements(null, RDFS.label, (RDFNode) null);
		while (rsc_itr.hasNext()) {
			Statement stmt = rsc_itr.nextStatement();
			Resource s = stmt.getSubject();
			String label = stmt.getObject().toString();
			Query query = createConstructQuery(s, label);
			QueryExecution qe = QueryExecutionFactory.sparqlService(sparqlEndpoint, query);
			qe.execConstruct(m);
		}
	}
	
	/* ================================================== */
	/* ================== Static Method ================= */
	/* ================================================== */
	private static Query createConstructQuery(Resource r, String label) {
		return QueryFactory.create("" +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
				"CONSTRUCT {<" + r +"> <"+ OWL.sameAs + "> ?x} " +
				"WHERE {?x rdfs:label \""+ label +"\"@ja} " + 
				"LIMIT 100");
	}

}

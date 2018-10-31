package modules.RDFConvert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import data.RDF.vocabulary.JASS;
import data.id.ModelIDMap;

public class EntityLinker {
	private List<String> sparqlEndpoints;
	
	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public EntityLinker(List<String> sparqlEndpoint) {
		this.sparqlEndpoints = sparqlEndpoint;
	}
	
	/* ================================================== */
	/* =================== Member Method ================ */
	/* ================================================== */
	/**
	 * 旧型
	 * @param m オントロジー
	 */
	public void executeBySameLabelIdentification(Model m) {
		// rdfs:labelを持つリソース
		StmtIterator rsc_itr = m.listStatements(null, RDFS.label, (RDFNode) null);
		while (rsc_itr.hasNext()) {
			Statement stmt = rsc_itr.nextStatement();
			Resource s = stmt.getSubject();
			String label = stmt.getObject().toString();
			Query query = QueryFactory.create("" +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" + 
					"CONSTRUCT {<" + s +"> <"+ OWL.sameAs + "> ?x} " +
					"WHERE {?x rdfs:label|skos:prefLabel \""+ label +"\"@ja} " + 
					"LIMIT 100");;
			sparqlEndpoints.forEach(se -> {
				QueryExecution qe = QueryExecutionFactory.sparqlService(se, query);
				qe.execConstruct(m);
			});
		}
	}
	
	/**
	 * JASSモデルを元にエンティティリンキングする
	 * @param JASSモデルとIDのMap
	 * @return {@code owl:sameAs}でリンクしたRDFグラフ
	 */
	public Model linkEntityWithCoexistentWords(ModelIDMap JASSMap) {
		Model jass = JASSMap.uniteModels();
		Map<String, Map<Resource, String>> stc_rsc_labelMap = 
				collectSentence_Resource_LabelMap(jass);
		Model sameAsOntology = ModelFactory.createDefaultModel();
		Set<Model> ontologies = stc_rsc_labelMap.entrySet().parallelStream()
				// 以下、終端処理までスレッドセーフか注意
				.map(Map.Entry::getValue)
				.map(this::linkEntityAtSentence)
				.collect(Collectors.toSet());
		ontologies.forEach(sameAsOntology::add);
		return sameAsOntology;
	}
	
	
	public Model linkEntityAtSentence(Map<Resource, String> resource_labelMap) {
		// リソース毎のリンク候補集合を初期化
		resource_labelMap.entrySet()
		.parallelStream()
		.forEach(entry -> {
			Resource coreResource = entry.getKey();
			String label = entry.getValue();
			Query query = createConstructQuery(label);
			sparqlEndpoints.forEach(se -> {
				QueryExecution qexec = QueryExecutionFactory.sparqlService(se, query);
				ResultSet results = qexec.execSelect();
				results.forEachRemaining(qs -> {
					Resource stc = qs.get("x").asResource();
				});
			});
		});
		
		return null;//TODO
	}
	
	/* ================================================== */
	/* ================== Static Method ================= */
	/* ================================================== */
	private static Query createConstructQuery(String label) {
		return QueryFactory.create("" +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
				"SELECT *" +
				"WHERE {?x rdfs:label \""+ label +"\"@ja} " + 
				"LIMIT 100");
	}
	

	private static final Query SENTENCE_RESOURCE_LABEL_QUERY = QueryFactory.create(""+
			"PREFIX rdf: <"+ RDF.getURI() +"> "+
			"PREFIX jass: <"+ JASS.getURI() +"> "+
			"SELECT * "+
			"WHERE {"+
			"?stc rdf:type jass:Sentence ; "+
				"jass:consistsOfClauses/rdf:rest*/rdf:first/jass:consistsOfCategorem "+
					"[jass:means ?rsc] , "+
					"[jass:infinitive ?label] .}");
	private static Map<String, Map<Resource, String>> collectSentence_Resource_LabelMap(Model m) {
		QueryExecution qexec = QueryExecutionFactory.create(SENTENCE_RESOURCE_LABEL_QUERY, m);
		ResultSet results = qexec.execSelect();
		
		// Sentence毎の自立語のリソースに対応したラベルとのマッピング
		Map<String, Map<Resource, String>> stc_rsc_labelMap = new HashMap<>();
		results.forEachRemaining(qs -> {
			Resource stc = qs.get("stc").asResource();
			Resource rsc = qs.get("rsc").asResource();
			String label = qs.get("label").asLiteral().getString();
			String stckey = stc.getURI();
			stc_rsc_labelMap.computeIfAbsent(stckey, v -> new HashMap<>()).put(rsc, label);
		});
		return stc_rsc_labelMap;
	}

}

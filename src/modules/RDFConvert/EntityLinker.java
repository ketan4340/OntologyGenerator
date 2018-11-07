package modules.RDFConvert;

import java.util.HashMap;
import java.util.HashSet;
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
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import data.RDF.vocabulary.JASS;
import data.id.ModelIDMap;

public class EntityLinker {
	private List<String> sparqlEndpoints;
	private int maxSizeOfINstatement;
	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public EntityLinker(List<String> sparqlEndpoint, int maxSizeOfINstatement) {
		this.sparqlEndpoints = sparqlEndpoint;
		this.maxSizeOfINstatement = maxSizeOfINstatement;
	}
	
	/* ================================================== */
	/* =================== Member Method ================ */
	/* ================================================== */
	/**
	 * 指定のモデル中のリソースを、DBpedia、DBpedia Japanese上で同じラベルをもつリソースと{@code owl:sameAs}プロパティで結び、モデルに追加する.
	 * @param m 生成した{@code owl:sameAs}トリプルを追加したいモデル
	 */
	public void executeBySameLabelIdentification(Model m) {
		// JASSモデル内でrdfs:labelを持つリソース
		Map<Resource, String> rsc_labelMap = new HashMap<>();
		StmtIterator labelStmtItr = m.listStatements(null, RDFS.label, (RDFNode) null);
		labelStmtItr.forEachRemaining(stmt -> rsc_labelMap.put(stmt.getSubject(), stmt.getObject().toString()));
		
		Set<Map<String, Resource>> label_rscMaps = splitReverseMapWithoutOverlapUnderSize(rsc_labelMap, maxSizeOfINstatement);
		label_rscMaps.forEach(lrMap -> {
			if (lrMap.isEmpty()) return;
			// ラベルを結合した文字列
			String inExprStr = lrMap.keySet().stream().map(s -> s.replaceAll(",", "\\,"))
					.map(s -> '"'+s+'"'+"@ja").collect(Collectors.joining(","));
			Query query = QueryFactory.create(""
					+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " 
					+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " 
					+ "SELECT ?x ?label "
					+ "WHERE {?x rdfs:label|skos:prefLabel ?label . "
					+ "FILTER(?label IN(" + inExprStr + ")) } ");
			sparqlEndpoints.forEach(se -> {
				QueryExecution qe = QueryExecutionFactory.sparqlService(se, query);
				ResultSet results = qe.execSelect();
				System.out.println("finish sparql. got results. : " + results.hasNext());//PRINT
				results.forEachRemaining(qs -> {
					Resource dbRsc = qs.getResource("x");
					String label = qs.getLiteral("label").getString();
					Resource jassRsc = lrMap.get(label);	// ラベルからJASSリソースを逆引き
					m.add(jassRsc, OWL.sameAs, dbRsc);		// JASSリソースとDBpediaのリソースをsameAsリンクして格納
				});
				qe.close();
			});

		});
	}
	/**
	 * {@code Map<K,V>}から{@code Map<V, K>}の集合へ、キー(元のマップの値)の重複がなく、かつ1つのマップが指定サイズ未満になるように分割する. 
	 * @param map キー・値を逆転し分割したいマップ
	 * @param size 分割後の{@code Map<V, K>}1つあたりの上限サイズ
	 * @return 分割された{@code Map<V, K>}の集合
	 */
	private static <K, V> Set<Map<V, K>> splitReverseMapWithoutOverlapUnderSize(Map<K, V> map, int maxSize) {
		Set<Map<V, K>> mapset = new HashSet<>();
		map.entrySet().forEach(e -> {
			K key = e.getKey();
			V value = e.getValue();
			mapset.parallelStream()
			.filter(m -> !m.containsKey(value))	// キーが重複せず
			.filter(m -> m.size()<maxSize)		// 分割後のサイズが指定量未満
			.findAny()							// 条件に合えばどれでもいい
			.orElseGet(() -> {
				Map<V, K> newmap = new HashMap<>();
				mapset.add(newmap);
				return newmap;
			})	// なければ新しく作る
			.put(value, key);					// 値とキーを逆にしてプット
		});
		return mapset;
	}
	
	/**
	 * リソース1つごとにエンドポイントにクエリを投げて、同じラベルを持つリソースを探す. 
	 * @param m オントロジー
	 */
	public void executeBySameLabelIdentification_old(Model m) {
		// rdfs:labelを持つリソース
		StmtIterator rsc_itr = m.listStatements(null, RDFS.label, (RDFNode) null);
		rsc_itr.forEachRemaining(stmt -> {
			Resource s = stmt.getSubject();
			String label = stmt.getObject().toString();
			Query query = QueryFactory.create(""
					+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " 
					+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" 
					+ "CONSTRUCT {<" + s +"> <"+ OWL.sameAs + "> ?x} "
					+ "WHERE {?x rdfs:label|skos:prefLabel \""+ label +"\"@ja . "
					+ "FILTER(?x IN()) "
					+ "} " 
					+ "LIMIT 100");
			sparqlEndpoints.forEach(se -> {
				QueryExecution qe = QueryExecutionFactory.sparqlService(se, query);
				qe.execConstruct(m);
			});
		});
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

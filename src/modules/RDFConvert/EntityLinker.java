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
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import data.RDF.vocabulary.JASS;

public class EntityLinker {
	private final List<String> sparqlEndpoints;
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
	 * @param model 生成した{@code owl:sameAs}トリプルを追加したいモデル
	 */
	public void executeBySameLabelIdentification(Model jassModel, Model model) {
		// JASSモデル内でrdfs:labelを持つリソース
		Map<Resource, String> rsrc_labelMap = findLabelingNounResource(jassModel);
		
		Set<Map<String, Resource>> label_rscMaps = splitReverseMapWithoutOverlapUnderSize(rsrc_labelMap, maxSizeOfINstatement);
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
				results.forEachRemaining(qs -> {
					Resource dbRsc = qs.getResource("x");
					String label = qs.getLiteral("label").getString();
					Resource jassRsc = lrMap.get(label);	// ラベルからJASSリソースを逆引き
					model.add(jassRsc, OWL.sameAs, dbRsc);		// JASSリソースとDBpediaのリソースをsameAsリンクして格納
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
	
	private static final Query NOUN_QUERY = QueryFactory.create(
			"PREFIX rdf: <"+ RDF.getURI() +"> " +
			"PREFIX rdfs: <"+ RDFS.getURI() +"> " +
			"PREFIX jass: <"+ JASS.getURI() +"> " +
			"SELECT ?rsrc ?label " +
			"WHERE {" +
			"?word a jass:Word ;" + 
			"      jass:mainPoS \"名詞\" ;" + 
			"      jass:means ?rsrc ;" + 
			"      jass:name ?label ." + 
			"{?word jass:subPoS1 \"一般\"} UNION {?word jass:subPoS2 \"固有名詞\"}" + 
			"MINUS{?word a jass:Phrase}" +
			"}"
		);
	private Map<Resource, String> findLabelingNounResource(Model m) {
		Map<Resource, String> rsrc_labelMap = new HashMap<>();
		QueryExecution qexec = QueryExecutionFactory.create(NOUN_QUERY, m);
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			Resource resource = qs.getResource("rsrc");
			Literal label = qs.getLiteral("label");
			rsrc_labelMap.put(resource, label.getString());
		}
		return rsrc_labelMap;
	}

}

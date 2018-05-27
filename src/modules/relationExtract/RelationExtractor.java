package modules.relationExtract;

import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import data.RDF.MyJenaModel;
import data.RDF.MyResource;
import data.RDF.RDFTriple;
import data.id.ModelIDMap;
import data.id.SentenceIDMap;

public class RelationExtractor {
	// RDFルール生成 (読み込み)
	private RDFRules extensionRules = RDFRuleReader.read(Paths.get("resource/rule/extensionRules.txt"));
	private RDFRules ontologyRules = RDFRuleReader.read(Paths.get("resource/rule/ontologyRules.txt"));

	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public RelationExtractor() {}
	

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public ModelIDMap convertMap_Sentence2JASSModel(SentenceIDMap sentenceMap) {
		ModelIDMap mm = new ModelIDMap();
		sentenceMap.forEach((k, v) -> mm.put(new MyJenaModel(JASSFactory.createJASSModel(k)), v));
		return mm;
	}


	/**
	 * JenaのModelを独自クラスRDFTripleのリストに置き換える.
	 * @param model JenaのModel
	 * @return RDFTripleのリスト
	 */
	public List<RDFTriple> convertModel_Jena2TripleList(Model model) {	
		List<RDFTriple> triples = new LinkedList<>();
		StmtIterator stmtIter = model.listStatements();
		while (stmtIter.hasNext()) {
			Statement stmt = stmtIter.nextStatement(); // get next statement
			Resource subject = stmt.getSubject(); // get the subject
			Property predicate = stmt.getPredicate(); // get the predicate
			RDFNode object = stmt.getObject(); // get the object

			RDFTriple triple = new RDFTriple(
					new MyResource(subject),
					new MyResource(predicate),
					new MyResource(object));
			triples.add(triple);
		}
		return triples;
	}

	public ModelIDMap convertMap_JASSModel2RDFModel(ModelIDMap JASSMap) {
		ModelIDMap ontologyMap = new ModelIDMap();

		// 拡張
		JASSMap.forEachKey(m -> m.expands(extensionRules));
		// 変換
		JASSMap.entrySet().stream()
			.map(e -> e.getKey().converts(ontologyRules, e.getValue()))
			.forEach(ontologyMap::putAll);
		
		return ontologyMap;
	}
		
	
	
}
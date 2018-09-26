package modules.RDFConvert;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import data.RDF.MyResource;
import data.RDF.RDFTriple;
import data.RDF.rule.RDFRule;
import data.RDF.rule.RDFRuleReader;
import data.RDF.rule.RDFRules;
import data.RDF.rule.RDFRulesSet;
import data.id.IDTuple;
import data.id.ModelIDMap;
import data.id.SentenceIDMap;
import data.id.StatementIDMap;

public class RelationExtractor {

	/** 標準のJASSオントロジー */
	public final Model defaultJASSModel;

	/** 拡張ルール */
	private final RDFRules extensionRules;
	/** オントロジー変換ルール */
	private final RDFRulesSet ontologyRulesSet;

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public RelationExtractor(RDFRules extensionRules, RDFRulesSet ontologyRules, Model jassModel) {
		this.extensionRules = extensionRules;
		this.ontologyRulesSet = ontologyRules;
		this.defaultJASSModel = jassModel;
	}

	public RelationExtractor(Path extensionRulePath, Path ontologyRulePath, Path defaultJASSPath) {
		this(RDFRuleReader.readRDFRules(extensionRulePath), RDFRuleReader.readRDFRulesSet(ontologyRulePath),
				ModelFactory.createDefaultModel().read(defaultJASSPath.toString()));
	}

	/* ================================================== */
	/* ================== Inner Class =================== */
	/* ================================================== */
	private static class Moderule {
		public final Model model;
		public final RDFRule rule;

		public Moderule(Model model, RDFRule rule) {
			this.model = model;
			this.rule = rule;
		}
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	/**
	 * {@code Sentence}のIDMapから{@code Model}(JASS) のIDMapに変換する.
	 * @param sentenceMap
	 * @return
	 */
	public ModelIDMap convertMap_Sentence2JASSModel(SentenceIDMap sentenceMap) {
		ModelIDMap modelIDMap = new ModelIDMap();
		sentenceMap.forEach((stc, id) -> {
			Model model = ModelFactory.createDefaultModel().add(defaultJASSModel);
			modelIDMap.put(stc.toRDF(model).getModel(), id);
		});
		return modelIDMap;
	}

	public StatementIDMap convertMap_Model2Statements(ModelIDMap modelMap) {
		Map<Model, List<Statement>> replaceMap = modelMap.keySet().stream()
				.collect(Collectors.toMap(m -> m, m -> m.listStatements().toList()));
		return modelMap.replaceModel2Statements(replaceMap);
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

			RDFTriple triple = new RDFTriple(new MyResource(subject), new MyResource(predicate),
					new MyResource(object));
			triples.add(triple);
		}
		return triples;
	}

	/**
	 * JASSモデルからオントロジーに変換する.
	 * @param JASSMap
	 * @return 変換したオントロジーマップ
	 */
	public ModelIDMap convertMap_JASSModel2RDFModel(ModelIDMap JASSMap) {
		ModelIDMap ontologyMap = new ModelIDMap();
		// 拡張
		// 拡張は全てのルールをチェックする
		JASSMap.forEachKey(this::extendsJASSModel);
		// 変換
		// 変換は1つでもルールにマッチした時点でその後のマッチングを止める
		JASSMap.entrySet().forEach(e -> {
			Model convertingModel = e.getKey();
			IDTuple idt = e.getValue();
			Optional<Moderule> modelWithRule = convertsJASSModel(convertingModel);
			modelWithRule.ifPresent(mr -> {
				IDTuple idt_clone = idt.clone();
				idt_clone.setRDFRuleID(String.valueOf(mr.rule.id()));
				ontologyMap.put(mr.model, idt_clone);
			});
		});
		return ontologyMap;
	}


	/* クエリ解決用 */
	private Model extendsJASSModel(Model jass) {
		extensionRules.forEach(r -> {
			Optional<Moderule> optmr = solveConstructQuery(jass, r);
			optmr.ifPresent(mr -> jass.add(mr.model));
		});
		return jass;
	}

	/**
	 * JASSモデルに対し、全てのRDFルールズを適用し、マッチするとjenaモデルを生成する。
	 * マッチしなくても空のモデルを返してる？
	 * 生成されたjenaモデルはマッチしたルールとのマッピングとして返される。
	 * @param jass
	 * @return 生成されたモデルと使用したルール
	 */
	private Optional<Moderule> convertsJASSModel(Model jass) {
		return ontologyRulesSet.stream().flatMap(r -> r.stream()).map(r -> solveConstructQuery(jass, r))
				.filter(opt -> opt.isPresent())	//TODO　isPresentは汚い
				.map(opt -> opt.get()).findFirst();
	}

	private Optional<Moderule> solveConstructQuery(Model model, RDFRule rule) {
		Model m = QueryExecutionFactory.create(createQuery(rule), model).execConstruct();
		return m.isEmpty() ? Optional.empty() : Optional.of(new Moderule(m, rule));
	}

	private Query createQuery(RDFRule rule) {
		return QueryFactory.create(createPrefixes4Query() + rule.writeQuery());
	}

	private String createPrefixes4Query() {
		return defaultJASSModel.getNsPrefixMap().entrySet().parallelStream()
				.map(e -> "PREFIX " + e.getKey() + ": <" + e.getValue() + ">").collect(Collectors.joining(" "));
	}

	/* ================================================== */
	/* ===================== Getter ===================== */
	/* ================================================== */
	public RDFRules getExtensionRules() {
		return extensionRules;
	}

	public RDFRulesSet getOntologyRules() {
		return ontologyRulesSet;
	}
}
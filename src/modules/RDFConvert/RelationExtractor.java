package modules.RDFConvert;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;

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
	public RelationExtractor(RDFRules extensionRules, RDFRulesSet ontologyRuleSet, Model jassModel) {
		this.extensionRules = extensionRules;
		this.ontologyRulesSet = ontologyRuleSet;
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
	 * @return JASSモデルとIDタプルのマップ
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
			Set<Moderule> modelWithRule = convertsJASSModel(convertingModel);
			modelWithRule.forEach(mr -> {
				IDTuple idt_clone = idt.clone();
				idt_clone.setRDFRuleID(String.valueOf(mr.rule.id()));
				ontologyMap.put(mr.model, idt_clone);
			});
		});
		return ontologyMap;
	}


	/* クエリ解決用 */
	/** 
	 * Modelを拡張する
	 * @param jass
	 * @return
	 */
	private Model extendsJASSModel(Model jass) {
		extensionRules.forEach(r -> {
			Optional<Moderule> moderule = solveConstructQuery(jass, r);
			moderule.ifPresent(mr -> jass.add(mr.model));
		});
		return jass;
	}

	/**
	 * JASSモデルに対し、全てのRDFルールズを適用し、マッチするとJenaモデルを生成する。
	 * 1つのRDFルールに対し、たかだか1つまでしかマッチしない。
	 * 1つもマッチしなければ空のセットを返す。
	 * 生成されたJenaモデルはマッチしたルールとのタプルとして返される。
	 * @param jass
	 * @return 生成されたモデルと使用したルールのタプルの集合
	 */
	private Set<Moderule> convertsJASSModel(Model jass) {
		return ontologyRulesSet.stream()
				.map(rs -> solveFirstConstructQuery(jass, rs))
				.flatMap(opt -> opt.map(Stream::of).orElseGet(Stream::empty))	//TODO
				.collect(Collectors.toSet());
	}

	private Optional<Moderule> solveFirstConstructQuery(Model m, RDFRules rs) {
		return rs.stream()
				.map(r -> solveConstructQuery(m, r))
				.flatMap(opt -> opt.map(Stream::of).orElseGet(Stream::empty))	//TODO
				.findFirst();			// 各ルールズにつき最初の1つだけ。
	}
	
	private Optional<Moderule> solveConstructQuery(Model model, RDFRule rule) {
		Model m = QueryExecutionFactory.create(createQuery(rule), model).execConstruct();
		return m.isEmpty() ? Optional.empty() : Optional.of(new Moderule(m, rule));
	}

	private Query createQuery(RDFRule rule) {
		return QueryFactory.create(createPrefixes4Query() + rule.toQueryString());
	}

	private String createPrefixes4Query() {
		return defaultJASSModel.getNsPrefixMap().entrySet().parallelStream()
				.map(e -> "PREFIX " + e.getKey() + ": <" + e.getValue() + ">").collect(Collectors.joining(" "));
	}
	
	public Model removeJASSOntology(Model m) {
		return m.difference(defaultJASSModel);
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
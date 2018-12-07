package modules.RDFConvert;

import java.nio.file.Path;
import java.util.Collection;
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
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import data.RDF.rule.RDFRule;
import data.RDF.rule.RDFRuleReader;
import data.RDF.rule.RDFRules;
import data.RDF.rule.RDFRulesSet;
import data.RDF.vocabulary.JASS;
import data.id.IDTuple;
import data.id.ModelIDMap;
import data.id.SentenceIDMap;
import data.id.StatementIDMap;
import grammar.word.Resourcable;

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
		this(RDFRuleReader.readRDFRules(extensionRulePath), 
				RDFRuleReader.readRDFRulesSet(ontologyRulePath),
				ModelFactory.createDefaultModel().read(defaultJASSPath.toUri().normalize().getPath()));
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
			stc.toJASS(model);
			modelIDMap.put(model, id);
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
	 * @param jassMap
	 * @return 変換したオントロジーマップ
	 */
	public ModelIDMap convertMap_JASSModel2RDFModel(ModelIDMap jassMap) {
		// 拡張
		// 拡張は全てのルールをチェックする
		expandsAll(jassMap);
		// 変換
		// 変換は1つでもルールにマッチした時点で同じファイル内のマッチングを止める
		return convertsAll(jassMap);
	}
	
	private void expandsAll(ModelIDMap jassMap) {
		jassMap.forEachKey(this::expands);
	}
	
	private ModelIDMap convertsAll(ModelIDMap jassMap) {
		//Resourcable.CATEGOREM_RSRC_POOL.write(System.out, "N-TRIPLE");
		ModelIDMap ontologyMap = new ModelIDMap();
		jassMap.forEach((jass, idt) -> {
			Set<Resource> proxyNodes = findsProxyNodes(jass);
			/*
			System.out.print("proxy node:  ");
			proxyNodes.forEach(pn -> System.out.print(pn.getLocalName() + ", "));System.out.println();//PRINT
			*/
			Set<Moderule> modelWithRule = converts(jass);
			modelWithRule.forEach(mr -> {
				IDTuple idt_clone = idt.clone();
				idt_clone.setRDFRuleID(String.valueOf(mr.rule.id()));
				Model m = mr.model;
				replaceProxy2CategoremResource(m, proxyNodes);
				ontologyMap.put(m, idt_clone);
			});
		});
		return ontologyMap;
	}

	/** 
	 * Modelを拡張する
	 * @param jass
	 * @return
	 */
	private Model expands(Model jass) {
		extensionRules.forEach(r -> {
			Optional<Moderule> moderule = solveConstructQuery(jass, r);
			moderule.ifPresent(mr -> jass.add(mr.model));
		});
		return jass;
	}

	/**
	 * JASSモデルに対し、全てのRDFルールズを適用し、マッチするとJenaモデルを生成する。
	 * 1つのRDFルールズに対し、たかだか1つまでしかマッチしない。
	 * 1つもマッチしなければ空のセットを返す。
	 * 生成されたJenaモデルはマッチしたルールとのタプルとして返される。
	 * @param jass
	 * @return 生成されたモデルと使用したルールのタプルの集合
	 */
	private Set<Moderule> converts(Model jass) {
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
	
	private Optional<Moderule> solveConstructQuery(Model m, RDFRule r) {
		Query query = QueryFactory.create(createPrefixes4Query() + r.toQueryString());
		Model resultModel = QueryExecutionFactory.create(query, m).execConstruct();
		return resultModel.isEmpty() ? Optional.empty() : Optional.of(new Moderule(resultModel, r));
	}

	private String createPrefixes4Query() {
		return defaultJASSModel.getNsPrefixMap().entrySet().parallelStream()
				.map(e -> "PREFIX " + e.getKey() + ": <" + e.getValue() + ">")
				.collect(Collectors.joining(" "));
	}
	
	public Model removeJASSOntology(Model m) {
		return m.difference(defaultJASSModel);
	}
	
	private static Set<Resource> findsProxyNodes(Model jassModel) {
		NodeIterator niter = jassModel.listObjectsOfProperty(JASS.means);
		return niter.filterKeep(RDFNode::isResource).mapWith(RDFNode::asResource).toSet();
	}
	
	/**
	 * モデルの中にある代理ノードを自立語リソースに置き換える.
	 * @param m 代理ノードを含むモデル
	 * @param proxyNodes 代理ノードの集合
	 */
	static void replaceProxy2CategoremResource(Model m, Collection<? extends Resource> proxyNodes) {
		/*
		System.out.println("before");
		m.write(System.out, "N-TRIPLE");//PRINT
		*/
		proxyNodes.forEach(pn -> {
			Resource coreNode = Resourcable.coreNodeOf(pn);
			m.listStatements(null, null, pn).toList().stream().forEach(stmt -> {
				m.add(stmt.getSubject(), stmt.getPredicate(), coreNode);
				m.remove(stmt);
			});
			m.listStatements(pn, null, (RDFNode) null).toList().stream().forEach(stmt -> {
				m.add(coreNode, stmt.getPredicate(), stmt.getObject());
				m.remove(stmt);
			});
			Model categoremResources = Resourcable.categoremResourcesOf(pn);
			//System.out.println(m.shortForm(pn.getURI()) + " \tcategorem resources: " + categoremResources.size());
			m.add(categoremResources);
		});
		/*
		System.out.println("after");
		m.write(System.out, "N-TRIPLE");//PRINT
		System.out.println();
		*/
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
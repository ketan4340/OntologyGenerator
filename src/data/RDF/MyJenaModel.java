package data.RDF;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Alt;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelChangedListener;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.NsIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.RSIterator;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.Seq;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.Lock;
import org.apache.jena.shared.PrefixMapping;

import data.id.IDTuple;
import data.id.Identifiable;
import data.id.ModelIDMap;
import modules.relationExtract.AbstractRDFRule;
import modules.relationExtract.RDFRules;


public class MyJenaModel implements Identifiable {
	private static int sum = 0;
	public static int tripleSum = 0;
	
	private final int id;
	private Model model;
	
	private final int ruleID;
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public MyJenaModel() {
		this(ModelFactory.createDefaultModel(), -1);
	}
	public MyJenaModel(Model model) {
		this(model, -1);
	}
	public MyJenaModel(int ruleID) {
		this(ModelFactory.createDefaultModel(), ruleID);
	}
	public MyJenaModel(Model model, int ruleID) {
		this.id = sum++;
		setModel(model);
		this.ruleID = ruleID;
	}
	
	/****************************************/
	/**********    Static Method    *********/
	/****************************************/


	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public void expands(AbstractRDFRule rule) {
		add(rule.solve(model));
	}
	public void expands(RDFRules rules) {
		rules.getRules().stream()
			.forEach(this::expands);
	}
	
	public MyJenaModel converts(AbstractRDFRule rule) {
		return new MyJenaModel(rule.solve(model), rule.id());
	}
	public ModelIDMap converts(RDFRules rules, IDTuple idTuple) {
		ModelIDMap convertedModelMap = new ModelIDMap(); 
		rules.getRules().stream()
			.map(this::converts)
			.forEach(m -> convertedModelMap.put(m, idTuple.clone()));
		return convertedModelMap;
	}
	
	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public int getID() {
		return id;
	}
	public Model getModel() {
		return model;
	}
	public void setModel(Model m) {
		this.model = m;
	}
	public int getRuleID() {
		return ruleID;
	}

	
	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public int id() {
		return getID();
	}


	/****************************************/
	/**********  Delegate  Method  **********/
	/****************************************/
	public Model abort() {
		return model.abort();
	}
	public Model add(List<Statement> arg0) {
		return model.add(arg0);
	}
	public Model add(Model arg0) {
		return model.add(arg0);
	}
	public Model add(Resource arg0, Property arg1, RDFNode arg2) {
		return model.add(arg0, arg1, arg2);
	}
	public Model add(Resource arg0, Property arg1, String arg2, boolean arg3) {
		return model.add(arg0, arg1, arg2, arg3);
	}
	public Model add(Resource arg0, Property arg1, String arg2, RDFDatatype arg3) {
		return model.add(arg0, arg1, arg2, arg3);
	}
	public Model add(Resource arg0, Property arg1, String arg2, String arg3) {
		return model.add(arg0, arg1, arg2, arg3);
	}
	public Model add(Resource arg0, Property arg1, String arg2) {
		return model.add(arg0, arg1, arg2);
	}
	public Model add(Statement arg0) {
		return model.add(arg0);
	}
	public Model add(Statement[] arg0) {
		return model.add(arg0);
	}
	public Model add(StmtIterator arg0) {
		return model.add(arg0);
	}
	public Model addLiteral(Resource arg0, Property arg1, boolean arg2) {
		return model.addLiteral(arg0, arg1, arg2);
	}
	public Model addLiteral(Resource arg0, Property arg1, char arg2) {
		return model.addLiteral(arg0, arg1, arg2);
	}
	public Model addLiteral(Resource arg0, Property arg1, double arg2) {
		return model.addLiteral(arg0, arg1, arg2);
	}
	public Model addLiteral(Resource arg0, Property arg1, float arg2) {
		return model.addLiteral(arg0, arg1, arg2);
	}
	public Model addLiteral(Resource arg0, Property arg1, int arg2) {
		return model.addLiteral(arg0, arg1, arg2);
	}
	public Model addLiteral(Resource arg0, Property arg1, Literal arg2) {
		return model.addLiteral(arg0, arg1, arg2);
	}
	public Model addLiteral(Resource arg0, Property arg1, long arg2) {
		return model.addLiteral(arg0, arg1, arg2);
	}
	public RDFNode asRDFNode(Node arg0) {
		return model.asRDFNode(arg0);
	}
	public Statement asStatement(Triple arg0) {
		return model.asStatement(arg0);
	}
	public Model begin() {
		return model.begin();
	}
	public <T> T calculateInTxn(Supplier<T> arg0) {
		return model.calculateInTxn(arg0);
	}
	public PrefixMapping clearNsPrefixMap() {
		return model.clearNsPrefixMap();
	}
	public void close() {
		model.close();
	}
	public Model commit() {
		return model.commit();
	}
	public boolean contains(Resource arg0, Property arg1, RDFNode arg2) {
		return model.contains(arg0, arg1, arg2);
	}
	public boolean contains(Resource arg0, Property arg1, String arg2, String arg3) {
		return model.contains(arg0, arg1, arg2, arg3);
	}
	public boolean contains(Resource arg0, Property arg1, String arg2) {
		return model.contains(arg0, arg1, arg2);
	}
	public boolean contains(Resource arg0, Property arg1) {
		return model.contains(arg0, arg1);
	}
	public boolean contains(Statement arg0) {
		return model.contains(arg0);
	}
	public boolean containsAll(Model arg0) {
		return model.containsAll(arg0);
	}
	public boolean containsAll(StmtIterator arg0) {
		return model.containsAll(arg0);
	}
	public boolean containsAny(Model arg0) {
		return model.containsAny(arg0);
	}
	public boolean containsAny(StmtIterator arg0) {
		return model.containsAny(arg0);
	}
	public boolean containsLiteral(Resource arg0, Property arg1, boolean arg2) {
		return model.containsLiteral(arg0, arg1, arg2);
	}
	public boolean containsLiteral(Resource arg0, Property arg1, char arg2) {
		return model.containsLiteral(arg0, arg1, arg2);
	}
	public boolean containsLiteral(Resource arg0, Property arg1, double arg2) {
		return model.containsLiteral(arg0, arg1, arg2);
	}
	public boolean containsLiteral(Resource arg0, Property arg1, float arg2) {
		return model.containsLiteral(arg0, arg1, arg2);
	}
	public boolean containsLiteral(Resource arg0, Property arg1, int arg2) {
		return model.containsLiteral(arg0, arg1, arg2);
	}
	public boolean containsLiteral(Resource arg0, Property arg1, long arg2) {
		return model.containsLiteral(arg0, arg1, arg2);
	}
	public boolean containsLiteral(Resource arg0, Property arg1, Object arg2) {
		return model.containsLiteral(arg0, arg1, arg2);
	}
	public boolean containsResource(RDFNode arg0) {
		return model.containsResource(arg0);
	}
	public Alt createAlt() {
		return model.createAlt();
	}
	public Alt createAlt(String arg0) {
		return model.createAlt(arg0);
	}
	public Bag createBag() {
		return model.createBag();
	}
	public Bag createBag(String arg0) {
		return model.createBag(arg0);
	}
	public RDFList createList() {
		return model.createList();
	}
	public RDFList createList(Iterator<? extends RDFNode> arg0) {
		return model.createList(arg0);
	}
	public RDFList createList(RDFNode[] arg0) {
		return model.createList(arg0);
	}
	public Literal createLiteral(String arg0, boolean arg1) {
		return model.createLiteral(arg0, arg1);
	}
	public Literal createLiteral(String arg0, String arg1) {
		return model.createLiteral(arg0, arg1);
	}
	public Literal createLiteral(String arg0) {
		return model.createLiteral(arg0);
	}
	public Statement createLiteralStatement(Resource arg0, Property arg1, boolean arg2) {
		return model.createLiteralStatement(arg0, arg1, arg2);
	}
	public Statement createLiteralStatement(Resource arg0, Property arg1, char arg2) {
		return model.createLiteralStatement(arg0, arg1, arg2);
	}
	public Statement createLiteralStatement(Resource arg0, Property arg1, double arg2) {
		return model.createLiteralStatement(arg0, arg1, arg2);
	}
	public Statement createLiteralStatement(Resource arg0, Property arg1, float arg2) {
		return model.createLiteralStatement(arg0, arg1, arg2);
	}
	public Statement createLiteralStatement(Resource arg0, Property arg1, int arg2) {
		return model.createLiteralStatement(arg0, arg1, arg2);
	}
	public Statement createLiteralStatement(Resource arg0, Property arg1, long arg2) {
		return model.createLiteralStatement(arg0, arg1, arg2);
	}
	public Statement createLiteralStatement(Resource arg0, Property arg1, Object arg2) {
		return model.createLiteralStatement(arg0, arg1, arg2);
	}
	public Property createProperty(String arg0, String arg1) {
		return model.createProperty(arg0, arg1);
	}
	public Property createProperty(String arg0) {
		return model.createProperty(arg0);
	}
	public ReifiedStatement createReifiedStatement(Statement arg0) {
		return model.createReifiedStatement(arg0);
	}
	public ReifiedStatement createReifiedStatement(String arg0, Statement arg1) {
		return model.createReifiedStatement(arg0, arg1);
	}
	public Resource createResource() {
		return model.createResource();
	}
	public Resource createResource(AnonId arg0) {
		return model.createResource(arg0);
	}
	public Resource createResource(Resource arg0) {
		return model.createResource(arg0);
	}
	public Resource createResource(String arg0, Resource arg1) {
		return model.createResource(arg0, arg1);
	}
	public Resource createResource(String arg0) {
		return model.createResource(arg0);
	}
	public Seq createSeq() {
		return model.createSeq();
	}
	public Seq createSeq(String arg0) {
		return model.createSeq(arg0);
	}
	public Statement createStatement(Resource arg0, Property arg1, RDFNode arg2) {
		return model.createStatement(arg0, arg1, arg2);
	}
	public Statement createStatement(Resource arg0, Property arg1, String arg2, boolean arg3) {
		return model.createStatement(arg0, arg1, arg2, arg3);
	}
	public Statement createStatement(Resource arg0, Property arg1, String arg2, String arg3, boolean arg4) {
		return model.createStatement(arg0, arg1, arg2, arg3, arg4);
	}
	public Statement createStatement(Resource arg0, Property arg1, String arg2, String arg3) {
		return model.createStatement(arg0, arg1, arg2, arg3);
	}
	public Statement createStatement(Resource arg0, Property arg1, String arg2) {
		return model.createStatement(arg0, arg1, arg2);
	}
	public Literal createTypedLiteral(boolean arg0) {
		return model.createTypedLiteral(arg0);
	}
	public Literal createTypedLiteral(Calendar arg0) {
		return model.createTypedLiteral(arg0);
	}
	public Literal createTypedLiteral(char arg0) {
		return model.createTypedLiteral(arg0);
	}
	public Literal createTypedLiteral(double arg0) {
		return model.createTypedLiteral(arg0);
	}
	public Literal createTypedLiteral(float arg0) {
		return model.createTypedLiteral(arg0);
	}
	public Literal createTypedLiteral(int arg0) {
		return model.createTypedLiteral(arg0);
	}
	public Literal createTypedLiteral(long arg0) {
		return model.createTypedLiteral(arg0);
	}
	public Literal createTypedLiteral(Object arg0, RDFDatatype arg1) {
		return model.createTypedLiteral(arg0, arg1);
	}
	public Literal createTypedLiteral(Object arg0, String arg1) {
		return model.createTypedLiteral(arg0, arg1);
	}
	public Literal createTypedLiteral(Object arg0) {
		return model.createTypedLiteral(arg0);
	}
	public Literal createTypedLiteral(String arg0, RDFDatatype arg1) {
		return model.createTypedLiteral(arg0, arg1);
	}
	public Literal createTypedLiteral(String arg0, String arg1) {
		return model.createTypedLiteral(arg0, arg1);
	}
	public Literal createTypedLiteral(String arg0) {
		return model.createTypedLiteral(arg0);
	}
	public Model difference(Model arg0) {
		return model.difference(arg0);
	}
	public void enterCriticalSection(boolean arg0) {
		model.enterCriticalSection(arg0);
	}
	public boolean equals(Object arg0) {
		return model.equals(arg0);
	}
	public void executeInTxn(Runnable arg0) {
		model.executeInTxn(arg0);
	}
	public String expandPrefix(String arg0) {
		return model.expandPrefix(arg0);
	}
	public Alt getAlt(Resource arg0) {
		return model.getAlt(arg0);
	}
	public Alt getAlt(String arg0) {
		return model.getAlt(arg0);
	}
	public Resource getAnyReifiedStatement(Statement arg0) {
		return model.getAnyReifiedStatement(arg0);
	}
	public Bag getBag(Resource arg0) {
		return model.getBag(arg0);
	}
	public Bag getBag(String arg0) {
		return model.getBag(arg0);
	}
	public Graph getGraph() {
		return model.getGraph();
	}
	public Lock getLock() {
		return model.getLock();
	}
	public Map<String, String> getNsPrefixMap() {
		return model.getNsPrefixMap();
	}
	public String getNsPrefixURI(String arg0) {
		return model.getNsPrefixURI(arg0);
	}
	public String getNsURIPrefix(String arg0) {
		return model.getNsURIPrefix(arg0);
	}
	public Statement getProperty(Resource arg0, Property arg1, String arg2) {
		return model.getProperty(arg0, arg1, arg2);
	}
	public Statement getProperty(Resource arg0, Property arg1) {
		return model.getProperty(arg0, arg1);
	}
	public Property getProperty(String arg0, String arg1) {
		return model.getProperty(arg0, arg1);
	}
	public Property getProperty(String arg0) {
		return model.getProperty(arg0);
	}
	public RDFNode getRDFNode(Node arg0) {
		return model.getRDFNode(arg0);
	}
	public RDFReader getReader() {
		return model.getReader();
	}
	public RDFReader getReader(String arg0) {
		return model.getReader(arg0);
	}
	public Statement getRequiredProperty(Resource arg0, Property arg1, String arg2) {
		return model.getRequiredProperty(arg0, arg1, arg2);
	}
	public Statement getRequiredProperty(Resource arg0, Property arg1) {
		return model.getRequiredProperty(arg0, arg1);
	}
	public Resource getResource(String arg0) {
		return model.getResource(arg0);
	}
	public Seq getSeq(Resource arg0) {
		return model.getSeq(arg0);
	}
	public Seq getSeq(String arg0) {
		return model.getSeq(arg0);
	}
	public RDFWriter getWriter() {
		return model.getWriter();
	}
	public RDFWriter getWriter(String arg0) {
		return model.getWriter(arg0);
	}
	public boolean independent() {
		return model.independent();
	}
	public Model intersection(Model arg0) {
		return model.intersection(arg0);
	}
	public boolean isClosed() {
		return model.isClosed();
	}
	public boolean isEmpty() {
		return model.isEmpty();
	}
	public boolean isIsomorphicWith(Model arg0) {
		return model.isIsomorphicWith(arg0);
	}
	public boolean isReified(Statement arg0) {
		return model.isReified(arg0);
	}
	public void leaveCriticalSection() {
		model.leaveCriticalSection();
	}
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1, boolean arg2) {
		return model.listLiteralStatements(arg0, arg1, arg2);
	}
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1, char arg2) {
		return model.listLiteralStatements(arg0, arg1, arg2);
	}
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1, double arg2) {
		return model.listLiteralStatements(arg0, arg1, arg2);
	}
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1, float arg2) {
		return model.listLiteralStatements(arg0, arg1, arg2);
	}
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1, int arg2) {
		return model.listLiteralStatements(arg0, arg1, arg2);
	}
	public StmtIterator listLiteralStatements(Resource arg0, Property arg1, long arg2) {
		return model.listLiteralStatements(arg0, arg1, arg2);
	}
	public NsIterator listNameSpaces() {
		return model.listNameSpaces();
	}
	public NodeIterator listObjects() {
		return model.listObjects();
	}
	public NodeIterator listObjectsOfProperty(Property arg0) {
		return model.listObjectsOfProperty(arg0);
	}
	public NodeIterator listObjectsOfProperty(Resource arg0, Property arg1) {
		return model.listObjectsOfProperty(arg0, arg1);
	}
	public RSIterator listReifiedStatements() {
		return model.listReifiedStatements();
	}
	public RSIterator listReifiedStatements(Statement arg0) {
		return model.listReifiedStatements(arg0);
	}
	public ResIterator listResourcesWithProperty(Property arg0, boolean arg1) {
		return model.listResourcesWithProperty(arg0, arg1);
	}
	public ResIterator listResourcesWithProperty(Property arg0, char arg1) {
		return model.listResourcesWithProperty(arg0, arg1);
	}
	public ResIterator listResourcesWithProperty(Property arg0, double arg1) {
		return model.listResourcesWithProperty(arg0, arg1);
	}
	public ResIterator listResourcesWithProperty(Property arg0, float arg1) {
		return model.listResourcesWithProperty(arg0, arg1);
	}
	public ResIterator listResourcesWithProperty(Property arg0, long arg1) {
		return model.listResourcesWithProperty(arg0, arg1);
	}
	public ResIterator listResourcesWithProperty(Property arg0, Object arg1) {
		return model.listResourcesWithProperty(arg0, arg1);
	}
	public ResIterator listResourcesWithProperty(Property arg0, RDFNode arg1) {
		return model.listResourcesWithProperty(arg0, arg1);
	}
	public ResIterator listResourcesWithProperty(Property arg0) {
		return model.listResourcesWithProperty(arg0);
	}
	public StmtIterator listStatements() {
		return model.listStatements();
	}
	public StmtIterator listStatements(Resource arg0, Property arg1, RDFNode arg2) {
		return model.listStatements(arg0, arg1, arg2);
	}
	public StmtIterator listStatements(Resource arg0, Property arg1, String arg2, String arg3) {
		return model.listStatements(arg0, arg1, arg2, arg3);
	}
	public StmtIterator listStatements(Resource arg0, Property arg1, String arg2) {
		return model.listStatements(arg0, arg1, arg2);
	}
	public StmtIterator listStatements(Selector arg0) {
		return model.listStatements(arg0);
	}
	public ResIterator listSubjects() {
		return model.listSubjects();
	}
	public ResIterator listSubjectsWithProperty(Property arg0, RDFNode arg1) {
		return model.listSubjectsWithProperty(arg0, arg1);
	}
	public ResIterator listSubjectsWithProperty(Property arg0, String arg1, String arg2) {
		return model.listSubjectsWithProperty(arg0, arg1, arg2);
	}
	public ResIterator listSubjectsWithProperty(Property arg0, String arg1) {
		return model.listSubjectsWithProperty(arg0, arg1);
	}
	public ResIterator listSubjectsWithProperty(Property arg0) {
		return model.listSubjectsWithProperty(arg0);
	}
	public PrefixMapping lock() {
		return model.lock();
	}
	public Model notifyEvent(Object arg0) {
		return model.notifyEvent(arg0);
	}
	public int numPrefixes() {
		return model.numPrefixes();
	}
	public String qnameFor(String arg0) {
		return model.qnameFor(arg0);
	}
	public Model query(Selector arg0) {
		return model.query(arg0);
	}
	public Model read(InputStream arg0, String arg1, String arg2) {
		return model.read(arg0, arg1, arg2);
	}
	public Model read(InputStream arg0, String arg1) {
		return model.read(arg0, arg1);
	}
	public Model read(Reader arg0, String arg1, String arg2) {
		return model.read(arg0, arg1, arg2);
	}
	public Model read(Reader arg0, String arg1) {
		return model.read(arg0, arg1);
	}
	public Model read(String arg0, String arg1, String arg2) {
		return model.read(arg0, arg1, arg2);
	}
	public Model read(String arg0, String arg1) {
		return model.read(arg0, arg1);
	}
	public Model read(String arg0) {
		return model.read(arg0);
	}
	public Model register(ModelChangedListener arg0) {
		return model.register(arg0);
	}
	public Model remove(List<Statement> arg0) {
		return model.remove(arg0);
	}
	public Model remove(Model arg0) {
		return model.remove(arg0);
	}
	public Model remove(Resource arg0, Property arg1, RDFNode arg2) {
		return model.remove(arg0, arg1, arg2);
	}
	public Model remove(Statement arg0) {
		return model.remove(arg0);
	}
	public Model remove(Statement[] arg0) {
		return model.remove(arg0);
	}
	public Model remove(StmtIterator arg0) {
		return model.remove(arg0);
	}
	public Model removeAll() {
		return model.removeAll();
	}
	public Model removeAll(Resource arg0, Property arg1, RDFNode arg2) {
		return model.removeAll(arg0, arg1, arg2);
	}
	public void removeAllReifications(Statement arg0) {
		model.removeAllReifications(arg0);
	}
	public PrefixMapping removeNsPrefix(String arg0) {
		return model.removeNsPrefix(arg0);
	}
	public void removeReification(ReifiedStatement arg0) {
		model.removeReification(arg0);
	}
	public boolean samePrefixMappingAs(PrefixMapping arg0) {
		return model.samePrefixMappingAs(arg0);
	}
	public PrefixMapping setNsPrefix(String arg0, String arg1) {
		return model.setNsPrefix(arg0, arg1);
	}
	public PrefixMapping setNsPrefixes(Map<String, String> arg0) {
		return model.setNsPrefixes(arg0);
	}
	public PrefixMapping setNsPrefixes(PrefixMapping arg0) {
		return model.setNsPrefixes(arg0);
	}
	public String shortForm(String arg0) {
		return model.shortForm(arg0);
	}
	public long size() {
		return model.size();
	}
	public boolean supportsSetOperations() {
		return model.supportsSetOperations();
	}
	public boolean supportsTransactions() {
		return model.supportsTransactions();
	}
	public Model union(Model arg0) {
		return model.union(arg0);
	}
	public Model unregister(ModelChangedListener arg0) {
		return model.unregister(arg0);
	}
	public PrefixMapping withDefaultMappings(PrefixMapping arg0) {
		return model.withDefaultMappings(arg0);
	}
	public Resource wrapAsResource(Node arg0) {
		return model.wrapAsResource(arg0);
	}
	public Model write(OutputStream arg0, String arg1, String arg2) {
		return model.write(arg0, arg1, arg2);
	}
	public Model write(OutputStream arg0, String arg1) {
		return model.write(arg0, arg1);
	}
	public Model write(OutputStream arg0) {
		return model.write(arg0);
	}
	public Model write(Writer arg0, String arg1, String arg2) {
		return model.write(arg0, arg1, arg2);
	}
	public Model write(Writer arg0, String arg1) {
		return model.write(arg0, arg1);
	}
	public Model write(Writer arg0) {
		return model.write(arg0);
	}
}
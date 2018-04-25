package modules.relationExtract;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;

import data.RDF.Namespace;

public class RDFRule {
	private static final String prefixRDF = Namespace.RDF.toQueryPrefixDefinition();
	private static final String prefixRDFS = Namespace.RDFS.toQueryPrefixDefinition();
	private static final String prefixOWL = Namespace.OWL.toQueryPrefixDefinition();
	private static final String prefixDC = Namespace.DC.toQueryPrefixDefinition();
	private static final String prefixDCTERM = Namespace.DCTERMS.toQueryPrefixDefinition();
	private static final String prefixSCHEMA = Namespace.SCHEMA.toQueryPrefixDefinition();
	private static final String prefixJASS = Namespace.JASS.toQueryPrefixDefinition();
	private static final String prefixGOO = Namespace.GOO.toQueryPrefixDefinition();
	private static final String prefixSIO = Namespace.SIO.toQueryPrefixDefinition();
	
	private RDFGraphPattern ifPattern;
	private RDFGraphPattern thenPattern;

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public RDFRule(String[][] ifs, String[][] thens) {
		this.ifPattern = new RDFGraphPattern(
				Stream.of(ifs)
				.map(tri -> new RDFTriplePattern(tri[0], tri[1], tri[2]))
				.collect(Collectors.toSet()));
		this.thenPattern = new RDFGraphPattern(
				Stream.of(thens)
				.map(tri -> new RDFTriplePattern(tri[0], tri[1], tri[2]))
				.collect(Collectors.toSet()));
	}


	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public Model expands(Model targetModel) {
		Model m = solve(targetModel);
		
		//TODO
		/*
		new Generator().convertJena2Original(m).stream()
		.map(tri -> tri.toString()).forEach(System.out::println);
		System.out.println();
		*/
		
		targetModel.add(m);
		return targetModel;
	}

	public Model converts(Model targetModel) {
		return solve(targetModel);
	}

	private Model solve(Model targetModel) {
		return QueryExecutionFactory.create(toQuery(), targetModel).execConstruct();
	}
	
	private Query toQuery() {
		String queryString = 
				prefixRDF+prefixRDFS+prefixOWL+prefixDC+prefixDCTERM+prefixSCHEMA+prefixJASS+prefixGOO+prefixSIO +
				"CONSTRUCT {" +
					thenPattern.joins(".", "", "", " ", "", " ") +
				"}" +
				"WHERE {" +
					ifPattern.joins(".", "", "", " ", "", " ") +
				"}";
		return QueryFactory.create(queryString);
	}

	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return "IF "+ ifPattern.toString() +"\nTHEN "+ thenPattern.toString();
	}
}
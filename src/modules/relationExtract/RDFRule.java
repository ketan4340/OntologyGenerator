package modules.relationExtract;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import data.original.Namespace;

public class RDFRule {
	private static final String prefixRDF = Namespace.RDF.toQueryPrefixDefinition();
	private static final String prefixRDFS = Namespace.RDFS.toQueryPrefixDefinition();
	private static final String prefixOWL = Namespace.OWL.toQueryPrefixDefinition();
	private static final String prefixDC = Namespace.DC.toQueryPrefixDefinition();
	private static final String prefixDCTERM = Namespace.DCTERMS.toQueryPrefixDefinition();
	private static final String prefixSCHEMA = Namespace.SCHEMA.toQueryPrefixDefinition();
	private static final String prefixJASS = Namespace.JASS.toQueryPrefixDefinition();
	private static final String prefixGOO = Namespace.GOO.toQueryPrefixDefinition();
	
	private Map<String, String> varURIMap;
	
	private Query ifStmt;	
	private Set<RDFTriplePattern> thenStmt;
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public RDFRule(String[][] ifs, String[][] thens) {
		this.varURIMap = Stream.concat(Stream.of(ifs), Stream.of(thens))
				.flatMap(Stream::of)
				.collect(Collectors.toMap(s -> s, s -> s, (k1, k2) -> k1));
		mapInit();
		String queryString = 
						prefixRDF+prefixRDFS+prefixOWL+prefixDC+prefixDCTERM+prefixSCHEMA+prefixJASS+prefixGOO +
						"SELECT * " +
						"WHERE {" +
						Stream.of(ifs).map(Stream::of)
						.map(stm -> stm.collect(Collectors.joining(" ")))
						.collect(Collectors.joining(" .")) +
						"}";
		this.ifStmt = QueryFactory.create(queryString);
		this.thenStmt = Stream.of(thens)
				.map(tri -> new RDFTriplePattern(tri[0], tri[1], tri[2]))
				.collect(Collectors.toSet());
	}
	
	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public List<Statement> solve(Model targetModel) {
		List<Statement> statements = new LinkedList<>();
		QueryExecution qexec = QueryExecutionFactory.create(ifStmt, targetModel);
		ResultSet resultSet = qexec.execSelect();
		List<String> varNames = resultSet.getResultVars();
		
		while (resultSet.hasNext()) {
			QuerySolution qsol = resultSet.next();
			varNames.stream().forEach(s -> varURIMap.put(s, qsol.getResource(s).getURI()));
			
			statements.addAll(thenStmt.stream()
					.map(tp -> tp.fillStatement(targetModel, varURIMap))
					.collect(Collectors.toList()));
		}
		return statements;
	}
	
	private void mapInit() {
		varURIMap.keySet().stream().forEach(s -> {
			if (s.startsWith("?")) {
				varURIMap.put(s, null);
			} else {
				String[] ns =  s.split(":");
				if (ns.length == 2) {
					String uri = Namespace.getURIofPrefix(ns[0]);
					varURIMap.put(s, uri);
				}
			}	
		});
	}

	
	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return ifStmt.toString() + "->" + 
				thenStmt.stream().map(tp -> tp.toString()).collect(Collectors.joining("."));
	}
}
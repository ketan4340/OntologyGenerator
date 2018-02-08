package modules.relationExtract;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import data.original.Namespace;

public class RDFRule {
	/**
	 * キーが変数名あるいはRDFノードの省略名
	 * 値が代入されるリソースのURI
	 */
	private Map<String, String> varURIMap;

	private RDFGraphPattern ifPattern;
	private RDFGraphPattern thenPattern;

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public RDFRule(String[][] ifs, String[][] thens) {
		this.varURIMap = Stream.concat(Stream.of(ifs), Stream.of(thens))
				.flatMap(Stream::of)
				.map(s -> s.startsWith("?")? s.substring(0, s.length()-1) : s)
				.collect(Collectors.toMap(s -> s, s -> s, (k1, k2) -> k1));
		mapInit();
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
	public List<Statement> solve(Model targetModel) {
		List<Statement> statements = new LinkedList<>();
		QueryExecution qexec = QueryExecutionFactory.create(ifPattern.toQuery(), targetModel);
		ResultSet resultSet = qexec.execSelect();
		List<String> varNames = resultSet.getResultVars();

		System.out.println("\n Rule : " + toString());
		System.out.println("varNames : " + String.join(",",	 varNames));	//TODO

		while (resultSet.hasNext()) {
			QuerySolution qsol = resultSet.next();
			varNames.stream().forEach(s -> varURIMap.put(s, qsol.getResource(s).getURI()));

			statements.addAll(thenPattern.getTriplePatterns().stream()
					.map(tp -> tp.fillStatement(targetModel, varURIMap))
					.collect(Collectors.toList()));
		}
		return statements;
	}

	private void mapInit() {
		Map<String, String> newmap = new HashMap<>();
		varURIMap.keySet().stream().forEach(s -> {
			if (s.startsWith("?")) {
				newmap.put(s.substring(1, s.length()), null);
			} else {
				String[] ns =  s.split(":");
				if (ns.length == 2) {
					String uri = Namespace.getURIofPrefix(ns[0]);
					newmap.put(s, uri+ns[1]);
				} else {
					newmap.put(s, s);
				}
			}
		});
		varURIMap = newmap;
	}


	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return ifPattern.toString() +"\n\t -> "+ thenPattern.toString();
	}
}
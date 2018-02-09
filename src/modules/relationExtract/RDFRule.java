package modules.relationExtract;

import java.util.AbstractMap.SimpleEntry;
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
				.map(s -> new SimpleEntry<>(s, s))
				.map(e -> {
					String s = e.getKey();
					String[] ns = s.split(":");
					if (ns.length == 2  && !s.startsWith("<") && !s.startsWith("\"")) {
						String uri = Namespace.getURIofPrefix(ns[0]);
						e.setValue(uri + ns[1]);
					}
					return e;
				})
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (k1, k2) -> k1));
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
			varNames.stream().forEach(s -> varURIMap.put("?"+s, qsol.get(s).toString()));

			statements.addAll(thenPattern.getTriplePatterns().stream()
					.map(tp -> tp.fillStatement(targetModel, varURIMap))
					.collect(Collectors.toList()));
		}
		return statements;
	}



	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return "IF "+ ifPattern.toString() +"\nTHEN "+ thenPattern.toString();
	}
}
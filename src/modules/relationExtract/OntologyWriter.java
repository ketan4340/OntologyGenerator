package modules.relationExtract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import data.RDF.RDFTriple;

public class OntologyWriter {
	public static final int N_TRIPLES = 1;
	public static final int TURTLE = 2;
	public static final int XML = 3;
	public static final int JSON_LD = 4;

	private static final Path regexFilePath = Paths.get("rules/ontLangRegex.txt");
	
	public List<RDFTriple> triples;
	public int serialize;		// RDFデータの出力形式
	public String extension;		// 拡張子
	private List<String> ontLangRegexes;

	public OntologyWriter(int serialize, List<RDFTriple> triples) {
		this.triples = triples;
		this.serialize = serialize;
		
		switch (serialize) {
		case N_TRIPLES:
			extension = ".n-tri.owl";
		case TURTLE:
			extension = ".turtle.owl";
		case XML:
			extension = ".xml.owl";
		case JSON_LD:
			extension = "json.owl";
		}

		try {
			this.ontLangRegexes = Files.readAllLines(regexFilePath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/* RDF/XMLファイルを出力する */
	public void output(String saveFile) {
		saveFile += extension;
		File file = new File(saveFile);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			String rdf = "";
			if(serialize == XML) {
				bw.write("<?xml version=\"1.0\"?>"+"\n"
					+ "<rdf:RDF"+"\n"
					+ "\t"+"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+"\n"
					+ "\t"+"xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""+"\n"
					+ "\t"+"xmlns:owl=\"http://www.w3.org/2002/07/owl#\""+"\n"
					+ "\t"+"xmlns:dc=\"http://purl.org/dc/elements/1.1/\""+"\n"
					+ "\t"+"xmlns=\"http://www.owl-ontologies.com/unnamed.owl#\""+"\n"
					+ "\t"+"xml:base=\"http://www.owl-ontologies.com/unnamed.owl\""+"\n"
					+ ">"+"\n"
					+ "<owl:Ontology rdf:about=\"\"/>"+"\n");
				bw.newLine();
				for(final RDFTriple triple: triples) {
					rdf = serializeRDF(triple);
					bw.write(rdf);
					bw.newLine();
				}
				bw.write("</rdf:RDF>");
				
			} else if (serialize == N_TRIPLES) {
				bw.write("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>."+"\n"
				+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>."+"\n"
				+ "@prefix owl: <http://www.w3.org/2002/07/owl#>."+"\n"
				+ "@prefix dc: <http://purl.org/dc/elements/1.1/>."+"\n");
				bw.newLine();
				for(final RDFTriple triple: triples) {
					System.out.println("triple = " + triple);
					rdf = serializeRDF(triple);
					//System.out.println("rdf : " + rdf);
					bw.write(rdf);
					bw.newLine();
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String serializeRDF(RDFTriple triple) {
		return setDefaultTriple(
				triple.getSubject().toString(),
				triple.getPredicate().toString(),
				triple.getObject().toString());
	}

	/*** body(本文に)公理を追記する ***/
	/* インスタンスの定義 */
	public String setType(String instance, String mainClass) {
		String axiom = new String();
		if (serialize == XML) {
			axiom = "<owl:Class rdf:ID=\"" + instance + "\">\n"
					+ "\t<rdf:type rdf:resource=\"#" + mainClass + "\"/>\n"
					+ "</owl:Class>\n";
		} else if(serialize == N_TRIPLES) {
			axiom = getN3Elem(instance)+" "
					+ "rdf:type "
					+ getN3Elem(mainClass)+".";
		}
		return axiom;
	}
	/* クラスを定義するだけ */
	public String setClass(String className) {
		String axiom = new String();
		if (serialize == XML) {
			axiom = "<owl:Class rdf:ID=\"" + className + "\"/>\n";
		} else if(serialize == N_TRIPLES) {
			axiom = getN3Elem(className)+" "
					+ "rdf:type "
					+ "owl:Class.";
		}
		return axiom;
	}
	/* サブクラスの定義 */
	public String setSubClassOf(String mainClass, String subClass) {
		String axiom = new String();
		if(serialize == XML) {
			axiom = "<owl:Class rdf:ID=\"" + mainClass + "\">\n"
					+ "\t<rdfs:subClassOf rdf:resource=\"#" + subClass + "\"/>\n"
					+ "</owl:Class>\n";
		} else if(serialize == N_TRIPLES) {
			axiom = getN3Elem(mainClass)+" "
					+ "rdfs:subClassOf "
					+ getN3Elem(subClass)+".";
		}
		return axiom;
	}
	/* プロパティの定義(domain,range両方) */
	public String setProperty(String property, String domain, String range) {
		String axiom = new String();
		if (serialize == XML) {
			axiom = "<owl:ObjectProperty rdf:ID=\"" + property + "\">\n"
					+ "\t<rdfs:domain rdf:resource=\"#" + domain + "\"/>\n"
					+ "\t<rdfs:range rdf:resource=\"#" + range + "\"/>\n"
	    			+ "</owl:ObjectProperty>\n";
		} else if(serialize == N_TRIPLES) {
			axiom = getN3Elem(property)+" "
					+ "rdfs:domain "+getN3Elem(domain)+"; "
					+ "rdfs:range "+getN3Elem(range)+".";
		}
	    return axiom;
	}
	/* プロパティの定義(domainのみ) */
	public String setPropertyDomain(String property, String domain) {
		String axiom = new String();
		if (serialize == XML) {
			axiom = "<owl:ObjectProperty rdf:ID=\"" + property + "\">\n"
					+ "\t<rdfs:domain rdf:resource=\"#" + domain + "\"/>\n"
					+ "</owl:ObjectProperty>\n";
		} else if(serialize == N_TRIPLES) {
			axiom = getN3Elem(property)+" "
					+ "rdfs:domain "
					+getN3Elem(domain)+".";
		}
	    return axiom;
	}
	/* プロパティの定義(rangeのみ) */
	public String setPropertyRange(String property, String range) {
		String axiom = new String();
		if (serialize == XML) {
			axiom = "<owl:ObjectProperty rdf:ID=\"" + property + "\">\n"
					+ "\t<rdfs:range rdf:resource=\"#" + range + "\"/>\n"
					+ "</owl:ObjectProperty>\n";
		} else if(serialize == N_TRIPLES) {
			axiom = getN3Elem(property)+" "
					+ "rdfs:range "
					+ getN3Elem(range)+".";
		}
	    return axiom;
	}
	/* サブプロパティの定義 */
	public String setSubPropertyOf(String mainProperty, String subProperty) {
		String axiom = new String();
		if (serialize == XML) {
			axiom = "<owl:ObjectProperty rdf:ID=\"" + mainProperty + "\">\n"
					+ "\t<rdfs:subPropertyOf rdf:resource=\"#" + subProperty + "\"/>\n"
					+ "</owl:ObjectProperty>\n";
		} else if(serialize == N_TRIPLES) {
			axiom = getN3Elem(mainProperty)+" "
					+ "rdfs:subPropertyOf "
					+ getN3Elem(subProperty)+".";
		}
		return axiom;
	}

	public String setDefaultTriple(String s, String p, String o) {
		String axiom = new String();
		if (serialize == XML) {
			axiom = "<rdfs:Resource rdf:ID=\""+ s +"\">\n"
					+ "\t<"+p+" rdf:resource=\"" + o + "\"/>\n"
					+ "</rdfs:Resource>\n";
		}else if(serialize == N_TRIPLES) {
			axiom = getN3Elem(s)+" "
					+ getN3Elem(p)+" "
					+ getN3Elem(o)+".";
		}
		return axiom;
	}

	private String getN3Elem(String elem) {
		boolean isOntLang = false;
		for(final String regex: ontLangRegexes) {
			if(elem.matches(regex)) {
				isOntLang = true;
				break;
			}
		}
		if(elem.matches("\\d+")) {	// 数値なら
			elem = "\""+elem+"\"";		// リテラルとして扱う
		}else if(isOntLang) {		// rules/ontLangRegexesで指定したオントロジー言語なら
			;							// そのまま
		}else {						// それ以外なら
			elem = "<"+elem+">";		// URIの記法に則る
		}
		return elem;
	}
}

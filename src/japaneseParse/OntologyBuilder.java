package japaneseParse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OntologyBuilder {
	public String body; // RDF/XMLの本文
	public String sub;
	/*
	public String saveFile;				// 出力ファイル名
	static String saveDir = "owls/";	// 出力ファイルを保管するディレクトリ
	static String saveExt = ".owl"; 	// owlの拡張子
	*/
	public OntologyBuilder() {
		body = "<?xml version=\"1.0\"?>\n"
				+ "<rdf:RDF\n"
				+ "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+ "\txmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "\txmlns=\"http://www.owl-ontologies.com/unnamed.owl#\"\n"
				+ "\txml:base=\"http://www.owl-ontologies.com/unnamed.owl\">\n"
				+ "<owl:Ontology rdf:about=\"\"/>\n";
	}

	/* <rdf:RDF>タグを閉じてRDF/XMLファイルを完結させる */
	public void close() {
		body += "</rdf:RDF>";
	}
	/* RDF/XMLファイルを出力する */
	public void output(String saveFile) {
		close();
		File file = new File(saveFile);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(body);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/* txtファイルを出力する.確認用 */
	public void output2(String saveFile) {
		close();
		File file = new File(saveFile);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(sub);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void addText(String text) {
		sub += text;
	}
	
	public void writeOntology(List<String> uri, List<List<Integer>> triples) {
		for(final List<Integer> triple: triples) {
			System.out.println(triple);
			int s = triple.get(0);
			int p = triple.get(1);
			int o = triple.get(2);
			switch (p) {
	    	case 0: // instance
	    		setType(uri.get(o), uri.get(s));
	    		break;
	    	case 1: // subclasses
	    		setSubClassOf(uri.get(o), uri.get(s));
	    		break;
	    	case 2: // subproperties
	    		setSubPropertyOf(uri.get(o), uri.get(s));
	    		break;
	    	case 3: // s_domains
	    		setPropertyDomain(uri.get(s), uri.get(o));
	    		break;
	    	case 4: // s_ranges
	    		setPropertyRange(uri.get(s), uri.get(o));
	    		break;
	    	default:
	    		setProperty(uri.get(p), uri.get(s), uri.get(o));
	    	}
		}
	}
	
	/*** body(本文に)公理を追記する ***/
	/* インスタンスの定義 */
	public String setType(String member, String mainClass) {
		String axiom = "<owl:Class rdf:ID=\"" + member + "\">\n"
				 + "\t<rdf:type rdf:resource=\"#" + mainClass + "\"/>\n"
				 + "</owl:Class>\n";
		body += axiom;
		return axiom;
	}
	/* クラスを定義するだけ */
	public String setClass(String className) {
		String axiom = "<owl:Class rdf:ID=\"" + className + "\"/>\n";
		body += axiom;
		return axiom;
	}
	/* サブクラスの定義 */
	public String setSubClassOf(String mainClass, String subClass) {
		String axiom = "<owl:Class rdf:ID=\"" + mainClass + "\">\n"
				 + "\t<rdfs:subClassOf rdf:resource=\"#" + subClass + "\"/>\n"
				 + "</owl:Class>\n";
		body += axiom;
		return axiom;
	}
	/* プロパティの定義(domain,range両方) */
	public String setProperty(String property, String domain, String range) {
	    String axiom = "<owl:ObjectProperty rdf:ID=\"" + property + "\">\n"
	    		+ "\t<rdfs:domain rdf:resource=\"#" + domain + "\"/>\n"
	    		+ "\t<rdfs:ragne rdf:resource=\"#" + range + "\"/>\n"
	    		+ "</owl:ObjectProperty>\n";
		body += axiom;
		return axiom;
	}
	/* プロパティの定義(domainのみ) */
	public String setPropertyDomain(String property, String domain) {
	    String axiom = "<owl:ObjectProperty rdf:ID=\"" + property + "\">\n"
	    		+ "\t<rdfs:domain rdf:resource=\"#" + domain + "\"/>\n"
	    		+ "</owl:ObjectProperty>\n";
		body += axiom;
		return axiom;
	}
	/* プロパティの定義(rangeのみ) */
	public String setPropertyRange(String property, String range) {
	    String axiom = "<owl:ObjectProperty rdf:ID=\"" + property + "\">\n"
	    		+ "\t<rdfs:ragne rdf:resource=\"#" + range + "\"/>\n"
	    		+ "</owl:ObjectProperty>\n";
		body += axiom;
		return axiom;
	}
	/* サブプロパティの定義 */
	public String setSubPropertyOf(String mainProperty, String subProperty) {
		String axiom = "<owl:ObjectProperty rdf:ID=\"" + mainProperty + "\">\n"
				 + "\t<rdfs:subPropertyOf rdf:resource=\"#" + subProperty + "\"/>\n"
				 + "</owl:ObjectProperty>\n";
		return axiom;
	}
	
	
	/* 本文を表示 */
	public void print() {
		System.out.println("---------------------------------------------------------------");
		System.out.println(body);
		System.out.println("---------------------------------------------------------------");
	}
}

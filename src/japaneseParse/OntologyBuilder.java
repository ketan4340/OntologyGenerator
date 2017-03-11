package japaneseParse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OntologyBuilder {
	//public String body; 		// RDF/XMLの本文
	public List<String> uri;
	public List<List<Integer>> triples;
	public boolean xml_n3;		// RDF/XML形式ならtrue,N-Triples形式ならfalse
	public String extension;	// 拡張子
	private List<String> ontLangRegexes;
	
	public OntologyBuilder(String xml_n3, List<String> uri, List<List<Integer>> triples) {
		this.uri = uri;
		this.triples = triples;
		if(xml_n3.equals("xml")) {
			this.xml_n3 = true;
			extension = ".xmlowl";
		}else if(xml_n3.equals("n3")) {
			extension = ".n3owl";
			this.xml_n3 = false;
		}
		ontLangRegexes = new ArrayList<String>();
		try {
			String fileName = "ontLangRegex.txt";
			File file = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String regex = br.readLine();
			while(regex != null) {
				ontLangRegexes.add(regex);
				regex = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* RDF/XMLファイルを出力する */
	public void output(String saveFile) {
		saveFile += extension;
		File file = new File(saveFile);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			String rdf = "";
			if(xml_n3) {
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
				for(final List<Integer> triple: triples) {
					rdf = getRDF(triple);
					bw.write(rdf);
					bw.newLine();
				}
				bw.write("</rdf:RDF>");
			}else {
				bw.write("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>."+"\n"
				+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>."+"\n"
				+ "@prefix owl: <http://www.w3.org/2002/07/owl#>."+"\n"
				+ "@prefix dc: <http://purl.org/dc/elements/1.1/>."+"\n");
				bw.newLine();
				for(final List<Integer> triple: triples) {
					rdf = getRDF(triple);
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
	
	public String getRDF(List<Integer> triple) {
		String rdf;
		int s = triple.get(0);
		int p = triple.get(1);
		int o = triple.get(2);
		
		rdf = setDefaultTriple(uri.get(s), uri.get(p), uri.get(o));
		/*
		switch (p) {
		case 0: // instance
			rdf = setType(uri.get(o), uri.get(s));
			break;
		case 1: // subclasses
	    	rdf = setSubClassOf(uri.get(o), uri.get(s));
	    	break;
	    case 2: // subproperties
	    	rdf = setSubPropertyOf(uri.get(o), uri.get(s));
	    	break;
	  	case 3: // s_domains
	  		rdf = setPropertyDomain(uri.get(s), uri.get(o));
	   		break;
	   	case 4: // s_ranges
	   		rdf = setPropertyRange(uri.get(s), uri.get(o));
	   		break;
	   	default:
	   		rdf = setProperty(uri.get(p), uri.get(s), uri.get(o));
	   }
	   */
		return rdf;
	}
	
	/*** body(本文に)公理を追記する ***/
	/* インスタンスの定義 */
	public String setType(String instance, String mainClass) {
		String axiom = new String();
		if(xml_n3) {
			axiom = "<owl:Class rdf:ID=\"" + instance + "\">\n"
					+ "\t<rdf:type rdf:resource=\"#" + mainClass + "\"/>\n"
					+ "</owl:Class>\n";
		}else {
			axiom = getN3Elem(instance)+" "
					+ "rdf:type "
					+ getN3Elem(mainClass)+".";
		}
		return axiom;
	}
	/* クラスを定義するだけ */
	public String setClass(String className) {
		String axiom = new String();
		if(xml_n3) {
			axiom = "<owl:Class rdf:ID=\"" + className + "\"/>\n";
		}else {
			axiom = getN3Elem(className)+" "
					+ "rdf:type "
					+ "owl:Class.";
		}
		return axiom;
	}
	/* サブクラスの定義 */
	public String setSubClassOf(String mainClass, String subClass) {
		String axiom = new String();
		if(xml_n3) {
			axiom = "<owl:Class rdf:ID=\"" + mainClass + "\">\n"
					+ "\t<rdfs:subClassOf rdf:resource=\"#" + subClass + "\"/>\n"
					+ "</owl:Class>\n";
		}else {
			axiom = getN3Elem(mainClass)+" "
					+ "rdfs:subClassOf "
					+ getN3Elem(subClass)+".";
		}
		return axiom;
	}
	/* プロパティの定義(domain,range両方) */
	public String setProperty(String property, String domain, String range) {
		String axiom = new String();
		if(xml_n3) {
			axiom = "<owl:ObjectProperty rdf:ID=\"" + property + "\">\n"
					+ "\t<rdfs:domain rdf:resource=\"#" + domain + "\"/>\n"
					+ "\t<rdfs:range rdf:resource=\"#" + range + "\"/>\n"
	    			+ "</owl:ObjectProperty>\n";
		}else {
			axiom = getN3Elem(property)+" "
					+ "rdfs:domain "+getN3Elem(domain)+"; "
					+ "rdfs:range "+getN3Elem(range)+".";
		}
	    return axiom;
	}
	/* プロパティの定義(domainのみ) */
	public String setPropertyDomain(String property, String domain) {
		String axiom = new String();
		if(xml_n3) {
			axiom = "<owl:ObjectProperty rdf:ID=\"" + property + "\">\n"
					+ "\t<rdfs:domain rdf:resource=\"#" + domain + "\"/>\n"
					+ "</owl:ObjectProperty>\n";
		}else {
			axiom = getN3Elem(property)+" "
					+ "rdfs:domain "
					+getN3Elem(domain)+".";
		}
	    return axiom;
	}
	/* プロパティの定義(rangeのみ) */
	public String setPropertyRange(String property, String range) {
		String axiom = new String();
		if(xml_n3) {
			axiom = "<owl:ObjectProperty rdf:ID=\"" + property + "\">\n"
					+ "\t<rdfs:range rdf:resource=\"#" + range + "\"/>\n"
					+ "</owl:ObjectProperty>\n";
		}else {
			axiom = getN3Elem(property)+" "
					+ "rdfs:range "
					+ getN3Elem(range)+".";
		}
	    return axiom;
	}
	/* サブプロパティの定義 */
	public String setSubPropertyOf(String mainProperty, String subProperty) {
		String axiom = new String();
		if(xml_n3) {
			axiom = "<owl:ObjectProperty rdf:ID=\"" + mainProperty + "\">\n"
					+ "\t<rdfs:subPropertyOf rdf:resource=\"#" + subProperty + "\"/>\n"
					+ "</owl:ObjectProperty>\n";
		}else {
			axiom = getN3Elem(mainProperty)+" "
					+ "rdfs:subPropertyOf "
					+ getN3Elem(subProperty)+".";
		}
		return axiom;
	}
	
	public String setDefaultTriple(String s, String p, String o) {
		String axiom = new String();
		if(xml_n3) {
			axiom = "<rdfs:Resource rdf:ID=\""+ s +"\">\n"
					+ "\t<"+p+" rdf:resource=\"" + o + "\"/>\n"
					+ "</rdfs:Resource>\n";
		}else {
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
		if(elem.matches("\\d+")) {
			elem = "\""+elem+"\"";
		}else if(isOntLang) {
			;		// そのままっつーこと
		}else {
			elem = "<"+elem+">";
		}
		return elem;
	}	
}

package japaneseParse;

import java.util.ArrayList;
import java.util.List;

public class Node {
	public static List<Integer> equaltriples = new ArrayList<Integer>();
    public static List<Integer> poequals = new ArrayList<Integer>();
    public static List<Integer> soequals = new ArrayList<Integer>();
    public static List<Integer> spequals = new ArrayList<Integer>();
    
    public List<Integer> s_predicates;
	public List<Integer> s_objects;
	public List<Integer> p_subjects;
	public List<Integer> p_objects;
	public List<Integer> o_subjects;
	public List<Integer> o_predicates;
	
	//1.主語・述語・目的語のうち2つが等しい
	public List<Integer> s_poequals;
	public List<Integer> p_soequals;
	public List<Integer> o_spequals;
	//2.RDF(S)語彙の主語と目的語
	public List<Integer> instances;
	public List<Integer> types;
	public List<Integer> subclasses;
	public List<Integer> superclasses;
	public List<Integer> subproperties;
	public List<Integer> superproperties;
	public List<Integer> s_domains;
	public List<Integer> domains;
	public List<Integer> s_ranges;
	public List<Integer> ranges;
	
	public Node(){
		s_predicates = new ArrayList<Integer>();
		s_objects = new ArrayList<Integer>();
		p_subjects = new ArrayList<Integer>();
		p_objects = new ArrayList<Integer>();
		o_subjects = new ArrayList<Integer>();
		o_predicates = new ArrayList<Integer>();
		
		s_poequals = new ArrayList<Integer>();
		p_soequals = new ArrayList<Integer>();
		o_spequals = new ArrayList<Integer>();
		
		instances = new ArrayList<Integer>();
		types = new ArrayList<Integer>();
		subclasses = new ArrayList<Integer>();
		superclasses = new ArrayList<Integer>();
		subproperties = new ArrayList<Integer>();
		superproperties = new ArrayList<Integer>();
		s_domains = new ArrayList<Integer>();
		domains = new ArrayList<Integer>();
		s_ranges = new ArrayList<Integer>();
		ranges = new ArrayList<Integer>();
	}
	
	public void s_addChild(int s, int p, int o){
		s_predicates.add(p);
		s_objects.add(o);
    	//主語・述語・目的語が同一のトリプルを保存
    	if(s==p && s==o)
    		equaltriples.add(s);
    	//主語・述語・目的語のうち2つが同一のトリプルを保存
		if(p==o){
			poequals.add(s);
			s_poequals.add(p);
		}
    	//RDF(S)語彙を述語とするトリプルの主語と目的語
    	switch (p) {
    	case 0: types.add(o);
    		break;
    	case 1: superclasses.add(o);
    		break;
    	case 2: superproperties.add(o);
    		break;
    	case 3: domains.add(o);
    		break;
    	case 4: ranges.add(o);
    		break;
    	}
	}
	public void p_addChild(int s, int p, int o){
		p_subjects.add(s);
		p_objects.add(o);
    	//主語・述語・目的語のうち2つが同一のトリプルを保存
		if(s==o){
			soequals.add(p);
			p_soequals.add(s);
		}
	}
	public void o_addChild(int s, int p, int o){
		o_subjects.add(s);
		o_predicates.add(p);
    	//主語・述語・目的語のうち2つが同一のトリプルを保存
		if(s==p){
			spequals.add(o);
			o_spequals.add(s);
		}
    	//RDF(S)語彙を述語とするトリプルの主語と目的語
    	switch (p) {
    	case 0: instances.add(s);
    		break;
    	case 1: subclasses.add(s);
    		break;
    	case 2: subproperties.add(s);
    		break;
    	case 3: s_domains.add(s);
    		break;
    	case 4: s_ranges.add(s);
    		break;
    	}
	}
	
	public static List<Node> setTriples2Nodes(List<String> uri, List<List<Integer>> triples) {
		List<Node> nodes = new ArrayList<Node>();
		for(int i = 0; i < uri.size(); i++) {
			nodes.add(new Node());
		}
		// トリプルをnodesに追加
		for (final List<Integer> triple: triples) {
			int s = triple.get(0);
			int p = triple.get(1);
			int o = triple.get(2);    	
			nodes.get(s).s_addChild(s, p, o);
			nodes.get(p).p_addChild(s, p, o);
			nodes.get(o).o_addChild(s, p, o);
		}
		return nodes;
	}
	
	public void printNode1() {
		System.out.println("sp"+s_predicates + ",so"+s_objects + ",ps"+p_subjects + ",po"+p_objects + ",os"+o_subjects + ",op"+o_predicates);
	}
    public void printNode2() {
    	System.out.println("p=o"+s_poequals+",s=o"+p_soequals+",o=s"+o_spequals+",inst"+instances+",tp"+types+",subc"+subclasses+",supc"+superclasses+",subp"+subproperties+",supp"+superproperties+",sdm"+s_domains+",dm"+domains+",srn"+s_ranges+",rn"+ranges);
    }
}
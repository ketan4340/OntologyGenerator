package data.id;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import grammar.Sentence;

public class ModelIDMap extends IDLinkedMap<Model> {
	private static final long serialVersionUID = -7922615222139193991L;
	
	
	/***********************************/
	/********** Static Method **********/
	/***********************************/
	public static SentenceIDMap create(List<Sentence> sentenceList) {
		LinkedHashMap<Sentence, IDTuple> lhm = sentenceList.stream()
				.collect(Collectors.toMap(s -> s, s -> new IDTuple(), (e1, e2) -> e1, LinkedHashMap::new));
		return new SentenceIDMap(lhm);
	}
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public ModelIDMap() {
		super();
	}
	public ModelIDMap(LinkedHashMap<Model, IDTuple> m) {
		super(m);
	}


	/***********************************/
	/********** Member Method **********/
	/***********************************/

}
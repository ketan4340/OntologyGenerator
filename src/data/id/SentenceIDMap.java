package data.id;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import grammar.Sentence;

public class SentenceIDMap extends IDLinkedMap<Sentence> {
	private static final long serialVersionUID = -2957160502289250254L;
	

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
	public SentenceIDMap() {
		super();
	}
	public SentenceIDMap(LinkedHashMap<Sentence, IDTuple> m) {
		super(m);
	}


	/***********************************/
	/********** Member Method **********/
	/***********************************/
	public void setLongSentenceID() {
		entrySet().stream().forEach(e -> e.getValue().setLongSentenceID(e.getKey().id));
	}
	public void setShortSentenceID() {
		entrySet().stream().forEach(e -> e.getValue().setShortSentenceID(e.getKey().id));
	}
	
	
}

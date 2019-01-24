package data.id;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import grammar.sentence.Sentence;

public class SentenceIDMap extends IDLinkedMap<Sentence> {
	private static final long serialVersionUID = -2957160502289250254L;


	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public SentenceIDMap() {
		super();
	}
	public SentenceIDMap(int initialCapacity) {
		super(initialCapacity);
	}
	public SentenceIDMap(LinkedHashMap<Sentence, IDTuple> m) {
		super(m);
	}


	/* ================================================== */
	/* ================== Static Method ================= */
	/* ================================================== */
	public static SentenceIDMap createFromList(List<Sentence> sentenceList) {
		return sentenceList.stream().collect(Collectors.toMap(
				s -> s, 
				s -> new IDTupleByModel(""), 
				(e1, e2) -> e1, 
				SentenceIDMap::new));
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public void setLongSentence() {
		forEach((s, idt) -> idt.setLongSentence(s));
	}
	public void setShortSentence() {
		forEach((s, idt) -> idt.setShortSentence(s));
	}

	public List<String> toStringList() {
		return keySet().stream().map(Sentence::name).collect(Collectors.toList());
	}
	
	public SentenceIDMap replaceSentence2Sentences(Map<Sentence, List<Sentence>> replaceMap) {
		SentenceIDMap sm = new SentenceIDMap();
		replaceMap.forEach(
				(from, toList) -> 
					toList.forEach(s -> {
						IDTuple cloneTuple = get(from).clone();						
						sm.put(s, cloneTuple);	
					})
		);
		return sm;
	}
	public ModelIDMap replaceSentence2Model(Map<Sentence, Model> replaceMap) {
		ModelIDMap mm = new ModelIDMap();
		replaceMap.forEach((st, md) -> mm.put(md, get(st).clone()));
		return mm;
	}

}

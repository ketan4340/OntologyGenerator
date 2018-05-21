package data.id;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class IDLinkedMap<K> {
	private LinkedHashMap<K, IDTuple> map;
	
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public IDLinkedMap() {
		setMap(new LinkedHashMap<K, IDTuple>());
	}
	public IDLinkedMap(List<K> list) {
		setMap(
				list.stream().collect(Collectors.toMap(
						s -> s, 
						s -> new IDTuple(), 
						(e1, e2) -> e1, 
						LinkedHashMap::new))
		);
	}
	
	
	/***********************************/
	/********** Member Method **********/
	/***********************************/
	public void scoreInit() {
		
	}
	public void setLongSentenceID() {
		map.entrySet().stream().forEach(e -> e.getValue().setLongSentenceID(e.getKey().id));
	}
	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public LinkedHashMap<K, IDTuple> getMap() {
		return map;
	}
	public void setMap(LinkedHashMap<K, IDTuple> map) {
		this.map = map;
	}
	
}
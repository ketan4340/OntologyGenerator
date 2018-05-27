package data.id;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;

import data.RDF.MyJenaModel;

public class ModelIDMap extends IDLinkedMap<MyJenaModel> {
	private static final long serialVersionUID = -7922615222139193991L;
	
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public ModelIDMap() {
		super();
	}
	public ModelIDMap(int initialCapacity) {
		super(initialCapacity);
	}
	public ModelIDMap(LinkedHashMap<MyJenaModel, IDTuple> m) {
		super(m);
	}


	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public void setRuleID() {
		forEach((k, v) -> v.setRDFRuleID(k.id()));
	}
	
	public Model uniteModels() {
		Model unionModel = ModelFactory.createDefaultModel();
		forEachKey(m -> unionModel.add(m.getModel()));
		return unionModel;
	}
	
	public ModelIDMap replaceModel2Models(Map<MyJenaModel, List<MyJenaModel>> replaceMap) {
		ModelIDMap mm = new ModelIDMap();
		replaceMap.forEach((md, mds) -> 
		mds.forEach(m -> mm.put(md, get(md).clone()))
		);
		return mm;
	}

	/**
	 * モデルをRDFトリプルのリストに置き換え，そのIDTupleを返す.
	 * @return
	 */
	public List<IDTuple> IDList() {
		List<IDTuple> idtupleList = new ArrayList<>();
		for (Map.Entry<MyJenaModel, IDTuple> e : entrySet()) {
			MyJenaModel model = e.getKey();
			IDTuple ids = e.getValue();
			for (Iterator<Statement> itr = model.listStatements(); itr.hasNext(); ) {
				Statement st = itr.next();
				IDTuple id = ids.clone();
				id.setTripleID(MyJenaModel.tripleSum++);
				id.setSubject(st.getSubject().toString());
				id.setPredicate(st.getPredicate().toString());
				id.setObject(st.getObject().toString());
				idtupleList.add(id);
			}
		}
		return idtupleList;
	}
		
}
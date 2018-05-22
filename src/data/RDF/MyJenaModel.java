package data.RDF;

import org.apache.jena.rdf.model.Model;

import data.id.Identifiable;


public class MyJenaModel implements Identifiable{
	private static int sum = 0;
	
	private final int id;
	private Model model;
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public MyJenaModel() {
		this.id = sum++;
	}
	public MyJenaModel(Model m) {
		this();
		setModel(m);
	}
	
	/****************************************/
	/**********    Static Method    *********/
	/****************************************/


	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	
	/**********   Getter, Setter   **********/
	public int getID() {
		return id;
	}
	public Model getModel() {
		return model;
	}
	public void setModel(Model m) {
		this.model = m;
	}
	
	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public int id() {
		return getID();
	}


}

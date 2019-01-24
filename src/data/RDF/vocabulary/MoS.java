package data.RDF.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class MoS {
	public static final String uri = "http://www.sw.cei.uec.ac.jp/k-lab/k-tanabe/mos#";


	public static final String getURI() {
		return uri;
	}
	
    protected static final Resource resource( String local ) {
    	return ResourceFactory.createResource( uri + local );
    }

    protected static final Property property( String local ) {
    	return ResourceFactory.createProperty( uri, local );
    }
	
	/* ================================================== */
	/* ====================== Class ===================== */
	/* ================================================== */
	public static final Resource Existence = resource("Existence");
	public static final Resource Degree = resource("Degree");
	public static final Resource CategoremResource = resource("CategoremResource");
	

	/* ================================================== */
	/* ================= Object Property ================ */
	/* ================================================== */
	public static final Property exist = property("exist");
	public static final Property attributeOf = property("attributeOf");
	public static final Property howMany = property("howMany");
	public static final Property of = property("of");
	
	/* ================================================== */
	/* ================== Data Property ================= */
	/* ================================================== */
	public static final Property name = property("name");
	

	/* ================================================== */
	/* =================== Individual =================== */
	/* ================================================== */
	public static final Resource maximum = resource("maximum");
	public static final Resource positive = resource("positive");
	public static final Resource medium = resource("medium");
	public static final Resource negative = resource("negative");
	public static final Resource minimum = resource("minimum");

}

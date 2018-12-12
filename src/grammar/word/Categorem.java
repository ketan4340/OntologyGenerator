package grammar.word;

import java.util.Collections;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.GOO;
import data.RDF.vocabulary.JASS;
import data.RDF.vocabulary.MoS;
import grammar.morpheme.Morpheme;
import language.pos.CabochaTags;

public class Categorem extends Word implements Resourcable {
	public static final Categorem EMPTY_CATEGOREM = new Categorem(Collections.emptyList());

	/* ================================================== */
	/* ================== Constructor =================== */
	/* ================================================== */
	public Categorem(List<Morpheme> morphemes) {
		super(morphemes);
	}
	public Categorem(Morpheme... morphemes) {
		super(morphemes);
	}
	public Categorem(String name, CabochaTags tags) {
		super(name, tags);
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	/** 全く同じWordを複製する */
	@Override
	public Categorem clone() {
		return new Categorem(children);
	}
	
	/* ================================================== */
	/* ================ Interface Method ================ */ 
	/* ================================================== */
	@Override
	public Resource toJASS(Model model) {
		return super.toJASS(model)
				.addProperty(RDF.type, JASS.Categorem)
				.addProperty(JASS.means, createProxyNode(model));
	}
	
	@Override
	public String resourceURI() {
		if (mainPoS().equals("名詞"))
			return GOO.uri + name();
		return GOO.uri + infinitive();
	}
	
	@Override
	public Resource createResource(Model m) {
		return m.createResource(resourceURI())
				.addProperty(RDF.type, MoS.CategoremResource);
	}
	
	@Override
	public String proxyNodeURI() {
		return JASS.getURI()+"proxynode/"+getClass().getSimpleName().toLowerCase()+id();
	}
	
}

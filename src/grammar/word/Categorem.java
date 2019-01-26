package grammar.word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.GOO;
import data.RDF.vocabulary.JASS;
import data.RDF.vocabulary.MoS;
import grammar.morpheme.Morpheme;

public class Categorem extends Word implements Resourcable {
	public static final Categorem EMPTY_CATEGOREM = new Categorem(Collections.emptyList());

	private Optional<NamedEntityTag> netag = Optional.empty();
	
	/* ================================================== */
	/* ================== Constructor =================== */
	/* ================================================== */
	public Categorem(List<Morpheme> morphemes) {
		super(morphemes);
	}
	public Categorem(Morpheme... morphemes) {
		super(morphemes);
	}
	private Categorem(Categorem categorem) {
		this(new ArrayList<>(categorem.children));
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public void setNETag(NamedEntityTag netag) {
		this.netag = Optional.ofNullable(netag);
	}
	public Optional<NamedEntityTag> getNETag() {
		return netag;
	}
	
	/* ================================================== */
	/* ================ Interface Method ================ */ 
	/* ================================================== */
	@Override
	public Categorem clone() {
		return new Categorem(this);
	}

	@Override
	public Resource toJASS(Model model) {
		Resource categoremResource = super.toJASS(model)
				.addProperty(RDF.type, JASS.Categorem)
				.addProperty(JASS.means, createProxyNode(model));
		netag.ifPresent(t -> categoremResource.addProperty(JASS.namedEntity, t.toJASS()));
		return categoremResource;
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
	
	
	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public int hashCode() {
		return Objects.hash(children, netag);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Categorem))
			return false;
		Categorem other = Categorem.class.cast(obj);
		return Objects.equals(this.id(), other.id()) &&
				Objects.equals(this.getChildren(), other.getChildren()) &&
				Objects.equals(this.netag, other.netag);
	}
	
}

package data.RDF.rule;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class RDFRulesSet extends HashSet<RDFRules> {
	private static final long serialVersionUID = 937700710931656086L;

	
	public RDFRulesSet() {
		super();
	}

	public RDFRulesSet(Collection<RDFRules> rulesCol) {
		super(rulesCol);
	}

	
	
	/** ログの出力用 */
	public List<String> toStringList() {
		return stream().flatMap(r -> r.stream()).map(RDFRule::toString).collect(Collectors.toList());
	}
}
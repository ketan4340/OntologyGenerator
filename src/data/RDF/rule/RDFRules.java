package data.RDF.rule;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RDFRules {

	private final LinkedHashSet<RDFRule> rules;

	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	public RDFRules(LinkedHashSet<RDFRule> rules) {
		this.rules = rules;
	}


	/* ================================================== */
	/* ==========         Member Method        ========== */
	/* ================================================== */
	/** ログの出力用 */
	public List<String> toStringList() {
		return rules.stream().map(RDFRule::toString).collect(Collectors.toList());
	}

	/* ================================================== */
	/* ==========       Delegate  Method       ========== */
	/* ================================================== */
	public Stream<RDFRule> stream() {
		return rules.stream();
	}
	public void forEach(Consumer<? super RDFRule> action) {
		rules.forEach(action);
	}

	/* ================================================== */
	/* ==========            Getter            ========== */
	/* ================================================== */
	public LinkedHashSet<RDFRule> getRules() {
		return rules;
	}


	/* ================================================== */
	/* ==========        Object  Method        ========== */
	/* ================================================== */
	@Override
	public String toString() {
		return rules.stream()
				.map(RDFRule::toString)
				.collect(Collectors.joining("\n"));
	}
}
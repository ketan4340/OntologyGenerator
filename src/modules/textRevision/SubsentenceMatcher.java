package modules.textRevision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import grammar.clause.Clause;
import grammar.pattern.ClausePattern;
import grammar.pattern.SubsentencePattern;
import grammar.sentence.ClauseSequence;
import grammar.sentence.Sentence;
import util.Range;

public final class SubsentenceMatcher {

	SubsentencePattern parentPattern;
	private final Sentence sentence;
    
	public SubsentenceMatcher(SubsentencePattern parent, Sentence sentence) {
		this.parentPattern = parent;
		this.sentence = sentence;
	}

	public boolean matches() {
		int patternsize = parentPattern.size();
		final int checkTimes = sentence.getChildren().size() - patternsize + 1;
		if (checkTimes < 1) 
			return false;
		for (int ci = 0; ci < checkTimes; ci++) {
			int from = ci, to = ci + patternsize;
			ClauseSequence cs = sentence.subClauseSequence(from, to);
			if (match(cs))	// 一つでもマッチした時点で真 
				return true;
		}
        return false;
	}
	public List<ClauseSequence> groups() {
		int patternsize = parentPattern.size();
		final int checkTimes = sentence.getChildren().size() - patternsize + 1;
		if (checkTimes < 1) 
			return Collections.emptyList();
		List<ClauseSequence> groups = new ArrayList<>(checkTimes);
		for (int ci = 0; ci < checkTimes; ci++) {
			int from = ci, to = ci + patternsize;
			ClauseSequence cs = sentence.subClauseSequence(from, to);
			if (match(cs))
		        groups.add(cs);
        }
        return groups;
	}
	public Optional<ClauseSequence> firstGroup() {
		return firstRange().map(r -> sentence.subClauseSequence(r.from(), r.to())); 
	}
    
	public Optional<Range> firstRange() {
		int patternsize = parentPattern.size();
		final int checkTimes = sentence.getChildren().size() - patternsize + 1;
		if (checkTimes < 1) 
			return Optional.empty();
		for (int ci = 0; ci < checkTimes; ci++) {
			int from = ci, to = ci + patternsize;
			if (match(from, to))
		        return Optional.of(new Range(from, to));
        }
        return Optional.empty();
	}
	
	public boolean replaceFirst(Function<ClauseSequence, Clause<?>> function) {
		Optional<Range> range_opt = firstRange();
		if (!range_opt.isPresent()) 
			return false;
		range_opt.ifPresent(r -> {
			ClauseSequence cs = sentence.subClauseSequence(r.from(), r.to());
			Clause<?> newcls = function.apply(cs);
			cs.clear();
			cs.add(newcls);
			Set<Clause<?>> formerDependeds = cs.stream().map(sentence::clausesDepending).flatMap(Set::stream).collect(Collectors.toSet());
			sentence.gatherDepending(newcls, formerDependeds);
		});
		return true;
	}
	
	private boolean match(int from, int to) {
		return match(sentence.subClauseSequence(from, to));
	}
    private boolean match(ClauseSequence cs) {
		ListIterator<Clause<?>> cls_litr = cs.listIterator();
		ListIterator<ClausePattern> cp_litr = parentPattern.listIterator();
		while (cls_litr.hasNext() && cp_litr.hasNext()) {
			if (!cp_litr.next().matches(cls_litr.next()))
				return false;
		}
		return true;
    }

    
}

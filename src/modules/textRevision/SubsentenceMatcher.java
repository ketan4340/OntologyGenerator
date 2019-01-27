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
			int from = ci, to = from + patternsize;
			if (match(from, to))
		        return Optional.of(new Range(from, to));
        }
        return Optional.empty();
	}
	public Optional<Range> lastRange() {
		int patternsize = parentPattern.size();
		final int checkTimes = sentence.getChildren().size() - patternsize + 1;
		if (checkTimes < 1) 
			return Optional.empty();
		for (int ci = 0; ci < checkTimes; ci++) {
			int from = checkTimes - ci, to = from + patternsize;	// 後ろから遡る
			if (match(from, to))
		        return Optional.of(new Range(from, to));
        }
        return Optional.empty();
	}
	
	public boolean replaceFirst(Function<ClauseSequence, Clause<?>> function) {
		Optional<Range> range_opt = firstRange();
		if (!range_opt.isPresent()) 
			return false;
		range_opt.ifPresent(r -> replace(r, function));
		return true;
	}
	public boolean replaceLast(Function<ClauseSequence, Clause<?>> function) {
		Optional<Range> range_opt = lastRange();
		if (!range_opt.isPresent()) 
			return false;
		range_opt.ifPresent(r -> replace(r, function));
		return true;
	}
	private void replace(Range r, Function<ClauseSequence, Clause<?>> function) {
		ClauseSequence formerClauses = sentence.subClauseSequence(r.from(), r.to());
		Clause<?> latterClause = function.apply(formerClauses);
		Set<Clause<?>> formerDependeds = formerClauses.stream()
				.map(sentence::clausesDependTo).flatMap(Set::stream)
				.collect(Collectors.toSet());
		formerDependeds.removeAll(formerClauses);	// これから消す文節は要らない
		formerClauses.clear();
		formerClauses.add(latterClause);
		sentence.gatherDepending(latterClause, formerDependeds);
	}
	
	private boolean match(int from, int to) {
		return match(sentence.subClauseSequence(from, to));
	}
    private boolean match(ClauseSequence cs) {
		ListIterator<ClausePattern> cp_litr = parentPattern.listIterator();
		ListIterator<Clause<?>> cls_litr = cs.listIterator();
		while (cp_litr.hasNext() && cls_litr.hasNext()) {
			Clause<?> cls = cls_litr.next();
			if (!cp_litr.next().matches(cls))
				return false;
			// 各文節の係り先が後続の文節にないといけない
			if (cls_litr.hasNext()) {// 最後尾は後続がないのでOK
				List<Clause<?>> followingClauses = cs.subList(cls_litr.nextIndex(), cs.size());
				if (!followingClauses.contains(cls.getDepending()))
					return false;
			}
		}
		return true;
    }

}

package grammar.clause;

import java.util.List;

import grammar.word.Adjunct;
import grammar.word.Phrase;
import grammar.word.Word;

public class SerialClause extends AbstractClause<Phrase>{
	private static int clausesSum = 0;

	private final int id;
	
	public SerialClause(Phrase categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(categorem, adjuncts, others);
		id = clausesSum++;
	}

	
	@Override
	public int getID() {
		return id;
	}
}
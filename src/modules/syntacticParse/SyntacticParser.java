package modules.syntacticParse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cabocha.Cabocha;
import grammar.clause.Clause;
import grammar.morpheme.Morpheme;
import grammar.naturalLanguage.NaturalLanguage;
import grammar.sentence.Sentence;
import grammar.word.NamedEntityTag;

public class SyntacticParser {
	private Cabocha cabocha;

	/* ================================================== */
	/* ================== Constructor =================== */
	/* ================================================== */
	public SyntacticParser() {
		cabocha = new Cabocha();
	}
	public SyntacticParser(Path propPath) {
		Properties prop = new Properties();
		try (InputStream is = Files.newInputStream(propPath)) {
			prop.loadFromXML(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		cabocha = new Cabocha(prop);
	}


	/* ================================================== */
	/* ================= Member Method ================== */
	/* ================================================== */
	/**
	 * 自然言語文のリストを構文解析し，文のリストを返す.
	 * @param naturalLanguages 自然言語文のリスト
	 * @return 文のリスト
	 */
	public List<Sentence> parseSentences(List<NaturalLanguage> naturalLanguages) {
		List<String> parseResult = cabocha.parse(NaturalLanguage.toStringList(naturalLanguages));
		return new CabochaDecoder().decodeProcessOutput(parseResult);
	}

	public void supplyDatesNamedEntityTag(List<Sentence> sentences, Path dateWordsPath) {
		Set<String> dateWords;
		try (Stream<String> lines = Files.lines(dateWordsPath)) {
			dateWords = lines.collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		sentences.parallelStream()
		.flatMap(s -> s.getChildren().stream())
		.map(Clause<?>::getCategorem)
		.filter(c -> {
			Optional<NamedEntityTag> tag_opt = c.getNETag();
			if (!tag_opt.isPresent()) return true;
			if (tag_opt.get() == NamedEntityTag.OPTIONAL) return true;
			return false;
		})
		.filter(c -> {
			if (dateWords.contains(c.name())) return true;
			Stream<String> mrphs = c.getChildren().stream().map(Morpheme::name);
			if (mrphs.anyMatch(dateWords::contains)) return true;
			return false;
		}) 
		.forEach(c -> c.setNETag(NamedEntityTag.DATE));
	}
	
}

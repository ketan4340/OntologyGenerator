package modules.syntacticParse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import cabocha.Cabocha;
import grammar.naturalLanguage.NaturalLanguage;
import grammar.sentence.Sentence;

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

	/**
	 * 自然言語文のリストを構文解析し，文のリストを返す.
	 * @param naturalLanguages 自然言語文のリスト
	 * @return 文のリスト
	 */
	public List<Sentence> parseSentences(List<NaturalLanguage> naturalLanguages) {
		List<String> parseResult = cabocha.parse(NaturalLanguage.toStringList(naturalLanguages));
		return new CabochaDecoder().decodeProcessOutput(parseResult);
	}

}
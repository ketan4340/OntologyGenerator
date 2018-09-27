package grammar.tags;

public interface CabochaPoSInterface {

	/** 品詞 */
	String mainPoS();
	/** 品詞細分類1 */
	String subPoS1();
	/** 品詞細分類2 */
	String subPoS2();
	/** 品詞細分類3 */
	String subPoS3();
	/** 活用型 */
	String conjugation();
	/** 活用形 */
	String inflection();
	/** 原形 */
	String infinitive();
	/** 読み */
	String yomi();
	/** 発音 */
	String pronunciation();

}
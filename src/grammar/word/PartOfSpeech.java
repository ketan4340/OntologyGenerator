package grammar.word;

public enum PartOfSpeech {
	Categorem("自立語"),
	Adjunct("付属語"),
	Other("その他"),
	;
	
	/** フィールド変数 */
	private final String label;

	/** コンストラクタ */
	private PartOfSpeech(final String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
}
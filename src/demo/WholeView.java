package demo;

import java.awt.GridLayout;

import javax.swing.JFrame;

import demo.ontology.OntologyTableView;
import demo.textField.DocumentView;
import demo.textField.InputTextView;

public class WholeView extends JFrame{
	/**** Model ****/

	/**** Controller ****/

	/**** View ****/


	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	/**** 細かい子ビューの配置を決める ****/
	public WholeView(InputTextView iptView, OntologyTableView ontView, DocumentView docView) {
		super("OntologyGenerator");

		/* ウインドウ全体の設定 */
		setExtendedState(JFrame.MAXIMIZED_BOTH);		// ディスプレイ全体に表示
		setLocationRelativeTo(null);					// フレームを中央に表示
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// ウインドウを閉じたら終了
		setLayout(new GridLayout(1, 3));				// 横に3つ並べる

		/* 子ビュー(JPanel)を配置 */
		add(iptView);
		add(ontView);
		add(docView);

		setVisible(true);
	}
}

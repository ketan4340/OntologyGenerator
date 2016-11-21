package demos.gosen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.java.sen.SenFactory;
import net.java.sen.StringTagger;
import net.java.sen.dictionary.Token;

public class Main {

	public static void main(String[] args) {
		/* GoSen */
		StringTagger tagger = SenFactory.getStringTagger(null);
		List<Token> tokens = new ArrayList<Token>();
		String jpnlang = "クジラは水生の動物であり哺乳類である。";
	/*
		try {
			tagger.analyze(jpnlang, tokens);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 解析結果の中身をいろいろ出力してみる
		for (Token token : tokens) {
		    System.out.println("=====");
		    System.out.println("surface : " + token.getSurface());
		    System.out.println("cost : " + token.getCost());
		    System.out.println("length : " + token.getLength());
		    System.out.println("start : " + token.getStart());
		    System.out.println("basicForm : " + token.getMorpheme().getBasicForm());
		    System.out.println("conjugationalForm : " + token.getMorpheme().getConjugationalForm());
		    System.out.println("conjugationalType : " + token.getMorpheme().getConjugationalType());
		    System.out.println("partOfSpeech : " + token.getMorpheme().getPartOfSpeech());
		    System.out.println("pronunciations : " + token.getMorpheme().getPronunciations());
		    System.out.println("readings : " + token.getMorpheme().getReadings());
		}
		*/
		
		/* CaboCha */
		Run(jpnlang);
	}
	
	//String型で文字列を受け取り、それを係り受け解析する関数
	public static void Run(String text){
		try {
			
			//UTF-8のBOMを除去するための準備←textファイルから読み込む場合を考慮
			byte [] bytes = {-17, -69, -65};
			String btmp= new String(bytes, "UTF-8");
			
			//BOM除去
			text=text.replaceAll(btmp, "");
		
			//cabochaの実行開始　ラティス形式で出力(-f1の部分で決定、詳しくはcabochaのhelp参照)
			ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/cabocha", "-f3");
			Process process = pb.start();
			
			//実行途中で文字列を入力(コマンドプロンプトで文字を入力する操作)
			OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream(), "UTF-8");
			osw.write(text);
			osw.close();
			
			//出力結果を読み込む
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			
			//一行ずつ読み込むための文字列の変数を用意
			String Line="";
			
			//出力結果の各行毎に配列へ格納するためのリストを用意
			ArrayList<String> out = new ArrayList<>();
		
			//最後の行までやり続ける
			while ((Line = br.readLine()) != null) {
				//読み込んだ行をリストへ格納
				out.add(Line);
					
				//行をコンソールへ表示
				System.out.println(Line);
			}
		
			//プロセス終了
			process.destroy();
			process.waitFor();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

package japaneseParse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LoadCSV {
	private List<String> uri;
	private List<List<Integer>> triples; 
	
	LoadCSV(String fileName) {
		uri = new ArrayList<String>();
		triples = new ArrayList<List<Integer>>();
		//　標準RDF(S)語彙の登録
		uri.add("rdf:type");
		uri.add("rdfs:subClassOf");
		uri.add("rdfs:subPropertyOf");
		uri.add("rdfs:domain");
		uri.add("rdfs:range");
		//String regexComma = "(?<!\\\\),"; // \,は無視する
		String regexComma = ",";
		Pattern pComma = Pattern.compile(regexComma);
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String line;	

            // ファイルを行単位で読む
			while ( (line = br.readLine()) != null ) {
                // カンマで分割したString配列を得る
				String[] array = pComma.split(line);
				String[] triple = new String[3];	// 必要な文字列だけ取り出し
				triple[0] = array[12];
				triple[1] = "rdfs:subClassOf";
				triple[2] = array[6];
				//System.out.println(triple[0] +"|"+ triple[1] +"|"+ triple[2]);
                //URIと内部IDとの対応配列を作る
                List<Integer> triple_id = new ArrayList<Integer>(3);
                for (int i=0; i < 3; i++) {
                	if( !uri.contains(triple[i]) ) uri.add(triple[i]);
                	triple_id.add(uri.indexOf(triple[i]));
                }
                triples.add(triple_id);
			}
			br.close();
        } catch( IOException e )  {
            System.out.println( "csvに入出力エラーがありました" );
        } catch( NumberFormatException e )  {
            System.out.println( "csvにフォーマットエラーがありました" );
        }
	}
	
	public List<String> getURI() {
		return uri;
	}
	public List<List<Integer>> getTriples() {
		return triples;
	}
	
}

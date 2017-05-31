package syntacticParse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractProcessManager {
	public static final Charset UTF8 = StandardCharsets.UTF_8;

	public Process process;


	/** 外部プロセスを実行 **/
	public Process startProcess(List<String> command) {
		try {
			return new ProcessBuilder(command).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	};

	/** 実行中，入力待ちの外部プロセスに文字列を入力する **/
	public void writeInput2Process(String input) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream(), UTF8);
			osw.write(input);
			osw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 外部プロセスの標準出力を読み込む **/
	public List<String> readProcessResult() {
		List<String> result = null;
		try (InputStream is = process.getInputStream()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, UTF8));
			result = br.lines().collect(Collectors.toList());
			is.close();		br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/** 外部プロセスの出力をファイルに書き出す **/
	public Path writeOutput2File(Path path) {
		try (InputStream is = process.getInputStream()) {
			Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);	// ファイルが存在するなら上書き
			is.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}
}
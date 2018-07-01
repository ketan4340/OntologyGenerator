package modules.syntacticParse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractProcessManager {
	/* 外部プロセスを起動するコマンド */
	protected Process process;
	protected List<String> command;


	/** 外部プロセス開始 */
	protected void startProcess() {
		try {
			process = new ProcessBuilder(command).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/** 外部プロセス終了 */
	protected void finishProcess() {
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** 実行中，入力待ちの外部プロセスに文字列を入力する */
	protected void writeInput2Process(String input) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8);
			osw.write(input);
			osw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 外部プロセスの標準出力をList<String>として読み込む */
	protected List<String> readProcessResult() {
		try (InputStream is = process.getInputStream()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			List<String> result = br.lines().collect(Collectors.toList());
			is.close();		br.close();
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	/** 外部プロセスの標準出力をファイルに書き出す */
	protected Path writeOutput2File(Path path) {
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
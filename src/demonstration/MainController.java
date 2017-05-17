package demonstration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MainController/* implements ActionListener*/{
	/*** Model ***/
	private InputModel i_model;
	private OutputModel o_model;

	/*** View ***/
	private MainView view;

	public MainController() {
		i_model = new InputModel();
		o_model = new OutputModel();

		view = new MainView(this);
		view.setModels(i_model, o_model);
		/* RunGeneratorボタンの実装 */
		view.getRunGeneratorBt().addActionListener(event -> {
			String text = view.getTxArea1().getText();
			List<String[]> triples = i_model.runGenerator(text);
			o_model.addAllTriples(triples);
		});
		/* RandomTextボタンの実装 */
		view.getRandomTextBt().addActionListener(event -> {
			List<String> strs = new LinkedList<String>();

			Path path = Paths.get("writings/gooText生物-動物名-あ.txt");
			//List<String> strs = Files.readAllLines(path, StandardCharsets.UTF_8);	// ファイルが小さければこれでもいい
			try(Stream<String> stream = Files.lines(path, Charset.forName("UTF-8"))) {
				stream.forEach(line -> strs.add(line));
			}catch(IOException e){
			  System.out.println(e);
			}

			String text = strs.get(new Random().nextInt(strs.size()));
			view.getTxArea1().setText(text);
		});

		// ModelのオブザーバーにViewを追加
		i_model.addObserver(view);
		o_model.addObserver(view);
	}

	public InputModel getI_model() {
		return i_model;
	}
	public void setI_model(InputModel i_model) {
		this.i_model = i_model;
	}
	public OutputModel getO_model() {
		return o_model;
	}
	public void setO_model(OutputModel o_Model) {
		this.o_model = o_Model;
	}

/*
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == view.getRunGeneratorBt()) {
			String text = view.getTxArea1().getText();
			List<String[]> triples = new ArrayList<String[]>();
			triples = i_model.runGenerator(text);

			o_model.addAllTriples(triples);

		}else if(e.getSource() == view.getRandomTextBt()) {
			String text = "*";
			try{
				  File file = new File("writings/gooText生物-動物名-あ.txt");
				  BufferedReader br = new BufferedReader(new FileReader(file));

				  int sum = 0;
				  List<String> strs = new LinkedList<String>();
				  String str;
				  while((str = br.readLine()) != null){
				    sum++;
				    strs.add(str);
				  }

				  int ran = new Random().nextInt(sum);
				  text = strs.get(ran);

				  br.close();
				}catch(FileNotFoundException err){
				  System.out.println(err);
				}catch(IOException err){
				  System.out.println(err);
				}

			view.getTxArea1().setText(text);
		}
	}
	*/

	/*
	 * 各ActionListener(インスタンス変数)の実装
	 */
	private ActionListener Action = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO 自動生成されたメソッド・スタブ

		}
	};
}

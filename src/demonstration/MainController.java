package demonstration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainController implements ActionListener{
	private InputModel i_model;
	private OutputModel o_model;

	private MainView view;

	public MainController() {
		i_model = new InputModel();
		o_model = new OutputModel();

		view = new MainView(this);
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
	public MainView getView() {
		return view;
	}
	public void setView(MainView view) {
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == view.getRunGeneratorBt()) {
			String text = view.getTxArea1().getText();
			List<String[]> triples = new ArrayList<String[]>();
			triples = i_model.runGenerator(text);

			o_model.setTriples(triples);

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
}

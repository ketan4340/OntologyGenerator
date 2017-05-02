package demonstration;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GenerateController implements ActionListener{
	private GenerateModel model;
	private JFrame view;

	public GenerateController(GenerateModel model) {
		this.model = model;
		this.view = new GenerateView(this);
	}

	public GenerateModel getModel() {
		return model;
	}
	public void setModel(GenerateModel model) {
		this.model = model;
	}

	public void setView(JFrame view) {
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == ((GenerateView) view).bt) {
			String text = ((GenerateView) view).getTxArea1().getText();
			model.runGenerator(text);

		}else if(e.getSource() == ((GenerateView) view).reset) {
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
				  Random rnd = new Random();
				  int ran = rnd.nextInt(sum);

				  text = strs.get(ran);

				  br.close();
				}catch(FileNotFoundException err){
				  System.out.println(err);
				}catch(IOException err){
				  System.out.println(err);
				}

			((GenerateView) view).getTxArea1().setText(text);
		}
	}
}

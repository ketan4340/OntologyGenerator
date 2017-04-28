package demonstration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class GenerateView extends JFrame implements Observer{
	JButton bt;
	JPanel pn1, pn2;
	JTextArea txArea1, txArea2;

	public GenerateView(String title) {
		setTitle(title);
	}

	public GenerateView(GenerateController controller) {
		setSize(1200,500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		pn1 = new JPanel();
		pn1.setSize(500, 500);
	    pn1.setBackground(Color.GRAY);
	    pn2 = new JPanel();
	    pn2.setSize(500, 500);
	    pn2.setBackground(Color.GRAY);

	    bt = new JButton("オントロジー構築");
	    bt.addActionListener(controller);

	    txArea1 = new JTextArea(25, 35);
	    txArea1.setLineWrap(true);

	    txArea2 = new JTextArea(25, 35);
	    txArea2.setLineWrap(true);

	    pn1.add(txArea1);
	    pn2.add(txArea2);

	    add(pn1, BorderLayout.WEST);
	    add(bt, BorderLayout.CENTER);
	    add(pn2, BorderLayout.EAST);

	    this.setVisible(true);
	}

	@Override
	public void update(Observable obs, Object arg) {
		GenerateModel model = (GenerateModel) obs;
		txArea1.setText("");
		for(final String writing: model.getWritingList()) {
			txArea1.append(writing+"\n");
		}

		txArea2.setText("");
		for(List<String> relation: model.getRelations()) {
			for(String concept: relation) {
				txArea2.append(concept + ",");
			}
			txArea2.append("\n");
		}
	}
}

package demonstration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

public class GenerateView extends JFrame implements Observer{
	//private GenerateController controller;
	public JButton bt, reset;
	public JPanel pn1, pn2;
	private JTextArea txArea1, txArea2;

	public GenerateView(GenerateController controller) {
		super("OntologyGenerator");
		//this.controller = controller;
		controller.setView(this);
		controller.getModel().addObserver(this);

		setSize(1200,800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		pn1 = new JPanel();
		pn1.setSize(1200, 300);
	    pn1.setBackground(Color.WHITE);
	    txArea1 = new JTextArea(10, 100);
	    txArea1.setLineWrap(true);
	    pn1.add(txArea1);

	    pn2 = new JPanel();
	    pn2.setLayout(new GridLayout(0, 3));
	    pn2.setSize(1200, 300);
	    pn2.setBackground(Color.WHITE);
	    JScrollPane scrollpane = new JScrollPane(pn2);
	    scrollpane.setPreferredSize(new Dimension(1200, 500));
	    /*
	    txArea2 = new JTextArea(25, 35);
	    txArea2.setLineWrap(true);
	    pn2.add(txArea2);
	    */
	    LineBorder border = new LineBorder(Color.BLACK, 2, true);
	    JLabel s = new JLabel("Subject");
	    s.setForeground(Color.CYAN);
	    s.setBorder(border);
	    s.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	    JLabel p = new JLabel("Predicate");
	    p.setForeground(Color.RED);
	    p.setBorder(border);
	    p.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	    JLabel o = new JLabel("Object");
	    o.setForeground(Color.CYAN);
	    o.setBorder(border);
	    o.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	    pn2.add(s);
	    pn2.add(p);
	    pn2.add(o);

	    bt = new JButton("オントロジー構築");
	    bt.addActionListener(controller);
	    reset = new JButton("ランダムテキスト");
	    reset.addActionListener(controller);

	    this.add(pn1, BorderLayout.NORTH);
	    this.add(bt, BorderLayout.WEST);
	    this.add(reset, BorderLayout.EAST);
	    this.add(scrollpane, BorderLayout.SOUTH);

	    this.setVisible(true);
	}

	public JTextArea getTxArea1() {
		return txArea1;
	}

	@Override
	public void update(Observable obs, Object arg) {
		GenerateModel model = (GenerateModel) obs;
		System.out.println("update"+model.getWritingList());

		txArea1.setText("");
		for(final String writing: model.getWritingList()) {
			txArea1.append(writing+"\n");
		}

		for(List<String> relation: model.getRelations()) {
			for(String concept: relation) {
			    JLabel c = new JLabel(concept);
			    c.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
			    pn2.add(c);
			}
		}
	}
}

package 지방_3과제;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Purchase extends BaseFrame {
	DefaultTableModel m = model("이름,등급,좌석,가격,생일".split(","));
	JTable t = table(m);
	JScrollPane jsp;
	JTextField jt;
	String str[] = "보유마일리지 : ,적립마일리지 : ,사용마일리지 : ".split(",");
	JPanel sen;
	int total;
	int mile;
	JButton btn;
	JLabel jl;

	public Purchase() {
		super("결제", 900, 600);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(lbl("결제하기", JLabel.CENTER, 25, f2));
		n.add(ns = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		ns.add(lbl("금액 : " + df.format(price) + "원", JLabel.LEFT, 15));

		c.add(jsp = new JScrollPane(t));

		s.add(sc = new JPanel(new GridLayout(0, 1)));
		s.add(se = new JPanel(new BorderLayout()), "East");

		addRow(); // 이거 내려버리면 총금액이랑 적립마일리지 0으로 되버림

		mile = (int) (total * 0.01);
		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(lbl(str[i], JLabel.LEFT, 15));
			if (i == 0) {
				tmp.add(lbl(df.format(user.get(6)) + "원", JLabel.LEFT, 15));
			} else if (i == 1) {
				tmp.add(lbl(df.format(mile) + "원", JLabel.LEFT, 15));
			} else {
				tmp.add(jt = new JTextField(15));
				tmp.add(btn = btn("사용", a -> {
					if (a.getActionCommand().equals("사용")) {
						if (jt.getText().isEmpty() || !isNumeric(jt.getText())
								|| toInt(jt.getText()) > toInt(user.get(6)) || toInt(jt.getText()) > total) {
							emsg("마일리지를 확인하세요.");
							return;
						}
						btn.setText("초기화");
						total -= toInt(jt.getText());
						jl.setText(total + "");
						jt.setEnabled(false);
						System.out.println(total);
					} else {
						jt.setEnabled(true);
						btn.setText("사용");
						total += toInt(jt.getText());
						jl.setText(total + "");
						System.out.println(total);
					}
				}));
			}
			sc.add(tmp);
		}

		se.add(sen = new JPanel(new FlowLayout(2)), "North");
		sen.add(jl);
		sen.add(lbl("원", JLabel.LEFT, 15));
		sen.add(btn("결제하기", a -> {
			int yesno = JOptionPane.showConfirmDialog(null, "총 : " + total + "원을 결제하시겠습니까?", "결제",
					JOptionPane.YES_NO_OPTION);
			if (yesno == JOptionPane.YES_OPTION) {
				for (int i = 0; i < t.getRowCount(); i++) {
					ReceiptList.add(t.getValueAt(i, 0) + " - " + t.getValueAt(i, 1));
				}
				exectue("insert reservation values(0,?,?,?,?)", u_no, s_no, mile,
						(jt.getText().equals("") ? "0" : jt.getText()));
				var last = getRows("select * from reservation order by r_no desc").get(0).get(0);
				for(int i = 0; i < t.getRowCount(); i++) {
					var age = t.getValueAt(i, 1).toString().equals("성인") ? 1 : t.getValueAt(i, 1).equals("유아") ? 2 : 3;
					exectue("insert boarding values(0, ?, ?, ?, ?, ?)", last, age, t.getValueAt(i, 0), t.getValueAt(i, 4), t.getValueAt(i, 2));
				}
				setVisible(false);
				new Receipt();
			}
		}));
	}

	void addRow() {
		m.setRowCount(0);
		total = 0;
		jl = new JLabel();

		for (int i = 0; i < peopleNameList.size(); i++) {
			Object[] row = new Object[5];
			row[0] = peopleNameList.get(i);
			row[1] = peopleRating.get(i);
			row[2] = peopleResultList.get(i);
			row[3] = df.format(peoplePriceList.get(i));
			row[4] = peopleBirthList.get(i);
			total += toInt(peoplePriceList.get(i));
			jl.setText(df.format(total));
			m.addRow(row);
		}
		jl.repaint();
		jl.revalidate();
	}
}

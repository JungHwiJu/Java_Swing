package 지방_3과제;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.Period;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyledEditorKit.ForegroundAction;

public class Info extends BaseFrame {
	DefaultTableModel m = model("이름(영문),생년월일,등급".split(","));
	JTable t = table(m);
	JScrollPane jsp;
	JTextField jt1, jt2, jt3;
	String str[] = "이름(영문),생년월일".split(",");
	JButton btn[] = { new JButton("삭제하기"), new JButton("완료") };
	boolean flag = false;
	
	public Info() {
		super("탑승객정보입력", 1000, 350);

		add(n = new JPanel(new BorderLayout()), "North");
		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(nc = new JPanel(new FlowLayout(2)));
		nw.add(lbl(path, JLabel.LEFT, 25, f1));
		nc.add(sz(btn("입력", a -> {
			if (jt1.getText().isEmpty() || jt2.getText().isEmpty() || jt3.getText().isEmpty()) {
				emsg("이름 또는 생년월일을 입력해주세요.");
				return;
			}
			if (jt1.getText().matches(".*[^a-zA-Z].*") || jt2.getText().matches(".*[^a-zA-Z].*")) {
				emsg("영문 이름은 영문만 가능합니다.");
				jt1.setText("");
				jt2.setText("");
				jt1.requestFocus();
				return;
			}
			try {
				if (LocalDate.parse(jt3.getText()).isAfter(LocalDate.now())) {
					emsg("생년월일을 확인해주세요.");
					jt3.setText("");
					jt3.requestFocus();
					return;
				}
			} catch (Exception e1) {
				emsg("생년월일을 확인해주세요.");
				jt3.setText("");
				jt3.requestFocus();
				return;
			}
			
			var age = Period.between(LocalDate.parse(jt3.getText()), LocalDate.now()).getYears();
			
			String input[] = new String[3];
			input[0] = jt1.getText() + " " + jt2.getText();
			input[1] = jt3.getText();
			if (age > 5) {
				input[2] = "성인";
			} else if (age >= 1 && age <= 5) {
				input[2] = "소아";
			} else if (age < 1) {
				input[2] = "유아";
			}
			m.addRow(input);

			jt1.setText("");
			jt2.setText("");
			jt3.setText("");
		}), 60, 25));
		add(w = new JPanel(new BorderLayout()), "West");
		w.add(wn = new JPanel(new GridLayout(0, 1)), "North");
		add(c = new JPanel(new BorderLayout()));
		c.add(cc = new JPanel(new BorderLayout()));
		c.add(cs = new JPanel(new FlowLayout(2)), "South");

		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(lbl(str[i], JLabel.LEFT, 15), 80, 25));
			if (i == 0) {
				tmp.add(sz(jt1 = new JTextField(6), 0, 25));
				tmp.add(sz(jt2 = new JTextField(6), 0, 25));
			} else {
				tmp.add(sz(jt3 = new JTextField(12), 0, 25));
			}
			wn.add(tmp);
		}

		cc.add(jsp = new JScrollPane(t));
		for (var btn : btn) {
			cs.add(btn);
			btn.addActionListener(a -> {
				if (a.getActionCommand().equals("삭제하기")) {
					if (t.getSelectedRow() == -1) {
						emsg("삭제할 레코드가 없거나 선택되지 않았습니다.");
						return;
					}
					m.removeRow(t.getSelectedRow());
				} else {
					if (t.getRowCount() <= 0) {
						emsg("탑승객을 1명 이상 입력하세요.");
						return;
					}
		
					for (int i = 0; i < t.getRowCount(); i++) {
						if(t.getValueAt(i, 2).equals("성인")) flag = true;
						price = toInt(getRows("select price from schedule where s_no = ?", s_no).get(0).get(0));
						peopleNameList.add(t.getValueAt(i, 0) + "");
						peoplePriceList.add(t.getValueAt(i, 2).equals("성인") ? price
								: (t.getValueAt(i, 2).equals("소아") ? (int) (price * 0.8) : (int) (price * 0.4)));
						peopleRating.add(t.getValueAt(i, 2) + "");
						peopleBirthList.add(t.getValueAt(i, 1) + "");
					}
					
					if(!flag) {
						emsg("성인x");
						return;
					}
					new Seat_2().addWindowListener(new Before(Info.this));
				}
			});

		}
		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 5, 10, 5));
	}

	public static void main(String[] args) {
		s_no = 13415;
		path = "dfdsf";
		new Info();
	}
}

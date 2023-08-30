package 지방_3과제;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Aviation extends BaseFrame {
	String str[] = "출발지,도착지,출발날짜".split(",");
	JComboBox<String> departCom, arriveCom;
	JLabel calIcon;
	JTextField jt;
	JPanel main;

	public Aviation() {
		super("항공권 조회", 280, 250);

//		add(main = new JPanel(new GridBagLayout()));
		add(main = new JPanel());
		main.add(c = new JPanel(new BorderLayout()));
		c.add(cc = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(1)), "South");

		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(lbl(str[i], JLabel.LEFT, 12), 50, 30));
			if (i == 0) {
				tmp.add(departCom = sz(new JComboBox<>(), 130, 30));
			} else if (i == 1) {
				tmp.add(arriveCom = sz(new JComboBox<>(), 130, 30));
			} else {
				tmp.add(jt = sz(new JTextField(), 130, 30));
				tmp.add(calIcon = new JLabel(getIcon("datafiles/달력.png", 30, 30)));
			}
			cc.add(tmp);
		}

		for (var r : getRows("select n_name from nation"))
			departCom.addItem(r.get(0).toString());

		s.add(sz(btn("확인", a -> {
			if (jt.getText().isEmpty() || departCom.getSelectedIndex() == -1 || arriveCom.getSelectedIndex() == -1) {
				emsg("빈칸이 있습니다.");
				return;
			}
			depart = departCom.getSelectedItem().toString();
			arrive = arriveCom.getSelectedItem().toString();
			new AirportSelect(Aviation.this).addWindowListener(new Before(Aviation.this));
		}), 100, 30));
		event();
		jt.setEnabled(false);
		departCom.setSelectedIndex(-1);
		arriveCom.setSelectedIndex(-1);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	void event() {
		departCom.addActionListener(a -> {
			arriveCom.removeAllItems();

			for (var r : getRows("select n_name from nation where n_name <> ?", departCom.getSelectedItem())) {
				arriveCom.addItem(r.get(0).toString());
			}
			arriveCom.setSelectedIndex(-1);
		});

		calIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(departCom.getSelectedIndex() == -1 || arriveCom.getSelectedIndex() == -1) {
					emsg("출발지와 도착지를 선택하세요.");
					return;
				}
				new Calender(jt, departCom.getSelectedIndex() + 1, arriveCom.getSelectedIndex() + 2)
						.addWindowListener(new Before(Aviation.this));
			}
		});
	}

	public static void main(String[] args) {
		new Aviation();
	}
}

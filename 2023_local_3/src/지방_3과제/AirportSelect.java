package 지방_3과제;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.TextStyle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

public class AirportSelect extends BaseFrame {
	DefaultTableModel m = model("번호,출발지,도착지,출발시간,도착시간,가격,잔여좌석".split(","));
	JTable t = table(m);
	JScrollPane jsp;
	JLabel datelbl;
	Aviation av;

	public AirportSelect(Aviation av) {
		super("항공권 선택", 650, 500);
		this.av = av;
		date = LocalDate.parse(this.av.jt.getText());

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(2)), "South");

		n.add(datelbl = lbl(String.format("%02d. %02d", date.getMonthValue(), date.getDayOfMonth()) + "("
				+ (date.getDayOfWeek().getDisplayName(TextStyle.SHORT, getLocale())) + ")", JLabel.LEFT, 20));
		c.add(jsp = new JScrollPane(t));

		s.add(btn("확인", a -> {
			if (t.getSelectedRow() == -1) {
				emsg("항공권을 선택해주세요.");
				return;
			}
			s_no = toInt(t.getValueAt(t.getSelectedRow(), 0));
			path = String.format(date.toString().substring(2, 4) + ".%02d.%02d ", date.getMonthValue(),
					date.getDayOfMonth()) + "(" + (date.getDayOfWeek().getDisplayName(TextStyle.SHORT, getLocale()))
					+ ")" + t.getValueAt(t.getSelectedRow(), 1) + " → " + t.getValueAt(t.getSelectedRow(), 2);
			System.out.println(path);
			new Info().addWindowListener(new Before(AirportSelect.this));
		}));

		addRow();
		n.setBorder(new CompoundBorder(new MatteBorder(0, 0, 2, 0, Color.black), new EmptyBorder(5, 5, 5, 5)));
		c.setBorder(new EmptyBorder(15, 5, 5, 5));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	void addRow() {
		m.setRowCount(0);

		var rs = getRows(
				"select distinct s.s_no, n1.n_name, n2.n_name, left(departTime,5), left(addtime(s.departTime, s.timeTaken),5), price from reservation r,schedule s, nation n1, nation n2 where depart = n1.n_no and arrival = n2.n_no and r.s_no = s.s_no and n1.n_name = ? and n2.n_name = ? and s.date = ?",
				av.departCom.getSelectedItem(), av.arriveCom.getSelectedItem(), av.jt.getText());
		Object obj[] = new Object[7];
		for (var r : rs) {
			var result = getRows(
					"select concat(50-(Select count(*) from boarding where r_no in (Select r_no from reservation where s_no= ?)), ' / 50')",
					r.get(0)).get(0);

			r.set(5, df.format(toInt(r.get(5))));
			r.add(result.get(0).toString());
			m.addRow(r.toArray());
		}
	}
}

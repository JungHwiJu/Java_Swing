import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Line.Info;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Cal extends BaseFrame {
	JLabel prev, date, next;
	JLabel days[] = new JLabel[42];
	JLabel datelbl;
	int year, month;
	LocalDate r_date, returnDay;
	String str[] = "일,월,화,수,목,금,토".split(",");

	public Cal() {
		super("대출연장", 400, 450);

		add(n = new JPanel(), "North");
		add(c = new JPanel(new GridLayout(1, 0)));
		add(s = new JPanel(new GridLayout(0, 7, 5, 5)), "South");

		n.add(prev = lbl("◀", 0, 15, a -> {
			r_date = r_date.minusMonths(1);
			setCal();
		}));
		n.add(date = lbl("", 0, 15));
		n.add(next = lbl("▶", 0, 15, a -> {
			r_date = r_date.plusMonths(1);
			setCal();
		}));

		for (int i = 0; i < str.length; i++) {
			var lbl = new JLabel(str[i], 0);
			lbl.setForeground(i % 7 == 0 ? Color.red : i % 7 == 6 ? Color.blue : Color.black);
			c.add(lbl);
		}
		for (int i = 0; i < days.length; i++) {
			s.add(days[i] = sz(new JLabel(i + "", 0), 50, 50));
			var rs = getRows("select * from rental where r_no = ?", r_no);
			for (var r : rs) {
				r_date = LocalDate.parse(r.get(3).toString());
				returnDay = r_date.plusDays(14);
				System.out.println(r_date + ", " + returnDay);
			}
			days[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = ((JLabel) e.getSource());

					if (me.getForeground().equals(Color.magenta)) {
						var rcount = LocalDate.of(year, month, toInt(me.getText())).toEpochDay()
								- returnDay.toEpochDay();
						System.out.println(rcount);
						var yn = JOptionPane.showConfirmDialog(null, "연장하시겠습니까?", "정보", JOptionPane.YES_NO_OPTION);
						if (yn == JOptionPane.YES_OPTION) {
							execute("update rental set r_returnday = '0000-00-00', r_count = ? where r_no = ?", rcount,
									r_no);
							iMsg("연장이 완료되었습니다.");
							dispose();
						}
					}
				}
			});
			days[i].setBorder(new LineBorder(Color.lightGray));
		}

		setCal();
		n.setOpaque(false);
		c.setOpaque(false);
		s.setOpaque(false);
		setVisible(true);
	}

	void setCal() {
		year = r_date.getYear();
		month = r_date.getMonthValue();
		date.setText(year + "년" + month + "월");
		for (var day : days) {
			day.setBorder(new LineBorder(Color.LIGHT_GRAY));
			day.setForeground(Color.black);
		}
		var ld = LocalDate.of(year, month, 1);
		var sday = ld.getDayOfWeek().getValue() % 7;
		for (int i = 0; i < 42; i++) {
			var tmp = ld.plusDays(i - sday);
			days[i].setVisible(tmp.getMonthValue() == month);
			days[i].setName(tmp.toString());
			days[i].setText(tmp.getDayOfMonth() + "");
		}
		for (var d = returnDay.plusDays(1); d.toEpochDay() < returnDay.plusDays(15).toEpochDay(); d = d.plusDays(1)) {
			for (var day : days) {
				if (day.getName().equals(d.toString())) {
					day.setBorder(new LineBorder(Color.black));
					day.setForeground(Color.magenta);
				}
			}
		}
	}

	public static void main(String[] args) {
		u_no = "1";
		r_no = 41;
		new Cal();
	}
}
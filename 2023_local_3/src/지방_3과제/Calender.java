package 지방_3과제;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Calender extends BaseFrame {
	JLabel prev, date, next;
	JLabel days[] = new JLabel[42];
	JTextField dateTxt;
	JLabel dateLbl;

	String st = "<html>", en = "<br>";
	
	int year, month;
	int depart, arrival;
	LocalDate today = LocalDate.now();
	String str[] = "일,월,화,수,목,금,토".split(",");
//	AirportSelect airport;

	public Calender() {
		super("날짜선택", 500, 500);

		add(n = new JPanel(), "North");
		add(c = new JPanel(new GridLayout(1, 0)));
		add(s = sz(new JPanel(new GridLayout(0, 7)), 0, 400), "South");

		n.add(prev = lbl("◀", 0, 15));
		n.add(date = lbl("", 0, 15));
		n.add(next = lbl("▶", 0, 15));

		for (int i = 0; i < str.length; i++) {
			JLabel lbl = new JLabel(str[i], 0);
			lbl.setForeground(i % 7 == 6 ? Color.blue : (i % 7 == 0 ? Color.red : Color.black));
			c.add(lbl);
		}

		for (int i = 0; i < days.length; i++) {
			s.add(days[i] = sz(new JLabel(i + "", 0), 50, 50));

			days[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = ((JLabel) e.getSource());
					if (me.isEnabled()) {
						var box = me.getText();
						System.out.println(box);
						if(box.contains("html")) box = box.substring(box.indexOf(st) + st.length(), box.indexOf(en));
						
						var day = toInt(box);
						if (today.getYear() == LocalDate.now().getYear()
								&& today.getMonthValue() == LocalDate.now().getMonthValue()
								&& day < LocalDate.now().getDayOfMonth()) {
							emsg("이전 날짜는 선택이 불가능합니다.");
							return;
						}

						String selectedDate = year + "-" + String.format("%02d", month) + "-"
								+ String.format("%02d", day);
						var rs = getRows(
								"SELECT * FROM 2023지방_3.schedule s where 1=1 and s.depart=? and arrival=? and date=?",
								depart, arrival, selectedDate);
						if (rs.isEmpty()) {
							emsg("해당날짜에는 항공스케줄이 없습니다.");
							return;
						}

						try {
							if (me.getName().equals("true")) {
								if (dateTxt != null) {
									dateTxt.setText(today.getYear() + "-" + String.format("%02d", today.getMonthValue())
											+ "-" + String.format("%02d", day));
								} else {
									dateLbl.setText(String.format("%02d. %02d", today.getMonthValue(), day) + "("
											+ (today.getDayOfWeek().getDisplayName(TextStyle.SHORT, getLocale()))
											+ ")");
									// airport.date = LocalDate.of(today.getYear(), today.getMonthValue(), day);
									// airport.addRow();
								}
								dispose();
							}
						} catch (Exception e1) {
						}

						for (var comp : s.getComponents()) {
							((JLabel) comp).setBorder(new LineBorder(Color.LIGHT_GRAY));
							((JLabel) comp).setName("false");
						}

						me.setBorder(new LineBorder(Color.blue));
						me.setName("true");

					}
				}
			});
			days[i].setBorder(new LineBorder(Color.lightGray));
		}

		setCal();

		prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (month == LocalDate.now().getMonthValue()) {
					return;
				}

				today = today.minusMonths(1);
				setCal();
			}
		});

		next.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (month == 12) {
					return;
				}

				today = today.plusMonths(1);
				setCal();
			}
		});

		n.setOpaque(false);
		c.setOpaque(false);
		s.setOpaque(false);
		setVisible(true);
	}

	public Calender(JTextField dateTxt, int depart, int arrival) {
		this();
		this.dateTxt = dateTxt;
		this.depart = depart;
		this.arrival = arrival;

		setCal();
	}

//	public DatePicker(AirportSelect airport) {
//		this();
//		this.airport = airport;
//		this.dateLbl = airport.dateLbl;
//	}

	void setCal() {
		year = today.getYear();
		month = today.getMonthValue();
		date.setText(year + "년 " + month + "월");

		var ld = LocalDate.of(year, month, 1);
		var sday = ld.getDayOfWeek().getValue() % 7;

		if (year == LocalDate.now().getYear() && month == LocalDate.now().getMonthValue()) {
			prev.setEnabled(false);
		} else {
			prev.setEnabled(true);
		}

		if (year == LocalDate.now().getYear() && month == 12) {
			next.setEnabled(false);
		} else {
			next.setEnabled(true);
		}

		for (int i = 0; i < days.length; i++) {
			var tmp = ld.plusDays(i - sday);
			
			String s = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", tmp.getDayOfMonth());
			days[i].setEnabled(tmp.getMonthValue() == month);
			if (year == LocalDate.now().getYear() && month == LocalDate.now().getMonthValue()
					&& tmp.getDayOfMonth() <= LocalDate.now().getDayOfMonth()) {
				days[i].setEnabled(false);
			}
			days[i].setText(tmp.getDayOfMonth() + "");
			var rs = getRows("select date from schedule where depart=? and arrival=? and month(date)=?", depart,
					arrival, month);
			if (!rs.isEmpty()) {
				for (var r : rs) {
					if (r.get(0).toString().equals(s)) {
						days[i].setText("<html>" + days[i].getText() + "<br>(" + r.size() + ")");
					}
				}
			}
		}

	}

	public static void main(String[] args) {
		new Calender(null, 1, 2);
	}
}

package 지방_3과제;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.management.monitor.MonitorSettingException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Seat extends BaseFrame {
	String str[] = "A,B,C,D,E".split(",");
	JScrollPane jsp;
	ArrayList<Item> items = new ArrayList<>();
	HashMap<String, JLabel> seatMap = new HashMap<>();
	int idx;
	JLabel totallbl;
	ArrayList<String> aa;
	ArrayList<String> b;

	public Seat() {
		super("좌석선택", 1030, 600);

		add(jsp = new JScrollPane(c = new JPanel(new BorderLayout(25, 25))));
		add(e = sz(new JPanel(new BorderLayout()), 250, 0), "East");

		c.add(cw = sz(new JPanel(new FlowLayout(2, 5, 5)), 230, 875), "West");
		c.add(cc = sz(new JPanel(new FlowLayout(1, 5, 5)), 150, 875));
		c.add(ce = sz(new JPanel(new FlowLayout(0, 5, 5)), 300, 875), "East");

		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 7; j++) {
				JPanel tmp = (j < 3 ? cw : (j < 4 ? cc : ce));
				JLabel lbl = null;

				if (j == 0 || j == 6)
					lbl = lbl(i == 0 ? "" : i + "", 0, Font.BOLD, 18);
				else if (i == 0)
					lbl = lbl(str[j - 1], JLabel.CENTER, 13);
				else {
					lbl = lbl(str[j - 1] + String.format("%02d", i), JLabel.CENTER, 13);

					lbl.setOpaque(true);
					lbl.setBorder(new LineBorder(Color.black));
					lbl.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							var me = (JLabel) e.getSource();

							if (me.getBackground().equals(Color.LIGHT_GRAY)) {
								emsg(me.getText().toUpperCase() + "좌석은 선택이 불가능합니다.");
								return;
							}

							var item = items.get(idx);
							var value = me.getText().substring(me.getText().length() - 3);
							
//							if(me.getText().matches(".*[0-9].*")) return;
							
							
							
							if (me.getBackground().equals(Color.red)) {
								me.setBackground(Color.white);
								items.get(idx).setText(item.name + " - ");
								items.get(idx).setName("");
							} else {
								if (!item.getName().isEmpty()) {

								}
								me.setBackground(Color.red);
								items.get(idx).setName(me.getText());
								items.get(idx).setText(item.name + " - " + me.getText());
								items.get(idx).select();
							}
						}
					});
					seatMap.put(str[j - 1] + String.format("%02d", i), lbl);
				}
				tmp.add(sz(lbl, j == 0 || j == 7 ? 35 : 80, i == 0 ? 20 : 80));
			}
		}

		for (var rs : getRows(
				"select seat from boarding b, reservation r, schedule s where b.r_no = r.r_no and r.s_no = s.s_no and date = ? and s.s_no = ?",
				date, s_no)) {
			seatMap.get(rs.get(0).toString()).setBackground(Color.lightGray);
		}

		e.add(totallbl = lbl("총 " + peopleNameList.size() + "명", 2, 1, 15), "North");
		e.add(ec = new JPanel());
		ec.setLayout(new BoxLayout(ec, BoxLayout.PAGE_AXIS));

		int i = 0;
		for (var peo : peopleNameList) {
			var item = new Item(peo, i++);

			ec.add(item);
			ec.add(Box.createVerticalStrut(20));
			items.add(item);
		}
		e.add(btn("선택완료", a -> {
			System.out.println(peopleResultList);
			if (peoplePriceList.isEmpty()) {
				dispose();
			} else {
				if (items.stream().filter(x -> !x.getName().isEmpty()).count() < peopleNameList.size()) {
					emsg("좌석을 모두 선택해주세요.");
					return;
				}

				for (int k = 0; k < items.size(); k++) {
					peopleResultList.add(items.get(k).getName());
				}
				new Purchase().addWindowListener(new Before(Seat.this));
			}
		}), "South");

		if (peoplePriceList.isEmpty()) {
			System.out.println("asdfmsadklfdsajfkdlsfds");
			peopleNameList.clear();
		} else {
			items.get(0).select();
		}
		c.setBackground(Color.white);
		e.setBorder(new EmptyBorder(10, 10, 10, 10));
		c.setOpaque(true);
		ce.setOpaque(true);
		cc.setOpaque(true);
		cw.setOpaque(true);
		setVisible(true);

	}

	public Seat(int s_no, int r_no, ArrayList<String> aa, ArrayList<String> b) {
		this();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				peopleNameList.clear();
				peopleResultList.clear();
				peoplePriceList.clear();
			}
		});
		
		this.aa = aa;
		this.b = b;
		setTitle("좌석변경");
		peopleNameList.clear();
		peopleResultList.clear();
		peoplePriceList.clear();
		totallbl.setText("총 " + aa.size() + "명");
		int i = 0;
		for (var peo : aa) {
			peopleNameList.add(peo);
			var item = new Item(peo, i);
			ec.add(item);
			ec.add(Box.createVerticalStrut(20));
			items.add(item);
			items.get(i).setText(aa.get(i) + " - " + b.get(i));
			i++;
		}

		for (var result : b) {
			items.get(b.indexOf(result)).setName(result);
			peopleResultList.add(result);
		}

		for (var rs : getRows("Select seat from boarding where r_no in (Select r_no from reservation where s_no= ? )",
				s_no)) {
			for (int j = 0; j < aa.size(); j++) {
				if (rs.get(0)
						.equals(getRows("select seat from boarding where r_no = ?", r_no).get(j).get(0).toString())) {
					seatMap.get(rs.get(0).toString()).setBackground(Color.red);
				} else {
					if (seatMap.get(rs.get(0).toString()).getBackground().equals(Color.red)) {
						seatMap.get(rs.get(0).toString()).setBackground(Color.red);
					} else
						seatMap.get(rs.get(0).toString()).setBackground(Color.LIGHT_GRAY);
				}
			}
		}

		items.get(0).select();
		c.setOpaque(true);
		ce.setOpaque(true);
		cc.setOpaque(true);
		cw.setOpaque(true);
	}

	class Item extends JLabel {
		String name;
		int i;

		public Item(String name, int i) {
			super(name);
			this.name = name;
			this.i = i;
			setName("");
			setMaximumSize(new Dimension(250, 35));
			setFont(new Font(f1, 0, 13));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					select();
				}
			});
			setBorder(new CompoundBorder(new LineBorder(Color.lightGray), new EmptyBorder(10, 10, 10, 10)));
			setOpaque(true);
		}

		void select() {
			idx = i;
			
			System.out.println("idx :" + idx);
			for (var it : items) {
				it.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(10, 10, 10, 10)));
			}
			setBorder(new CompoundBorder(new LineBorder(Color.blue), new EmptyBorder(10, 10, 10, 10)));
		}
	}

}

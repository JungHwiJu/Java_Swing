package 지방_3과제;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class My extends BaseFrame {
	DefaultTableModel m = model("번호,출발지,도착지,날짜,출발시간,소요시간,사용마일리지,적립마일리지,s_no".split(","));
	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			var comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (LocalDate.parse(table.getModel().getValueAt(row, 3).toString()).isEqual(LocalDate.now())) {
				comp.setForeground(Color.RED);
			} else {
				comp.setForeground(Color.BLACK);
			}
			return comp;
		}
	};
	JTable t = table(m);
	JScrollPane jsp;
	int total;
	JLabel jl = new JLabel();
	ArrayList<String> nameList = new ArrayList<>();
	ArrayList<String> seatList = new ArrayList<>();
	ArrayList<String> b_noList = new ArrayList<>();
	
	public My() {
		super("마이페이지", 800, 500);
		addRow();

		for (int i = 0; i < m.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		dtcr.setHorizontalAlignment(0);

		add(c = new JPanel(new BorderLayout()));
		c.add(jsp = new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(2)), "South");
		
		s.add(lbl("마일리지 : ", JLabel.LEFT, 15));
		s.add(jl);
		s.add(lbl("원", JLabel.LEFT, 15));
		jl.setFont(new Font("",1, 15));
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					LocalDate date = LocalDate.parse(t.getValueAt(t.getSelectedRow(), 3).toString());
					LocalTime depart = LocalTime.parse(t.getValueAt(t.getSelectedRow(), 4).toString());
					LocalTime arrive = LocalTime.parse(t.getValueAt(t.getSelectedRow(), 5).toString());
					var pop = new JPopupMenu();
					var it = new JMenuItem();
					if (date.isBefore(LocalDate.now())
							&& (depart.isAfter(LocalTime.now()) || arrive.isBefore(LocalTime.now()))) {
						it.setText("삭제하기");
					} else {
						for (var cap : "예약취소,좌석 변경".split(",")) {
							it = new JMenuItem(cap);
							pop.add(it);
							it.addActionListener(a -> {
								if (a.getActionCommand().equals("예약취소")) {
									System.out.println(total);
									exectue("delete from reservation where r_no = ?", t.getValueAt(t.getSelectedRow(), 0));
									total += toInt(t.getValueAt(t.getSelectedRow(), 7).toString().replace(",", ""));
									jl.setText(df.format(total));
									System.out.print(total);
								} else {
									peopleNameList.clear();
									peopleResultList.clear();
									b_noList.clear();
									my_date = t.getValueAt(t.getSelectedRow(), 3).toString();
									int r_no = toInt(t.getValueAt(t.getSelectedRow(), 0));
									System.out.println("r_no : " + r_no);
									
									var rs = getRows("select * from boarding where r_no = ?", r_no);
									for (int i = 0; i < rs.size(); i++) {
										System.out.println("k");
										peopleNameList.add(getRows("select * from boarding where r_no = ?", r_no).get(i)
												.get(3).toString());
										peopleResultList.add(getRows("select * from boarding where r_no = ?", r_no).get(i)
												.get(5).toString());
										b_noList.add(rs.get(i).get(0).toString());
									}
									System.out.println(b_noList);
									new Seat_2(toInt(t.getValueAt(t.getSelectedRow(), 8)),
											toInt(t.getValueAt(t.getSelectedRow(), 0)))
													.addWindowListener(new Before(My.this));
								}
							});
						}
					}
					pop.add(it);
					it.addActionListener(a -> {
						if (a.getActionCommand().equals("삭제하기")) {
							exectue("delete from reservation r where r.r_no = ? and r.u_no = ?",
									t.getValueAt(t.getSelectedRow(), 0), u_no);
							addRow();
						}
					});
					pop.show(t, e.getX(), e.getY());
				}
			}

		});

		t.getColumn("s_no").setMinWidth(0);
		t.getColumn("s_no").setMaxWidth(0);
		
		((JPanel)getContentPane()).setBorder(new EmptyBorder(10,5,15,5));
	}

	void addRow() {
		m.setRowCount(0);
		total = 0;
		
		var rs = getRows(
				"select r.r_no, n1.n_name, n2.n_name, s.date, s.departTime, s.timeTaken, r.expense, r.income, r.s_no from reservation r, nation n1, nation n2, schedule s, user u where depart = n1.n_no and arrival = n2.n_no and r.u_no = u.u_no and r.s_no = s.s_no and u.u_no = ?",
				u_no);
		for (var r : rs) {
			r.set(6, df.format(toInt(r.get(6))));
			r.set(7, df.format(toInt(r.get(7))));
			m.addRow(r.toArray());
			total += toInt(user.get(6));
		}
		jl.setText(df.format(total));
	}

	public static void main(String[] args) {
		u_no = "1";
		user = new ArrayList<>();
		user.add(1);
		user.add("user1");
		user.add("user1!");
		user.add("박지안");
		user.add("PARK JIAN");
		user.add("2000-01-01");
		user.add("64188");
		new My();
	}
}
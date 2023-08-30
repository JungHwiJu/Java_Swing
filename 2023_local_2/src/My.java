import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class My extends BaseFrame {
	Object[] tClass = { Boolean.class, Icon.class, Object.class, Object.class, Object.class, Object.class, Object.class,
			Object.class, Object.class };
	JRadioButton jr[] = { new JRadioButton("대출내역"), new JRadioButton("관심도서") };
	JButton jb[] = { new JButton("삭제하기"), new JButton("pdf출력") };
	ButtonGroup bg = new ButtonGroup();
	DefaultTableModel m = model(" ,이미지,도서명,읽은페이지,대출일,반납일,대출상태,r_no,b_no".split(","));
	JTable t = new JTable(m) {
		public java.lang.Class<?> getColumnClass(int column) {
			return (Class<?>) tClass[column];
//			if (column == 0) {
//				return Boolean.class;
//			} else if (column == 1) {
//				return Icon.class;
//			}
//			return Object.class;
		};

		public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
			var comp = super.prepareRenderer(renderer, row, column);

			if (t.getValueAt(row, 6).toString().equals("연체중")) {
				comp.setForeground(Color.red);
				return comp;
			}
			comp.setForeground(Color.black);
			return comp;
		};

		public boolean isCellEditable(int row, int column) {
			if (column == 0)
				return true;
			return false;
		};
	};

	public My() {
		super("마이페이지", 1000, 450);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()), "South");
		n.add(ne = new JPanel(), "East");
		s.add(sw = new JPanel(), "West");
		s.add(se = new JPanel(), "East");

		n.add(lbl("회원 : " + u_name, JLabel.LEFT, 15), "West");
		for (var r : jr) {
			ne.add(r);
			bg.add(r);
			r.addActionListener(a -> {
				if (a.getActionCommand().equals("대출내역")) {
					addRow("SELECT b.b_img, b.b_name, concat(r.r_reading, \"/\", b.b_page), r.r_date, if(r.r_returnday= '0000-00-00', due_date, r.r_returnday), if(state = '0', '반납완료', if(state = '1', '연체중', '대출중')) as state,r.r_no,b.b_no FROM rentalstate r, book b where b.b_no = r.b_no and r.u_no = "
							+ u_no);
					jb[1].setEnabled(true);
					for (int i = 2; i < t.getColumnCount(); i++) {
						t.getColumnModel().getColumn(i)
								.setHeaderValue("도서명,읽은페이지,대출일,반납일,대출상태,r_no,b_no".split(",")[i - 2]);
					}
				} else {
					jb[1].setVisible(false);
					addRow("select b.b_img, b.b_name,d.d_name, b.b_author, b.b_page, concat(b.b_count, \"권\"), l.l_no, b.b_no from likebook l, book b, division d where l.b_no = b.b_no and b.d_no = d.d_no and l.u_no = "
							+ u_no);
					for (int i = 2; i < t.getColumnCount(); i++) {
						t.getColumnModel().getColumn(i).setHeaderValue("도서,분류,저자,페이지,수량,l_no,b_no".split(",")[i - 2]);
					}
				}
				repaint();
				revalidate();
			});
		}

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				jb[0].setEnabled(false);
				if (jr[0].isSelected()) {
					for (int i = 0; i < m.getRowCount(); i++) {
						if (t.getValueAt(i, 6).toString().equals("반납완료") && (Boolean) t.getValueAt(i, 0) == true) {
							jb[0].setEnabled(true);
						}
						if ((t.getValueAt(i, 6).toString().equals("연체중") || t.getValueAt(i, 6).toString().equals("대출중"))
								&& (Boolean) t.getValueAt(i, 0) == true) {
							jb[0].setEnabled(false);
						}
					}
				} else {
					for (int i = 0; i < m.getRowCount(); i++) {
						if ((Boolean) t.getValueAt(i, 0) == true) {
							jb[0].setEnabled(true);
							return;
						}
					}
					jb[0].setEnabled(false);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					for (int i = 0; i < m.getRowCount(); i++) {
						if (i != t.getSelectedRow()) {
							t.setValueAt(false, i, 0);
						} else {
							t.setValueAt(true, i, 0);
						}
					}
					var pop = new JPopupMenu();
					if (t.getValueAt(t.getSelectedRow(), 6).toString().equals("대출중")) {
						JMenuItem[] item = { new JMenuItem("연장하기"), new JMenuItem("반납하기") };
						for (var i : item) {
							i.addActionListener(a -> {
								if (a.getActionCommand().equals("연장하기")) {
									var rs = getRows("select r_count from rental where r_no = ?",
											t.getValueAt(t.getSelectedRow(), 7));
									for(var r : rs) {
										if(toInt(r.get(0)) != 0) {
											eMsg("연장은 1번만 가능합니다.");
											return;
										}else {
											//대출연장 폼 이동
											r_no = toInt(t.getValueAt(t.getSelectedRow(), 7).toString());
											new Cal().addWindowListener(new Before(My.this));
										}
									}
								}else {
									execute("update rental set r_returnday = ? where r_no = ?", LocalDate.now(), t.getValueAt(t.getSelectedRow(), 7));
									execute("update book set b_count = (b_count + 1) where b_no = ?", t.getValueAt(t.getSelectedRow(), 8));
									addRow("SELECT b.b_img, b.b_name, concat(r.r_reading, \"/\", b.b_page), r.r_date, if(r.r_returnday= '0000-00-00', due_date, r.r_returnday), if(state = '0', '반납완료', if(state = '1', '연체중', '대출중')) as state,r.r_no,b.b_no FROM rentalstate r, book b where b.b_no = r.b_no and r.u_no = " + u_no);
								}
							});
							pop.add(i);
						}
					}else if(t.getValueAt(t.getSelectedRow(), 6).toString().equals("연체중")) {
						var it = new JMenuItem("반납하기");
						pop.add(it);
					}
					pop.show(t, e.getX(), e.getY());
				}
				if (e.getClickCount() == 2) {
					if (t.getSelectedColumn() == 1) {
						if (t.getValueAt(t.getSelectedRow(), 6).toString().replace("권", "").equals("0")) {
							// 도서 정보 폼 이동
						} else {
							// 5-2 이동
						}
					}
				}
			}
		});

		c.add(sz(new JScrollPane(t), 900, 400));
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		var dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(0);
		for (int i = 2; i < m.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		t.getColumnModel().getColumn(7).setMinWidth(0);
		t.getColumnModel().getColumn(7).setMaxWidth(0);
		t.getColumnModel().getColumn(8).setMinWidth(0);
		t.getColumnModel().getColumn(8).setMaxWidth(0);
		t.setRowHeight(70);

		addRow("SELECT b.b_img, b.b_name, concat(r.r_reading,\"/\", b.b_page), r.r_date, if(r.r_returnday= '0000-00-00', due_date, r.r_returnday), if(state = '0', '반납완료', if(state = '1', '연체중', '대출중')) as state,r.r_no,b.b_no FROM rentalstate r, book b where b.b_no = r.b_no and r.u_no = "
				+ u_no);

		String all = null, reTurn = null, overdue = null, loan = null;

		try {
			var rs1 = getRows("select count(*) from rental r where r.u_no = ? group by r.u_no", u_no);
			all = rs1.get(0).get(0).toString();

			var rs2 = getRows(
					"SELECT count(*) FROM rental where u_no = ? and DATE_ADD(r_date, INTERVAL 14 + r_count DAY) > r_returnday and r_returnday",
					u_no);
			reTurn = rs2.get(0).get(0).toString();

			var rs3 = getRows(
					"SELECT count(*) FROM rental where u_no = ? and DATE_ADD(r_date, INTERVAL 14 + r_count DAY) < now() and (r_returnday = '0000-00-00' or r_returnday = null)",
					u_no);
			overdue = rs3.get(0).get(0).toString();

			var rs4 = getRows(
					"SELECT count(*) FROM rental where u_no = ? and DATE_ADD(r_date, INTERVAL 14 + r_count DAY) > now() and (r_returnday = '0000-00-00' or r_returnday = null);",
					u_no);
			loan = rs4.get(0).get(0).toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 4; i++) {
			sw.add(lbl("총 대여이력:,반납완료:,연체 중:,대출 중:".split(",")[i] + new String[] { all, reTurn, overdue, loan }[i] + "권",
					JLabel.LEFT, 15));
		}

		for (var b : jb) {
			if (b.getText().equals("삭제하기"))
				b.setEnabled(false);
			se.add(b);
			b.addActionListener(a -> {
				if (a.getActionCommand().equals("삭제하기")) {
					for (int i = 0; i < m.getRowCount(); i++) {
						if ((boolean) t.getValueAt(i, 0) == true) {
							try {
								if (jr[0].isSelected()) {
									execute("delete from rental where r_no = " + t.getValueAt(i, 7));
								} else {
									execute("delete from likebook where l_no = " + t.getValueAt(i, 7));
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				} else {
					try {
						var print = t.print();
					} catch (PrinterException e1) {
						e1.printStackTrace();
					}
				}
			});
		}

		jr[0].setSelected(true);
		setVisible(true);
	}

	void addRow(String sql) {
		m.setRowCount(0);

		var rs = getRows(sql);
		for (var r : rs) {
			Object objts[] = new Object[m.getColumnCount()];
			for (int i = 0; i < objts.length; i++) {
				if (i == 0) {
					objts[i] = false;
				} else if (i == 1) {
					objts[i] = getBlob(r.get(i - 1), 60, 60);
				} else {
					objts[i] = r.get(i - 1);
				}
			}
			m.addRow(objts);
		}
	}

	public static void main(String[] args) {
		u_no = "1";
		u_name = "박소희";
		new My();
	}
}

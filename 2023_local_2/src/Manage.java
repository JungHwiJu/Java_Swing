import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Manage extends BaseFrame {
	DefaultTableModel m = model("이미지,도서명,분류,저자,수량,페이지,b_no".split(","));
	JTable t = new JTable(m) {
		public java.lang.Class<?> getColumnClass(int column) {
			return column == 0 ? Icon.class : Object.class;
		};
	};
	JScrollPane jsp;
	JTextField jt;
	JComboBox<String> com;
	JLabel jl = new JLabel();
	String sql;

	public Manage() {
		super("도서관리", 850, 500);

		add(n = new JPanel(new BorderLayout()), "North");
		n.add(nc = new JPanel(new FlowLayout()));
		n.add(ns = new JPanel(new FlowLayout(4)), "South");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		var dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(0);
		for (int i = 1; i < m.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		nc.add(lbl("도서 관리", JLabel.CENTER, 30));
		ns.add(com = new JComboBox<String>("도서명,저자".split(",")));
		ns.add(sz(jt = new JTextField(25), 0, 35));
		ns.add(btn("검색", a -> {
			sql = "SELECT b.b_img, b.b_name,d.d_name,b.b_author,b.b_count, b.b_page,b.b_no, count(*) over() as count  FROM book b, division d where b.d_no = d.d_no and ";
			if (com.getSelectedIndex() == 0) {
				sql += "b.b_name like '%";
			} else {
				sql += "b.b_author like '%";
			}
			sql += jt.getText() + "%'";
			System.out.println(sql);
			search(sql);
		}));
		c.add(sz(new JScrollPane(t), 900, 400));
		
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
			
				if (e.getButton() == 3) {
					var pop = new JPopupMenu();
					var item = new JMenuItem("삭제하기");
					pop.add(item);
					item.addActionListener(a -> {
						try {
							stmt.execute("delete from book where b_no = " + t.getValueAt(t.getSelectedRow(), 6));
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						iMsg("삭제가 완료되었습니다");
						search(sql);
					});
					pop.show(t, e.getX(), e.getY());
				}
			}
		});

		s.add(jl);
		jl.setFont(new Font("", 1, 15));
		sql = "SELECT b.b_img, b.b_name,d.d_name,b.b_author,b.b_count, b.b_page,b.b_no, count(*) over() as count  FROM book b, division d where b.d_no = d.d_no;";
		t.setRowHeight(70);
		t.getColumnModel().getColumn(6).setMinWidth(0);
		t.getColumnModel().getColumn(6).setMaxWidth(0);
		t.getColumn("도서명").setPreferredWidth(350);
		t.getColumn("저자").setPreferredWidth(60);
		t.setBackground(Color.white);
		c.setBorder(new EmptyBorder(15, 5, 15, 5));
		search(sql);
		setVisible(true);
	}

	void search(String sql) {
		m.setRowCount(0);

		var rs = getRows(sql);
		for (var r : rs) {
			jl.setText("검색 건수 : " + rs.size() + "권");
			Object objs[] = new Object[m.getColumnCount()];
			for (int i = 0; i < objs.length; i++) {
				if (i == 0) {
					objs[i] = getBlob(r.get(0), 60, 60);
				}else {
					objs[i] = r.get(i);
				}
			}
			m.addRow(objs);
		}
	}

	public static void main(String[] args) {
		new Manage();
	}
}
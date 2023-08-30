import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class BookList extends BaseFrame {
	JComboBox<String> com;
	JTextField jt;
	DefaultTableModel m = model("분류".split(","));
	JTable t = table(m);
	JScrollPane jsp;
	JLabel jll = new JLabel();
	JPanel jp;

	public BookList() {
		super("도서목록", 800, 450);

		add(n = new JPanel(new BorderLayout()), "North");
		n.add(nc = new JPanel(new FlowLayout()));
		n.add(ns = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");

		nc.add(lbl("도서 목록", JLabel.CENTER, 30));
		ns.add(lbl("검색", JLabel.LEFT));
		ns.add(com = new JComboBox<String>("도서명,저자".split(",")));
		ns.add(jt = new JTextField(15));
		ns.add(btn("검색", a -> search()));

		m.addRow(new Object[] { "전체" });

		for (var r : getRows("select d_name from division"))
			m.addRow(new Object[] { r.get(0) });

		t.setRowSelectionInterval(0, 0);

		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(sz(new JScrollPane(t), 100, 0), "West");
		c.add(new JScrollPane(cc = new JPanel(new GridLayout(0, 4, 5, 5))));

		search();

		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		s.add(jll);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	void search() {
		cc.removeAll();

		String sql = "select b.b_no, b.b_name, b.b_img from book b, division d where b.d_no = d.d_no";
		if (t.getSelectedRow() != 0)
			sql += " and d.d_no = " + t.getSelectedRow() + "\r";
		if (com.getSelectedIndex() != 0)
			sql += " and b.b_author like '%" + jt.getText() + "%'";
		var rs = getRows(sql);

		if (rs.isEmpty()) {
			eMsg("검색결과가 없습니다.");
			com.setSelectedIndex(0);
			t.setRowSelectionInterval(0, 0);
			search();
			return;
		}

		for (var r : rs) {
			int no = (int) r.get(0);
			var tmp = new JPanel(new BorderLayout());
			sz(tmp, 100, 200);
			JLabel jl;
			jl = lbl((getRows("select * from likebook where u_no = ? and b_no = ?", u_no, no)).isEmpty() ? "♡" : "♥", 0,
					25);
			jl.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (getRows("select * from likebook where u_no = ? and b_no = ?", u_no, no).isEmpty()) {
						execute("insert likebook values(0,?,?)", u_no, no);
						jl.setText("♥");
					} else {
						execute("delete from likebook where u_no = ? and b_no = ?", u_no, no);
						jl.setText("♡");
					}
				}
			});
			jl.setForeground(Color.red);
			tmp.add(jp = new JPanel(new FlowLayout(0)) {
				@Override
				protected void paintComponent(Graphics g) {
					g.drawImage(getBlob(r.get(2), this.getWidth(), this.getHeight()).getImage(), 0, 0, null);
					setOpaque(false);
				}
			});

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					b_no = (int) r.get(0);
					new Info().addWindowListener(new Before(BookList.this));
				}
			});

			jp.add(jl);
			tmp.add(jp);
			tmp.add(lbl(r.get(1) + "", 0), "South");
			cc.add(tmp);
			tmp.setBorder(new LineBorder(Color.black));
		}

		while (cc.getComponentCount() < 6) {
			cc.add(new JPanel());
		}

		jll.setText("검색건수 : " + rs.size() + "건");
		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		u_no = "1";
		new BookList();
	}
}

package 지방_3과제;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class BaseFrame extends JFrame {
	static Connection con = DB.con;
	static Statement stmt = DB.stmt;
	static {
		try {
			stmt.execute("use 2023지방_3");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String f1 = "맑은 고딕", f2 = "HY헤드라인M";
	static DecimalFormat df = new DecimalFormat("#,##0");
	static ArrayList<Object> user;
	static ArrayList<String> peopleNameList = new ArrayList<>(); // 이름
	static ArrayList<Integer> peoplePriceList = new ArrayList<>(); // 가격
	static ArrayList<String> peopleResultList = new ArrayList<>(); // 좌석
	static ArrayList<String> peopleRating = new ArrayList<>(); // 등급
	static ArrayList<Object> ReceiptList = new ArrayList<>(); //티켓 수
	static ArrayList<Object> peopleAgeList = new ArrayList<>(); // 나이
	static ArrayList<Object> peopleBirthList = new ArrayList<>(); // 생일
	
	static String u_no = "";
	static int s_no;
	static int division;
	static String path;
	static LocalDate date;
	static int price;
	static String sno;
	static String arrive, depart, my_date;

	JPanel n, c, w, e, s;
	JPanel nn, nc, nw, ne, ns;
	JPanel cn, cc, cw, ce, cs;
	JPanel wn, wc, ww, we, ws;
	JPanel en, ec, ew, ee, es;
	JPanel sn, sc, sw, se, ss;

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		for (var f : UIManager.getLookAndFeelDefaults().keySet()) {
			if (f.toString().contains("back"))
				UIManager.getLookAndFeelDefaults().put(f, new ColorUIResource(Color.white));
		}
		((JPanel) getContentPane()).setBackground(Color.white);
		setVisible(true);
	}

	static ArrayList<ArrayList<Object>> getRows(String sql, Object... objs) {
		var col = new ArrayList<ArrayList<Object>>();
		try {
			var pst = con.prepareStatement(sql);
			if (objs != null)
				for (int i = 0; i < objs.length; i++) {
					pst.setObject(i + 1, objs[i]);
				}
			System.out.println(pst);
			var rs = pst.executeQuery();
			while (rs.next()) {
				var row = new ArrayList<>();
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					row.add(rs.getObject(i + 1));
				}
				col.add(row);
			}
			return col;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static void exectue(String sql, Object... objs) {
		try {
			var pst = con.prepareStatement(sql);
			if (objs != null)
				for (int i = 0; i < objs.length; i++) {
					pst.setObject(i + 1, objs[i]);
				}
			pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String getOne(String sql, Object... objs) {
		return getRows(sql, objs).get(0).get(0).toString();
	}

	static int toInt(Object o) {
		return Integer.parseInt(o.toString());
	}

	static boolean isNumeric(String t) {
		try {
			var i = Integer.parseInt(t);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	static void imsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
	}

	static void emsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	static ImageIcon getIcon(String t, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(t).getScaledInstance(w, h, 4));
	}

	static ImageIcon getBlob(Object t, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[]) t).getScaledInstance(w, h, 4));
	}

	static JLabel lbl(String t, int a, int f, int s) {
		JLabel l = new JLabel(t, a);
		l.setFont(new Font("맑은 고딕", f, s));
		return l;
	}

	static JLabel lbl(String t, int a, int s) {
		var l = new JLabel(t, a);
		l.setFont(new Font("", 1, s));
		return l;
	}

	static JLabel lbl(String t, int a, int s, String f) {
		var l = new JLabel(t, a);
		l.setFont(new Font(f, 1, s));
		return l;
	}

	static JLabel lbl(String t, int a, int s, String f, Consumer<MouseEvent> c) {
		var l = lbl(t, a, s, f);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				c.accept(e);
			}
		});
		return l;
	}

	static JButton btn(String cap1, ActionListener a) {
		var b = new JButton(cap1);
		b.addActionListener(a);
		return b;
	}

	static DefaultTableModel model(String str[]) {
		var d = new DefaultTableModel(null, str) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return d;
	}

	static JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(0);
		var dtcr = new DefaultTableCellRenderer();
		for (int i = 0; i < m.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		dtcr.setHorizontalAlignment(0);
		return t;
	}

	static <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	class Before extends WindowAdapter {
		BaseFrame b;
		
		public Before(BaseFrame b) {
			this.b = b;
			setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			setVisible(true);
		}
	}
	
	
}

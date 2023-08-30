

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.mysql.cj.jdbc.NonRegisteringDriver;

public interface Base {
	
	static Connection con = DB.con;
	static Statement stmt = DB.stmt;

	default ArrayList<ArrayList<Object>> getRows(String sql, Object... vals) {
		var row = new ArrayList<ArrayList<Object>>();

		try {
			var pst = con.prepareStatement(sql);
			if (vals != null) {
				for (int i = 0; i < vals.length; i++) {
					pst.setObject(i + 1, vals[i]);
				}
			}
			System.out.println(pst);

			var rs = pst.executeQuery();
			while (rs.next()) {
				var col = new ArrayList<Object>();
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					col.add(rs.getObject(i + 1));
				}
				row.add(col);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return row;
	}

	default ArrayList<Object> getRow(String sql, Object... vals) {
		if (getRows(sql, vals).isEmpty()) {
			return null;
		} else {
			return getRows(sql, vals).get(0);
		}
	}
	
	default int toInt(Object o) {
		return Integer.parseInt(o.toString());
	}
	
	default boolean isNumeric(String t) {
		try {
			Integer.parseInt(t);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	default void execute(String sql, Object... vals) {
		try {
			var pst = con.prepareStatement(sql);
			if (vals != null) {
				for (int i = 0; i < vals.length; i++) {
					pst.setObject(i + 1, vals[i]);
				}
			}
			pst.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
	}

	default int cint(Object o) {
		return Integer.parseInt(o.toString());
	}
	
	default JLabel lbl(String lbl, int a, Consumer<MouseEvent> con) {
		var l = lbl(lbl, a, 13);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				con.accept(e);
			}
		});
		return l;
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default JLabel lbl(String t, int a, int s) {
		var l = new JLabel(t, a);
		l.setFont(new Font("맑은 고딕", Font.BOLD, s));
		return l;
	}
	default JLabel lbl(String t, int a) {
		return new JLabel(t, a);
	}

	interface invoker {
		void run(MouseEvent e);
	}
	
	default ImageIcon getBlob(Object t, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[]) t).getScaledInstance(w, h, 4));
	}
	
	default JLabel lbl(String t, int a, String ty, int b, int s) {
		JLabel jl = new JLabel(t,a);
		jl.setFont(new Font(ty, b, s));
		return jl;
		
	}

	default JLabel lbl(String t, int a, int s, invoker i) {
		var l = new JLabel(t, a);
		l.setFont(new Font("맑은 고딕", Font.PLAIN, s));
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				i.run(e);
			}
		});

		return l;
	}

	default ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, 4));
	}

	default JButton btn(String t, ActionListener a) {
		var b = new JButton(t);
		b.addActionListener(a);
		return b;
	}

	
	default DefaultTableModel model(String[] col) {
		var m = new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return m;
	}

	default JTable table(DefaultTableModel m) {
		var jt = new JTable(m);
		var dtcr = new DefaultTableCellRenderer();

		jt.getTableHeader().setReorderingAllowed(false);
		jt.getTableHeader().setResizingAllowed(false);
		jt.getTableHeader().setBackground(Color.white);
		dtcr.setHorizontalAlignment(0);

		for (int i = 0; i < m.getColumnCount(); i++) {
			jt.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return jt;
	}

	default void addRow(DefaultTableModel m, ArrayList<ArrayList<Object>> rs) {
		m.setRowCount(0);

		for (var r : rs) {
			m.addRow(r.toArray());
		}
	}

	default JFileChooser fileChooser(JLabel img, int w, int h) {
		var jfc = new JFileChooser("datafiles/상품이미지/");
		var filter = new FileNameExtensionFilter("JPG Images", "jpg");

		jfc.setFileFilter(filter);
		jfc.setDialogTitle("이미지 선택");
		if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			img.setIcon(getIcon(jfc.getSelectedFile().getPath(), w, h));
		}

		return jfc;
	}

	class Before extends WindowAdapter {
		JFrame f;

		public Before(JFrame f) {
			this.f = f;
			f.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			f.setVisible(true);
		}
	}

}

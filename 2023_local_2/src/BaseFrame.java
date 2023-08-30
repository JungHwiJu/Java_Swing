

import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BaseFrame extends JFrame implements Base {

	JPanel n, c, s, e, w;
	JPanel nn, nc, ns, ne, nw;
	JPanel cn, cc, cs, ce, cw;
	JPanel sn, sc, ss, se, sw;
	JPanel en, ec, es, ee, ew;
	JPanel wn, wc, ws, we, ww;
	static int b_no, r_no;
	static String u_name, b_name, r_name;
	static String u_no;
	
	static ArrayList<Object> user;
	static ArrayList<String> division = new ArrayList<String>();

	static {
		try {
			stmt.execute("use 2023지방_2");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		((JPanel) getContentPane()).setBackground(Color.white);
	}

}

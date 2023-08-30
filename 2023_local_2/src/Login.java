import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {
	JTextField jt[] = {new JTextField(), new JPasswordField()};
	
	int cnt = 0;

	public Login() {
		super("로그인", 400, 220);

		setLayout(new BorderLayout());

		add(n = new JPanel(new BorderLayout()), "North");
		n.add(lbl("로그인", 0, "HY헤드라인M", 0, 25));

		add(c = new JPanel(new GridLayout(0, 1, 10, 15)));
		String st[] = "아이디,비밀번호".split(",");
		for (int i = 0; i < st.length; i++) {
			var p = new JPanel(new BorderLayout());
			p.add(sz(lbl(st[i], 2), 80, 1), "West");
			p.add(jt[i]);
			c.add(p);
		}

		var p = new JPanel(new BorderLayout());
		p.setBorder(new EmptyBorder(5, 0, 0, 0));
		c.add(p);
		sz(p, 1, 35);

		p.add(btn("로그인", a -> {
			var id = jt[0].getText();
			var pw = jt[1].getText();

			if (id.isEmpty() || pw.isEmpty()) {
				eMsg("빈칸이 있습니다.");
				return;
			}

			var rs = getRow("select * from user where u_id = ? and u_pw = ?", id, pw + "\r");
			if (rs == null) {
				eMsg("아이디 또는 비밀번호를 확인하세요");
				cnt++;
				jt[0].setText("");
				jt[1].setText("");
				jt[0].requestFocus();
				return;
			}
			if (cnt == 3) {
				eMsg("3회 오류로 종료합니다");
				dispose();
			}
			u_no = rs.get(0).toString();
			u_name = rs.get(1).toString();
			System.out.println(u_no);
			System.out.println(u_name);

			dispose();

		}));

		c.setBorder(new EmptyBorder(3, 20, 20, 20));

		setVisible(true);

	}

	public static void main(String[] args) {
		new Login();

	}
}

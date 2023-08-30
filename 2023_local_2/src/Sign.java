
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Sign extends BaseFrame {
	String str[] = "이름,아이디,비밀번호".split(",");
	JTextField jt[] = new JTextField[3];

	public Sign() {
		super("회원가입", 400, 300);

		add(lbl("회원가입", JLabel.CENTER, 30), "North");
		add(c = new JPanel(new GridLayout(0, 1, 20, 10)));
		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new BorderLayout());
			tmp.add(sz(lbl(str[i], JLabel.LEFT, 15), 80, 1), "West");
			tmp.add(jt[i] = new JTextField(25));
			c.add(tmp);
		}
		c.add(cs = new JPanel(new BorderLayout()));
		cs.setBorder(new EmptyBorder(0, 0, 10, 0));
		cs.add(btn("회원가입", a -> {
			for (var t : jt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			if (!jt[0].getText().matches(".*[가-힣].*") || jt[0].getText().length() < 2) {
				eMsg("이름은 한글로 2글자 이상만 가능합니다.");
				jt[0].setText("");
				jt[0].requestFocus();
				return;
			}
			if (jt[1].getText().equals("admin")
					|| !getRows("select * from user where u_id = ?", jt[1].getText()).isEmpty()) {
				eMsg("이미 있는 아이디입니다.");
				return;
			}
			if (jt[1].getText().matches(".*[가-힣].*") || jt[1].getText().matches(".*[ㄱ-ㅎ].*")) {
				eMsg("아이디에 한글은 사용이 불가합니다.");
				return;
			}
			if (!(jt[2].getText().matches(".*[a-zA-Z].*") && jt[2].getText().matches(".*[0-9].*")
					&& jt[2].getText().matches(".*\\W.*") && jt[2].getText().length() >= 6)) {
				eMsg("비밀번호 형식이 일치하지 않습니다.");
				return;
			}
			String stchk[] = jt[2].getText().split("");

			for (int i = 0; i < jt[2].getText().length() - 1; i++) {
				if (stchk[i].equals(stchk[i + 1])) {
					eMsg("비밀번호는 연속으로 같은 글자가 올 수 없습니다.");
					return;
				}
			}
			iMsg(jt[0].getText() + "님 회원가입이 완료되었습니다.");
			execute("insert into user values(0, ?, ?, ?)", jt[0].getText(), jt[1].getText(), jt[2].getText());
			dispose();
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(25, 20, 10, 20));
		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}

package 지방_3과제;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Sign extends BaseFrame {
	JLabel lbl;
	JButton btn1, btn2;
	String str[] = "아이디,비밀번호,이름(한글),이름(영문),생년월일".split(",");
	JTextField jt[] = new JTextField[5];
	boolean flag;

	public Sign() {
		super("회원가입", 500, 350);

		add(lbl = lbl("회원가입", JLabel.CENTER, 30, f2), "North");
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(1)), "South");
		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(lbl(str[i], JLabel.LEFT, 13), 90, 25));
			if (i == 0) {
				tmp.add(sz(jt[0] = new JTextField(15), 0, 25));
				tmp.add(btn1 = btn("중복확인", a -> {
					if (jt[0].getText().isEmpty()) {
						emsg("아이디를 입력해주세요.");
						return;
					}
					var id = getRows("select * from user where id = ?", jt[0].getText());
					if (!id.isEmpty()) {
						emsg("중복된 아이디입니다.");
						jt[0].setText("");
						jt[0].requestFocus();
						return;
					}
					imsg("사용 가능한 아이디입니다.");
					flag = true;
				}));
			} else
				tmp.add(sz(jt[i] = new JTextField(15), 0, 25));
			c.add(tmp);
		}
		s.add(btn2 = btn("회원가입", a -> {
			if (a.getActionCommand().equals("회원가입")) {
				for (var t : jt) {
					if (t.getText().isEmpty()) {
						emsg("빈칸이 있습니다.");
						return;
					}
				}

				if (!flag) {
					emsg("중복확인을 해주세요.");
					return;
				}

				if (!jt[2].getText().matches(".*[가-힣].*")) {
					emsg("한글 이름은 한글만 가능합니다.");
					jt[2].setText("");
					jt[2].requestFocus();
					return;
				}

				if (!jt[3].getText().matches(".*[a-zA-Z].*")) {
					emsg("영어 이름은 영문만 가능합니다.");
					jt[3].setText("");
					jt[3].requestFocus();
					return;
				}
				
				if (jt[3].getText().split(" ").length != 2) {
					emsg("영문 이름은 성과 이름을 구분해주세요.");
					jt[3].setText("");
					jt[3].requestFocus();
					return;
				}
				
				try {
					if (LocalDate.parse(jt[4].getText()).isAfter(LocalDate.now())) {
						emsg("생년월일을 확인해주세요.");
						return;
					}
				} catch (Exception e1) {
					emsg("생년월일을 확인해주세요.");
					return;
				}

				imsg(jt[0].getText() + "님 회원가입이 완료되었습니다.");
				exectue("insert user values(0,?,?,?,?,?,?)", jt[0].getText(), jt[1].getText(), jt[2].getText(),
						jt[3].getText(), jt[4].getText().toUpperCase(), 0);
				dispose();
			} else {
				imsg(jt[0].getText() + "님 정보가 수정되었습니다.");
				exectue("update user set pw = ?, name1 = ?, name2 = ?, birth = ? where u_no = ?", jt[1].getText(), jt[2].getText(),
						jt[3].getText(), jt[4].getText(), u_no);
				dispose();
			}
		}));
		pack();
	}

	public Sign(String u_no) {
		this();
		
		var rs = getRows("select * from user where u_no = ?", u_no);
		btn1.setVisible(false);
		btn2.setText("정보수정");

		jt[0].setText(rs.get(0).get(1).toString());
		jt[1].setText(rs.get(0).get(2).toString());
		jt[2].setText(rs.get(0).get(3).toString());
		jt[3].setText(rs.get(0).get(4).toString());
		jt[4].setText(rs.get(0).get(5).toString());
		jt[0].setEnabled(false);
		
		
		pack();
	}

	public static void main(String[] args) {
		u_no = "1";
		new Sign();
	}
}

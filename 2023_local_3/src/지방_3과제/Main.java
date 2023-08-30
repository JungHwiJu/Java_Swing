package 지방_3과제;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.mysql.cj.util.LogUtils;

public class Main extends BaseFrame {
	String str[] = "ID:,PW:".split(",");
	JTextField jt[] = new JTextField[2];
	JButton btn1[] = { new JButton("로그인"), new JButton("회원가입"), new JButton("대륙별분석") };
	JButton btn2[] = { new JButton("로그아웃"), new JButton("정보수정"), new JButton("항공권 조회"), new JButton("마이페이지"),
			new JButton("연령별분석") };
	JPanel main;
	ArrayList<JLabel> imglist = new ArrayList<>();

	public Main() {
		super("메인", 650, 550);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(null));
		add(s = new JPanel(new BorderLayout()), "South");
		s.add(main = new JPanel());
		main.add(sc = new JPanel(new GridLayout(0, 1)));
		s.add(ss = new JPanel(new FlowLayout(1)), "South");

		n.add(new JLabel(getIcon("datafiles/구름.jpg", 650, 150)), "North");
		n.add(lbl("SKY AIRLINE", JLabel.CENTER, 30, f2));
		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(lbl(str[i], JLabel.LEFT, 15), 50, 25));
			tmp.add(jt[i] = new JTextField(15));
			sc.add(tmp);
		}

		var img = new JLabel(getIcon("datafiles/비행기.jpg", 200, 200));
		c.add(img).setBounds(imglist.size() * 200, 0, 450, 200);
		imglist.add(img);

		logout();
		ani();
		((JPanel) getContentPane()).setBorder(new EmptyBorder(0, 5, 5, 5));
	}

	void login() {
		setSize(650, 500);
		sc.setVisible(false);
		ss.removeAll();
		for (var btn : btn2) {
			ss.add(btn);
			btn.addActionListener(a -> {
				if (a.getActionCommand().equals("로그아웃")) {
					dispose();
				} else if (a.getActionCommand().equals("정보수정")) {
					new Sign(u_no).addWindowListener(new Before(Main.this));
				} else if (a.getActionCommand().equals("항공권 조회")) {
					new Aviation().addWindowListener(new Before(Main.this));
				} else if (a.getActionCommand().equals("마이페이지")) {
					new My().addWindowListener(new Before(Main.this));
				} else {
					new Chart().addWindowListener(new Before(Main.this));
				}
			});
		}

		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				new Main();
			};
		});
	}

	void logout() {
		setSize(650, 550);
		jt[0].setText("");
		jt[1].setText("");
		sc.setVisible(true);
		ss.removeAll();
		for (var btn : btn1) {
			ss.add(sz(btn, 100, 30));
			btn.addActionListener(a -> {
				if (a.getActionCommand().equals("로그인")) {
					if (jt[0].getText().isEmpty() || jt[1].getText().isEmpty()) {
						emsg("공백이 있습니다.");
						return;
					}

					var rs = getRows("select * from user where id = ? and pw = ?", jt[0].getText(), jt[1].getText());
					if (rs.isEmpty()) {
						emsg("ID 또는 PW를 확인하세요.");
						jt[0].setText("");
						jt[1].setText("");
						jt[0].requestFocus();
						return;
					}

					user = rs.get(0);
					u_no = rs.get(0).get(0).toString();
					imsg(user.get(3) + "님 환영합니다.");
					login();
				} else if (a.getActionCommand().equals("회원가입")) {
					new Sign().addWindowListener(new Before(Main.this));
				} else {
					new World().addWindowListener(new Before(Main.this));
				}
			});
		}
	}

	void ani() {
		new Thread(() -> {
			while (true) {
				for (int i = 0; i < imglist.size(); i++) {
					imglist.get(i).setLocation(imglist.get(i).getX() + 1,
							(int) (Math.sin(imglist.get(0).getX() / 25) * 8));
					if (imglist.get(i).getX() >= 500)
						imglist.get(i).setLocation(-400, 0);
					try {
						Thread.sleep(5);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static void main(String[] args) {
		new Main();
	}
}
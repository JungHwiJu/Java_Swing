import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;

public class Main extends BaseFrame {

	FadeImage fadeImage;
	JComboBox<String> com;
	JButton btn[] = new JButton[6];

	int idx = 1; // 이미지 변경 idx
	String str[] = "로그인,회원가입,도서 목록,마이페이지,책 읽기,종료".split(",");
	String imgStr = "로그인 후 이용해주세요.";

	public Main() {
		super("메인", 900, 700);

		add(fadeImage = new FadeImage(), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(), "South");

		c.add(cn = new JPanel(new FlowLayout(0)), "North");
		c.add(cc = new JPanel(new GridLayout(1, 0, 25, 0)));

		for (int i = 0; i < str.length; i++) {
			s.add(btn[i] = btn(str[i], e -> {
				switch (e.getActionCommand()) {
				case "로그인":
					new Login().addWindowListener(new Before(Main.this));
					break;
				case "회원가입":
					new Sign().addWindowListener(new Before(Main.this));
					break;
				case "도서목록":
					new BookList().addWindowListener(new Before(Main.this));
					break;
				case "마이페이지":
					break;
				case "책 읽기":
					break;
				case "도서관리":
					break;
				case "도서등록":
					break;
				case "통계":
					break;
				case "로그아웃":
					break;
				default:
					System.exit(0);
				}
			}));
		}

		cn.add(com = new JComboBox<String>());

		com.addItem("전체");
		for (var r : getRows("select d_name from division")) {
			com.addItem(r.get(0).toString());
		}

		com.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				setItem();
			}
		});

		setItem();
		logout();

//		adminLogin();

		btn[2].setEnabled(false);
		btn[3].setEnabled(false);
		btn[4].setEnabled(false);
		c.setBorder(new TitledBorder(new LineBorder(Color.black), "인기 도서", 1, 0, new Font("맑은 고딕", Font.BOLD, 15)));
		c.setOpaque(false);
		s.setOpaque(false);
		cn.setOpaque(false);
		cc.setOpaque(false);
		setVisible(true);
	}

	void logout() {
		imgStr = "로그인 후 이용해주세요.";
		fadeImage.repaint();
		fadeImage.revalidate();
	}

	void userLogin() {
		imgStr = u_name + "님 환영합니다.";
		fadeImage.repaint();
		fadeImage.revalidate();

		btn[0].setText("로그아웃");
		btn[1].setEnabled(false);
		btn[2].setEnabled(true);
		btn[3].setEnabled(true);
		btn[4].setEnabled(true);
	}

	void adminLogin() {
		imgStr = "관리자님 환영합니다.";
		fadeImage.repaint();
		fadeImage.revalidate();

		btn[0].setText("로그아웃");
		btn[2].setText("도서관리");
		btn[3].setText("도서등록");
		btn[4].setText("대출통계");
		btn[1].setVisible(false);
		for (int i = 0; i < btn.length; i++) {
			btn[i].setEnabled(true);
		}
	}

	void setItem() {
		cc.removeAll();

		int idx = com.getSelectedIndex();
		String division = (idx == 0 ? "" : "and d.d_no='" + idx + "'");

		var rs = getRows(
				"SELECT b.b_img, b.b_no, b.b_name, b.b_author, b.b_exp, count(1) FROM 2023지방_2.rental r, book b, division d where r.b_no=b.b_no and b.d_no=d.d_no "
						+ division + " group by b.b_no order by count(1) desc, b.b_no limit 5");
		for (var r : rs) {
			JPanel tmp = new JPanel(new BorderLayout());
			JLabel img = new JLabel(new ImageIcon(
					Toolkit.getDefaultToolkit().createImage((byte[]) r.get(0)).getScaledInstance(120, 150, 4)));

			tmp.add(img);
			tmp.add(lbl(r.get(2).toString(), 0, 13), "South");

			img.setToolTipText("<html>저자 : " + r.get(3) + "<br>설명 : " + r.get(4));
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2) {
						if (user == null) {
							if (btn[0].getText().equals("로그인")) {
								eMsg("로그인 후 이용해주세요.");
								return;
							} else {
								// 도서수정 폼
							}
						} else {
							// 도서정보 폼
						}
					}
				}
			});

			cc.add(tmp);

			tmp.setOpaque(false);
			tmp.setBorder(new LineBorder(Color.black));
		}

		cc.repaint();
		cc.revalidate();
	}

	class FadeImage extends JPanel {
		public static final long RUNNING_TIME = 2000;

		Timer timer1 = null;
		Timer timer2 = null;
		BufferedImage[] imgs = new BufferedImage[3];
		BufferedImage inImage = null;

		private float alpha = 0f;
		private long startTime = -1;
		private int idx = 0;

		public FadeImage() {
			try {
				for (int i = 0; i < imgs.length; i++) {
					imgs[i] = ImageIO.read(new File("datafiles/메인" + (i + 1) + ".jpg"));
				}
			} catch (IOException exp) {
				exp.printStackTrace();
			}

			timer1 = new Timer(40, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (startTime < 0) {
						startTime = System.currentTimeMillis();
					} else {
						long duration = System.currentTimeMillis() - startTime;
						if (duration >= RUNNING_TIME) {
							startTime = -1;
							timer1.stop();
							timer2.start();
						} else {
							alpha += 0.02f;
						}
						repaint();
					}
				}
			});

			timer2 = new Timer(40, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (startTime < 0) {
						startTime = System.currentTimeMillis();
					} else {
						long duration = System.currentTimeMillis() - startTime;
						if (duration >= RUNNING_TIME) {
							startTime = -1;
							idx = (idx == 2 ? 0 : idx + 1);
							alpha = 0f;
							inImage = imgs[idx];
							startTime = -1;
							timer2.stop();
							timer1.start();
						} else {
							alpha = 1f - ((float) duration / (float) RUNNING_TIME);
						}
						repaint();
					}
				}
			});

			sz(this, 0, 400);
			inImage = imgs[idx];
			timer2.start();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2d.drawImage(inImage, 0, 0, 900, 400, this);
			g2d.setFont(new Font("HY헤드라인M", Font.BOLD, 50));
			g2d.setColor(Color.white);
			g2d.drawString(imgStr, 230, 200);
		}
	}

	public static void main(String[] args) {
		UIManager.put("ToolTip.background", new ColorUIResource(Color.white));
		new Main();
	}
}

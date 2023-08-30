import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class Info extends BaseFrame {
	JButton btn;
	JLabel img, jl;
	JPanel jp, jp2;
	JTextArea area = new JTextArea();

	public Info() {
		super("도서 정보", 400, 450);

		var b_info = getRows("select * from book where b_no = ?", b_no).get(0);

		add(n = new JPanel(new FlowLayout(FlowLayout.LEFT)), "North");
		add(c = new JPanel(new GridLayout(1, 0, 5, 5)));
		c.add(img = sz(new JLabel(getBlob(b_info.get(7), 200, 250)), 200, 250));
		c.add(cc = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()), "South");
		s.add(sc = new JPanel(new BorderLayout()));
		s.add(ss = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		img.setBorder(new LineBorder(Color.black));
		n.add(lbl(b_info.get(1).toString(), JLabel.LEFT, 30));

		cc.add(jp = new JPanel(new FlowLayout(0)), "North");
		cc.add(jp2 = new JPanel(new GridLayout(0, 1)));
		// jp.add(jl = lbl(division.get((int) b_info.get(2)), JLabel.LEFT, 15));
		// jl.setBorder(new CompoundBorder(new LineBorder(Color.black), new
		// EmptyBorder(5, 5, 5, 5)));
		jp.add(jl = lbl(
				getRows("select d.d_name, b.b_no from book b, division d where b.d_no = d.d_no and b.b_no = ?", b_no)
						.get(0).get(0).toString(),
				JLabel.CENTER, 20));
		sz(jl, 100, 30);
		jl.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));

		String cap[] = { "저자 : " + b_info.get(3), "재고 : " + b_info.get(4) + "권 / 페이지 : " + b_info.get(5) + "p" };
		for (var t : cap) {
			jl = lbl(t, 2, 15);
			jl.setBorder(new MatteBorder(0, 0, 2, 0, Color.black));
			jp2.add(jl);
		}
		jp2.setBorder(new EmptyBorder(0, 0, 100, 0));

		area.setLineWrap(true);
		area.setEditable(false);
		sc.add(area);
		area.setText(b_info.get(6).toString());
		area.setBorder(new TitledBorder(new LineBorder(Color.black), "설명", 0, 0, new Font("HY헤드라인M", 0, 15)));
		ss.add(btn = btn("대출", a -> {
			try {
				var rs = getRows("select r_date, r_returnday, r_count from rental where b_no = ? and u_no = ?", b_no,
						u_no).get(0);
				if (LocalDate.parse(rs.get(0).toString()).isAfter(LocalDate.now())
						&& LocalDate.parse(rs.get(1).toString()).isBefore(LocalDate.now())) {
					eMsg("이미 대출 중인 도서입니다.");
					return;
				}
				if (LocalDate.parse(rs.get(1).toString()).plusDays((int) rs.get(2)).isAfter(LocalDate.now())) {
					eMsg("현재 연체중인 도서가 있습니다.\n도서를 반납하시고 이용해주세요.");
					return;
				}
			} catch (Exception e) {
				System.out.println("여기임");
			}
			if (getRows("select b_count from book where b_no = ?", b_no).equals("0")) {
				btn.setEnabled(false);
			}
			iMsg("대출이 완료되었습니다.");
			jl.setText("재고 : " + ((int) b_info.get(4) - 1) + "권 / 페이지 : " + b_info.get(5) + "p");
			execute("update book set b_count = b_count -1 where b_no = ?", b_no);
			execute("insert rental values(0,?,?,curdate(),0,0,date_add(curdate(), interval 14 day))", u_no, b_no);
			dispose();
		}));

		if (getRows("select b_count from book where b_no = ?", b_no).equals("0")) {
			btn.setEnabled(false);
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		u_no = "1";
		b_no = 1;
		new Info();
	}
}

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class BookEdit extends BaseFrame {
	String str[] = "분류,도서명,저자,수량,페이지".split(",");
	JComboBox<String> com;
	JTextField jt[] = new JTextField[4];
	JTextArea area;
	JButton btn;
	File f;
	int b_no;
	JLabel img;

	public BookEdit(int b_no) {
		super(b_no == 0 ? "도서등록" : "도서수정", 580, 500);

		add(c = new JPanel(new BorderLayout()));
		c.add(img = sz(new JLabel(getIcon("datafiles/book/" + b_no + ".jpg", 250, 280)), 250, 280));
		c.add(cc = new JPanel(new GridLayout(0, 1)),"East");
		add(s = new JPanel(new BorderLayout()), "South");
		s.add(sc = new JPanel(new BorderLayout()));
		s.add(ss = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");

		img.setBorder(new LineBorder(Color.black));

		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(lbl(str[i], JLabel.LEFT, 20), 80, 25));
			if (i == 0) {
				tmp.add(com = new JComboBox<String>());
				com.addItem("");
				for (var r : getRows("select d_name from division"))
					com.addItem(r.get(0).toString());
			} else {
				tmp.add(sz(jt[i - 1] = new JTextField(20), 0, 35));
			}
			cc.add(tmp);
		}
		sc.add(lbl("설명", JLabel.LEFT, 15), "North");
		sc.add(area = new JTextArea(6, 55));
		area.setLineWrap(true);
		area.setBorder(new LineBorder(Color.black));

		ss.add(btn = btn("등록", a -> {
			if (a.getActionCommand().equals("수정")) {
				for (var t : jt) {
					if (t.getText().isEmpty()) {
						eMsg("빈칸이 있습니다.");
						return;
					}
				}

				if (!isNumeric(jt[2].getText())) {
					eMsg("수량은 1이상의 숫자로 입력하세요.");
					jt[2].setText("");
					jt[2].requestFocus();
					return;
				}

				execute("update book set b_author = ?, b_count = ? where b_no = ?", jt[1].getText(), jt[2].getText(),
						b_no);
				if (f != null) {
					try {
						execute("update base set b_img = ? where b_no = ?", new FileInputStream(f), b_no);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				iMsg("수정이 완료되었습니다.");
			} else {
				var rs = getRows("select * from book where b_name like ? and b_author like ? and d_no = ?",
						"%" + jt[0].getText() + "%", "%" + jt[1].getText() + "%", com.getSelectedIndex());
				if (!rs.isEmpty()) {
					eMsg("이미 있는 도서입니다.");
					return;
				}
				try {
					execute("insert book values(0,?,?,?,?,?,?,?)", jt[0].getText(), com.getSelectedIndex(),
							jt[1].getText(), jt[2].getText(), jt[3].getText(), area.getText(), new FileInputStream(f));
				} catch (Exception e) {
					e.printStackTrace();
				}
				iMsg("등록이 완료되었습니다.");
			}
		}));

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					var jfc = new JFileChooser("datafiles/book");
					jfc.setMultiSelectionEnabled(false);
					if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						img.setIcon(getIcon(jfc.getSelectedFile().getPath(), img.getWidth(), img.getHeight()));
						f = jfc.getSelectedFile();
					}
				}
			}
		});

		s.setBorder(new EmptyBorder(5, 0, 0, 0));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 5, 20, 5));
		if (b_no != 0) {
			this.b_no = b_no;
			data(b_no);
		}
		setVisible(true);
	}

	void data(int b_no) {
		var rs = getRows("select * from book where b_no = ?", b_no);

		if (rs.isEmpty()) {
			btn.setText("등록");
		} else {
			f = null;
			btn.setText("수정");
			var r = rs.get(0);

			img.setIcon(getBlob(r.get(7), 250, 280));
			com.setSelectedIndex(toInt(r.get(2)));
			jt[0].setText(r.get(1).toString());
			jt[1].setText(r.get(3).toString());
			jt[2].setText(r.get(4).toString());
			jt[3].setText(r.get(5).toString());
			area.setText(r.get(6).toString());
			com.setEnabled(false);
			jt[0].setEnabled(false);
			jt[3].setEnabled(false);
		}
	}

	public static void main(String[] args) {
		new BookEdit(1);
	}
}

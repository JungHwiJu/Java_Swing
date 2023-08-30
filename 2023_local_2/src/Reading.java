import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Reading extends BaseFrame {
	ArrayList<String> rentalBook = new ArrayList<String>(), allReadBook = new ArrayList<String>();
	ImageIcon img;
	JLabel jl, imglbl;
	DefaultTableModel m = model("도서명,r_no".split(","));
	JTable t = new JTable(m) {
		public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
			JComponent jcom = (JComponent) super.prepareRenderer(renderer, row, column);
			if (allReadBook.contains(m.getValueAt(row, column).toString())) {
				jcom.setForeground(Color.blue);
			} else
				jcom.setForeground(Color.black);
			return jcom;
		};
	};
	JButton btn;
	String r_book;
	boolean flag;
	Thread th;
	JScrollPane jsp;
	JPanel p;
	int rp, ap, r;

	public Reading() {
		super("책읽기", 500, 250);

		add(c = new JPanel(new BorderLayout()));
		img = null;
		c.add(sz(jl = new JLabel(img), 150, 0));
		c.add(cc = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()));

		jl.setBorder(new LineBorder(Color.black));

		cc.add(jsp = new JScrollPane(t));

		s.add(btn = btn("읽기", a -> {
			if (a.getActionCommand().equals("읽기")) {
				if (th != null) {
					flag = true;

				} else {
					setSize(600, 400);
					p.setVisible(true);
					th = new Thread(() -> {
						flag = true;
						imglbl.setText(rp + "/" + ap);
						while(true) {
							try {
								Thread.sleep(500);
								imglbl.setText(rp + "/" + ap);
								rp++;
								if(rp == ap) {
									iMsg("책을 다 읽었습니다.");
									setSize(600, 350);
									btn.setText("읽기");
									btn.setEnabled(false);
									execute("update rental set r_reading = ? where r_no = ?", ap, m.getValueAt(r, 1));
									allReadBook.add(r_book);
									repaint();
									revalidate();
								}
								repaint();
							} catch (Exception e) {
								e.printStackTrace();
								th = null;
								flag = false;
								break;
							}
						}
					});
					th.start();
				}
				btn.setText("그만읽기");
			} else {
				execute("update rental set r_reading = ? where r_no = ?", rp, m.getValueAt(t.getSelectedRow(), 1));
				flag = false;
				th.suspend();
				btn.setText("읽기");
			}
		}), "South");

		s.add(sz(p = new JPanel(new BorderLayout()), 0, 50));
		ap = 10;
		rp = 2;
		p.add(imglbl = new JLabel("", 0) {
			@Override
			public void paint(Graphics g) {
				// TODO Auto-generated method stub
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.green);
				g2d.fillRect(0, 0, (int) ((double) this.getWidth() / ap * rp), this.getHeight());
			}
		});
		p.setBorder(new LineBorder(Color.black));
		p.setVisible(false);

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (flag) {
					eMsg("책 읽기를 멈춘 후 선택하세요.");
					t.changeSelection(r, 0, false, false);
					return;
				}
				int row = t.getSelectedRow();
				r = row;
				if (allReadBook.contains(t.getValueAt(row, 0))) {
					if (JOptionPane.showConfirmDialog(null, "읽기를 완료한 도서입니다.\n다시 읽으시겠습니까?", "질문",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						execute("update rental set r_reading = 0 where r_no = ?", m.getValueAt(row, 1));
						allReadBook.remove(m.getValueAt(row, 0));
					} else
						return;
				}
				rp = (int) getRows("select r_reading from rental where r_no = ?", m.getValueAt(t.getSelectedRow(), 1))
						.get(0).get(0);
				ap = (int) getRows("select b_page from book where b_name = ?", m.getValueAt(t.getSelectedRow(), 0))
						.get(0).get(0);
				imglbl.setText(rp + "/" + ap);
				r_book = t.getValueAt(t.getSelectedRow(), 0).toString();
				System.out.println(allReadBook);
				img = getBlob(getRows("select b_img from book where b_name = ?", m.getValueAt(t.getSelectedRow(), 0))
						.get(0).get(0), jl.getWidth(), jl.getHeight());
				jl.setIcon(img);
				repaint();
				revalidate();
				btn.setEnabled(true);
			}
		});
		btn.setEnabled(false);
		btn.setOpaque(false);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (flag) {
					eMsg("책 읽기를 멈춘 후 선택하세요.");
					setDefaultCloseOperation(JFrame.ERROR);
				}
			}
		});
		setVisible(true);
	}

	void addRow() {
		m.setRowCount(0);

		var rs = getRows(
				"select b_name, r_no from rental r, book b where u_no = ? and datediff(curdate(),r_returnday) <=0 and b.b_no = r.b_no",
				u_no);
		for(var r : rs) {
			m.addRow(r.toArray());
		}
	}
	
	public static void main(String[] args) {
		new Reading();
	}
}

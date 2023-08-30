import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Read extends BaseFrame {
	ArrayList<String> rentalBook = new ArrayList<String>(), allReadBook = new ArrayList<String>();
	JLabel img;
	DefaultTableModel m = model("도서명,r_no,read,b_no".split(","));
	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
		public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			var comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (table.getModel().getValueAt(row, 2).toString().equals("O")) {
				comp.setForeground(Color.blue);
			} else {
				comp.setForeground(Color.black);
			}
			return comp;
		};
	};
	JTable t = table(m);
	JButton btn;
	Thread th;
	int StartPage, FinalPage, readingPage;
	JScrollPane jsp;
	JProgressBar bar;
	boolean flag;
	String r_book;

	public Read() {
		super("책읽기", 600, 350);

		for (int i = 0; i < m.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		add(c = new JPanel(new BorderLayout(10, 5)));
		c.add(sz(img = new JLabel(), 200, 250), "West");
		c.add(cc = new JPanel(new BorderLayout(15, 15)));
		add(s = new JPanel(new BorderLayout()), "South");

		img.setBorder(new LineBorder(Color.black));

		cc.add(jsp = new JScrollPane(t));

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if(flag) {
						eMsg("책 읽기를 멈춘 후 선택하세요.");
						
//						t.changeSelection(readingPage, 0, false, false);
						return;
					}
					
					if (t.getValueAt(t.getSelectedRow(), 2).equals("O")) {
						if (JOptionPane.showConfirmDialog(null, "읽기를 완료한 도서입니다.\n다시 읽으시겠습니까?", "질문",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							execute("update rental set r_reading = 0 where r_no = ?",
									m.getValueAt(t.getSelectedRow(), 1));
							addRow();
						}
					}
					btn.setEnabled(true);
					StartPage = (int) getRows("select r_reading from rental where r_no = ?",
							m.getValueAt(t.getSelectedRow(), 1)).get(0).get(0);
					FinalPage = (int) getRows("select b_page from book where b_name = ?",
							m.getValueAt(t.getSelectedRow(), 0)).get(0).get(0);
					bar.setString(StartPage + " / " + FinalPage);
					img.setIcon(getIcon("datafiles/book/" + t.getValueAt(t.getSelectedRow(), 3) + ".jpg", 200, 250));
					r_book = t.getValueAt(t.getSelectedRow(), 0).toString();
					bar.setValue((int) (1.0 * StartPage / FinalPage * 100));
					System.out.println((int) (1.0 * StartPage / FinalPage * 100));
					bar.setVisible(true);
					repaint();
					revalidate();
				}
			}
		});

		s.add(bar = sz(new JProgressBar(0, 100), 0, 30));
		this.setVisible(true);
		bar.setBorder(new LineBorder(Color.black));
		bar.setStringPainted(true);
		bar.setForeground(Color.green);
		bar.setUI(new BasicProgressBarUI() {
			@Override
			protected Color getSelectionBackground() {
				return Color.black;
			}

			@Override
			protected Color getSelectionForeground() {
				return Color.black;
			}
		});

		s.add(btn = btn("읽기", a -> {
			if(a.getActionCommand().equals("읽기")) {
				if(th != null) {
					flag= true;
					th.resume();
				}else {
					setSize(600, 400);
					th = new MyThread();
					th.start();
				}
				btn.setText("그만읽기");
			}else {
				execute("update rental set r_reading = ? where r_no = ?", readingPage, m.getValueAt(t.getSelectedRow(), 1));
				flag = false;
				btn.setText("읽기");
			}
		}), "South");

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (flag) {
					eMsg("책 읽기를 멈춘 후 선택하세요.");
				}else {
					dispose();
				}
			}
		});

		bar.setVisible(false);
		btn.setEnabled(false);
		t.getColumnModel().getColumn(1).setMinWidth(0);
		t.getColumnModel().getColumn(1).setMaxWidth(0);
		t.getColumnModel().getColumn(2).setMinWidth(0);
		t.getColumnModel().getColumn(2).setMaxWidth(0);
		t.getColumnModel().getColumn(3).setMinWidth(0);
		t.getColumnModel().getColumn(3).setMaxWidth(0);

		addRow();
		s.setBorder(new EmptyBorder(10, 0, 0, 0));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 10, 10, 10));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);					
		setVisible(true);
	}

	void addRow() {
		m.setRowCount(0);

		var rs = getRows(
				"select b.b_name, r.r_no, if(b.b_page <= r.r_reading,'O','X'),b.b_no from rental r, book b where r.b_no = b.b_no and r.u_no = ? and r_returnday = '0000-00-00'",
				u_no);
		System.out.println(rs);
		for (var r : rs) {
			m.addRow(r.toArray());
		}
	}
	
	class MyThread extends Thread{
		@Override
		public void run() {
			flag = true;
			bar.setString(StartPage + " / " + FinalPage);
			while(true) {
				try {
					Thread.sleep(1000);
					StartPage++;
					bar.setString(StartPage + " / " + FinalPage);
					bar.setValue((int) (1.0 * StartPage / FinalPage * 100));
					if(StartPage == FinalPage) {
						iMsg("책을 다 읽었습니다.");
						flag = false;
						setSize(600, 350);
						bar.setVisible(false);
						btn.setText("읽기");
						btn.setEnabled(false);
						execute("update rental set r_reading = ? where r_no = ?", FinalPage, t.getValueAt(t.getSelectedRow(), 1));
						addRow();
					}
					repaint();
					revalidate();
				} catch (Exception e) {
					e.printStackTrace();
					th = null;
					flag = false;
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		u_no = "1";
		new Read();
	}
	
}

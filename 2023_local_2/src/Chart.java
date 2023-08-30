
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class Chart extends BaseFrame {
	JPanel chart;
	Color color[] = { Color.red, Color.orange, Color.yellow, Color.green, Color.blue };

	public Chart() {
		super("통계", 525, 470);

		add(lbl("인기 순위", JLabel.CENTER, 25), "North");

		var rs = getRows(
				"select count(*), b.b_name, b.b_no from rental r, book b where r.b_no = b.b_no group by b.b_no order by count(*) desc, b.b_no limit 5");
		add(chart = new JPanel() {
			@Override
			public void paint(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				int max = rs.stream().mapToInt(a -> cint(a.get(0))).max().getAsInt();

				for (int i = 0; i < 5; i++) {
					var h = (int) (cint(rs.get(i).get(0)) / (double) max * 250);
					g2d.setColor(color[i]);

					g2d.fillRect(50 + i * 90, 300 - h, 50, h);
					g2d.setColor(Color.black);
					g2d.drawRect(50 + i * 90, 300 - h, 50, h);
					g2d.setColor(Color.BLACK);
					g2d.setFont(new Font("맑은 고딕", 1, 13));
					g2d.drawString(rs.get(i).get(0) + "건", 60 + i * 90, 290 - h);
				}
			}
		});
		
		add(s = new JPanel(new FlowLayout(1, 40, 0)),"South");
		
		for (int i = 0; i < 5; i++) {
			JTextArea jtea = new JTextArea();
			jtea.setText(rs.get(i).get(1) + "");
			jtea.setLineWrap(true);
			jtea.setEditable(false);
			sz(jtea, 50, 90);
			s.add(jtea);
		}
		
		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart();
	}
}
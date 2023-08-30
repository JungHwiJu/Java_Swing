package 지방_3과제;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Chart extends BaseFrame {

	public Chart() {
		super("연령별 분석", 490, 550);

		add(lbl("연령별 분석", JLabel.CENTER, 45, f1), "North");
		add(c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				var rs = getRows(
						"select count(*)/(select count(*) from boarding) from reservation r, boarding b where b.r_no = r.r_no group by b.agegroup");

				int h = 250, w = 60;
				int baseX = 100, baseY = 80;
				for (int i = 0; i < 3; i++) {
					int dh = (int) (h * Double.parseDouble(rs.get(i).get(0).toString()));
					g2d.setColor(Color.LIGHT_GRAY);
					g2d.fillRect(baseX, baseY, w, h);
					g2d.setColor(Color.LIGHT_GRAY.darker());
					g2d.fillOval(baseX, baseY - 20 / 2, w, 20);
					g2d.setColor(Color.BLUE.brighter());
					g2d.fillRect(baseX, baseY + h - dh, w, dh);
					g2d.fillOval(baseX, baseY + h - 20 / 2, w, 20);
					g2d.setColor(Color.BLUE.darker());
					g2d.fillOval(baseX, baseY + h - dh - 20 / 2, w, 20);
					g2d.setColor(Color.WHITE);
					g2d.setFont(new Font(f2, 1, 17));
					g2d.drawString(String.format("%.1f", Double.parseDouble(rs.get(i).get(0).toString()) * 100) + "%",
							baseX + 8, 130);
					g2d.setColor(Color.black);
					g2d.setFont(new Font(f1, 1, 18));
					g2d.drawString("성인", 110, 360);
					g2d.drawString("소아", 210, 360);
					g2d.drawString("유아", 310, 360);
					baseX += 100;
				}
			}
		});
		((JPanel)getContentPane()).setBorder(new EmptyBorder(5,5,5,5));
	}
	public static void main(String[] args) {
		new Chart();
	}
}

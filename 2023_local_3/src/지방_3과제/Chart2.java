package 지방_3과제;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class Chart2 extends BaseFrame {

	public Chart2() {
		super("연령별 분석", 350, 500);

		add(lbl("연령별 분석", 0, 35), "North");
		add(c = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				var rs = getRows(
						"SELECT count(1) / (select count(1) from boarding) FROM 2023지방_3.boarding b, reservation r where b.r_no=r.r_no group by agegroup;");

				int h = 150, w = 50;
				int baseX = 50, baseY = 50;
				for (int i = 0; i < 3; i++) {
					int dh = (int) (h * Double.parseDouble(rs.get(i).get(0).toString()));
					g2d.setColor(Color.LIGHT_GRAY);
					g2d.fillRect(baseX, baseY - 10, w, h);
					g2d.setColor(Color.LIGHT_GRAY.darker());
					g2d.fillOval(baseX, baseY - 40 / 2, w, 20);
					g2d.setColor(Color.BLUE);
					g2d.fillRect(baseX, baseY + h - dh - 10, w, dh);
					g2d.fillOval(baseX, baseY + h - 40 / 2, w, 20);
					g2d.setColor(Color.BLUE.darker());
					g2d.fillOval(baseX, baseY + h - dh - 40 / 2, w, 20);
					g2d.setColor(Color.WHITE);
					g2d.drawString(String.format("%.1f", Double.parseDouble(rs.get(i).get(0).toString()) * 100) + "%",
							baseX + 10, 70);
					baseX += 80;
				}
			}
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart2();
	}

}

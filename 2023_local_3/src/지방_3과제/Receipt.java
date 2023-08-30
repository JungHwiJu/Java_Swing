package 지방_3과제;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.LocalTime;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Receipt extends BaseFrame {

	public Receipt() {
		super("영수증", 650, ReceiptList.size() * 300 + 30);

		add(c = new JPanel(new GridLayout(0, 1)));
		c.setOpaque(false);

		for (var r : ReceiptList) {
			var tmp = new JPanel(new BorderLayout());
			var rs = getRows(
					"select concat(n1.n_name,'(',n1.code,')',' → ', n2.n_name, '(',n2.code,')') from nation n1, nation n2 where n1.n_name like ? and n2.n_name like ?",
					"%" + depart + "%", "%" + arrive + "%");
			tmp.add(new JLabel() {
				@Override
				protected void paintComponent(Graphics g) {
					var g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					try {
						var img = ImageIO.read(new File("datafiles/티켓.jpg"));
						g2d.drawImage(img, 0, 0 + ReceiptList.size(), 550, 250, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					g2d.setFont(new Font(f1, 1, 25));
					g2d.drawString(rs.get(0).get(0).toString(), 20, 90 * ReceiptList.size() / peopleNameList.size());
					g2d.setFont(new Font(f1, 1, 20));
					g2d.drawString(
							peopleNameList.get(0) + " (" + peopleRating.get(0) + ") - " + peopleResultList.get(0), 20,
							160 * ReceiptList.size() / peopleNameList.size());
					g2d.setFont(new Font(f1, 1, 13));
					g2d.drawString("생년월일 : " + peopleRating.get(0), 20,
							190 * ReceiptList.size() / peopleNameList.size());
					LocalTime time = LocalTime.now();
					g2d.drawString(date + " / " + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond(), 20,
							230 * ReceiptList.size() / peopleNameList.size());
				}
			});
			tmp.setOpaque(false);
			c.add(tmp);
		}
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				new Main().login();
			}
		});
		
		((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 25, 15, 25));
	}
}
package 지방_3과제;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class World extends BaseFrame {

	JComboBox<String> com;
	JLabel lblMapImg;

	static final int IMG_WIDTH = 900;
	static final int IMG_HEIGHT = 530;

	ArrayList<ArrayList<Object>> 대륙별로예약인원수쿼리 = getRows(
			"SELECT sum(b.agegroup) FROM continent c, nation n, schedule s, reservation r, boarding b\r\n"
					+ "where 1=1\r\n" + "and n.c_no = c.c_no\r\n" + "and (s.depart = n.n_no\r\n"
					+ "or s.arrival = n.n_no)\r\n" + "and r.s_no = s.s_no\r\n" + "and b.r_no = r.r_no\r\n"
					+ "group by c.c_no\r\n" + ";");

	public World() {
		super("대륙별 분석", 900, 600);

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel(null));

		n.add(lbl("대륙", 0, 15));
		n.add(com = new JComboBox<String>());
		c.add(lblMapImg = new JLabel(getIcon("datafiles/지도/0.jpg", IMG_WIDTH, IMG_HEIGHT))).setBounds(0, 0, IMG_WIDTH,
				IMG_HEIGHT);

		com.addItem("세계");
		for (var r : getRows("select c_name from continent")) {
			com.addItem(r.get(0).toString());
		}

		com.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				chgImg(com.getSelectedIndex());
			}
		});

		chgImg(0);
		n.setOpaque(false);
		setVisible(true);
	}

	void chgImg(int c_no) {
		c.removeAll();

		c.add(lblMapImg = new JLabel(getIcon("datafiles/지도/0.jpg", IMG_WIDTH, IMG_HEIGHT))).setBounds(0, 0, IMG_WIDTH,
				IMG_HEIGHT);
		if (c_no == 0) {
			lblMapImg.setIcon(getIcon("datafiles/지도/0.jpg", IMG_WIDTH, IMG_HEIGHT));

			for (var r : getRows("select c_no, x, y from continent")) {
				var redCircle = new JLabel() {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.setColor(Color.red);
						g.fillOval(0, 0, toInt(대륙별로예약인원수쿼리.get(toInt(r.get(0)) - 1).get(0)) / 800,
								toInt(대륙별로예약인원수쿼리.get(toInt(r.get(0)) - 1).get(0)) / 800);
					}
				};

				c.add(redCircle).setBounds(toInt(r.get(1)) - 30, toInt(r.get(2)) - 30, 100, 100);
				c.setComponentZOrder(redCircle, 0);
			}

		} else {
			lblMapImg.setIcon(getIcon("datafiles/지도/" + c_no + ".jpg", IMG_WIDTH, IMG_HEIGHT));

			for (var r : getRows("select x, y from nation where c_no=?", c_no)) {
				var redCircle = new JLabel() {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.setColor(Color.red);
						g.fillOval(0, 0, 30, 30);
					}
				};

				c.add(redCircle).setBounds(toInt(r.get(0)) - 30, toInt(r.get(1)) - 30, 100, 100);
				c.setComponentZOrder(redCircle, 0);
			}
		}

		c.repaint();
		c.revalidate();
	}

	public static void main(String[] args) {
		new World();
	}

}

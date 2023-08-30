package 지방_3과제;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Seat_2 extends BaseFrame {
	/* scroll 용 */
	JScrollPane jsp;
	ArrayList<SeatRes> items = new ArrayList<>();
	/* seat map */
	HashMap<String, JLabel> seatMap = new HashMap<>();
	SeatRes spas; /* selected passenger */
	ArrayList<String> nameList = new ArrayList<>();
	ArrayList<String> seatList = new ArrayList<>();

	public Seat_2() {
		super("좌석선택", 900, 600);
		add(jsp = new JScrollPane(c = new JPanel(new BorderLayout(25, 25))));
		add(e = sz(new JPanel(new BorderLayout()), 250, 0), "East");
		BoxLayout box = new BoxLayout(c, BoxLayout.Y_AXIS);
		c.setLayout(box);
		var seatLabels = "\0,  A,B  ,\0,C,\0,  D,E  ,\0".split(",");
		var seatLabelAligns = new int[] { 0, JLabel.LEFT, JLabel.RIGHT, 0, JLabel.CENTER, 0, JLabel.LEFT, JLabel.RIGHT,
				0 };
		/* 1단계 상단 라벨 포함 된 column 1 2 3 4,.... */
		for (int i = 0; i < 11; i++) {
			/* 각 row 의 Panel를 넣음 */
			JPanel rowPanel = new JPanel(new FlowLayout()); /* center layout */
			for (int j = 0; j < 9; j++) {
				/* 첫번째 열일 경우 공백 그 외 번호 (i) */
				if (j == 0 || j == 8) {
					var lbl = lbl(i + "", j == 0 ? JLabel.LEFT : JLabel.RIGHT, 20);
					rowPanel.add(i == 0 ? Box.createHorizontalStrut(20) : sz(lbl, 30, 20));
					continue;
				}
				/* 복도 */
				if (j == 3 || j == 5) {
					rowPanel.add(Box.createHorizontalStrut(60));
					continue;
				}

				if (i == 0) { /* 상단 라벨 열 일때 */
					rowPanel.add(sz(lbl(seatLabels[j], seatLabelAligns[j], 20), 70, 20));
				} else { /* 제외한 item 일때 */
					String key = String.format("%s%02d", seatLabels[j].trim(), i);
					JLabel lbl = lbl(key, JLabel.CENTER, 20);
					lbl.setBorder(new LineBorder(Color.BLACK));
					rowPanel.add(sz(lbl, 70, 70));
					seatMap.put(key, lbl);
					lbl.setOpaque(true);
					lbl.addMouseListener(new MouseAdapter() {
						/* 로직부라서 굳이 고치지 않음 */
						@Override
						public void mousePressed(MouseEvent e) {
							var me = (JLabel) e.getSource();
							/* 이미 예약 되었을 때 */
							if (me.getBackground().equals(Color.LIGHT_GRAY)) {
								emsg(me.getText().toUpperCase() + "좌석은 선택이 불가능합니다.");
								return;
							}
							/* dependancy */
							if (me.getBackground().equals(Color.red)) {
								me.setBackground(Color.white);
								var dependancy = items.stream().filter(a -> a.passSeat.equals(key)).findAny().get();
								dependancy.passSeat = "";
								dependancy.update();
							} else { /* 선택 시 */
								var try_get = seatMap.get(spas.passSeat);
								if (try_get != null)
									try_get.setBackground(Color.WHITE);
								spas.passSeat = key;
								spas.update();
								me.setBackground(Color.red);
							}
						}
					});
				}
			}
			/* 모든 변경이 끝난 후 add */
			c.add(rowPanel);
		}

		for (var rs : getRows(
				"select seat from boarding b, reservation r, schedule s where b.r_no = r.r_no and r.s_no = s.s_no and date = ? and s.s_no = ?",
				date, s_no)) {
			seatMap.get(rs.get(0) + "").setBackground(Color.lightGray);
		}

		e.add(lbl("총 " + peopleNameList.size() + "명", 2, 1, 15), "North");
		e.add(ec = new JPanel());
		ec.setLayout(new BoxLayout(ec, BoxLayout.PAGE_AXIS));
		
		if (peoplePriceList.isEmpty()) {
			for (int i = 0; i < peopleNameList.size(); i++) {
				var pas = new SeatRes(peopleNameList.get(i), peopleResultList.get(i));
				items.add(pas);
				ec.add(pas);
				ec.add(Box.createVerticalStrut(20));
			}
		}else {			
			for (int i = 0; i < peopleNameList.size(); i++) {
				var pas = new SeatRes(peopleNameList.get(i));
				items.add(pas);
				ec.add(pas);
				ec.add(Box.createVerticalStrut(20));
			}
		}

		spas = items.get(0);
		spas.select();

		e.add(btn("선택완료", a -> {
			if (peoplePriceList.isEmpty()) {
				for (int i = 0; i < items.size(); i++) {
					exectue("update boarding set seat = ? where b_name = ?", items.get(i).passSeat,
							items.get(i).passName);
				}
				dispose();
			} else {
				if (items.size() != items.stream().filter(x -> !x.passSeat.isEmpty()).count()) {
					emsg("좌석을 모두 선택해주세요.");
					return;
				}
				for (int k = 0; k < items.size(); k++) {
					peopleResultList.add(items.get(k).passSeat);
				}
				new Purchase().addWindowListener(new Before(Seat_2.this));

			}
		}), "South");

		c.setBackground(Color.white);
		e.setBorder(new EmptyBorder(10, 10, 10, 10));
		c.setOpaque(true);
		setVisible(true);
	}

	public Seat_2(int s_no, int r_no) {
		this();
		System.out.println(peopleNameList);
		setTitle("좌석변경");
		for (var rs : getRows("select seat from boarding b inner join reservation r on b.r_no = r.r_no and r.s_no = ?",
				s_no)) {
			for (int j = 0; j < peopleNameList.size(); j++) {
				if (rs.get(0)
						.equals(getRows("select seat from boarding where r_no = ?", r_no).get(j).get(0).toString())) {
					seatMap.get(rs.get(0).toString()).setBackground(Color.red);
				} else {
					if (seatMap.get(rs.get(0).toString()).getBackground().equals(Color.red)) {
						seatMap.get(rs.get(0).toString()).setBackground(Color.red);
					} else
						seatMap.get(rs.get(0).toString()).setBackground(Color.LIGHT_GRAY);
				}
			}
		}
		items.get(0).select();
	}

	/* Seat Reservation Information */
	class SeatRes extends JLabel {
		public String passName;
		public String passSeat = "";

		Border defaultBorder = new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(10, 10, 10, 10));
		Border selectedBorder = new CompoundBorder(new LineBorder(Color.blue), new EmptyBorder(10, 10, 10, 10));

		public SeatRes(String name) {
			passName = name;
			/* ui */
			setMaximumSize(new Dimension(250, 35));
			setFont(new Font(f1, 0, 13));
			setBorder(defaultBorder);
			setOpaque(true);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					select();
				}
			});
			update();
		}

		public SeatRes(String name, String seat) {
			passName = name;
			passSeat = seat;

			setMaximumSize(new Dimension(250, 35));
			setFont(new Font(f1, 0, 13));
			setBorder(defaultBorder);
			setOpaque(true);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					select();
				}
			});
			update();
		}

		void update() {
			setText(passName + " - " + passSeat);
		}

		void select() {
			spas = this;
			for (var it : items) {
				it.setBorder(defaultBorder);
			}
			setBorder(selectedBorder);
		}

	}

}
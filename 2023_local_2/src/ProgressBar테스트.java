import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class ProgressBar테스트 {
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.setSize(500, 500);
		
		//프로그래스바 진행상황 표시
		JProgressBar pb = new JProgressBar(0, 100);
		pb.setPreferredSize(new Dimension(0, 50));
		
		pb.setForeground(Color.BLACK);
		pb.setBackground(Color.ORANGE);
		pb.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		pb.setStringPainted(true);
		
		BasicProgressBarUI ui = new BasicProgressBarUI() {
			@Override
			protected Color getSelectionBackground() {
				return Color.GREEN;
			}
			
			@Override
			protected Color getSelectionForeground() {
				return Color.RED;
			}
		};
		pb.setUI(ui);
	
		
		//테스트
		jf.add(pb, "North");
		jf.setVisible(true);
		
		for(int i=0; i<=100; i++) {
			pb.setValue(i);
			pb.setString(i + "/" + 100);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
}

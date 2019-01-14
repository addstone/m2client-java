
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jootnet.m2client.map.Map;
import org.jootnet.m2client.map.internal.Maps;
import org.jootnet.m2client.ui.GamePanel;

public class Main {
	
	public static void main(String[] args) throws IOException {
		System.setProperty("org.jootnet.m2client.data.dir", "D:\\Program Files (x86)\\盛大游戏\\Legend of mir\\data");
		System.setProperty("org.jootnet.m2client.map.dir", "D:\\Program Files (x86)\\盛大游戏\\Legend of mir\\Map");
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("测试地图渲染");
		frame.setSize(1366, 768);
		frame.setLocationRelativeTo(null);
		//long startT = System.currentTimeMillis();
		Map map = Maps.get("bsr02", "不知道");
		//long endT = System.currentTimeMillis();
		//System.out.println("map load use " + (endT - startT) + " ms");
		map.move(205, 194);
		JPanel gamePanel = new GamePanel(map);
		gamePanel.setSize(1280, 720);
		gamePanel.setLocation(0, 0);
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
					map.move(map.roleX() + 1, map.roleY());
				} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
					map.move(map.roleX() - 1, map.roleY());
				} else if(e.getKeyCode() == KeyEvent.VK_UP) {
					map.move(map.roleX(), map.roleY() - 1);
				} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					map.move(map.roleX(), map.roleY() + 1);
				}
			}
		});
		frame.setLayout(null);
		frame.add(gamePanel);
		frame.setVisible(true);
	}

}

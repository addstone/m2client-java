import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.jootnet.m2client.map.Map;
import org.jootnet.m2client.map.internal.Maps;
import org.jootnet.m2client.texture.internal.Textures;
import org.jootnet.m2client.ui.GamePanel;

public class Main {

	public static void main(String[] args) throws IOException {
		System.setProperty("org.jootnet.m2client.data.dir", "D:\\Program Files (x86)\\ʢ������\\��Ѫ����\\Data");
		System.setProperty("org.jootnet.m2client.map.dir", "D:\\Program Files (x86)\\ʢ������\\��Ѫ����\\Map");
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("���Ե�ͼ��Ⱦ");
		//frame.setLocationRelativeTo(null);
		frame.setSize(1280, 720);
		Map map = Maps.get("0");
		map.move(330, 330);
		frame.setContentPane(new GamePanel(map));
		frame.setVisible(true);
		//ImageIO.write(Textures.getTextureImmediately("npc", 1992).toBufferedImage(true), "jpg", new File("C:\\Users\\��\\Desktop\\1.jpg"));
	}

}

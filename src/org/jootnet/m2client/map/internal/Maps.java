package org.jootnet.m2client.map.internal;

import java.io.File;
import java.util.HashMap;

import org.jootnet.m2client.map.Map;
import org.jootnet.m2client.util.BinaryReader;

/**
 * ��ͼ������<br>
 * ��ͼ�ļ�ͷ��Delphi������������<br>
 * <pre>
 * TMapHeader = packed record
    wWidth      :Word;                 	//���			2
    wHeight     :Word;                 	//�߶�			2
    sTitle      :String[15]; 			//����			16
    UpdateDate  :TDateTime;          	//��������			8
    VerFlag     :Byte;					//��ʶ(�µĸ�ʽΪ02)	1
    Reserved    :array[0..22] of Char;  //����			23
  end;
 * </pre>
 * ʮ����֮��İ汾���ܳ����°汾��ͼ��3KM2����20110428����Ķ��°��ͼ��֧��<br>
 * ���������ʳ�����֮����ʱ����֪���°��ͼ����������ֽ��Ǹ����õ�
 * 
 * @author johness
 */
public final class Maps {

	private static java.util.Map<String, Map> maps = new HashMap<String, Map>();
	private static Object map_locker = new Object();
	
	static class MapImpl extends Map {

		MapImpl(String name, MapInfo info) {
			super(name, info);
		}
		
	}
	
	/**
	 * ��ȡһ����ͼ����
	 * 
	 * @param mapNo
	 * 		��ͼ���<br>
	 * 		����ͼ�ļ�����
	 * @return ���������ĵ�ͼ����
	 */
	public static final Map get(String mapNo) {
		synchronized (map_locker) {
			if(maps.containsKey(mapNo))
				return maps.get(mapNo);
			try{
				String mapDir = System.getProperty("org.jootnet.m2client.map.dir", System.getProperty("user.dir"));
				String mapPath = mapDir;
				if(!mapPath.endsWith(File.separator))
					mapPath += File.separator;
				mapPath += mapNo + ".map";
				BinaryReader br_map = new BinaryReader(new File(mapPath), "r");
				MapInfo mapInfo = new MapInfo();
				mapInfo.setWidth(br_map.readShortLE());
				mapInfo.setHeight(br_map.readShortLE());
				br_map.skipBytes(24);
				boolean newMapFlag = br_map.readByte() == 2; // �°��ͼÿһ��Tileռ��14���ֽڣ����������ֽ�����δ֪
				br_map.skipBytes(23);
				MapTileInfo[][] mapTileInfos = new MapTileInfo[mapInfo.getWidth()][mapInfo.getHeight()];
				for (int width = 0; width < mapInfo.getWidth(); ++width)
					for (int height = 0; height < mapInfo.getHeight(); ++height) {
						MapTileInfo mi = new MapTileInfo();
						// ��ȡ����
						short bng = br_map.readShortLE();
						// ��ȡ�м��
						short mid = br_map.readShortLE();
						// ��ȡ�����
						short obj = br_map.readShortLE();
						// ���ñ���
						if((bng & 0x7fff) > 0) {
							mi.setBngImgIdx((short) ((bng & 0x7fff) - 1));
							mi.setHasBng(true);
						}
						// �����м��
						if((mid & 0x7fff) > 0) {
							mi.setMidImgIdx((short) ((mid & 0x7fff) - 1));
							mi.setHasMid(true);
						}
						// ���ö����
						if((obj & 0x7fff) > 0) {
							mi.setObjImgIdx((short) ((obj & 0x7fff) - 1));
							mi.setHasObj(true);
						}
						// �����Ƿ��վ��
						mi.setCanWalk((bng & 0x8000) != 0x8000 && (obj & 0x8000) != 0x8000);
						// �����Ƿ�ɷ���
						mi.setCanFly((obj & 0x8000) != 0x8000);
						
						// ��ȡ������(��7��byte)
						byte btTmp = br_map.readByte();
						if((btTmp & 0x80) == 0x80) {
							mi.setDoorIdx((byte) (btTmp & 0x7F));
							mi.setHasDoor(true);
						}
						// ��ȡ��ƫ��(��8��byte)
						btTmp = br_map.readByte();
						mi.setDoorOffset(btTmp);
						if((btTmp & 0x80) == 0x80) mi.setDoorOpen(true);
						// ��ȡ����֡��(��9��byte)
						btTmp = br_map.readByte();
						mi.setAniFrame(btTmp);
						if((btTmp & 0x80) == 0x80) {
							mi.setAniFrame((byte) (btTmp & 0x7F));
							mi.setHasAni(true);
						}
						// ��ȡ�����ö�����֡��(��10��byte)
						mi.setAniTick(br_map.readByte());
						// ��ȡ��Դ�ļ�����(��11��byte)
						mi.setObjFileIdx(br_map.readByte());
						// ��ȡ����(��12��byte)
						mi.setLight(br_map.readByte());
						if(newMapFlag)
							br_map.skipBytes(2);
						if (width % 2 != 0 || height % 2 != 0)
							mi.setHasBng(false);
						mapTileInfos[width][height] = mi;
					}
				mapInfo.setMapTiles(mapTileInfos);
				br_map.close();
				Map ret = new MapImpl(mapNo, mapInfo);
				maps.put(mapNo, ret);
				return ret;
			}catch(Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * �ӻ�����ϵͳ�ĵ�ͼ�������Ƴ��ض���ŵĵ�ͼ
	 * 
	 * @param mapNo
	 * 		��ͼ���
	 */
	public static final void remove(String mapNo) {
		synchronized (map_locker) {
			if(maps.containsKey(mapNo))
				maps.remove(mapNo);
		}
	}
}

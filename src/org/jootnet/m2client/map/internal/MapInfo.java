package org.jootnet.m2client.map.internal;

/**
 * ��Ѫ����2��ͼ
 * <br>
 * ����ͼ�ļ�(*.map)��Java�����ݽṹ������
 * <br>
 * ��һ��MapHeader��һ��MapTile��ά����
 * <br>
 * ��ʵ���ϲ�ʹ��MapHeader��MapTile����ΪMapHeader��MapTile����̫ɢ��������ʹ��
 * <br>
 * ���ǽ�MapHeader�йؼ���ͼ��Ϣ��ȡ�����ŵ�Map���MapTile���½���Ϊ{@link MapTileInfo}�Է�������߼�
 * 
 * @author johness
 */
public final class MapInfo {
	
	/** ��ͼ��� */
	private short width;
	/** ��ͼ�߶� */
	private short height;
	/** ��ͼ������ */
	private MapTileInfo[][] tiles;
	
	MapInfo() { }
	
	/** ��ȡ��ͼ��� */
	public short getWidth() {
		return width;
	}
	/** ���õ�ͼ��� */
	void setWidth(short width) {
		this.width = width;
	}
	/** ��ȡ��ͼ�߶� */
	public short getHeight() {
		return height;
	}
	/** ���õ�ͼ�߶� */
	void setHeight(short height) {
		this.height = height;
	}
	/** ��ȡ��ͼ����Ϣ */
	public MapTileInfo[][] getTiles() {
		return tiles;
	}
	/** ���õ�ͼ����Ϣ */
	void setMapTiles(MapTileInfo[][] mapTiles) {
		this.tiles = mapTiles;
	}
}

package org.jootnet.m2client.texture.internal;

/**
 * ��Ѫ����2ͼƬ��Ϣ
 * 
 * @author johness
 */
final class ImageInfo {

	public static final ImageInfo EMPTY = new ImageInfo();
	
	ImageInfo() { }
	
	private short width;
	private short height;
	private short offsetX;
	private short offsetY;
	private byte colorBit = 8;
	
	/**
	 * ��ȡͼƬɫ��<br>
	 * ��ȡֵΪ
	 * <ul>
	 * <li>1:�ڰ�������ɫ</li>
	 * <li>8:256����ɫ</li>
	 * <li>16:65536����ɫ</li>
	 * <li>24:16777215����ɫ</li>
	 * <li>32:4294967295����ɫ</li>
	 * </ul>
	 * 
	 * @return
	 */
	public byte getColorBit() {
		return colorBit;
	}
	void setColorBit(byte colorBit) {
		this.colorBit = colorBit;
	}
	
	/**
	 * ��ȡͼƬ���
	 * 
	 * @return ͼƬ���,��λΪ����
	 */
	public short getWidth() {
		return width;
	}
	void setWidth(short width) {
		this.width = width;
	}

	/**
	 * ��ȡͼƬ�߶�
	 * 
	 * @return ͼƬ�߶�,��λΪ����
	 */
	public short getHeight() {
		return height;
	}
	void setHeight(short height) {
		this.height = height;
	}

	/**
	 * ��ȡͼƬ����ƫ����
	 * 
	 * @return ͼƬ����ƫ����,��λΪ����
	 */
	public short getOffsetX() {
		return offsetX;
	}
	void setOffsetX(short offsetX) {
		this.offsetX = offsetX;
	}

	/**
	 * ��ȡͼƬ����ƫ����
	 * 
	 * @return ͼƬ����ƫ����,��λΪ����
	 */
	public short getOffsetY() {
		return offsetY;
	}
	void setOffsetY(short offsetY) {
		this.offsetY = offsetY;
	}
}

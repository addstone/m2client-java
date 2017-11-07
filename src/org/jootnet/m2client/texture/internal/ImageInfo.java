package org.jootnet.m2client.texture.internal;

/**
 * 热血传奇2图片信息
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
	 * 获取图片色深<br>
	 * 可取值为
	 * <ul>
	 * <li>1:黑白两种颜色</li>
	 * <li>8:256种颜色</li>
	 * <li>16:65536种颜色</li>
	 * <li>24:16777215种颜色</li>
	 * <li>32:4294967295种颜色</li>
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
	 * 获取图片宽度
	 * 
	 * @return 图片宽度,单位为像素
	 */
	public short getWidth() {
		return width;
	}
	void setWidth(short width) {
		this.width = width;
	}

	/**
	 * 获取图片高度
	 * 
	 * @return 图片高度,单位为像素
	 */
	public short getHeight() {
		return height;
	}
	void setHeight(short height) {
		this.height = height;
	}

	/**
	 * 获取图片横向偏移量
	 * 
	 * @return 图片横向偏移量,单位为像素
	 */
	public short getOffsetX() {
		return offsetX;
	}
	void setOffsetX(short offsetX) {
		this.offsetX = offsetX;
	}

	/**
	 * 获取图片纵向偏移量
	 * 
	 * @return 图片纵向偏移量,单位为像素
	 */
	public short getOffsetY() {
		return offsetY;
	}
	void setOffsetY(short offsetY) {
		this.offsetY = offsetY;
	}
}

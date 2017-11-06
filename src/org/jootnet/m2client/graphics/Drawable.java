package org.jootnet.m2client.graphics;

import org.jootnet.m2client.texture.Texture;

/**
 * �ɻ��ƶ���
 * 
 * @author ��
 */
public interface Drawable {

	/**
	 * ���»�������
	 * 
	 * @param ctx
	 * 		������Ϣ������
	 * @return
	 * 		��ǰ���������Ƿ���Ҫ���Ƶ���Ԫ����
	 */
	boolean adjust(GraphicsContext ctx);
	
	/**
	 * ���غ���ƫ����
	 * 
	 * @return �����ڸ�Ԫ���еĺ���ƫ����
	 */
	int offsetX();
	
	/**
	 * ��������ƫ����
	 * 
	 * @return �����ڸ�Ԫ���е�����ƫ����
	 */
	int offsetY();
	
	/**
	 * ���ػ�������
	 * 
	 * @return Ҫ��֪����Ԫ���е�����
	 */
	Texture content();
}
















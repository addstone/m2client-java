package org.jootnet.m2client.texture.internal;

import java.io.Closeable;

import org.jootnet.m2client.texture.Texture;

/**
 * ͼƬ��ͨ�ýӿ�
 * 
 * @author johness
 */
interface ImageLibrary extends Closeable {

	/**
	 * ��ȡͼƬ����ͼƬ����
	 * 
	 * @return ͼƬ����
	 */
	int count();
	
	/**
	 * ��ȡͼƬ����ָ��������ͼƬ����
	 * 
	 * @param index
	 * 		ͼƬ����
	 * @return ��ӦͼƬ����
	 */
	Texture tex(int index);
}

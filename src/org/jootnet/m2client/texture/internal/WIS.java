package org.jootnet.m2client.texture.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jootnet.m2client.texture.Texture;
import org.jootnet.m2client.util.BinaryReader;
import org.jootnet.m2client.util.SDK;

/**
 * ��Ѫ����2WISͼƬ��
 * 
 * @author johness
 */
final class WIS implements ImageLibrary {

	private int imageCount;
	/**
	 * ��ȡ����ͼƬ����
	 * 
	 * @return �����ڵ�ǰWIS���е�ͼƬ����
	 */
	int getImageCount() {
		return imageCount;
	}
    /* ͼƬ������ʼλ�� */
    private int[] offsetList;
    /* ͼƬ���ݳ��� */
    private int[] lengthList;
    private ImageInfo[] imageInfos;
    /**
     * ��ȡ����ͼƬ��Ϣ����
     * 
     * @return ���д����ڵ�ǰWIS���е�ͼƬ��Ϣ����
     */
	ImageInfo[] getImageInfos() {
		return imageInfos;
	}
	/* WIS�ļ������ȡ���� */
	private BinaryReader br_wis;
	private boolean loaded;
	/**
	 * ��ȡ�����״̬
	 * 
	 * @return true��ʾ����سɹ� false��ʾ����ʧ��
	 */
	public boolean isLoaded() {
		return loaded;
	}
	/* �ļ�ָ���ȡ�� */
    private Object wis_locker = new Object();
	
    WIS(String wisPath) {
    	File f_wis = new File(wisPath);
		if(!f_wis.exists()) return;
		if(!f_wis.isFile()) return;
		if(!f_wis.canRead()) return;
    	try {
    		br_wis = new BinaryReader(f_wis, "r");
    		// ���ļ�ĩβ��ʼ��ȡͼƬ����������Ϣ
    		// һ��������Ϣ����12���ֽ�(3��intֵ)������ΪͼƬ������ʼλ��(������ļ�)��ͼƬ���ݴ�С(����������Ϣ)������
    		// ʹ������List����offsetList��lengthList
    		List<Integer> offsets = new ArrayList<Integer>();
    		List<Integer> lengths = new ArrayList<Integer>();
    		int readPosition = (int) (br_wis.length() - 12);
    		int currentOffset = 0;
    		int currentLength = 0;
    		do {
    			br_wis.seek(readPosition);
    			readPosition -= 12;
    			
    			currentOffset = br_wis.readIntLE();
    			currentLength = br_wis.readIntLE();
    			
    			offsets.add(currentOffset);
    			lengths.add(currentLength);
    		} while(currentOffset > 512);
    		Collections.reverse(offsets);
    		Collections.reverse(lengths);
    		offsetList = new int[offsets.size()];
    		for(int i = 0; i < offsetList.length; ++i)
    			offsetList[i] = offsets.get(i);
    		lengthList = new int[lengths.size()];
    		for(int i = 0; i < lengthList.length; ++i)
    			lengthList[i] = lengths.get(i);
    		imageCount = offsetList.length;
    		// ��ȡͼƬ��Ϣ
    		imageInfos = new ImageInfo[imageCount];
    		for(int i = 0; i < imageCount; ++i) {
				int offset = offsetList[i];
				if(offset + 12 > br_wis.length()) {
					// ���ݳ���ֱ�Ӹ�ֵΪ��ͼƬ
					imageInfos[i] = ImageInfo.EMPTY;
            		continue;
				}
    			ImageInfo ii = new ImageInfo();
    			br_wis.seek(offsetList[i] + 4);
    			ii.setWidth((short)br_wis.readUnsignedShortLE());
				ii.setHeight((short)br_wis.readUnsignedShortLE());
				ii.setOffsetX(br_wis.readShortLE());
				ii.setOffsetY(br_wis.readShortLE());
                imageInfos[i] = ii;
    		}
            loaded = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
        
    /**
	 * ��ѹ����
	 * @param packed ѹ��������
	 * @param unpackLength ��ѹ�����ݴ�С
	 */
	private static byte[] unpack(byte[] packed, int unpackLength) {
		int srcLength = packed.length; // ѹ�������ݴ�С
		byte[] result = new byte[unpackLength]; // ��ѹ������
		int srcIndex = 0; // ��ǰ��ѹ���ֽ�����
		int dstIndex = 0; // ��ѹ���̻�ԭ�����ֽ�����
		// ��ѹ����Ϊ���ֽڽ���(�ֽ�ӦתΪ1-256)
		// �����ǰ�ֽڷ�0���ʾ������һ���ֽ�������䵱ǰ�ֽڸ��ֽ�λ��
		// �����ǰ�ֽ�Ϊ0����һ���ֽڲ�Ϊ0���ʾ�����¸��ֽڿ�ʼ����һ���ֽڳ��ȶ�û��ѹ����ֱ�Ӹ��Ƶ�Ŀ������
		// �����ǰ�ֽ�Ϊ0����һ���ֽ�ҲΪ0������������ݣ����账��
		// XX YY ��ʾ��YY���XX���ֽ�
		// 00 XX YY ZZ ... ��ʾ��YY��ʼXX���ֽ���δ��ѹ���ģ�ֱ�Ӹ��Ƴ�������
		while(srcLength > 0 && unpackLength > 0) {
			int length = packed[srcIndex++] & 0xff; // ȡ����һ����־λ
			int value = packed[srcIndex++] & 0xff; // ȡ���ڶ�����־λ
			srcLength -= 2;
			/*if(value == 0 && length == 0) {
				// ������
				continue;
			} else */if(length != 0) {
				// ��Ҫ��ѹ��
				unpackLength -= length;
				for(int i = 0; i < length; ++i) {
					result[dstIndex++] = (byte) value;
				}
			} else if(value != 0) {
				srcLength -= value;
				unpackLength -= value;
				System.arraycopy(packed, srcIndex, result, dstIndex, value);
				dstIndex += value;
				srcIndex += value;
			}
		}
		return result;
	}
    
    /**
     * �ر�WIS�����ͷ������õ��ļ����Լ��ڴ�ռ��
     */
	public final void close() throws IOException {
		synchronized (wis_locker) {
			offsetList = null;
			lengthList = null;
            imageInfos = null;
            loaded = false;
			if (br_wis != null)
            {
				br_wis.close();
            }
		}
	}

	public final Texture tex(int index) {
		if(!loaded) return Texture.EMPTY;
		if(index < 0) return Texture.EMPTY;
		if(index >= imageCount) return Texture.EMPTY;
		if(imageInfos[index] == ImageInfo.EMPTY) return Texture.EMPTY;
    	try{
    		ImageInfo ii = imageInfos[index];
    		int offset = offsetList[index];
    		int length = lengthList[index];
    		/*if(length < 14) {
    			// ����ǿհ�ͼƬ
    			return Texture.EMPTY;
    		}*/
    		byte[] imageBytes = new byte[ii.getWidth() * ii.getHeight()];
    		byte[] packed = null;
    		byte encry = 0;
    		synchronized (wis_locker) {
        		// �Ƿ�ѹ��(RLE)
        		br_wis.seek(offset);
        		encry = br_wis.readByte();
        		br_wis.skipBytes(11);
        		if(encry == 1) {
        			// ѹ����
        			packed = new byte[length - 12];
        			br_wis.read(packed);
        		} else {
        			// ûѹ��
        			br_wis.read(imageBytes);
        		}
			}
    		if(encry == 1)
    			imageBytes = unpack(packed, imageBytes.length);
    		byte[] sRGB = new byte[ii.getWidth() * ii.getHeight() * 3];
    		int index1 = 0;
    		for(int h = 0; h < ii.getHeight(); ++h)
    			for(int w = 0; w < ii.getWidth(); ++w) {
    				byte[] pallete = SDK.palletes[imageBytes[index1++] & 0xff];
					int _idx = (w + h * ii.getWidth()) * 3;
					sRGB[_idx] = pallete[1];
					sRGB[_idx + 1] = pallete[2];
					sRGB[_idx + 2] = pallete[3];
    			}
	    	return new Texture(sRGB, ii.getWidth(), ii.getHeight(), ii.getOffsetX(), ii.getOffsetY());
    	} catch(Exception ex) {
    		ex.printStackTrace();
    		return Texture.EMPTY;
    	}
    }

	public int count() {
		return imageCount;
	}

}

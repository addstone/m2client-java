package org.jootnet.m2client.texture.internal;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jootnet.m2client.texture.Texture;
import org.jootnet.m2client.util.BinaryReader;
import org.jootnet.m2client.util.SDK;

/**
 * ��Ѫ����2WILͼƬ��
 * 
 * @author johness
 */
final class WIL implements ImageLibrary {

	/**
	 * �Ƿ�ֻ��WIL�е����ݽ���ͼƬ��������WIX������
	 */
	public static boolean GLOBAL_ONLYWIL_MODE = false;
	
	private int imageCount;
	/**
	 * ��ȡ����ͼƬ����
	 * 
	 * @return �����ڵ�ǰWIL���е�ͼƬ����
	 */
	int getImageCount() {
		return imageCount;
	}
	/* �汾��ʶ */
    private int verFlag;
    /* ͼƬ������ʼλ�� */
    private int[] offsetList;
    private ImageInfo[] imageInfos;
    /**
     * ��ȡ����ͼƬ��Ϣ����
     * 
     * @return ���д����ڵ�ǰWIL���е�ͼƬ��Ϣ����
     */
	ImageInfo[] getImageInfos() {
		return imageInfos;
	}
	/* WIL�ļ������ȡ���� */
	private BinaryReader br_wil;
	private volatile boolean loaded;
	/**
	 * ��ȡ�����״̬
	 * 
	 * @return true��ʾ����سɹ� false��ʾ����ʧ��
	 */
	public boolean isLoaded() {
		return loaded;
	}
	/* �ļ�ָ���ȡ�� */
    private Object wil_locker = new Object();
    
    WIL(String wilPath) {
		File f_wil = new File(wilPath);
		if(!f_wil.exists()) return;
		if(!f_wil.isFile()) return;
		if(!f_wil.canRead()) return;
    	try {
        	String wixPath = SDK.changeFileExtension(wilPath, "wix");
        	boolean wilOnlyMode = false;
    		File f_wix = new File(wixPath);
    		if(GLOBAL_ONLYWIL_MODE || !f_wix.exists()) {
    			wilOnlyMode = true;
    		}
    		if(!wilOnlyMode && !f_wix.isFile()) return;
    		if(!wilOnlyMode && !f_wix.canRead()) return;
			br_wil = new BinaryReader(f_wil, "r");
			br_wil.skipBytes(44); // ��������
			imageCount = br_wil.readIntLE(); // ͼƬ����
			offsetList = new int[imageCount + 1];
			offsetList[imageCount] = (int)br_wil.length();
			int colorCount = SDK.colorCountToBitCount(br_wil.readIntLE()); // ɫ���
			if(colorCount == 8) {
				// 8λ�Ҷ�ͼ���ܰ汾��ʶ��Ϊ0����ʱ������һ��
				br_wil.skipBytes(4); // ���Ե�ɫ��
				verFlag = br_wil.readIntLE();
			}
    		if(!wilOnlyMode) {
	    		BinaryReader br_wix = new BinaryReader(f_wix, "r");
				br_wix.skipBytes(44); // ��������
				int indexCount = br_wix.readIntLE(); // ��������(Ҳ��ͼƬ����)
				if(verFlag != 0)
					br_wix.skipBytes(4); // �汾��ʶ��Ϊ0��Ҫ����4�ֽ�
				for (int i = 0; i < indexCount; ++i)
	            {
	                // ��ȡ����ƫ����
					offsetList[i] = br_wix.readIntLE();
	            }
				br_wix.close();
    		} else {
				imageInfos = new ImageInfo[imageCount];
				int lastOffset = 1024 + 48 + 8;
				for(int i = 0; i < imageCount; ++i) {
					offsetList[i] = lastOffset;
	    			if(colorCount == 8) {
    					if(lastOffset + 9 > br_wil.length()) {
    						// ���ݳ���ֱ�Ӹ�ֵΪ��ͼƬ
    						imageInfos[i] = ImageInfo.EMPTY;
    	            		continue;
    					}
	    			} else {
    					if(lastOffset + 12 > br_wil.length()) {
    						// ���ݳ���ֱ�Ӹ�ֵΪ��ͼƬ
    						imageInfos[i] = ImageInfo.EMPTY;
    	            		continue;
    					}
	    			}
					br_wil.seek(lastOffset);
					short w = (short)br_wil.readUnsignedShortLE();
					short h = (short)br_wil.readUnsignedShortLE();
	                lastOffset += 8;
					if(w == 1 && h == 1) {
						// WIL�����п�ͼƬ����ʱͼƬ��СΪ1x1
						lastOffset += 4;
						imageInfos[i] = ImageInfo.EMPTY;
	            		continue;
					}
					ImageInfo ii = new ImageInfo();
	                ii.setColorBit((byte) colorCount);
	                ii.setWidth(w);
	                ii.setHeight(h);
					ii.setOffsetX(br_wil.readShortLE());
					ii.setOffsetY(br_wil.readShortLE());
	                imageInfos[i] = ii;
	                lastOffset += SDK.widthBytes(colorCount * w) * h;
				}
				loaded = true;
				return;
    		}
			imageInfos = new ImageInfo[imageCount];
			for (int i = 0; i < imageCount; ++i) {
				int offset = offsetList[i];
				if(offset + 9 > br_wil.length()) {
					// ���ݳ���ֱ�Ӹ�ֵΪ��ͼƬ
					imageInfos[i] = ImageInfo.EMPTY;
            		continue;
				}
				int length = offsetList[i + 1] - offset - 8;
				if(length < 2) {
					// WIL��ɫ������Ϊ1���ֽڵ��ǿ�ͼƬ����ʱͼƬ��СΪ1x1
					imageInfos[i] = ImageInfo.EMPTY;
            		continue;
				}
                // ��ȡͼƬ��Ϣ
                ImageInfo ii = new ImageInfo();
                ii.setColorBit((byte) colorCount);
                br_wil.seek(offset);
				ii.setWidth((short)br_wil.readUnsignedShortLE());
				ii.setHeight((short)br_wil.readUnsignedShortLE());
				ii.setOffsetX(br_wil.readShortLE());
				ii.setOffsetY(br_wil.readShortLE());
                imageInfos[i] = ii;
            }
            loaded = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }

    /**
     * �ر�WIL�����ͷ������õ��ļ����Լ��ڴ�ռ��
     */
	public final void close() throws IOException {
		synchronized (wil_locker) {
			offsetList = null;
            imageInfos = null;
            loaded = false;
			if (br_wil != null)
            {
				br_wil.close();
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
	    	byte[] pixels = null;
	    	synchronized(wil_locker) {
	    		br_wil.seek(offsetList[index] + 8);
	    		int pixelLength = offsetList[index + 1] - offsetList[index];
                pixels = new byte[pixelLength - 8];
				br_wil.readFully(pixels);
				if(pixels.length == 1) {
					// �հ�ͼƬ
					byte[] sRGB = new byte[3];
					byte[] pallete = SDK.palletes[pixels[0] & 0xff];
					sRGB[0] = pallete[1];
					sRGB[1] = pallete[2];
					sRGB[2] = pallete[3];
					return new Texture(sRGB, (short)1, (short)1);
				}
	    	}
	    	byte[] sRGB = new byte[ii.getWidth() * ii.getHeight() * 3];
	    	if (ii.getColorBit() == 8)
            {
                int p_index = 0;
                for (int h = ii.getHeight() - 1; h >= 0; --h)
                    for (int w = 0; w < ii.getWidth(); ++w)
                    {
                        // ��������ֽ�
                        if (w == 0)
                            p_index += SDK.skipBytes(8, ii.getWidth());
                        byte[] pallete = SDK.palletes[pixels[p_index++] & 0xff];
    					int _idx = (w + h * ii.getWidth()) * 3;
    					sRGB[_idx] = pallete[1];
    					sRGB[_idx + 1] = pallete[2];
    					sRGB[_idx + 2] = pallete[3];
                    }
            }
	    	else if (ii.getColorBit() == 16)
            {
	    		ByteBuffer bb = ByteBuffer.wrap(pixels);
	    		bb.order(ByteOrder.LITTLE_ENDIAN);
	    		int p_index = 0;
                for (int h = ii.getHeight() - 1; h >= 0; --h)
                    for (int w = 0; w < ii.getWidth(); ++w, p_index += 2)
                    {
                        // ��������ֽ�
                        if (w == 0)
                            p_index += SDK.skipBytes(16, ii.getWidth());
                        short pdata = bb.getShort(p_index);
                        byte r = (byte) ((pdata & 0xf800) >> 8);// ��������16λ������������Զ���˺���8λ
                        byte g = (byte) ((pdata & 0x7e0) >> 3);// �����3λ����ǿתʱǰ8λ���Զ���ʧ
                        byte b = (byte) ((pdata & 0x1f) << 3);// ����3λ
    					int _idx = (w + h * ii.getWidth()) * 3;
    					sRGB[_idx] = r;
    					sRGB[_idx + 1] = g;
    					sRGB[_idx + 2] = b;
                    }
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

package org.jootnet.m2client.texture.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.InflaterInputStream;

import org.jootnet.m2client.texture.Texture;
import org.jootnet.m2client.util.BinaryReader;
import org.jootnet.m2client.util.SDK;

/**
 * ��Ѫ����2WZLͼƬ��
 * 
 * @author johness
 */
final class WZL implements ImageLibrary {

	private int imageCount;
	/**
	 * ��ȡ����ͼƬ����
	 * 
	 * @return �����ڵ�ǰWZL���е�ͼƬ����
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
     * @return ���д����ڵ�ǰWZL���е�ͼƬ��Ϣ����
     */
	ImageInfo[] getImageInfos() {
		return imageInfos;
	}
	/* WZL�ļ������ȡ���� */
	private BinaryReader br_wzl;
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
    private Object wzl_locker = new Object();
    
    WZL(String wzlPath) {
    	String wzxPath = SDK.changeFileExtension(wzlPath, "wzx");
		File f_wzx = new File(wzxPath);
		if(!f_wzx.exists()) return;
		if(!f_wzx.isFile()) return;
		if(!f_wzx.canRead()) return;
		File f_wzl = new File(wzlPath);
		if(!f_wzl.exists()) return;
		if(!f_wzl.isFile()) return;
		if(!f_wzl.canRead()) return;
    	try {
    		BinaryReader br_wzx = new BinaryReader(f_wzx, "r");
    		br_wzx.skipBytes(44); // ��������
    		imageCount = br_wzx.readIntLE();
			offsetList = new int[imageCount];
			for (int i = 0; i < imageCount; ++i)
			{
				// ��ȡ����ƫ�Ƶ�ַ
				offsetList[i] = br_wzx.readIntLE();
			}
			br_wzx.close();
			br_wzl = new BinaryReader(f_wzl, "r");
			imageInfos = new ImageInfo[imageCount];
            lengthList = new int[imageCount];
            for (int i = 0; i < imageCount; ++i) {
            	int offset = offsetList[i];
            	if(offset < 48) {
            		// WZL��offsetΪ0���ǿ�ͼƬ
					imageInfos[i] = ImageInfo.EMPTY;
            		continue;
            	}
            	if(offset + 16 > br_wzl.length()) {
					// ���ݳ���ֱ�Ӹ�ֵΪ��ͼƬ
					imageInfos[i] = ImageInfo.EMPTY;
            		continue;
				}
                // ��ȡͼƬ��Ϣ�����ݳ���
                ImageInfo ii = new ImageInfo();
                br_wzl.seek(offset);
                ii.setColorBit((byte) (br_wzl.readByte() == 5 ? 16 : 8));
                br_wzl.skipBytes(3); // ����3�ֽ�δ֪����
                ii.setWidth((short)br_wzl.readUnsignedShortLE());
				ii.setHeight((short)br_wzl.readUnsignedShortLE());
				ii.setOffsetX(br_wzl.readShortLE());
				ii.setOffsetY(br_wzl.readShortLE());
                imageInfos[i] = ii;
                lengthList[i] = br_wzl.readIntLE();
            }
            loaded = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }

    /** ��zlib��ѹ 
     * @throws IOException */
	private static byte[] unzip(byte[] ziped) throws IOException {
		InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(ziped));
		ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
		int i = 1024;
		byte[] buf = new byte[i];

		while ((i = iis.read(buf, 0, i)) > 0) {
			o.write(buf, 0, i);
		}
		return o.toByteArray();
	}
    
    /**
     * �ر�WZL�����ͷ������õ��ļ����Լ��ڴ�ռ��
     */
	public final void close() throws IOException {
		synchronized (wzl_locker) {
			offsetList = null;
			lengthList = null;
            imageInfos = null;
            loaded = false;
			if (br_wzl != null)
            {
				br_wzl.close();
            }
		}
	}

	public final Texture tex(int index) {
		if(!loaded) return Texture.EMPTY;
		if(index < 0) return Texture.EMPTY;
		if(index >= imageCount) return Texture.EMPTY;
		if(imageInfos[index] == ImageInfo.EMPTY) return Texture.EMPTY;
		if(lengthList[index] == 0) return Texture.EMPTY;
    	try{
    		ImageInfo ii = imageInfos[index];
    		int offset = offsetList[index];
    		int length = lengthList[index];
    		byte[] pixels = new byte[length];
    		synchronized (wzl_locker) {
        		br_wzl.seek(offset + 16);
        		br_wzl.read(pixels);
			}
    		pixels = unzip(pixels);
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

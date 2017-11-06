package org.jootnet.m2client.texture;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.jootnet.m2client.util.SDK;

/**
 * ��Ѫ����ͼƬ����<br>
 * ʹ�����ֽ�sRGB��ʽ���ɫ������<br>
 * ͼƬ��֧��͸��ɫ������Ϊ��ɫ<br>
 * ʹ��˫�������ͼ����<br>
 * ��ͼƬ��{@link BufferedImage}�໥ת��
 * 
 * @author johness
 */
public final class Texture implements Cloneable {

	private static int EMPTY_COLOR_INDEX = 0;
	/**
	 * ��ͼƬ
	 */
	public static final Texture EMPTY = new Texture(new byte[]{SDK.palletes[EMPTY_COLOR_INDEX][1],SDK.palletes[EMPTY_COLOR_INDEX][2],SDK.palletes[EMPTY_COLOR_INDEX][3]}, (short)1, (short)1);
	/**
	 * ��BufferedImageͼƬ
	 */
	public static final BufferedImage EMPTY_BUFFEREDIMAGE;
	
	static {
		EMPTY_BUFFEREDIMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		byte[] cls = ((DataBufferByte)EMPTY_BUFFEREDIMAGE.getRaster().getDataBuffer()).getData();
		cls[0] = SDK.palletes[EMPTY_COLOR_INDEX][0];
		cls[1] = SDK.palletes[EMPTY_COLOR_INDEX][1];
		cls[2] = SDK.palletes[EMPTY_COLOR_INDEX][2];
		cls[3] = SDK.palletes[EMPTY_COLOR_INDEX][3];
	}
	
	private byte[] pixels;
	private short width;
	private short height;
	private short offsetX;
	private short offsetY;
	private volatile boolean dirty;
	
	private static byte[] emptyPixels;
	private static long clearCount;
	private static Object clear_locker = new Object();
	private Object proc_locker = new Object();
	
	/**
	 * ��RGB�ֽ����鴴��ͼƬ����
	 * 
	 * @param sRGB
	 * 		ͼƬɫ����������<br>
	 * 		ÿ������ռ�������ֽڽ��д洢����ͼƬ���Ͻǵ����½ǣ�������RGB˳��
	 * @param width
	 * 		ͼƬ���
	 * @param height
	 * 		ͼƬ�߶�
	 * 
	 * @throws IllegalArgumentException ������������ݳ��Ȳ�����Ҫ��
	 */
	public Texture(byte[] sRGB, short width, short height) throws IllegalArgumentException {
		this(sRGB, width, height, (short)0, (short)0);
	}
	
	/**
	 * ��RGB�ֽ����鴴��ͼƬ����
	 * 
	 * @param sRGB
	 * 		ͼƬɫ����������<br>
	 * 		ÿ������ռ�������ֽڽ��д洢����ͼƬ���Ͻǵ����½ǣ�������RGB˳��
	 * @param width
	 * 		ͼƬ���
	 * @param height
	 * 		ͼƬ�߶�
	 * @param offsetX
	 * 		ͼƬ����ƫ����
	 * @param offsetY
	 * 		ͼƬ����ƫ����
	 * 
	 * @throws IllegalArgumentException ������������ݳ��Ȳ�����Ҫ��
	 */
	public Texture(byte[] sRGB, short width, short height, short offsetX, short offsetY) throws IllegalArgumentException {
		if(sRGB != null && width > 0 && height > 0 && sRGB.length != (width * height * 3))
			throw new IllegalArgumentException("sRGB length not match width * height * 3 !!!");
		this.pixels = sRGB;
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
	/**
	 * ��ȡͼƬ���
	 * 
	 * @return ͼƬ���,��λΪ����
	 */
	public short getWidth() {
		return width;
	}
	/**
	 * ��ȡͼƬ�߶�
	 * 
	 * @return ͼƬ�߶�,��λΪ����
	 */
	public short getHeight() {
		return height;
	}
	/**
	 * ��ȡͼƬ����ƫ����
	 * 
	 * @return ͼƬ����ƫ����,��λΪ����
	 */
	public short getOffsetX() {
		return offsetX;
	}
	/**
	 * ��ȡͼƬ����ƫ����
	 * 
	 * @return ͼƬ����ƫ����,��λΪ����
	 */
	public short getOffsetY() {
		return offsetY;
	}
	
	/**
	 * �жϵ�ǰͼƬ�Ƿ�Ϊ��
	 * 
	 * @return true��ʾ��ǰͼƬΪ�գ����������κδ���/����/���л�
	 */
	public final boolean empty() {
		return this == EMPTY || pixels == null || pixels.length == 0 || width < 1 || height < 1;
	}
	
	/**
	 * �жϵ�ǰͼƬ�Ƿ��޸Ĺ�<br>
	 * ��ǰ��������֮��ͼƬ�ᱻ��Ϊδ�޸ģ����´ε��û᷵��false<br>
	 * һ����{@link #toBufferedImage(boolean)}���ʹ�ã��ж�ʱ��
	 * 
	 * @return �ϴε��ô˺���֮��ͼƬ�Ƿ��޸Ĺ�
	 * @see #toBufferedImage(boolean)
	 */
	public final boolean dirty() {
		synchronized (proc_locker) {
			boolean _dirty = dirty;
			dirty = false;
			return _dirty;
		}
	}
	
	protected void finalize() {
		synchronized (clear_locker) {
			clearCount--;
			if(clearCount < 1) {
				clearCount = 0;
				emptyPixels = null;
			}
		}
	}
	
	/**
	 * ������ǰͼƬ���ݵĿ�¡��������¡<br>
	 * ���贴����ǰͼƬ��������Ŀ�¡����ʹ��{@link #clip(int, int, int, int)}
	 * 
	 * @return ��ǰͼƬ������¡
	 * 
	 * @see #clip(int, int, int, int)
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		if(empty())
			return EMPTY;
		synchronized (proc_locker) {
			byte[] sRGB = new byte[pixels.length];
			System.arraycopy(pixels, 0, sRGB, 0, pixels.length);
			return new Texture(sRGB, width, height);
		}
	}
	
	/**
	 * ������ǰͼƬ���ݵĿ�¡�����ֿ�¡<br>
	 * ���������ҷ����·�����ͼƬ�������Գ������֣������Ϸ����ɳ��������������ֱ�Ӳ����д���<br>
	 * ���贴����ǰͼ������¡����ʹ��{@link #clone()}
	 * 
	 * @param x
	 * 		��¡������ʼx����
	 * @param y
	 * 		��¡������ʼy����
	 * @param w
	 * 		��¡������
	 * @param h
	 * 		��¡����߶�
	 * 
	 * @return ��ǰͼƬ���������¡
	 * 
	 * @see #clone()
	 */
	public final Texture clip(int x, int y, int w, int h) {
		if(empty())
			return EMPTY;
		if(x < 0 || x > width || y < 0 || y > height) return EMPTY;
		synchronized (proc_locker) {
			int rx = x + w;
			if(rx >= width)
				rx = width - 1;
			int by = y + h;
			if(by >= height)
				by = height - 1;
			byte[] npixels = new byte[(rx - x) * (by - y) * 3];
			for(int i = y; i < by; ++i) {
				for(int j = x; j < rx; ++j) {
					int _idx = (j + i * width) * 3;
					npixels[(j - x + (i - y) * width) * 3] = pixels[_idx];
					npixels[(j - x + (i - y) * width) * 3 + 1] = pixels[_idx + 1];
					npixels[(j - x + (i - y) * width) * 3 + 2] = pixels[_idx + 2];
				}
			}
			return new Texture(npixels, (short)(rx -x), (short)(by -y));
		}
	}
	
	/**
	 * ��ͼƬ����ת��Ϊ{@link BufferedImage}����<br>
	 * Ĭ�ϲ�֧��Alphaͨ������Ϊ��ͼ���㷨�ǶȽ���û�С�͸��ɫ������ģ�ֻ��������ͼƬ����ʱ��������<br>
	 * �����Ҫ��ͼƬ�н��ض���ɫ��Ϊ͸������ʹ��{@link #toBufferedImageTransparent(byte, byte, byte)}
	 * 
	 * @param disaposable
	 * 		����Ƿ���һ���Ե�<br>
	 * 		����ֵֻΪfalseʱ���ؽ���е�BufferedImage��ͼƬ�������뵱ǰ����ʹ��ͬһ���ֽ�����<br>
	 * 		�Ե�ǰ������κβ�������Ӱ�쵽�������ص�ͼƬչʾ�����������ڶ��߳��г���ͼƬ˺��<br>
	 * 		��ˣ���������Ϊ�Լ�ͷ���������ģ������봫��true<br>
	 * 		�����ϣ�����false�ĺ������ã�����һ�κͶ��Ч������һ���ģ�����true�ĵ�������Ҫͨ��{@link #dirty()}����ʱ���ж�
	 * 
	 * @return ͼƬ���ݶ�Ӧ��{@link BufferedImage}����
	 * 
	 * @see #toBufferedImageTransparent(byte, byte, byte)
	 * @see #dirty()
	 * @see DataBufferByte
	 */
	public final BufferedImage toBufferedImage(boolean disaposable) {
		if(empty())
			return EMPTY_BUFFEREDIMAGE;
		synchronized (proc_locker) {
			byte[] _pixels = null;
			if(!disaposable) {
				_pixels = pixels;
			} else {
				_pixels = new byte[pixels.length];
				System.arraycopy(pixels, 0, _pixels, 0, pixels.length);
			}
			// ��byte[]תΪDataBufferByte���ں�������BufferedImage����
	        DataBufferByte dataBuffer = new DataBufferByte(_pixels, pixels.length);
	        // sRGBɫ�ʿռ����
	        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	        int[] nBits = {8, 8, 8};
	        int[] bOffs = {0, 1, 2};
	        ComponentColorModel colorModel = new ComponentColorModel(cs, nBits, false, false,
	                                             Transparency.OPAQUE,
	                                             DataBuffer.TYPE_BYTE);        
	        WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, width, height, width*3, 3, bOffs, null);
	        return new BufferedImage(colorModel,raster,false,null);
		}
	}
	
	/**
	 * ��ͼƬ����ת��Ϊ{@link BufferedImage}����<br>
	 * �������Ҫ����͸��ɫ����ʹ��{@link #toBufferedImage(boolean)}<br>
	 * �˺������Ὣ����ֵ���뻺�棬��һ���Ե�
	 * 
	 * @param r
	 * 		͸��ɫR����
	 * @param g
	 * 		͸��ɫG����
	 * @param b
	 * 		͸��ɫB����
	 * @return ��ָ����ɫ��Ϊ͸��ɫ��BufferedImage
	 * 
	 * @see #toBufferedImage(boolean)
	 */
	public final BufferedImage toBufferedImageTransparent(byte r, byte g, byte b) {
		if(empty())
			return EMPTY_BUFFEREDIMAGE;
		synchronized (proc_locker) {
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			DataBufferByte dataBuffer = (DataBufferByte)bi.getRaster().getDataBuffer();
			byte[] _pixels = dataBuffer.getData();
			for(int h = 0; h < height; ++h) {
				for(int w = 0; w < width; ++w) {
					byte _r = pixels[(w + h * width) * 3];
					byte _g = pixels[(w + h * width) * 3 + 1];
					byte _b = pixels[(w + h * width) * 3 + 2];
					byte _a = _r == r && _g == g && _b == b ? 0 : (byte)255;
					_pixels[(w + h * width) * 4] = _a;
					_pixels[(w + h * width) * 4 + 1] = _b;
					_pixels[(w + h * width) * 4 + 2] = _g;
					_pixels[(w + h * width) * 4 + 3] = _r;
				}
			}
			return bi;
		}
	}
	
	/**
	 * ���ͼƬɫ������<br>
	 * ���ͼƬȫ��ɫ������<br>
	 * �����Ҫ�����������ɫ��������ʹ��{@link #clear(int, int, int, int)}
	 * 
	 * @see #clear(int, int, int, int)
	 */
	public final void clear() {
		if(empty()) return;
		synchronized (proc_locker) {
			synchronized (clear_locker) {
				if(emptyPixels == null || emptyPixels.length < pixels.length)
					emptyPixels = new byte[pixels.length];
				System.arraycopy(emptyPixels, 0, pixels, 0, pixels.length);
				clearCount++;
			}
			dirty = true;
		}
	}
	
	/**
	 * ���ͼƬɫ������<br>
	 * ���ͼƬ�ڲ�������ɫ������<br>
	 * ���������ҷ����·�����ͼƬ�������Գ������֣������Ϸ����ɳ��������������ֱ�Ӳ����д���<br>
	 * �����Ҫ���ȫ��ɫ��������ʹ��{@link #clear()}
	 * 
	 * @param x
	 * 		Ҫ�����������ʼx����
	 * @param y
	 * 		Ҫ�����������ʼy����
	 * @param w
	 * 		Ҫ�����������
	 * @param h
	 * 		Ҫ���������߶�
	 * 
	 * @see #clear()
	 */
	public final void clear(int x, int y, int w, int h) {
		if(empty()) return;
		if(x < 0 || x > width || y < 0 || y > height) return;
		synchronized (proc_locker) {
			int rx = x + w;
			if(rx >= width)
				rx = width - 1;
			int by = y + h;
			if(by >= height)
				by = height - 1;
			for(int i = y; i < by; ++i) {
				for(int j = x; j < rx; ++j) {
					int _idx = (j + i * width) * 3;
					pixels[_idx] = pixels[_idx + 1] = pixels[_idx + 2] = 0;
				}
			}
			dirty = true;
		}
	}
	
	/**
	 * ��ͼƬת��Ϊ�Ұ�<br>
	 * ��ͼƬȫ������ת��Ϊ�Ұ�<br>
	 * �����Ҫת����������Ϊ�Ұ���ʹ��{@link #toGray(int, int, int, int)}
	 * 
	 * @see #toGray(int, int, int, int)
	 */
	public final void toGray() {
		if(empty()) return;
		synchronized (proc_locker) {
			for(int i = 0; i < pixels.length - 2; i += 3) {
				pixels[i] *= 0.299;
				pixels[i + 1] *= 0.587;
				pixels[i + 2] *= 0.114;
			}
			dirty = true;
		}
	}
	
	/**
	 * ��ͼƬת��Ϊ�Ұ�<br>
	 * ��ͼƬ��������ת��Ϊ�Ұ�<br>
	 * ���������ҷ����·�����ͼƬ�������Գ������֣������Ϸ����ɳ��������������ֱ�Ӳ����д���<br>
	 * �����Ҫת��ȫ������Ϊ�Ұ���ʹ��{@link #toGray()}
	 * 
	 * @param x
	 * 		Ҫת����������ʼx����
	 * @param y
	 * 		Ҫת����������ʼy����
	 * @param w
	 * 		Ҫת����������
	 * @param h
	 * 		Ҫת��������߶�
	 * 
	 * @see #toGray()
	 */
	public final void toGray(int x, int y, int w, int h) {
		if(empty()) return;
		if(x < 0 || x > width || y < 0 || y > height) return;
		synchronized (proc_locker) {
			int rx = x + w;
			if(rx >= width)
				rx = width - 1;
			int by = y + h;
			if(by >= height)
				by = height - 1;
			for(int i = y; i < by; ++i) {
				for(int j = x; j < rx; ++j) {
					int _idx = (j + i * width) * 3;
					pixels[_idx] *= 0.299;
					pixels[_idx + 1] *= 0.587;
					pixels[_idx + 2] *= 0.114;
				}
			}
			dirty = true;
		}
	}
	
	/**
	 * ��ͼƬ���з�ɫ����<br>
	 * ��ͼƬȫ��������з�ɫ����<br>
	 * �����Ҫ�Բ���������з�ɫ������ʹ��{@link #inverse(int, int, int, int)}
	 * 
	 * @see #inverse(int, int, int, int)
	 */
	public final void inverse() {
		if(empty()) return;
		synchronized (proc_locker) {
			for(int i = 0; i < pixels.length; ++i) {
				pixels[i] ^= 0xff;
			}
			dirty = true;
		}
	}
	
	/**
	 * ��ͼƬ���з�ɫ����<br>
	 * ��ͼƬ����������з�ɫ����<br>
	 * ���������ҷ����·�����ͼƬ�������Գ������֣������Ϸ����ɳ��������������ֱ�Ӳ����д���<br>
	 * �����Ҫ��ȫ��������з�ɫ������ʹ��{@link #inverse()}
	 * 
	 * @param x
	 * 		Ҫת����������ʼx����
	 * @param y
	 * 		Ҫת����������ʼy����
	 * @param w
	 * 		Ҫת����������
	 * @param h
	 * 		Ҫת��������߶�
	 * 
	 * @see #inverse()
	 */
	public final void inverse(int x, int y, int w, int h) {
		if(empty()) return;
		if(x < 0 || x > width || y < 0 || y > height) return;
		synchronized (proc_locker) {
			int rx = x + w;
			if(rx >= width)
				rx = width - 1;
			int by = y + h;
			if(by >= height)
				by = height - 1;
			for(int i = y; i < by; ++i) {
				for(int j = x; j < rx; ++j) {
					int _idx = (j + i * width) * 3;
					pixels[_idx] ^= 0xff;
					pixels[_idx + 1] ^= 0xff;
					pixels[_idx + 2] ^= 0xff;
				}
			}
			dirty = true;
		}
	}
	
	/**
	 * ��ͼƬ����͸���ȴ���<br>
	 * ��ͼƬȫ���������͸���ȴ���<br>
	 * �����Ҫ�Բ����������͸���ȴ�����ʹ��{@link #alpha(float, int, int, int, int)}
	 * 
	 * @param alpha ͸����
	 * 
	 * @see #alpha(float, int, int, int, int)
	 */
	public final void alpha(float alpha) {
		if(empty()) return;
		synchronized (proc_locker) {
			for(int i = 0; i < pixels.length; ++i) {
				pixels[i] *= alpha;
			}
			dirty = true;
		}
	}
	
	/**
	 * ��ͼƬ����͸���ȴ���<br>
	 * ��ͼƬ�����������͸���ȴ���<br>
	 * ���������ҷ����·�����ͼƬ�������Գ������֣������Ϸ����ɳ��������������ֱ�Ӳ����д���<br>
	 * �����Ҫ��ȫ���������͸���ȴ�����ʹ��{@link #alpha(float)}
	 * 
	 * @param alpha ͸����
	 * 
	 * @param x
	 * 		Ҫ�����������ʼx����
	 * @param y
	 * 		Ҫ�����������ʼy����
	 * @param w
	 * 		Ҫ�����������
	 * @param h
	 * 		Ҫ���������߶�
	 * 
	 * @see #alpha(float)
	 */
	public final void alpha(float alpha, int x, int y, int w, int h) {
		if(empty()) return;
		if(x < 0 || x > width || y < 0 || y > height) return;
		synchronized (proc_locker) {
			int rx = x + w;
			if(rx >= width)
				rx = width - 1;
			int by = y + h;
			if(by >= height)
				by = height - 1;
			for(int i = y; i < by; ++i) {
				for(int j = x; j < rx; ++j) {
					int _idx = (j + i * width) * 3;
					pixels[_idx] *= alpha;
					pixels[_idx + 1] *= alpha;
					pixels[_idx + 2] *= alpha;
				}
			}
			dirty = true;
		}
	}
	
	/**
	 * ��һ��Ŀ��ͼ���ϵ���ǰͼ����<br>
	 * ʹ����ͨ��ͼ����ӷ�ʽ<br>
	 * ��ֱ��ʹ��Ŀ��rgb��Ϊ��ͼƬ��rgb<br>
	 * �����Ҫʹ��Overlay��ʽ����ʹ��{@link #blendAdd(Texture, Point, float)}��ʽ<br>
	 * �����Ҫ֧��͸��ɫ����ʹ��{@link #blendNormalTransparent(Texture, Point, float, byte, byte, byte)}
	 * �˲������ı�Ŀ��ͼ�����ݣ���ʹ������alpha����
	 * 
	 * @param tar
	 * 		Ŀ��ͼ��
	 * @param loc
	 * 		ͼ�������ʼ����
	 * @param alpha
	 * 		Ŀ��ͼ��͸����
	 * 
	 * @see #blendAdd(Texture, Point, float)
	 * @see #blendAddTransparent(Texture, Point, float, byte, byte, byte)
	 * @see #blendNormalTransparent(Texture, Point, float, byte, byte, byte)
	 */
	public final void blendNormal(Texture tar, Point loc, float alpha) {
		if(empty()) return;
		if(tar.empty()) return;
		synchronized (proc_locker) {
			int x = loc.x;
			int y = loc.y;
			if(x < 0 || x > width || y < 0 || y < height) return;
			int rx = x + tar.width;
			if(rx >= width)
				rx = width - 1;
			int by = y + tar.height;
			if(by >= height)
				by = height - 1;
			for(int i = y; i < by; ++i) {
				for(int j = x; j < rx; ++j) {
					int _idx_this = (j + i * width) * 3;
					int _idx_that = (j - x + (i - y) * width) * 3;
					pixels[_idx_this] = (byte) (tar.pixels[_idx_that] * alpha);
					pixels[_idx_this + 1] = (byte) (tar.pixels[_idx_that + 1] * alpha);
					pixels[_idx_this + 2] = (byte) (tar.pixels[_idx_that + 2] * alpha);
				}
			}
			dirty = true;
		}
	}
	
	/**
	 * ��һ��Ŀ��ͼ���ϵ���ǰͼ����<br>
	 * ʹ����ͨ��ͼ����ӷ�ʽ<br>
	 * ��ֱ��ʹ��Ŀ��rgb��Ϊ��ͼƬ��rgb<br>
	 * �����Ҫʹ��Overlay��ʽ����ʹ��{@link #blendAddTransparent(Texture, Point, float, byte, byte, byte)}��ʽ<br>
	 * �˲������ı�Ŀ��ͼ�����ݣ���ʹ������alpha����<br>
	 * ֧��͸��ɫ�������Ŀ������Ŀ��ͼƬ����ɫ�Ǹ���ֵ�����
	 * 
	 * @param tar
	 * 		Ŀ��ͼ��
	 * @param loc
	 * 		ͼ�������ʼ����
	 * @param alpha
	 * 		Ŀ��ͼ��͸����
	 * @param r
	 * 		͸��ɫR����
	 * @param g
	 * 		͸��ɫ����
	 * @param b
	 * 		͸��ɫ����
	 * 
	 * @see #blendAdd(Texture, Point, float)
	 * @see #blendAddTransparent(Texture, Point, float, byte, byte, byte)
	 * @see #blendNormal(Texture, Point, float)
	 */
	public final void blendNormalTransparent(Texture tar, Point loc, float alpha, byte r, byte g, byte b) {
		if(empty()) return;
		if(tar.empty()) return;
		synchronized (proc_locker) {
			int x = loc.x;
			int y = loc.y;
			if(x < 0 || x > width || y < 0 || y < height) return;
			int rx = x + tar.width;
			if(rx >= width)
				rx = width - 1;
			int by = y + tar.height;
			if(by >= height)
				by = height - 1;
			for(int i = y; i < by; ++i) {
				for(int j = x; j < rx; ++j) {
					int _idx_this = (j + i * width) * 3;
					int _idx_that = (j - x + (i - y) * width) * 3;
					byte _r = tar.pixels[_idx_that];
					byte _g = tar.pixels[_idx_that + 1];
					byte _b = tar.pixels[_idx_that + 2];
					if(r != _r || _g != g || _b != b) {
						pixels[_idx_this] = (byte) (_r * alpha);
						pixels[_idx_this + 1] = (byte) (_g * alpha);
						pixels[_idx_this + 2] = (byte) (_b * alpha);
					}
				}
			}
			dirty = true;
		}
	}
	
	/**
	 * ��һ��Ŀ��ͼ���ϵ���ǰͼ����<br>
	 * ʹ��Overlay��ͼ����ӷ�ʽ<br>
	 * ���Կ���Add���ģʽ����OpenGL����glBlendFunc(GL_SRC_COLOR, GL_ONE)<br>
	 * �����Ҫʹ����ͨ��ʽ����ʹ��{@link #blendNormal(Texture, Point, float)}��ʽ<br>
	 * ����֧��͸��ɫ����ʹ��{@link #blendNormalTransparent(Texture, Point, float, byte, byte, byte)}
	 * �˲������ı�Ŀ��ͼ�����ݣ���ʹ������alpha����
	 * 
	 * @param tar
	 * 		Ŀ��ͼ��
	 * @param loc
	 * 		ͼ�������ʼ����
	 * @param alpha
	 * 		Ŀ��ͼ��͸����
	 * 
	 * @see #blendNormal(Texture, Point, float)
	 * @see #blendNormalTransparent(Texture, Point, float, byte, byte, byte)
	 * @see #blendAddTransparent(Texture, Point, float, byte, byte, byte)
	 */
	public final void blendAdd(Texture tar, Point loc, float alpha) {
		if(empty()) return;
		if(tar.empty()) return;
		synchronized (proc_locker) {
			int x = loc.x;
			int y = loc.y;
			if(x < 0 || x > width || y < 0 || y < height) return;
			int rx = x + tar.width;
			if(rx >= width)
				rx = width - 1;
			int by = y + tar.height;
			if(by >= height)
				by = height - 1;
			for(int i = y; i < by; ++i) {
				for(int j = x; j < rx; ++j) {
					int _idx_this = (j + i * width) * 3;
					int _idx_that = (j - x + (i - y) * width) * 3;
					byte r = (byte) (tar.pixels[_idx_that] * alpha);
					byte g = (byte) (tar.pixels[_idx_that + 1] * alpha);
					byte b = (byte) (tar.pixels[_idx_that + 2] * alpha);
					pixels[_idx_this] = (byte) ((r < 128) ? (2 * pixels[_idx_this] * r / 255) : (255 - 2 * (255 - pixels[_idx_this]) * (255 - r) / 255));
					pixels[_idx_this + 1] = (byte) ((g < 128) ? (2 * pixels[_idx_this + 1] * g / 255) : (255 - 2 * (255 - pixels[_idx_this + 1]) * (255 - g) / 255));
					pixels[_idx_this + 2] = (byte) ((b < 128) ? (2 * pixels[_idx_this + 2] * b / 255) : (255 - 2 * (255 - pixels[_idx_this + 2]) * (255 - b) / 255));
				}
			}
		}
		dirty = true;
	}
	
	/**
	 * ��һ��Ŀ��ͼ���ϵ���ǰͼ����<br>
	 * ʹ��Overlay��ͼ����ӷ�ʽ<br>
	 * ���Կ���Add���ģʽ����OpenGL����glBlendFunc(GL_SRC_COLOR, GL_ONE)<br>
	 * �����Ҫʹ����ͨ��ʽ����ʹ��{@link #blendNormalTransparent(Texture, Point, float, byte, byte, byte)}��ʽ<br>
	 * �˲������ı�Ŀ��ͼ�����ݣ���ʹ������alpha����<br>
	 * ֧��͸��ɫ�������Ŀ������Ŀ��ͼƬ����ɫ�Ǹ���ֵ�����
	 * 
	 * @param tar
	 * 		Ŀ��ͼ��
	 * @param loc
	 * 		ͼ�������ʼ����
	 * @param alpha
	 * 		Ŀ��ͼ��͸����
	 * @param r
	 * 		͸��ɫR����
	 * @param g
	 * 		͸��ɫ����
	 * @param b
	 * 		͸��ɫ����
	 * 
	 * @see #blendNormal(Texture, Point, float)
	 * @see #blendNormalTransparent(Texture, Point, float, byte, byte, byte)
	 * @see #blendAdd(Texture, Point, float)
	 */
	public final void blendAddTransparent(Texture tar, Point loc, float alpha, byte r, byte g, byte b) {
		if(empty()) return;
		if(tar.empty()) return;
		synchronized (proc_locker) {
			int x = loc.x;
			int y = loc.y;
			if(x < 0 || x > width || y < 0 || y < height) return;
			int rx = x + tar.width;
			if(rx >= width)
				rx = width - 1;
			int by = y + tar.height;
			if(by >= height)
				by = height - 1;
			for(int i = y; i < by; ++i) {
				for(int j = x; j < rx; ++j) {
					int _idx_this = (j + i * width) * 3;
					int _idx_that = (j - x + (i - y) * width) * 3;
					byte _r = (byte) (tar.pixels[_idx_that] * alpha);
					byte _g = (byte) (tar.pixels[_idx_that + 1] * alpha);
					byte _b = (byte) (tar.pixels[_idx_that + 2] * alpha);
					if(r != _r || _g != g || _b != b) {
						pixels[_idx_this] = (byte) ((_r < 128) ? (2 * pixels[_idx_this] * _r / 255) : (255 - 2 * (255 - pixels[_idx_this]) * (255 - _r) / 255));
						pixels[_idx_this + 1] = (byte) ((_g < 128) ? (2 * pixels[_idx_this + 1] * _g / 255) : (255 - 2 * (255 - pixels[_idx_this + 1]) * (255 - _g) / 255));
						pixels[_idx_this + 2] = (byte) ((_b < 128) ? (2 * pixels[_idx_this + 2] * _b / 255) : (255 - 2 * (255 - pixels[_idx_this + 2]) * (255 - _b) / 255));
					}
				}
			}
		}
		dirty = true;
	}
}

package org.jootnet.m2client.graphics.texture;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.github.jootnet.mir2.core.Texture;
import com.github.jootnet.mir2.core.image.ImageInfo;
import com.github.jootnet.mir2.core.image.ImageLibraries;
import com.github.jootnet.mir2.core.image.ImageLibrary;

public final class Textures {

	private static Map<String, ImageLibrary> libraries = new HashMap<String, ImageLibrary>();
	
	/**
	 * 从指定路径中解析出一个图片库并存入内存缓存
	 * 
	 * @param libName
	 * 		图片库名称
	 * @return 图片库对象
	 */
	static final ImageLibrary get(String libName) {
		if(libraries.containsKey(libName))
			return libraries.get(libName);
		try{
			String libPath = System.getProperty("org.jootnet.m2client.data.dir", System.getProperty("user.dir"));
			if(!libPath.endsWith(File.separator))
				libPath += File.separator;
			libPath += libName;
			ImageLibrary il = ImageLibraries.get(libName, libPath);
			if(il != null)
				libraries.put(libName, il);
			return il;
		}catch(RuntimeException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private static Object tex_locker = new Object();
	private static Stack<TextureLoader> texLoaders = new Stack<TextureLoader>();
	private static Map<String, Texture> textures = new HashMap<String, Texture>();
	private static Set<String> loadings = new HashSet<String>();
	static class UpdateThread extends Thread {
		UpdateThread() {
			setName("TextureLoader-autoUpdate-Thread");
			setDaemon(true);
		}
		
		@Override
		public void run() {
			while(true) {
				synchronized (tex_locker) {
					boolean allDone = true;
					for(TextureLoader l : texLoaders) {
						if(!l.update())
							allDone = false;
					}
					if(allDone) break;
					updateThread = null;
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}
	private static UpdateThread updateThread = null;
	private static String buildKey(String dataFileName, int index) {
		return ((char)(dataFileName.length() + '0')) + dataFileName + index;
	}
	// 给包内部对象使用的函数，主要是给异步加载对象使用的存入异步加载成功的纹理数据
	static void putTexture(String libName, int index, Texture tex) {
		synchronized (tex_locker) {
			String key = buildKey(libName, index);
			textures.put(key, tex);
			loadings.remove(key);
		}
	}
	
	/**
	 * 异步读取一张纹理到缓存
	 * <br>
	 * 需要获取这张纹理的话请使用{@link #getTextureFromCache(String, int) getTextureFromCache}函数
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @see #isTextureInLoad(String, int)
	 * @see #isTextureInCache(String, int)
	 */
	public static void loadTextureAsync(String dataFileName, int index) {
		dataFileName = dataFileName.toLowerCase();
		synchronized (tex_locker) {
			TextureLoader loader = null;
			boolean containFlag = false;
			for(TextureLoader l : texLoaders) {
				if(l.getDataFileName().equals(dataFileName)) {
					containFlag = true;
					loader = l;
					break;
				}
			}
			if(!containFlag) {
				loader = new TextureLoader(dataFileName);
				texLoaders.add(loader);
			}
			loader.load(index);
			if(updateThread == null)
				(updateThread = new UpdateThread()).start();
		}
	}
	/**
	 * 异步读取多张张纹理到缓存
	 * <br>
	 * 需要获取这张纹理的话请使用{@link #getTextureFromCache(String, int) getTextureFromCache}函数
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index数组
	 * 		纹理索引
	 * @see #isTextureInLoad(String, int)
	 * @see #isTextureInCache(String, int)
	 */
	public static void loadTextureAsync(String dataFileName, List<Integer> index) {
		dataFileName = dataFileName.toLowerCase();
		if(index == null || index.isEmpty()) return;
		Collections.sort(index); // 加大预读命中率，顺序读
		synchronized (tex_locker) {
			TextureLoader loader = null;
			boolean containFlag = false;
			for(TextureLoader l : texLoaders) {
				if(l.getDataFileName().equals(dataFileName)) {
					containFlag = true;
					loader = l;
					break;
				}
			}
			if(!containFlag) {
				loader = new TextureLoader(dataFileName);
				texLoaders.add(loader);
			}
			for(int idx : index) {
				loader.load(idx);
			}
			if(updateThread == null)
				(updateThread = new UpdateThread()).start();
		}
	}
	
	/**
	 * 立即获取某张纹理
	 * <br>
	 * 不加入缓存
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @return 获取到的纹理，不可能为null，但可能为空，使用{@link Texture#empty()}判定
	 * @see #getTextureFromCache(String, int)
	 */
	public static Texture getTextureImmediately(String dataFileName, int index) {
		dataFileName = dataFileName.toLowerCase();
		return get(dataFileName).tex(index);
	}

	/**
	 * 立即获取某张纹理的属性
	 * <br>
	 * 不加入缓存
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @return 获取到的纹理属性，不可能为null，但可能为空，为{@link ImageInfo#EMPTY}
	 */
	public static ImageInfo getImageInfoImmediately(String dataFileName, int index) {
		dataFileName = dataFileName.toLowerCase();
		return get(dataFileName).info(index);
	}
	
	/**
	 * 从缓存中获取已异步加载的纹理
	 * <br>
	 * 需要先请求异步加载{@link #loadTextureAsync(String, int) loadTextureAsync}
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @return 获取到的纹理，可能为null
	 * @see #getTextureImmediately(String, int)
	 */
	public static Texture getTextureFromCache(String dataFileName, int index) {
		dataFileName = dataFileName.toLowerCase();
		synchronized (tex_locker) {
			return textures.get(buildKey(dataFileName, index));
		}
	}

	/**
	 * 判定指定纹理是否已存在于缓存中
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @return 指定纹理是否已加入缓存
	 */
	public static boolean isTextureInCache(String dataFileName, int index) {
		dataFileName = dataFileName.toLowerCase();
		synchronized (tex_locker) {
			return textures.containsKey(buildKey(dataFileName, index));
		}
	}
	
	/**
	 * 判定指定纹理是否正在异步加载
	 * 
	 * @param dataFileName
	 * 		纹理所在图片库名称
	 * @param index
	 * 		纹理索引
	 * @return 指定纹理是否已请求异步加载并还未成功
	 */
	public static boolean isTextureInLoad(String dataFileName, int index) {
		dataFileName = dataFileName.toLowerCase();
		synchronized (tex_locker) {
			return loadings.contains(buildKey(dataFileName, index));
		}
	}
	
	/**
	 * 获取某个图片库的异步加载进度
	 * 
	 * @param dataFileName
	 * 		图片库名称
	 * @return
	 * 		0~1之间的小数表示百分比；为1表示(该库)所有被请求的异步加载均成功；一般不为0，为0要么是一个都还未成功(可能性极低)，要么是该库内纹理从未被请求异步加载
	 */
	public static float getLoadProgress(String dataFileName) {
		dataFileName = dataFileName.toLowerCase();
		synchronized (tex_locker) {
			for(TextureLoader l : texLoaders) {
				if(l.getDataFileName().equals(dataFileName))
					return l.getProgress();
			}
			return 0f;
		}
	}
	
	/**
	 * 获取异步加载进度(所有库)
	 * 
	 * @return
	 * 		0~1之间的小数表示百分比；为1表示所有被请求的异步加载均成功；一般不为0，为0要么是一个都还未成功(可能性极低)，要么是还没有纹理被请求异步加载
	 */
	public static float getLoadProgress() {
		synchronized (tex_locker) {
			if(texLoaders.isEmpty()) return 0f;
			float ap = 0f;
			for(TextureLoader l : texLoaders) {
				ap += l.getProgress();
			}
			return ap / texLoaders.size(); // FIXME float除int会不会不为1而为0.999999???
		}
	}

	private static final BufferedImage EMPTY_BUFFEREDIMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
	
	/**
	 * 将图片数据转换为{@link BufferedImage}对象<br>
	 * 默认不支持Alpha通道，因为从图像算法角度讲是没有“透明色”概念的，只有在两张图片叠加时才有意义<br>
	 * 如果需要在图片中将特定颜色置为透明，则使用{@link #toBufferedImageTransparent(byte, byte, byte)}
	 * 
	 * @param texture 要转换的图片数据
	 * 
	 * @param disaposable
	 * 		结果是否是一次性的<br>
	 * 		当此值只为false时返回结果中的BufferedImage中图片数据是与当前对象使用同一个字节数组<br>
	 * 		对当前对象的任何操作都会影响到函数返回的图片展示，甚至可能在多线程中出现图片撕裂<br>
	 * 		因此，除非你认为自己头脑是清晰的，否则请传递true<br>
	 * 		理论上，传递false的函数调用，调用一次和多次效果都是一样的，传递true的调用则需要通过{@link #dirty()}进行时机判断
	 * 
	 * @return 图片数据对应的{@link BufferedImage}对象
	 */
	public static BufferedImage toBufferedImage(Texture texture, boolean disaposable) {
		if(texture.empty())
			return EMPTY_BUFFEREDIMAGE;
		byte[] _pixels = null;
		if(!disaposable) {
			_pixels = texture.getRGBs();
		} else {
			_pixels = new byte[texture.getRGBs().length];
			System.arraycopy(texture.getRGBs(), 0, _pixels, 0, texture.getRGBs().length);
		}
		// 将byte[]转为DataBufferByte用于后续创建BufferedImage对象
        DataBufferByte dataBuffer = new DataBufferByte(_pixels, texture.getRGBs().length);
        // sRGB色彩空间对象
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        int[] nBits = {8, 8, 8};
        int[] bOffs = {0, 1, 2};
        ComponentColorModel colorModel = new ComponentColorModel(cs, nBits, false, false,
                                             Transparency.OPAQUE,
                                             DataBuffer.TYPE_BYTE);        
        WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, texture.getWidth(), texture.getHeight(), texture.getWidth()*3, 3, bOffs, null);
        return new BufferedImage(colorModel,raster,false,null);
	}
}

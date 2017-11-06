package org.jootnet.m2client.texture.internal;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jootnet.m2client.texture.Texture;
import org.jootnet.m2client.util.SDK;

public final class Textures {

	private static Map<String, ImageLibrary> libraries = new HashMap<String, ImageLibrary>();
	private static Object lib_locker = new Object();
	
	/**
	 * ��ָ��·���н�����һ��ͼƬ�Ⲣ�����ڴ滺��
	 * 
	 * @param libName
	 * 		ͼƬ������
	 * @return ͼƬ�����
	 */
	static final ImageLibrary get(String libName) {
		synchronized (lib_locker) {
			if(libraries.containsKey(libName))
				return libraries.get(libName);
			try{
				String libPath = System.getProperty("org.jootnet.m2client.data.dir", System.getProperty("user.dir"));
				if(!libPath.endsWith(File.separator))
					libPath += File.separator;
				libPath += libName;
				String wzlPath = SDK.changeFileExtension(libPath, "wzl");
				WZL wzl = new WZL(wzlPath);
				if(wzl.isLoaded()) {
					libraries.put(libName, wzl);
					return wzl;
				}
				String wisPath = SDK.changeFileExtension(libPath, "wis");
				WIS wis = new WIS(wisPath);
				if(wis.isLoaded()) {
					libraries.put(libName, wis);
					return wis;
				}
				String wilPath = SDK.changeFileExtension(libPath, "wil");
				WIL wil = new WIL(wilPath);
				if(wil.isLoaded()) {
					libraries.put(libName, wil);
					return wil;
				}
				return null;
			}catch(RuntimeException ex) {
				ex.printStackTrace();
				return null;
			}
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
				boolean allDone = true;
				for(TextureLoader l : texLoaders) {
					if(!l.update())
						allDone = false;
				}
				if(allDone) break;
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	private static UpdateThread updateThread = new UpdateThread();
	private static String buildKey(String dataFileName, int index) {
		return ((char)(dataFileName.length() + '0')) + dataFileName + index;
	}
	// �����ڲ�����ʹ�õĺ�������Ҫ�Ǹ��첽���ض���ʹ�õĴ����첽���سɹ�����������
	static void putTexture(String libName, int index, Texture tex) {
		synchronized (tex_locker) {
			String key = buildKey(libName, index);
			textures.put(key, tex);
			loadings.remove(key);
		}
	}
	
	/**
	 * �첽��ȡһ����������
	 * <br>
	 * ��Ҫ��ȡ��������Ļ���ʹ��{@link #getTextureFromCache(String, int) getTextureFromCache}����
	 * 
	 * @param dataFileName
	 * 		��������ͼƬ������
	 * @param index
	 * 		��������
	 * @see #isTextureInLoad(String, int)
	 * @see #isTextureInCache(String, int)
	 */
	public static void loadTextureAsync(String dataFileName, int index) {
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
			if(!updateThread.isAlive())
				updateThread.start();
		}
	}
	
	/**
	 * ������ȡĳ������
	 * <br>
	 * �����뻺��
	 * 
	 * @param dataFileName
	 * 		��������ͼƬ������
	 * @param index
	 * 		��������
	 * @return ��ȡ��������������Ϊnull��������Ϊ�գ�ʹ��{@link Texture#empty()}�ж�
	 * @see #getTextureFromCache(String, int)
	 */
	public static Texture getTextureImmediately(String dataFileName, int index) {
		return get(dataFileName).tex(index);
	}
	
	/**
	 * �ӻ����л�ȡ���첽���ص�����
	 * <br>
	 * ��Ҫ�������첽����{@link #loadTextureAsync(String, int) loadTextureAsync}
	 * 
	 * @param dataFileName
	 * 		��������ͼƬ������
	 * @param index
	 * 		��������
	 * @return ��ȡ������������Ϊnull
	 * @see #getTextureImmediately(String, int)
	 */
	public static Texture getTextureFromCache(String dataFileName, int index) {
		synchronized (tex_locker) {
			return textures.get(buildKey(dataFileName, index));
		}
	}
	
	/**
	 * �ж�ָ�������Ƿ��Ѵ����ڻ�����
	 * 
	 * @param dataFileName
	 * 		��������ͼƬ������
	 * @param index
	 * 		��������
	 * @return ָ�������Ƿ��Ѽ��뻺��
	 */
	public static boolean isTextureInCache(String dataFileName, int index) {
		synchronized (tex_locker) {
			return textures.containsKey(buildKey(dataFileName, index));
		}
	}
	
	/**
	 * �ж�ָ�������Ƿ������첽����
	 * 
	 * @param dataFileName
	 * 		��������ͼƬ������
	 * @param index
	 * 		��������
	 * @return ָ�������Ƿ��������첽���ز���δ�ɹ�
	 */
	public static boolean isTextureInLoad(String dataFileName, int index) {
		synchronized (tex_locker) {
			return loadings.contains(buildKey(dataFileName, index));
		}
	}
	
	/**
	 * ��ȡĳ��ͼƬ����첽���ؽ���
	 * 
	 * @param dataFileName
	 * 		ͼƬ������
	 * @return
	 * 		0~1֮���С����ʾ�ٷֱȣ�Ϊ1��ʾ(�ÿ�)���б�������첽���ؾ��ɹ���һ�㲻Ϊ0��Ϊ0Ҫô��һ������δ�ɹ�(�����Լ���)��Ҫô�Ǹÿ��������δ�������첽����
	 */
	public static float getLoadProgress(String dataFileName) {
		synchronized (tex_locker) {
			for(TextureLoader l : texLoaders) {
				if(l.getDataFileName().equals(dataFileName))
					return l.getProgress();
			}
			return 0f;
		}
	}
	
	/**
	 * ��ȡ�첽���ؽ���(���п�)
	 * 
	 * @return
	 * 		0~1֮���С����ʾ�ٷֱȣ�Ϊ1��ʾ���б�������첽���ؾ��ɹ���һ�㲻Ϊ0��Ϊ0Ҫô��һ������δ�ɹ�(�����Լ���)��Ҫô�ǻ�û�����������첽����
	 */
	public static float getLoadProgress() {
		synchronized (tex_locker) {
			if(texLoaders.isEmpty()) return 0f;
			float ap = 0f;
			for(TextureLoader l : texLoaders) {
				ap += l.getProgress();
			}
			return ap / texLoaders.size(); // FIXME float��int�᲻�᲻Ϊ1��Ϊ0.999999???
		}
	}
}

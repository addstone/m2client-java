package org.jootnet.m2client.texture.internal;

import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * �����ȡ��
 * <br>
 * �����ʵ��������ʵ���̰߳�ȫ<b>ʹ�ö����ʵ��ʵ�ֶ��̼߳����������ٶ�</b>
 */
final class TextureLoader {
	// ͼƬ�������
	private String dataFileName;
	String getDataFileName() {
		return dataFileName;
	}
	TextureLoader(String dataFileName) {
		this.dataFileName = dataFileName;
	}
	
	/** �Ѿ����ص��������� */
	private volatile int loaded = 0;
	/** ��Ҫ���ص��������� */
	private volatile int toLoad = 0;
	
	/** ������� */
	private Stack<TextureLoadingTask> tasks = new Stack<TextureLoadingTask>();

	/** �̳߳� */
	private final ExecutorService threadPool = Executors.newFixedThreadPool(1, new ThreadFactory() {
		
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, "TextureLoader-Load-Thread");
			thread.setDaemon(true);
			return thread;
		}
	});
	ExecutorService getThreadPool() {
		return threadPool;
	}
	
	/**
	 * ��������ط����������
	 * 
	 * @param index
	 * 		��������
	 */
	synchronized void load(int index) {
		if (tasks.size() == 0) {
			loaded = 0;
			toLoad = 0;
		}
		tasks.push(new TextureLoadingTask(this, index));
		toLoad++;
	}
	/**
	 * ����������صĲ���
	 * <br>
	 * ����֡����
	 * 
	 * @return ���������Ҫ���ص������������򷵻�true����֮����false
	 */
	synchronized boolean update() {
		if(tasks.size() == 0) return true;
		return updateTask() && tasks.size() == 0;
	}
	/** ���µ�ǰִ�е�����״̬<br>XXX����������У�������ÿֻ֡����һ������ */
	private boolean updateTask() {
		boolean result = false;
		Iterator<TextureLoadingTask> itTasks = tasks.iterator();
		while(itTasks.hasNext()) {
			TextureLoadingTask task = itTasks.next();
			if (task.update()) {
				loaded++;
				itTasks.remove();
				result = true;
			}
		}
		return result;
	}
	/** �����̵߳ȴ����м������ */
	void finishLoading() {
		while (!update())
			Thread.yield();
	}
	/** ��ȡ���ذٷֱ� */
	float getProgress() {
		if (toLoad == 0) return 1;
		return Math.min(1, loaded / (float)toLoad);
	}
}

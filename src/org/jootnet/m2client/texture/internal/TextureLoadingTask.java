package org.jootnet.m2client.texture.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jootnet.m2client.texture.Texture;

/**
 * �����������
 */
final class TextureLoadingTask implements Callable<Texture> {

	// �������������
	private TextureLoader loader;
	// Ҫ���ص���������
	private int index;
	public int getIndex() {
		return index;
	}
	/** �������� */
	private Future<Texture> loadFuture = null;
	
	TextureLoadingTask(TextureLoader loader, int index) {
		this.loader = loader;
		this.index = index;
	}
	
	@Override
	public Texture call() throws Exception {
		return Textures.get(loader.getDataFileName()).tex(index);
	}
	
	boolean update() {
		if (loadFuture == null) {
			loadFuture = loader.getThreadPool().submit(this);
		}
		else if (loadFuture.isDone()) {
			try {
				Textures.putTexture(loader.getDataFileName(), index, loadFuture.get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO ���ܵ��쳣�������ף�֪ͨ��
			}
			return true;
		}
		return false;
	}
}

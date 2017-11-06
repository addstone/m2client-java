package org.jootnet.m2client.graphics;

/**
 * ������Ϣ
 * 
 * @author ��
 */
public interface GraphicsContext {
	/**
	 * ��ȡ��ǰ��Ϸ������
	 * 
	 * @return ���
	 */
	int getWidth();
	
	/**
	 * ��ȡ��ǰ��Ϸ����߶�
	 * 
	 * @return �߶�
	 */
	int getHeight();
	
	/**
	 * ��ȡ��ǰ��Ϸ����ÿ�����ˢ�´���
	 * <br>
	 * ʵ��ˢ�´���Ӧ��С�ڴ���ֵ
	 * 
	 * @return ���֡��
	 */
	int getMaxFps();
	
	/**
	 * ��ȡ��֡���
	 * 
	 * @return ��ǰ֡��ǰһ֮֡��ļ��(��λΪ����)
	 */
	float getDeltaTime();
	
	/**
	 * ��ȡ��Ⱦ֡��
	 * 
	 * @return ǰһ���ܹ���Ⱦ֡��
	 */
	int getFramesPerSecond();
	
	/**
	 * ��ȡ��ǰ֡��
	 * 
	 * @return ��ǰΪ��һ��ڼ�֡
	 */
	int getCurrentFrame();
	
	/**
	 * ��ȡ��ǰ��{@link #getMaxFps() maxFps}�ڵڼ�֡
	 * <br>
	 * �˲������ǵ�ǰ(��Ⱦ)���ڵ�֡����������һ����Ⱦѭ���еĵڼ�֡
	 * <br>
	 * ÿ����һ����Ч��Ⱦ������ֵ��1
	 * <br>
	 * ����ֵ���Ϊ{@link #getMaxFps() maxFps}����СΪ1
	 * <br>
	 * �˲�����Ϊ����֡���Ʋ���
	 * 
	 * @return ��ȡ��ǰΪ��Ⱦѭ���еڼ�����Ч��Ⱦ
	 */
	int getTickFrames();
}

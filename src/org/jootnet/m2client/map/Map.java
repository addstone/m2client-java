package org.jootnet.m2client.map;

import org.jootnet.m2client.graphics.Drawable;
import org.jootnet.m2client.graphics.GraphicsContext;
import org.jootnet.m2client.map.internal.MapInfo;
import org.jootnet.m2client.texture.Texture;

public abstract class Map implements Drawable {
	
	/**
	 * ��ͼ�ſ��
	 * <br>
	 * �߼���������Ļ���ؿ��
	 */
	private final static byte PIXEL_WIDTH_PER_TILE = 48;
	/**
	 * ��ͼ�ſ��
	 * <br>
	 * �߼���������Ļ���ظ߶�
	 */
	private final static byte PIXEL_HEIGHT_PER_TILE = 32;
	/** ���Ƶ�ͼʱ�������������� */
	private final static byte EXTEND_LEFT = 5;
	/** ���Ƶ�ͼʱ�������������� */
	private final static byte EXTEND_RIGHT = 5;
	/** ���Ƶ�ͼʱ�������������� */
	private final static byte EXTEND_BOTTOM = 5;
	
	/** ��ͼ��� */
	private int mw;
	/** ��ͼ�߶� */
	private int mh;
	/** ��ɫ�������� */
	private short x;
	/** ��ɫ�������� */
	private short y;
	/** ��ͼ�����������Ͻ�(�������Ϸ����ֱ������ϵ) */
	private short px;
	/** ��ͼ�����������Ͻ�(�������Ϸ����ֱ������ϵ) */
	private short py;
	/** ��ͼ���������� */
	private short gw;
	/** ��ͼ��������߶� */
	private short gh;
	/** ��ͼ�������Ͻ�Ϊ��ͼ��ڼ��� */
	private short tws;
	/** ��ͼ�������Ͻ�Ϊ��ͼ��ڼ��� */
	private short ths;
	/** �����������½�Ϊ��ͼ��ڼ��� */
	private short twe;
	/** �����������½�Ϊ��ͼ��ڼ��� */
	private short the;
	/**
	 * ����ͼƬ��Ҫ׼�����������Ͻ�����
	 * <br>
	 * ���ڵ�ͼ���ƶ�Ҫ����Ҫ��Ԥ���ɫ���ܳ��ֵ�����
	 * <br>
	 * ���ǽ���������2����С��ΪԤ��Ľ�ɫ���ܳ��ֵ�λ��
	 */
	private short pws;
	/**
	 * ����ͼƬ��Ҫ׼�������Ͻ�����
	 */
	private short phs;
	/**
	 * ����ͼƬ��Ҫ׼�������½�����
	 */
	private short pwe;
	/**
	 * ����ͼƬ��Ҫ׼�������½�����
	 */
	private short phe;
	
	private String name;
	private MapInfo info;
	protected Map(String name, MapInfo info) {
		this.name = name;
		this.info = info;
		// ��ͼ���(����)
		mw = info.getWidth() * PIXEL_WIDTH_PER_TILE;
		// ��ͼ�߶�(����)
		mh = info.getHeight() * PIXEL_HEIGHT_PER_TILE;
	}
	
	/**
	 * ��ȡ��ɫ��������
	 * 
	 * @return ������
	 */
	public int roleX() {
		return x;
	}
	/**
	 * ��ȡ��ɫ��������
	 * 
	 * @return ������
	 */
	public int roleY() {
		return y;
	}
	
	private boolean moved;
	/**
	 * �ƶ���ɫ��������(����ڵ�ͼ)
	 * 
	 * @param x
	 * 		������
	 * @param y
	 * 		������
	 */
	public void move(int x, int y) {
		this.x = (short) x;
		this.y = (short) y;
		moved = true;
	}
	
	@Override
	public boolean adjust(GraphicsContext ctx) {
		if(moved) {
			// ��������������Ͻ�����
			// �����������Ͻ�x
			px = (short) (ctx.getWidth() > mw ? (ctx.getWidth() - mw) / 2 : 0);
			// �����������Ͻ�y
			py = (short) (ctx.getHeight() > mh ? (ctx.getHeight() - mh) / 2 : 0);
			// ������ƿ�Ⱥ͸߶�
			// ���ƿ��
			gw = (short) (ctx.getWidth() > mw ? mw : ctx.getWidth());
			// ���Ƹ߶�
			gh = (short) (ctx.getHeight() > mh ? mh : ctx.getHeight());
	
			// ��ͼ�������Ͻ�Ϊ��ͼ��ڼ���
			tws = (short) (x - (gw / PIXEL_WIDTH_PER_TILE - 1) / 2);
			if (tws < 0)
				tws = 0;
			// ��ͼ�������Ͻ�Ϊ��ͼ��ڼ���
			ths = (short) (y - (gh / PIXEL_HEIGHT_PER_TILE - 1) / 2);
			if (ths < 0)
				ths = 0;
			
			// �����������½�Ϊ��ͼ��ڼ���
			// ���������������ƶ�����֤����㲻ȱʧ
			twe = (short) (tws + gw / PIXEL_WIDTH_PER_TILE + EXTEND_RIGHT);
			if(the > info.getWidth())
				the = info.getWidth();
			// �����������½�Ϊ��ͼ��ڼ���
			// �����������������죬��֤����㲻ȱʧ
			the = (short) (ths + gh / PIXEL_HEIGHT_PER_TILE + EXTEND_BOTTOM);
			if(the > info.getHeight())
				the = info.getHeight();
	
			// ����׼������
			pws = (short) (x - (gw / PIXEL_WIDTH_PER_TILE - 1));
			if (pws < 0)
				pws = 0;
			phs = (short) (y - (gh / PIXEL_HEIGHT_PER_TILE - 1));
			if (phs < 0)
				phs = 0;
			pwe = (short) (tws + gw / PIXEL_WIDTH_PER_TILE * 2);
			if (pwe > info.getWidth())
				pwe = info.getWidth();
			phe = (short) (ths + gh / PIXEL_HEIGHT_PER_TILE * 2);
			if (phe > info.getHeight())
				phe = info.getHeight();
	
			// �����޷����ڻ����������С�����������������ʼλ������Ӧ���������ƶ���һ�񣬻�����ֹ����ҲҪ��Ӧ������
			if ((gw / PIXEL_WIDTH_PER_TILE - 1) % 2 != 0)
				twe -= 1;
			if ((gh / PIXEL_HEIGHT_PER_TILE - 1) % 2 != 0)
				the -= 1;
		}
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int offsetX() {
		return 0;
	}

	@Override
	public int offsetY() {
		return 0;
	}

	@Override
	public Texture content() {
		// TODO Auto-generated method stub
		return null;
	}

}

package org.jootnet.m2client.map;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jootnet.m2client.graphics.Drawable;
import org.jootnet.m2client.graphics.GraphicsContext;
import org.jootnet.m2client.map.internal.MapInfo;
import org.jootnet.m2client.map.internal.MapTileInfo;
import org.jootnet.m2client.texture.Texture;
import org.jootnet.m2client.texture.internal.Textures;

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
	
	private Texture mapTex = null;
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
			moved = false;
		}
		if(mapTex == null || mapTex.getWidth() != ctx.getWidth() || mapTex.getHeight() != ctx.getHeight())
			mapTex = new Texture(new byte[ctx.getWidth() * ctx.getHeight() * 3], (short)ctx.getWidth(), (short)ctx.getHeight());
		
		// ���ƣ������뻺��
		List<Integer> tileIdx = new ArrayList<Integer>();
		List<Integer> smTileIdx = new ArrayList<Integer>();
		List<Integer> obj0Idx = new ArrayList<Integer>();
		List<Integer> obj2Idx = new ArrayList<Integer>();
		List<Integer> obj3Idx = new ArrayList<Integer>();
		List<Integer> obj4Idx = new ArrayList<Integer>();
		List<Integer> obj5Idx = new ArrayList<Integer>();
		List<Integer> obj6Idx = new ArrayList<Integer>();
		List<Integer> obj7Idx = new ArrayList<Integer>();
		List<Integer> obj8Idx = new ArrayList<Integer>();
		List<Integer> obj9Idx = new ArrayList<Integer>();
		List<Integer> obj10Idx = new ArrayList<Integer>();
		List<Integer> obj11Idx = new ArrayList<Integer>();
		List<Integer> obj12Idx = new ArrayList<Integer>();
		List<Integer> obj13Idx = new ArrayList<Integer>();
		List<Integer> obj14Idx = new ArrayList<Integer>();
		List<Integer> obj15Idx = new ArrayList<Integer>();
		// ���ڵ�ͼ���ݣ�������Ƶĵ�һ��Ϊ����������ש������ʾ���˴����������������ƣ���֤���ש�Ͷ�̬��ͼ/���ߵ���ȷ����
		int left = tws - EXTEND_LEFT;
		if(left < 0)
			left = 0;
		for(int w = left; w < twe; ++w) {
			for (int h = ths; h < the; ++h) {
				MapTileInfo mti = info.getTiles()[w][h];
				// �������Ͻ�x
				int cpx = (int) (px + (w - tws) * PIXEL_WIDTH_PER_TILE);
				// �������Ͻ�y
				int cpy = (int) (py + (h - ths) * PIXEL_HEIGHT_PER_TILE);
				if (mti.isHasBng()) {
					Texture tex = Textures.getTextureFromCache("Tiles", mti.getBngImgIdx());
					if(tex == null) {
						tileIdx.add((int) mti.getBngImgIdx());
					} else {
						mapTex.blendNormal(tex, new Point(cpx, cpy), 1);
					}
				}
				if (mti.isHasMid()) {
					Texture tex = Textures.getTextureFromCache("SmTiles", mti.getBngImgIdx());
					if(tex == null) {
						smTileIdx.add((int) mti.getBngImgIdx());
					} else {
						mapTex.blendNormal(tex, new Point(cpx, cpy), 1);
					}
				}
			}
		}
		// �������ש���ٻ��ƶ����
		// TODO ����̬��ͼ������������ϲ㾫��
		for(int w = left; w < twe; ++w) {
			for (int h = ths; h < the; ++h) {
				MapTileInfo mti = info.getTiles()[w][h];
				// �������Ͻ�x
				int cpx = (int) (px + (w - tws) * PIXEL_WIDTH_PER_TILE);
				// �������Ͻ�y
				int cpy = (int) (py + (h - ths) * PIXEL_HEIGHT_PER_TILE);
				if (mti.isHasAni()) {
					int frame = mti.getAniFrame();
					int ati = (ctx.getTickFrames() - 1) / (ctx.getMaxFps() / frame);
					if(ati < 0) ati = 0;
					if(ati >= frame) ati = frame - 1;
					String objFileName = "Objects";
					if(mti.getObjFileIdx() != 0)
						objFileName += mti.getObjFileIdx();
					Texture t = Textures.getTextureFromCache(objFileName, mti.getObjImgIdx() + ati);
					if(t == null) {
						switch(mti.getObjFileIdx()) {
						case 0:
							obj0Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 2:
							obj2Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 3:
							obj3Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 4:
							obj4Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 5:
							obj5Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 6:
							obj6Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 7:
							obj7Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 8:
							obj8Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 9:
							obj9Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 10:
							obj10Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 11:
							obj11Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 12:
							obj12Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 13:
							obj13Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 14:
							obj14Idx.add(mti.getObjImgIdx() + ati);
							break;
						case 15:
							obj15Idx.add(mti.getObjImgIdx() + ati);
							break;
						}
					} else {
						mapTex.blendAdd(t, new Point(cpx + t.getOffsetX(), cpy - t.getHeight() + t.getOffsetY()), 1);
					}
				} else if (mti.isHasObj()) {
					String objFileName = "Objects";
					if(mti.getObjFileIdx() != 0)
						objFileName += mti.getObjFileIdx();
					Texture t = Textures.getTextureFromCache(objFileName, mti.getObjImgIdx());
					if(t == null) {
						switch(mti.getObjFileIdx()) {
						case 0:
							obj0Idx.add((int) mti.getObjImgIdx());
							break;
						case 2:
							obj2Idx.add((int) mti.getObjImgIdx());
							break;
						case 3:
							obj3Idx.add((int) mti.getObjImgIdx());
							break;
						case 4:
							obj4Idx.add((int) mti.getObjImgIdx());
							break;
						case 5:
							obj5Idx.add((int) mti.getObjImgIdx());
							break;
						case 6:
							obj6Idx.add((int) mti.getObjImgIdx());
							break;
						case 7:
							obj7Idx.add((int) mti.getObjImgIdx());
							break;
						case 8:
							obj8Idx.add((int) mti.getObjImgIdx());
							break;
						case 9:
							obj9Idx.add((int) mti.getObjImgIdx());
							break;
						case 10:
							obj10Idx.add((int) mti.getObjImgIdx());
							break;
						case 11:
							obj11Idx.add((int) mti.getObjImgIdx());
							break;
						case 12:
							obj12Idx.add((int) mti.getObjImgIdx());
							break;
						case 13:
							obj13Idx.add((int) mti.getObjImgIdx());
							break;
						case 14:
							obj14Idx.add((int) mti.getObjImgIdx());
							break;
						case 15:
							obj15Idx.add((int) mti.getObjImgIdx());
							break;
						}
					} else {
						mapTex.blendAdd(t, new Point(cpx, cpy - t.getHeight()), 1);
					}
				}
			}
		}
		Textures.loadTextureAsync("Tiles", tileIdx);
		Textures.loadTextureAsync("SmTiles", smTileIdx);
		Textures.loadTextureAsync("Objects", obj0Idx);
		Textures.loadTextureAsync("Objects2", obj2Idx);
		Textures.loadTextureAsync("Objects3", obj3Idx);
		Textures.loadTextureAsync("Objects4", obj4Idx);
		Textures.loadTextureAsync("Objects5", obj5Idx);
		Textures.loadTextureAsync("Objects6", obj6Idx);
		Textures.loadTextureAsync("Objects7", obj7Idx);
		Textures.loadTextureAsync("Objects8", obj8Idx);
		Textures.loadTextureAsync("Objects9", obj9Idx);
		Textures.loadTextureAsync("Objects10", obj10Idx);
		Textures.loadTextureAsync("Objects11", obj11Idx);
		Textures.loadTextureAsync("Objects12", obj12Idx);
		Textures.loadTextureAsync("Objects13", obj13Idx);
		Textures.loadTextureAsync("Objects14", obj14Idx);
		Textures.loadTextureAsync("Objects15", obj15Idx);
		
		/*try {
			if(tileIdx.isEmpty() &&
					smTileIdx.isEmpty() &&
					obj0Idx.isEmpty() &&
					obj2Idx.isEmpty() &&
					obj3Idx.isEmpty())
			javax.imageio.ImageIO.write(mapTex.toBufferedImage(false), "jpg", new java.io.File("C:\\Users\\��\\Desktop\\1.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//return true;
		return tileIdx.isEmpty() &&
				smTileIdx.isEmpty() &&
				obj0Idx.isEmpty() &&
				obj2Idx.isEmpty() &&
				obj3Idx.isEmpty() &&
				obj4Idx.isEmpty() &&
				obj5Idx.isEmpty() &&
				obj6Idx.isEmpty() &&
				obj7Idx.isEmpty() &&
				obj8Idx.isEmpty() &&
				obj9Idx.isEmpty() &&
				obj10Idx.isEmpty() &&
				obj11Idx.isEmpty() &&
				obj12Idx.isEmpty() &&
				obj13Idx.isEmpty() &&
				obj14Idx.isEmpty() &&
				obj15Idx.isEmpty();
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
		return mapTex;
	}

}

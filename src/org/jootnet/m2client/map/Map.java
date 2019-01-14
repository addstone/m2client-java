package org.jootnet.m2client.map;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jootnet.m2client.graphics.Drawable;
import org.jootnet.m2client.graphics.GraphicsContext;
import org.jootnet.m2client.graphics.texture.Textures;

import com.github.jootnet.mir2.core.Texture;
import com.github.jootnet.mir2.core.image.ImageInfo;
import com.github.jootnet.mir2.core.map.MapTileInfo;

public abstract class Map implements Drawable {
	
	// 不可移动标记块儿索引
	private final static int SMTILE_WALK_N = 58;
	// 不可飞跃标记块儿索引
	private final static int SMTILE_FLY_N = 59;
	
	/**
	 * 地图磁块宽
	 * <br>
	 * 逻辑坐标点的屏幕像素宽度
	 */
	private final static byte PIXEL_WIDTH_PER_TILE = 48;
	/**
	 * 地图磁块高
	 * <br>
	 * 逻辑坐标点的屏幕像素高度
	 */
	private final static byte PIXEL_HEIGHT_PER_TILE = 32;
	/** 绘制地图时向左延伸块儿数量 */
	private final static byte EXTEND_LEFT = 50;
	/** 绘制地图时向右延伸块儿数量 */
	private final static byte EXTEND_RIGHT = 50;
	/** 绘制地图时向下延伸块儿数量 */
	private final static byte EXTEND_BOTTOM = 50;
	
	/** 地图宽度 */
	private int mw;
	/** 地图高度 */
	private int mh;
	/** 角色身处横坐标 */
	private short x;
	/** 角色身处纵坐标 */
	private short y;
	/** 地图绘制区域左上角(相对于游戏区域直角坐标系) */
	private short px;
	/** 地图绘制区域右上角(相对于游戏区域直角坐标系) */
	private short py;
	/** 地图绘制区域宽度 */
	private short gw;
	/** 地图绘制区域高度 */
	private short gh;
	/** 绘图区域左上角为地图块第几列 */
	private short tws;
	/** 绘图区域左上角为地图块第几行 */
	private short ths;
	/** 绘制区域右下角为地图块第几列 */
	private short twe;
	/** 绘制区域右下角为地图块第几行 */
	private short the;
	/**
	 * 纹理图片需要准备的坐标左上角列数
	 * <br>
	 * 对于地图绘制而要，需要先预测角色可能出现的坐标
	 * <br>
	 * 我们将绘制区域2倍大小作为预测的角色可能出现的位置
	 */
	private short pws;
	/**
	 * 纹理图片需要准备的左上角行数
	 */
	private short phs;
	/**
	 * 纹理图片需要准备的右下角列数
	 */
	private short pwe;
	/**
	 * 纹理图片需要准备的右下角行数
	 */
	private short phe;
	
	public com.github.jootnet.mir2.core.map.Map info() {
		return info;
	}
	
	private String name;
	public String name() {
		return name;
	}
	private com.github.jootnet.mir2.core.map.Map info;
	public Map(String name, com.github.jootnet.mir2.core.map.Map info) {
		this.name = name;
		this.info = info;
		// 地图宽度(像素)
		mw = info.getWidth() * PIXEL_WIDTH_PER_TILE;
		// 地图高度(像素)
		mh = info.getHeight() * PIXEL_HEIGHT_PER_TILE;
	}
	
	/**
	 * 获取角色身处横坐标
	 * 
	 * @return 横坐标
	 */
	public int roleX() {
		return x;
	}
	/**
	 * 获取角色身处纵坐标
	 * 
	 * @return 纵坐标
	 */
	public int roleY() {
		return y;
	}
	
	private boolean moved;
	/**
	 * 移动角色身处的坐标(相对于地图)
	 * 
	 * @param x
	 * 		横坐标
	 * @param y
	 * 		纵坐标
	 */
	public void move(int x, int y) {
		this.x = (short) x;
		this.y = (short) y;
		moved = true;
	}
	
	private Texture mapTex = null;
	private Texture mapBaseTex = null;
	private java.util.Map<String, List<Integer>> ilIdxs = new Hashtable<>();
	@Override
	public boolean adjust(GraphicsContext ctx) {
		if(mapBaseTex == null || mapBaseTex.getWidth() != ctx.getWidth() || mapBaseTex.getHeight() != ctx.getHeight()) {
			mapBaseTex = new Texture(new byte[ctx.getWidth() * ctx.getHeight() * 3], (short)ctx.getWidth(), (short)ctx.getHeight());
		}
		boolean baseCompleted = ilIdxs.keySet().parallelStream().filter(ilName -> ilName.toLowerCase().startsWith("tiles") || ilName.toLowerCase().startsWith("smtiles")).count() == 0; // 地图前两层不需每次重绘，使用缓冲
		if(moved) {
			// 计算绘制区域左上角坐标
			// 绘制区域左上角x
			px = (short) (ctx.getWidth() > mw ? (ctx.getWidth() - mw) / 2 : 0);
			// 绘制区域左上角y
			py = (short) (ctx.getHeight() > mh ? (ctx.getHeight() - mh) / 2 : 0);
			// 计算绘制宽度和高度
			// 绘制宽度
			gw = (short) (ctx.getWidth() > mw ? mw : ctx.getWidth());
			// 绘制高度
			gh = (short) (ctx.getHeight() > mh ? mh : ctx.getHeight());
	
			// 绘图区域左上角为地图块第几列
			tws = (short) (x - (gw / PIXEL_WIDTH_PER_TILE - 1) / 2);
			if (tws < 0)
				tws = 0;
			// 绘图区域左上角为地图块第几行
			ths = (short) (y - (gh / PIXEL_HEIGHT_PER_TILE - 1) / 2);
			if (ths < 0)
				ths = 0;
			
			// 绘制区域右下角为地图块第几列
			// 将绘制区域向右移动，保证对象层不缺失
			twe = (short) (tws + gw / PIXEL_WIDTH_PER_TILE + EXTEND_RIGHT);
			if(the > info.getWidth())
				the = info.getWidth();
			// 绘制区域右下角为地图块第几行
			// 将绘制区域向下延伸，保证对象层不缺失
			the = (short) (ths + gh / PIXEL_HEIGHT_PER_TILE + EXTEND_BOTTOM);
			if(the > info.getHeight())
				the = info.getHeight();
	
			// 纹理准备参数
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
	
			// 对于无法置于绘制区域“正中”的情况，在上面的起始位置中相应坐标向上移动了一格，绘制终止坐标也要相应的上移
			if ((gw / PIXEL_WIDTH_PER_TILE - 1) % 2 != 0)
				twe -= 1;
			if ((gh / PIXEL_HEIGHT_PER_TILE - 1) % 2 != 0)
				the -= 1;
			moved = false;
			baseCompleted = false;
		}
		
		ilIdxs.clear();
		Texture tmp_tex = Textures.getTextureFromCache("SmTiles", SMTILE_WALK_N);
		if(tmp_tex == null) {
			ilIdxs.putIfAbsent("SmTiles", new ArrayList<>());
			ilIdxs.get("SmTiles").add(SMTILE_WALK_N);
		}
		tmp_tex = Textures.getTextureFromCache("SmTiles", SMTILE_FLY_N);
		if(tmp_tex == null) {
			ilIdxs.putIfAbsent("SmTiles", new ArrayList<>());
			ilIdxs.get("SmTiles").add(SMTILE_FLY_N);
		}
		// 对于地图数据，如果绘制的第一列为奇数，则大地砖不会显示，此处将绘制区域向左移，保证大地砖和动态地图/光线等正确绘制
		int left = tws - EXTEND_LEFT;
		if(left < 0)
			left = 0;
		if(!baseCompleted) {
			for(int w = left; w < twe; ++w) {
				for (int h = ths; h < the; ++h) {
					MapTileInfo mti = info.getTiles()[w][h];
					// 绘制左上角x
					int cpx = (int) (px + (w - tws) * PIXEL_WIDTH_PER_TILE);
					// 绘制左上角y
					int cpy = (int) (py + (h - ths) * PIXEL_HEIGHT_PER_TILE);
					if (mti.isHasBng()) {
						String tileFileName = "Tiles"+(mti.getBngFileIdx()==0?"":mti.getBngFileIdx());
						Texture tex = Textures.getTextureFromCache(tileFileName, mti.getBngImgIdx());
						if(tex == null) {
							ilIdxs.putIfAbsent(tileFileName, new ArrayList<>());
							ilIdxs.get(tileFileName).add((int) mti.getBngImgIdx());
						} else {
							mapBaseTex.blendNormal(tex, cpx, cpy, 1);
						}
					}
					if (mti.isHasMid()) {
						String smTileFileName = "SmTiles"+(mti.getMidFileIdx()==0?"":mti.getMidFileIdx());
						Texture tex = Textures.getTextureFromCache(smTileFileName, mti.getMidImgIdx());
						if(tex == null) {
							ilIdxs.putIfAbsent(smTileFileName, new ArrayList<>());
							ilIdxs.get(smTileFileName).add((int) mti.getMidImgIdx());
						} else {
							mapBaseTex.blendNormal(tex, cpx, cpy, 1);
						}
					}
				}
			}
		}
		
		if(mapTex == null)
			try {
				mapTex = (Texture) mapBaseTex.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			mapBaseTex.copyTo(mapTex);
		// 绘制完地砖后再绘制对象层
		// TODO 将动态地图绘制提出到最上层精灵
		for(int w = left; w < twe; ++w) {
			for (int h = ths; h < the; ++h) {
				MapTileInfo mti = info.getTiles()[w][h];
				// 绘制左上角x
				int cpx = (int) (px + (w - tws) * PIXEL_WIDTH_PER_TILE);
				// 绘制左上角y
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
						ilIdxs.putIfAbsent(objFileName, new ArrayList<>());
						ilIdxs.get(objFileName).add(mti.getObjImgIdx() + ati);
					} else {
						ImageInfo ii = Textures.getImageInfoImmediately(objFileName, mti.getObjImgIdx() + ati);
						if(mti.isAniBlendMode())
							mapTex.blendAdd(t, cpx + ii.getOffsetX(), cpy - t.getHeight() + ii.getOffsetY() + PIXEL_HEIGHT_PER_TILE);
						else
							mapTex.blendNormalTransparent(t, cpx + ii.getOffsetX(), cpy - t.getHeight() + ii.getOffsetY() + PIXEL_HEIGHT_PER_TILE, 1, (byte)0, (byte)0, (byte)0);
					}
				} else if (mti.isHasObj()) {
					String objFileName = "Objects";
					if(mti.getObjFileIdx() != 0)
						objFileName += mti.getObjFileIdx();
					Texture t = Textures.getTextureFromCache(objFileName, mti.getObjImgIdx());
					if(t == null) {
						ilIdxs.putIfAbsent(objFileName, new ArrayList<>());
						ilIdxs.get(objFileName).add((int) mti.getObjImgIdx());
					} else {
						mapTex.blendNormalTransparent(t, cpx, cpy - t.getHeight() + PIXEL_HEIGHT_PER_TILE, 1, (byte)0, (byte)0, (byte)0);
					}
				}
			}
		}
		
		// 绘制可移动标记
		for(int w = left; w < twe; ++w) {
			for (int h = ths; h < the; ++h) {
				MapTileInfo mti = info.getTiles()[w][h];
				// 绘制左上角x
				int cpx = (int) (px + (w - tws) * PIXEL_WIDTH_PER_TILE);
				// 绘制左上角y
				int cpy = (int) (py + (h - ths) * PIXEL_HEIGHT_PER_TILE);
				if(!mti.isCanWalk()) {
					Texture tn_walk = Textures.getTextureFromCache("SmTiles", SMTILE_WALK_N);
					if(tn_walk !=null)
						mapTex.blendNormalTransparent(tn_walk, cpx, cpy, 1, (byte)0, (byte)0, (byte)0);
				}
				if(!mti.isCanFly()) {
					Texture tn_fly = Textures.getTextureFromCache("SmTiles", SMTILE_FLY_N);
					if(tn_fly !=null)
						mapTex.blendNormalTransparent(tn_fly, cpx, cpy, 1, (byte)0, (byte)0, (byte)0);
				}
			}
		}
		
		ilIdxs.entrySet().stream().forEach(kv -> Textures.loadTextureAsync(kv.getKey(), kv.getValue()));
		
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
		return mapTex;
	}

}

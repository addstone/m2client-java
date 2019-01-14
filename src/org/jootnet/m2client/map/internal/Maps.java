package org.jootnet.m2client.map.internal;

import java.io.File;

import org.jootnet.m2client.map.Map;

public final class Maps {

	static class MapImpl extends Map {

		MapImpl(String name, com.github.jootnet.mir2.core.map.Map info) {
			super(name, info);
		}

	}

	/**
	 * 获取一个地图对象
	 * 
	 * @param mapNo
	 *            地图编号<br>
	 *            即地图文件名称
	 * @param mapName 地图名称
	 * @return 解析出来的地图对象
	 */
	public static final Map get(String mapNo, String mapName) {
		String mapDir = System.getProperty("org.jootnet.m2client.map.dir", System.getProperty("user.dir"));
		String mapPath = mapDir;
		if (!mapPath.endsWith(File.separator))
			mapPath += File.separator;
		mapPath += mapNo + ".map";
		com.github.jootnet.mir2.core.map.Map mapInfo = com.github.jootnet.mir2.core.map.Maps.get(mapNo, mapPath);
		Map ret = new MapImpl(mapName, mapInfo);
		return ret;
	}

	/**
	 * 从缓存在系统的地图集合中移除特定编号的地图
	 * 
	 * @param mapNo
	 *            地图编号
	 */
	public static final void remove(String mapNo) {
		com.github.jootnet.mir2.core.map.Maps.remove(mapNo);
	}
}

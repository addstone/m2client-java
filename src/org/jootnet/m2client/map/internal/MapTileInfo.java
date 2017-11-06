package org.jootnet.m2client.map.internal;

/**
 * MapTile��������߼�����������ʽ
 * 
 * @author johness
 */
public final class MapTileInfo {

	/** ����ͼ���� */
	private short bngImgIdx;
	/** �Ƿ��б���ͼ(����Ѫ����2��ͼ�У�����ͼ��СΪ4����ͼ�飬���嵽���Ƶ�ͼʱ�������ֻ�к������궼Ϊ˫��ʱ�Ż���) */
	private boolean hasBng;
	/** �Ƿ������(վ��) */
	private boolean canWalk;
	/** ���䱳��ͼ���� */
	private short midImgIdx;
	/** �Ƿ��в���ͼ */
	private boolean hasMid;
	/** ����ͼ���� */
	private short objImgIdx;
	/** �Ƿ��ж���ͼ */
	private boolean hasObj;
	/** �Ƿ���Է�Խ */
	private boolean canFly;
	/** ������ */
	private byte doorIdx;
	/** �Ƿ����� */
	private boolean hasDoor;
	/** ��ƫ�� */
	private byte doorOffset;
	/** ���Ƿ��� */
	private boolean doorOpen;
	/** ����֡�� */
	private byte aniFrame;
	/** �Ƿ��ж��� */
	private boolean hasAni;
	/** ������֡�� */
	private byte aniTick;
	/** ��Դ�ļ����� */
	private byte objFileIdx;
	/** ���� */
	private byte light;
	
	MapTileInfo() { }

	/** ��ȡ����ͼ���� */
	public short getBngImgIdx() {
		return bngImgIdx;
	}
	/** ���ñ���ͼ���� */
	void setBngImgIdx(short bngImgIdx) {
		this.bngImgIdx = bngImgIdx;
	}
	/** ��ȡ�õ�ͼ���Ƿ��б���ͼ */
	public boolean isHasBng() {
		return hasBng;
	}
	/** ���øõ�ͼ���Ƿ��б���ͼ */
	void setHasBng(boolean hasBng) {
		this.hasBng = hasBng;
	}
	/** ��ȡ�õ�ͼ���Ƿ����վ�����߹� */
	public boolean isCanWalk() {
		return canWalk;
	}
	/** ���øõ�ͼ���Ƿ����վ�����߹� */
	void setCanWalk(boolean canWalk) {
		this.canWalk = canWalk;
	}
	/** ��ȡ����ͼ���� */
	public short getMidImgIdx() {
		return midImgIdx;
	}
	/** ���ò���ͼ���� */
	void setMidImgIdx(short midImgIdx) {
		this.midImgIdx = midImgIdx;
	}
	/** ��ȡ�õ�ͼ���Ƿ��в���ͼ */
	public boolean isHasMid() {
		return hasMid;
	}
	/** ���øõ�ͼ���Ƿ��в���ͼ */
	void setHasMid(boolean hasMid) {
		this.hasMid = hasMid;
	}
	/** ��ȡ����ͼ���� */
	public short getObjImgIdx() {
		return objImgIdx;
	}
	/** ���ö���ͼ���� */
	void setObjImgIdx(short objImgIdx) {
		this.objImgIdx = objImgIdx;
	}
	/** ��ȡ�õ�ͼ���Ƿ��ж���ͼ */
	public boolean isHasObj() {
		return hasObj;
	}
	/** ���øõ�ͼ���Ƿ��ж���ͼ */
	void setHasObj(boolean hasObj) {
		this.hasObj = hasObj;
	}
	/** ��ȡ�õ�ͼ���Ƿ���Է�Խ */
	public boolean isCanFly() {
		return canFly;
	}
	/** ���øõ�ͼ���Ƿ���Է�Խ */
	void setCanFly(boolean canFly) {
		this.canFly = canFly;
	}
	/** ��ȡ������ */
	public byte getDoorIdx() {
		return doorIdx;
	}
	/** ���������� */
	void setDoorIdx(byte doorIdx) {
		this.doorIdx = doorIdx;
	}
	/** ��ȡ�õ�ͼ���Ƿ����� */
	public boolean isHasDoor() {
		return hasDoor;
	}
	/** ���øõ�ͼ���Ƿ����� */
	void setHasDoor(boolean hasDoor) {
		this.hasDoor = hasDoor;
	}
	/** ��ȡ��ƫ�� */
	public byte getDoorOffset() {
		return doorOffset;
	}
	/** ������ƫ�� */
	void setDoorOffset(byte doorOffset) {
		this.doorOffset = doorOffset;
	}
	/** ��ȡ�õ�ͼ�����Ƿ�� */
	public boolean isDoorOpen() {
		return doorOpen;
	}
	/** ���øõ�ͼ�����Ƿ�� */
	void setDoorOpen(boolean doorOpen) {
		this.doorOpen = doorOpen;
	}
	/** ��ȡ����֡�� */
	public byte getAniFrame() {
		return aniFrame;
	}
	/** ���ö���֡�� */
	void setAniFrame(byte aniFrame) {
		this.aniFrame = aniFrame;
	}
	/** ��ȡ�õ�ͼ���Ƿ��ж��� */
	public boolean isHasAni() {
		return hasAni;
	}
	/** ���øõ�ͼ���Ƿ��ж��� */
	void setHasAni(boolean hasAni) {
		this.hasAni = hasAni;
	}
	/** ��ȡ������֡�� */
	public byte getAniTick() {
		return aniTick;
	}
	/** ���ö�����֡�� */
	void setAniTick(byte aniTick) {
		this.aniTick = aniTick;
	}
	/** ��ȡ��Դ�ļ����� */
	public byte getObjFileIdx() {
		return objFileIdx;
	}
	/** ������Դ�ļ����� */
	void setObjFileIdx(byte objFileIdx) {
		this.objFileIdx = objFileIdx;
	}
	/** ��ȡ���� */
	public byte getLight() {
		return light;
	}
	/** �������� */
	void setLight(byte light) {
		this.light = light;
	}
}

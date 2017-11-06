package org.jootnet.m2client.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * �ļ������ƶ�ȡ��
 * <br>
 * ����̳�{@link RandomAccessFile}�����һϵ����<b>LE</b>��β�ĺ�������ȡ���ֽ���<b>Little-Endian</b>��ʽ����
 * 
 * @author johness
 */
public final class BinaryReader extends RandomAccessFile {

	public BinaryReader(File file, String mode) throws FileNotFoundException {
		super(file, mode);
	}
	
	/**
	 * �����ж�ȡһ������������
	 * <br>
	 * ��λ����ǰ�ƽ������ֽ�
	 * 
	 * @return һ�������Σ���Little-Endian��ʽ����
	 * @throws IOException �ļ��Ѵﵽĩβ
	 */
	public final short readShortLE() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch2 << 8) + (ch1 << 0));
    }
	
	/**
	 * �����ж�ȡһ���޷��Ŷ���������
	 * <br>
	 * ��λ����ǰ�ƽ������ֽ�
	 * 
	 * @return һ���޷��Ŷ����Σ���Little-Endian��ʽ����
	 * @throws IOException �ļ��Ѵﵽĩβ
	 */
	public final int readUnsignedShortLE() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch2 << 8) + (ch1 << 0);
    }
	
	/**
	 * �����ж�ȡһ��˫�ֽ��ַ�
	 * <br>
	 * ��λ����ǰ�ƽ������ֽ�
	 * 
	 * @return һ��˫�ֽ��ַ�����Little-Endian��ʽ����
	 * @throws IOException �ļ��Ѵﵽĩβ
	 */
	public final char readCharLE() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char)((ch2 << 8) + (ch1 << 0));
    }
	
	/**
	 * �����ж�ȡһ�����ֽ�����
	 * <br>
	 * ��λ����ǰ�ƽ��ĸ��ֽ�
	 * 
	 * @return һ�����Σ���Little-Endian��ʽ����
	 * @throws IOException �ļ��Ѵﵽĩβ
	 */
	public final int readIntLE() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        int ch3 = this.read();
        int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
    }
	
	/**
	 * �����ж�ȡһ�����ֽڳ�����
	 * <br>
	 * ��λ����ǰ�ƽ��˸��ֽ�
	 * 
	 * @return һ�������Σ���Little-Endian��ʽ����
	 * @throws IOException �ļ��Ѵﵽĩβ
	 */
	public final long readLongLE() throws IOException {
        return ((long)(readIntLE()) & 0xFFFFFFFFL) + (readIntLE() << 32);
    }
	
	/**
	 * �����ж�ȡһ�������ȸ�����
	 * <br>
	 * ��λ����ǰ�ƽ��ĸ��ֽ�
	 * 
	 * @return һ�������ȸ���������Little-Endian��ʽ����
	 * @throws IOException �ļ��Ѵﵽĩβ
	 */
	public final float readFloatLE() throws IOException {
        return Float.intBitsToFloat(readIntLE());
    }
	
	/**
	 * �����ж�ȡһ��˫���ȸ�����
	 * <br>
	 * ��λ����ǰ�ƽ��˸��ֽ�
	 * 
	 * @return һ��˫���ȸ���������Little-Endian��ʽ����
	 * @throws IOException �ļ��Ѵﵽĩβ
	 */
	public final double readDoubleLE() throws IOException {
        return Double.longBitsToDouble(readLongLE());
    }
}

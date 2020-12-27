package com.yc;

import java.util.Arrays;
import java.util.Random; // 导报

/**
 * company 源辰
 * @author navy
 * date 2020-12-26
 * email haijunzhou@hnit.edu.cn
 * javadoc -d <要保存这个api文档的目录> <要生成api文档的java源文件>
 * 如： javadoc -d .\api *.java
 */
public class ClearMine {
	private int row; // 行数
	private int col; // 列数
	private int level; // 级别
	private int mineCount = 0; // 雷的数量
	private int[][] mineMap = null; // 存储扫雷地图

	/**
	 * 构造方法
	 * @param level 你要玩的扫雷的等级，1-初级、2-中级、3-高级
	 */
	public ClearMine(int level) {
		this.level = level;
		init(); // 调用初始化方法
	}

	/**
	 * 初始化方法
	 */
	private void init() {
		switch(level) {
		case 1: 
			this.row = 9;
			this.col = 9;
			this.mineCount = 10;
			break;
		case 2:
			this.row = 16;
			this.col = 16;
			this.mineCount = 40;
			break;
		case 3:
			this.row = 16;
			this.col = 30;
			this.mineCount = 99;
			break;
		}
		mineMap = new int[row][col];
	}

	/**
	 * 布雷的方法   0b-二进制 0-八进制  0x-十六进制
	 * 0b00000001 最后一个用来表示有雷或没雷
	 * 0b00000110 倒数第2、3位表示状态  00-未打开  01-已打开  10-插旗  11-?
	 * 0b01111000 倒数第4、5、6、7位表示周围雷的数量
	 */
	private void burnMine() {
		Random rd = new Random();
		int x, y;

		for (int i = 0; i < mineCount; )  {
			x = rd.nextInt(row); // [0, row)
			y = rd.nextInt(col);

			// 判断这个位置有没有雷
			// 0b00000000
			if ( (mineMap[x][y] & 0b00000001) == 1) { // 说明有雷
				continue; // 直接进行下一次循环
			}

			// 如果没有雷，当前这个位置布上雷
			mineMap[x][y] = 0b00000001;

			// 这个位置还有上面吗？如果有，则上面的三个位置雷数+1
			if (x - 1 >= 0) { // 说明有上面  
				mineMap[x - 1][y] += 0b00001000;

				// 判断有左上角吗？
				if (y - 1 >= 0) { // 说明有左上角
					mineMap[x - 1][y - 1] += 0b1000;
				}

				// 判断有右上角吗？
				if ( y + 1 < col) { // 说明有右上角
					mineMap[x - 1][y + 1] += 0b1000;
				}
			}


			if (x + 1 < row) { // 说明有下面  
				mineMap[x + 1][y] += 0b00001000; // 正下方

				// 判断有左下角吗？
				if (y - 1 >= 0) { // 说明有左上角
					mineMap[x + 1][y - 1] += 0b1000;
				}

				// 判断有右下角吗？
				if ( y + 1 < col) { // 说明有右上角
					mineMap[x + 1][y + 1] += 0b1000;
				}
			}

			if (y - 1 >= 0) { // 说明有正左方
				mineMap[x][y - 1] += 0b1000;
			}

			if (y + 1 < col) { // 说明有正右方
				mineMap[x][y + 1] += 0b1000;
			}

			++ i; // 说明此次布雷是成功 
		}

		showMap(); // 显示地图
	}

	/**
	 * 显示地图的方法
	 */
	public void showMap() {
		System.out.println("=============================================");
		for (int[] arr : mineMap) {
			for (int num : arr) {
				if ((num & 0b1) == 1) { // 说明这个位置有雷
					System.out.print(" A ");
				} else { // 说明没有雷
					if ((num >>> 3) == 0) { // 说明这个地方是空白 >>> 无符号移位  >> 有符号移位
						System.out.print("   ");
					} else { // 说明周围有雷，则显示周围雷的数量
						System.out.print(" " + (num >>> 3) + " ");
					}
				}
			}
			System.out.println("\n");
		}
	}
	
	/**
	 * 初始化地图的方法 -> 将二维数组中的所有值赋为0
	 */
	public void initArray() {
		for (int[] arr : mineMap) {
			Arrays.fill(arr, 0);
		}
	}

	/**
	 * 重置地图的方法
	 * @param level
	 */
	public void reset(int level) {
		this.level = level;
		init(); // 修改行、列、雷数、地图大小
	}

	/**
	 * 获取地图
	 * @return
	 */
	public int[][] getMineMap() {
		burnMine(); // 先将雷埋好
		return mineMap;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getMineCount() {
		return mineCount;
	}
}
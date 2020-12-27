package com.yc.clearmine;

import java.util.Arrays;
import java.util.Random;

/**
 * @company 源辰
 * @author navy
 *
 */
public class ClearMine {
	private int row; // 行数
	private int col; // 列数
	private int level; // 等级
	private int mineCount; // 雷的数量
	private int[][] mineMap = null; // 扫雷的地图
	
	/**
	 * 初始化地图
	 * @param level 等级
	 */
	public ClearMine(int level) {
		this.level = level;
		init();
	}
	
	/**
	 * 初始化地图信息
	 */
	private void init() {
		switch (level) {
		case 1 : 
			this.row = 9;
			this.col = 9;
			this.mineCount = 10;
			break;
		case 2 : 
			this.row = 16;
			this.col = 16;
			this.mineCount = 40;
			break;
		case 3 : 
			this.row = 16;
			this.col = 30;
			this.mineCount = 99;
			break;
		} 
		
		mineMap = new int[row][col];
	}
	
	/**
	 * 布雷的方法
	 * 0b00000000
	 * 0b00000001 最后一位表示有雷或没雷   1说明有雷  0说明没有雷
	 * 0b00000110 倒数第2、3位表示格子的状态   00 表示没有打开   01表示打开  10表示插旗   11表示?
	 * 0b01111000 表示雷的数量
	 */
	private void burnMine() {
		Random rd = new Random();
		int x, y;
		for (int i = 0; i < mineCount; ) {
			x = rd.nextInt(row); // [0, row)
			y = rd.nextInt(col);
			
			// 判断这个位置有没有雷
			if ((mineMap[x][y] & 0b00000001) == 0) { // 说明此处没有雷
				// 先在地图上表明这个位置有雷
				mineMap[x][y] = 0b00000001;
				
				// 将这个雷周围的8个格子中的数都加1
				if (x - 1 >= 0) { // 说明是这个格子的上面
					mineMap[x - 1][y] += 0b00001000; // 正上面加1
					
					if (y - 1 >= 0) { // 说明是左上方
						mineMap[x - 1][y - 1] += 0b00001000;
					}
					
					if (y + 1 < col) { // 说明是右上面
						mineMap[x - 1][y + 1] += 0b00001000;
					}
				}
				
				if (x + 1 < row) { // 说明是下方
					mineMap[x + 1][y] += 0b00001000; // 正下方加1
					
					if (y - 1 >= 0) { // 说明是左下方
						mineMap[x + 1][y - 1] += 0b00001000;
					}
					
					if (y + 1 < col) { // 说明是右下面
						mineMap[x + 1][y + 1] += 0b00001000;
					}
				}
				
				if (y - 1 >= 0) { // 说明是正左边
					mineMap[x][y - 1] += 0b00001000; // 左边
				}
				
				if (y + 1 < col) { // 说明是正右边
					mineMap[x][y+ 1] += 0b00001000; // 右边
				}
				
				// 说明已经成功埋了一个颗雷
				i ++;
			}
		}
		
		// showMap();
	}
	
	/**
	 * 显示地图的方法
	 */
	public void showMap() {
		// 增强型for循环 for (要迭代的集合中的数据类型  迭代变量 ： 要迭代的集合  )
		for (int[] arr : mineMap) {
			for (int num : arr) {
				// 先判断这个位置是有雷还是没雷
				if ((num & 0b00000001) == 1) { // 说明有雷
					System.out.print(" A ");
				} else {
					if ((num >>> 3) == 0) { // 说明是空白
						System.out.print("   ");
					} else {
						System.out.print(" " + (num >>> 3) + " ");
					}
				}
			}
			System.out.println();
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
	
	/**
	 * 初始化地图的方法 -> 将二维数组中的所有值赋为0
	 */
	public void initArray() {
		for (int[] arr : mineMap) {
			Arrays.fill(arr, 0);
		}
	}

	public int[][] getMineMap() {
		burnMine(); // 先将雷埋好
		return mineMap;
	}
}

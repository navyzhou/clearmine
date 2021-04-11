package com.yc;

import java.util.Random;

/**
 * 扫雷
 * 源辰信息
 * @author navy
 * @date 2021年3月29日
 * Email: haijunzhou@hnit.edu.cn
 */
public class ClearMine {
	int row; // 行数
	int col; // 列数
	int level; // 等级
	int mineCount = 0; // 雷的数量
	int[][] mineMap = null; // 扫雷地图
	
	/**
	 * 构造方法，创建对象的时候需要调用
	 * 创建对象时，用户必须给定玩的等级
	 */
	public ClearMine(int level) {
		this.level = level;
		init(); // 初始化游戏棋盘
	}
	
	/**
	 * 初始化方法
	 */
	public void init() {
		// 根据用户给定的级别，设置行数、列数、雷数以及创建这个二位数组
		switch(level) {
		case 1: 
			row = 9;
			col = 9;
			mineCount = 10;
			break;
		case 2:
			row = 16;
			col = 16;
			mineCount = 40;
			break;
		case 3:
			row = 16;
			col = 30;
			mineCount = 99;
			break;
		}
		
		mineMap = new int[row][col];
	}
	
	/**
	 * 布雷的方法，循环布雷，每成功布雷一个，计数器+1，直到所有的雷都已经不完
	 * 随机生成两个数，构成一个点的坐标，如果这个坐标上面没有雷，则将雷放在这个位置，然后这个雷周围的8个位置上的数+1
	 * 如果这个位置已经有了，则继续随机生成下一个坐标，直到所有的雷不完。
	 * 问题：每个位置多有三个信息要存
	 * 	1、这个位置有雷还是没雷
	 * 	2、这个位置周围有没有雷，如果有，有几个雷
	 * 	3、这个位置的打开状态：未打开、已打开、插旗、问号
	 * 解决方法：采用byte存储 一个byte8位   0000 0000
	 * 0b0000 0001 最后一个表示有雷或没雷  有雷最后一个位1，没有雷最后一位为0  0b0000 0001 ^ 0b0000 0001 = 1
	 * 0b0000 0110 倒数第2、第3位表示打开状态   00-未打开  01-已打开  10-插旗   11-问号  0b0000 0100 ^ 0b0000 0110 = 0b0000 0110
	 * 0b0111 1000 倒数第4、5、6、7为表示周围雷的数量 0b0011 1000 >>> 0b0000 0111 = 7
	 */
	public void burnMine() {
		Random rd = new Random();
		int x, y;
		for (int i = 0; i < mineCount; ) {
			x = rd.nextInt(row);
			y = rd.nextInt(col);
			
			// 判断这个位置有没有雷      如果高位都是0，可以省略
			if ((mineMap[x][y] & 0b1) == 1) { // 说明有雷
				continue; // 则不执行后面的代码，直接进行下一次循环
			}
			
			// 说明这个位置没有雷，则在这个位置上布上雷
			mineMap[x][y] = 0b1;
			
			// 判断这个位置周围八个方向是否可以延伸，如果可以，则需要+1    
			if (x - 1 >= 0) { // 说明有上方   
				mineMap[x - 1][y] += 0b1000; // 正上方雷数+1
				
				if (y - 1 >= 0) { // 说明有左上方
					mineMap[x - 1][y - 1] += 0b1000;  // 左上方雷数+1
				}
				
				if (y + 1 < col) { // 说明有右上方
					mineMap[x - 1][y + 1] += 0b1000;  // 右上方雷数+1
				}
			}
			
			if (x + 1 < row) { // 说明有下方   
				mineMap[x + 1][y] += 0b1000; // 正下方雷数+1
				
				if (y - 1 >= 0) { // 说明有左下方
					mineMap[x + 1][y - 1] += 0b1000;  // 左下方雷数+1
				}
				
				if (y + 1 < col) { // 说明有右下方
					mineMap[x + 1][y + 1] += 0b1000;  // 右下方雷数+1
				}
			}
			
			if (y - 1 >= 0) { // 说明有正左边
				mineMap[x][y - 1] += 0b1000; // 正左方雷数+1
			}
			
			if (y + 1 < col) { // 说明有正右边
				mineMap[x][y + 1] += 0b1000; // 正右方雷数+1
			}
			
			++ i; // 已布雷数 + 1
		}
		
		showMap();
	}
	
	/**
	 * 获取扫雷地图的方法  - 已经布雷的地图
	 * @return
	 */
	public int[][] getMineMap() {
		burnMine(); // 先调用布雷方法布雷
		return mineMap;
	}
	
	/**
	 * 初始化数组
	 */
	public void initArray() {
		for (int i = 0; i < row; i ++) {
			for (int j = 0; j < col; j ++) {
				mineMap[i][j] = 0;
			}
		}
	}
	
	/**
	 * 重置地图的方法
	 * @param level
	 */
	public void reset(int level) {
		this.level = level;
		init(); // 初始化扫雷地图
	}
	
	public void showMap() {
		for (int[] arr : mineMap) {
			for (int num : arr) {
				if ((num & 0b1) == 1) { // 说明这个地方有雷
					System.out.print("A   ");
				} else { // 说明没有雷
					// 周围有雷吗？
					if ((num >>> 3) == 0) { // 说明周围也没有用雷，即当前这个位置是空白
						System.out.print("    ");
					} else {
						System.out.print((num >>> 3) + "   ");
					}
				}
			}
			System.out.println("\n");
		}
	}
	
}

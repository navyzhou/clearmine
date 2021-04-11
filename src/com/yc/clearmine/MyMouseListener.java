package com.yc.clearmine;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.yc.clearmine.util.PlayMusicUtil;

/**
 * 事件监听
 * 源辰信息
 * @author navy
 * @date 2021年4月11日
 * Email haijunzhou@hnit.edu.cn
 */
public class MyMouseListener extends MouseAdapter{
	int size = 40;
	ClearMineUI clearMineUI;
	
	public MyMouseListener(int size, ClearMineUI clearMineUI) {
		this.size = size;
		this.clearMineUI = clearMineUI;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { // 当在鼠标在界面上点击时，会自动运行这个方法
		if (clearMineUI.start) {
			clearMineUI.timeTask();
			clearMineUI.start = false;
		}
		PlayMusicUtil.playStart();
		int x = e.getY() / size;
		int y = e.getX() / size; 
		
		// 说明越界了
		if (x >= clearMineUI.row || y >= clearMineUI.col || x < 0 || y < 0) {
			return;
		}
		
		int value = clearMineUI.mineMap[x][y]; // 获取用户点击的这个位置的值
		
		if (e.getClickCount() == 2) { // 说明是双击
			int count = value >>> 3; // 得到数量
			
			if (count == 0) { // 说明这个地方是空白
				return;
			}
			
			int total = getMineCount(x, y);
			if (total == -1) { // 说明是中间炸了
				clearMineUI.isBomb = true;
				// clearMineUI.repaint();
				// return;
			} else {
				if (total != count) { // 如果插旗的数量跟实际雷数不相等
					return;
				}
				
				// 如果相等，则判断周围8个位置是否已经打开
				openRound(x, y); // 打开这个位置的周围
			}
		} else { // 说明是单机
			if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) { // 说明是左击
				// 如果这个位置已经打开，则不管
				if ((value & 0b110) == 0b010) { // 说明已经打开
					return;
				}
				
				// 判断这个位置有没有插旗，如果有插旗，则要取消插旗
				if ((value & 0b110) == 0b100) { // 说明已经插旗     10  00
					clearMineUI.mineMap[x][y] = value & 0b11111001;
				} else if ((value & 0b1) == 0b1) { // 说明这个地方是地雷
					clearMineUI.isBomb = true; // 说明炸啦
				} else { // 要判断这个地方是否是空白，如果是空白，则要向周围弹开，如果不是，则显示   00 01
					// clearMineUI.mineMap[x][y] += 0b10;
					openWhiteOption(x, y);
				}
			} else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) { // 说明是右击
				// 如果已经打开，则不能右击
				if ((value & 0b110) == 0b010) {
					return;
				}
				
				// 如果没有打开，第一次是插旗，第二次是?，第三次是取消
				if ((value & 0b110) == 0b100) { // 说明此处已经插旗，那么变成?  100 -> 110  010
					clearMineUI.mineMap[x][y] += 0b010; // 变成?
				} else if ((value & 0b110) == 0b110) { // 说明此处是?，则取消  // 110 -> 000
					clearMineUI.mineMap[x][y] = value & 0b11111001;
				} else {
					// 否则就是插旗
					if (clearMineUI.mineCount <= 0) {
						return;
					}
					clearMineUI.mineMap[x][y] += 0b100; // 00 -> 10
				}
			}
		}
		// 重绘地图
		clearMineUI.repaint();
		
		if (clearMineUI.isBomb) { // 如果炸了
			gameOver(1);
		} else if (checkGameOver()){ // 如果游戏正常结束
			gameOver(2);
		}
	}
	
	/**
	 * 用来计算周围成功标识的雷的数量，即成功插旗的数量
	 * @param x
	 * @param y
	 * @return
	 */
	private int getMineCount(int x, int y) {
		int total = 0;
		int temp = 0;
		
		if (x - 1 >= 0) { // 向上
			temp = checkFlag(x - 1, y);
			if (temp == -1) {
				return -1;
			}
			total += temp;
			
			if (y - 1 >= 0) { // 左上角
				temp = checkFlag(x - 1, y - 1);
				if (temp == -1) {
					return -1;
				}
				total += temp;
			}
			
			if (y + 1 < clearMineUI.col) { // 右上角
				temp = checkFlag(x - 1, y + 1);
				if (temp == -1) {
					return -1;
				}
				total += temp;
			}
		}
		
		if (x + 1 < clearMineUI.row ) { // 向下
			temp = checkFlag(x + 1, y);
			if (temp == -1) {
				return -1;
			}
			total += temp;
			
			if (y - 1 >= 0) { // 左上角
				temp = checkFlag(x + 1, y - 1);
				if (temp == -1) {
					return -1;
				}
				total += temp;
			}
			
			if (y + 1 < clearMineUI.col) { // 右上角
				temp = checkFlag(x + 1, y + 1);
				if (temp == -1) {
					return -1;
				}
				total += temp;
			}
		}
		
		if (y - 1 >= 0) { // 左边
			temp = checkFlag(x, y - 1);
			if (temp == -1) {
				return -1;
			}
			total += temp;
		}
		
		if (y + 1 < clearMineUI.col) { // 右边
			temp = checkFlag(x, y + 1);
			if (temp == -1) {
				return -1;
			}
			total += temp;
		}
		
		return total;
	}
	
	/**
	 * 双击打开周围的方法
	 * @param x
	 * @param y
	 */
	private void openRound(int x, int y) {
		if (x - 1 >= 0) {
			openPosition(x - 1, y); // 正上方
			
			if (y - 1 >= 0) {
				openPosition(x - 1, y - 1);
			}
			
			if (y + 1 < clearMineUI.col) {
				openPosition(x - 1, y + 1);
			}
		}
		
		if (x + 1 < clearMineUI.row) {
			openPosition(x + 1, y);
			
			if (y - 1 >= 0) {
				openPosition(x + 1, y - 1);
			}
			
			if (y + 1 < clearMineUI.col) {
				openPosition(x + 1, y + 1);
			}
		}
		
		if (y - 1 >= 0) {
			openPosition(x, y - 1);
		}
		
		if (y + 1 < clearMineUI.col) {
			openPosition(x, y + 1);
		}
	}
	
	/**
	 * 打开指定位置的方法
	 * @param x
	 * @param y
	 */
	private void openPosition(int x, int y) {
		int state = clearMineUI.mineMap[x][y];
		
		// 有雷                                                                     插旗                                                                      已经打开
		if ((state & 0b1) == 1 || (state & 0b110) == 0b100 || (state & 0b110) == 0b010) {
			return;
		}
		
		if ((state >>>3) == 0) {
			openWhiteOption(x, y);
			return;
		}
		
		clearMineUI.mineMap[x][y] += 0b10; // 打开当前位置
	}
	
	/**
	 * 检查这个位置是否正确插旗
	 * @param x
	 * @param y
	 * @return
	 */
	private int checkFlag(int x, int y) {
		// 说明这个地方有雷                                                                                                              
		if ((clearMineUI.mineMap[x][y] & 0b110) == 0b100) { 
			if ((clearMineUI.mineMap[x][y] & 0b1) == 0b1) { // 说明这个地方已经插旗
				return 1;
			} else {
				return -1;
			}
		}
		return 0;
	}
	
	/**
	 * 空白判断及打开
	 */
	private void openWhiteOption(int x, int y) {
		int state = clearMineUI.mineMap[x][y]; // 获取用户点击的这个位置的值
		
		// 如果这个地方是雷或者已经插旗或者已经打开，则不往下走
		if ((state & 0b1) == 1 || (state & 0b110) == 0b100 || (state & 0b110) == 0b010) {
			return;
		}
		
		// 说明当前这位置是空白或者数字，则可以打开
		clearMineUI.mineMap[x][y] += 0b010;  // 0b000  + 0b010 = 0b010
		
		// 如果当前位置是空白，则需要继续判断周围8个位置是否空白或数字，如果是空白则要继续打开，如果是数字打开就行
		if ((state >>> 3) == 0) { // 说明此处是空白，则需要继续打开周围8个位置
			// 向上
			if (x - 1 >= 0) { // 说明可以向上走
				openWhiteOption(x - 1, y);
				
				if (y - 1 >= 0) { // 可以往左上角
					openWhiteOption(x - 1, y - 1);
				}
				
				if (y + 1 < clearMineUI.col) { // 说明可以往右上角
					openWhiteOption(x - 1, y + 1);
				}
			}
			
			// 向下
			if (x + 1 < clearMineUI.row) { // 说明可以往下走
				openWhiteOption(x + 1, y);
				
				if (y - 1 >= 0) { // 说明能往左下角走
					openWhiteOption(x + 1, y - 1);
				}
				
				if (y + 1 < clearMineUI.col) { // 说明能往右下角走
					openWhiteOption(x + 1, y + 1);
				}
			}
			
			// 向左
			if (y - 1 >= 0) {
				openWhiteOption(x, y - 1);
			}
			
			// 向右
			if (y + 1 < clearMineUI.col) {
				openWhiteOption(x, y + 1);
			}
		}
	}
	
	/**
	 * 统计插旗的数量
	 */
	private void totalFlag() {
		int count = 0;
		for (int[] arr : clearMineUI.mineMap) {
			for (int num : arr) {
				if ((num & 0b110) == 0b100) { // 说这个地方已经插旗了
					++ count;
				}
			}
		}
		clearMineUI.mineCount = clearMineUI.clearMineCount - count;
		clearMineUI.label_1.setText(" 0" + clearMineUI.mineCount + " ");
	}
	
	/**
	 * 判断游戏结束的方法
	 * 除雷以外，其它的所有都已经打开
	 * @return
	 */
	private boolean checkGameOver() {
		totalFlag(); // 先统计插旗数
		for (int[] arr : clearMineUI.mineMap) {
			for (int num : arr) {
				if ((num & 0b1) == 0b0 && (num & 0b110) != 0b010) { // 说明这个地方不是雷并且你的状态不是  01，即状态不是打开
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 游戏结束的方法
	 * @param flag
	 */
	private void gameOver(int flag) {
		int index = 0;
		if (flag == 1) { // 说明是炸死的
			PlayMusicUtil.playOver();
			clearMineUI.label_2.setIcon(new ImageIcon(ClearMineUI.class.getResource("/images/cry.png")));
			index = JOptionPane.showConfirmDialog(clearMineUI, "对不起，您已经被炸死了...\n是否再来一局???", "游戏结束", JOptionPane.YES_NO_OPTION);
		} else { // 说明是正常结束
			PlayMusicUtil.playWin();
			index = JOptionPane.showConfirmDialog(clearMineUI, "恭喜您，赢了...\n是否再来一局???", "游戏结束", JOptionPane.YES_NO_OPTION);
		}
		
		if (index == 1) { // 说明用户选择的是否
			System.exit(0);
		}
		
		// 重新开始游戏
		clearMineUI.again();
	}
}

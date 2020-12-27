package com.yc.clearmine;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

public class MyMouseListener extends MouseAdapter{
	private int size = 20;
	private MainGameUI game;

	public MyMouseListener(int size,MainGameUI game) {
		this.size = size;
		this.game = game;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//super.mouseClicked(e);
		int x = (e.getY() - 50) / size;
		int y = (e.getX() - 3) / size;
		
		if (x >= game.row || y >= game.col || x < 0 || y < 0) { // 说明用户点击的范围不是棋盘
			return;
		}

		//showMap();

		if (e.getClickCount() == 2) {
			// 弹开周围的八个位置，如果是雷不打开，如果是空白继续打开
			int count = (game.mineMap[x][y] >>> 3); // 获取双击的这个位置的地图数
			if (count == 0) { // 说你是空白区域
				return;
			}
			
			int total = getMineCount(x, y);
			
			if (total == -1) { // 说明插旗有错误的情况，则不管
				return;
			}
			
			if (total != count) { // 说明周围的雷没有被成功标识
				return;
			}
			
			// 否则，需要判断这个位置的周围8个是否打开，如果没有则打开(插旗的不管)，如果是空白，则继续弹开
			openRound(x, y);
			
		} else {
			if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {  // 左击
				// 如果这个位置已经打开，则不管
				if ((game.mineMap[x][y] & 0b00000110) == 0b00000010) { // 说明是打开了
					return; // 直接返回
				}

				// 先判断有没有插旗，如果有插旗，则取消插旗
				if ((game.mineMap[x][y] & 0b00000110)== 0b00000100) { // 说明此处已经插旗
					game.mineMap[x][y] = game.mineMap[x][y] & 0b01111001; // 则去掉旗子
				} else if ((game.mineMap[x][y] & 0b00000001)== 0b00000001) { // 说明是地雷
					game.isBomb = true; // 说明炸了
				} else {// 那么就要判断当前位置是不是空白，如果是则要弹开
					// game.mineMap[x][y] += 0b10;
					openWhiteOption(x, y);
				}

			} else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
				if ((game.mineMap[x][y] & 0b00000110) == 0b00000010) { // 说明是打开了
					return; // 直接返回
				}
				
				// 右击第一次插旗  第二次变成? 第三次取消。如果此处已经插旗则点击无效，如果是?则可以直接打开
				// 说明要插旗
				if ((game.mineMap[x][y] & 0b00000110)== 0b00000100) { // 说明此处已经插旗，则不管
					return;
				}
				game.mineMap[x][y] += 0b00000100; // 则插旗
			}
		} 

		game.repaint(); // 重绘
		
		if (game.isBomb) {
			gameOver(1);
		} else if(isGameOver()){
			gameOver(2);
		}
	}

	/**
	 * 打开空白
	 * @param x
	 * @param y
	 */
	private void openWhiteOption(int x, int y) {
		int state = game.mineMap[x][y]; // 获取当前位置的值

		//            有雷                                                                      已经打开                                                                          已经插旗
		if ((state & 0b1) == 0b1 || (state & 0b110) == 0b10 || (state & 0b110) == 0b100) {
			return;
		}

		game.mineMap[x][y] += 0b10; // 当前这个位置需要打开
		if ((state >>> 3) == 0) { // 说明当前是空白，则周围要弹开
			if (x - 1 >= 0) { // 向上
				openWhiteOption(x - 1, y); // 正上方

				if (y - 1 >= 0) { // 左上方
					openWhiteOption(x - 1, y - 1);
				}

				if (y + 1 < game.col) { // 右上方
					openWhiteOption(x - 1, y + 1);
				}
			}

			if (x + 1 < game.row) { // 向下
				openWhiteOption(x + 1, y); // 正下方

				if (y - 1 >= 0) { // 左下方
					openWhiteOption(x + 1, y - 1);
				}

				if (y + 1 < game.col) { // 右下方
					openWhiteOption(x + 1, y + 1);
				}
			}

			if (y - 1 >= 0) { // 左边
				openWhiteOption(x, y - 1);
			}

			if (y + 1 < game.col) { // 右边
				openWhiteOption(x, y + 1);
			}
		}
	}

	/**
	 * 统计点击位置周围的插旗是否已经完成，即周围的雷正确的找出了多少个
	 * @param x
	 * @param y
	 * @return 如果返回-1，说明插旗有误，否则返回插旗正确的数量
	 */
	private int getMineCount(int x, int y) {
		int total = 0;
		int temp = 0;
		
		if (x - 1 >= 0) { // 向上
			// 判断这个位置有没有插旗
			temp = checkFlag(x - 1, y);
			if ( temp == -1) {
				return -1;
			}
			total += temp;
			
			if (y - 1 >= 0) { // 左上方
				temp = checkFlag(x - 1, y - 1);
				if ( temp == -1) {
					return -1;
				}
				total += temp;
			}

			if (y + 1 < game.col) { // 右上方
				temp = checkFlag(x - 1, y + 1);
				if ( temp == -1) {
					return -1;
				}
				total += temp;
			}
		}

		if (x + 1 < game.row) { // 向下
			temp = checkFlag(x + 1, y);
			if ( temp == -1) {
				return -1;
			}
			total += temp;

			if (y - 1 >= 0) { // 左下方
				temp = checkFlag(x + 1, y - 1);
				if ( temp == -1) {
					return -1;
				}
				total += temp;
			}

			if (y + 1 < game.col) { // 右下方
				temp = checkFlag(x + 1, y + 1);
				if ( temp == -1) {
					return -1;
				}
				total += temp;
			}
		}

		if (y - 1 >= 0) { // 左边
			temp = checkFlag(x, y - 1);
			if ( temp == -1) {
				return -1;
			}
			total += temp;
		}

		if (y + 1 < game.col) { // 右边
			temp = checkFlag(x, y + 1);
			if ( temp == -1) {
				return -1;
			}
			total += temp;
		}
		return total;
	}
	
	/**
	 * 检查对应位置是否已经插旗正确
	 * @param x
	 * @param y
	 * @return 如果是0说明没有插旗，如果是1说明成功插旗，如果是-1说明插旗错误
	 */
	private int checkFlag(int x, int y) {
		if ((game.mineMap[x][y] & 0b00000110) == 0b00000100) { // 说明已经插旗
			// 这个位置有雷
			if ((game.mineMap[x][y] & 0b1) == 1) { // 说明有雷，即插旗正确
				return 1;
			} else {
				return -1;
			}
		}
		return 0;
	}
	
	/**
	 * 打开周围的8个位置
	 * @param x
	 * @param y
	 */
	private void openRound(int x, int y) {
		if (x - 1 >= 0) { // 向上
			openPosition(x - 1, y); // 正上方

			if (y - 1 >= 0) { // 左上方
				openPosition(x - 1, y - 1);
			}

			if (y + 1 < game.col) { // 右上方
				openPosition(x - 1, y + 1);
			}
		}

		if (x + 1 < game.row) { // 向下
			openPosition(x + 1, y); // 正下方

			if (y - 1 >= 0) { // 左下方
				openPosition(x + 1, y - 1);
			}

			if (y + 1 < game.col) { // 右下方
				openPosition(x + 1, y + 1);
			}
		}

		if (y - 1 >= 0) { // 左边
			openPosition(x, y - 1);
		}

		if (y + 1 < game.col) { // 右边
			openPosition(x, y + 1);
		}
	}
	
	private void openPosition(int x, int y) {
		int state = game.mineMap[x][y]; // 获取当前位置的值
		
		//  有雷                                                                               插旗                                                                                         已经打开
		if ((state & 0b1) == 0b1 || (state & 0b110) == 0b10 || (state & 0b110) == 0b100) {
			return;
		}
		
		if ((state >>> 3) == 0) { // 说明当前是空白，则周围要弹开
			openWhiteOption(x, y);
			return;
		}
		
		game.mineMap[x][y] += 0b10; // 当前这个位置需要打开
	}
	
	/**
	 * 游戏结束
	 * @param flag
	 */
	private void gameOver(int flag) {
		int index = 0;
		if (flag == 1) { // 说明是炸死的
			index = JOptionPane.showConfirmDialog(game, "对不起，您被炸身亡...\n是否再来一局???", "游戏结束", JOptionPane.YES_NO_OPTION);
		} else {
			index = JOptionPane.showConfirmDialog(game, "恭喜您，赢了...\n是否再来一局???", "游戏结束", JOptionPane.YES_NO_OPTION);
		}
		
		if (index == 1) { // 说明是否
			System.exit(0);
		} else { // 如果再来一局
			game.again();
		}
	}
	
	/**
	 * 判断游戏是否结束
	 * @return
	 */
	private boolean isGameOver() {
		int count = 0;
		for (int[] arr : game.mineMap) {
			for (int num : arr) {
				// 若干已经插旗，并且旗子下面是雷，则计数
				if ((num & 0b110) == 0b100 && (num & 0b1) == 1) {
					count ++;
				} 
			}
		}
		
		if (count == game.clearMine.getMineCount()) { // 说明所有雷都已经找出来了
			return true;
		}
		
		return false;
	}
	
	/*public void showMap() {
		// 增强型for循环 for (要迭代的集合中的数据类型  迭代变量 ： 要迭代的集合  )
		for (int[] arr : game.mineMap) {
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
	}*/
}

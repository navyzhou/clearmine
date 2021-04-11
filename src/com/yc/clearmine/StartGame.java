package com.yc.clearmine;

import com.yc.clearmine.util.YcUtil;

/**
 * 启动游戏的方法
 * 源辰信息
 * @author navy
 * @date 2021年4月11日
 * Email haijunzhou@hnit.edu.cn
 */
public class StartGame {
	public static void main(String[] args) {
		ClearMineUI window = new ClearMineUI();
		window.setVisible(true);
		YcUtil.tary(window);
	}
}

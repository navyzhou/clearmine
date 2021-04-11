package com.yc.clearmine.util;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * 托盘工具类
 * @company 源辰
 * @author navy
 */
public class YcUtil {
	public static void tary(final JFrame frame){
		SystemTray tray = SystemTray.getSystemTray(); // 获得本操作系统托盘的实例

		PopupMenu pop = new PopupMenu(); // 构造一个右键弹出式菜单
		MenuItem show = new MenuItem("Max");
		MenuItem hide = new MenuItem("Min");
		MenuItem exit = new MenuItem("Exit");
		pop.add(hide);
		pop.add(show);
		pop.addSeparator();
		pop.add(exit);

		ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(YcUtil.class.getResource("/images/logo.png"))); // 将要显示到托盘中的图标
		TrayIcon trayIcon = new TrayIcon(icon.getImage(),"扫雷", pop); //实例化托盘图标
		trayIcon.setImageAutoSize(true);

		// 为托盘图标监听点击事件
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if ( e.getButton() == MouseEvent.BUTTON1 ){ // BUTTON1：左击  BUTTON2：中键点击   BUTTON3：右击
					// if(e.getClickCount()== 2){ // 鼠标双击图标
					//if(e.getClickCount() == 1){ // 鼠标单击图标
					// trayIcon.displayMessage("警告", "这是一个警告提示!", TrayIcon.MessageType.WARNING); 
					if (frame.isVisible()) { // 如果主窗体是显示的，则隐藏
						frame.setVisible(false);// 隐藏主窗体
						return;
					} 
					frame.setExtendedState(JFrame.NORMAL); // 设置状态为正常
					frame.setVisible(true);// 显示主窗体
				}
			}
		});

		show.addActionListener(new ActionListener() {
			@Override 
			public void actionPerformed(ActionEvent e) {  // Performed： 表演
				frame.setExtendedState(JFrame.NORMAL); // 设置状态为正常
				frame.setVisible(true);// 显示主窗体
			}
		});

		hide.addActionListener(new ActionListener() {
			@Override 
			public void actionPerformed(ActionEvent e) {  // Performed： 表演
				frame.setVisible(false);// 隐藏主窗体
			}
		});

		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon);  //从系统的托盘实例中移除托盘图标  
				frame.dispose();
				System.exit(0);
			}
		});

		try {
			tray.add(trayIcon); // 将托盘图标添加到系统托盘中
		} catch (AWTException e1) {
			e1.printStackTrace();
		} 
	}
}

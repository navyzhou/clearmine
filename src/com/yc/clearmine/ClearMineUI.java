package com.yc.clearmine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;

/**
 * 扫雷主界面
 * 源辰信息
 * @author navy
 * @date 2021年3月29日
 * Email: haijunzhou@hnit.edu.cn
 */
public class ClearMineUI extends JFrame {
	private static final long serialVersionUID = 2335825853601510413L;
	int row; // 行数
	int col; // 列数
	int size = 40; // 图片大小
	int clearMineCount = 40; // 雷数
	int mineCount = 40; // 剩余雷数
	JLabel label_1 = null; // 剩余雷数
	JLabel label_2 = null; // 笑脸
	JLabel label_3 = null; // 倒计时

	ClearMine clearMine; // 棋盘地图对象
	int[][] mineMap; // 当前生成好的棋盘地图
	boolean isBomb = false; // 是否炸了
	boolean start = true; // 是否开始游戏
	int time = 0; // 倒计时
	Timer timer; // 定时器

	Image[] images = {
			Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/0.png")),	
			Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/1.png")),	
			Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/2.png")),	
			Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/3.png")),	
			Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/4.png")),	
			Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/5.png")),	
			Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/6.png")),	
			Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/7.png")),	
			Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/8.png"))
	};

	Image bombImge = Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/bomb.png")); // 未炸
	Image bomb0Imge = Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/bomb0.png")); // 爆炸
	Image redFlagImge = Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/f.png")); // 插旗
	Image questionImge = Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/q.png")); // 问号
	Image initImge = Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/b.png")); // 初始化图片



	public ClearMineUI() {
		clearMine = new ClearMine(2); // 默认是中级
		row = clearMine.row;
		col = clearMine.col;
		clearMineCount = clearMine.mineCount; // 雷数
		mineCount = clearMineCount;
		mineMap = clearMine.getMineMap(); // 获取地图
		initialize(); // 初始化界面
	}

	// 初始化界面的方法
	public void initialize() {
		// 设置图标
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/images/logo.png")));
		this.setTitle("源辰-扫雷");
		this.setSize(col * size + 7, row * size + 114); // 设置界面大小
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 点击关闭按钮是关闭并退出
		this.setLocationRelativeTo(null); // 让界面居中显示
		this.setResizable(false); // 界面大小不可变

		// 创建菜单栏
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		// 创建菜单
		JMenu menu = new JMenu("设置");
		// 将菜单放到菜单栏中
		menuBar.add(menu);  // 菜单栏中添加菜单项

		// 创建单选菜单
		JRadioButtonMenuItem radioItem = new JRadioButtonMenuItem("初级（10个雷）");
		// 将这个单选菜单加入到设置菜单中
		menu.add(radioItem);
		// 创建单选菜单
		JRadioButtonMenuItem radioItem1 = new JRadioButtonMenuItem("中级（40个雷）");
		// 将这个单选菜单加入到设置菜单中
		menu.add(radioItem1);
		radioItem1.setSelected(true); // 默认选中中级

		// 创建单选菜单
		JRadioButtonMenuItem radioItem2 = new JRadioButtonMenuItem("高级（99个雷）");
		// 将这个单选菜单加入到设置菜单中
		menu.add(radioItem2);

		// 我们同一时刻只允许选中其中的一个单选项，所以我们必须把这些单选按钮放到一个组中
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(radioItem);
		buttonGroup.add(radioItem1);
		buttonGroup.add(radioItem2);

		// 给单选菜单添加选择事件
		radioItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) { // 说明是选中
					reset(1); // 重置为初级
				}
			}
		});

		radioItem1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) { // 说明是选中
					reset(2); // 重置为中级
				}
			}
		});

		radioItem2.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) { // 说明是选中
					reset(3); // 重置为高级
				}
			}

		});

		JSplitPane splitPane = new JSplitPane(); // 分割面板
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.getContentPane().add(splitPane, BorderLayout.CENTER);

		// 创建面板
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setSize(col * size + 7, 40);
		splitPane.setLeftComponent(panel);

		label_1 = new JLabel(" 0" + clearMineCount + " ");
		label_1.setBounds(10, 10, 62, 40); // 距左边距离  距顶部距离 宽度  高度
		label_1.setFont(new Font("微软雅黑", Font.BOLD, 24)); // 设置字体
		label_1.setForeground(Color.RED); // 设置字体颜色
		label_1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.add(label_1);

		label_2 = new JLabel("");
		label_2.setIcon(new ImageIcon(ClearMineUI.class.getResource("/images/smile.png")));
		label_2.setBounds((col * size - 16) / 2, 17, 26, 26);
		label_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				again(); // 重新开始游戏的方法
			}

		});
		panel.add(label_2);

		label_3 = new JLabel(" 00:00 ");
		label_3.setBounds(col * size - 96, 10, 85, 40);
		label_3.setFont(new Font("微软雅黑", Font.BOLD, 24)); // 设置字体
		label_3.setForeground(Color.RED); // 设置字体颜色
		label_3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.add(label_3);

		// 扫雷区域
		ClearMineMapUI mapPanel = new ClearMineMapUI();
		mapPanel.addMouseListener(new MyMouseListener(size, this)); // 添加监听事件
		splitPane.setRightComponent(mapPanel);


		// 设置分割线的粗细
		splitPane.setDividerSize(1);
		splitPane.setDividerLocation(60);
	}

	/**
	 * 重置地图的方法
	 * @param level
	 */
	private void reset(int level) {
		clearMine.reset(level); // 重置扫雷地图的方法
		// 重新获取行数、列数、雷数
		row = clearMine.row;
		col = clearMine.col;
		mineMap = clearMine.getMineMap(); // 地图
		clearMineCount = clearMine.mineCount; // 雷数
		mineCount = clearMineCount;
		time = 0; // 重新开始计数
		start = true; // 重置游戏开始标识
		isBomb = false;

		if (timer != null) {
			timer.cancel();
		}

		label_1.setText(" 0" + clearMineCount + " "); // 重置剩余雷数
		label_3.setText(" 00:00 "); // 重新开始计时

		// 界面大小要重置
		this.setSize(col * size + 7, row * size + 114); // 重置界面大小
		this.setLocationRelativeTo(null); // 重新设置居中

		// 设置笑面和计时的位置
		label_2.setBounds((col * size - 16) / 2, 17, 26, 26);
		label_3.setBounds(col * size - 96, 10, 85, 40);
	}

	/**
	 * 重新开始游戏的方法
	 */
	protected void again() {
		clearMine.initArray(); // 初始化地图
		mineMap = clearMine.getMineMap(); // 重新布雷，获取布雷后的地图
		clearMineCount = clearMine.mineCount ; // 重置剩余雷数
		mineCount = clearMineCount;
		time = 0; // 重新开始计数
		start = true; // 重置游戏开始标识
		isBomb = false;

		if (timer != null) {
			timer.cancel();
		}

		label_1.setText(" 0" + clearMineCount + " "); // 重置剩余雷数的显示
		label_3.setText(" 00:00 "); // 重新开始计时
		isBomb = false; // 将是否炸了重置
		label_2.setIcon(new ImageIcon(ClearMineUI.class.getResource("/images/smile.png")));

		repaint(); // 重绘地图
	}

	/**
	 * 定时器
	 */
	protected void timeTask() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				++ time;
				String str = "";
				if (time <= 59) {
					str = time < 10 ? " 00:0" + time + " " : " 00:" + time + " ";
				} else {
					int temp = time / 60;
					str = temp < 10 ? " 0" + temp : " " + temp;
					str += ":";
					temp = time % 60;
					str += temp < 10 ? "0" + temp + " " : temp + " ";
				}
				label_3.setText(str);
			}

		}, 0, 1000);
	}


	class ClearMineMapUI extends JPanel {
		private static final long serialVersionUID = -457067721155089477L;

		@Override
		public void paint(Graphics g) { // 当显示界面时，会自动调用此方法
			super.paint(g);

			int value = 0;
			if (isBomb) {
				for (int i = 0; i < row; i ++) {
					for (int j = 0; j < col; j ++) {
						value = mineMap[i][j];
						// 如果这个地方有插旗，而且是地雷，则说明成功挖雷
						if ((value & 0b110) == 0b100 && (value & 0b1) == 0b1) {
							g.drawImage(bombImge, j * size, i * size, size, size, this);
						} else if ((value & 0b1) == 0b1){ // 如果是雷有没有插旗
							g.drawImage(bomb0Imge, j * size, i * size, size, size, this);
						} else {
							if ((value & 0b110) == 0b10) {
								g.drawImage(images[value >>> 3], j * size, i * size, size, size, this);
							} else {
								g.drawImage(initImge, j * size, i * size, size, size, this);
							}
						}
					}
				}
			} else {
				for (int i = 0; i < row; i ++) {
					for (int j = 0; j < col; j ++) {
						value = mineMap[i][j];
						// 先判断这个位置的状态
						if ((value & 0b110) == 0) { // 说明没有打开  00-未打开 01-已打开 10-插旗 11-问号
							g.drawImage(initImge, j * size, i * size, size, size, this);
						} else if ((value & 0b110) == 0b100) { // 说明已经插旗
							g.drawImage(redFlagImge, j * size, i * size, size, size, this);
						} else if ((value & 0b110) == 0b110) { // 说明是问号
							g.drawImage(questionImge, j * size, i * size, size, size, this);
						} else { 
							// 已经打开，并且没有踩到雷
							g.drawImage(images[value >>> 3], j * size, i * size, size, size, this);
						}
					}
				}
			}
		}
	}
}

package com.yc.clearmine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * 扫雷主界面
 * @author navy
 * @date 2019年12月2日
 */
public class MainGameUI extends JFrame{
	private static final long serialVersionUID = 4171648522985703511L;

	protected ClearMine clearMine;
	protected int row;
	protected int col;
	protected int[][] mineMap; // 地图，用来存放已经布好雷的地图
	protected int size = 20; // 图片大小
	protected boolean isBomb = false; // 是否炸了

	private Image[] images = {
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/0.jpg")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/1.jpg")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/2.jpg")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/3.jpg")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/4.jpg")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/5.jpg")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/6.jpg")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/7.jpg")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/8.jpg"))	
	};

	//private Image bombImage = Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/bomb.jpg"));
	private Image bomb0Image = Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/bomb0.jpg"));
	private Image redFlagImage = Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/flag.jpg"));
	private Image questionImage = Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/flag2.jpg"));
	private Image initImage = Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/s.jpg"));

	public MainGameUI(){
		init();
	}

	/**
	 * 实例化操作
	 */
	public void init() {
		clearMine = new ClearMine(1); // 默认等级是1
		row = clearMine.getRow();
		col = clearMine.getCol();
		mineMap = clearMine.getMineMap();

		createContent();
	}

	/**
	 * 创建页面内容
	 */
	public void createContent() {
		setTitle("扫雷"); // 设置界面的标题
		// 设置页面的图标
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/images/logo.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置默认的关闭操作
		setSize(188, 236); // 设置界面大小
		setLocationRelativeTo(null); // 设置页面居中显示
		setResizable(false); // 设置界面大小为不可变

		// 添加菜单
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar); // 设置当前界面的菜单栏为我们刚才创建的这一个

		// 在菜单栏中添加菜单项
		JMenu menu = new JMenu("设置");
		menuBar.add(menu); // 将这个菜单放到菜单栏中

		ButtonGroup buttonGroup = new ButtonGroup(); // 创建一个按钮组，同一时刻只允许选中一个

		// 创建单选按钮
		JRadioButtonMenuItem radioItem_1 = new JRadioButtonMenuItem("初级(雷数10)");
		menu.add(radioItem_1);
		radioItem_1.setSelected(true); // 默认是初级

		JRadioButtonMenuItem radioItem_2 = new JRadioButtonMenuItem("中级(雷数40)");
		menu.add(radioItem_2);

		JRadioButtonMenuItem radioItem_3 = new JRadioButtonMenuItem("高级(雷数99)");
		menu.add(radioItem_3);

		// 把这个三个单选放到一个组，这样用户每次就只能选中一个了
		buttonGroup.add(radioItem_1);
		buttonGroup.add(radioItem_2);
		buttonGroup.add(radioItem_3);

		menu.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {

			}

			@Override
			public void menuDeselected(MenuEvent e) {
				repaint();
			}

			@Override
			public void menuCanceled(MenuEvent e) {

			}
		});

		radioItem_1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) { // 说明是选中的
					reset(1); // 重置界面为初级
				}
			}
		});

		radioItem_2.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) { // 说明是选中的
					reset(2); // 重置界面为中级
				}
			}
		});

		radioItem_3.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) { // 说明是选中的
					reset(3); // 重置界面为高级
				}
			}
		});

		JMenuItem menuItem = new JMenuItem("重来");
		menuBar.add(menuItem);

		menuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { // 当用户点击重来时
				again();
			}
		});
	}
	
	/**
	 * 重新开局
	 */
	protected void again() {
		// 在重新布雷前，需要将地图初始化为最开始的状态，即二维数组中的所有值都是0
		clearMine.initArray(); // 初始化二维数组地图的方法
		mineMap = clearMine.getMineMap(); // 重新获取地图，此时会重新布雷
		isBomb = false;
		repaint(); // 重绘界面
	}

	/**
	 * 重置界面的方法
	 * @param i 等级
	 */
	private void reset(int i) {
		// 重新生产地图
		clearMine.reset(i);
		row = clearMine.getRow();
		col = clearMine.getCol();
		mineMap = clearMine.getMineMap(); // 重新获取地图
		setSize(col * size + 8, row * size + 56);
		setLocationRelativeTo(null); // 重新居中显示
		repaint(); // 重绘界面
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (isBomb) { // 则显示地图
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j ++) {
					if ((mineMap[i][j] & 0b0000001) == 0b00000001) { // 说明这个地方是雷
						g.drawImage(bomb0Image, j * size + 3, i * size + 50, size, size, this);
					} else if ( (mineMap[i][j] >>>3) == 0){ // 如果是空表
						g.drawImage(images[0], j * size + 3, i * size + 50, size, size, this);
					} else { // 数字
						g.drawImage(images[(mineMap[i][j] >>>3)], j * size + 3, i * size + 50, size, size, this);
					}
				}
			}
		} else {
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j ++) {
					// 先判断有没有打开
					if ((mineMap[i][j] & 0b00000110) == 0) { // 说明没有打开   00
						g.drawImage(initImage, j * size + 3, i * size + 50, size, size, this);
					} else if ((mineMap[i][j] & 0b00000110) == 0b00000100) { // 判断有没有插旗  10
						g.drawImage(redFlagImage, j * size + 3, i * size + 50, size, size, this);
					} else if ((mineMap[i][j] & 0b00000110) == 0b00000110) { // 说明是?号  11
						g.drawImage(questionImage, j * size + 3, i * size + 50, size, size, this);
					} else { // 说明已经打开 01， 而且这个肯定不是雷
						if ( (mineMap[i][j] >>>3) == 0){ // 如果是空表
							g.drawImage(images[0], j * size + 3, i * size + 50, size, size, this);
						} else { // 数字
							g.drawImage(images[(mineMap[i][j] >>>3)], j * size + 3, i * size + 50, size, size, this);
						}
					}
				}
			}
		}

		// 设置绘笔的颜色
		g.setColor(Color.gray);

		for (int i = 0; i <= row; i ++) {
			g.drawLine(3, i * size + 50, col * size + 3, i * size + 50);
		}

		for (int i = 0; i<= col; i++) {
			g.drawLine(3 + i * size, 50, 3 + i * size, row * size + 50);
		}
	}
}

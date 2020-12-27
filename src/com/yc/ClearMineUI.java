package com.yc;

import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JSplitPane;

import com.yc.clearmine.MainGameUI;

import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

public class ClearMineUI extends JFrame {
	private static final long serialVersionUID = 7999959991655466038L;
	private int time = 0;
	protected int row;
	protected int col;
	protected int size = 40;
	protected ClearMine clearMine;
	protected int[][] mineMap; // 地图，用来存放已经布好雷的地图
	protected boolean isBomb = false; // 是否炸了
	private JLabel label_2; // 计时
	protected JLabel label_smile; // 笑脸
	protected JLabel label_1; // 雷数
	protected int clearMineCount = 0; // 剩余雷数
	
	private Image[] images = {
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/0.png")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/1.png")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/2.png")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/3.png")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/4.png")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/5.png")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/6.png")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/7.png")),	
			Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/8.png"))	
	};

	private Image bombImage = Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/bomb0.png"));
	// private Image bomb0Image = Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/bomb.png"));
	private Image redFlagImage = Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/f.png"));
	private Image questionImage = Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/q.png"));
	private Image initImage = Toolkit.getDefaultToolkit().getImage(MainGameUI.class.getResource("/image/b.png"));
	
	

	/**
	 * Launch the application.
	 */
	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClearMineUI window = new ClearMineUI();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClearMineUI() {
		clearMine = new ClearMine(2);
		this.row = clearMine.getRow();
		this.col = clearMine.getCol();
		this.clearMineCount = clearMine.getMineCount();
		mineMap = clearMine.getMineMap();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(ClearMineUI.class.getResource("/image/logo.png")));
		this.setTitle("源辰-扫雷");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(col * size + 8, row * size + 120);
		this.setLocationRelativeTo(null); // 重新居中显示
		this.setResizable(false); // 设置界面大小为不可变
		
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("设置");
		menuBar.add(menu);
		
		JRadioButtonMenuItem radioItem = new JRadioButtonMenuItem("初级（10个雷）");
		menu.add(radioItem);
		
		JRadioButtonMenuItem radioItem1 = new JRadioButtonMenuItem("中级（40个雷）");
		radioItem1.setSelected(true);
		menu.add(radioItem1);
		
		JRadioButtonMenuItem radioItem2 = new JRadioButtonMenuItem("高级（99个雷）");
		menu.add(radioItem2);
		
		ButtonGroup buttonGroup = new ButtonGroup(); // 创建一个按钮组，同一时刻只允许选中一个
		buttonGroup.add(radioItem);
		buttonGroup.add(radioItem1);
		buttonGroup.add(radioItem2);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT); // 设置分割线方向
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setSize(col * size + 8, 40);
		splitPane.setLeftComponent(panel);
		
		label_1 = new JLabel(" 0" + clearMineCount + " ");
		label_1.setBounds(10, 10, 62, 40);
		label_1.setFont(new Font("微软雅黑", Font.BOLD, 24));
		label_1.setForeground(Color.RED);
		label_1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.add(label_1);
		
		label_smile = new JLabel("");
		label_smile.setIcon(new ImageIcon(ClearMineUI.class.getResource("/image/smile.png")));
		label_smile.setBounds((col * size - 26) / 2, 17, 26, 26);
		panel.add(label_smile);
		
		label_2 = new JLabel(" 00:00 ");
		label_2.setBounds(col * size - 100, 10, 85, 40);
		label_2.setFont(new Font("微软雅黑", Font.BOLD, 24));
		label_2.setForeground(Color.RED);
		label_2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.add(label_2);
		
		ClearMineMapUI mapPanel = new ClearMineMapUI();
		mapPanel.addMouseListener(new MyMouseListener(size, this));
		splitPane.setRightComponent(mapPanel);
		
		splitPane.setDividerSize(1);//设置分割线的宽度
		splitPane.setDividerLocation(60); //设定分割线的距离左边的位置
		
		Timer timer = new  Timer(); 
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				++ time;
				label_2.setText(timeStr());
			}
		}, 1000, 1000);
		
		
		radioItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) { // 说明是选中的
					reset(1); // 重置界面为初级
				}
			}
		});

		radioItem1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) { // 说明是选中的
					reset(2); // 重置界面为中级
				}
			}
		});

		radioItem2.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) { // 说明是选中的
					reset(3); // 重置界面为高级
				}
			}
		});
	}

	/**
	 * 计时
	 * @return
	 */
	private String timeStr() {
		int time = this.time;
		String str = " ";
		if (time < 60) {
			str += "00:";
		} else {
			int temp = time / 60;
			str += temp >= 10 ? String.valueOf(temp) : "0" + temp;
			str += ":";
			time = time % 60;
		}
		str += time >= 10 ? String.valueOf(time) : "0" + time;
		return str;
	}
	
	/**
	 * 重置界面的方法
	 * @param i 等级
	 */
	private void reset(int level) {
		clearMine.reset(level);
		this.row = clearMine.getRow();
		this.col = clearMine.getCol();
		mineMap = clearMine.getMineMap();
		this.time = 0;
		this.clearMineCount = clearMine.getMineCount();
		label_1.setText(" 0" + clearMineCount + " ");
		// 重新生产地图
		this.setSize(col * size + 8, row * size + 120);
		this.setLocationRelativeTo(null); // 重新居中显示
		label_2.setBounds(col * size - 100, 10, 85, 40);
		label_smile.setBounds((col * size - 26) / 2, 17, 26, 26);
	}
	
	/**
	 * 重新开局
	 */
	protected void again() {
		// 在重新布雷前，需要将地图初始化为最开始的状态，即二维数组中的所有值都是0
		clearMine.initArray(); // 初始化二维数组地图的方法
		mineMap = clearMine.getMineMap(); // 重新获取地图，此时会重新布雷
		isBomb = false;
		this.clearMineCount = clearMine.getMineCount();
		label_1.setText(" 0" + clearMineCount + " ");
		this.time = 0;
		label_smile.setIcon(new ImageIcon(ClearMineUI.class.getResource("/image/smile.png")));
		repaint(); // 重绘界面
	}
	
	class ClearMineMapUI extends JPanel{
		private static final long serialVersionUID = -2417574373129563246L;
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (isBomb) { // 则显示地图
				for (int i = 0; i < row; i++) {
					for (int j = 0; j < col; j ++) {
						if ((mineMap[i][j] & 0b0000001) == 0b00000001) { // 说明这个地方是雷
							g.drawImage(bombImage, j * size, i * size, size, size, this);
						} else if ( (mineMap[i][j] >>>3) == 0){ // 如果是空表
							g.drawImage(images[0], j * size, i * size, size, size, this);
						} else { // 数字
							g.drawImage(images[(mineMap[i][j] >>>3)], j * size, i * size, size, size, this);
						}
					}
				}
			} else {
				for (int i = 0; i < row; i++) {
					for (int j = 0; j < col; j ++) {
						// 先判断有没有打开
						if ((mineMap[i][j] & 0b00000110) == 0) { // 说明没有打开   00
							g.drawImage(initImage, j * size, i * size, size, size, this);
						} else if ((mineMap[i][j] & 0b00000110) == 0b00000100) { // 判断有没有插旗  10
							g.drawImage(redFlagImage, j * size, i * size, size, size, this);
						} else if ((mineMap[i][j] & 0b00000110) == 0b00000110) { // 说明是?号  11
							g.drawImage(questionImage, j * size, i * size, size, size, this);
						} else { // 说明已经打开 01， 而且这个肯定不是雷
							if ( (mineMap[i][j] >>>3) == 0){ // 如果是空表
								g.drawImage(images[0], j * size, i * size, size, size, this);
							} else { // 数字
								g.drawImage(images[(mineMap[i][j] >>>3)], j * size, i * size, size, size, this);
							}
						}
					}
				}
			}
		}
	}

}

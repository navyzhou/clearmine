package com.yc.clearmine;

public class StartGame {
	public static void main(String[] args) {
		MainGameUI game = new MainGameUI();
		game.addMouseListener(new MyMouseListener(game.size, game));
		game.setVisible(true);
	}
}

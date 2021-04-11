package com.yc.clearmine.util;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

/**
 * 音乐播放工具类
 * @company 源辰
 * @author navy
 */
public class PlayMusicUtil {
	public static AudioClip soundPut;
	
	/**
	 * 游戏
	 */
	public static void playStart() {
		URL put = PlayMusicUtil.class.getClass().getResource("/music/anniu1.wav"); // 点击声音文件
		soundPut = Applet.newAudioClip(put); //落子声音剪辑对象
		soundPut.play();
	}

	/**
	 * 播放胜利的声音
	 */
	public static void playWin() {
		URL win = PlayMusicUtil.class.getClass().getResource("/music/life.wav"); // 获胜声音文件
		AudioClip soundWin = Applet.newAudioClip(win); //获胜声音剪辑对象
		soundWin.play();
	}
	
	/**
	 * 播放胜利的声音
	 */
	public static void playOver() {
		URL win = PlayMusicUtil.class.getClass().getResource("/music/gameover.wav"); // 游戏结束
		AudioClip soundWin = Applet.newAudioClip(win); //获胜声音剪辑对象
		soundWin.play();
	}
}

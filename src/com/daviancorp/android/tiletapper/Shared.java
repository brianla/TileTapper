package com.daviancorp.android.tiletapper;

import android.util.Log;

import com.daviancorp.framework.GameFramework;

/* Singleton Class */
public final class Shared {
	private static final String TAG = "Shared";
	
	public static final int EASY = 1;
	public static final int MEDIUM = 2;
	public static final int HARD = 3;
	public static final int INSANE = 4;
	
	private static Shared instance = null;
	
	private GameFramework game;
	private GameSave gameSave;
	private int easyHS, mediumHS, hardHS, insaneHS;
	private int mode;
	private boolean musicOn, soundOn;
	private boolean aNewbie, aCasual, aHardcore, aPro, aHavingFun;
	
	private Shared() {
		
	}
	
	public static Shared getInstance() {
		if (instance == null) {
			instance = new Shared();
		}
		return instance;
	}
	
	public GameFramework getGame() {
		return game;
	}

	public void setGame(GameFramework game) {
		this.game = game;
	}

	public GameSave getGameSave() {
		return gameSave;
	}

	public void setGameSave(GameSave gameSave) {
		this.gameSave = gameSave;
	}
	
	public int getEasyHS() {
		return easyHS;
	}

	public void setEasyHS(int easyHS) {
		this.easyHS = easyHS;
	}

	public int getMediumHS() {
		return mediumHS;
	}

	public void setMediumHS(int mediumHS) {
		this.mediumHS = mediumHS;
	}

	public int getHardHS() {
		return hardHS;
	}

	public void setHardHS(int hardHS) {
		this.hardHS = hardHS;
	}
	
	public int getInsaneHS() {
		return insaneHS;
	}

	public void setInsaneHS(int insaneHS) {
		this.insaneHS = insaneHS;
	}
	
	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public boolean isMusicOn() {
		return musicOn;
	}

	public void setMusicOn(boolean musicOn) {
		this.musicOn = musicOn;
	}

	public boolean isSoundOn() {
		return soundOn;
	}

	public void setSoundOn(boolean soundOn) {
		this.soundOn = soundOn;
	}

	public boolean isNewbie() {
		return aNewbie;
	}

	public void setNewbie(boolean aNewbie) {
		this.aNewbie = aNewbie;
	}

	public boolean isCasual() {
		return aCasual;
	}

	public void setCasual(boolean aCasual) {
		this.aCasual = aCasual;
	}

	public boolean isHardcore() {
		return aHardcore;
	}

	public void setHardcore(boolean aHardcore) {
		this.aHardcore = aHardcore;
	}

	public boolean isPro() {
		return aPro;
	}

	public void setPro(boolean aPro) {
		this.aPro = aPro;
	}

	public boolean isHavingFun() {
		return aHavingFun;
	}

	public void setHavingFun(boolean aHavingFun) {
		this.aHavingFun = aHavingFun;
	}

	/* Get the info for muted/unmuted music
	 */
	public void getMusic() {
		if (musicOn == true) {			
			if (!Assets.theme.isPlaying()) {
				Assets.theme.play();
			}
		} 
		else {
			if (Assets.theme.isPlaying()) {
				Assets.theme.stop();
			}
		}
	}
	
	/* Toggle the mode option
	 */
	public void toggleMode() {
		switch (mode) {
			case EASY:
				mode = MEDIUM;
				break;
			case MEDIUM:
				mode = HARD;
				break;
			case HARD:
				mode = INSANE;
				break;
			case INSANE:
				mode = EASY;
				break;
		}
		
		saveGame();
	}
	
	/* Toggle the music option
	 */
	public void toggleMusic() {
		musicOn = !musicOn;
		getMusic();
		saveGame();
	}
	
	/* Toggle the sound option
	 */
	public void toggleSound() {
		soundOn = !soundOn;
		saveGame();
	}
	
	/* Check if score beats high score
	 */
	public void checkScore(int score) {
		game.onEnteredScore(mode, score);
		
//		switch (mode) {
//		case EASY:
//			if (score > easyHS) {
//				easyHS = score;
//				saveGame();
//			}
//			break;
//		case MEDIUM:
//			if (score > mediumHS) {
//				mediumHS = score;
//				saveGame();
//			}
//			break;
//		case HARD:
//			if (score > hardHS) {
//				hardHS = score;
//				saveGame();
//			}
//			break;
//		case INSANE:
//			if (score > insaneHS) {
//				insaneHS = score;
//				saveGame();
//			}
//			break;
//		}
	}
	
	/* Save game info to system
	 */
	public boolean saveGame() {
		try {
			gameSave.saveGame(easyHS, mediumHS, hardHS, insaneHS, mode, musicOn, soundOn,
					aNewbie, aCasual, aHardcore, aPro, aHavingFun);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

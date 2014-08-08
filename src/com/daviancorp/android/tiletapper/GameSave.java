package com.daviancorp.android.tiletapper;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.daviancorp.framework.JSONSerializer;

public class GameSave extends JSONSerializer {

	private static final String JSON_HIGHSCORE_EASY = "easy_highscore";
	private static final String JSON_HIGHSCORE_MEDIUM = "medium_highscore";
	private static final String JSON_HIGHSCORE_HARD = "hard_highscore";
	private static final String JSON_HIGHSCORE_INSANE = "insane_highscore";
	private static final String JSON_MODE = "mode";
	private static final String JSON_MUSIC_OPTION = "music_option";
	private static final String JSON_SOUND_OPTION = "sound_option";
	
	private static final String JSON_A_NEWBIE = "newbie_achievement";
	private static final String JSON_A_CASUAL = "casual_achievement";
	private static final String JSON_A_HARDCORE = "hardcore_achievement";
	private static final String JSON_A_PRO = "pro_achievement";
	private static final String JSON_A_HAVING_FUN = "having_fun_achievement";
	
	public GameSave(Context c, String f) {
		super(c, f);
	}
	
	public void saveGame(int easyHS, int mediumHS, int hardHS, int insaneHS, int mode, 
			boolean music, boolean sound, boolean aNewbie, boolean aCasual, 
			boolean aHardcore, boolean aPro, boolean aHavingFun)
		throws JSONException, IOException {
		
		// Build an array in JSON
		JSONObject json = new JSONObject();
		
		json.put(JSON_HIGHSCORE_EASY, easyHS);
		json.put(JSON_HIGHSCORE_MEDIUM, mediumHS);
		json.put(JSON_HIGHSCORE_HARD, hardHS);
		json.put(JSON_HIGHSCORE_INSANE, insaneHS);
		json.put(JSON_MODE, mode);
		json.put(JSON_MUSIC_OPTION, music);
		json.put(JSON_SOUND_OPTION, sound);
		
		json.put(JSON_A_NEWBIE, aNewbie);
		json.put(JSON_A_CASUAL, aCasual);
		json.put(JSON_A_HARDCORE, aHardcore);
		json.put(JSON_A_PRO, aPro);
		json.put(JSON_A_HAVING_FUN, aHavingFun);
		
		JSONArray array = new JSONArray();
		array.put(json);
		
		super.save(array);
	}

	public int loadEasyHighScore() {
		try {
			JSONArray array = super.load();
			int easyHS = array.getJSONObject(0).getInt(JSON_HIGHSCORE_EASY);
			return easyHS;
		}
		catch (Exception e) {
			return 0;
		}
	}
	
	public int loadMediumHighScore() {
		try {
			JSONArray array = super.load();
			int mediumHS = array.getJSONObject(0).getInt(JSON_HIGHSCORE_MEDIUM);
			return mediumHS;
		}
		catch (Exception e) {
			return 0;
		}
	}
	
	public int loadHardHighScore() {
		try {
			JSONArray array = super.load();
			int hardHS = array.getJSONObject(0).getInt(JSON_HIGHSCORE_HARD);
			return hardHS;
		}
		catch (Exception e) {
			return 0;
		}
	}

	public int loadInsaneHighScore() {
		try {
			JSONArray array = super.load();
			int insaneHS = array.getJSONObject(0).getInt(JSON_HIGHSCORE_INSANE);
			return insaneHS;
		}
		catch (Exception e) {
			return 0;
		}
	}
	
	public int loadMode() {
		try {
			JSONArray array = super.load();
			int mode = array.getJSONObject(0).getInt(JSON_MODE);
			return mode;
		}
		catch (Exception e) {
			return Shared.EASY;
		}
	}
	
	/* Check if music was played or muted in the past
	 */
	public boolean loadMusicOption() {
		try {
			JSONArray array = super.load();
			boolean music_option = array.getJSONObject(0).getBoolean(JSON_MUSIC_OPTION);
			return music_option;
		}
		catch (Exception e) {
			return true;
		}
	}
	
	/* Check if sound was played or muted in the past
	 */
	public boolean loadSoundOption() {
		try {
			JSONArray array = super.load();
			boolean sound_option = array.getJSONObject(0).getBoolean(JSON_SOUND_OPTION);
			return sound_option;
		}
		catch (Exception e) {
			return true;
		}
	}
	
	public boolean loadAchievementNewbie() {
		try {
			JSONArray array = super.load();
			boolean aNewbie = array.getJSONObject(0).getBoolean(JSON_A_NEWBIE);
			return aNewbie;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean loadAchievementCasual() {
		try {
			JSONArray array = super.load();
			boolean aCasual = array.getJSONObject(0).getBoolean(JSON_A_CASUAL);
			return aCasual;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean loadAchievementHardcore() {
		try {
			JSONArray array = super.load();
			boolean aHardcore = array.getJSONObject(0).getBoolean(JSON_A_HARDCORE);
			return aHardcore;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean loadAchievementPro() {
		try {
			JSONArray array = super.load();
			boolean aPro = array.getJSONObject(0).getBoolean(JSON_A_PRO);
			return aPro;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean loadAchievementHavingFun() {
		try {
			JSONArray array = super.load();
			boolean aHavingFun = array.getJSONObject(0).getBoolean(JSON_A_HAVING_FUN);
			return aHavingFun;
		}
		catch (Exception e) {
			return false;
		}
	}
}

package com.daviancorp.android.tiletapper;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.daviancorp.android.tiletapper.GameScreen.GameState;
import com.daviancorp.framework.GameFramework;
import com.daviancorp.framework.Graphics;
import com.daviancorp.framework.Image;
import com.daviancorp.framework.Input.TouchEvent;
import com.daviancorp.framework.Screen;
import com.daviancorp.framework.implementation.AndroidGame;
import com.google.android.gms.games.Games;

public class MainMenuScreen extends Screen {
	enum HomeState {
		Home, Option, Highscore
	}

	HomeState state = HomeState.Home;
	
	/* TODO */
	private static final String TAG = "MainMenuScreen";

	private static final int TEXT_COLOR = 0xff6B6564;
	private static final int ORANGE_COLOR = 0xffff8a00;

	private boolean switchUpdate;
	private Shared shared;
	private Paint paint;

	public MainMenuScreen(GameFramework game) {
		super(game);
		
		switchUpdate = true;
		shared = Shared.getInstance();
		shared.getMusic();
		
		paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(TEXT_COLOR);
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		
		if (state == HomeState.Home)
			updateHome(touchEvents, deltaTime);
		if (state == HomeState.Option)
			updateOption(touchEvents, deltaTime);
		if (state == HomeState.Highscore)
			updateHighscore(touchEvents, deltaTime);
	}

	public void updateHome(List<TouchEvent> touchEvents, float deltaTime) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			if (i < touchEvents.size()) {
				TouchEvent event = touchEvents.get(i);

				/* TODO */
				if (switchUpdate) {
					if (event.type == TouchEvent.TOUCH_UP) {
						// Pressed Play button
						if (inBounds(event, 250, 600, 300, 150)) {
							game.setScreen(new GameScreen(game));
						}
	
						// Pressed Options button
						else if (inBounds(event, 200, 750, 400, 150)) {
							state = HomeState.Option;
							switchUpdate = false;
						}
						
						// Pressed Leaderboards button
						else if (inBounds(event, 150, 900, 500, 150)) {
							game.onShowLeaderboardsRequested();
							
//							state = HomeState.Highscore;
//							switchUpdate = false;
						}
						
						// Pressed Achievements button
						else if (inBounds(event, 150, 1050, 500, 150)) {
							game.onShowAchievementsRequested();
							
//							state = HomeState.Highscore;
//							switchUpdate = false;
						}
					}
				} 
				else {
					if (event.type == TouchEvent.TOUCH_DOWN) {
						switchUpdate = true;
					}
				}
			}
		}
	}

	public void updateOption(List<TouchEvent> touchEvents, float deltaTime) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			if (i < touchEvents.size()) {
				TouchEvent event = touchEvents.get(i);

				if (switchUpdate) {
					/* TODO */
					if (event.type == TouchEvent.TOUCH_UP) {
						
						// Toggle Mode
						if (inBounds(event, 50, 600, 700, 150)) {
							shared.toggleMode();
						}
						
						// Toggle Music
						if (inBounds(event, 50, 750, 700, 150)) {
							shared.toggleMusic();
						}
	
						// Toggle Sound
						else if (inBounds(event, 50, 900, 700, 150)) {
							shared.toggleSound();
						}
						
						// Pressed Back button
						else if (inBounds(event, 250, 1050, 300, 150)) {
							state = HomeState.Home;
							switchUpdate = false;
						}
					}
				}
				else {
					if (event.type == TouchEvent.TOUCH_DOWN) {
						switchUpdate = true;
					}
				}
			}
		}
	}
	

	public void updateHighscore(List<TouchEvent> touchEvents, float deltaTime) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			if (i < touchEvents.size()) {
				TouchEvent event = touchEvents.get(i);

				if (switchUpdate) {
					
					// Pressed Back button
					if (inBounds(event, 250, 1050, 300, 150)) {
						state = HomeState.Home;
						switchUpdate = false;
					}
				}
				else {
					if (event.type == TouchEvent.TOUCH_DOWN) {
						switchUpdate = true;
					}
				}
			}
		}
	}

	private boolean inBounds(TouchEvent event, int x, int y, int width,
			int height) {
		if (event.x > x && event.x < x + width - 1 && event.y > y
				&& event.y < y + height - 1)
			return true;
		else
			return false;
	}

	@Override
	public void paint(float deltaTime) {
		Graphics g = game.getGraphics();
		paint.setTextSize(200);
		paint.setColor(Color.WHITE);
		
		g.drawImage(Assets.home, 0, 0);
//		g.drawRect(50, 100, 700, 450, Color.argb(255, 33, 33, 33));
//		g.drawString("TILE", 400, 300, paint);
//		g.drawString("TAPPER", 400, 500, paint);
		
		if (state == HomeState.Home)
			drawHomeUI();
		if (state == HomeState.Option)
			drawOptionUI();
		if (state == HomeState.Highscore)
			drawHighscoreUI();
	}

	private void drawHomeUI() {
		Graphics g = game.getGraphics();
		paint.setTextSize(100);
		paint.setColor(TEXT_COLOR);
		
		g.drawString("Play", 400, 700, paint);
		g.drawString("Options", 400, 850, paint);
		g.drawString("Leaderboards", 400, 1000, paint);
		g.drawString("Achievements", 400, 1150, paint);

		// TODO
//		 g.drawRect(250, 700, 300, 150, Color.argb(100, 50, 0, 0));
//		 g.drawRect(200, 850, 400, 150, Color.argb(100, 0, 50, 0));
//		 g.drawRect(150, 1000, 500, 150, Color.argb(100, 0, 0, 50));
	}

	private void drawOptionUI() {
		Graphics g = game.getGraphics();
		paint.setTextSize(100);
		paint.setColor(TEXT_COLOR);
		
		// Drawing Game Mode option
		g.drawString("Mode:", 200, 700, paint);
		paint.setColor(ORANGE_COLOR);
		switch(shared.getMode()) {
			case Shared.EASY:
				g.drawString("Easy", 550, 700, paint);
				break;
			case Shared.MEDIUM:
				g.drawString("Medium", 550, 700, paint);
				break;
			case Shared.HARD:
				g.drawString("Hard", 550, 700, paint);
				break;
			case Shared.INSANE:
				g.drawString("Insane", 550, 700, paint);
				break;
		}
		
		// Drawing Music option
		paint.setColor(TEXT_COLOR);
		g.drawString("Music:", 200, 850, paint);

		paint.setColor(ORANGE_COLOR);
		if(shared.isMusicOn()) {
			g.drawString("On", 450, 850, paint);

			paint.setColor(TEXT_COLOR);
			g.drawString("Off", 650, 850, paint);
		}
		else {
			g.drawString("Off", 650, 850, paint);
			
			paint.setColor(TEXT_COLOR);
			g.drawString("On", 450, 850, paint);
		}
		g.drawString("/", 550, 850, paint);
		
		// Drawing Sound option
		g.drawString("Sound:", 200, 1000, paint);
		
		paint.setColor(ORANGE_COLOR);
		if(shared.isSoundOn()) {
			g.drawString("On", 450, 1000, paint);

			paint.setColor(TEXT_COLOR);
			g.drawString("Off", 650, 1000, paint);
		}
		else {
			g.drawString("Off", 650, 1000, paint);
			
			paint.setColor(TEXT_COLOR);
			g.drawString("On", 450, 1000, paint);
		}
		g.drawString("/", 550, 1000, paint);
		
		// Drawing Back option
		g.drawString("Back", 400, 1150, paint);

		// TODO
//		 g.drawRect(50, 600, 700, 150, Color.argb(100, 50, 0, 0));
//		 g.drawRect(50, 750, 700, 150, Color.argb(100, 0, 50, 0));
//		 g.drawRect(250, 900, 300, 150, Color.argb(100, 0, 00, 50));
	}
	
	private void drawHighscoreUI() {
		Graphics g = game.getGraphics();
		paint.setTextSize(100);
		paint.setColor(TEXT_COLOR);
		
		g.drawString("Easy:", 200, 700, paint);
		g.drawString("Medium:", 200, 850, paint);
		g.drawString("Hard:", 200, 1000, paint);
		g.drawString("Back", 400, 1150, paint);
		
		paint.setColor(ORANGE_COLOR);
		g.drawString("" + shared.getEasyHS(), 550, 700, paint);
		g.drawString("" + shared.getMediumHS(), 550, 850, paint);
		g.drawString("" + shared.getHardHS(), 550, 1000, paint);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		paint = null;
	}

	@Override
	public void backButton() {
		android.os.Process.killProcess(android.os.Process.myPid());

	}
}

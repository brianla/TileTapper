package com.daviancorp.framework.implementation;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.daviancorp.android.tiletapper.R;
import com.daviancorp.android.tiletapper.Shared;
import com.daviancorp.framework.Audio;
import com.daviancorp.framework.FileIO;
import com.daviancorp.framework.GameFramework;
import com.daviancorp.framework.Graphics;
import com.daviancorp.framework.Input;
import com.daviancorp.framework.Screen;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;


public abstract class AndroidGame extends BaseGameActivity 
	implements GameFramework, View.OnClickListener {
	
	 // request codes we use when invoking an external activity
	final String TAG = "AndroidGame";
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;
	
    AndroidFastRenderView renderView;
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    WakeLock wakeLock;
    
	AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        
        LinearLayout myLayout = new LinearLayout(this);
        myLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        int frameBufferWidth = isPortrait ? 800: 1280;
        int frameBufferHeight = isPortrait ? 1280: 800;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                frameBufferHeight, Config.RGB_565);
        
        float scaleX = (float) frameBufferWidth
                / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight
                / getWindowManager().getDefaultDisplay().getHeight();

        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, renderView, scaleX, scaleY);
        screen = getInitScreen();      
		
        // My edit
        SignInButton signIn = new SignInButton(this);
        signIn.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        signIn.setId(R.id.sign_in_button);
        signIn.setOnClickListener(this);
        
        Button signOut = new Button(this);
        signOut.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        signOut.setId(R.id.sign_out_button);
        signOut.setOnClickListener(this);
        signOut.setVisibility(Button.GONE);
        signOut.setText("Sign Out");
        
//        ArrayList<View> buttons = new ArrayList<View>();
//        buttons.add(signIn);
//        buttons.add(signOut);
//        
//        renderView.addTouchables(buttons);
        //
        myLayout.addView(renderView);
        myLayout.addView(signIn);
        myLayout.addView(signOut);
        
        setContentView(myLayout);
        
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyGame");
        
        mOutbox.loadLocal(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        screen.resume();
        renderView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        renderView.pause();
        screen.pause();

        if (isFinishing())
            screen.dispose();
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public FileIO getFileIO() {
        return fileIO;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }
    
    
    public Screen getCurrentScreen() {

        return screen;
    }
    
    // My edit
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            beginUserInitiatedSignIn();
        }
        else if (view.getId() == R.id.sign_out_button) {
            // sign out.
            signOut();

            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }
    
    @Override
	public void onSignInSucceeded() {
        // show sign-out button, hide the sign-in button  	
        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        
        // (your code here: update UI, enable functionality that depends on sign in, etc)
    }
    
    @Override
	public void onSignInFailed() {
        // Sign in has failed. So show the user the sign-in button.
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
    }

    @Override
    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()),
                    RC_UNUSED);
        } else {
        	beginUserInitiatedSignIn();
//            showAlert(getString(R.string.achievements_not_available));
        }
    }

    @Override
    public void onShowLeaderboardsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()),
                    RC_UNUSED);
        } else {
        	beginUserInitiatedSignIn();
 //           showAlert(getString(R.string.leaderboards_not_available));
        }
    }
    
    @Override
    public void onEnteredScore(int mode, int score) {
    
        // check for achievements
        checkForAchievements(mode, score);

        // update leaderboards
        updateLeaderboards(mode, score);

        // push those accomplishments to the cloud, if signed in
        pushAccomplishments();
    }

    private void checkForAchievements(int mode, int score) {
        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
    	if (score > 200) {
    		switch (mode) {
	    		case Shared.EASY:
	    			mOutbox.mNewbieAchievement = true;
	    			break;
				case Shared.MEDIUM:
					mOutbox.mCasualAchievement = true;
					break;
				case Shared.HARD:
					mOutbox.mHardcoreAchievement = true;
					break;
				case Shared.INSANE:
					mOutbox.mProAchievement = true;
					break;
    		}
    	}
    }
    
    private void updateLeaderboards(int mode, int score) {
    	 if (mode == Shared.EASY && mOutbox.mEasyModeScore < score) {
             mOutbox.mEasyModeScore = score;
    	 }
         else if (mode == Shared.MEDIUM && mOutbox.mMediumModeScore < score) {
             mOutbox.mMediumModeScore = score;
    	 }
         else if (mode == Shared.HARD && mOutbox.mHardModeScore < score) {
             mOutbox.mHardModeScore = score;
    	 }
         else if (mode == Shared.INSANE && mOutbox.mInsaneModeScore < score) {
             mOutbox.mInsaneModeScore = score; 
    	 }
    }
    
    private void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, so save locally
            mOutbox.saveLocal(this);
            return;
        }
        
        // Achievements
        if (mOutbox.mNewbieAchievement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_newbie));
            mOutbox.mNewbieAchievement = false;
        }
        if (mOutbox.mCasualAchievement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_casual));
            mOutbox.mCasualAchievement = false;
        }
        if (mOutbox.mHardcoreAchievement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_hardcore));
            mOutbox.mHardcoreAchievement = false;
        }
        if (mOutbox.mProAchievement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_pro));
            mOutbox.mProAchievement = false;
        }
        Games.Achievements.increment(getApiClient(), getString(R.string.achievement_having_fun), 1);
        
        // Leaderboards
        if (mOutbox.mEasyModeScore >= 0) {
            Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_easy_mode),
                    mOutbox.mEasyModeScore);
            mOutbox.mEasyModeScore = -1;
        }
        if (mOutbox.mMediumModeScore >= 0) {
            Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_medium_mode),
                    mOutbox.mMediumModeScore);
            mOutbox.mMediumModeScore = -1;
        }
        if (mOutbox.mHardModeScore >= 0) {
            Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_hard_mode),
                    mOutbox.mHardModeScore);
            mOutbox.mHardModeScore = -1;
        }
        if (mOutbox.mInsaneModeScore >= 0) {
            Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_insane_mode),
                    mOutbox.mInsaneModeScore);
            mOutbox.mInsaneModeScore = -1;
        }
        mOutbox.saveLocal(this);
    }

    
    class AccomplishmentsOutbox {
        boolean mNewbieAchievement = false;
        boolean mCasualAchievement = false;
        boolean mHardcoreAchievement = false;
        boolean mProAchievement = false;
        boolean mHavingFunAchievement = false;
        int mEasyModeScore = -1;
        int mMediumModeScore = -1;
        int mHardModeScore = -1;
        int mInsaneModeScore = -1;

        boolean isEmpty() {
            return !mNewbieAchievement && !mCasualAchievement && !mHardcoreAchievement &&
                    !mProAchievement && mHavingFunAchievement && mEasyModeScore < 0 && 
                    mMediumModeScore < 0 && mHardModeScore < 0 && mInsaneModeScore < 0;
        }

        public void saveLocal(Context ctx) {
            /* TODO: This is left as an exercise. To make it more difficult to cheat,
             * this data should be stored in an encrypted file! And remember not to
             * expose your encryption key (obfuscate it by building it from bits and
             * pieces and/or XORing with another string, for instance). */
        }

        public void loadLocal(Context ctx) {
            /* TODO: This is left as an exercise. Write code here that loads data
             * from the file you wrote in saveLocal(). */
        }
    }
}

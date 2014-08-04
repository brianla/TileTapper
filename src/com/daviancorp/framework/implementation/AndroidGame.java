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
    public void leaderboardScore(int mode, int score) {
    	if (isSignedIn()) {
    		String s = "";
    		
    		switch (mode) {
	    		case Shared.EASY:
	    			s = getString(R.string.leaderboard_easy_mode);
	    			break;
	    		case Shared.MEDIUM:
	    			s = getString(R.string.leaderboard_medium_mode);
	    			break;
	    		case Shared.HARD:
	    			s = getString(R.string.leaderboard_hard_mode);
	    			break;
	    		case Shared.INSANE:
	    			s = getString(R.string.leaderboard_insane_mode);
	    			break;
    		}
    		
    		Games.Leaderboards.submitScore(getApiClient(), s, score);
    	}
    }
    //
    
    public Screen getCurrentScreen() {

        return screen;
    }
}

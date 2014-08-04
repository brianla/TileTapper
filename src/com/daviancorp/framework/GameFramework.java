package com.daviancorp.framework;

import com.google.android.gms.common.api.GoogleApiClient;

public interface GameFramework {

    public Audio getAudio();

    public Input getInput();

    public FileIO getFileIO();

    public Graphics getGraphics();

    public void setScreen(Screen screen);

    public Screen getCurrentScreen();

    public Screen getInitScreen();
    
    public void onShowAchievementsRequested();
    
    public void onShowLeaderboardsRequested();
    
    public GoogleApiClient getApi();
}

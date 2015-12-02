package com.amcclory77.murdership;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.amcclory77.murdership.MurderShipDrawRunnable.DrawBuffer;
import com.amcclory77.murdership.MurderShipDrawRunnable.DrawBuffer.DrawObject;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class MurderShipGameRunnable implements Runnable {
	
	public static final int DIALOG_CANCEL = 0;
	public static final int DIALOG_INTRODUCE = 1;
	public static final int DIALOG_QUESTION = 2;
	public static final int DIALOG_ACCUSE = 3;
	public static final int DIALOG_EXAMINE = 4;
	public static final int DIALOG_PICKUP = 5;
	
	// The base class of all events stores in the event queue
	class GameEvent {
		public GameEvent() {
			eventTime = System.currentTimeMillis();
		}
		long eventTime;
	}

	class KeyGameEvent extends GameEvent {

		public KeyGameEvent(int keyCode, boolean up, KeyEvent msg) {
			this.keyCode = keyCode;
			this.msg = msg;
			this.up = up;
		}

		public int keyCode;
		public KeyEvent msg;
		public boolean up;
	}

	class TouchGameEvent extends GameEvent {

		public TouchGameEvent(MotionEvent event) {
			type = event.getAction();
			x = (int) event.getX();
			y = (int) event.getY();
			if (event.getHistorySize() > 0) {
				historicalX = (int)event.getHistoricalX(0);
				historicalY = (int)event.getHistoricalY(0);
			}
			else {
				historicalX = 0;
				historicalY = 0;
			}
		}

		public int type;
		public int x;
		public int y;
		public int historicalX;
		public int historicalY;
	}

	private class SurfaceChangedGameEvent extends GameEvent {

		DisplayMetrics m_dm;
		int mWidth;
		int mHeight;

		public SurfaceChangedGameEvent(DisplayMetrics dm, int width, int height) {
			m_dm = dm;
			mWidth = width;
			mHeight = height;
		}
	}
	
	private class DialogEvent extends GameEvent {
		
		public DialogEvent(int event)
		{
			mEvent = event;
		}
		
		public int mEvent;
	}

	protected ConcurrentLinkedQueue<GameEvent> mEventQueue = new ConcurrentLinkedQueue<GameEvent>();
	
	class DrawObjectComparator implements Comparator<DrawBuffer.DrawObject> {

		//@Override
		public int compare(DrawObject object1, DrawObject object2) {
			if (object1.mType == DrawBuffer.ACTION_TYPE)
				return 1;
			else if (object2.mType == DrawBuffer.ACTION_TYPE)
				return -1;
			else if (object1.mType == DrawBuffer.TARGET_TYPE && object2.mType != DrawBuffer.CHARACTER_TYPE)
				return 1;
			else if (object2.mType == DrawBuffer.TARGET_TYPE && object1.mType != DrawBuffer.CHARACTER_TYPE)
				return -1;
			else if (object1.mType == DrawBuffer.TARGET_TYPE && object2.mType == DrawBuffer.CHARACTER_TYPE)
				return -1;
			else if (object2.mType == DrawBuffer.TARGET_TYPE && object1.mType == DrawBuffer.CHARACTER_TYPE)
				return 1;
			else if (object1.mType == DrawBuffer.OBJECT_TYPE || object1.mType == DrawBuffer.ITEM_TYPE)
				return -1;
			else if (object2.mType == DrawBuffer.OBJECT_TYPE || object2.mType == DrawBuffer.ITEM_TYPE)
				return 1;
			else
				return object1.mBottom - object2.mBottom;
		}
	}
	
	DrawObjectComparator mDrawObjectComparator;
	
	MurderShipDrawRunnable.DrawBuffer mActiveGameBuffer;
	
	public boolean mIsRunning = false;
	public boolean mIsPaused = false;
	public boolean mRequestCameraCenter = false;
	
	private int mCameraLeft = 0;
	private int mCameraTop = 0;
	private int mCameraWidth = 0;
	private int mCameraHeight = 0;
	
	private int mSurfaceOffsetLeft = 0;
	private int mSurfaceOffsetTop = 0;
	
	private MurderShip mActivity;
	
	private MurderShipDrawRunnable mDrawRunnable;
	
	private MurderShipGameData mGameData;
	
	private MurderShipStrings mStrings;
	
	private boolean mDoneRunningOnUiThread;
	
	public MurderShipGameRunnable(MurderShip activity, MurderShipGameData gameData, MurderShipDrawRunnable drawRunnable) {
		mActivity = activity;
		mGameData = gameData;
		mDrawRunnable = drawRunnable;
		mActiveGameBuffer = mDrawRunnable.mActiveGameBuffer;
		mDrawObjectComparator = new DrawObjectComparator();
		
		mStrings = new MurderShipStrings();
	}
	
	public boolean doKeyDown(int keyCode, KeyEvent msg) {
        mEventQueue.add(new KeyGameEvent(keyCode, false, msg));
        return true;
    }

    public boolean doKeyUp(int keyCode, KeyEvent msg) {
        mEventQueue.add(new KeyGameEvent(keyCode, true, msg));  
        return true;
    }
    
    public boolean doTouchEvent (MotionEvent event) {
    	mEventQueue.add(new TouchGameEvent(event));
    	return true;
    }
    
    public boolean doSurfaceChangedEvent(DisplayMetrics dm, int width, int height) {
    	mEventQueue.add(new SurfaceChangedGameEvent(dm, width, height));
    	return true;
    }
    
    public boolean doDialogEvent(int event)
    {
    	mEventQueue.add(new DialogEvent(event));
    	return true;
    }
    
    public void doSurfaceChanged(DisplayMetrics dm, int width, int height)
    {
    	mCameraWidth = width;
    	mCameraHeight = height;
    	
    	centerCameraOnHero();
		forceCameraOnMap();
		
    	mSurfaceOffsetLeft = dm.widthPixels - width;
    	mSurfaceOffsetTop = dm.heightPixels - height;
    	
    	Log.e("SURFACE", "dm.width: " + dm.widthPixels + " dm.height: " + dm.heightPixels + " width: " + width + " height: " + height);
    }
    
    public void setMap(MurderShipGameData gameData) {
    	mGameData = gameData;
    }
    
    private void centerCameraOnHero() {
    	mCameraLeft = mGameData.mMap.mGameObjects[mGameData.mMap.mHeroIndex].mLeft - (mCameraWidth/2);
		mCameraTop = mGameData.mMap.mGameObjects[mGameData.mMap.mHeroIndex].mBottom - (mCameraHeight/2);
    }
    
    // If the camera is off the map, force it back on
    private void forceCameraOnMap() {
		if (mCameraLeft < 0)
			mCameraLeft = 0;
		else if ((mCameraLeft + mCameraWidth) >= mGameData.mMapWidthTiles * mGameData.mBitmaps.mTileWidth)
			mCameraLeft = (mGameData.mMapWidthTiles * mGameData.mBitmaps.mTileWidth) - mCameraWidth;
		
		if (mCameraTop < 0)
			mCameraTop = 0;
		else if ((mCameraTop + mCameraHeight) >= mGameData.mMapHeightTiles * mGameData.mBitmaps.mTileHeight)
			mCameraTop = (mGameData.mMapHeightTiles * mGameData.mBitmaps.mTileHeight) - mCameraHeight;
    }
    
    public void run() {

		while(mIsRunning)
    	{
    		long startTime = System.currentTimeMillis();
			processGameEvents();

    		int heroLeft = mGameData.mMap.mGameObjects[mGameData.mMap.mHeroIndex].mLeft;
    		int heroTop = mGameData.mMap.mGameObjects[mGameData.mMap.mHeroIndex].mBottom;
    		
    		// Update game state if runnable isn't paused
    		if (!mIsPaused)
    		{
				mGameData.updateGameState();
				checkStoryPoint();
    		}
    		if (mRequestCameraCenter) {
				centerCameraOnHero();
    			mRequestCameraCenter = false;
    		}
			followHeroWithCamera(heroLeft, heroTop);
			updateDrawBuffer();

    		long endTime = System.currentTimeMillis();
    		Log.d("Game Runnable", "Updating game state required: " + (endTime - startTime) + "ms");
    		
    		// Wait for the drawing thread to finish drawing the inactive buffer
    		// so that the game thread can use it as the new active buffer
    		long startWaitTime = System.currentTimeMillis();
 
    		synchronized(mDrawRunnable) {
    			mDrawRunnable.mGameRunnableReadyToSwapBuffers = true;
    			while (!mDrawRunnable.mDrawRunnableReadyToSwapBuffers) {
    				try {
						mDrawRunnable.wait();
    				} catch (InterruptedException e) {
    				}
    			}
    			mActiveGameBuffer = mDrawRunnable.mActiveGameBuffer;
    			mDrawRunnable.mGameRunnableReadyToSwapBuffers = false;
    			mDrawRunnable.mDrawRunnableReadyToSwapBuffers = false;
    		}
    		long endWaitTime = System.currentTimeMillis();
    		Log.d("Game Runnable", "Waiting for draw runnable required: " + (endWaitTime - startWaitTime) + "ms");	
    	}	
	}
    
    private void checkStoryPoint()
    {
    	MurderShipGameData.GameMap.AnimateObject heroObject = (MurderShipGameData.GameMap.AnimateObject)mGameData.mMap.mGameObjects[mGameData.mMap.mHeroIndex];

    	switch(mGameData.mMap.mStoryPoint)
    	{
    	case MurderShipGameData.GameMap.PRE_INTRO_POINT:

			launchDialog(MurderShip.RESPONSE_DIALOG,
					mStrings.getIntroTitleString(),
					mStrings.getIntroMessageString(mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mVictimIndex)),
					false,
					mGameData.mBitmaps.mCharacterBitmap[heroObject.mBitmap]);
    		
    		break;
    	case MurderShipGameData.GameMap.PRE_YELLOW_ALERT_POINT:
    		
    		launchDialog(MurderShip.RESPONSE_DIALOG,
    				mStrings.getYellowAlertTitleString(),
    				mStrings.getYellowAlertMessageString(mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mVictimIndex)),
    				false,
    				mGameData.mBitmaps.mCharacterBitmap[heroObject.mBitmap]);
    		break;
    	case MurderShipGameData.GameMap.PRE_RED_ALERT_POINT:

    		launchDialog(MurderShip.RESPONSE_DIALOG,
    				mStrings.getRedAlertTitleString(),
    				mStrings.getRedAlertMessageString(mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mVictimIndex)),
    				true,
    				mGameData.mBitmaps.mCharacterBitmap[heroObject.mBitmap]);
    		
    		mGameData.beginStalkingHero(); // Start stalking the hero now that the murderer has lost patience
    		
    		break;
    	case MurderShipGameData.GameMap.PRE_ARREST_POINT:
    	{
    		MurderShipGameData.GameMap.AnimateObject murdererObject = (MurderShipGameData.GameMap.AnimateObject)mGameData.mMap.mGameObjects[mGameData.mMap.mMurdererIndex];

    		if (mGameData.haveWeapon())
    		{
    			launchDialog(MurderShip.RESPONSE_DIALOG,
    					"Continued",
    					mStrings.getAccuseWithWeaponString(),
    					true,
    					mGameData.mBitmaps.mCharacterBitmap[murdererObject.mBitmap]);
    		}
    		else
    		{
    			launchDialog(MurderShip.RESPONSE_DIALOG,
    					"Continued",
    					mStrings.getAccuseWithoutWeaponString(),
    					true,
    					mGameData.mBitmaps.mCharacterBitmap[murdererObject.mBitmap]);

    			mGameData.beginStalkingHero(); // Start stalking the hero now that he has accused the murderer without the weapon
    		}
    	}
    	break;
    	case MurderShipGameData.GameMap.PRE_HERO_MURDERED_POINT:
    	{
    		MurderShipGameData.GameMap.InanimateObject murderWeapon = (MurderShipGameData.GameMap.InanimateObject)mGameData.mMap.mGameObjects[mGameData.mMap.mMurderWeaponIndex];

    		launchDialog(MurderShip.RESPONSE_DIALOG,
    				mStrings.getHeroMurderedTitleString(),
    				mStrings.getHeroMurderedMessageString(mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mMurdererIndex), murderWeapon.mStringIndex),
    				false,
    				mGameData.mBitmaps.mCharacterBitmap[heroObject.mBitmap + mGameData.mBitmaps.mNumCharacterRows *(heroObject.mAnimation)]);
    	}
    		break;
    	case MurderShipGameData.GameMap.PRE_WIN_POINT:
    		
    		launchDialog(MurderShip.RESPONSE_DIALOG,
    				mStrings.getWinTitleString(),
    				mStrings.getWinMessageString(mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mMurdererIndex), 1000 - mGameData.getAlertLevel()),
    				false,
    				mGameData.mBitmaps.mCharacterBitmap[heroObject.mBitmap]);
    		
    		break;
    	case MurderShipGameData.GameMap.PRE_LOSE_POINT:
    		
    		launchDialog(MurderShip.RESPONSE_DIALOG,
    				mStrings.getLoseTitleString(),
    				mStrings.getLoseMessageString(mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mMurdererIndex)),
    				false,
    				mGameData.mBitmaps.mCharacterBitmap[heroObject.mBitmap  + mGameData.mBitmaps.mNumCharacterRows *(heroObject.mAnimation)]);
    		
    		break;
    	}
    }
    
    // Update the current draw buffer for all objects
    private void updateDrawBuffer() {
    	
    	mActiveGameBuffer.mNumObjects = 0;
    	
    	for (int i = 0; i < mGameData.mMap.mNumGameObjects; ++i) {
    		MurderShipDrawRunnable.DrawBuffer.DrawObject drawObject = mActiveGameBuffer.mDrawObjects[mActiveGameBuffer.mNumObjects];

    		MurderShipGameData.GameMap.LocationObject gameObject = mGameData.mMap.mGameObjects[i];
    		
    		if (gameObject.mVisible)
    		{
    			drawObject.mLeft = gameObject.mLeft;
    			drawObject.mBottom = gameObject.mBottom;

    			if (gameObject instanceof MurderShipGameData.GameMap.AnimateObject) 
    			{
    				MurderShipGameData.GameMap.AnimateObject animateObject = (MurderShipGameData.GameMap.AnimateObject)gameObject;

    				switch (animateObject.mType)
    				{
    				case MurderShipGameData.GameMap.AnimateObject.HERO_TYPE:
    				case MurderShipGameData.GameMap.AnimateObject.SUSPECT_TYPE:
    					drawObject.mType = MurderShipDrawRunnable.DrawBuffer.CHARACTER_TYPE;
    					break;
    				case MurderShipGameData.GameMap.AnimateObject.TARGET_TYPE:
    					drawObject.mType = MurderShipDrawRunnable.DrawBuffer.TARGET_TYPE;
    					break;
    				}
    				drawObject.mBitmap = animateObject.mBitmap + mGameData.mBitmaps.mNumCharacterRows *(animateObject.mAnimation);
    				++mActiveGameBuffer.mNumObjects;
    			}
    			else if (gameObject instanceof MurderShipGameData.GameMap.InanimateObject)
    			{
    				MurderShipGameData.GameMap.InanimateObject inanimateObject = (MurderShipGameData.GameMap.InanimateObject)gameObject;

    				switch (inanimateObject.mType)
    				{
    				case MurderShipGameData.GameMap.InanimateObject.OBJECT_TYPE:
    					drawObject.mType = MurderShipDrawRunnable.DrawBuffer.OBJECT_TYPE;
    					break;
    				case MurderShipGameData.GameMap.InanimateObject.MAGNIFYING_GLASS_TYPE:
    					drawObject.mType = MurderShipDrawRunnable.DrawBuffer.MAGNIFYING_GLASS_TYPE;
    					break;
    				case MurderShipGameData.GameMap.InanimateObject.ITEM_TYPE:
    					drawObject.mType = MurderShipDrawRunnable.DrawBuffer.ITEM_TYPE;
    					break;
    				}
    				drawObject.mBitmap = inanimateObject.mBitmap;
    				++mActiveGameBuffer.mNumObjects;
    			}
    			else if (gameObject instanceof MurderShipGameData.GameMap.ActionObject)
    			{
    				MurderShipGameData.GameMap.ActionObject actionObject = (MurderShipGameData.GameMap.ActionObject)gameObject;
    				// Only add action object and increment buffer count if the object is visible
    				if (actionObject.mVisible)
    				{
    					drawObject.mType = MurderShipDrawRunnable.DrawBuffer.ACTION_TYPE;
    					drawObject.mBitmap = actionObject.mBitmap;
    					++mActiveGameBuffer.mNumObjects;
    				}
    			}
    		}
    	}
    	
    	try {
    	Arrays.sort(mActiveGameBuffer.mDrawObjects, 0, mActiveGameBuffer.mNumObjects, mDrawObjectComparator);
    	} catch (Exception e) {
			Log.e("Exception", "Caught Exception: " + e.getMessage());
		} 
    	
    	mActiveGameBuffer.mCameraLeft = mCameraLeft;
    	mActiveGameBuffer.mCameraTop = mCameraTop;
    	
    	mActiveGameBuffer.mAlertLevel = mGameData.getAlertLevel();
    	if (mGameData.getAlertLevel() < MurderShipGameData.YELLOW_ALERT_LEVEL)
    		mActiveGameBuffer.mAlertColor = MurderShipDrawRunnable.GREEN_ALERT;
    	else if (mGameData.getAlertLevel() < MurderShipGameData.RED_ALERT_LEVEL)
    		mActiveGameBuffer.mAlertColor = MurderShipDrawRunnable.YELLOW_ALERT;
    	else
    		mActiveGameBuffer.mAlertColor = MurderShipDrawRunnable.RED_ALERT;
    	
    	mActiveGameBuffer.mIsValid = true;
    }
    
    private void followHeroWithCamera(int prevHeroLeft, int prevHeroBottom) {
    	
    	// Move the camera with the hero if necessary
    	MurderShipGameData.GameMap.LocationObject heroObject = mGameData.mMap.mGameObjects[mGameData.mMap.mHeroIndex];
    	
    	int deltaX = heroObject.mLeft - prevHeroLeft;
    	int deltaY = heroObject.mBottom - prevHeroBottom;
    	
    	int cameraOffsetX = heroObject.mLeft - mCameraLeft;
    	if (deltaX < 0 && cameraOffsetX < (mCameraWidth/7) * 3)
    		mCameraLeft += deltaX;
    	else if (deltaX > 0 && cameraOffsetX > (mCameraWidth/7) * 4)
    		mCameraLeft += deltaX;

    	int cameraOffsetY = heroObject.mBottom - mCameraTop;
    	if (deltaY < 0 && cameraOffsetY < (mCameraHeight/7) * 4)
    		mCameraTop += deltaY;
    	else if (deltaY > 0 && cameraOffsetY > (mCameraHeight/7) * 5)
    		mCameraTop += deltaY;

    	forceCameraOnMap();
    }
    
    // Iterate over all game events until queue is found to be empty
    private void processGameEvents() {
    	
    	// Poll game events until no more to poll
    	for (;;) {
            GameEvent event = mEventQueue.poll();
            if (event == null)
                break;
            
            // Check keyboard events
            if (event instanceof KeyGameEvent) {
                KeyGameEvent keyGameEvent = (KeyGameEvent)event;
                
                if (!keyGameEvent.up) {
                	switch(keyGameEvent.keyCode) {
                	case KeyEvent.KEYCODE_DPAD_UP:
                		mCameraTop-=16;
                		break;
                	case KeyEvent.KEYCODE_DPAD_DOWN:
                		mCameraTop+=16;
                		break;
                	case KeyEvent.KEYCODE_DPAD_LEFT:
                		mCameraLeft-=16;
                		break;
                	case KeyEvent.KEYCODE_DPAD_RIGHT:
                		mCameraLeft+=16;
                		break;
                	}
                	forceCameraOnMap();
                }
            }
            else if (event instanceof TouchGameEvent) {
            	// Look for touch screen events
            	TouchGameEvent touchGameEvent = (TouchGameEvent)event;
            	
            	if (touchGameEvent.type == MotionEvent.ACTION_DOWN) {
            		int xPos = (touchGameEvent.x - mSurfaceOffsetLeft) + mCameraLeft;
            		int yPos = (touchGameEvent.y - mSurfaceOffsetTop) + mCameraTop;
            		
            		final MurderShipGameData.GameMap.ActionObject actionObject = (MurderShipGameData.GameMap.ActionObject)mGameData.mMap.mGameObjects[mGameData.mMap.mActionIndex];
            		
            		// Enqueue a dialog if the action image was clicked, otherwise update the target location
            		if (actionObject.mVisible == true &&
            				xPos >= actionObject.mLeft &&
            				xPos <= actionObject.mLeft + (mGameData.mBitmaps.mTileWidth * MurderShipBitmaps.ACTION_WIDTH_IN_TILES) &&
            				yPos >= actionObject.mBottom - (mGameData.mBitmaps.mTileHeight * MurderShipBitmaps.ACTION_HEIGHT_IN_TILES) &&
            				yPos <= actionObject.mBottom)
            		{
            			LaunchActionDialog(actionObject);
            		}
            		else
            		{
            			mGameData.updateTarget(xPos, yPos);
            		}
            	}
            	else if (touchGameEvent.type == MotionEvent.ACTION_MOVE && touchGameEvent.historicalX != 0 && touchGameEvent.historicalY != 0) {
            		mCameraLeft+=(touchGameEvent.historicalX - touchGameEvent.x);
            		mCameraTop+=(touchGameEvent.historicalY - touchGameEvent.y);
            		forceCameraOnMap();
            	}
            }
            else if (event instanceof SurfaceChangedGameEvent) {
            	SurfaceChangedGameEvent surfaceChangedGameEvent = (SurfaceChangedGameEvent)event;
            	doSurfaceChanged(surfaceChangedGameEvent.m_dm, surfaceChangedGameEvent.mWidth, surfaceChangedGameEvent.mHeight);
            }
            else if (event instanceof DialogEvent) {
            	DialogEvent dialogEvent = (DialogEvent)event;
            	HandleDialogEvent(dialogEvent.mEvent);
            }
    	}
    }
    
    private void launchDialog(final int id, final String title, final String text, final boolean criticalText, final Bitmap bitmap)
    {
    	mDoneRunningOnUiThread = false;

    	mActivity.runOnUiThread(new Runnable() {
    		public void run()
			{
    			mActivity.launchDialog(id,
						title,
						text,
						criticalText,
						bitmap);
    			
    			mDoneRunningOnUiThread = true;
			}
    	});
		// Spin wait for dialog to be created
		while (!mDoneRunningOnUiThread) { processGameEvents(); }
    }
    
    private void closeDialog()
    {
    	mDoneRunningOnUiThread = false;

    	mActivity.runOnUiThread(new Runnable() {
    		public void run()
			{
    			mActivity.closeDialog();
    			
    			mDoneRunningOnUiThread = true;
			}
    	});
    	
    	// Spin wait for dialog to be created
		while (!mDoneRunningOnUiThread) { processGameEvents(); }
    }
    
    private void gameOver()
    {
    	mActivity.runOnUiThread(new Runnable() {
    		public void run()
			{
    			mActivity.gameOver();
			}
    	});
    }
	
	private void LaunchActionDialog(final MurderShipGameData.GameMap.ActionObject actionObject)
	{
		if (mGameData.mMap.mGameObjects[actionObject.mTarget] instanceof MurderShipGameData.GameMap.AnimateObject)
		{
			MurderShipGameData.GameMap.AnimateObject animateObject = (MurderShipGameData.GameMap.AnimateObject)mGameData.mMap.mGameObjects[actionObject.mTarget];

			launchDialog(MurderShip.CHARACTER_DIALOG,
					mStrings.getCharacterNameString(animateObject.mCharacterStringIndex),
					"",
					false,
					mGameData.mBitmaps.mCharacterBitmap[animateObject.mBitmap]);
		}
		else if (mGameData.mMap.mGameObjects[actionObject.mTarget] instanceof MurderShipGameData.GameMap.InanimateObject)
		{
			MurderShipGameData.GameMap.InanimateObject inanimateObject = (MurderShipGameData.GameMap.InanimateObject)mGameData.mMap.mGameObjects[actionObject.mTarget];

			if (inanimateObject.mType == MurderShipGameData.GameMap.InanimateObject.ITEM_TYPE)
			{
				launchDialog(MurderShip.ITEM_DIALOG,
						mStrings.getItemNameString(inanimateObject.mStringIndex),
						"",
						false,
						mGameData.mBitmaps.mItemBitmap[inanimateObject.mBitmap]);
			}
			else if (inanimateObject.mType == MurderShipGameData.GameMap.InanimateObject.MAGNIFYING_GLASS_TYPE)
			{
				launchDialog(MurderShip.ITEM_DIALOG,
						mStrings.getMagnifyingGlassNameString(),
						"",
						false,
						mGameData.mBitmaps.mMagnifyingGlassBitmap);
			}
			else if (inanimateObject.mType == MurderShipGameData.GameMap.InanimateObject.OBJECT_TYPE)
			{
				launchDialog(MurderShip.OBJECT_DIALOG,
						mStrings.getObjectNameString(inanimateObject.mStringIndex),
						"",
						false,
						mGameData.mBitmaps.mObjectBitmap[inanimateObject.mBitmap]);
			}
		}
	}
	
	private void HandleDialogEvent(final int event)
	{
		closeDialog();

		switch(event) {
		case DIALOG_CANCEL:
			HandleDialogCancelEvent();
			break;
		case DIALOG_INTRODUCE:
			HandleDialogIntroduceEvent();
			break;
		case DIALOG_QUESTION:
			HandleDialogQuestionEvent();
			break;
		case DIALOG_ACCUSE:
			HandleDialogAccuseEvent();
			break;
		case DIALOG_EXAMINE:
			HandleDialogExamineEvent();
			break;
		case DIALOG_PICKUP:
			HandleDialogPickupEvent();
			break;
		}
	}
	
	private void HandleDialogCancelEvent()
	{
		switch(mGameData.mMap.mStoryPoint)
		{
		case MurderShipGameData.GameMap.PRE_INTRO_POINT:
			mGameData.mMap.mStoryPoint = MurderShipGameData.GameMap.POST_INTRO_POINT;
			break;
		case MurderShipGameData.GameMap.PRE_YELLOW_ALERT_POINT:
			mGameData.mMap.mStoryPoint = MurderShipGameData.GameMap.POST_YELLOW_ALERT_POINT;
			break;
		case MurderShipGameData.GameMap.PRE_RED_ALERT_POINT:
			mGameData.mMap.mStoryPoint = MurderShipGameData.GameMap.POST_RAMPAGE_POINT;
			break;
		case MurderShipGameData.GameMap.PRE_ACCUSE_POINT:
			mGameData.mMap.mStoryPoint = MurderShipGameData.GameMap.POST_ACCUSE_POINT;
			break;
		case MurderShipGameData.GameMap.PRE_ARREST_POINT:
			if (mGameData.haveWeapon())
				mGameData.mMap.mStoryPoint = MurderShipGameData.GameMap.POST_ARREST_POINT;
			else
				mGameData.mMap.mStoryPoint = MurderShipGameData.GameMap.POST_RAMPAGE_POINT;
			break;
		case MurderShipGameData.GameMap.PRE_HERO_MURDERED_POINT:
			mGameData.mMap.mStoryPoint = MurderShipGameData.GameMap.POST_HERO_MURDERED_POINT;
			break;
		case MurderShipGameData.GameMap.PRE_LOSE_POINT:
			mGameData.mMap.mStoryPoint = MurderShipGameData.GameMap.POST_LOSE_POINT;
			gameOver();
			break;
		case MurderShipGameData.GameMap.PRE_WIN_POINT:
			mGameData.mMap.mStoryPoint = MurderShipGameData.GameMap.POST_WIN_POINT;
			gameOver();
			break;
		}
	}
	
	private void HandleDialogIntroduceEvent()
	{
		MurderShipGameData.GameMap.ActionObject actionObject = (MurderShipGameData.GameMap.ActionObject)mGameData.mMap.mGameObjects[mGameData.mMap.mActionIndex];
		
		if (mGameData.mMap.mGameObjects[actionObject.mTarget] instanceof MurderShipGameData.GameMap.AnimateObject)
		{
			MurderShipGameData.GameMap.AnimateObject animateObject = (MurderShipGameData.GameMap.AnimateObject)mGameData.mMap.mGameObjects[actionObject.mTarget];
			
			launchDialog(
					MurderShip.RESPONSE_DIALOG,
					mStrings.getCharacterNameString(animateObject.mCharacterStringIndex),
					mStrings.getCharacterIntroString(animateObject.mCharacterStringIndex),
					false,
					mGameData.mBitmaps.mCharacterBitmap[animateObject.mBitmap]);	
		}
	}
	
	private void HandleDialogQuestionEvent()
	{
		MurderShipGameData.GameMap.ActionObject actionObject = (MurderShipGameData.GameMap.ActionObject)mGameData.mMap.mGameObjects[mGameData.mMap.mActionIndex];

		if (mGameData.mMap.mGameObjects[actionObject.mTarget] instanceof MurderShipGameData.GameMap.AnimateObject)
		{
			MurderShipGameData.GameMap.AnimateObject animateObject = (MurderShipGameData.GameMap.AnimateObject)mGameData.mMap.mGameObjects[actionObject.mTarget];
			
			if (mGameData.foundVictim())
			{
				launchDialog(MurderShip.RESPONSE_DIALOG,
						mStrings.getCharacterNameString(animateObject.mCharacterStringIndex),
						mStrings.getCharacterVictimFoundQuestionString(animateObject.mCharacterStringIndex,
								animateObject.mCharacterStringIndex != animateObject.mAlibiCharacterStringIndex,
								animateObject.mAlibiStringIndex,
								animateObject.mAlibiCharacterStringIndex,
								mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mVictimIndex),
								animateObject.mAccuseCharacterStringIndex),
						false,
						mGameData.mBitmaps.mCharacterBitmap[animateObject.mBitmap]);
				
				mGameData.incrementAlertLevel(MurderShipGameData.ALERT_OF_ASKING_QUESTIONS_AFTER_BODY);
			}
			else
			{
				launchDialog(MurderShip.RESPONSE_DIALOG,
						mStrings.getCharacterNameString(animateObject.mCharacterStringIndex),
						mStrings.getCharacterPreMurderQuestionString(animateObject.mCharacterStringIndex, mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mVictimIndex)),
						false,
						mGameData.mBitmaps.mCharacterBitmap[animateObject.mBitmap]);
				
				mGameData.incrementAlertLevel(MurderShipGameData.ALERT_OF_ASKING_QUESTIONS_BEFORE_BODY);
			}
		}
	}
	
	private void HandleDialogAccuseEvent()
	{
		MurderShipGameData.GameMap.ActionObject actionObject = (MurderShipGameData.GameMap.ActionObject)mGameData.mMap.mGameObjects[mGameData.mMap.mActionIndex];
		
		if (mGameData.mMap.mGameObjects[actionObject.mTarget] instanceof MurderShipGameData.GameMap.AnimateObject)
		{
			MurderShipGameData.GameMap.AnimateObject animateObject = (MurderShipGameData.GameMap.AnimateObject)mGameData.mMap.mGameObjects[actionObject.mTarget];

			// If the body has been found a confession or, if the accusation is false, a big increase in alert level will result
			if (mGameData.foundVictim())
			{
				if (animateObject.mRole == MurderShipGameData.GameMap.AnimateObject.MURDERER_ROLE)
				{
					launchDialog(MurderShip.RESPONSE_DIALOG,
							mStrings.getCharacterNameString(animateObject.mCharacterStringIndex),
							mStrings.getCharacterPostMurderConfessionString(animateObject.mCharacterStringIndex, mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mVictimIndex)),
							true,
							mGameData.mBitmaps.mCharacterBitmap[animateObject.mBitmap]);
					
					mGameData.mMap.mStoryPoint = MurderShipGameData.GameMap.PRE_ACCUSE_POINT;
				}
				else
				{
					launchDialog(MurderShip.RESPONSE_DIALOG,
							mStrings.getCharacterNameString(animateObject.mCharacterStringIndex),
							mStrings.getCharacterPostMurderDenialString(animateObject.mCharacterStringIndex, mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mVictimIndex)),
							false,
							mGameData.mBitmaps.mCharacterBitmap[animateObject.mBitmap]);
					
					mGameData.incrementAlertLevel(MurderShipGameData.ALERT_OF_FALSELY_ACCUSING);
				}
			}
			else
			{
				launchDialog(MurderShip.RESPONSE_DIALOG,
						mStrings.getCharacterNameString(animateObject.mCharacterStringIndex),
						mStrings.getCharacterPreMurderAccusationString(animateObject.mCharacterStringIndex, mGameData.mMap.getCharacterStringIndex(mGameData.mMap.mVictimIndex)),
						false,
						mGameData.mBitmaps.mCharacterBitmap[animateObject.mBitmap]);
			}
		}
	}
	
	private void HandleDialogExamineEvent()
	{
		MurderShipGameData.GameMap.ActionObject actionObject = (MurderShipGameData.GameMap.ActionObject)mGameData.mMap.mGameObjects[mGameData.mMap.mActionIndex];
		
		if (mGameData.mMap.mGameObjects[actionObject.mTarget] instanceof MurderShipGameData.GameMap.InanimateObject)
		{
			MurderShipGameData.GameMap.InanimateObject inanimateObject = (MurderShipGameData.GameMap.InanimateObject)mGameData.mMap.mGameObjects[actionObject.mTarget];
			
			if (inanimateObject.mType == MurderShipGameData.GameMap.InanimateObject.ITEM_TYPE)
			{
				// If this is not the murder weapon, or the hero does not have a magnifying glass
				if (!inanimateObject.mUsedInMurder || !mGameData.haveMagnifyingGlass())
				{
					launchDialog(MurderShip.RESPONSE_DIALOG,
							mStrings.getItemNameString(inanimateObject.mStringIndex),
							mStrings.getItemNoMurderWeaponSearchString(inanimateObject.mStringIndex, mGameData.haveMagnifyingGlass()),
							false,
							mGameData.mBitmaps.mItemBitmap[inanimateObject.mBitmap]);
					
					mGameData.incrementAlertLevel(MurderShipGameData.ALERT_OF_SEARCHING_FOR_WEAPON);
				}
				else
				{
					// Find the murder weapon
					launchDialog(MurderShip.RESPONSE_DIALOG,
							mStrings.getItemNameString(inanimateObject.mStringIndex),
							mStrings.getItemMurderWeaponSearchString(inanimateObject.mStringIndex),
							true,
							mGameData.mBitmaps.mItemBitmap[inanimateObject.mBitmap]);
					
					mGameData.findWeapon();
					
					mGameData.incrementAlertLevel(MurderShipGameData.ALERT_OF_FINDING_WEAPON);
				}
			}
			else if (inanimateObject.mType == MurderShipGameData.GameMap.InanimateObject.MAGNIFYING_GLASS_TYPE)
			{
				launchDialog(MurderShip.RESPONSE_DIALOG,
						mStrings.getMagnifyingGlassNameString(),
						mStrings.getMagnifyingGlassExamineString(),
						false,
						mGameData.mBitmaps.mMagnifyingGlassBitmap);
			}
			else
			{
				String objectResponse;
				boolean objectCritical;
				Bitmap objectBitmap;
				
				// Find the murder victim
				if (inanimateObject.mUsedInMurder)
				{
					MurderShipGameData.GameMap.AnimateObject murderVictim = (MurderShipGameData.GameMap.AnimateObject)mGameData.mMap.mGameObjects[mGameData.mMap.mVictimIndex];
					
					objectResponse = mStrings.getObjectBodyFoundSearchString(inanimateObject.mStringIndex, murderVictim.mCharacterStringIndex);
					objectCritical = true;
					objectBitmap = mGameData.mBitmaps.mCharacterBitmap[murderVictim.mBitmap + mGameData.mBitmaps.mNumCharacterRows *(murderVictim.mAnimation)];
					
					murderVictim.mVisible = true;
					
					mGameData.findVictim();
					
					mGameData.incrementAlertLevel(MurderShipGameData.ALERT_OF_FINDING_BODY);
				}
				else
				{
					objectResponse = mStrings.getObjectNoBodySearchString(inanimateObject.mStringIndex);
					objectCritical = false;
					objectBitmap = mGameData.mBitmaps.mObjectBitmap[inanimateObject.mBitmap];
					
					mGameData.incrementAlertLevel(MurderShipGameData.ALERT_OF_SEARCHING_FOR_BODY);
				}
				launchDialog(MurderShip.RESPONSE_DIALOG,
						mStrings.getObjectNameString(inanimateObject.mStringIndex),
						objectResponse,
						objectCritical,
						objectBitmap);
			}
		}
	}
	
	private void HandleDialogPickupEvent()
	{
		MurderShipGameData.GameMap.ActionObject  actionObject = (MurderShipGameData.GameMap.ActionObject)mGameData.mMap.mGameObjects[mGameData.mMap.mActionIndex];

		if (mGameData.mMap.mGameObjects[actionObject.mTarget] instanceof MurderShipGameData.GameMap.InanimateObject)
		{
			MurderShipGameData.GameMap.InanimateObject inanimateObject = (MurderShipGameData.GameMap.InanimateObject)mGameData.mMap.mGameObjects[actionObject.mTarget];
			
			if (inanimateObject.mType == MurderShipGameData.GameMap.InanimateObject.ITEM_TYPE)
			{
				// If this is not the murder weapon, or the murder weapon has not been found yet
				if (!inanimateObject.mUsedInMurder || !mGameData.foundWeapon())
				{
					launchDialog(MurderShip.RESPONSE_DIALOG,
							mStrings.getItemNameString(inanimateObject.mStringIndex),
							mStrings.getItemNoMurderWeaponPickUpString(inanimateObject.mStringIndex),
							false,
							mGameData.mBitmaps.mItemBitmap[inanimateObject.mBitmap]);
				}
				else
				{
					// Pick up the murder weapon
					launchDialog(MurderShip.RESPONSE_DIALOG,
							mStrings.getItemNameString(inanimateObject.mStringIndex),
							mStrings.getItemMurderWeaponPickUpString(inanimateObject.mStringIndex),
							true,
							mGameData.mBitmaps.mItemBitmap[inanimateObject.mBitmap]);
					
					mGameData.pickupWeapon(inanimateObject);
				}
			}
			else if (inanimateObject.mType == MurderShipGameData.GameMap.InanimateObject.MAGNIFYING_GLASS_TYPE)
			{
				launchDialog(MurderShip.RESPONSE_DIALOG,
						mStrings.getMagnifyingGlassNameString(),
						mStrings.getMagnifyingGlassPickUp(),
						false,
						mGameData.mBitmaps.mMagnifyingGlassBitmap);
				
				mGameData.pickupMagnifyingGlass(inanimateObject);
			}
			
		}
	}
}

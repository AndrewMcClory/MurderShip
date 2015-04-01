package com.amcclory77.murdership;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.Typeface;


public class MurderShipDrawRunnable implements Runnable {
	
	public static final int STATE_START = -1;
    public static final int STATE_PLAY = 0;
    
    public static final int GREEN_ALERT = 0;
    public static final int YELLOW_ALERT = 1;
    public static final int RED_ALERT = 2;
    
	public boolean mIsRunning = false;
	
	private MurderShipGameData mGameData;
	private MurderShipBitmaps mBitmaps;

	// Data for the visible game surface
	class GameSurface {
		
		public int mLeft = 0;
		public int mTop = 0;
		
		public int mWidth = 0;
		public int mHeight = 0;
		
		public Canvas mCanvas;
		public SurfaceHolder mSurfaceHolder;
	}
	
	GameSurface mGameSurface;
	
	// Data for the background of tiles that is repeatedly copied onto the game surface
	private class TileBitmap {
		
		public int mLeft = 0;
		public int mTop = 0;
		
		public int mWidth = 0;
		public int mHeight = 0;
		
		public int mActiveBitmap = 0;
		public int mInactiveBitmap = 1;
		
		public Canvas [] mCanvas;
		public Bitmap [] mBitmap;
		
		public TileBitmap() {
			mBitmap = new Bitmap[2];
			
			mBitmap[0] = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
			mBitmap[1] = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
			
			mCanvas = new Canvas[2];
			
			mCanvas[0] = new Canvas(mBitmap[0]);
			mCanvas[1] = new Canvas(mBitmap[1]);
		}
	}
	
	private TileBitmap mTileBitmap;
	
	private Rect mSrcRect;
	private Rect mDestRect;
	
	private long mStartTime;
	private long mNumFrames;
	
	class DrawBuffer {
		
		public static final int MAX_GAME_OBJECTS = 500;
		
		public static final int CHARACTER_TYPE = 0;
		public static final int TARGET_TYPE = 1;
		public static final int OBJECT_TYPE = 2;
		public static final int ITEM_TYPE = 3;
		public static final int ACTION_TYPE = 4;
		public static final int MAGNIFYING_GLASS_TYPE = 5;
		
		public boolean mIsValid = false;
		
		class DrawObject {
			int mLeft = 0;
			int mBottom = 0;
			
			public int mType = 0;
			public int mBitmap = 0;
		}
		
		public DrawObject [] mDrawObjects;
		
		public int mNumObjects = 0;
		
		public int mCameraLeft = 0;
		public int mCameraTop = 0;
		
		public int mAlertLevel = 0;
		public int mAlertColor = GREEN_ALERT;
		
		public DrawBuffer() {
			mDrawObjects = new DrawObject[MAX_GAME_OBJECTS];
			
			for (int i = 0; i < MAX_GAME_OBJECTS; ++i) {
				mDrawObjects[i] = new DrawObject();
			}
		}
	}

	public DrawBuffer [] mDrawBuffer;
	public DrawBuffer mActiveDrawBuffer;
	public DrawBuffer mActiveGameBuffer;
	
	public boolean mGameRunnableReadyToSwapBuffers = false;
	public boolean mDrawRunnableReadyToSwapBuffers = false;
	
	private Paint mAlertLevelPaint;
	
	interface DrawEvent {
    }
	
	class SurfaceChangedDrawEvent implements DrawEvent {
		
		SurfaceHolder mHolder;
		int mWidth;
		int mHeight;
		
		public SurfaceChangedDrawEvent(SurfaceHolder holder, int width, int height) {
			mHolder = holder;
			mWidth = width;
			mHeight = height;
		}
	}
	
	protected ConcurrentLinkedQueue<DrawEvent> mEventQueue = new ConcurrentLinkedQueue<DrawEvent>();
	
	public MurderShipDrawRunnable(MurderShipGameData gameData, SurfaceHolder surfaceHolder, Context context, Handler handler) {

		mSrcRect = new Rect();
		mDestRect = new Rect();
		
    	// Store the game data
    	mGameData = gameData;
    	// Store reference to the bitmaps
    	mBitmaps = mGameData.mBitmaps;
    	
    	// Create viewable game surface
    	mGameSurface = new GameSurface();
    	mGameSurface.mSurfaceHolder = surfaceHolder;
    	mGameSurface.mCanvas = new Canvas();
    	
    	// Create tile bitmap that will serve as the game's scrollable background
    	mTileBitmap = new TileBitmap();
    	
    	// Create draw buffers, which will facilitate communication between game and draw threads
    	mDrawBuffer = new DrawBuffer[2];
		mDrawBuffer[0] = new DrawBuffer();
		mDrawBuffer[1] = new DrawBuffer();
		
		mActiveGameBuffer = mDrawBuffer[0];
		mActiveDrawBuffer = mDrawBuffer[1];
		
		// Create paint object that describes Alert level text
		mAlertLevelPaint = new Paint();
		mAlertLevelPaint.setTextSize(18);
		mAlertLevelPaint.setShadowLayer(1, 0, 0, Color.rgb(0, 0, 0));
		mAlertLevelPaint.setTypeface(Typeface.DEFAULT_BOLD);
	}
    
	public void doSurfaceChangedEvent(SurfaceHolder holder, int width, int height) {
		mEventQueue.add(new SurfaceChangedDrawEvent(holder, width, height));
	}
	
	private void invalidateBuffers() {
		mDrawBuffer[0].mIsValid = false;
    	mDrawBuffer[1].mIsValid = false;
	}
	
    private void doSurfaceChanged(SurfaceHolder holder, int width, int height) {
    	
    	invalidateBuffers();
    	
    	mGameSurface.mWidth = width;
    	mGameSurface.mHeight = height;

    	// Calculate width and height of tile bitmap in tiles by dividing game surface width in pixels by tile width
    	// Add two tiles on each axis so that viewable surface will be framed by tiles
    	int tileBitmapWidthTiles = (mGameSurface.mWidth / mBitmaps.mTileWidth) + 2;
    	int tileBitmapHeightTiles = (mGameSurface.mHeight / mBitmaps.mTileHeight) + 2;

    	// If surface dimensions are not evenly divisible by tile dimensions, add extra tile for padding
    	if (mGameSurface.mWidth % mBitmaps.mTileWidth != 0)
    		tileBitmapWidthTiles++;
    	if (mGameSurface.mHeight % mBitmaps.mTileHeight != 0)
    		tileBitmapHeightTiles++;

    	// Resize and redraw the background bitmap of tiles with the new dimensions
    	resizeTileBitmap(tileBitmapWidthTiles, tileBitmapHeightTiles);
    	drawTileBitmap();
    }
    
    public void run() {
    	
    	mNumFrames = 0;
    	mStartTime = System.currentTimeMillis();
    	
    	boolean redrawTileBitmap = true;
    	
    	while(mIsRunning)
    	{
    		processDrawEvents();
    		
    		synchronized(this) {
    			if (mGameRunnableReadyToSwapBuffers)
    			{	
    				mDrawRunnableReadyToSwapBuffers = true;
    				
    				//Log.e("BUFFERS", "START SWAP");
    				DrawBuffer tempDrawBuffer = mActiveDrawBuffer;
    				mActiveDrawBuffer = mActiveGameBuffer;
    				mActiveGameBuffer = tempDrawBuffer;
    				//Log.e("BUFFERS", "END SWAP");
    				
    				this.notifyAll();
    			}	
    		}
    		
    		if (mActiveDrawBuffer.mIsValid) {
    			
    			if (redrawTileBitmap)
    			{
    				drawTileBitmap();
    				redrawTileBitmap = false;
    			}
    				
    			scrollTileBitmap();
    			drawCanvas();
    			mNumFrames++;
 
    			long currentTime = System.currentTimeMillis();

    			long elapsedTime = currentTime - mStartTime;

    			if (elapsedTime > 1000) {

    				Log.i("INFO:", "FPS: " + (mNumFrames*1000)/elapsedTime + " Frames: " + mNumFrames + " Times in ms: " + elapsedTime);
    				mNumFrames = 0;
    				mStartTime = System.currentTimeMillis();
    			}
    		}
    	}
    }
    
 // Iterate over all draw events until queue is found to be empty
    private void processDrawEvents() {
    	
    	// Poll draw events until no more to poll
    	for (;;) {
            DrawEvent event = mEventQueue.poll();
            if (event == null)
                break;
            
            // Check keyboard events
            if (event instanceof SurfaceChangedDrawEvent) {
                SurfaceChangedDrawEvent surfaceChangedDrawEvent = (SurfaceChangedDrawEvent)event;
                
                doSurfaceChanged(surfaceChangedDrawEvent.mHolder, surfaceChangedDrawEvent.mWidth, surfaceChangedDrawEvent.mHeight);
        
            }
    	}
    }
    
    private void resizeTileBitmap(int widthTiles, int heightTiles) {
    	
		mTileBitmap.mWidth = widthTiles * mBitmaps.mTileWidth;
		mTileBitmap.mHeight = heightTiles * mBitmaps.mTileHeight;
		
		mTileBitmap.mBitmap[0] = Bitmap.createBitmap(mTileBitmap.mWidth, mTileBitmap.mHeight, Bitmap.Config.ARGB_8888);
		mTileBitmap.mBitmap[1] = Bitmap.createBitmap(mTileBitmap.mWidth, mTileBitmap.mHeight, Bitmap.Config.ARGB_8888);
		
		mTileBitmap.mCanvas[0] = new Canvas(mTileBitmap.mBitmap[0]);
		mTileBitmap.mCanvas[1] = new Canvas(mTileBitmap.mBitmap[1]);
	}
    
    private void drawTileBitmap() {
    	// Passing in a deltaX and deltaY equivalent to the dimensions of the bitmap itself
    	// This will force the entire tile bitmap to be redrawn
    	drawTileBitmap(mTileBitmap.mCanvas[mTileBitmap.mActiveBitmap], mTileBitmap.mWidth, mTileBitmap.mHeight);
    }
    
    // Draws a subset of the tile bitmap's tiles based on deltaX and deltaY parameters
    // e.g., deltaX indicates how much the bitmap has shifted left (if negative) or right since its previous position
    // Only blocks on the edges of the tile bitmap will be redrawn
    private void drawTileBitmap(Canvas canvas, int deltaX, int deltaY) {
 
    	int widthTiles = mTileBitmap.mWidth/mBitmaps.mTileWidth;
    	int heightTiles = mTileBitmap.mHeight/mBitmaps.mTileHeight;

    	// Loop counters
    	int curPixelX, curPixelY; // Current pixel location where a tile will be drawn
    	int curTileX, curTileY; // Tile indices in map of tile to be drawn
    	int curDeltaX, curDeltaY; // As edge rows and columns are draw, our delta positions will approach 0
    	int tilesToCheckX, tilesToCheckY; // Number of tiles to scan over in each direction
    	
    	curDeltaX = deltaX;
    	tilesToCheckX = widthTiles;
    	
    	if (deltaX < 0) {
    		// Scan tiles from left to right
    		curPixelX = 0;
    		curTileX = mTileBitmap.mLeft/mBitmaps.mTileWidth;
    	}
    	else {
    		// Scan tiles from right to left
    		curPixelX = mTileBitmap.mWidth - mBitmaps.mTileWidth;
    		curTileX = (mTileBitmap.mLeft/mBitmaps.mTileWidth) + (widthTiles - 1);
    	}
    	
    	while (tilesToCheckX > 0)
    	{
    		curDeltaY = deltaY;
    		tilesToCheckY = heightTiles;
    		
    		if (deltaY < 0) {
    			// Scan tiles from top to bottom
    			curPixelY = 0;
    			curTileY = mTileBitmap.mTop/mBitmaps.mTileHeight;
    		}
    		else {
    			// Scan tiles from bottom to top
    			curPixelY = mTileBitmap.mHeight - mBitmaps.mTileHeight;
    			curTileY = (mTileBitmap.mTop/mBitmaps.mTileHeight) + (heightTiles - 1);
    		}
    		
    		while (tilesToCheckY > 0)
    		{	
    			if ((curDeltaX != 0 || curDeltaY != 0) && curTileX < mGameData.mMapWidthTiles && curTileY < mGameData.mMapHeightTiles)
    			{
    				canvas.drawBitmap(mBitmaps.mRoomBitmap[mGameData.mTiles[curTileX][curTileY]], curPixelX, curPixelY, null);
    			}
    			
    			if (deltaY < 0) {
    				// Increment y axis variables (move down one tile column)
    				curPixelY+=mBitmaps.mTileHeight;
    				curTileY++;
    				curDeltaY+=mBitmaps.mTileHeight;
    				if (curDeltaY > 0)
    					curDeltaY = 0;
    			}
    			else {
    				// Decrement y axis variables (move up one tile column)
    				curPixelY-=mBitmaps.mTileHeight;
    				curTileY--;
    				curDeltaY-=mBitmaps.mTileHeight;
    				if (curDeltaY < 0)
    					curDeltaY = 0;
    			}
    			
    			tilesToCheckY--;
    		}
    		
    		if (deltaX < 0) {
    			// Increment x axis variables (move right one tile column)
				curPixelX+=mBitmaps.mTileWidth;
				curTileX++;
				curDeltaX+=mBitmaps.mTileWidth;
				if (curDeltaX > 0)
					curDeltaX = 0;
			}
			else {
				// Decrement x axis variables (move left one tile column)
				curPixelX-=mBitmaps.mTileWidth;
				curTileX--;
				curDeltaX-=mBitmaps.mTileWidth;
				if (curDeltaX < 0)
					curDeltaX = 0;
			}
    		
    		tilesToCheckX--;
    	}
    }
    
    // Check if tile bitmap no longer encloses viewable surface, scrolling the tile bitmap if necessary
    private void scrollTileBitmap() {
    	
    	int oldLeft = mTileBitmap.mLeft;
    	int oldTop = mTileBitmap.mTop;
    	
    	int surfaceRight = mActiveDrawBuffer.mCameraLeft + mGameSurface.mWidth - 1;
    	int tileBitmapRight = mTileBitmap.mLeft + mTileBitmap.mWidth - 1;
    	
    	int surfaceBottom = mActiveDrawBuffer.mCameraTop + mGameSurface.mHeight - 1;
    	int tileBitmapBottom = mTileBitmap.mTop + mTileBitmap.mHeight - 1;
    	
    	int gameMapRight = (mGameData.mMapWidthTiles * mBitmaps.mTileWidth) - 1;
    	int gameMapBottom = (mGameData.mMapHeightTiles * mBitmaps.mTileHeight) - 1;
    	
    	// Shift left border if it is to the right of the surface's left position
    	if (mTileBitmap.mLeft > mActiveDrawBuffer.mCameraLeft) {
    		while (mTileBitmap.mLeft >= mActiveDrawBuffer.mCameraLeft)
    			mTileBitmap.mLeft-=mBitmaps.mTileWidth;
    	}
    	else if (tileBitmapRight < surfaceRight)
    	{
    		// Shift the right border right if it is to the left of the surface's right position
    		while (tileBitmapRight <= surfaceRight)
    		{
    			tileBitmapRight+=mBitmaps.mTileWidth;
    			mTileBitmap.mLeft+=mBitmaps.mTileWidth;
    		}
    		
    		if (tileBitmapRight > gameMapRight)
    			mTileBitmap.mLeft = (gameMapRight - mTileBitmap.mWidth) + 1;
    	}

        if (mTileBitmap.mLeft < 0)
            mTileBitmap.mLeft = 0;
    	
    	// Shift top border if it is below the surface's top position
    	if (mTileBitmap.mTop > mActiveDrawBuffer.mCameraTop) {
    		while (mTileBitmap.mTop >= mActiveDrawBuffer.mCameraTop)
    			mTileBitmap.mTop-=mBitmaps.mTileHeight;
    	}
    	else if (tileBitmapBottom < surfaceBottom)
    	{
    		// Shift the bottom border down if it is above the surface's bottom position
    		while (tileBitmapBottom <= surfaceBottom)
    		{
    			tileBitmapBottom+=mBitmaps.mTileHeight;
    			mTileBitmap.mTop+=mBitmaps.mTileHeight;
    		}
    		
    		if (tileBitmapBottom > gameMapBottom)
                mTileBitmap.mTop = (gameMapBottom - mTileBitmap.mHeight) + 1;
    	}

        if (mTileBitmap.mTop < 0)
            mTileBitmap.mTop = 0;
    	
    	// If the tile bitmap has moved in either direction, it will be necessary to scroll
    	if (mTileBitmap.mLeft != oldLeft || mTileBitmap.mTop != oldTop)
    	{
    		// Scroll the portion of the tile bitmap that will still reside in the new bitmap
    		// This is done so that it will not be necessary to redraw ALL the tiles individually
    		int deltaX = mTileBitmap.mLeft - oldLeft;
    		int deltaY = mTileBitmap.mTop - oldTop;
    		
    		mSrcRect.set(0, 0, mTileBitmap.mWidth, mTileBitmap.mHeight);
    		mDestRect.set(0, 0, mTileBitmap.mWidth, mTileBitmap.mHeight);
    		
    		if (deltaX < 0) {
    			mSrcRect.right+=deltaX;
    			mDestRect.left-=deltaX;
    		}
    		else if (deltaX > 0) {
    			mSrcRect.left+=deltaX;
    			mDestRect.right-=deltaX;
    		}
    		
    		if (deltaY < 0) {
    			mSrcRect.bottom+=deltaY;
    			mDestRect.top-=deltaY;
    		}
    		else if (deltaY > 0) {
    			mSrcRect.top+=deltaY;
    			mDestRect.bottom-=deltaY;
    		}
    		
    		// Copy a portion of the active bitmap to the inactive bitmap and activate the inactive bitmap
    		// This is done because bad things happen when you copy a bitmap onto itself
    		mTileBitmap.mCanvas[mTileBitmap.mInactiveBitmap].drawBitmap(mTileBitmap.mBitmap[mTileBitmap.mActiveBitmap], mSrcRect, mDestRect, null);
    		
    		int temp = mTileBitmap.mActiveBitmap;
    		mTileBitmap.mActiveBitmap = mTileBitmap.mInactiveBitmap;
    		mTileBitmap.mInactiveBitmap = temp;
    		
    		// Draw the newly visible tiles based on the deltaX and deltaY values just computed
    		drawTileBitmap(mTileBitmap.mCanvas[mTileBitmap.mActiveBitmap], deltaX, deltaY);
    	}
    }
    
    private void drawCanvas()
    {
    	int srcLeft = mActiveDrawBuffer.mCameraLeft - mTileBitmap.mLeft;
    	int srcTop = mActiveDrawBuffer.mCameraTop - mTileBitmap.mTop;
    	
    	mSrcRect.set(srcLeft, srcTop, srcLeft + mGameSurface.mWidth, srcTop + mGameSurface.mHeight);
    	mDestRect.set(0, 0, mGameSurface.mWidth, mGameSurface.mHeight);
    	
    	// Draw the visible portion of the active tile bitmap onto the game surface and post
    	mGameSurface.mCanvas = mGameSurface.mSurfaceHolder.lockCanvas(null);
    	mGameSurface.mCanvas.drawBitmap(mTileBitmap.mBitmap[mTileBitmap.mActiveBitmap], mSrcRect, mDestRect, null);
    	
    	drawSprites();
    	
    	drawAlertLevel();
    	
    	mGameSurface.mSurfaceHolder.unlockCanvasAndPost(mGameSurface.mCanvas);
    }
    
    private void drawSprites()
    { 
    	for (int i = 0; i < mActiveDrawBuffer.mNumObjects; ++i) {
    		DrawBuffer.DrawObject drawObject = mActiveDrawBuffer.mDrawObjects[i];
    		
    		Bitmap bitmap = null;
    		switch (drawObject.mType) {
    		case DrawBuffer.CHARACTER_TYPE:
    			bitmap = mBitmaps.mCharacterBitmap[drawObject.mBitmap];
    			break;
    		case DrawBuffer.TARGET_TYPE:
    			bitmap = mBitmaps.mTargetBitmap;
    			break;
    		case DrawBuffer.OBJECT_TYPE:
    			bitmap = mBitmaps.mObjectBitmap[drawObject.mBitmap];
    			break;
    		case DrawBuffer.ITEM_TYPE:
    			bitmap = mBitmaps.mItemBitmap[drawObject.mBitmap];
    			break;
    		case DrawBuffer.MAGNIFYING_GLASS_TYPE:
    			bitmap = mBitmaps.mMagnifyingGlassBitmap;
    			break;
    		case DrawBuffer.ACTION_TYPE:
    			bitmap = mBitmaps.mActionBitmap[drawObject.mBitmap];
    			break;
    		}
    		
    		if (drawObject.mLeft + bitmap.getWidth() > mActiveDrawBuffer.mCameraLeft &&
    				drawObject.mLeft < mActiveDrawBuffer.mCameraLeft + mGameSurface.mWidth &&
    				drawObject.mBottom > mActiveDrawBuffer.mCameraTop &&
    				drawObject.mBottom < mActiveDrawBuffer.mCameraTop + mGameSurface.mHeight + bitmap.getHeight())
    			mGameSurface.mCanvas.drawBitmap(bitmap, drawObject.mLeft - mActiveDrawBuffer.mCameraLeft, (drawObject.mBottom - (bitmap.getHeight()-1)) - mActiveDrawBuffer.mCameraTop, null);
    	}
    }
    
    private void drawAlertLevel()
    {
    	if (mActiveDrawBuffer.mAlertColor == GREEN_ALERT)
    		mAlertLevelPaint.setColor(Color.rgb(0, 215, 0));
    	else if (mActiveDrawBuffer.mAlertColor == YELLOW_ALERT)
    		mAlertLevelPaint.setColor(Color.rgb(255, 219, 88));
    	else
    		mAlertLevelPaint.setColor(Color.rgb(215, 0, 0));
    	
    	mGameSurface.mCanvas.drawText("Alert Level: " + mActiveDrawBuffer.mAlertLevel, 5, mGameSurface.mHeight - 5, mAlertLevelPaint);
    }
}
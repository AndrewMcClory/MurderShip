package com.amcclory77.murdership;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MurderShip extends Activity implements SurfaceHolder.Callback, OnClickListener, OnKeyListener {

	private SurfaceView mSurfaceView;

	private TextView mTitleText;
	private TextView mCreditsText;

	private MurderShipGameData mGameData;

	private MurderShipGameRunnable mGameRunnable;
	private MurderShipDrawRunnable mDrawRunnable;

	private Thread mGameThread;
	private Thread mDrawThread;

	private boolean mSurfaceCreated = false;

	public static final int NO_DIALOG = -1;
	public static final int CHARACTER_DIALOG = 0;
	public static final int ITEM_DIALOG = 1;
	public static final int OBJECT_DIALOG = 2;
	public static final int RESPONSE_DIALOG = 3;

	private int mDialogID;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    	mDialogID = NO_DIALOG;

        setContentView(R.layout.main);
        mSurfaceView = (SurfaceView)findViewById(R.id.MurderShipView);
        mSurfaceView.getHolder().addCallback(this);

        Button newGameButton = (Button)findViewById(R.id.NewGameButton);
        newGameButton.setOnClickListener(this);

        Button continueGameButton = (Button)findViewById(R.id.ContinueGameButton);
        continueGameButton.setOnClickListener(this);

        mTitleText = (TextView)findViewById(R.id.TitleText);
        mCreditsText = (TextView)findViewById(R.id.CreditsText);

        mGameData = new MurderShipGameData(mSurfaceView.getContext());

        mDrawRunnable = new MurderShipDrawRunnable(mGameData, mSurfaceView.getHolder());
		mGameRunnable = new MurderShipGameRunnable(this, mGameData, mDrawRunnable);
    }

    private void saveGameState() {

    	try {
			FileOutputStream fos = openFileOutput(getResources().getString(R.string.STATE_FILENAME), Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(mGameData.mMap);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void loadGameState() {
    	try {
    		FileInputStream fis = openFileInput(getResources().getString(R.string.STATE_FILENAME));
    		ObjectInputStream ois = new ObjectInputStream(fis);
    		MurderShipGameData.GameMap savedGameMap = (MurderShipGameData.GameMap)ois.readObject();
    		mGameData.setGameMap(savedGameMap);

            Button continueGameButton = (Button)findViewById(R.id.ContinueGameButton);
    		continueGameButton.setEnabled(true);
    		ois.close();
        	fis.close();
    	} catch (Exception e) {
    		mGameData.setRandomMap();
            Button continueGameButton = (Button)findViewById(R.id.ContinueGameButton);
    		continueGameButton.setEnabled(false);
    	}
    }

    @Override
    public void onPause() {
    	super.onPause();
    	killThreads();
    	saveGameState();
    }

    @Override
    public void onResume() {
    	super.onResume();
    	loadGameState();
    	startThreads();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	killThreads();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK || mTitleText.getVisibility() != View.INVISIBLE) {
            return super.onKeyDown(keyCode, msg);
        } else {
        	super.onKeyDown(keyCode, msg);
        	(mGameRunnable).doKeyDown(keyCode, msg);
        	return false;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK || mTitleText.getVisibility() != View.INVISIBLE) {
            return super.onKeyUp(keyCode, msg);
        } else {
        	return mGameRunnable.doKeyUp(keyCode, msg);
        }
    }

    public boolean onTouchEvent (MotionEvent event) {

    	if (mTitleText.getVisibility() == View.INVISIBLE &&
    			(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE))
    		return mGameRunnable.doTouchEvent(event);
    	else
    		return super.onTouchEvent(event);
    }

    private void startThreads() {
		if (!mGameRunnable.mIsRunning && !mDrawRunnable.mIsRunning && mSurfaceCreated) {

    		mGameThread = new Thread(mGameRunnable);

			mGameRunnable.mIsPaused = mTitleText.getVisibility() != View.INVISIBLE || mDialogID != NO_DIALOG;

    		mGameRunnable.mIsRunning = true;
    		mGameThread.start();

    		mDrawThread = new Thread(mDrawRunnable);
    		mDrawRunnable.mIsRunning = true;
    		mDrawThread.start();
    	}
    }

    private void killThreads() {
    	if (mGameRunnable.mIsRunning && mDrawRunnable.mIsRunning) {
    		// Send a signal to the game thread to stop then repeatedly try to join
    		mGameRunnable.mIsRunning = false;
    		boolean retry = true;

    		while (retry) {
    			try {
    				mGameThread.join();
    				retry = false;
    			} catch (InterruptedException ignored) {
    			}
    		}

    		// Send a signal to the draw thread to stop then repeatedly try to join.
    		mDrawRunnable.mIsRunning = false;
    		retry = true;

    		while (retry) try {
				mDrawThread.join();
				retry = false;
			} catch (InterruptedException ignored) {
			}
    	}
    }

    public void surfaceCreated(SurfaceHolder arg0) {
    	mSurfaceCreated = true;

    	startThreads();
    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
    	mSurfaceCreated = false;

    	killThreads();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	killThreads();

    	System.gc();

    	DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        mGameRunnable.doSurfaceChangedEvent(dm, width, height);
        mDrawRunnable.doSurfaceChangedEvent(holder, width, height);

        startThreads();
    }

	//@Override
	public void onClick(View v) {
		if (v.getId() == R.id.NewGameButton)
		{

			setTitleVisibility(View.INVISIBLE);

			killThreads();
			mGameData.setRandomMap();
			startThreads();

			mGameRunnable.mRequestCameraCenter = true;
		}
		else if (v.getId() == R.id.ContinueGameButton)
		{
			setTitleVisibility(View.INVISIBLE);

			mGameRunnable.mIsPaused = false;

			mGameRunnable.mRequestCameraCenter = true;
		}
		else if (v.getId() == R.id.Introduce)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_INTRODUCE);
		}
		else if (v.getId() == R.id.Question)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_QUESTION);
		}
		else if (v.getId() == R.id.Accuse)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_ACCUSE);
		}
		else if (v.getId() == R.id.Examine)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_EXAMINE);
		}
		else if (v.getId() == R.id.PickUp)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_PICKUP);
		}
        else if (v.getId() == R.id.Cancel) {
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_CANCEL);
        }
	}

	private void setTitleVisibility(int visible)
	{
		mTitleText.setVisibility(visible);
		mCreditsText.setVisibility(visible);
        Button newGameButton = (Button)findViewById(R.id.NewGameButton);
		newGameButton.setVisibility(visible);
        Button continueGameButton = (Button)findViewById(R.id.ContinueGameButton);
		continueGameButton.setVisibility(visible);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mGameRunnable.mIsPaused = true;
        Button continueGameButton = (Button)findViewById(R.id.ContinueGameButton);
		continueGameButton.setEnabled(true);
		setTitleVisibility(View.VISIBLE);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		mGameRunnable.mIsPaused = true;
		setTitleVisibility(View.VISIBLE);
		return super.onPrepareOptionsMenu(menu);
	}

	public void launchDialog(int id, String title, String text, boolean criticalText, Bitmap bitmap)
	{
		mDialogID = id;

        mGameRunnable.mIsPaused = true;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = MurderShipDialog.newInstance(id, title, text, criticalText, bitmap);
        newFragment.show(ft, "dialog");
	}

	public void closeDialog()
	{
		if (mDialogID != NO_DIALOG) {
            DialogFragment prev = (DialogFragment)getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                prev.dismiss();
				mDialogID = NO_DIALOG;
            }
        }
		mGameRunnable.mIsPaused = false;
	}

	public void gameOver()
	{
		// Delete the game file, show the main menu, and create a new random map
		deleteFile(getResources().getString(R.string.STATE_FILENAME));
        Button continueGameButton = (Button)findViewById(R.id.ContinueGameButton);
		continueGameButton.setEnabled(false);
		setTitleVisibility(View.VISIBLE);

		killThreads();
		mGameData.setRandomMap();
		startThreads();

		mGameRunnable.mRequestCameraCenter = true;
	}

	//@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_CANCEL);
		}

		return false;
	}
}
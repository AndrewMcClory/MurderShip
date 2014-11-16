package com.amcclory77.murdership;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MurderShipBitmaps {
	
	public final static int CHARACTER_WIDTH_IN_TILES = 1;
	public final static int CHARACTER_HEIGHT_IN_TILES = 2;
	
	public final static int OBJECT_WIDTH_IN_TILES = 1;
	public final static int OBJECT_HEIGHT_IN_TILES = 1;
	
	public final static int ITEM_WIDTH_IN_TILES = 1;
	public final static int ITEM_HEIGHT_IN_TILES = 1;
	
	public final static int ACTION_WIDTH_IN_TILES = 2;
	public final static int ACTION_HEIGHT_IN_TILES = 2;

	private Resources mRes;
	
	public int mTileWidth = 0;
	public int mTileHeight = 0;
	
	public int mNumCharacterRows = 0;
	
	public Bitmap [] mRoomBitmap = null;
	public Bitmap [] mCharacterBitmap = null;
	public Bitmap [] mObjectBitmap = null;
	public Bitmap [] mItemBitmap = null;
	public Bitmap [] mActionBitmap = null;
	public Bitmap mTargetBitmap = null;
	public Bitmap mMagnifyingGlassBitmap = null;
	
	public MurderShipBitmaps(Context context) {
		mRes = context.getResources();
		LoadTarget();
		LoadRooms();
		LoadCharacters();
		LoadObjects();
		LoadItems();
		LoadActions();
		LoadMagnifyingGlass();
	}
	
	private void LoadRooms() {
		Bitmap rooms = BitmapFactory.decodeResource(mRes, R.drawable.rooms);
		
		int widthTiles = rooms.getWidth() / mTileWidth;
		int heightTiles = rooms.getHeight() / mTileHeight;
		
		mRoomBitmap = new Bitmap[widthTiles * heightTiles];
		
		int leftPixel = 0;
		int topPixel = 0;
		int curBitmap = 0;
		
		for (int i = 0; i < widthTiles; ++i) {
			topPixel = 0;
			for (int j = 0; j < heightTiles; ++j) {
				mRoomBitmap[curBitmap] = Bitmap.createBitmap(rooms, leftPixel, topPixel, mTileWidth, mTileHeight, null, false);
				curBitmap++;
				topPixel+=mTileHeight;
			}
			leftPixel+=mTileWidth;
		}
	}
	
	private void LoadCharacters() {
		Bitmap characters = BitmapFactory.decodeResource(mRes, R.drawable.characters);
		
		int characterWidth = mTileWidth * CHARACTER_WIDTH_IN_TILES;
		int characterHeight = mTileHeight * CHARACTER_HEIGHT_IN_TILES;
		
		int widthCharacters = characters.getWidth() / characterWidth;
		int heightCharacters = characters.getHeight() / characterHeight;
		
		mCharacterBitmap = new Bitmap[widthCharacters * heightCharacters];
		
		int leftPixel = 0;
		int topPixel = 0;
		int curCharacter = 0;
		
		for (int i = 0; i < widthCharacters; ++i) {
			topPixel = 0;
			for (int j = 0; j < heightCharacters; ++j) {
				mCharacterBitmap[curCharacter] = Bitmap.createBitmap(characters, leftPixel, topPixel, characterWidth, characterHeight, null, false);
				curCharacter++;
				topPixel+=characterHeight;
			}
			leftPixel+=characterWidth;
		}
		
		mNumCharacterRows = heightCharacters;
	}
	
	private void LoadObjects() {
		Bitmap objects = BitmapFactory.decodeResource(mRes, R.drawable.objects);
		
		int objectWidth = mTileWidth * OBJECT_WIDTH_IN_TILES;
		int objectHeight = mTileHeight * OBJECT_HEIGHT_IN_TILES;
		
		int widthObjects = objects.getWidth() / objectWidth;
		int heightObjects = objects.getHeight() / objectHeight;
		
		mObjectBitmap = new Bitmap[widthObjects * heightObjects];
		
		int leftPixel = 0;
		int topPixel = 0;
		int curObject = 0;
		
		for (int i = 0; i < widthObjects; ++i) {
			topPixel = 0;
			for (int j = 0; j < heightObjects; ++j) {
				mObjectBitmap[curObject] = Bitmap.createBitmap(objects, leftPixel, topPixel, objectWidth, objectHeight, null, false);
				
				curObject++;
				topPixel+=objectHeight;
			}
			leftPixel+=objectWidth;
		}
	}
	
	private void LoadItems() {
		Bitmap items = BitmapFactory.decodeResource(mRes, R.drawable.items);
		
		int itemWidth = mTileWidth * ITEM_WIDTH_IN_TILES;
		int itemHeight = mTileHeight * ITEM_HEIGHT_IN_TILES;
		
		int widthItems = items.getWidth() / itemWidth;
		int heightItems = items.getHeight() / itemHeight;
		
		mItemBitmap = new Bitmap[widthItems * heightItems];
		
		int leftPixel = 0;
		int topPixel = 0;
		int curItem = 0;
		
		for (int i = 0; i < widthItems; ++i) {
			topPixel = 0;
			for (int j = 0; j < heightItems; ++j) {
				mItemBitmap[curItem] = Bitmap.createBitmap(items, leftPixel, topPixel, itemWidth, itemHeight, null, false);
				curItem++;
				topPixel+=itemHeight;
			}
			leftPixel+=itemWidth;
		}
	}
	
	private void LoadActions() {
		
		Bitmap actions = BitmapFactory.decodeResource(mRes, R.drawable.actions);
		
		int actionWidth = mTileWidth * ACTION_WIDTH_IN_TILES;
		int actionHeight = mTileHeight * ACTION_HEIGHT_IN_TILES;
		
		int widthActions = actions.getWidth() / actionWidth;
		int heightActions = actions.getHeight() / actionHeight;
		
		mActionBitmap = new Bitmap[widthActions * heightActions];
		
		int leftPixel = 0;
		int topPixel = 0;
		int curAction = 0;
		
		for (int i = 0; i < widthActions; ++i) {
			topPixel = 0;
			for (int j = 0; j < heightActions; ++j) {
				mActionBitmap[curAction] = Bitmap.createBitmap(actions, leftPixel, topPixel, actionWidth, actionHeight, null, false);
				curAction++;
				topPixel+=actionHeight;
			}
			leftPixel+=actionWidth;
		}
	}
	
	private void LoadTarget() {
		mTargetBitmap = BitmapFactory.decodeResource(mRes, R.drawable.target);
		mTileWidth = mTargetBitmap.getWidth();
		mTileHeight = mTargetBitmap.getHeight();
	}
	
	private void LoadMagnifyingGlass() {
		mMagnifyingGlassBitmap = BitmapFactory.decodeResource(mRes, R.drawable.magnifyingglass);
	}
}

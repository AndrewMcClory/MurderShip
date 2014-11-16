package com.amcclory77.murdership;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import android.content.Context;

public class MurderShipGameData {
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	public static final int ALERT_OF_CHANGING_ROOMS = 2;
	public static final int ALERT_OF_ASKING_QUESTIONS_BEFORE_BODY = 10;
	public static final int ALERT_OF_ASKING_QUESTIONS_AFTER_BODY = 30;
	public static final int ALERT_OF_SEARCHING_FOR_BODY = 15;
	public static final int ALERT_OF_FINDING_BODY = 30;
	public static final int ALERT_OF_SEARCHING_FOR_WEAPON = 15;
	public static final int ALERT_OF_FINDING_WEAPON = 30;
	public static final int ALERT_OF_FALSELY_ACCUSING = 1000;
	public static final int TIME_FOR_AUTOMATIC_ALERT_INCREASE = 3000;
	
	public static final int YELLOW_ALERT_LEVEL = 500;
	public static final int RED_ALERT_LEVEL = 1000;
	
	Random mRandomNumGenerator;
	
	public MurderShipBitmaps mBitmaps;
	
	// The minimum required state to save and restore game state
	static public class GameMap implements Serializable {
		
			private static final long serialVersionUID = 1L;
			
			public static final int DEFAULT_WIDTH_IN_ROOM_SECTIONS = 10;
			public static final int DEFAULT_HEIGHT_IN_ROOM_SECTIONS = 10;
			
			public static final int DEFAULT_ROOM_WIDTH_IN_TILES = 6;
			public static final int DEFAULT_ROOM_HEIGHT_IN_TILES = 6;
			
			public static final int PRE_INTRO_POINT = 0;
			public static final int POST_INTRO_POINT = 1;
			public static final int PRE_YELLOW_ALERT_POINT = 2;
			public static final int POST_YELLOW_ALERT_POINT = 3;
			public static final int PRE_RED_ALERT_POINT = 4;
			public static final int POST_RED_ALERT_POINT = 5;
			public static final int PRE_ACCUSE_POINT = 6;
			public static final int POST_ACCUSE_POINT = 7;
			public static final int PRE_ARREST_POINT = 8;
			public static final int POST_ARREST_POINT = 9;
			public static final int PRE_RAMPAGE_POINT = 10;
			public static final int POST_RAMPAGE_POINT = 11;
			public static final int PRE_HERO_MURDERED_POINT = 12;
			public static final int POST_HERO_MURDERED_POINT = 13;
			public static final int PRE_LOSE_POINT = 14;
			public static final int POST_LOSE_POINT = 15;
			public static final int PRE_WIN_POINT = 16;
			public static final int POST_WIN_POINT = 17;
			
			public int mWidthInRoomSections;
			public int mHeightInRoomSections;
			
			public int mRoomWidthInTiles;
			public int mRoomHeightInTiles;
			
			public int mTileWidth;
			public int mTileHeight;
			
			static public class RoomDesc implements Serializable {
				
				private static final long serialVersionUID = 1L;
				
				public int mWidthRoomSections;
				public int mHeightRoomSections;
				public int mWallBitmap;
				public int mFloorBitmap;
				public int mLeftRoomSection;
				public int mTopRoomSection;
				
				public RoomDesc(int widthRoomSections, int heightRoomSections, int wallBitmap, int floorBitmap) {
					mWidthRoomSections = widthRoomSections;
					mHeightRoomSections = heightRoomSections;
					mWallBitmap = wallBitmap;
					mFloorBitmap = floorBitmap;
					
					invalidateLocation();
				}
				
				public boolean isValidLocation() {
					return mLeftRoomSection >= 0 && mTopRoomSection >= 0;
				}
				
				public void invalidateLocation() {
					mLeftRoomSection = -1;
					mTopRoomSection = -1;
				}
			}
			
			public int mNumRooms;
			public RoomDesc [] mRoomDescs;
			
			static public class SectionCoordinates {
				int mX;
				int mY;
				
				public SectionCoordinates(int x, int y)
				{
					mX = x;
					mY = y;
				}
			}
			
			static public class LocationObject implements Serializable {
				private static final long serialVersionUID = 1L;
				public int mLeft = 0;
				public int mBottom = 0;
				public boolean mVisible = true;
			}
			
			static public class InanimateObject extends LocationObject implements Serializable {
				
				private static final long serialVersionUID = 1L;
				
				public static final int OBJECT_TYPE = 1;
				public static final int ITEM_TYPE = 2;
				public static final int MAGNIFYING_GLASS_TYPE = 3;

				public int mType;
				public int mBitmap;
				public int mStringIndex;
				public boolean mUsedInMurder;
				
				public InanimateObject(int left, int bottom, int type, int bitmap, int stringIndex) {
					mLeft = left;
					mBottom = bottom;
					mType = type;
					mBitmap = bitmap;
					mStringIndex = stringIndex;
					mUsedInMurder = false;
				}
			}

			static public class AnimateObject extends LocationObject implements Serializable {
				
				private static final long serialVersionUID = 1L;
				
				private static final int MAX_DISTANCE_TO_EXIT = 100;

				public static final int UNASSIGNED_STATE = 0;
				public static final int HERO_TRAVELING_STATE = 1;
				public static final int SUSPECT_TRAVELING_STATE = 2;
				public static final int GO_TO_TARGET = 3;
				public static final int DEAD_STATE = 4;
				public static final int STALKER_UNASSIGNED_STATE = 5;
				public static final int STALKER_TRAVELING_STATE = 6;

				public static final int HERO_TYPE = 0;
				public static final int SUSPECT_TYPE = 1;
				public static final int TARGET_TYPE = 2;
				
				public static final int INNOCENT_ROLE = 0;
				public static final int VICTIM_ROLE = 1;
				public static final int MURDERER_ROLE = 2;
				
				public int mState;

				public int mType;
				public int mSpeed;
				public int mBitmap;
				public int mAnimation;
				public int mCharacterStringIndex;
				
				public int mDestLeft;
				public int mDestBottom;

				public long mMinTimeForUpdate;

				public long mTimeSinceLastUpdate;
				
				public int mRole;
				
				public int mAlibiCharacterStringIndex;
				public int mAlibiStringIndex;
				public int mAccuseCharacterStringIndex;

				public AnimateObject(int left, int bottom, int offsetBase, int type, int speed, int bitmap, int conversationIndex, long minTimeForUpdate) {
					mLeft = left;
					mBottom = bottom;
					mType = type;
					mSpeed = speed;
					mBitmap = bitmap;
					mAnimation = 0;
					mCharacterStringIndex = conversationIndex;
					mMinTimeForUpdate = minTimeForUpdate;

					mDestLeft = left;
					mDestBottom = bottom;
					
					mTimeSinceLastUpdate = System.currentTimeMillis();
					mState = UNASSIGNED_STATE;
					
					mRole = INNOCENT_ROLE;
					mAlibiCharacterStringIndex = -1;
					mAlibiStringIndex = -1;
					mAccuseCharacterStringIndex = -1;
				}
			}
			
			static public class ActionObject extends LocationObject implements Serializable
			{
				private static final long serialVersionUID = 1L;
				
				private static final int TALK_BITMAP = 0;
				private static final int LOOK_BITMAP = 1;
				private static final int MAGNIFYING_BITMAP = 2;
				
				int mTarget;
				int mBitmap;
				
				public ActionObject()
				{
					mTarget = -1;
					mBitmap = -1;
					mVisible = false;
				}
			}

			public int mNumGameObjects = 0;
			public LocationObject [] mGameObjects;
			
			public int mTargetIndex = -1;
			public int mHeroIndex = -1;
			public int mActionIndex = -1;
			public int mVictimIndex = -1;
			public int mMurdererIndex = -1;
			public int mMurderWeaponIndex = -1;
			
			private int mAlertLevel = 0;
			private boolean mHaveMagnifyingGlass = false;
			private boolean mFoundVictim = false;
			private boolean mFoundWeapon = false;
			private boolean mHaveWeapon = false;
			public int mStoryPoint = PRE_INTRO_POINT;

			
			public GameMap(int tileWidth, int tileHeight)
			{
				mTileWidth = tileWidth;
				mTileHeight = tileHeight;
			}
			
			private void generateRandomMap() {
				mWidthInRoomSections = DEFAULT_WIDTH_IN_ROOM_SECTIONS;
				mHeightInRoomSections = DEFAULT_HEIGHT_IN_ROOM_SECTIONS;

				mRoomWidthInTiles = DEFAULT_ROOM_WIDTH_IN_TILES;
				mRoomHeightInTiles = DEFAULT_ROOM_HEIGHT_IN_TILES;

				generateDefaultRoomDescs();
				generateRandomRoomLocs();
				generateRandomGameObjects();
			}

			private void generateDefaultRoomDescs() {
				mNumRooms = 20;
				mRoomDescs = new RoomDesc[mNumRooms];

				mRoomDescs[0] =  new RoomDesc(1, 1, 2, 19);
				mRoomDescs[1] =  new RoomDesc(1, 2, 3, 19);
				mRoomDescs[2] =  new RoomDesc(2, 1, 4, 18);
				mRoomDescs[3] =  new RoomDesc(2, 2, 5, 18);
				mRoomDescs[4] =  new RoomDesc(1, 1, 6, 17);
				mRoomDescs[5] =  new RoomDesc(1, 2, 7, 17);
				mRoomDescs[6] =  new RoomDesc(2, 1, 8, 16);
				mRoomDescs[7] =  new RoomDesc(2, 2, 9, 16);
				mRoomDescs[8] =  new RoomDesc(1, 1, 2, 15);
				mRoomDescs[9] =  new RoomDesc(1, 2, 3, 15);
				mRoomDescs[10] =  new RoomDesc(2, 1, 4, 14);
				mRoomDescs[11] =  new RoomDesc(2, 2, 5, 14);
				mRoomDescs[12] =  new RoomDesc(1, 3, 6, 13);
				mRoomDescs[13] =  new RoomDesc(3, 1, 7, 13);
				mRoomDescs[14] =  new RoomDesc(1, 3, 8, 12);
				mRoomDescs[15] =  new RoomDesc(3, 1, 9, 12);
				mRoomDescs[16] =  new RoomDesc(1, 1, 2, 19);
				mRoomDescs[17] =  new RoomDesc(1, 1, 3, 19);
				mRoomDescs[18] =  new RoomDesc(2, 1, 4, 18);
				mRoomDescs[19] =  new RoomDesc(1, 2, 5, 18);
			}

			private void generateRandomRoomLocs()
			{
				// Create an Integer array containing all room IDs
				int [] roomIDs = new int[mNumRooms];

				for (int i = 0; i < mNumRooms; ++i) {
					roomIDs[i] = i;
				}

				// Shuffle the array so we place rooms in a random order
				Collections.shuffle(Arrays.asList(roomIDs));

				// Stores room IDs already positioned on the map
				Vector<Integer> positionedRooms = new Vector<Integer>();

				// Matrix indicating which room sections are occupied
				boolean occupiedRoomSections[][] = new boolean[mWidthInRoomSections][mHeightInRoomSections];
				for (int x = 0; x < mWidthInRoomSections; ++x)
				{
					for (int y = 0; y < mHeightInRoomSections; ++y)
					{
						occupiedRoomSections[x][y] = false;
					}
				}

				for (int i = 0; i < mNumRooms; ++i)
				{
					RoomDesc roomDesc = mRoomDescs[roomIDs[i]];

					// Attempt to find a location for this room
					if (findRoomLocation(roomDesc, positionedRooms, occupiedRoomSections))
					{
						positionedRooms.addElement(roomIDs[i]);
						AddRoomToOccupiedMatrix(roomDesc, occupiedRoomSections);
					}
				}
				
				// Rebuild mRoomDescs using only the positioned rooms
				RoomDesc [] positionedRoomDescs = new RoomDesc[positionedRooms.size()];
				for (int i = 0; i < positionedRooms.size(); ++i)
					positionedRoomDescs[i] = mRoomDescs[positionedRooms.elementAt(i)];
				
				mRoomDescs = positionedRoomDescs;
				mNumRooms = positionedRooms.size();
			}

			// Find a location for this room attached to others rooms if any exist
			private boolean findRoomLocation(RoomDesc roomDesc, Vector<Integer> positionedRooms, boolean occupiedRoomSections[][])
			{
				Random randomNumGenerator = new Random();
				
				// If no rooms have been successfully positioned yet, position this one in the center of the map
				if (positionedRooms.isEmpty())
				{
					roomDesc.mLeftRoomSection = (mWidthInRoomSections/2) - (roomDesc.mWidthRoomSections/2);
					roomDesc.mTopRoomSection = (mHeightInRoomSections/2) - (roomDesc.mHeightRoomSections/2);

					if (testRoomLocation(roomDesc, occupiedRoomSections))
						return true;
				}
				else {
					int numPositionedRooms = positionedRooms.size();
					int numRoomSectionsChecked = 0;
					int randomPositionedRoomIndex = randomNumGenerator.nextInt(numPositionedRooms);

					while (numRoomSectionsChecked < numPositionedRooms) {
						RoomDesc roomDescToAttach = mRoomDescs[randomPositionedRoomIndex];
						if  (findRoomLocation(roomDesc, roomDescToAttach, occupiedRoomSections))
							return true;

						++randomPositionedRoomIndex;
						if (randomPositionedRoomIndex >= numPositionedRooms)
							randomPositionedRoomIndex = 0;

						++numRoomSectionsChecked;
					}
				}

				roomDesc.invalidateLocation();
				return false;
			}

			private boolean findRoomLocation(RoomDesc roomDesc1, RoomDesc roomDesc2, boolean occupiedRoomSections[][]) {

				Random randomNumGenerator = new Random();
				
				int numEdgesChecked = 0;
				int randomEdge = randomNumGenerator.nextInt(4);
				while (numEdgesChecked < 4) {
					++randomEdge;
					if (randomEdge >= 4)
						randomEdge = 0;

					if (findRoomLocation(roomDesc1, roomDesc2, randomEdge, occupiedRoomSections))
						return true;

					++numEdgesChecked;
				}

				roomDesc1.invalidateLocation();
				return false;
			}

			private boolean findRoomLocation(RoomDesc roomDesc1, RoomDesc roomDesc2, int edge, boolean occupiedRoomSections[][]) {

				int numPossibleOffsets;
				int randomOffset;
				int numOffsetsChecked = 0;
				Random randomNumGenerator = new Random();

				switch(edge) {
				case UP: // Attach roomDesc1 to top of roomDesc2
					numPossibleOffsets = (roomDesc1.mWidthRoomSections + roomDesc2.mWidthRoomSections) - 1;
					randomOffset = randomNumGenerator.nextInt(numPossibleOffsets);
					roomDesc1.mTopRoomSection = roomDesc2.mTopRoomSection - roomDesc1.mHeightRoomSections;

					while (numOffsetsChecked < numPossibleOffsets) {

						roomDesc1.mLeftRoomSection = (roomDesc2.mLeftRoomSection - roomDesc1.mWidthRoomSections) + 1 + randomOffset;
						if (testRoomLocation(roomDesc1, occupiedRoomSections))
							return true;

						if (randomOffset >= numPossibleOffsets)
							randomOffset = 0;
						++numOffsetsChecked;
					}
					break;
				case DOWN: // Attach roomDesc1 to bottom of roomDesc2
					numPossibleOffsets = (roomDesc1.mWidthRoomSections + roomDesc2.mWidthRoomSections) - 1;
					randomOffset = randomNumGenerator.nextInt(numPossibleOffsets);
					roomDesc1.mTopRoomSection = roomDesc2.mTopRoomSection + roomDesc2.mHeightRoomSections;

					while (numOffsetsChecked < numPossibleOffsets) {

						roomDesc1.mLeftRoomSection = (roomDesc2.mLeftRoomSection - roomDesc1.mWidthRoomSections) + 1 + randomOffset;
						if (testRoomLocation(roomDesc1, occupiedRoomSections))
							return true;

						if (randomOffset >= numPossibleOffsets)
							randomOffset = 0;
						++numOffsetsChecked;
					}
					break;
				case LEFT: // Attach roomDesc1 to left of roomDesc2
					numPossibleOffsets = (roomDesc1.mHeightRoomSections + roomDesc2.mHeightRoomSections) - 1;
					randomOffset = randomNumGenerator.nextInt(numPossibleOffsets);
					roomDesc1.mLeftRoomSection = roomDesc2.mLeftRoomSection - roomDesc1.mWidthRoomSections;

					while (numOffsetsChecked < numPossibleOffsets) {

						roomDesc1.mTopRoomSection = (roomDesc2.mTopRoomSection - roomDesc1.mHeightRoomSections) + 1 + randomOffset;
						if (testRoomLocation(roomDesc1, occupiedRoomSections))
							return true;

						if (randomOffset >= numPossibleOffsets)
							randomOffset = 0;
						++numOffsetsChecked;
					}
					break;
				case RIGHT: // Attach roomDesc1 to right of roomDesc2;
					numPossibleOffsets = (roomDesc1.mHeightRoomSections + roomDesc2.mHeightRoomSections) - 1;
					randomOffset = randomNumGenerator.nextInt(numPossibleOffsets);
					roomDesc1.mLeftRoomSection = roomDesc2.mLeftRoomSection + roomDesc2.mWidthRoomSections;

					while (numOffsetsChecked < numPossibleOffsets) {

						roomDesc1.mTopRoomSection = (roomDesc2.mTopRoomSection - roomDesc1.mHeightRoomSections) + 1 + randomOffset;
						if (testRoomLocation(roomDesc1, occupiedRoomSections))
							return true;

						if (randomOffset >= numPossibleOffsets)
							randomOffset = 0;
						++numOffsetsChecked;
					}
					break;
				}

				roomDesc1.invalidateLocation();
				return false;
			}

			// Tests if a room of the specified location and dimensions can be placed in the room matrix
			private boolean testRoomLocation(RoomDesc roomDesc, boolean occupiedRoomSections[][]) {

				for (int x = roomDesc.mLeftRoomSection; x < roomDesc.mLeftRoomSection + roomDesc.mWidthRoomSections; ++x) {
					for (int y = roomDesc.mTopRoomSection; y < roomDesc.mTopRoomSection + roomDesc.mHeightRoomSections; ++y) {

						// Verify that coordinate actually resides in room vector
						if (x < 0 || x >= mWidthInRoomSections || y < 0 || y >= mHeightInRoomSections)
							return false;
						else if (occupiedRoomSections[x][y])
							return false;
					}
				}
				return true;
			}

			private void AddRoomToOccupiedMatrix(RoomDesc roomDesc, boolean occupiedRoomSections[][]) {
				for (int x = roomDesc.mLeftRoomSection; x < roomDesc.mLeftRoomSection + roomDesc.mWidthRoomSections; ++x) {
					for (int y = roomDesc.mTopRoomSection; y < roomDesc.mTopRoomSection + roomDesc.mHeightRoomSections; ++y) {
						occupiedRoomSections[x][y] = true;
					}
				}
			}

			void generateRandomGameObjects() {
				
				// Initialize game object once for maximum number of game objects to avoid future allocations
				mGameObjects = new LocationObject[MurderShipDrawRunnable.DrawBuffer.MAX_GAME_OBJECTS];
				mNumGameObjects = 0;

				// Create target object
				mTargetIndex = 0;
				mGameObjects[mTargetIndex] = new AnimateObject(0, 0, 0,
						AnimateObject.TARGET_TYPE, 0, 0, -1, 0);
				++mNumGameObjects;

				// Create hero object
				mHeroIndex = 1;
				mGameObjects[mHeroIndex] = new AnimateObject(0, 0,
						(MurderShipBitmaps.CHARACTER_HEIGHT_IN_TILES-1) * mTileHeight,
						AnimateObject.HERO_TYPE, 8, 0, -1, 45);
				++mNumGameObjects;
				
				// Create action items
				mActionIndex = 2;
				mGameObjects[mActionIndex] = new ActionObject();
				++mNumGameObjects;
				
				// Create suspects
				for (int i = 0; i < 10; ++i)
				{
					mGameObjects[mNumGameObjects++] = new AnimateObject(0, 0,
							(MurderShipBitmaps.CHARACTER_HEIGHT_IN_TILES-1) * mTileHeight,
							AnimateObject.SUSPECT_TYPE, 4, i + 1, i, 45);
				}
				
				// Create objects
				for (int i = 0; i < 10; ++i)
				{	
					mGameObjects[mNumGameObjects++] = new InanimateObject(0, 0, InanimateObject.OBJECT_TYPE, i, i);
				}
				
				// Create magnifying class item
				mGameObjects[mNumGameObjects++] = new InanimateObject(0, 0, InanimateObject.MAGNIFYING_GLASS_TYPE, 0, 0);
				
				// Create murder items
				for (int i = 0; i < 9; ++i)
				{
					mGameObjects[mNumGameObjects++] = new InanimateObject(0, 0, InanimateObject.ITEM_TYPE, i, i);
				}

				// Shuffle location of all game objects
				placeObjectsInRandomRooms();
				
				// Set murderer, victim, and alibi data for all suspects
				generateSuspectRoles();
				
				mGameObjects[0].mLeft = mGameObjects[1].mLeft;
				mGameObjects[0].mBottom = mGameObjects[1].mBottom;
			}

			private void placeObjectsInRandomRooms() {

				// Stores location object indices already positioned
				Vector<Integer> positionedObjectIndices = new Vector<Integer>();
				positionedObjectIndices.addElement(0);

				boolean [][] occupiedSections;
				
				// Record occupied tiles
				occupiedSections = new boolean[mWidthInRoomSections][mHeightInRoomSections];

				// Initialize occupied tile to false
				for (int x = 0; x < mWidthInRoomSections; ++x)
					for (int y = 0; y < mHeightInRoomSections; ++y)
						occupiedSections[x][y] = false;
				
				SectionCoordinates sectionCoordinates = new SectionCoordinates(-1, -1);

				// Place all game objects in random rooms
				for (int i = 1; i < mNumGameObjects; ++i) {

					LocationObject gameObject = mGameObjects[i];
					
					if (getRandomUnoccupiedSection(occupiedSections, sectionCoordinates))
					{
						gameObject.mLeft = getLeftCenterOfRoomSection(sectionCoordinates.mX);
						gameObject.mBottom = getBottomCenterOfRoomSection(sectionCoordinates.mY);
						occupiedSections[sectionCoordinates.mX][sectionCoordinates.mY] = true;
						positionedObjectIndices.addElement(i);
					}
				}
				
				// Rebuild mGameObjects using only the positioned rooms
				LocationObject [] positionedObjectDescs = new LocationObject[positionedObjectIndices.size()];
				for (int i = 0; i < positionedObjectIndices.size(); ++i)
					positionedObjectDescs[i] = mGameObjects[positionedObjectIndices.elementAt(i)];
				
				mGameObjects = positionedObjectDescs;
				mNumGameObjects = positionedObjectIndices.size();
				
			}
			
			public boolean getRandomUnoccupiedSection(boolean [][] occupiedSections, SectionCoordinates sectionCoordinates)
			{
				Random randomNumGenerator = new Random();
				
				int randomRoomID = randomNumGenerator.nextInt(mNumRooms);
				int numRoomsChecked = 0;
				
				while (numRoomsChecked < mNumRooms) {
					
					// Search for a valid room
					RoomDesc roomDesc = mRoomDescs[randomRoomID];
					
					// Choose a random room section
					int leftSectionOffset = randomNumGenerator.nextInt(roomDesc.mWidthRoomSections);
					int bottomSectionOffset = randomNumGenerator.nextInt(roomDesc.mHeightRoomSections);
					
					int numPossibleSections = roomDesc.mWidthRoomSections * roomDesc.mHeightRoomSections;
					int numSectionsChecked = 0;
					
					// Check all tiles for a place to position the object
					while (numSectionsChecked < numPossibleSections) {
						
						int leftSection = roomDesc.mLeftRoomSection + leftSectionOffset;
						int bottomSection = roomDesc.mTopRoomSection + bottomSectionOffset;
						
						// If this section is not occupied, occupy it with this object
						if (!occupiedSections[leftSection][bottomSection])
						{
							sectionCoordinates.mX = leftSection;
							sectionCoordinates.mY = bottomSection;
							return true;
						}
						
						// Test the next position
						leftSectionOffset++;
						if (leftSectionOffset >= roomDesc.mWidthRoomSections) {
							leftSectionOffset = 0;
							++bottomSectionOffset;
						}
						if (bottomSectionOffset >= roomDesc.mHeightRoomSections) {
							bottomSectionOffset = 0;
						}
						++numSectionsChecked;
					}
					
					++randomRoomID;

					// Cycle through all rooms looking for a valid one
					if (randomRoomID >= mNumRooms)
						randomRoomID = 0;
					
					++numRoomsChecked;
				}
				
				return false;
			}
			
			// Assign random roles to all suspects
			private void generateSuspectRoles()
			{
				Vector<Integer> suspectIndices = new Vector<Integer>();
				
				// Find all suspect game objects and store them in a vector
				for (int i = 0; i < mNumGameObjects; ++i)
				{
					GameMap.LocationObject gameObject = mGameObjects[i];
					
					if (gameObject instanceof GameMap.AnimateObject)
					{
						GameMap.AnimateObject animateObject = (GameMap.AnimateObject)gameObject;
						if (animateObject.mType == GameMap.AnimateObject.SUSPECT_TYPE)
							suspectIndices.addElement(i);
					}
				}
				
				// Shuffle the vector, so that the suspects appear in a random order
				Collections.shuffle(suspectIndices);
				
				// Shuffle the group alibis
				Vector<Integer> groupAlibis = new Vector<Integer>();
				for (int i = 0; i < MurderShipStrings.NUM_GROUP_ALIBIS; ++i)
					groupAlibis.addElement(i);
				Collections.shuffle(groupAlibis);
				int groupAlibiIndex = 0;
				
				// Shuffle the solo alibis
				Vector<Integer> soloAlibis = new Vector<Integer>();
				for (int i = 0; i < MurderShipStrings.NUM_SOLO_ALIBIS; ++i)
					soloAlibis.addElement(i);
				Collections.shuffle(soloAlibis);
				int soloAlibiIndex = 0;
				
				// Assign roles and alibis based on position in vector
				for (int i = 0; i < suspectIndices.size(); ++i)
				{
					GameMap.AnimateObject suspectObject = (GameMap.AnimateObject)mGameObjects[suspectIndices.elementAt(i)];
					
					if (i == 0)
					{
						// First suspect in vector is the murder victim
						suspectObject.mRole = GameMap.AnimateObject.VICTIM_ROLE;
						mVictimIndex = suspectIndices.elementAt(0);
						murderSuspect(suspectObject);
					}
					else if (i == 1)
					{
						// Second suspect is the murderer
						suspectObject.mRole = GameMap.AnimateObject.MURDERER_ROLE;
						mMurdererIndex = suspectIndices.elementAt(1);
						
						// The murderer will choose the next suspect in the vector with whom to claim an alibi
						// If there are no more suspects, the murderer will choose a solo alibi
						if (i < (suspectIndices.size() - 1))
						{
							suspectObject.mAlibiCharacterStringIndex = getCharacterStringIndex(suspectIndices.elementAt(i + 1));
							suspectObject.mAlibiStringIndex = groupAlibis.elementAt(groupAlibiIndex++);
						}
						else
						{
							suspectObject.mAlibiCharacterStringIndex = suspectObject.mCharacterStringIndex;
							suspectObject.mAlibiStringIndex = soloAlibis.elementAt(soloAlibiIndex++);
						}
					}
					else 
					{
						// All other suspects are innocent
						suspectObject.mRole = GameMap.AnimateObject.INNOCENT_ROLE;
						
						// Even numbered innocent suspects create alibis with the suspects ahead of them in the vector
						// of with themselves if they are the last suspect
						if (i % 2 == 0)
						{
							if (i < (suspectIndices.size() - 1))
							{
								suspectObject.mAlibiCharacterStringIndex = getCharacterStringIndex(suspectIndices.elementAt(i + 1));
								suspectObject.mAlibiStringIndex = groupAlibis.elementAt(groupAlibiIndex++);
								
								// Have the suspect's alibi partner match alibis with the suspect
								GameMap.AnimateObject alibiCharacter = (GameMap.AnimateObject)mGameObjects[suspectIndices.elementAt(i + 1)];
								alibiCharacter.mAlibiCharacterStringIndex = suspectObject.mCharacterStringIndex;
								alibiCharacter.mAlibiStringIndex = suspectObject.mAlibiStringIndex;
							}
							else
							{
								suspectObject.mAlibiCharacterStringIndex = suspectObject.mCharacterStringIndex;
								suspectObject.mAlibiStringIndex = soloAlibis.elementAt(soloAlibiIndex++);
							}		
						}
					}
					
					// Each suspect blames the one two ahead of them in the list
					int accusePos = i + 2;
					if (accusePos >= suspectIndices.size())
						accusePos = (accusePos - suspectIndices.size()) + 1;
					
					suspectObject.mAccuseCharacterStringIndex = getCharacterStringIndex(suspectIndices.elementAt(accusePos));
					
					if (groupAlibiIndex >= MurderShipStrings.NUM_GROUP_ALIBIS)
						groupAlibiIndex = 0;
					else if (soloAlibiIndex >= MurderShipStrings.NUM_SOLO_ALIBIS)
						soloAlibiIndex = 0;
				}
			}
			
			// Return the character's string index
			public int getCharacterStringIndex(int objectIndex)
			{
				if (objectIndex < 0 || objectIndex >= mNumGameObjects)
					return -1;
				
				GameMap.LocationObject gameObject = mGameObjects[objectIndex];
				
				if (gameObject instanceof AnimateObject)
				{
					AnimateObject animateObject = (AnimateObject)mGameObjects[objectIndex];
					
					return animateObject.mCharacterStringIndex;
				}
				else
					return -1;
			}
			
			private void murderSuspect(AnimateObject animateObject)
			{
				animateObject.mState = AnimateObject.DEAD_STATE;
				
				// Choose a random object in which to hide the body
				int murderObjectIndex = getRandomObjectIndex();
				GameMap.InanimateObject object = (GameMap.InanimateObject)mGameObjects[murderObjectIndex];
				// Choose a random item to be the murder weapon
				mMurderWeaponIndex = getRandomItemIndex();
				GameMap.InanimateObject item = (GameMap.InanimateObject)mGameObjects[mMurderWeaponIndex];
				
				animateObject.mLeft = object.mLeft;
				animateObject.mBottom = object.mBottom;
				animateObject.mVisible = false;
				object.mUsedInMurder = true;
				item.mUsedInMurder = true;
			}
			
			private void murderHero()
			{
				AnimateObject heroObject = (AnimateObject)mGameObjects[mHeroIndex];
				heroObject.mAnimation = 2;
				heroObject.mState = AnimateObject.DEAD_STATE;
			}
			
			private int getRandomObjectIndex()
			{
				Vector<Integer> gameObjectIndices = new Vector<Integer>();
				
				// Place all objects in vector
				for (int i = 0; i < mNumGameObjects;++i)
				{
					if (mGameObjects[i] instanceof InanimateObject)
					{
						InanimateObject inanimateObject = (InanimateObject)mGameObjects[i];
						if (inanimateObject.mType == InanimateObject.OBJECT_TYPE)
							gameObjectIndices.addElement(i);	
					}
				}
				
				Random randomNumGenerator = new Random();
				
				if (gameObjectIndices.size() <= 0)
					return -1;
				else
					return gameObjectIndices.elementAt(randomNumGenerator.nextInt(gameObjectIndices.size()));
			}
			
			private int getRandomItemIndex()
			{
				Vector<Integer> gameItemIndices = new Vector<Integer>();
				
				// Place all items in vector
				for (int i = 0; i < mNumGameObjects;++i)
				{
					if (mGameObjects[i] instanceof GameMap.InanimateObject)
					{
						InanimateObject inanimateObject = (InanimateObject)mGameObjects[i];
						if (inanimateObject.mType == InanimateObject.ITEM_TYPE)
							gameItemIndices.addElement(i);	
					}
				}
				
				Random randomNumGenerator = new Random();
				
				if (gameItemIndices.size() <= 0)
					return -1;
				else
					return gameItemIndices.elementAt(randomNumGenerator.nextInt(gameItemIndices.size()));
			}
			
			private boolean objectsOverlap(LocationObject object1, LocationObject object2)
			{
				int left1 = object1.mLeft;
				int right1 = object1.mLeft + mTileWidth;
				int top1 = object1.mBottom - mTileHeight;
				int bottom1 = object1.mBottom;
				
				int left2 = object2.mLeft;
				int right2 = object2.mLeft + mTileWidth;
				int top2 = object2.mBottom - mTileHeight;
				int bottom2 = object2.mBottom;
				
				return (((left1 >= left2 && left1 <= right2) || (right1 >= left2 && right1 <= right2)) &&
						((top1 >= top2 && top1 <= bottom2) || (bottom1 >= top2 && bottom1 <= bottom2)));
			}
			
			public int getLeftCenterOfRoomSection(int leftRoomSection) {
				int left = leftRoomSection * (mRoomWidthInTiles * mTileWidth);
				// Add a half of a room in width to center bitmap
				left+=(mRoomWidthInTiles * mTileWidth) / 2;
				return left;
			}

			public int getBottomCenterOfRoomSection(int topRoomSection) {
				int top = topRoomSection * (mRoomHeightInTiles * mTileHeight);
				// Add a half of a room in height to center bitmap;
				top+=((mRoomHeightInTiles * mTileHeight) / 2) + (mTileHeight - 1);
				return top;
			}

	}
	
	public GameMap mMap;

	public class Exit {

		int [] mNeighborRoomIDs;

		public Exit()
		{
			// Create a potential exit for each direction
			mNeighborRoomIDs = new int[4];
			for (int i = 0; i < 4; ++i)
			{
				mNeighborRoomIDs[i] = -1;
			}
		}

		public void setNeighbor(int direction, int neighborID)
		{
			mNeighborRoomIDs[direction] = neighborID;
		}
	}
	
	private Exit [][] mExits;
	
	private class CheckPointSet 
	{
		private static final int MAX_CHECKPOINTS_PER_OBJECT = 100;
		
		private class CheckPoint
		{
			int mLeft = 0;
			int mBottom = 0;
		}
		
		private int mNumCheckPoints = 0;
		private CheckPoint [] mCheckPointStack;
		
		public CheckPointSet()
		{
			mCheckPointStack = new CheckPoint[MAX_CHECKPOINTS_PER_OBJECT];
			
			for (int i = 0; i < MAX_CHECKPOINTS_PER_OBJECT; ++i)
			{
				mCheckPointStack[i] = new CheckPoint();
			}
		}
	}
	
	private CheckPointSet [] mCheckPointSets;
	
	public int mMapWidthTiles;
	public int mMapHeightTiles;

	public int [][] mRoomSections;
	public int [][] mTiles;
	public boolean [][] mObstacles;
	
	// Shortest room path distances and paths
	public int mRoomDistance[][];
	public int mRoomPath[][];

	private boolean mTargetNew = false;
	private long mTimeSinceLastAlertIncrease;

	private class PathNode {
		int mDirection = -1;
		int mDistanceFromStart = -1;
		int mDistanceToEnd = -1;
	}
	
	private PathNode [][] mPathNodes;

	public MurderShipGameData(Context context) {
		// Load and store all game bitmaps
		mBitmaps = new MurderShipBitmaps(context);
		mRandomNumGenerator = new Random(); // Seed random number generator
		mTimeSinceLastAlertIncrease = System.currentTimeMillis();
	}

	// Set the game map object and initialize all auxiliary data structures
	public void setGameMap(GameMap gameMap) {
		mMap = gameMap;
		initializeAuxilaryData();
	}

	// Create a random game map object and initialize all auxiliary data structures
	public void setRandomMap() {
		mMap = new GameMap(mBitmaps.mTileWidth, mBitmaps.mTileHeight);
		mMap.generateRandomMap();
		initializeAuxilaryData();
	}

	// Save the map
	public void saveMap(FileOutputStream fos) {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(fos);
			oos.writeObject(mMap);
			oos.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	// Generate supporting data structures for all objects
	public void initializeAuxilaryData() {
		
		mMapWidthTiles = mMap.mWidthInRoomSections * mMap.mRoomWidthInTiles;
		mMapHeightTiles = mMap.mHeightInRoomSections * mMap.mRoomHeightInTiles;

		// Initialize matrix of room sections and occupied room sections
		mRoomSections = new int[mMap.mWidthInRoomSections][mMap.mHeightInRoomSections];
		
		for (int x = 0; x < mMap.mWidthInRoomSections; ++x)
			for (int y = 0; y < mMap.mHeightInRoomSections; ++y)
			{
				mRoomSections[x][y] = -1;
			}

		// Initialize matrices of tiles
		mTiles = new int[mMapWidthTiles][mMapHeightTiles];
		mObstacles = new boolean[mMapWidthTiles][mMapHeightTiles];

		// Set space background for tiles
		for (int i = 0; i < mMapWidthTiles; ++i)
		{
			for (int j = 0; j < mMapHeightTiles; ++j) {

				int randNum = mRandomNumGenerator.nextInt(30);
				if (randNum < 21)
					mTiles[i][j] = 10;
				else if (randNum < 25)
					mTiles[i][j] = 0;
				else if (randNum < 29)
					mTiles[i][j] = 1;
				else
					mTiles[i][j] = 11;

				mObstacles[i][j] = false;
			}
		}

		// Add map rooms to all tile based matrices
		for (int i = 0; i < mMap.mNumRooms; ++i)
		{
			GameMap.RoomDesc roomDesc = mMap.mRoomDescs[i];
			AddRoomToRoomMap(i, roomDesc);
			AddRoomToTileMap(roomDesc);
		}

		// Set up room distance and path matrices
		mRoomDistance = new int[mMap.mNumRooms][mMap.mNumRooms];
		mRoomPath = new int[mMap.mNumRooms][mMap.mNumRooms];

		for (int m = 0; m < mMap.mNumRooms; ++m)
			for (int n = 0; n < mMap.mNumRooms; ++n) {
				if (m != n) {
					mRoomDistance[m][n] = mMap.mNumRooms;
					mRoomPath[m][n] = -1;
				}
				else {
					mRoomDistance[m][n] = 0;
					mRoomPath[m][n] = m;
				}
			}

		// Carve out exits for all room squares adjacent to other rooms
		addExits();

		// Calculate shortest paths to each room based on the exits
		generateAllRoomsShortestPaths();
		
		// Set game objects as obstacles
		addObstacles();

		// Construct path node matrix that will be used to compute shortest paths within rooms
		mPathNodes = new PathNode[mMapWidthTiles][mMapHeightTiles];
		for (int i = 0; i < mMapWidthTiles; ++i)
			for (int j = 0; j < mMapWidthTiles; ++j)
				mPathNodes[i][j] = new PathNode();
		
		// Construct checkpoint arrays for all animate objects
		mCheckPointSets = new CheckPointSet[mMap.mNumGameObjects];
		
		for (int i = 0; i < mMap.mNumGameObjects; ++i) {
			if (mMap.mGameObjects[i] instanceof GameMap.AnimateObject)
				mCheckPointSets[i] = new CheckPointSet();
		}
	}

	private void AddRoomToRoomMap(int roomID, GameMap.RoomDesc roomDesc) {

		for (int x = roomDesc.mLeftRoomSection; x < roomDesc.mLeftRoomSection + roomDesc.mWidthRoomSections; ++x) {
			for (int y = roomDesc.mTopRoomSection; y < roomDesc.mTopRoomSection + roomDesc.mHeightRoomSections; ++y) {
				mRoomSections[x][y] = roomID;
			}
		}
	}

	private void AddRoomToTileMap(GameMap.RoomDesc roomDesc) {
		for (int x = roomDesc.mLeftRoomSection; x < roomDesc.mLeftRoomSection + roomDesc.mWidthRoomSections; ++x) {
			for (int y = roomDesc.mTopRoomSection; y < roomDesc.mTopRoomSection + roomDesc.mHeightRoomSections; ++y) {
				for (int i = 0; i < mMap.mRoomWidthInTiles; ++i) {
					for (int j = 0; j < mMap.mRoomHeightInTiles; ++j) {
						if (x == roomDesc.mLeftRoomSection && i == 0)
							mTiles[x*mMap.mRoomWidthInTiles+i][y*mMap.mRoomHeightInTiles+j] = roomDesc.mWallBitmap;
						else if (x == (roomDesc.mLeftRoomSection + roomDesc.mWidthRoomSections) - 1 && i == mMap.mRoomWidthInTiles - 1)
							mTiles[x*mMap.mRoomWidthInTiles+i][y*mMap.mRoomHeightInTiles+j] = roomDesc.mWallBitmap;
						else if (y == roomDesc.mTopRoomSection && j == 0)
							mTiles[x*mMap.mRoomWidthInTiles+i][y*mMap.mRoomHeightInTiles+j] = roomDesc.mWallBitmap;
						else if (y == (roomDesc.mTopRoomSection + roomDesc.mHeightRoomSections) - 1 && j == mMap.mRoomHeightInTiles - 1)
							mTiles[x*mMap.mRoomWidthInTiles+i][y*mMap.mRoomHeightInTiles+j] = roomDesc.mWallBitmap;
						else
							mTiles[x*mMap.mRoomWidthInTiles+i][y*mMap.mRoomHeightInTiles+j] = roomDesc.mFloorBitmap;

						
						// Record obstacle locations
						if (mTiles[x*mMap.mRoomWidthInTiles+i][y*mMap.mRoomHeightInTiles+j] == roomDesc.mWallBitmap)
							mObstacles[x*mMap.mRoomWidthInTiles+i][y*mMap.mRoomHeightInTiles+j] = true;
						else
							mObstacles[x*mMap.mRoomWidthInTiles+i][y*mMap.mRoomHeightInTiles+j] = false;
					}
				}
			}
		}
	}
	
	// Register items and objects as obstacles
	private void addObstacles()
	{
		
		for (int i = 0; i < mMap.mNumGameObjects; ++i) {
			GameMap.LocationObject gameObject = mMap.mGameObjects[i];
			
			if (gameObject instanceof GameMap.InanimateObject) 
			{
				mObstacles[getLeftTile(gameObject.mLeft)][getBottomTile(gameObject.mBottom)] = true;
			}
		}
	}

	private void addExits() {

		int leftTile = 0;
		int topTile = 0;
		GameMap.RoomDesc roomDesc;

		mExits = new Exit[mMap.mWidthInRoomSections][mMap.mHeightInRoomSections];

		for (int x = 0; x < mMap.mWidthInRoomSections; ++x) {
			for (int y = 0; y < mMap.mHeightInRoomSections; ++y) {

				mExits[x][y] = new Exit();

				leftTile = x*mMap.mRoomWidthInTiles;
				topTile = y*mMap.mRoomHeightInTiles;

				if (mRoomSections[x][y] >= 0) {
					roomDesc = mMap.mRoomDescs[mRoomSections[x][y]];

					if (x - 1 >= 0 && mRoomSections[x-1][y] >= 0 && mRoomSections[x-1][y] != mRoomSections[x][y]) {
						mTiles[leftTile][topTile+(mMap.mRoomHeightInTiles / 2)] = roomDesc.mFloorBitmap;
						mObstacles[leftTile][topTile+(mMap.mRoomHeightInTiles / 2)] = false;
						mRoomDistance[mRoomSections[x][y]][mRoomSections[x-1][y]] = 1;
						mRoomPath[mRoomSections[x][y]][mRoomSections[x-1][y]] = mRoomSections[x][y];
						mExits[x][y].setNeighbor(LEFT, mRoomSections[x-1][y]);
					}
					if (x + 1 < mMap.mWidthInRoomSections && mRoomSections[x+1][y] >= 0 && mRoomSections[x+1][y] != mRoomSections[x][y]) {
						mTiles[leftTile+(mMap.mRoomWidthInTiles - 1)][topTile+(mMap.mRoomHeightInTiles / 2)] = roomDesc.mFloorBitmap;
						mObstacles[leftTile+(mMap.mRoomWidthInTiles - 1)][topTile+(mMap.mRoomHeightInTiles / 2)] = false;
						mRoomDistance[mRoomSections[x][y]][mRoomSections[x+1][y]] = 1;
						mRoomPath[mRoomSections[x][y]][mRoomSections[x+1][y]] = mRoomSections[x][y];
						mExits[x][y].setNeighbor(RIGHT, mRoomSections[x+1][y]);
					}
					if (y - 1 >= 0 && mRoomSections[x][y-1] >= 0 && mRoomSections[x][y-1] != mRoomSections[x][y]) {
						mTiles[leftTile+(mMap.mRoomWidthInTiles / 2)][topTile] = roomDesc.mFloorBitmap;
						mObstacles[leftTile+(mMap.mRoomWidthInTiles / 2)][topTile] = false;
						mRoomDistance[mRoomSections[x][y]][mRoomSections[x][y-1]] = 1;
						mRoomPath[mRoomSections[x][y]][mRoomSections[x][y-1]] = mRoomSections[x][y];
						mExits[x][y].setNeighbor(UP, mRoomSections[x][y-1]);
					}
					if (y + 1 < mMap.mHeightInRoomSections && mRoomSections[x][y+1] >= 0 && mRoomSections[x][y+1] != mRoomSections[x][y]) {
						mTiles[leftTile+(mMap.mRoomWidthInTiles / 2)][topTile+(mMap.mRoomHeightInTiles - 1)] = roomDesc.mFloorBitmap;
						mObstacles[leftTile+(mMap.mRoomWidthInTiles / 2)][topTile+(mMap.mRoomHeightInTiles - 1)] = false;
						mRoomDistance[mRoomSections[x][y]][mRoomSections[x][y+1]] = 1;
						mRoomPath[mRoomSections[x][y]][mRoomSections[x][y+1]] = mRoomSections[x][y];
						mExits[x][y].setNeighbor(DOWN, mRoomSections[x][y+1]);
					}
				}
			}
		}
	}

	// Calculate all rooms shortest paths based on the Floyd-Warshall algorithm
	private void generateAllRoomsShortestPaths() {

		for (int k = 0; k < mMap.mNumRooms; ++k)
			for (int i = 0; i < mMap.mNumRooms; ++i)
				for (int j = 0; j < mMap.mNumRooms; ++j) {
					if (mRoomDistance[i][k] + mRoomDistance[k][j] < mRoomDistance[i][j]) {
						mRoomDistance[i][j] = mRoomDistance[i][k] + mRoomDistance[k][j];
						mRoomPath[i][j] = k;
					}	
				}
	}

	public void updateTarget(int targetLeft, int targetBottom) {
		mTargetNew = true;
		
		// Snap target to nearest tile boundary
		mMap.mGameObjects[mMap.mTargetIndex].mLeft = (targetLeft/mBitmaps.mTileWidth) * mBitmaps.mTileWidth;
		mMap.mGameObjects[mMap.mTargetIndex].mBottom = (targetBottom/mBitmaps.mTileHeight) * mBitmaps.mTileHeight;
		mMap.mGameObjects[mMap.mTargetIndex].mBottom += (mBitmaps.mTileHeight-1);
	}
	
	public void pickupMagnifyingGlass(GameMap.InanimateObject inanimateObject)
	{
		if (inanimateObject.mType == GameMap.InanimateObject.MAGNIFYING_GLASS_TYPE)
		{
			pickupObject(inanimateObject);
			mMap.mHaveMagnifyingGlass = true;
		}
	}
	
	public boolean haveMagnifyingGlass()
	{
		return mMap.mHaveMagnifyingGlass;
	}
	
	public void pickupWeapon(GameMap.InanimateObject inanimateObject)
	{
		if (inanimateObject.mUsedInMurder && foundWeapon())
		{
			pickupObject(inanimateObject);
			mMap.mHaveWeapon = true;
		}
	}
	
	public boolean haveWeapon()
	{
		return mMap.mHaveWeapon;
	}
	
	public void findWeapon()
	{
		mMap.mFoundWeapon = true;
	}
	
	public boolean foundWeapon()
	{
		return mMap.mFoundWeapon;
	}
	
	public void findVictim()
	{
		mMap.mFoundVictim = true;
	}
	
	public boolean foundVictim()
	{
		return mMap.mFoundVictim;
	}
	
	private void pickupObject(GameMap.InanimateObject inanimateObject)
	{
		inanimateObject.mVisible = false;
		mObstacles[getLeftTile(inanimateObject.mLeft)][getBottomTile(inanimateObject.mBottom)] = false;
	}
	
	

	public void incrementAlertLevel(int increment)
	{
		mMap.mAlertLevel+=increment;
	}
	
	public int getAlertLevel()
	{
		return mMap.mAlertLevel;
	}
	
	public void beginStalkingHero()
	{
		GameMap.AnimateObject murdererObject = (GameMap.AnimateObject)mMap.mGameObjects[mMap.mMurdererIndex];
		murdererObject.mState = GameMap.AnimateObject.STALKER_UNASSIGNED_STATE;
		murdererObject.mMinTimeForUpdate = 0;
	}
	
	public void updateGameState() {
		
		// Update states of every game object
		for (int i = 0; i < mMap.mNumGameObjects; ++i) {

			// The game object stores all of the objects' state
			// The corresponding draw object, owned by the draw runnable, contains only the state needed for rendering the object
			GameMap.LocationObject gameObject = mMap.mGameObjects[i];
			
			if (gameObject instanceof GameMap.AnimateObject)
			{
				GameMap.AnimateObject animateObject = (GameMap.AnimateObject)gameObject;
				
				// Only update this object's state if enough time has elapsed
				if (System.currentTimeMillis() - animateObject.mTimeSinceLastUpdate >= animateObject.mMinTimeForUpdate)
				{
					switch(animateObject.mState)
					{
					case GameMap.AnimateObject.UNASSIGNED_STATE:
						doUnassignedState(animateObject, mCheckPointSets[i]);
						break;
					case GameMap.AnimateObject.SUSPECT_TRAVELING_STATE:
						doSuspectTravelingState(animateObject, mCheckPointSets[i]);
						break;
					case GameMap.AnimateObject.HERO_TRAVELING_STATE:
						doHeroTravelingState(animateObject, mCheckPointSets[i]);
						break;
					case GameMap.AnimateObject.GO_TO_TARGET:
						doGoToTarget(animateObject, mCheckPointSets[i]);
						break;
					case GameMap.AnimateObject.DEAD_STATE:
						doDeadState(animateObject);
						break;
					case GameMap.AnimateObject.STALKER_UNASSIGNED_STATE:
						doStalkerUnassignedState(animateObject, mCheckPointSets[i]);
						break;
					case GameMap.AnimateObject.STALKER_TRAVELING_STATE:
						doStalkerTravelingState(animateObject, mCheckPointSets[i]);
						break;
					}
						
					animateObject.mTimeSinceLastUpdate = System.currentTimeMillis();
				}
			}
			if (gameObject instanceof GameMap.ActionObject)
			{
				GameMap.ActionObject actionObject = (GameMap.ActionObject)gameObject;
				updateActionObject(actionObject);
			}
			
		}
		
		// Alert level rises periodically
		if (System.currentTimeMillis() - mTimeSinceLastAlertIncrease > TIME_FOR_AUTOMATIC_ALERT_INCREASE)
		{
			mTimeSinceLastAlertIncrease = System.currentTimeMillis();
			incrementAlertLevel(1);
		}
		
		checkStoryPoint();
	}
	
	private void checkStoryPoint()
	{
		// Only update to a "pre" story point if the game runnable's dialog has updated to a "post" story point
		if (mMap.mStoryPoint == GameMap.POST_HERO_MURDERED_POINT)
		{
			mMap.mStoryPoint = GameMap.PRE_LOSE_POINT;
		}
		else if (mMap.mStoryPoint == GameMap.POST_RAMPAGE_POINT)
		{
			if (mMap.objectsOverlap(mMap.mGameObjects[mMap.mMurdererIndex], mMap.mGameObjects[mMap.mHeroIndex]))
			{
				mMap.murderHero();
				mMap.mStoryPoint = GameMap.PRE_HERO_MURDERED_POINT;
			}
		}
		else if (mMap.mStoryPoint == GameMap.POST_ARREST_POINT)
		{
			mMap.mStoryPoint = GameMap.PRE_WIN_POINT;
		}
		else if (mMap.mStoryPoint == GameMap.POST_ACCUSE_POINT)
		{
			mMap.mStoryPoint = GameMap.PRE_ARREST_POINT;
		}
		else if (mMap.mStoryPoint == GameMap.POST_INTRO_POINT || mMap.mStoryPoint == GameMap.POST_YELLOW_ALERT_POINT || mMap.mStoryPoint == GameMap.POST_RED_ALERT_POINT)
		{
			if (mMap.mAlertLevel >= RED_ALERT_LEVEL && mMap.mStoryPoint < GameMap.POST_RED_ALERT_POINT)
			{
				mMap.mStoryPoint = GameMap.PRE_RED_ALERT_POINT;
			}
			else if (mMap.mAlertLevel >= YELLOW_ALERT_LEVEL && mMap.mStoryPoint < GameMap.POST_YELLOW_ALERT_POINT)
			{
				mMap.mStoryPoint = GameMap.PRE_YELLOW_ALERT_POINT;
			}
		}
	}

	private void doUnassignedState(GameMap.AnimateObject animateObject, CheckPointSet checkPointSet)
	{

		if (animateObject.mType == GameMap.AnimateObject.SUSPECT_TYPE)
		{
			setRandomDestination(animateObject);
			
			calculatePath(animateObject, checkPointSet);

			animateObject.mState = GameMap.AnimateObject.SUSPECT_TRAVELING_STATE;
			animateObject.mMinTimeForUpdate = 45;
		}
		else if (animateObject.mType == GameMap.AnimateObject.TARGET_TYPE)
		{
			if (mTargetNew)
			{
				GameMap.AnimateObject heroObject = (GameMap.AnimateObject)mMap.mGameObjects[mMap.mHeroIndex];
				heroObject.mState = GameMap.AnimateObject.GO_TO_TARGET;
				mTargetNew = false;
			}

			animateObject.mMinTimeForUpdate = 0;
		}
	}
	
	private void doStalkerUnassignedState(GameMap.AnimateObject animateObject, CheckPointSet checkPointSet)
	{
		GameMap.AnimateObject heroObject = (GameMap.AnimateObject)mMap.mGameObjects[mMap.mHeroIndex];
		
		animateObject.mDestLeft = heroObject.mLeft;
		animateObject.mDestBottom = heroObject.mBottom;
		
		calculatePath(animateObject, checkPointSet);
		
		animateObject.mState = GameMap.AnimateObject.STALKER_TRAVELING_STATE;
		animateObject.mMinTimeForUpdate = heroObject.mMinTimeForUpdate;
		animateObject.mSpeed = heroObject.mSpeed + 1;
	}
	
	private void setRandomDestination(GameMap.AnimateObject animateObject)
	{
		boolean [][] occupiedSections;
		
		// Record occupied tiles
		occupiedSections = new boolean[mMap.mWidthInRoomSections][mMap.mHeightInRoomSections];

		// Initialize occupied tile to false
		for (int x = 0; x < mMap.mWidthInRoomSections; ++x)
			for (int y = 0; y < mMap.mHeightInRoomSections; ++y)
				occupiedSections[x][y] = false;
		
		// Occupy tiles where inanimate objects are located, or other animate objects are destined
		for (int i = 0; i < mMap.mNumGameObjects; ++i)
		{
			GameMap.LocationObject gameObject = mMap.mGameObjects[i];
			
			// Invisible objects do not occupy room sections
			if (!gameObject.mVisible)
				continue;
			
			// Occupy tiles where inanimate objects are located
			if (gameObject instanceof GameMap.InanimateObject)
			{
				GameMap.InanimateObject inanimateObject = (GameMap.InanimateObject)gameObject;
				occupiedSections[getLeftRoomSection(inanimateObject.mLeft)][getBottomRoomSection(inanimateObject.mBottom)] = true;
			}
			else if (gameObject instanceof GameMap.AnimateObject)
			{
				// Occupy the destination of animated objects, to prevent two characters from choosing to go to the same place
				GameMap.AnimateObject suspectObject = (GameMap.AnimateObject)gameObject;
				if (suspectObject.mType == GameMap.AnimateObject.SUSPECT_TYPE &&
						suspectObject != animateObject)
					occupiedSections[getLeftRoomSection(suspectObject.mDestLeft)][getBottomRoomSection(suspectObject.mDestBottom)] = true;
			}
		}
		
		GameMap.SectionCoordinates sectionCoordinates = new GameMap.SectionCoordinates(-1, -1);
		
		if (mMap.getRandomUnoccupiedSection(occupiedSections, sectionCoordinates))
		{
			animateObject.mDestLeft = mMap.getLeftCenterOfRoomSection(sectionCoordinates.mX);
			animateObject.mDestBottom = mMap.getBottomCenterOfRoomSection(sectionCoordinates.mY);
		}
		else
		{
			animateObject.mDestLeft = animateObject.mLeft;
			animateObject.mDestBottom = animateObject.mBottom;
		}
	}
	
	private void doHeroTravelingState(GameMap.AnimateObject animateObject, CheckPointSet checkPointSet)
	{
		int startRoomID = getRoomID(animateObject.mLeft, animateObject.mBottom);
		
		if (doTravelingState(animateObject, checkPointSet))
			animateObject.mState = GameMap.AnimateObject.UNASSIGNED_STATE;
		
		int endRoomID = getRoomID(animateObject.mLeft, animateObject.mBottom);
		
		if (startRoomID != endRoomID)
			incrementAlertLevel(ALERT_OF_CHANGING_ROOMS);
	}
	
	private void doSuspectTravelingState(GameMap.AnimateObject animateObject, CheckPointSet checkPointSet)
	{
		if (doTravelingState(animateObject, checkPointSet))
		{
			// Arrived at destination
			animateObject.mState = GameMap.AnimateObject.UNASSIGNED_STATE;
			animateObject.mMinTimeForUpdate = 90000;
		}
	}
	
	private void doStalkerTravelingState(GameMap.AnimateObject animateObject, CheckPointSet checkPointSet)
	{
		if (doTravelingState(animateObject, checkPointSet))
		{
			// Arrived at destination
			animateObject.mState = GameMap.AnimateObject.STALKER_UNASSIGNED_STATE;
			animateObject.mMinTimeForUpdate = 0;
		}
	}

	// Returns true if arrived at destination
	private boolean doTravelingState(GameMap.AnimateObject animateObject, CheckPointSet checkPointSet) {

		// If object has not reached its pixel destination, move it
		if (checkPointSet.mNumCheckPoints > 0)
		{
			CheckPointSet.CheckPoint checkPoint = checkPointSet.mCheckPointStack[checkPointSet.mNumCheckPoints-1];

			if (animateObject.mLeft != checkPoint.mLeft || animateObject.mBottom != checkPoint.mBottom)
			{
				moveTowardCheckPoint(animateObject, checkPointSet);
			}
			
			if (animateObject.mLeft == checkPoint.mLeft && animateObject.mBottom == checkPoint.mBottom)
			{
				--checkPointSet.mNumCheckPoints;
			}

			// Update the animation frame
			animateObject.mAnimation = 1 - animateObject.mAnimation;
		}
		
		// If there are now no longer any checkpoints remaining, check if character has arrived at destination
		// Otherwise, compute new set of checkpoints
		if (checkPointSet.mNumCheckPoints <= 0)
		{
			if (animateObject.mLeft != animateObject.mDestLeft || animateObject.mBottom != animateObject.mDestBottom)
			{
				// The character has not arrived at its destination
				// Another set of checkpoints will be calculated for it
				calculatePath(animateObject, checkPointSet);
			}
			else
			{
				return true;
			}
			
		}
		
		return false;
	}

	private void doGoToTarget(GameMap.AnimateObject animateObject, CheckPointSet checkPointSet) {

		// Set hero's destination to target location
		animateObject.mDestLeft = mMap.mGameObjects[mMap.mTargetIndex].mLeft;
		animateObject.mDestBottom = mMap.mGameObjects[mMap.mTargetIndex].mBottom;
		calculatePath(animateObject, checkPointSet);
		animateObject.mState = GameMap.AnimateObject.HERO_TRAVELING_STATE;
		animateObject.mMinTimeForUpdate = 45;
	}
	
	private void doDeadState(GameMap.AnimateObject animateObject) {
		animateObject.mAnimation = 2;
		animateObject.mMinTimeForUpdate = 10000;
	}

	private void updateActionObject(GameMap.ActionObject actionObject) {
		
		actionObject.mVisible = false;
		
		GameMap.LocationObject heroObject = mMap.mGameObjects[mMap.mHeroIndex];
		int heroLeftTile = getLeftTile(heroObject.mLeft);
		int heroBottomTile = getBottomTile(heroObject.mBottom);
		
		// Check proximity of all objects
		for (int i = 0; i < mMap.mNumGameObjects; ++i)
		{
			// Pointless to compare proximity of hero to itself or to an invisible object, or to the murder victim
			if (i == mMap.mHeroIndex || i == mMap.mTargetIndex || i == mMap.mActionIndex || !mMap.mGameObjects[i].mVisible || i == mMap.mVictimIndex)
				continue;
			
			int objectLeftTile = getLeftTile(mMap.mGameObjects[i].mLeft);
			int objectBottomTile = getBottomTile(mMap.mGameObjects[i].mBottom);
			
			// Check visible objects that are less than 2 tiles from hero
			if (manhattanDistance(objectLeftTile, objectBottomTile, heroLeftTile, heroBottomTile) < 2)
			{
				actionObject.mTarget = i;
				
				int heroCenter = heroObject.mLeft + ((mMap.mTileWidth * MurderShipBitmaps.CHARACTER_WIDTH_IN_TILES) / 2);
				actionObject.mLeft = heroCenter - ((mMap.mTileWidth * MurderShipBitmaps.ACTION_WIDTH_IN_TILES)/2);
				
				actionObject.mBottom = heroObject.mBottom - (mMap.mTileHeight * MurderShipBitmaps.CHARACTER_HEIGHT_IN_TILES);

				// Force object on map
				if (actionObject.mLeft < 0)
					actionObject.mLeft = 0;
				else if (actionObject.mLeft + (mMap.mTileWidth * MurderShipBitmaps.ACTION_WIDTH_IN_TILES) >= mMapWidthTiles * mMap.mTileWidth)
					actionObject.mLeft = (mMapWidthTiles * mMap.mTileWidth) - (mMap.mTileWidth * MurderShipBitmaps.ACTION_WIDTH_IN_TILES);

				if (actionObject.mBottom < (mMap.mTileHeight * MurderShipBitmaps.ACTION_HEIGHT_IN_TILES))
					actionObject.mBottom = (mMap.mTileHeight * MurderShipBitmaps.ACTION_HEIGHT_IN_TILES) - 1;
				else if (actionObject.mBottom  >= (mMapHeightTiles * mMap.mTileHeight))
					actionObject.mBottom = (mMapHeightTiles * mMap.mTileHeight) - 1;

				if (mMap.mGameObjects[i] instanceof GameMap.AnimateObject)
					actionObject.mBitmap = GameMap.ActionObject.TALK_BITMAP;
				else
				{
					if (haveMagnifyingGlass())
						actionObject.mBitmap = GameMap.ActionObject.MAGNIFYING_BITMAP;
					else
						actionObject.mBitmap = GameMap.ActionObject.LOOK_BITMAP;
				}
				
				actionObject.mVisible = true;
			}
		}
	}
	
	private void moveTowardCheckPoint(GameMap.AnimateObject animateObject, CheckPointSet checkPointSet) {
		int deltaX = 0;
		int deltaY = 0;

		if (checkPointSet.mNumCheckPoints < 1)
			return;

		CheckPointSet.CheckPoint checkPoint = checkPointSet.mCheckPointStack[checkPointSet.mNumCheckPoints-1];

		if (animateObject.mLeft < checkPoint.mLeft)
			deltaX = Math.min(canMoveRight(animateObject), checkPoint.mLeft - animateObject.mLeft);
		else if (animateObject.mLeft > checkPoint.mLeft)
			deltaX = Math.max(canMoveLeft(animateObject), checkPoint.mLeft - animateObject.mLeft);

		animateObject.mLeft += deltaX;

		if (animateObject.mBottom < checkPoint.mBottom)
			deltaY = Math.min(canMoveDown(animateObject), checkPoint.mBottom - animateObject.mBottom);
		else if (animateObject.mBottom > checkPoint.mBottom)
			deltaY = Math.max(canMoveUp(animateObject), checkPoint.mBottom - animateObject.mBottom);

		animateObject.mBottom += deltaY;

		// If checkpoint is unreachable, destination is unreachable
		// Set destination to current location
		if (deltaX == 0 && deltaY == 0) {
			checkPointSet.mNumCheckPoints = 0;
			animateObject.mDestLeft = animateObject.mLeft;
			animateObject.mDestBottom = animateObject.mBottom;
		}
	}

	private int canMoveLeft(GameMap.AnimateObject animateObject) {
		int actualDistance = 0;
		int maxDistance = 0-animateObject.mSpeed;

		int leftTile = getLeftTile(animateObject.mLeft);
		int bottomTile = getBottomTile(animateObject.mBottom);
		int offsetTile = bottomTile;
		if ((animateObject.mBottom+1) % mBitmaps.mTileHeight != 0)
			--offsetTile;

		actualDistance = (leftTile * mBitmaps.mTileWidth) - animateObject.mLeft;

		// See how far object can move left toward its maximum distance until hitting an obstacle
		while (actualDistance > maxDistance) {
			--leftTile;
			if (leftTile < 0 || mObstacles[leftTile][bottomTile] || mObstacles[leftTile][offsetTile])
				return actualDistance;

			actualDistance-=mBitmaps.mTileWidth;
		}

		return maxDistance;
	}

	private int canMoveRight(GameMap.AnimateObject animateObject) {
		int actualDistance = 0;
		int maxDistance = animateObject.mSpeed;

		int leftTile = getLeftTile(animateObject.mLeft);
		int bottomTile = getBottomTile(animateObject.mBottom);
		int offsetTile = bottomTile;
		if ((animateObject.mBottom+1) % mBitmaps.mTileHeight != 0)
			--offsetTile;

		actualDistance = (leftTile * mBitmaps.mTileWidth) - animateObject.mLeft;

		// See how far object can move right toward its maximum distance until hitting an obstacle
		while (actualDistance < maxDistance) {
			++leftTile;
			if (leftTile >= mMapWidthTiles || mObstacles[leftTile][bottomTile] || mObstacles[leftTile][offsetTile])
				return actualDistance;

			actualDistance+=mBitmaps.mTileWidth;
		}

		return maxDistance;
	}

	private int canMoveUp(GameMap.AnimateObject animateObject) {
		int actualDistance = 0;
		int maxDistance = 0-animateObject.mSpeed;

		int leftTile = getLeftTile(animateObject.mLeft);
		int topTile = getBottomTile(animateObject.mBottom);
		int offsetTile = leftTile;
		if (animateObject.mLeft % mBitmaps.mTileWidth != 0)
			++offsetTile;

		actualDistance = (topTile * mBitmaps.mTileHeight) + (mBitmaps.mTileHeight - 1) - animateObject.mBottom;

		// See how far object can move up toward its maximum distance until hitting an obstacle
		while (actualDistance > maxDistance) {
			--topTile;
			if (topTile < 0 || mObstacles[leftTile][topTile] || mObstacles[offsetTile][topTile])
				return actualDistance;

			actualDistance-=mBitmaps.mTileHeight;
		}

		return maxDistance;
	}

	private int canMoveDown(GameMap.AnimateObject animateObject) {
		int actualDistance = 0;
		int maxDistance = animateObject.mSpeed;

		int leftTile = getLeftTile(animateObject.mLeft);
		int bottomTile = getBottomTile(animateObject.mBottom);
		int offsetTile = leftTile;
		if (animateObject.mLeft % mBitmaps.mTileWidth != 0)
			++offsetTile;

		actualDistance = (bottomTile * mBitmaps.mTileHeight) + (mBitmaps.mTileHeight - 1) - animateObject.mBottom;

		// See how far object can move down toward its maximum distance until hitting an obstacle
		while (actualDistance < maxDistance) {
			++bottomTile;
			if (bottomTile >= mMapHeightTiles || mObstacles[leftTile][bottomTile] || mObstacles[offsetTile][bottomTile])
				return actualDistance;

			actualDistance+=mBitmaps.mTileHeight;
		}

		return maxDistance;
	}

	// Pushes intermediate checkpoints onto stack to reach current checkpoint
	private boolean calculatePath(GameMap.AnimateObject animateObject, CheckPointSet checkPointSet) {

		// Calculate intermediate checkpoints to this checkpoint
		
		int roomIDCurrent = getRoomID(animateObject.mLeft, animateObject.mBottom);
		int roomIDDest = getRoomID(animateObject.mDestLeft, animateObject.mDestBottom);

		// Treat a destination outside any room as if it were a local destination
		if (roomIDCurrent == roomIDDest || roomIDDest == -1) {
			checkPointSet.mNumCheckPoints = 1;
			CheckPointSet.CheckPoint newCheckPoint = checkPointSet.mCheckPointStack[0];
			newCheckPoint.mLeft = animateObject.mDestLeft;
			newCheckPoint.mBottom = animateObject.mDestBottom;
			calculateIntermediateCheckPoints(animateObject, checkPointSet);
			return true;
		}

		// Find the next room to target on the path to the destination room
		int roomIDNext = roomIDDest;

		while (mRoomPath[roomIDCurrent][roomIDNext] != roomIDCurrent) {
			roomIDNext = mRoomPath[roomIDCurrent][roomIDNext];

			// If no path was found, the room is unreachable, so return false
			if (roomIDNext == -1)
				return false;
		}

		// Find the closest exit in this room which leads to the next room
		int shortestDistanceToExit = GameMap.AnimateObject.MAX_DISTANCE_TO_EXIT;
		int bestXsection = -1;
		int bestYsection = -1;
		int bestDirection = -1;

		int leftRoomSection = mMap.mRoomDescs[roomIDCurrent].mLeftRoomSection;
		int rightRoomSection = leftRoomSection + mMap.mRoomDescs[roomIDCurrent].mWidthRoomSections;
		int topRoomSection = mMap.mRoomDescs[roomIDCurrent].mTopRoomSection;
		int bottomRoomSection = topRoomSection + mMap.mRoomDescs[roomIDCurrent].mHeightRoomSections;

		// Check possible exits in each room section of the current room
		for (int x = leftRoomSection; x < rightRoomSection; ++x)
			for (int y = topRoomSection; y < bottomRoomSection; ++y)
			{
				Exit exit = mExits[x][y];

				// Check exits in each direction
				for (int z = 0; z < 4; ++z)
				{
					int leftCenterTile = (x*mMap.mRoomWidthInTiles) + (mMap.mRoomWidthInTiles/2);
					int topCenterTile = (y*mMap.mRoomHeightInTiles) + (mMap.mRoomHeightInTiles/2);

					int dist = manhattanDistance(getLeftTile(animateObject.mLeft), getBottomTile(animateObject.mBottom), leftCenterTile, topCenterTile);

					if (exit.mNeighborRoomIDs[z] == roomIDNext &&
							dist < shortestDistanceToExit)
					{
						bestXsection = x;
						bestYsection = y;
						bestDirection = z;
						shortestDistanceToExit = dist;
					}
				}
			}

		if (bestDirection != -1)
		{
			int leftTile = bestXsection * mMap.mRoomWidthInTiles;
			int topTile = bestYsection * mMap.mRoomHeightInTiles;

			checkPointSet.mNumCheckPoints = 1;
			CheckPointSet.CheckPoint newCheckPoint = checkPointSet.mCheckPointStack[0];

			switch (bestDirection) {
			case MurderShipGameData.UP:
				newCheckPoint.mLeft = (leftTile+(mMap.mRoomWidthInTiles / 2)) * mBitmaps.mTileWidth;
				newCheckPoint.mBottom = (topTile * mBitmaps.mTileHeight) - 1;
				break;
			case MurderShipGameData.DOWN:
				newCheckPoint.mLeft = (leftTile+(mMap.mRoomWidthInTiles / 2)) * mBitmaps.mTileWidth;
				newCheckPoint.mBottom = ((topTile+(mMap.mRoomHeightInTiles)) * mBitmaps.mTileHeight);
				break;
			case MurderShipGameData.LEFT:
				newCheckPoint.mLeft = (leftTile - 1) * mBitmaps.mTileWidth;
				newCheckPoint.mLeft+=(mBitmaps.mTileWidth-1);
				newCheckPoint.mBottom = ((topTile+(mMap.mRoomHeightInTiles / 2)) * mBitmaps.mTileHeight) + (mBitmaps.mTileHeight - 1);
				break;
			case MurderShipGameData.RIGHT:
				newCheckPoint.mLeft = (leftTile+(mMap.mRoomWidthInTiles)) * mBitmaps.mTileWidth;
				newCheckPoint.mBottom = ((topTile+(mMap.mRoomHeightInTiles / 2)) * mBitmaps.mTileHeight)  + (mBitmaps.mTileHeight - 1);
				break;
			}
			
			calculateIntermediateCheckPoints(animateObject, checkPointSet);

			return true;
		}
		else
			return false;
	}

	// Search for the shortest path within the room to the closest tile to the game object's current check point
	void calculateIntermediateCheckPoints(GameMap.AnimateObject animateObject, CheckPointSet checkPointSet) {

		int roomID = getRoomID(animateObject.mLeft, animateObject.mBottom);

		if (roomID == -1 || checkPointSet.mNumCheckPoints < 1)
			return;

		CheckPointSet.CheckPoint destCheckPoint = checkPointSet.mCheckPointStack[checkPointSet.mNumCheckPoints-1];

		int leftTile = mMap.mRoomDescs[roomID].mLeftRoomSection * mMap.mRoomWidthInTiles;
		int topTile = mMap.mRoomDescs[roomID].mTopRoomSection * mMap.mRoomHeightInTiles;
		int rightTile = leftTile + (mMap.mRoomDescs[roomID].mWidthRoomSections * mMap.mRoomWidthInTiles);
		int bottomTile = topTile + (mMap.mRoomDescs[roomID].mHeightRoomSections * mMap.mRoomHeightInTiles);

		int xTile, yTile;

		// Clear the path nodes in this room
		for (xTile = leftTile; xTile < rightTile; ++xTile) {
			for (yTile = topTile; yTile < bottomTile; ++yTile) {
				mPathNodes[xTile][yTile].mDistanceFromStart = -1;
			}	
		}

		// Determine the tile of the destination
		int destLeftTile = getLeftTile(destCheckPoint.mLeft);
		int destBottomTile = getBottomTile(destCheckPoint.mBottom);

		// Initialize the path node at the object's initial location
		int startLeftTile = getLeftTile(animateObject.mLeft);
		int startBottomTile = getBottomTile(animateObject.mBottom);

		PathNode curPathNode = mPathNodes[startLeftTile][startBottomTile];
		curPathNode.mDirection = MurderShipGameData.UP;
		curPathNode.mDistanceFromStart = 0;
		curPathNode.mDistanceToEnd = manhattanDistance(startLeftTile, startBottomTile, destLeftTile, destBottomTile);

		int pathNodesAdded = 1;
		int curDistanceFromStart = 0;

		int bestLeftTile = startLeftTile;
		int bestTopTile = startBottomTile;
		int bestDistance = curPathNode.mDistanceToEnd;

		// Repeatedly add new path nodes adjacent to previously added path nodes in the room until no more can be added
		// or the destination is reached
		while (pathNodesAdded > 0) {
			pathNodesAdded = 0;

			// Iterate over the path nodes in this room
			for (xTile = leftTile; xTile < rightTile; ++xTile) {
				for (yTile = topTile; yTile < bottomTile; ++yTile) {
					curPathNode = mPathNodes[xTile][yTile];

					// Skip if the tile is an obstacle or has been calculated already
					if (mObstacles[xTile][yTile] == true || curPathNode.mDistanceFromStart != -1)
						continue;

					PathNode adjacentPathNode = null;

					if (yTile - 1 >= topTile && mPathNodes[xTile][yTile - 1].mDistanceFromStart == curDistanceFromStart) {
						// Check tile above
						adjacentPathNode = mPathNodes[xTile][yTile - 1];
						curPathNode.mDirection = MurderShipGameData.UP;
					}
					else if (yTile + 1 < bottomTile && mPathNodes[xTile][yTile + 1].mDistanceFromStart == curDistanceFromStart) {
						// Check tile below
						adjacentPathNode = mPathNodes[xTile][yTile + 1];
						curPathNode.mDirection = MurderShipGameData.DOWN;
					}
					else if (xTile - 1 >= leftTile && mPathNodes[xTile - 1][yTile].mDistanceFromStart == curDistanceFromStart) {
						// Check tile to left
						adjacentPathNode = mPathNodes[xTile - 1][yTile];
						curPathNode.mDirection = MurderShipGameData.LEFT;
					}
					else if (xTile + 1 < rightTile && mPathNodes[xTile + 1][yTile].mDistanceFromStart == curDistanceFromStart) {
						// Check tile to right
						adjacentPathNode = mPathNodes[xTile + 1][yTile];
						curPathNode.mDirection = MurderShipGameData.RIGHT;
					}

					if (adjacentPathNode != null) {
						curPathNode.mDistanceFromStart = curDistanceFromStart+1;
						curPathNode.mDistanceToEnd = manhattanDistance(xTile, yTile, destLeftTile, destBottomTile);
						if (curPathNode.mDistanceToEnd < bestDistance) {
							bestDistance = curPathNode.mDistanceToEnd;
							bestLeftTile = xTile;
							bestTopTile = yTile;
						}
						++pathNodesAdded;
					}
				}	
			}
			++curDistanceFromStart;
		}

		// Add intermediate checkpoints by tracing path from best tile to start tile
		xTile = bestLeftTile;
		yTile = bestTopTile;

		while (xTile != startLeftTile || yTile != startBottomTile) {

			if (checkPointSet.mNumCheckPoints >= GameMap.AnimateObject.MAX_DISTANCE_TO_EXIT) {
				break;
			}
			
			if (xTile != bestLeftTile || yTile != bestTopTile) {
				CheckPointSet.CheckPoint checkPoint = checkPointSet.mCheckPointStack[checkPointSet.mNumCheckPoints++];
				checkPoint.mLeft = xTile * mBitmaps.mTileWidth;
				checkPoint.mBottom = (yTile * mBitmaps.mTileHeight) + (mBitmaps.mTileHeight - 1);
			}
			
			switch (mPathNodes[xTile][yTile].mDirection) {
			case MurderShipGameData.UP:
				--yTile;
				break;
			case MurderShipGameData.DOWN:
				++yTile;
				break;
			case MurderShipGameData.LEFT:
				--xTile;
				break;
			case MurderShipGameData.RIGHT:
				++xTile;
				break;
			}
		}
	}

	private int manhattanDistance(int srcLeft, int srcTop, int destLeft, int destTop) {
		return Math.abs(destLeft - srcLeft) + Math.abs(destTop - srcTop);
	}

	private int getRoomID(int left, int bottom) {
		return mRoomSections[getLeftRoomSection(left)][getBottomRoomSection(bottom)];
	}

	private int getLeftTile(int left) {
		return left/mBitmaps.mTileWidth;
	}

	private int getBottomTile(int bottom) {
		return bottom/mBitmaps.mTileHeight;
	}

	private int getLeftRoomSection(int left) {
		return left / (mMap.mRoomWidthInTiles * mBitmaps.mTileWidth);
	}

	private int getBottomRoomSection(int bottom) {
		return bottom / (mMap.mRoomHeightInTiles * mBitmaps.mTileHeight);
	}
}

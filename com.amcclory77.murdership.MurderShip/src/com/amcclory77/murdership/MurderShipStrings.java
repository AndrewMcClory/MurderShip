package com.amcclory77.murdership;

public class MurderShipStrings {

	public static final int NUM_CHARACTERS = 10;
	public static final int NUM_OBJECTS = 10;
	public static final int NUM_ITEMS = 9;
	public static final int NUM_GROUP_ALIBIS = 10;
	public static final int NUM_SOLO_ALIBIS = 5;
	
	// Character conversations
	private String [] mCharacterNames;
	private String [] mCharacterIntros;
	private String [] mCharacterPreMurderQuestions;
	private String [] mCharacterPreMurderAccusations;
	private String [] mCharacterPostMurderConfessions;
	private String [] mCharacterPostMurderDenials;
	private String [] mCharacterRemorseStatements;
	private String [] mCharacterAccuseStatements;
	
	private String mAccuseWithoutWeapon;
	private String mAccuseWithWeapon;
	
	// Alibi strings
	private String [] mGroupAlibis;
	private String [] mSoloAlibis;
	
	// Object messages
	private String [] mObjectNames;
	private String [] mObjectNoBodySearches;
	private String [] mObjectBodyFoundSearches;
	
	// Item messages
	private String [] mItemNames;
	private String [] mItemNoMurderWeaponSearches;
	private String [] mItemMurderWeaponSearches;
	private String [] mItemNoMurderWeaponPickUps;
	private String [] mItemMurderWeaponPickUps;
	
	private String mMagnifyingGlassName;
	private String mMagnifyingGlassExamine;
	private String mMagnifyingGlassPickUp;
	private String mWithMagnifyingGlassSearch;
	private String mWithoutMagnifyingGlassSearch;
	
	private String mIntroTitle;
	private String mIntroMessage;
	
	private String mYellowAlertTitle;
	private String mYellowAlertMessage;
	
	private String mRedAlertTitle;
	private String mRedAlertMessage;
	
	private String mWinTitle;
	private String mWinMessage;
	
	private String mLoseTitle;
	private String mLoseMessage;
	
	private String mHeroMurderedTitle;
	private String mHeroMurderedMessage;
	
	public MurderShipStrings() {
		// Create all character conversations
		CreateCharacterNames();
		CreateCharacterIntros();	
		CreateCharacterPreMurderQuestions();
		CreateCharacterPreMurderAccusations();
		CreateCharacterPostMurderConfessions();
		CreateCharacterPostMurderDenials();
		CreateCharacterRemorseStatements();
		CreateCharacterAccuseStatements();
		
		// Create alibi strings
		CreateGroupAlibis();
		CreateSoloAlibis();
		
		// Create all object messages
		CreateObjectNames();
		CreateObjectNoBodySearches();
		CreateObjectBodyFoundSearches();
		
		// Create all item messages
		CreateItemNames();
		CreateItemNoMurderWeaponSearches();
		CreateItemMurderWeaponSearches();
		CreateItemNoMurderWeaponPickUps();
		CreateItemMurderWeaponPickups();
		
		CreateMagnifyingGlassStrings();
		
		CreateIntroStrings();
		
		CreateAlertStrings();
		
		CreateAccuseWithWeaponString();
		CreateAccuseWithoutWeaponString();
		
		CreateGameOverMessages();
	}

	private void CreateCharacterNames() 
	{
		mCharacterNames = new String[NUM_CHARACTERS];
		
		mCharacterNames[0] = new String("Commander Blue");
		mCharacterNames[1] = new String("Mr. Pinko");
		mCharacterNames[2] = new String("Greeny Shoes");
		mCharacterNames[3] = new String("Mr. Redfro");
		mCharacterNames[4] = new String("Square Bot");
		mCharacterNames[5] = new String("Mr. Bulbs");
		mCharacterNames[6] = new String("Bee Man");
		mCharacterNames[7] = new String("Mr. Blinky");
		mCharacterNames[8] = new String("Frazzle Neck");
		mCharacterNames[9] = new String("Mr. Triangle");
	}
	
	private void CreateCharacterIntros()
	{
		mCharacterIntros = new String[NUM_CHARACTERS];
		
		mCharacterIntros[0] = new String("Nice to meet you, Inspector, I'm Commander Blue.");
		mCharacterIntros[1] = new String("Nice to meet you, Inspector, I'm Mr. Pinko.");
		mCharacterIntros[2] = new String("Nice to meet you, Inspector, I'm Greeny Shoes.");
		mCharacterIntros[3] = new String("Nice to meet you, Inspector, I'm Mr. Redfro.");
		mCharacterIntros[4] = new String("Nice to meet you, Inspector, I'm Square Bot.");
		mCharacterIntros[5] = new String("Nice to meet you, Inspector, I'm Mr. Bulbs.");
		mCharacterIntros[6] = new String("Nice to meet you, Inspector, I'm Bee Man.");
		mCharacterIntros[7] = new String("Nice to meet you, Inspector, I'm Mr. Blinky.");
		mCharacterIntros[8] = new String("Nice to meet you, Inspector, I'm Frazzle Neck.");
		mCharacterIntros[9] = new String("Look at my pointy head!  I'm Mr. Triangle!");
	}
	
	private void CreateCharacterPreMurderQuestions()
	{
		mCharacterPreMurderQuestions = new String[NUM_CHARACTERS];
		
		mCharacterPreMurderQuestions[0] = new String("You had better find <1> soon.  <1> should have reported for duty hours ago.");
		mCharacterPreMurderQuestions[1] = new String("Have you seen <1>?  I'm starting to worry.");
		mCharacterPreMurderQuestions[2] = new String("Any sign of <1>?  I sure hope nothing happened.");
		mCharacterPreMurderQuestions[3] = new String("I'm looking for <1>.  <1> should be around here somewhere.");
		mCharacterPreMurderQuestions[4] = new String("If you see <1>, tell the tardy officer to report for duty.");
		mCharacterPreMurderQuestions[5] = new String("I'm getting nervous.  I haven't seen <1> in some time.");
		mCharacterPreMurderQuestions[6] = new String("Where is <1>?  I don't know.  Do you?");
		mCharacterPreMurderQuestions[7] = new String("Do you need help looking for <1>?  Unfortunately, I have no idea where to look.");
		mCharacterPreMurderQuestions[8] = new String("You don't think that anything happened to <1>, do you?");
		mCharacterPreMurderQuestions[9] = new String("Sorry, Inspector, I don't know where <1> is.");
	}
	
	private void CreateCharacterPreMurderAccusations()
	{
		mCharacterPreMurderAccusations = new String[NUM_CHARACTERS];
		
		mCharacterPreMurderAccusations[0] = new String("Excuse me, are you even certain a crime was committed?");
		mCharacterPreMurderAccusations[1] = new String("You found <1>'s body?!  Oh, then why do you think he was murdered?");
		mCharacterPreMurderAccusations[2] = new String("No body means no murder.  I'm sure <1> is fine.");
		mCharacterPreMurderAccusations[3] = new String("I thought <1> was just missing.  Are you suggesting he was murdered?");
		mCharacterPreMurderAccusations[4] = new String("Without seeing a body, how can you conclude <1> was murdered?");
		mCharacterPreMurderAccusations[5] = new String("Apologies, Inspector, but I don't think you have any proof that foul play is involved in <1>'s disappearance.");
		mCharacterPreMurderAccusations[6] = new String("What? I'm confused.  You think <1> was murdered?");
		mCharacterPreMurderAccusations[7] = new String("What are you talking about?  You think something bad happened to <1>?  Do you have proof?");
		mCharacterPreMurderAccusations[8] = new String("Is <1> missing or is <1> murdered?  Oh, please do find him.");
		mCharacterPreMurderAccusations[9] = new String("A triangle would never do anything wrong.");
	}
	
	private void CreateCharacterPostMurderConfessions()
	{
		mCharacterPostMurderConfessions = new String[NUM_CHARACTERS];
		
		mCharacterPostMurderConfessions[0] = new String("Congratulations, Inspector.  You solved the mystery.  I never did like <1>.");
		mCharacterPostMurderConfessions[1] = new String("How did you figure it out?  Yes, I killed <1>!");
		mCharacterPostMurderConfessions[2] = new String("Well, you solved the case, Inspector.  I murdered <1>, and I should have killed you!");
		mCharacterPostMurderConfessions[3] = new String("Yes, I disposed of <1>, and I should have killed you!");
		mCharacterPostMurderConfessions[4] = new String("I murdered <1>, and am happy I did!");
		mCharacterPostMurderConfessions[5] = new String("You got me, Inspector.  I had no choice but to murder <1>.");
		mCharacterPostMurderConfessions[6] = new String("You think you're pretty smart, huh?  Yes, I am <1>'s murderer.");
		mCharacterPostMurderConfessions[7] = new String("Yes, I murdered <1>.  Are you going to arrest me now?");
		mCharacterPostMurderConfessions[8] = new String("It looks like you figured it all out.  I'm your killer.  I'd murder <1> again if I had the chance.");
		mCharacterPostMurderConfessions[9] = new String("Yes, I killed <1>!  I'm glad that I'll never see that dishonest, sneak again!");
	}
	
	private void CreateCharacterPostMurderDenials()
	{
		mCharacterPostMurderDenials = new String[NUM_CHARACTERS];
		
		mCharacterPostMurderDenials[0] = new String("I think you had better look at the evidence again.  I didn't like <1> much, but I'm no murderer.");
		mCharacterPostMurderDenials[1] = new String("You're wrong!  Check my alibi again!  I would never kill <1>.");
		mCharacterPostMurderDenials[2] = new String("Wrong!  I did not kill <1>, and you cannot prove that I did!");
		mCharacterPostMurderDenials[3] = new String("Perhaps <1> was murdered, but you have the wrong person.");
		mCharacterPostMurderDenials[4] = new String("Your reasoning is flawed.  I did not kill <1>.  I suggest you find who did.");
		mCharacterPostMurderDenials[5] = new String("I'm disappointed in you, Inspector.  My alibi is sound.  I'm not <1>'s murderer.");
		mCharacterPostMurderDenials[6] = new String("You're incompetent Inspector!  Let's hope you can find <1>'s real murderer.");
		mCharacterPostMurderDenials[7] = new String("<1> maybe dead, but I didn't do it.  Look elsewhere.");
		mCharacterPostMurderDenials[8] = new String("You have no evidence that I killed <1>.  Check your facts again.");
		mCharacterPostMurderDenials[9] = new String("What?  Me murder <1>?  I'm afraid you're quite mistaken, Inspector.");
	}
	
	private void CreateCharacterRemorseStatements()
	{
		mCharacterRemorseStatements = new String[NUM_CHARACTERS];
		
		mCharacterRemorseStatements[0] = new String("So, <1> was murdered, eh?  Interesting.");
		mCharacterRemorseStatements[1] = new String("Oh, how terrible!  <1> was murdered?");
		mCharacterRemorseStatements[2] = new String("<1> was murdered?  That's terrible!");
		mCharacterRemorseStatements[3] = new String("<1> is dead?  It's hard to believe there is a murderer aboard.");
		mCharacterRemorseStatements[4] = new String("<1> was terminated?  That is very illogical.");
		mCharacterRemorseStatements[5] = new String("How dreadful!  I hope you find out who killed <1>.");
		mCharacterRemorseStatements[6] = new String("Not <1>!  That is most dreadful!");
		mCharacterRemorseStatements[7] = new String("<1> is dead?  Well, I for one won't be missing him.");
		mCharacterRemorseStatements[8] = new String("<1> is, like dead?  Like, actually dead?");
		mCharacterRemorseStatements[9] = new String("You mean that <1> was murdered?  Oh no!");
	}
	
	private void CreateCharacterAccuseStatements()
	{
		mCharacterAccuseStatements = new String[NUM_CHARACTERS];
		
		mCharacterAccuseStatements[0] = new String("I would question <1> if I were you.  <1> never liked <2>.");
		mCharacterAccuseStatements[1] = new String("If you want my opinion, I'd keep an eye on <1>.");
		mCharacterAccuseStatements[2] = new String("Why don't you talk to <1>?");
		mCharacterAccuseStatements[3] = new String("Talk to <1>.  <1> always seemed odd to me.");
		mCharacterAccuseStatements[4] = new String("I shall never understand murder.");
		mCharacterAccuseStatements[5] = new String("It's hard to imagine anyone committing murder, well except for maybe <1>.");
		mCharacterAccuseStatements[6] = new String("Talk to <1>.  There was no love lost between <1> and <2>.");
		mCharacterAccuseStatements[7] = new String("I hope you catch the murderer soon!");
		mCharacterAccuseStatements[8] = new String("I'm going to sleep with one eye open from now on!");
		mCharacterAccuseStatements[9] = new String("Good luck finding the murderer, Inspector!");
	}
	
	private void CreateGroupAlibis()
	{
		mGroupAlibis = new String[NUM_GROUP_ALIBIS];
		
		mGroupAlibis[0] = new String("I was reconfiguring the ship engines with <1> all day, so I couldn't have been the murderer.");
		mGroupAlibis[1] = new String("It wasn't me.  Just ask <1>, we were having dinner together when the murder took place.");
		mGroupAlibis[2] = new String("How could I be the killer?  I was playing space racquetball with <1> for the past two hours.");
		mGroupAlibis[3] = new String("Fortunately, I have a perfect alibi.  I was filling out personnel reports with <1> all day.");
		mGroupAlibis[4] = new String("I didn't hear or see anything.  I was repairing the transporters with <1>.");
		mGroupAlibis[5] = new String("I don't know anything.  I was busy aligning the long range sensors with <1>.");
		mGroupAlibis[6] = new String("<1> and I were engaged in target practice, so you can cross us off your list.");
		mGroupAlibis[7] = new String("I'm not your killer.  Ask <1>-we spent the past several hours recalibrating the navigational systems.");
		mGroupAlibis[8] = new String("<1> and I were on a shuttle mission, so neither of us can be your killer.");
		mGroupAlibis[9] = new String("It's a good thing I have an alibi.  I was on an away mission with <1>.");
	}
	
	private void CreateSoloAlibis()
	{
		mSoloAlibis = new String[NUM_SOLO_ALIBIS];
		
		mSoloAlibis[0] = new String("I realize this looks bad for me, but I spent the day alone.");
		mSoloAlibis[1] = new String("I was completing my geological study, alone, but I assure you I'm not a murderer.");
		mSoloAlibis[2] = new String("I wish I had a better alibi, but you must believe me that I was in my quarters, alone, when the murder took place.");
		mSoloAlibis[3] = new String("Unfortunately, I have no alibi.  Yes, I know it looks bad for me.");
		mSoloAlibis[4] = new String("As for where I was yesterday.  It's none of your business!");
	}
	
	private void CreateObjectNames()
	{
		mObjectNames = new String[NUM_OBJECTS];
		
		mObjectNames[0] = new String("Gray crate");
		mObjectNames[1] = new String("Blue crate");
		mObjectNames[2] = new String("Blue chair");
		mObjectNames[3] = new String("Pink chair");
		mObjectNames[4] = new String("Gray console");
		mObjectNames[5] = new String("Blue console");
		mObjectNames[6] = new String("Fish tank");
		mObjectNames[7] = new String("Dog house");
		mObjectNames[8] = new String("Partition");
		mObjectNames[9] = new String("Bookshelf");
		
	}
	
	private void CreateObjectNoBodySearches()
	{
		mObjectNoBodySearches = new String[NUM_OBJECTS];
		
		mObjectNoBodySearches[0] = new String("You peer into the gray crate, but find nothing.");
		mObjectNoBodySearches[1] = new String("You peer into the blue crate, but find nothing.");
		mObjectNoBodySearches[2] = new String("You look behind the blue chair, but find nothing.");
		mObjectNoBodySearches[3] = new String("You look behind the pink chair, but find nothing.");
		mObjectNoBodySearches[4] = new String("You search behind the gray console, but find nothing.");
		mObjectNoBodySearches[5] = new String("You search behind the blue console, but find nothing.");
		mObjectNoBodySearches[6] = new String("You look behind the fish tank, but find nothing.");
		mObjectNoBodySearches[7] = new String("You look inside the dog house, but find nothing but fur.");
		mObjectNoBodySearches[8] = new String("You look behind the partition, but find nothing.");
		mObjectNoBodySearches[9] = new String("You look behind the bookshelf, but find nothing.");
	}
	
	private void CreateObjectBodyFoundSearches()
	{
		mObjectBodyFoundSearches = new String[NUM_OBJECTS];
		
		mObjectBodyFoundSearches[0] = new String("You peer into the gray crate, and discover the bloody body of <1>!");
		mObjectBodyFoundSearches[1] = new String("You peer into the blue crate, and discover the bloody body of <1>!");
		mObjectBodyFoundSearches[2] = new String("You look behind the blue chair, and find the mangled body of <1>!");
		mObjectBodyFoundSearches[3] = new String("You look behind the pink chair, and find the mangled body of <1>!");
		mObjectBodyFoundSearches[4] = new String("You search behind the gray console, and see the broken body of <1>!");
		mObjectBodyFoundSearches[5] = new String("You search behind the blue console, and see the broken body of <1>!");
		mObjectBodyFoundSearches[6] = new String("You look behind the fish tank, and find the corpse of <1>!");
		mObjectBodyFoundSearches[7] = new String("You look inside the dog house, and find the corpse of <1>!");
		mObjectBodyFoundSearches[8] = new String("You look behind the partition, and discover the bloody body of <1>!");
		mObjectBodyFoundSearches[9] = new String("You look behind the bookshelf, and discover the bloody body of <1>!");
	}
	
	private void CreateItemNames()
	{
		mItemNames = new String[NUM_ITEMS];
		
		mItemNames[0] = new String("Pet snake");
		mItemNames[1] = new String("Laser sword");
		mItemNames[2] = new String("Futuristic scythe");
		mItemNames[3] = new String("Potted plant");
		mItemNames[4] = new String("Painting");
		mItemNames[5] = new String("Ray gun");
		mItemNames[6] = new String("Sunflower");
		mItemNames[7] = new String("Pet black widow");
		mItemNames[8] = new String("Lamp");
	}
	
	private void CreateItemNoMurderWeaponSearches()
	{
		mItemNoMurderWeaponSearches = new String[NUM_ITEMS];
		mItemNoMurderWeaponSearches[0] = new String("The snake seems friendly enough.");
		mItemNoMurderWeaponSearches[1] = new String("Cool, it's a laser sword.");
		mItemNoMurderWeaponSearches[2] = new String("If you've seen one futuristic scythe, you've seen them all.");
		mItemNoMurderWeaponSearches[3] = new String("It's just a boring potted plant.");
		mItemNoMurderWeaponSearches[4] = new String("It looks like an ordinary, not terribly original, painting.");
		mItemNoMurderWeaponSearches[5] = new String("It's just an ordinary ray gun.");
		mItemNoMurderWeaponSearches[6] = new String("Hmm, just a boring, potted, sunflower.");
		mItemNoMurderWeaponSearches[7] = new String("Be careful, don't touch it!");
		mItemNoMurderWeaponSearches[8] = new String("This lamp looks like an antique.");
	}
	
	private void CreateItemMurderWeaponSearches()
	{
		mItemMurderWeaponSearches = new String[NUM_ITEMS];
		mItemMurderWeaponSearches[0] = new String("There is blood on the snake's teeth.  The snake is the murder weapon!");
		mItemMurderWeaponSearches[1] = new String("There are blood stains on the hilt.  The laser sword is the murder weapon!");
		mItemMurderWeaponSearches[2] = new String("There is blood on the scythe!  You have found the murder weapon!");
		mItemMurderWeaponSearches[3] = new String("You discover blood around the pot.  The plant must be the murder weapon!");
		mItemMurderWeaponSearches[4] = new String("Is that wet paint?  No, it's blood!  The painting is the murder weapon...somehow!");
		mItemMurderWeaponSearches[5] = new String("The ray gun was discharged very recently!  It is the murder weapon!");
		mItemMurderWeaponSearches[6] = new String("There is blood around the clay pot!  Someone used this sunflower in a murder!");
		mItemMurderWeaponSearches[7] = new String("There is blood on the spider's fangs!  Someone was killed with this pet!");
		mItemMurderWeaponSearches[8] = new String("There are traces of blood on the lamp.  This must be the murder weapon!");
	}
	
	private void CreateItemNoMurderWeaponPickUps()
	{
		mItemNoMurderWeaponPickUps = new String[NUM_ITEMS];
		
		mItemNoMurderWeaponPickUps[0] = new String("Why would you want to carry a snake around?");
		mItemNoMurderWeaponPickUps[1] = new String("Though the laser sword is cool, you don't particularly need it.");
		mItemNoMurderWeaponPickUps[2] = new String("There's no reason to carry a scythe around.  Come to think of it, who needs a scythe on a spaceship?");
		mItemNoMurderWeaponPickUps[3] = new String("You're an inspector, damn it, not a gardener!");
		mItemNoMurderWeaponPickUps[4] = new String("The painting is fine where it is.");
		mItemNoMurderWeaponPickUps[5] = new String("Yeah, ray guns are cool, but you are not licensed to carry this one.");
		mItemNoMurderWeaponPickUps[6] = new String("You're an inspector, damn it, not a gardener!");
		mItemNoMurderWeaponPickUps[7] = new String("Um, no, you don't want to carry a venomous spider around!");
		mItemNoMurderWeaponPickUps[8] = new String("The lamp is fine where it is.");
	}
	
	private void CreateItemMurderWeaponPickups()
	{
		mItemMurderWeaponPickUps = new String[NUM_ITEMS];
		
		mItemMurderWeaponPickUps[0] = new String("You CAREFULLY catch the murderous snake, and take it with you.  But who released the snake?");
		mItemMurderWeaponPickUps[1] = new String("The laser sword was clearly used in a murder.  You take it with you.");
		mItemMurderWeaponPickUps[2] = new String("The scythe was clearly used in a murder.  You take it with you.");
		mItemMurderWeaponPickUps[3] = new String("You pick up the bloody clay pot and carry it with you.");
		mItemMurderWeaponPickUps[4] = new String("You pick up the painting, dripping with blood, and carry it with you.");
		mItemMurderWeaponPickUps[5] = new String("This laser gun was clearly fired recently.  You take the murder weapon with you.");
		mItemMurderWeaponPickUps[6] = new String("You pick up the bloody clay pot and carry it with you.");
		mItemMurderWeaponPickUps[7] = new String("You CAREFULLY catch the murderous spider, and take it with you.  But who released the spider?");
		mItemMurderWeaponPickUps[8] = new String("The bloody lamp clearly was used in a murder.  You take it with you.");
	}
	
	private void CreateMagnifyingGlassStrings() 
	{
		mMagnifyingGlassName = new String("Magnifying glass");
		mMagnifyingGlassExamine = new String("It's a magnifying glass.  That might be useful.");
		mMagnifyingGlassPickUp = new String("Good idea, you take the magnifying glass.");
		mWithMagnifyingGlassSearch = new String("Closer inspection with the magnifying class reveals nothing unusual.");
		mWithoutMagnifyingGlassSearch = new String("You might be able to see more if you had a magnifying glass.");
	}
	
	private void CreateIntroStrings()
	{
		mIntroTitle = new String("Security Officer's Log");
		mIntroMessage = new String("Crew Member <1> was expected to report to duty over six hours ago.  As Chief Security Officer, it is my duty to determine what happened...");
	}
	
	private void CreateAlertStrings()
	{
		mYellowAlertTitle = new String("Security Officer's Log");
		mYellowAlertMessage = new String("I had better determine what happened to <1> soon.  I fear that my investigation is drawing suspicion!");
		
		mRedAlertTitle = new String("Security Officer's Log");
		mRedAlertMessage = new String("I fear it has taken me too long to find <1>'s murderer.  The murderer is hunting me!");
	}
	
	private void CreateAccuseWithWeaponString()
	{
		mAccuseWithWeapon = new String("I see you found the murder weapon as well.  Congratulations, Inspector.  I suppose I'm under arrest.");
	}
	
	private void CreateAccuseWithoutWeaponString()
	{
		mAccuseWithoutWeapon = new String("Ah, but I see you do not have the murder weapon.  Fortunately, I do!  Good bye, Inspector!");
	}
	
	private void CreateGameOverMessages()
	{
		mWinTitle = new String("Congratulations");
		mWinMessage = new String("Good work, Inspector!  You successfully stopped the vicious murderer, <1>.  Your final score was <2>.");
		
		mLoseTitle = new String("Game Over");
		mLoseMessage = new String("Unfortunately, your failure has allowed <1> to escape.  Better luck next time.");
		
		mHeroMurderedTitle = new String("Uh Oh!");
		mHeroMurderedMessage = new String("<1> grabs the <2>, and efficiently executes you with it!");
	}
	
	public String getCharacterNameString(int characterIndex)
	{
		if (characterIndex < 0 || characterIndex >= NUM_CHARACTERS)
			return mCharacterNames[0];
		else
			return mCharacterNames[characterIndex];
	}
	
	
	public String getCharacterIntroString(int characterIndex)
	{
		if (characterIndex < 0 || characterIndex >= NUM_CHARACTERS)
			return mCharacterIntros[0];
		else
			return mCharacterIntros[characterIndex];
	}
	
	public String getCharacterPreMurderQuestionString(int characterIndex, int victimIndex)
	{
		if (characterIndex < 0 || characterIndex >= NUM_CHARACTERS)
			return mCharacterPreMurderQuestions[0].replaceAll("<1>", getCharacterNameString(victimIndex));
		else
			return mCharacterPreMurderQuestions[characterIndex].replaceAll("<1>", getCharacterNameString(victimIndex));
	}
	
	public String getCharacterPreMurderAccusationString(int characterIndex, int victimIndex)
	{
		if (characterIndex < 0 || characterIndex >= NUM_CHARACTERS)
			return mCharacterPreMurderAccusations[0].replaceAll("<1>", getCharacterNameString(victimIndex));
		else
			return mCharacterPreMurderAccusations[characterIndex].replaceAll("<1>", getCharacterNameString(victimIndex));
	}
	
	public String getCharacterPostMurderConfessionString(int characterIndex, int victimIndex)
	{
		if (characterIndex < 0 || characterIndex >= NUM_CHARACTERS)
			return mCharacterPostMurderConfessions[0].replaceAll("<1>", getCharacterNameString(victimIndex));
		else
			return mCharacterPostMurderConfessions[characterIndex].replaceAll("<1>", getCharacterNameString(victimIndex));
	}
	
	public String getCharacterPostMurderDenialString(int characterIndex, int victimIndex)
	{
		if (characterIndex < 0 || characterIndex >= NUM_CHARACTERS)
			return mCharacterPostMurderDenials[0].replaceAll("<1>", getCharacterNameString(victimIndex));
		else
			return mCharacterPostMurderDenials[characterIndex].replaceAll("<1>", getCharacterNameString(victimIndex));
	}
	
	public String getCharacterRemorseStatementString(int characterIndex, int victimIndex)
	{
		if (characterIndex < 0 || characterIndex >= NUM_CHARACTERS)
			return mCharacterRemorseStatements[0].replaceAll("<1>", getCharacterNameString(victimIndex));
		else
			return mCharacterRemorseStatements[characterIndex].replaceAll("<1>", getCharacterNameString(victimIndex));
	}
	
	public String getCharacterAccuseStatementString(int characterIndex, int accuseIndex, int victimIndex)
	{
		if (characterIndex < 0 || characterIndex >= NUM_CHARACTERS)
			return mCharacterAccuseStatements[0].replaceAll("<1>", getCharacterNameString(accuseIndex)).replaceAll("<2>", getCharacterNameString(victimIndex));
		else
			return mCharacterAccuseStatements[characterIndex].replaceAll("<1>", getCharacterNameString(accuseIndex)).replaceAll("<2>", getCharacterNameString(victimIndex));
	}
	
	public String getCharacterVictimFoundQuestionString(int characterIndex, boolean groupAlibi, int alibiIndex, int alibiCharacterIndex, int victimIndex, int accuseIndex)
	{
		String result = getCharacterRemorseStatementString(characterIndex, victimIndex);
		
		if (groupAlibi)
			result+=("  " + getGroupAlibiString(alibiIndex, alibiCharacterIndex));
		else
			result+=("  " + getSoloAlibiString(alibiIndex));
		
		result+=("  " + getCharacterAccuseStatementString(characterIndex, accuseIndex, victimIndex));
		
		return result;
	}
	
	public String getGroupAlibiString(int groupAlibiIndex, int alibiCharacterIndex)
	{
		if (groupAlibiIndex < 0 || groupAlibiIndex >= NUM_GROUP_ALIBIS)
			return mGroupAlibis[0].replaceAll("<1>", getCharacterNameString(alibiCharacterIndex));
		else
			return mGroupAlibis[groupAlibiIndex].replaceAll("<1>", getCharacterNameString(alibiCharacterIndex));
	}
	
	public String getSoloAlibiString(int soloAlibiIndex)
	{
		if (soloAlibiIndex < 0 || soloAlibiIndex >= NUM_SOLO_ALIBIS)
			return mSoloAlibis[0];
		else
			return mSoloAlibis[soloAlibiIndex];
	}
	
	public String getObjectNameString(int objectIndex)
	{
		if (objectIndex < 0 || objectIndex >= NUM_OBJECTS)
			return mObjectNames[0];
		else
			return mObjectNames[objectIndex];
	}
	
	public String getObjectNoBodySearchString(int objectIndex)
	{
		if (objectIndex < 0 || objectIndex >= NUM_OBJECTS)
			return mObjectNoBodySearches[0];
		else
			return mObjectNoBodySearches[objectIndex];
	}
	
	public String getObjectBodyFoundSearchString(int objectIndex, int victimIndex)
	{
		if (objectIndex < 0 || objectIndex >= NUM_OBJECTS)
			return mObjectBodyFoundSearches[0].replaceAll("<1>", getCharacterNameString(victimIndex));
		else
			return mObjectBodyFoundSearches[objectIndex].replaceAll("<1>", getCharacterNameString(victimIndex));
	}
	
	public String getItemNameString(int itemIndex)
	{
		if (itemIndex < 0 || itemIndex >= NUM_ITEMS)
			return mItemNames[0];
		else
			return mItemNames[itemIndex];
	}
	
	public String getItemNoMurderWeaponSearchString(int itemIndex, boolean haveMagnifyingGlass)
	{
		String result;
		
		if (itemIndex < 0 || itemIndex >= NUM_ITEMS)
			result=mItemNoMurderWeaponSearches[0];
		else
			result=mItemNoMurderWeaponSearches[itemIndex];
		
		if (haveMagnifyingGlass)
			result+=("  " + mWithMagnifyingGlassSearch + " ");
		else
			result+=("  " + mWithoutMagnifyingGlassSearch);
		
		return result;
	}
	
	public String getItemMurderWeaponSearchString(int itemIndex)
	{
		if (itemIndex < 0 || itemIndex >= NUM_ITEMS)
			return mItemMurderWeaponSearches[0];
		else
			return mItemMurderWeaponSearches[itemIndex];
	}
	
	public String getItemNoMurderWeaponPickUpString(int itemIndex)
	{
		if (itemIndex < 0 || itemIndex >= NUM_ITEMS)
			return mItemNoMurderWeaponPickUps[0];
		else
			return mItemNoMurderWeaponPickUps[itemIndex];
	}
	
	public String getItemMurderWeaponPickUpString(int itemIndex)
	{
		if (itemIndex < 0 || itemIndex >= NUM_ITEMS)
			return mItemMurderWeaponPickUps[0];
		else
			return mItemMurderWeaponPickUps[itemIndex];
	}
	
	public String getMagnifyingGlassNameString()
	{
		return mMagnifyingGlassName;
	}
	
	public String getMagnifyingGlassExamineString()
	{
		return mMagnifyingGlassExamine;
	}
	
	public String getMagnifyingGlassPickUp()
	{
		return mMagnifyingGlassPickUp;
	}
	
	public String getIntroTitleString()
	{
		return mIntroTitle;
	}
	
	public String getIntroMessageString(int victimIndex)
	{
		return mIntroMessage.replaceAll("<1>", getCharacterNameString(victimIndex));
	}
	
	public String getYellowAlertTitleString()
	{
		return mYellowAlertTitle;
	}
	
	public String getYellowAlertMessageString(int victimIndex)
	{
		return mYellowAlertMessage.replaceAll("<1>", getCharacterNameString(victimIndex));
	}
	
	public String getRedAlertTitleString()
	{
		return mRedAlertTitle;
	}
	
	public String getRedAlertMessageString(int victimIndex)
	{
		return mRedAlertMessage.replaceAll("<1>", getCharacterNameString(victimIndex));
	}
	
	public String getAccuseWithWeaponString()
	{
		return mAccuseWithWeapon;
	}
	
	public String getAccuseWithoutWeaponString()
	{
		return mAccuseWithoutWeapon;
	}
	
	public String getWinTitleString()
	{
		return mWinTitle;
	}
	
	public String getWinMessageString(int murdererIndex, int score)
	{
		return mWinMessage.replaceAll("<1>", getCharacterNameString(murdererIndex)).replaceAll("<2>", Integer.toString(score));
	}
	
	public String getLoseTitleString()
	{
		return mLoseTitle;
	}
	
	public String getLoseMessageString(int murdererIndex)
	{
		return mLoseMessage.replaceAll("<1>", getCharacterNameString(murdererIndex));
	}
	
	public String getHeroMurderedTitleString()
	{
		return mHeroMurderedTitle;
	}
	
	public String getHeroMurderedMessageString(int murdererIndex, int itemIndex)
	{
		return mHeroMurderedMessage.replaceAll("<1>", getCharacterNameString(murdererIndex)).replaceAll("<2>", getItemNameString(itemIndex));
	}
}

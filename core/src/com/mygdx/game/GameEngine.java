package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * @author Gandhi-Inc.
 * @version Assessment 3
 *          An executable version of the game can be found at: http://gandhi-inc.me/downloads/assessment3.jar
 *          Our website is: www.gandhi-inc.me
 * @since Assessment 3
 */
public class GameEngine {
    /**
     * Stores current game-state, enabling transitions between screens and external QOL drawing functions
     */
    private Game game;

    /**
     * The game's engine only ever runs while the main in-game interface is showing, so it was designed to manipulate
     * elements (both visual and logical in nature) on that screen
     * It therefore requires access to the public methods in the GameScreen class, so instantiation in this class
     * is a necessity
     */
    private GameScreen gameScreen;

    /**
     * Stores data pertaining to the game's active players
     * For more information, check the "Player" class
     */
    private Player[] players;

    /**
     * Holds the numeric getID of the player who's currently active in the game
     */
    private int currentPlayerID;

    /**
     * Holds the number of the phase that the game is currently in
     * Varies between 1 and 5
     */
    private int phase;

    /**
     * Defines whether or not a tile has been acquired in the current phase of the game
     */
    private boolean tileAcquired;

    /**
     * Timer used to dictate the pace and flow of the game
     * This has a visual interface which will be displayed in the top-left corner of the game-screen
     */
    private GameTimer timer;

    /**
     * Object providing QOL drawing functions to simplify visual construction and rendering tasks
     */
    private Drawer drawer;

    /**
     * Holds all of the data and the functions of the game's market
     * Also comes bundled with a visual interface which can be rendered on to the game's screen
     */
    private Market market;

    /**
     * Array holding the tiles to be laid over the map
     * Note that the tiles' visuals are encoded by the image declared and stored in the GameScreen class (and not here)
     */
    private Tile[] tiles;

    /**
     * Holds the data pertaining to the currently-selected tile
     */
    private Tile selectedTile;

    /**
     * Variable dictating whether the game is running or paused at any given moment
     */
    private State state;

    /**
     * An integer signifying the ID of the next roboticon to be created
     *
     */
    private Integer roboticonIDCounter = 0;

    /**
     * Constructs the game's engine. Imports the game's state (for direct renderer access) and the data held by the
     * GameScreen which this engine directly controls; then goes on to set up player-data for the game's players,
     * tile-data for the on-screen map and the in-game market for in-play manipulation. In the cases of the latter
     * two tasks, the engine directly interacts with the GameScreen object imported to it (as a parameter of this
     * constructor) so that it can draw the visual interfaces for the game's market and tiles directly to the game's
     * primary interface.
     *
     * @param game Variable storing the game's state
     * @param gameScreen The object encoding the in-game interface which is to be controlled by this engine
     * @param player1 The name of player 1
     * @param player2 The name of player 2
     * @param college1 The college for player 1
     * @param college2 The college for player 2
     */
    public GameEngine(Game game, GameScreen gameScreen, String player1, String player2, College college1, College college2) {
        this.game = game;
        //Import current game-state to access the game's renderer

        this.gameScreen = gameScreen;
        //Bind the engine to the main in-game interface
        //Required to alter the visuals and logic of the interface directly through this engine

        drawer = new Drawer(this.game);
        //Import QOL drawing function

        players = new Player[3];
        currentPlayerID = 1;
        //Set up objects to hold player-data
        //Start the game such that player 1 makes the first move

        phase = 1;
        //Start the game in the first phase (of 5, which recur until all tiles are claimed)

        timer = new GameTimer(0, new TTFont(Gdx.files.internal("font/testfontbignoodle.ttf"), 120), Color.WHITE, new Runnable() {
            @Override
            public void run() {
                nextPhase();
            }
        });
        //Set up game timer
        //Game timer automatically ends the current turn when it reaches 0

        tiles = new Tile[16];
        //Initialise data for all 16 tiles on the screen
        //This instantiation does NOT automatically place the tiles on the game's main interface

        for (int i = 0; i < 16; i++) {
            final int fi = i;
            final GameScreen gs = gameScreen;

            tiles[i] = new Tile(this.game, i + 1, 5, 5, 5, false, new Runnable() {
                @Override
                public void run() {
                    gs.selectTile(tiles[fi]);
                    selectedTile = tiles[fi];
                }
            });
        }
        //Configure all 16 tiles with independent yields and landmark data
        //Also assign listeners to them so that they can detect mouse clicks

        market = new Market(game, this);
        //Instantiates the game's market and hands it direct renderer access

        state = State.RUN;
        //Mark the game's current play-state as "running" (IE: not paused)

        // Create 2 players objects which will represent the two human players taking the arguments of an integer to represent their ID and a string which is the players name
        Player Player1 = new Player(1, player1);
        Player Player2 = new Player(2, player2);
        // Then assign these players to indexes in the array of players
        players[1] = Player1;
        players[2] = Player2;

        // Then assign the college to players that they chose on the main menu
        players[1].assignCollege(college1);
        players[2].assignCollege(college2);
        // Then assign the player to the college to insure integrity of the data throughout the system
        college1.assignPlayer(players[1]);
        college2.assignPlayer(players[2]);
    }

    /**
     * Advances the game's progress upon call
     * Acts as a state machine of sorts, moving the game from one phase to another depending on what phase it is
     * currently at when this method if called. If player 1 is the current player in any particular phase, then the
     * phase number remains and control is handed off to the other player: otherwise, control returns to player 1 and
     * the game advances to the next state, implementing any state-specific features as it goes.
     * <br>
     * PHASE 1: Acquisition of Tiles
     * PHASE 2: Acquisition of Roboticons
     * PHASE 3: Placement of Roboticons
     * PHASE 4: Production of Resources by Roboticons
     * PHASE 5: Market Trading
     */
    public void nextPhase() {
        System.out.print("Player " + currentPlayerID + " | Phase " + phase + "\n");
        // Create a random effect class so that the random effects can be performed during the game
        RandomEffect randomEffect = new RandomEffect();
        timer.stop();

        // Switch statement for what to do on advance the phase
        switch (phase) {
            case 1: Phase2Setup();
                    break;
            case 2: Phase3Setup();
                    break;
            case 3: Phase4Setup();
                    break;
            case 4: Phase5Setup();
                    break;
            case 5: Phase1Setup();
                    // At the end of a round perform the random effect functionality
            		randomEffect.randomlyChooseEffect(players[currentPlayerID]);
                    break;
        }

        gameScreen.closeUpgradeOverlay();
        //If the upgrade overlay is open, close it when the next phase begins
    }

    private void Phase2Setup()
    {
        if(tileAcquired == true) {
            tileAcquired = false;

            if (currentPlayerID == 1) {
                switchCurrentPlayer();
            } else {
                phase = 2;
                gameScreen.updatePhaseLabel("BUY ROBOTICONS");

                timer.setTime(0, 30);
                timer.start();
                //Reset the timer to 30 seconds and start it if the game is proceeding into phase 2

                switchCurrentPlayer();
                drawer.switchTextButton(gameScreen.endTurnButton(), true, Color.WHITE);
                //Once the game moves out of phase 1, re-enable the "end turn" button
                //This button is disabled during phase 1 to force players into claiming tiles

                market.refreshButtonAvailability();
                //Update the market's interface to allow for roboticons to be purchased
            }
        }
    }

    private void Phase3Setup()
    {
        timer.setTime(0, 30);
        timer.start();
        //Reset the timer to 30 seconds, regardless of whether the game is staying in phase 2 or proceeding into phase 3

        if(currentPlayerID == 1){
            switchCurrentPlayer();

            market.refreshButtonAvailability();
            //Update the market interface for the other player
        }
        else{
            phase = 3;
            gameScreen.updatePhaseLabel("PLACE ROBOTICONS");

            market.refreshButtonAvailability();
            //Disable the market's interface

            switchCurrentPlayer();
        }

        gameScreen.selectTile(selectedTile);
        //Re-select the current tile to prevent buttons from being enabled mistakenly
    }

    private void Phase4Setup()
    {
        if(currentPlayerID == 1){
            timer.setTime(0, 30);
            timer.start();
            //Reset the timer to 30 seconds for the other player

            switchCurrentPlayer();
        }
        else {
            phase = 4;
            gameScreen.updatePhaseLabel("PRODUCTION");

            timer.setTime(0, 0);
            //Stop the timer if the game is entering phase 4

            switchCurrentPlayer();
        }

        gameScreen.selectTile(selectedTile);
        //Re-select the current tile to prevent buttons from being enabled mistakenly
    }

    private void Phase5Setup()
    {
        List<Tile> tileList = players[1].getTileList();
        for (Tile Tile : tileList){

            if (tileList.size() > 0){
                players[1] = Tile.Produce(players[1]);
                switchCurrentPlayer();
            }

        }
        List<Tile> tileList2 = players[2].getTileList();
        for (Tile Tile : tileList2){
            if(tileList2.size() > 0){
                players[2] = Tile.Produce(players[2]);
                switchCurrentPlayer();
            }

        }

        phase = 5;
        gameScreen.updatePhaseLabel("MARKET OPEN");

        market.refreshButtonAvailability();
        //Open the market again

        gameScreen.selectTile(selectedTile);
        //Re-select the current tile to prevent buttons from being enabled mistakenly

        // The game has ended then perform the function for the game to end
        if(checkGameEnd() == true){
            gameEnd();
        }

    }

    private void gameEnd()
    {
        // Store the players current scores
        Integer score1 = players[1].calculateScore(market);
        Integer score2 = players[2].calculateScore(market);
        // Log the winner to the terminal (Mainly used for testing, user is unlikely to view this unless they run the game from the terminal)
        String winner = "";
        if(score1 > score2)
        {
            System.out.print("Player 1 Wins!");
            winner = "Player 1 wins!";
        }
        else if (score1 < score2)
        {
            System.out.print("Player 2 Wins!");
            winner = "Player 2 wins!";
        }
        else
        {
            System.out.print("Players draw!");
            winner = "It was a tie";
        }

        // Disable the end turn button so that they cannot continue to play the game after this point
        drawer.switchTextButton(gameScreen.endTurnButton(), false, Color.GRAY);
        // Save the players scores to GameSave.txt file
        LeaderboardBackend.AddPlayerToLeaderboard(players[1].getName(), players[1].calculateScore(market));
        LeaderboardBackend.AddPlayerToLeaderboard(players[2].getName(), players[2].calculateScore(market));

        // Display a dialog to tell the players who won
    	JOptionPane winnerOptionPane = new JOptionPane(winner + " Click 'Ok' to return to main menu");
    	JDialog winnerDialog = winnerOptionPane.createDialog("Congratulations!");
    	winnerDialog.setAlwaysOnTop(true);
    	winnerDialog.setVisible(true);

    	//Game freezes at this stage...
    	game.setScreen(new MainMenu(game));							// Returns to main screen
    }

    private void Phase1Setup()
    {
        if (currentPlayerID == 1) {
            switchCurrentPlayer();

            market.refreshButtonAvailability();
            //Update the market's interface for the other player
        }
        else if (checkGameEnd() == false) {
    		market.produceRoboticons();
            phase = 1;
            gameScreen.updatePhaseLabel("ACQUISITION");

            market.refreshButtonAvailability();
            //Close the market again

            drawer.switchTextButton(gameScreen.endTurnButton(), false, Color.GRAY);
            //Disable the "end turn" button during phase 1 to force players into claiming tiles
            switchCurrentPlayer();
        }

        gameScreen.selectTile(selectedTile);
        //Re-select the current tile to prevent buttons from being enabled mistakenly
    }

    /**
     * Sets the current player to be that which isn't active whenever this is called
     * Updates the in-game interface to reflect the statistics and the identity of the player now controlling it
     */
    private void switchCurrentPlayer() {
        currentPlayerID = 3 - currentPlayerID;
        //3 - 1 = 2
        //3 - 2 = 1
        //This naturally switches the getID of the currently-active player from 1 to 2 and vice-versa

        gameScreen.currentPlayerIcon().setDrawable(new TextureRegionDrawable(new TextureRegion(players[currentPlayerID].getCollege().getLogoTexture())));
        gameScreen.currentPlayerIcon().setSize(64, 64);
        //Find and draw the icon representing the "new" player's associated college

        gameScreen.updateInventoryLabels();
        //Display the "new" player's inventory on-screen

    }

    /**
     * Pauses the game and opens the pause-menu (which is just a sub-stage in the GameScreen class)
     * Specifically pauses the game's timer and marks the engine's internal play-state to [State.PAUSE]
     */
    public void pauseGame() {
        timer.stop();
        //Stop the game's timer

        gameScreen.openPauseStage();
        //Prepare the pause menu to accept user inputs

        state = State.PAUSE;
        //Mark that the game has been paused
    }

    /**
     * Resumes the game and re-opens the primary in-game inteface
     * Specifically increments the in-game timer by 1 second, restarts it and marks the engine's internal play-state
     * to [State.PAUSE]
     * Note that the timer is incremented by 1 second to circumvent a bug that causes it to lose 1 second whenever
     * it's restarted
     */
    public void resumeGame() {
        state = State.RUN;
        //Mark that the game is now running again

        gameScreen.openGameStage();
        //Show the main in-game interface again and prepare it to accept inputs

        if (timer.minutes() > 0 || timer.seconds() > 0) {
            timer.increment();
            timer.start();
        }
        //Restart the game's timer from where it left off
        //The timer needs to be incremented by 1 second before being restarted because, for a reason that I can't
        //quite identify, restarting the timer automatically takes a second off of it straight away
    }

    /**
     * Claims the last tile to have been selected on the main GameScreen for the active player
     * This grants them the ability to plant a Roboticon on it and yield resources from it for themselves
     * Specifically registers the selected tile under the object holding the active player's data, re-colours its
     * border for owner identification purposes and moves the game on to the next player/phase
     */
    public void claimTile() {
        if (phase == 1 && selectedTile.isOwned() == false) {
            players[currentPlayerID].assignTile(selectedTile);
            //Assign selected tile to current player

            selectedTile.setOwner(players[currentPlayerID]);
            //Set the owner of the currently selected tile to be the current player

            tileAcquired = true;
            //Mark that a tile has been acquired on this turn

            // Removed the code to control the tile color from here, has now been migrated to the Tile and Collge class since it is the College which determines the Color of the tile

            nextPhase();
            //Advance the game
        }
    }

    /**
     * Deploys a Roboticon on the last tile to have been selected
     * Draws a Roboticon from the active player's Roboticon count and assigns it to the tile in question
     */
    public void deployRoboticon() {
        if (phase == 3) {
            if (selectedTile.hasRoboticon() == false) {
                if (players[currentPlayerID].getRoboticonInventory() > 0) {
                    Roboticon Roboticon = new Roboticon(roboticonIDCounter, players[currentPlayerID], selectedTile);
                    selectedTile.assignRoboticon(Roboticon);
                    roboticonIDCounter += 1;
                    players[currentPlayerID].decreaseRoboticonInventory();
                    gameScreen.updateInventoryLabels();
                }
            }
        }
    }

    /**
     * Return's the game's current play-state, which can either be [State.RUN] or [State.PAUSE]
     * This is not to be confused with the game-state (which is directly linked to the renderer)
     *
     * @return State The game's current play-state
     */
    public State state() {
        return state;
    }

    /**
     * Return's the game's phase as a number between (or possibly one of) 1 and 5
     *
     * @return Integer The game's current phase
     */
    public int phase() {
        return phase;
    }

    /**
     * Returns all of the data pertaining to the array of players managed by the game's engine
     * Unless the game's architecture changes radically, this should only ever return two Player objects
     *
     * @return Player[] An array of all Player objects (encapsulating player-data) managed by the engine
     */
    public Player[] players() { return players; }

    /**
     * Returns the data pertaining to the player who is active at the time when this is called
     *
     * @return Player The current user's Player object, encoding all of their data
     */
    public Player currentPlayer() { return players[currentPlayerID]; }

    /**
     * Returns the ID of the player who is active at the time when this is called
     *
     * @return Integer The current player's ID
     */
    public int currentPlayerID() {
        return currentPlayerID;
    }

    /**
     * Returns the GameTimer declared and managed by the engine
     *
     * @return GameTimer The game's internal timer
     */
    public GameTimer timer() {
        return timer;
    }

    /**
     * Collectively returns every Tile managed by the engine in array
     *
     * @return Tile[] An array of all Tile objects (encapsulating tile-data) managed by the engine
     */
    public Tile[] tiles() {
        return tiles;
    }

    /**
     * Returns the data pertaining to the last Tile that was selected by a player
     *
     * @return Tile The last Tile to have been selected
     */
    public Tile selectedTile() {
        return selectedTile;
    }

    /**
     * Returns all of the data pertaining to the game's market, which is declared and managed by the engine
     *
     * @return Market The game's market
     */
    public Market market() {
        return market;
    }

    /**
     * Returns a value that's true if all tiles have been claimed, and false otherwise
     *
     * @return Boolean Determines if the game has ended or not
     */
    private boolean checkGameEnd(){
        boolean end = true;
        for(Tile Tile : tiles){
            if(Tile.getOwner().getPlayerID() == 0){
                end = false;
            }
        }
        return end;
    }

    /**
     * Updates the data pertaining to the game's current player
     * This is used by the Market class to process item transactions
     *
     * @param currentPlayer The new Player object to represent the active player with
     */
    public void updateCurrentPlayer(Player currentPlayer) {
        players[currentPlayerID] = currentPlayer;

        gameScreen.updateInventoryLabels();
        //Refresh the on-screen inventory labels to reflect the new object's possessions
    }

    /**
     * Function for upgrading a particular level of the roboticon stored on the last tile to have been selected
     * @param resource The type of resource which the roboticon will gather more of {0: ore | 1: energy | 2: food}
     */
    public void upgradeRoboticon(int resource) {
        if (selectedTile().getRoboticonStored().getLevel()[resource] < selectedTile().getRoboticonStored().getMaxLevel()) {
            switch (resource) {
                case (0):
                    currentPlayer().setMoney(currentPlayer().getMoney() - selectedTile.getRoboticonStored().getOreUpgradeCost());
                    break;
                case (1):
                    currentPlayer().setMoney(currentPlayer().getMoney() - selectedTile.getRoboticonStored().getEnergyUpgradeCost());
                    break;
                case (2):
                    currentPlayer().setMoney(currentPlayer().getMoney() - selectedTile.getRoboticonStored().getFoodUpgradeCost());
                    break;
            }

            selectedTile().getRoboticonStored().upgrade(resource);
        }
        //Upgrade the specified resource
        //0: ORE
        //1: ENERGY
        //2: FOOD
    }

    /**
     * Encodes possible play-states
     * These are not to be confused with the game-state (which is directly linked to the renderer)
     */
    public enum State {
        RUN,
        PAUSE
    }
}

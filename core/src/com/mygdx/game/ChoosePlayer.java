package com.mygdx.game;

import java.awt.Desktop;
import java.net.URL;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * This has been modified from the main menu class
 * @author Gandhi Inc.
 * @since Assessment 3
 * @version Assessment 3
 *
 */
public class ChoosePlayer implements Screen {

    /**
     * Stores current game-state, enabling transitions between screens and external QOL drawing functions
     */
    private Game game;

    /**
     * On-screen stage which can be populated with actors
     */
    private Stage stage;

    /**
     * Provides the spatial framework for menu buttons and labels to be organised over
     */
    private Table table;

    /**
     * Array of all the text that needs to be rended. 
     */
    private TextButton[] buttons = new TextButton[12];

    /**
     * Establishes the font which is used to encode the menu's options
     */
    private TTFont menuFont;

    /**
     * Object defining QOL drawing functions for rectangles and on-screen tables
     * Used in this class accelerate table row creation
     */
    private Drawer drawer;

    /**
     * Batch that manages the rendering pipeline for all of the images to be displayed on the screen
     */
    private SpriteBatch batch;

    /**
     * The object which will encode the menu's background
     */
    private Sprite background;


    public ChoosePlayer(Game game){ 																				    //Import current game-state
        this.game = game;
    }

    /**
     * Secondary constructor of the main menu which focuses on preparing visual elements
     * Specifically instantiates the menu's stage; spatial construction table; fonts; background image and buttons
     * before adding the stage containing the table (which itself contains the menu's labels, buttons and background
     * image) to the screen's rendering pipeline, which is also set up at the beginning of this method
     */
    @Override
    public void show() {
        drawer = new Drawer(game);

        batch = new SpriteBatch(); 																					//Initialise sprite-batch
        
        stage = new Stage();
        table = new Table();																						//Initialise stage and button-table

        menuFont = new TTFont(Gdx.files.internal("font/enterthegrid.ttf"), 36, 2, Color.BLACK, false);				//Initialise menu font

        Gdx.input.setInputProcessor(stage);																			//Set the stage up to accept user inputs
        
        background = new Sprite(new Texture("image/Solid_white.svg.png"));
        background.setSize(background.getWidth(), background.getHeight());
        background.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); 									//Fill the screen with the table


        TextButton.TextButtonStyle menuButtonStyle = new TextButton.TextButtonStyle(); 								//enables the button to look like it is depressed when it is pressed
        menuButtonStyle.font = menuFont.font();
        menuButtonStyle.fontColor = Color.BLACK;
        menuButtonStyle.pressedOffsetX = 1;
        menuButtonStyle.pressedOffsetY = -1;

        buttons[0] = new TextButton("Back to main menu", menuButtonStyle); 	
        buttons[0].addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                game.setScreen(new MainMenu(game));
            }
        });
        
        
        buttons[1] = new TextButton("Derwent", menuButtonStyle); 												//places the lines of text into an array
        buttons[2] = new TextButton("Langwith", menuButtonStyle); 															//which is then rendered onto the screen later
        buttons[3] = new TextButton("Vanburgh", menuButtonStyle);
        buttons[4] = new TextButton("James", menuButtonStyle);
        buttons[5] = new TextButton("Wentworth", menuButtonStyle);
        buttons[6] = new TextButton("Halifax", menuButtonStyle);
        buttons[7] = new TextButton("Alcuin", menuButtonStyle);
        buttons[8] = new TextButton("Goodricke", menuButtonStyle);
        buttons[9] = new TextButton("Constantine", menuButtonStyle);
        
        buttons[10] = new TextButton("Start Game", menuButtonStyle);
        buttons[10].addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                game.setScreen(new GameScreen(game));
            }
        });


        
        for (int i = 0; i < buttons.length; i++) {																	//renders the buttons
            drawer.addTableRow(table, buttons[i]);
        }

        
        stage.addActor(table);																						//FINALISE TABLE

    }

    /**
     * Renders all visual elements (set up in the [show()] subroutine and all of its subsiduaries) to the window
     * This is called to prepare each and every frame that the screen deploys
     *
     * @param delta
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1); 																			 //First instruction sets background colour
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin(); 																								//Run through the rendering pipeline to draw the menu's background image to the screen

        background.draw(batch);
        batch.end();
        
        stage.act(delta);																							//Draw the stage onto the screen
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    /**
     * Disposes of all visual data used to construct previous frames
     * This is called after each frame is rendered, and remains necessary to prevent memory leaks
     */
    @Override
    public void dispose() {
        menuFont.dispose();
        stage.dispose();
    }
}
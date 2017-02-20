package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.Random;

/**
 * @author Gandhi-Inc.
 * @version Assessment 3
 *          An executable version of the game can be found at: http://gandhi-inc.me/downloads/assessment3.jar
 *          Our website is: www.gandhi-inc.me
 * @since Assessment 3
 */
public class Roboticon {
    /**
     * Unique numerical identifier of the roboticon.
     */
    private Integer RoboticonID;

    /**
     * Variable holding what tile the roboticon is stored on.
     */
    private Tile CurrentTile;

    /**
     * Integer variable determining the maximum level of roboticons allowed in the game.
     */
    private Integer MaxLevel = 10;

    /**
     * Holds the current ore-mining level of the Roboticon
     */
    private int oreLevel = 1;

    /**
     * Holds the current energy-generating level of the Roboticon
     */
    private int energyLevel = 1;

    /**
     * Holds the current food-growing level of the Roboticon
     */
    private int foodLevel = 1;

    /**
     * Upgrade array, holds the possible levels of upgrade for the current robot. Stored as [Ore, Energy, Food]
     */
    private int Upgrades[] = {1, 1, 1};

    /**
     * The image object providing the roboticon's visual representation
     */
    private Image icon;

    /**
     * The texture encoding the roboticon's visual representation
     */
    private Texture iconTexture;

    /**
     * Constructor of the class
     *
     * @param ID     An integer uniquely defining the roboticon, starting at 0
     * @param Player A Player object to own the roboticon
     * @param Tile   A Tile object the roboticon is positioned on and therefore belongs to
     */
    public Roboticon(int ID, Player Player, Tile Tile) {
        RoboticonID = ID;
        this.CurrentTile = Tile;
        this.iconTexture = new Texture("image/Roboticon.png");
        this.icon = new Image(iconTexture);

        this.CurrentTile.assignRoboticon(this);
    }

    /**
     * Function to get the current roboticon Level
     * @return Integer array of the resource
     */
    public int[] getLevel() {
        int[] levels = new int[3];
        levels[0] = oreLevel;
        levels[1] = energyLevel;
        levels[2] = foodLevel;
        return levels;
    }

    public int getRoboticonID(){
        return RoboticonID;
    }

    /**
     * Method to upgrade a single level of the roboticon.
     * <p>
     * The parameter 'Resource' specifies 'Ore', 'Energy' or 'Food' to be upgraded one level.
     * </p>
     *
     * 0: ORE
     * 1: ENERGY
     * 2: FOOD
     * @param resource The resource that is going to be upgraded for this roboticon
     */
    public void upgrade(int resource) {
        switch (resource) {
            case (0):
                oreLevel += 1;
                break;
            case (1):
                energyLevel += 1;
                break;
            case (2):
                foodLevel += 1;
                break;
        }
    }

    /**
     * A method to return an array of all possible upgrades available to the roboticon at its current state
     *
     * @return Upgrades Returns an Integer Array in the form [Ore, Energy, Food]
     */
    public int[] possibleUpgrades() {
        if (oreLevel <= MaxLevel) {
            this.Upgrades[0] = oreLevel += 1;
        }
        if (energyLevel <= MaxLevel) {
            this.Upgrades[1] = energyLevel += 1;
        }
        if (foodLevel <= MaxLevel) {
            this.Upgrades[2] = foodLevel += 1;
        }

        return this.Upgrades;
    }

    /**
     * A method to return the production modifier offered by the roboticon.
     * <p>
     * Contains inherent randomness, not just a 1:1 ratio of level to return each phase of production. The modifier is used outside of
     * this class to multiply the inherent resources located on that tile.
     * </p>
     *
     * @return Modifiers Array to return the modifier for resource production, stored [Ore, Energy, Food]
     */
    public Integer[] productionModifier() {
        Integer[] Modifiers = {1, 1, 1};
        Integer Max = 5;
        Integer Min = 0;
        Random rand = new Random();

        int n = rand.nextInt(Max) + Min;
        Modifiers[0] = oreLevel * n;

        n = rand.nextInt(Max) + Min;
        Modifiers[1] = energyLevel * n;

        n = rand.nextInt(Max) + Min;
        Modifiers[2] = foodLevel * n;

        return Modifiers;
    }

    public void setProductionModifier(int ore, int energy, int food)
    {
    	oreLevel = ore;
    	energyLevel = energy;
    	foodLevel = food;

    }


    /**
     * Returns an Image object with the texture of the roboticon's icon mapped to it
     *
     * @return Image Icon representing the roboticon
     */
    public Image getIcon() {
        return this.icon;
    }

    /**
     * Returns the texture file encoding the roboticon's icon
     *
     * @return Texture The texture encoding the roboticon's icon
     */
    public Texture getIconTexture() {
        return iconTexture;
    }

    /**
     * Provides the roboticon's current ore-mining upgrade cost
     * @return Integer The roboticon's ore-mining upgrade cost
     */
    public int getOreUpgradeCost() {
        return (oreLevel * 6);
    }

    /**
     * Provides the roboticon's current food-growing upgrade cost
     * @return Integer The roboticon's food-growing upgrade cost
     */
    public int getFoodUpgradeCost() {
        return (foodLevel * 6);
    }

    /**
     * Provides the roboticon's current energy-generating upgrade cost
     * @return Integer The roboticon's energy-generating upgrade cost
     */
    public int getEnergyUpgradeCost() {
        return (energyLevel * 6);
    }

    /**
     * Provides the maximum level to which this roboticon can be upgraded to (across all three individual upgrade types)
     * @return Integer The roboticon's consistent level cap
     */
    public int getMaxLevel() {
        return MaxLevel;
    }
}

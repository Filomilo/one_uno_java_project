package org.SharedPack;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * a class that holds data about uno card
 */
public class UnoCard  implements Serializable  {
    /**
     * enum typpes that holds all possible colors of card in game
     */
    public enum UNO_COLOR{
        GREEN,
        RED,
        YELLOW,
        BLUE,
        BLACK
    }

    /**
     * enum types that holds all types of uno cards in game
     */
    public enum UNO_TYPE{
      REGULAR,
      PLUS2,
        PLUS4,
        COLOR,
        REVERSE,
        BLOCK,
    }


    /**
     * a constructor that setups card data from each varaible seperate
     * @param type
     * @param color
     * @param numb
     */
    public UnoCard(UNO_TYPE type, UNO_COLOR color, int numb) {
        this.type = type;
        this.color = color;
        this.numb = numb;
    }

    /**
     * a constructor that set ups card data from the data base query result
     * @param resultSet
     * @throws SQLException
     */
    public UnoCard(ResultSet resultSet) throws SQLException {

        switch (resultSet.getString("COLOR")) {
            case "GREEN": this.color=UNO_COLOR.GREEN ; break;
            case "RED": this.color=UNO_COLOR.RED ; break;
            case "YELLOW": this.color=UNO_COLOR.YELLOW ; break;
            case "BLUE": this.color=UNO_COLOR.BLUE ; break;
            case "BLACK": this.color=UNO_COLOR.BLACK ; break;
        }

        switch (resultSet.getString("TYPE")) {
            case "BLOCK": this.type=UNO_TYPE.BLOCK ; break;
            case "PLUS2": this.type=UNO_TYPE.PLUS2 ; break;
            case "PLUS4": this.type=UNO_TYPE.PLUS4 ; break;
            case "REVERSE": this.type=UNO_TYPE.REVERSE ; break;
            case "COLOR": this.type=UNO_TYPE.COLOR ; break;
            case "REGULAR": this.type=UNO_TYPE.REGULAR ; break;
        }
        if(this.type!= UNO_TYPE.REGULAR)
        {
            this.numb=-1;
        }
        else
        {
            this.numb=resultSet.getInt("NUMB");
        }
    }

    /**
     * overwrittern method to print data baout card
     * @return
     */
    @Override
    public String toString() {
        if(type==UNO_TYPE.REGULAR)
        return type +"," + color + "," + numb ;
        else
            return type +"," + color  ;
    }

    /**
     * varaible that sores type of card
     */
    private UNO_TYPE type;
    /**
     * variable taht stores color of card
     */
    private UNO_COLOR color;
    /**
     * cariables taht stores number on card
     */
    private final int numb;

    /**
     * setter to set color of card
     * @param color
     */
    public void setColor(UNO_COLOR color) {
        this.color = color;
    }


    /**
     * a getter to get type of card
     * @return
     */
    public UNO_TYPE getType() {
        return type;
    }

    /**
     * gett to get color of card
     * @return
     */
    public UNO_COLOR getColor() {
        return color;
    }

    /**
     * a gett to get number on card
     * @return
     */
    public int getNumb() {
        return numb;
    }





}

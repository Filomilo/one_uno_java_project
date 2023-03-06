package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.PrimitiveIterator;

public class UnoCard {
    static enum UNO_COLOR{
        GREEN,
        RED,
        YELLOW,
        BLUE,
        BLACK
    };
    static enum UNO_TYPE{
      REGULAR,
      PLUS2,
        PLUS4,
        COLOR,
        REVERSE,
        BLOCK,
    };


    public UnoCard(UNO_TYPE type, UNO_COLOR color, int numb) {
        this.type = type;
        this.color = color;
        this.numb = numb;
    }

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


    @Override
    public String toString() {
        if(type==UNO_TYPE.REGULAR)
        return type +"," + color + "," + numb ;
        else
            return type +"," + color  ;
    }

    private UNO_TYPE type;
    private UNO_COLOR color;
    private int numb;

    public void setType(UNO_TYPE type) {
        this.type = type;
    }

    public void setColor(UNO_COLOR color) {
        this.color = color;
    }

    public void setNumb(int numb) {
        this.numb = numb;
    }

    public UNO_TYPE getType() {
        return type;
    }

    public UNO_COLOR getColor() {
        return color;
    }

    public int getNumb() {
        return numb;
    }





}

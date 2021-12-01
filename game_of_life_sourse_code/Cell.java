import javax.swing.*;
import java.awt.*;

public class Cell extends JButton {

    // these two states allow us to find the cell from the multidimensional array primary_cell_set
    public int row;     // which row the cell resides in when put on the board
    public int column;      // which column the cell resides in when put on the board
    public boolean alive = false;   // whether the cell is alive or not

    // a constructor
    public Cell(int row, int column){
        /*The id should be something like a3, h8, ... based on the row and column the cell is at*/
        this.row = row;
        this.column = column;
    }

    // an overloaded constructor
    public Cell(int row, int column, boolean alive){
        this(row, column);
        this.alive = alive;
    }

    // a cell can turn on
    public void turnOn(){
        this.alive = true;
        this.setBackground(Color.GREEN);
    }

    // a cell can turn off
    public void turnOff(){
        alive = false;
        this.setBackground(Color.DARK_GRAY);
    }

    // toggle functionality
    public boolean toggle(){
        if (this.alive){
            this.turnOff();
        } else{
            turnOn();
        }
        return alive;
    }
}
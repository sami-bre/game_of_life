/*
* CONWAY'S GAME OF LIFE
* Programmed by Samuel Birhanu
* Date created - 10/15/2021
* Last update - 11/18/2021
* */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Game {

    // giving the total dimension of the game
    public int number_of_rows;
    public int number_of_columns;


    public static void main(String args[]){
        Game game = new Game(60, 100);
        game.run();
    }
    

    // Two-dimensional array containing all the cells
    Cell[][] primary_cell_set;

    // Two-dimensional array to hold boolean values as pending states of the cells on the next generation.
    // this is how everything works: at each gen, we loop through each cell and check if the cell will be alive or
    // dead in the next gen. then we store that boolean data on the corresponding position in the next_gen _state array
    // once we're done looping over the cells, we start looping over the next_gen_state array and turn each cell on or off
    // depending on the corresponding boolean value in the next_gen-state array. That's how the next generation is produces.
    // Then we simply repeat this process and we have the game of life!
    Boolean[][] next_gen_state;

    NextActionListener next_listener = new NextActionListener();
    JButton next_button;
    JButton clear_button;

    // a boolean to tell if the game loop is running or not.
    // the game loop runs as far as this boolean is true. the 'Stop' button sets this variable false
    // to make the game loop end.
    // if game_loop_running is false, the game loop has finished or hasn't started yet.
    boolean game_loop_running = false;

    // constructor of the game class
    public Game(int rows, int columns){
        number_of_rows = rows;
        number_of_columns = columns;
        primary_cell_set = new Cell[number_of_rows][number_of_columns];
        next_gen_state = new Boolean[number_of_rows][number_of_columns];
    }


    public void run(){

        JFrame frame = new JFrame("Conways game of life"); // the main window
        JPanel board_panel = new JPanel();   // a JPanel to contain all the cells
        JPanel control_panel = new JPanel();    // a JPanel to contain controlling buttons (clear, next, play)
        control_panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Create and set up the 'Clear' button. the button used to clear the cells board.
        clear_button = new JButton("Clear");
        clear_button.addActionListener(new ClearActionListener());
        control_panel.add(clear_button);

        // Create and set up the 'Next' button. the button used to generate and display the next generation
        next_button = new JButton("Next");
        next_button.addActionListener(next_listener);
        control_panel.add(next_button);

        // Create and set up the 'Play' button. the button used to generate and display successive generations
        // of cells continuously until the 'Stop' button fires.
        JButton play_button = new JButton("Play");
        play_button.addActionListener(new PlayActionListener());
        control_panel.add(play_button);
//
        // Create and set up the 'Stop' button. the button used to stop a running loop of successive generations
        JButton stop_button = new JButton("Stop");
        stop_button.addActionListener(new StopActionListener());
        control_panel.add(stop_button);


        // we want a grid layout manager for out board_panel
        board_panel.setLayout(new GridLayout(number_of_rows,number_of_columns));



        // Create an ActionListener to register with all the cells.
        // (the cells fire ActionEvents since they extend JButton).
        ActionListener cell_click_listener = new CellClickListener();

        // Putting cells in all slots of the primary_cell_set array and false booleans in all slots of the next_gen_state array.
        // .... for the purpose of initialization.
        Cell temp_button;
        for(int i=0; i<number_of_rows; i++){
            for(int j=0; j<number_of_columns; j++){
                temp_button = new Cell(i, j, false);
                board_panel.add(temp_button);
                temp_button.setBackground(Color.DARK_GRAY);
                temp_button.addActionListener(cell_click_listener);
                primary_cell_set[i][j] = temp_button;
                next_gen_state[i][j] = false;
            }
        }

        // finishing touch on the JFrame and its components
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(board_panel, BorderLayout.CENTER);
        frame.getContentPane().add(control_panel, BorderLayout.SOUTH);


        frame.setSize(1200, 720);
        frame.setResizable(false);
        frame.setVisible(true);
    }


    // ActionListener to Listen to all cell clicks
    class CellClickListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            Cell cell = (Cell) e.getSource();
            cell.toggle();
        }
    }

    // ActionListener to listed to the clear button
    class ClearActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            // we're looping through the primary_cell_set and the next_gen_state arrays (both at the same time)
            // ... and turning each cell in primary_cell_set off, setting each slot in next_gen_state false.
            for(int i=0; i<number_of_rows; i++){
                for(int j=0; j<number_of_columns; j++){
                    // turning each cell off
                    primary_cell_set[i][j].turnOff();
                    // setting each slot in next_gen_state off
                    next_gen_state[i][j] = false;
                }
            }
        }
    }

    // ActionListener to listen to the 'Next' button
    class NextActionListener implements ActionListener{
        ArrayList<Cell> neighbors;
        Cell c;
        int counter = 0;
        boolean slot;


        @Override
        public void actionPerformed(ActionEvent e){
            generate_next_generation();
        }

        void generate_next_generation(){

            // prepare the next_gen_state array. this method resets the array and reassigns each slot in it
            // to represent the state of each cell in the next generation.
            prepare_next_gen_state();

            // reset the board temporarily by turning each cell off. this reset is a transition between two generations
            for(Cell[] cell_set : primary_cell_set){
                for(Cell cell : cell_set){
                    cell.turnOff();
                }
            }

            // loop through the primary_cell_set and the next_gen_state at the same time and turn cells ON
            // whose corresponding slots in next_gen_state are true. This is where we configure the cells
            // according to the next_gen_state array.
            for(int i=0; i<number_of_rows; i++){
                for(int j=0; j<number_of_columns; j++){
                    slot = next_gen_state[i][j];
                    if(slot){
                        primary_cell_set[i][j].turnOn();
                    }
                }
            }
        }

        void prepare_next_gen_state(){
            // this method prepares the next_gen_state array in such a way that it shows which sells
            // will be alive and which will be dead in the next generation. This is important because
            // we use this prepared next_gen_state array to configure each cell in the next generation.


            // loop through next_gen_state to set everything false. we're resetting the array here.
            for(int i=0; i<number_of_rows; i++){
                for(int j=0; j<number_of_columns; j++){
                    next_gen_state[i][j] = false;
                }
            }

            for(int i=0; i<number_of_rows; i++){
                for(int j=0; j<number_of_columns; j++){
                    // we can get the specific cell now by using i and j
                    // use info about the location of this cell to find all its neighbors
                    // then use the rules of the game of life to determine if this cell will
                    // be alive or dead in the next generation. then retain that info (live or dead)
                    // in the corresponding slot in the next_gen_state array.

                    // get the cell we're working with
                    c = primary_cell_set[i][j];
                    // get its neighbors
                    neighbors = getNeighbors(c);
                    // count how many of the neighbors are alive
                    for (Cell neighbor : neighbors){
                        if(neighbor.alive){
                            counter++;
                        }
                    }

                    // APPLYING THE RULES OF THE GAME
                    // now if two of the neighbors are alive and the cell is alive itself in this generation,
                    // it will be alive in the next generation too (according to the rules of the game of life)
                    if(counter==2 && c.alive){
                        // so set the corresponding slot in next_gen_state true
                        next_gen_state[i][j] = true;
                        // and if the cell has exactly 3 neighbors, it will be alive in the next generation
                        // regardless of its state on the current generation. (again, the rules of the game dictate)
                    } else if(counter==3){
                        // so set the corresponding slot in next_gen_state true
                        next_gen_state[i][j] = true;
                    }
                    // reset the counter
                    counter = 0;
                }
            }
        }
    }

    // ActionListener to listen to the 'Play' button
    class PlayActionListener implements ActionListener, Runnable{
        @Override
        public void actionPerformed(ActionEvent e){
            // we need a separate thread for the game loop because the game loop and the GUI (specifically,
            // the 'Stop' button) have to work concurrently.
            // this class is both an ActionListener and a Runnable.
            Thread t = new Thread(this);
            t.start();
        }

        @Override
        public void run() {
            // we don't want the next_button and _clear button t be pressed while the game is playing in
            // loop. so we disable them as the game loop starts.
            next_button.setEnabled(false);
            clear_button.setEnabled(false);

            // set game_loop_running to true, so the game loop gets running
            game_loop_running = true;

            // this is the loop that runs consecutive generations continuously.
            // it starts with the 'Play' button and ends with the 'Stop' button.
            while(game_loop_running){
                next_listener.generate_next_generation();
                try {
                    sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            // We enable the 'Next' and 'Clear' buttons as soon as the game loop ends
            next_button.setEnabled(true);
            clear_button.setEnabled(true);
        }
    }

    // ActionListener to listen to the 'Stop' button.
    class StopActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            game_loop_running = false;
        }
    }

    // method to return neighbors of a call
    ArrayList<Cell> getNeighbors(Cell cell){
        ArrayList<Cell> neighbors = new ArrayList<>();
        int row = cell.row;
        int col = cell.column;

        // most of the cells have 8 neighbors each. but the ones on the edges are exceptions. so
        // we first check if the cell is at an edge or corner and find its neighbors accordingly

        if(row == 0 && col == 0){           // top-left corner
            neighbors.add(primary_cell_set[row][col+1]);
            neighbors.add(primary_cell_set[row+1][col+1]);
            neighbors.add(primary_cell_set[row+1][col]);
        } else if(row == 0 && col == number_of_columns-1){   // top-right corner
            //  position specific code
            neighbors.add(primary_cell_set[row][col-1]);
            neighbors.add(primary_cell_set[row+1][col-1]);
            neighbors.add(primary_cell_set[row+1][col]);
        } else if(row == number_of_rows-1 && col == 0){   // bottom-left corner
            //  position specific code
            neighbors.add(primary_cell_set[row-1][col]);
            neighbors.add(primary_cell_set[row-1][col+1]);
            neighbors.add(primary_cell_set[row][col+1]);
        } else if(row == number_of_rows-1 && col == number_of_columns-1){  // bottom-right corner
            //  position specific code
            neighbors.add(primary_cell_set[row-1][col]);
            neighbors.add(primary_cell_set[row-1][col-1]);
            neighbors.add(primary_cell_set[row][col-1]);

        } else if(row == 0){                // the entire top row except the two corners
            // position specific code
            // these cells each have 5 neighbors
            neighbors.add(primary_cell_set[row][col-1]);
            neighbors.add(primary_cell_set[row+1][col-1]);
            neighbors.add(primary_cell_set[row+1][col]);
            neighbors.add(primary_cell_set[row+1][col+1]);
            neighbors.add(primary_cell_set[row][col+1]);

        } else if(row == number_of_rows-1){               // the entire bottom row except the two corners
            // position specific code
            // these cells each have 5 neighbors
            neighbors.add(primary_cell_set[row][col-1]);
            neighbors.add(primary_cell_set[row-1][col-1]);
            neighbors.add(primary_cell_set[row-1][col]);
            neighbors.add(primary_cell_set[row-1][col+1]);
            neighbors.add(primary_cell_set[row][col+1]);

        } else if(col == 0){                // the entire left column except the two corners
            // position specific code
            // these cells each have 5 neighbors
            neighbors.add(primary_cell_set[row-1][col]);
            neighbors.add(primary_cell_set[row-1][col+1]);
            neighbors.add(primary_cell_set[row][col+1]);
            neighbors.add(primary_cell_set[row+1][col+1]);
            neighbors.add(primary_cell_set[row+1][col]);

        } else if(col == number_of_columns-1){               // the entire right column except the two corners
            // position specific code
            // these cells each have 5 neighbors
            neighbors.add(primary_cell_set[row-1][col]);
            neighbors.add(primary_cell_set[row-1][col-1]);
            neighbors.add(primary_cell_set[row][col-1]);
            neighbors.add(primary_cell_set[row+1][col-1]);
            neighbors.add(primary_cell_set[row+1][col]);

        } else {                            // the standard 8-neighbored cell
            // a general code for all normal 8-neighbored cells
            neighbors.add(primary_cell_set[row-1][col-1]);
            neighbors.add(primary_cell_set[row-1][col]);
            neighbors.add(primary_cell_set[row-1][col+1]);
            neighbors.add(primary_cell_set[row][col-1]);
            neighbors.add(primary_cell_set[row+1][col-1]);
            neighbors.add(primary_cell_set[row+1][col]);
            neighbors.add(primary_cell_set[row+1][col+1]);
            neighbors.add(primary_cell_set[row][col+1]);
        }
        return neighbors;
    }
}

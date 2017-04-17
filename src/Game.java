import javafx.geometry.Pos;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by DDshka on 08.04.2017.
 * may be we need packages? Does anyone here can recode it with packages?
 */
public class Game
{
    //game core
    private static Cell Cells[][];

    //window swttings
    private static int Width = 0;
    private static int Height = 0;
    private static String Title = "";

    //game vars
    private static boolean GameIsRunning = true;
    private static int Mines = 3;
    private static int OpenedCells = 0;
    private static int FreeCells = 0;

    //game constants (should be moved to constants in nearest future)
    private final static int MOUSE_LEFT_BUTTON = 0;
    private final static int MOUSE_RIGHT_BUTTON = 1;

    public static void setResolution(int width, int height)
    {
        Width = width;
        Height = height;
    }

    public static void setTitle(String title)
    {
        Title = title;
    }

    public static void start(int minesInColumn, int minesInRow)
    {
        try
        {
            Display.setDisplayMode(new DisplayMode(Width, Height));
            Display.setTitle(Title);
            //Display.setResizable(true);
            Display.create();
        }
        catch (LWJGLException e)
        {

        }

        //Creating matrix in window.
        // Now we are in Cartesian coordinate system (Decart)
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity(); //Resers any previous projection matrices
        glOrtho(0, Width, Height, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_TEXTURE_2D);
        //TODO: HANDLE ERROR IF THERE ANY TEXTURES
        Textures.bindTextures(); //IMPORTANT!11 ERROR NOT HANDLED!11

        //RENDERING
        Field field = new Field(minesInColumn, minesInRow, 64, Mines);
        while(!Display.isCloseRequested() && GameIsRunning)
        {
            glClear(GL_COLOR_BUFFER_BIT); //To clear a 2D drawing canvas

            //NYA();
            field.Render();
            Control.checkMouseClicks();

            if (Mines == 0 && OpenedCells == FreeCells)
            {
                GameIsRunning = false;
                System.out.println("YOU ARE WINNER");
            }

            Display.update();
            Display.sync(60);
        }

        Textures.releaseTextures();
        Display.destroy();
        System.exit(0);
    }

    private final static class Field
    {
        public Field(int minesInRow, int minesInColumn, int cellSize, int minesCount)
        {
            int row = 0;
            int cell = 0;
            int minesTotal = 0;
            Cells = new Cell[minesInRow][minesInColumn];
            for (int i = 0; i < minesInRow; i++)
            {
                for (int j = 0; j < minesInColumn; j++)
                {
                    Cells[i][j] = new Cell(cell, row, cellSize);
                    cell += cellSize;
                }
                cell = 0;
                row += cellSize;
            }
            generateMines(minesCount);
            FreeCells = minesInRow * minesInColumn - Mines;
        }

        public void Render()
        {
            for (Cell[] row : Cells)
                for (Cell cell : row)
                    cell.draw();
        }

        private void generateMines(int minesCount)
        {
            System.out.println("GENERATING " + minesCount + " BOMBS");
            int minesTotal = 0;
            while (minesTotal < minesCount)
                for (Cell[] row : Cells)
                    for (Cell cell : row)
                    {
                        Random rnd = new Random();
                        int rand = rnd.nextInt(100);
                        //Mine spawns with 10% chance; IMPORTANT!11
                        if (rand <= 10 && !cell.isMine() && minesTotal < minesCount)
                        {
                            cell.setMine();
                            minesTotal++;
                            System.out.println(minesTotal + " BOMB PLANTED");
                        }
                    }

            for (int i = 0; i < Cells.length; i++)
                for (int j = 0; j < Cells[i].length; j++)
                {
                    List<Position> adjCells = Control.getAdjoiningCells(new Position(i, j));
                    byte count = 0;
                    for (Position adjPos : adjCells)
                    {
                        Cell adjCell = Cells[adjPos.X][adjPos.Y];
                        if (adjCell.isMine())
                            count++;
                    }
                    Cells[i][j].setMinesAroundCount(count);
                }
        }
    }

    private final static class Control
    {
        //Mouse clicks handler
        public static void checkMouseClicks()
        {
            while(Mouse.next())
                if (Mouse.getEventButtonState())
                    changeCellState();
        }

        private static Position getCell(int x, int y)
        {
            for (int i = 0; i < Cells.length; i++)
                for (int j = 0; j < Cells[i].length; j++)
                    if (Cells[i][j].isInCell(x, y))
                        return new Position(i, j);

            return null;
        }

        private static List<Position> getAdjoiningCells(Position pos)
        {
            List<Position> cellList = new ArrayList<>();
            Position[] checkedPositions =
            {
                new Position(pos.X + 1, pos.Y),
                new Position(pos.X + 1, pos.Y + 1),
                new Position(pos.X + 1, pos.Y - 1),
                new Position(pos.X - 1, pos.Y),
                new Position(pos.X - 1, pos.Y + 1),
                new Position(pos.X - 1, pos.Y - 1),
                new Position(pos.X, pos.Y + 1),
                new Position(pos.X, pos.Y - 1),
            };

            //BEST COSTYL EVER - I WILL BE BURNED IN HELLS FIRE IN SOME OBVIOUS WAY
            for (int i = 0; i < checkedPositions.length; i++)
            {
                try
                {
                    int x = checkedPositions[i].X;
                    int y = checkedPositions[i].Y;
                    Cell cell = Cells[x][y];
                    cellList.add(checkedPositions[i]);
                }
                catch (IndexOutOfBoundsException e) {
                    //System.out.println("LOL");
                }
            }

            return cellList;
        }

        private static void checkSingleClick(Position pos)
        {
            Cell cell = Cells[pos.X][pos.Y];
            if (Mouse.getEventButton() == MOUSE_LEFT_BUTTON)
            {
                openCell(pos);
            }
            else if (Mouse.getEventButton() == MOUSE_RIGHT_BUTTON)
                switch (cell.getState())
                {
                    case Closed:
                        cell.setState(State.Marked);
                        if (cell.isMine()) Mines--;
                        break;
                    case Marked:
                        cell.setState(State.Question);
                        if (cell.isMine()) Mines++;
                        break;
                    case Question:
                        cell.setState(State.Closed);
                        break;
                }
        }

        private static void checkRightAndLeft(Position pos)
        {
            //BASE FOR RIGHT AND LEFT MOUSE BUTTON CLICKS
            Cell cell = Cells[pos.X][pos.Y];
            if(Mouse.isButtonDown(MOUSE_RIGHT_BUTTON) && Mouse.getEventButton() == MOUSE_LEFT_BUTTON)
                if (cell.getState() == State.Opened)
                {
                    List<Position> adjusting = getAdjoiningCells(pos);
                    int countMarked = 0;
                    for (Position adjPosition : adjusting)
                    {
                        Cell adjCell = Cells[adjPosition.X][adjPosition.Y];
                        if (adjCell.getState() == State.Marked)
                            countMarked++;
                    }

                    if (countMarked != cell.getMinesAroundCount()) return;

                    openBlock(pos);
                }
        }

        private static void openBlock(Position centerPos)
        {
            List<Position> adjusting = getAdjoiningCells(centerPos);
            for (Position adjPosition : adjusting)
            {
                Cell adjCell = Cells[adjPosition.X][adjPosition.Y];
                if (adjCell.getState() != State.Marked)
                    openCell(adjPosition);
            }
        }

        private static void openCell(Position pos)
        {
            Cell cell = Cells[pos.X][pos.Y];
            if (cell.getState() == State.Opened) return;

            cell.setState(State.Opened);
            OpenedCells++;
            System.out.println(pos.X + " " + pos.Y + " Cell opened");
            if (cell.isMine())
            {
                GameIsRunning = false;
                System.out.println("MINE PRESSED");
            }
            else if (cell.getMinesAroundCount() == 0)
            {
                openBlock(pos);
                List<Position> adjCells = getAdjoiningCells(pos);
                for (Position adjPos : adjCells)
                {
                    Cell adjCell = Cells[adjPos.X][adjPos.Y];
                    if (adjCell.getMinesAroundCount() == 0)
                        openBlock(adjPos);
                }
            }
        }

        private static void changeCellState()
        {
            int x = Mouse.getX();
            int y = Mouse.getY();

            Position pos = getCell(x, Height - y); //WTF WITH Y? Features of LWJGL

            checkSingleClick(pos);
            checkRightAndLeft(pos);
        }
    }

    private final static class Position
    {
        public int X;
        public int Y;
        public Position(int x, int y)
        {
            X = x;
            Y = y;
        }
    }
}

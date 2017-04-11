import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by DDshka on 08.04.2017.
 * may be we need packages? Does anyone here can recode it with packages?
 */
public class Game
{
    private static  Cell Cells[][];
    private static int Width = 0;
    private static int Height = 0;
    private static String Title = "";
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
        Field field = new Field(minesInColumn, minesInRow, 64);
        while(!Display.isCloseRequested())
        {
            glClear(GL_COLOR_BUFFER_BIT); //To clear a 2D drawing canvas

            //NYA();
            field.Render();
            Control.checkMouseClicks();

            Display.update();
            Display.sync(60);
        }

        Textures.releaseTextures();
        Display.destroy();
        System.exit(0);
    }

    private final static class Field
    {
        public Field(int minesInRow, int minesInColumn, int cellSize)
        {
            int row = 0;
            int cell = 0;
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
        }

        public void Render()
        {
            for (Cell[] row : Cells)
                for (Cell cell : row)
                    cell.draw();
        }
    }

    private final static class Control
    {
        //Mouse clicks handler
        //TODO: Right with left click changes state
        public static void checkMouseClicks()
        {
            while(Mouse.next())
                if (Mouse.getEventButtonState())
                    changeCellState();
        }

        private static Position getCell(int x, int y)
        {
            /* IN SHALOM, I mean IN FOREACH
            //Foreach instruction in Java are made in that way.
            //Very weird, but who cares?
            for (Cell[] row : Cells)
                for (Cell cell : row)
                    if (cell.isInCell(x, y))
                        return cell;
            */

            for (int i = 0; i < Cells.length; i++)
                for (int j = 0; j < Cells[i].length; j++)
                    if (Cells[i][j].isInCell(x, y))
                        return new Position(i, j);

            return null;
        }


        private static List getAdjoiningCells(Position pos)
        {
            List<Cell> cellList = new ArrayList<Cell>();
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
                    cellList.add(Cells[x][y]);
                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println("LOL");
                }
            }

            return cellList;
        }

        private static void changeCellState()
        {
            int x = Mouse.getX();
            int y = Mouse.getY();

            Position pos = getCell(x, Height - y); //WTF WITH Y? Features of LWJGL
            Cell cell = Cells[pos.X][pos.Y];

            List<Cell> adjusting = getAdjoiningCells(pos);

            //BASE FOR RIGHT AND LEFT MOUSE BUTTON CLICKS
            if(Mouse.isButtonDown(MOUSE_RIGHT_BUTTON))
                if (cell.getState() == State.Opened)
                {
                    System.out.println("Right button clicked");
                    if (Mouse.getEventButton() == MOUSE_LEFT_BUTTON)
                    {
                        System.out.println("Left button clicked after right");
                        for (Cell _cell : adjusting)
                            _cell.setState(State.Marked);
                    }
                }

            if (Mouse.getEventButton() == MOUSE_LEFT_BUTTON)
                cell.setState(State.Opened);
            else if (Mouse.getEventButton() == MOUSE_RIGHT_BUTTON)
                switch (cell.getState())
                {
                    case Closed:
                        cell.setState(State.Marked);
                        break;
                    case Marked:
                        cell.setState(State.Question);
                        break;
                    case Question:
                        cell.setState(State.Closed);
                        break;
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
}

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

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

    private static class Field
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

    private static class Control
    {
        //Mouse clicks handler
        //TODO: Right with left click changes state
        public static void checkMouseClicks()
        {
            while(Mouse.next())
                if (Mouse.getEventButtonState())
                    changeCellState();
        }

        private static Cell getCell(int x, int y)
        {
            //Foreach instruction in Java are made in that way.
            //Very weird, but who cares?
            for (Cell[] row : Cells)
                for (Cell cell : row)
                    if (cell.isInCell(x, y))
                        return cell;

            return null;
        }

        private static void changeCellState()
        {
            int x = Mouse.getX();
            int y = Mouse.getY();
            Cell cell = getCell(x, Height - y); //WTF WITH Y? Features of LWJGL

            if (cell.getState() == State.Opened)
                return;

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
    }
}

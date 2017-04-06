/**
 * Created by DDshka on 25.03.2017.
 */

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.LWJGLException;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.opengl.*;

import java.io.*;

public class Main
{
    public static int WIDTH = 640;
    public static int HEIGHT = 640;

    public static int MinesInRow = 10;
    public static int MinesInColumn = 10;
    public static int SIZE = WIDTH / 10;

    private final static int MOUSE_LEFT_BUTTON = 0;
    private final static int MOUSE_RIGHT_BUTTON = 1;

    public static Cell Cells[][];

    //Just test. Very very cute test
    public static void NYA()
    {
        glBegin(GL_TRIANGLES);

        glColor3f(0,1,0);
        glVertex2f(0, HEIGHT);

        glColor3f(1, 0,0);
        glVertex2f(WIDTH, HEIGHT);

        glColor3f(0,0,1);
        glVertex2f(0, 0);

        glColor3f(0,0,1);
        glVertex2f(0, 0);

        glColor3f(0,1,1);
        glVertex2f(WIDTH, 0);

        glColor3f(1,0,0);
        glVertex2f(WIDTH, HEIGHT);
        glEnd();
    }

    //Mouse clicks handler
    //TODO: Right with left click changes state
    public static void checkMouseClicks()
    {
        while(Mouse.next())
            if (Mouse.getEventButtonState())
                changeCellState();
    }

    private static void changeCellState()
    {
        int x = Mouse.getX();
        int y = Mouse.getY();
        Cell cell = getCell(x, HEIGHT - y); //WTF WITH Y? Features of LWJGL

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

    public static Cell getCell(int x, int y)
    {
        //Foreach instruction in Java are made in that way.
        //Very weird, but who cares?
        for (Cell[] row : Cells)
            for (Cell cell : row)
                if (cell.isInCell(x, y))
                    return cell;

        return null;
    }

    //Minefield creator
    //TODO:RECODE IN THE NEAREST FUTURE.
    public static void CreateField()
    {
        int row = 0;
        int cell = 0;
        Cells = new Cell[MinesInRow][MinesInColumn];
        for (int i = 0; i < MinesInRow; i++)
        {
            for (int j = 0; j < MinesInColumn; j++)
            {
                Cells[i][j] = new Cell(cell, row, SIZE);
                cell += SIZE;
            }
            cell = 0;
            row += SIZE;
        }
    }

    public static void RenderField()
    {
        for (Cell[] row : Cells)
            for (Cell cell : row)
                cell.draw();
    }

    public static void main(String[] args)
    {
       try
       {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle("OLOLOLOLOLOLOLOL");
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
        glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_TEXTURE_2D);
        //TODO: HANDLE IF THERE ANY TEXTURES
        Textures.bindTextures(); //IMPORTANT!11 ERROR NOT HANDLED!11

        //RENDERING
       CreateField();
       while(!Display.isCloseRequested())
       {
            glClear(GL_COLOR_BUFFER_BIT); //To clear a 2D drawing canvas

           //NYA();
           RenderField();
           checkMouseClicks();

           Display.update();
           Display.sync(60);
       }

       Display.destroy();
       System.exit(0);
    }
}


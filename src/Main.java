/**
 * Created by DDshka on 25.03.2017.
 */
import static org.lwjgl.opengl.GL11.*;

public class Main
{
    public static int WIDTH = 640;
    public static int HEIGHT = 640;

    public static int MinesInRow = 10;
    public static int MinesInColumn = 10;
    public static int SIZE = WIDTH / 10;


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

    public static void main(String[] args)
    {
        Game.setResolution(WIDTH, HEIGHT);
        Game.start(MinesInRow, MinesInColumn);
    }
}


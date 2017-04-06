import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import sun.font.TrueTypeFont;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by DDshka on 29.03.2017.
 */
public final class Cell
{
    private float X;
    private float Y;
    private float Size = 0;
    private boolean isDrawn = false;
    private boolean isMine = false;
    private State state = State.Closed;

    /**
     * @param x X-coordinate of upper-left corner
     * @param y Y-coordinate of upper-left corner
     * @param size Size of cell-rectangle
     */
    public Cell(float x, float y, float size)
    {
        X = x;
        Y = y;
        Size = size;
    }

    public void setMine()
    {
        isMine = true;
    }

    public boolean isMine()
    {
        return this.isMine;
    }

    //TODO: make render via array of vertexes
    public void draw()
    {
        if (state == State.Closed)
        {
            glBegin(GL_TRIANGLES);
                glVertex2f(X, Y);                         glTexCoord2f(0, 0);
                glVertex2f(X + Size, Y);               glTexCoord2f(1, 1);
                glVertex2f(X,  Y + Size);              glTexCoord2f(0, 1);

                glVertex2f(X + Size, Y + Size);     glTexCoord2f(1, 1);
                glVertex2f(X, Y + Size);               glTexCoord2f(0, 0);
                glVertex2f(X + Size, Y);               glTexCoord2f(1, 0);
            glEnd();
        }
        else if (state == State.Opened)
        {
            if (isMine)
            {

            }
            else
            {
                //Some tests with font render
                //TrueTypeFont TextRender;
                //Font font = new Font("Times new Roman", Font.BOLD, 24);
                //TextRender = new TrueTypeFont(""font, false);

                glBegin(GL_TRIANGLES);
                    glColor3f(1, 0, 0);     glVertex2f(X, Y);
                    glColor3f(0, 1, 0);     glVertex2f(X + Size, Y);
                    glColor3f(0, 0, 1);     glVertex2f(X,  Y + Size);

                    glColor3f(1, 0, 0);     glVertex2f(X + Size, Y + Size);
                    glColor3f(0, 0, 1);     glVertex2f(X, Y + Size);
                    glColor3f(0, 1, 0);     glVertex2f(X + Size, Y);
                glEnd();

            }
        }
        else if (state == State.Marked)
        {
            //glColor3f(1, 0, 0);

            Texture texture;
            try {
                texture = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/ADOLF.png")));
                // Replace PNG with your file extension
                glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            glVertex2f(X, Y);                         glTexCoord2f(0, 0);
            glVertex2f(X + Size, Y);               glTexCoord2f(1, 1);
            glVertex2f(X,  Y + Size);              glTexCoord2f(0, 1);

            glVertex2f(X + Size, Y + Size);     glTexCoord2f(1, 1);
            glVertex2f(X, Y + Size);               glTexCoord2f(0, 0);
            glVertex2f(X + Size, Y);               glTexCoord2f(1, 0);

            /*
            glBegin(GL_TRIANGLES);
                glVertex2f(X, Y);
                glVertex2f(X + Size, Y);
                glVertex2f(X,  Y + Size);

                glVertex2f(X + Size, Y + Size);
                glVertex2f(X, Y + Size);
                glVertex2f(X + Size, Y);
            glEnd();
            */
        }
        else if (state == State.Question)
        {
            glColor3f(0, 1, 0);
            glBegin(GL_TRIANGLES);
                glVertex2f(X, Y);
                glVertex2f(X + Size, Y);
                glVertex2f(X,  Y + Size);

                glVertex2f(X + Size, Y + Size);
                glVertex2f(X, Y + Size);
                glVertex2f(X + Size, Y);
            glEnd();
        }
        glColor3f(1,1,1); //IMPORTANT!!!11 DO NOT ERASE THIS CODE. On this shit depends background color. IDK why.
        isDrawn = true;
    }

    public void setState(State state)
    {
        this.state = state;
    }
    public State getState() { return this.state; }

    /**Checks if the given point is in the cell
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return  TRUE if point is in the cell or
 *              FALSE in all other cases
     */
    public boolean isInCell(int x, int y)
    {
        if (x <= X + Size &&
            y <= Y + Size &&
            x >= X &&
            y >= Y)
        {
            return true;
        }
        return false;
    }
}

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

    //TODO: make render via array of vertexes
    public void draw()
    {
        if (state == State.Closed)
        {
            drawTexture(Constants.CELL);
        }
        else if (state == State.Opened)
        {
            if (isMine)
            {

            }
            else
            {
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
            drawTexture(Constants.FLAG);
        }
        else if (state == State.Question)
        {
            drawTexture(Constants.QUESTION);
        }
        glColor3f(1,1,1); //IMPORTANT!!!11 DO NOT ERASE THIS CODE. On this shit depends background color. IDK why.
        isDrawn = true;
    }
    
    private void drawTexture(int textureId)
    {
        glBindTexture(GL_TEXTURE_2D, Textures.TexturesData[textureId]);
        glBegin(GL_TRIANGLES);
            glTexCoord2f(0, 0);     glVertex2f(X, Y);
            glTexCoord2f(1, 0);     glVertex2f(X + Size, Y);
            glTexCoord2f(0, 1);     glVertex2f(X,  Y + Size);
            glTexCoord2f(1, 1);     glVertex2f(X + Size, Y + Size);
            glTexCoord2f(0, 1);     glVertex2f(X, Y + Size);
            glTexCoord2f(1, 0);     glVertex2f(X + Size, Y);
        glEnd();
    }
}

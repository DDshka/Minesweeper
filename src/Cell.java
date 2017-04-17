import static org.lwjgl.opengl.GL11.*;

/**
 * Created by DDshka on 29.03.2017.
 */
public final class Cell
{
    private float X;
    private float Y;
    private float Size = 0;
    private byte minesAround = 0;
    private byte _state = 0;
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

    public int getMinesAroundCount() { return minesAround; }

    public void setMinesAroundCount(byte number) { minesAround = number; }

    public State getState() { return this.state; }

    public void setState(State state)
    {
        this.state = state;
        switch (this.state)
        {
            case Opened:
                if (!isMine())
                    _state = (minesAround > 0) ? minesAround : Constants.FREE;
                else
                    _state = Constants.MINE;
                break;
            case Closed:
                _state = Constants.CELL;
                break;
            case Question:
                _state = Constants.QUESTION;
                break;
            case Marked:
                _state = Constants.FLAG;
                break;
        }
    }

    /**Checks if the given point is in the cell
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return  TRUE if point is in the cell or
     *              FALSE in all other cases
     */
    public boolean isInCell(int x, int y)
    {
        return x <= X + Size
            && y <= Y + Size
            && x >= X
            && y >= Y;
    }

    //TODO: make render via array of vertexes
    public void draw()
    {
        drawTexture(_state);
        glColor3f(1,1,1); //IMPORTANT!!!11 DO NOT ERASE THIS CODE. On this shit depends background color. IDK why.
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

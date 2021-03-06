import java.io.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import org.lwjgl.BufferUtils;

/**
 * Created by DDshka on 06.04.2017.
 */
public class Textures
{
    public static final int RGB = 3;
    public static final int RGBA = 4;

    public static int[] TexturesData = null;

    public static void bindTextures()
    {
        if (TexturesData != null) return;
        TexturesData = new int[Constants.TEXTURES_PATHS.length];
        for (int i = 0; i < Constants.TEXTURES_PATHS.length; i++)
            TexturesData[i] = Textures.loadTexture(Constants.TEXTURES_PATHS[i], Textures.RGBA);
    }

    public static void releaseTextures()
    {
        for (int i = 0; i < Constants.TEXTURES_PATHS.length; i++)
            glDeleteTextures(TexturesData[i]);
    }

    private static BufferedImage loadImage(String location)
    {
        try {
            BufferedImage image = ImageIO.read(new File(location));
            return image;
        } catch (IOException e) {
            System.out.println("Could not load texture: " + location);
        }
        return null;
    }

    private static int loadTexture(String location, int BYTES_PER_PIXEL){
        BufferedImage image = loadImage(location);

        if (image == null) {
            return 0;
        }

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want, for example:

        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        //setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        //setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer); //GL_RGBA8 was GL_RGB8A

        return textureID;
    }
}

package de.brod.translate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Andreas_2 on 06.07.2015.
 */
public class Flags {

    public static void main(String[] args) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(Flags.class.getResourceAsStream("./Collection-national-flags.png"));
            extractImage(img, "", 4, 12);
            extractImage(img, "de", 5, 4);
            extractImage(img, "es", 12, 10);
            extractImage(img, "fr", 1, 4);
            extractImage(img, "it", 8, 5);
            extractImage(img, "ru", 8, 9);
            extractImage(img, "zh", 6, 2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractImage(BufferedImage img, String de, int px, int py) throws IOException {
        // 15,13
        String s = "./CardManiac/res/drawable";
        if (de.length() > 0)
            s += "-" + de;
        File rootFolder = new File(s);
        if (!rootFolder.exists()) {
            throw new FileNotFoundException(rootFolder.getAbsolutePath());
        }
        float w = img.getWidth() / 15f;
        float h = img.getHeight() / 13f;

        BufferedImage subimage = img.getSubimage((int) (px * w), (int) (py * h), (int) w, (int) h);

        int newWidth = 128;
        int newHeight = 128;
        BufferedImage resized = new BufferedImage(newWidth, newHeight, subimage.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(subimage, 0, 0, newWidth, newHeight, 0, 0, subimage.getWidth(), subimage.getHeight(), null);
        g.dispose();

        System.out.println(rootFolder.getAbsolutePath());

        ImageIO.write(resized, "png", new FileOutputStream(new File(rootFolder, "flag.png")));
        System.out.println("OK");

    }
}

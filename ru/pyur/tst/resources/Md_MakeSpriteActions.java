package ru.pyur.tst.resources;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;


public class Md_MakeSpriteActions extends Module {

    public Md_MakeSpriteActions(Session session) { initHtml(session); }



    @Override
    public void makeContent() {

        b("Генерация спрайта...");


        File directory = new File("./resources/action_icon/");

        File[] files = directory.listFiles();

        if (files == null) {
            //System.out.println("directory.files == null");
            b("Failed. directory not exists.");
            return;
        }


        ArrayList<File> files_2 = new ArrayList<>();

        for (File file : files) {
            //System.out.println(file.getName());

            //if (file.isDirectory()) {}

            if (file.isFile()) {
                if (file.getName().matches(".+\\.png")) {
                    files_2.add(file);
                }
            }
        }


        // -------- make sprite -------- //

        int sprite_width = 1024;
        int sprite_height = (files_2.size() / 64) * 16;  // 64 icons of 16x16 per 1024 line
        int[] sprite_matrix = new int[sprite_width * sprite_height];
        DataBufferInt sprite_buffer = new DataBufferInt(sprite_matrix, sprite_matrix.length);

        //BufferedImage sprite = new BufferedImage(1024, sprite_height, BufferedImage.TYPE_INT_ARGB);

        int[] bandMasks = {0xFF0000, 0xFF00, 0xFF, 0xFF000000}; // ARGB (yes, ARGB, as the masks are R, G, B, A always) order
        //SinglePixelPackedSampleModel sppsm = new SinglePixelPackedSampleModel(TYPE_BYTE, 1024, 16, bandMasks);

        //WritableRaster sprite = new WritableRaster(sppsm, new Point(0,0));
        //WritableRaster sprite = Raster.createWritableRaster(sppsm, new Point(0,0));
        WritableRaster sprite = Raster.createPackedRaster(sprite_buffer, sprite_width, sprite_height, sprite_width, bandMasks, null);

        int i = 0;
        for (File file : files_2) {
            BufferedImage img;
            try {
                img = ImageIO.read(file);
            } catch (Exception e) {
                e.printStackTrace();
                b("Failed. read image " + file.getAbsoluteFile());
                return;
            }

            if (img.getWidth() != 16 || img.getHeight() != 16 ) {
                b("Failed. wrong image dimensions " + img.getWidth() + " x " + img.getHeight() + ". " + file.getAbsoluteFile());
                return;
            }

            Raster raster = img.getData();
            //WritableRaster sprite = raster.createCompatibleWritableRaster(1024, 16);

            int y = (i / 64) * 16;
            int x = (i % 64) * 16;
            sprite.setRect(x, y, raster);

            i++;
        }

        ColorModel cm = ColorModel.getRGBdefault();
        BufferedImage image = new BufferedImage(cm, sprite, cm.isAlphaPremultiplied(), null);

        try {
            File output_file = new File("sprite_actions.png");
            ImageIO.write(image, "png", output_file);
        } catch (Exception e) { e.printStackTrace(); }


        // -------- insert in db -------- //

//        final String DB_URL = "jdbc:sqlite:config.db";
//
//        Connection conn;
//
//        try {
//            conn = DriverManager.getConnection(DB_URL);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

        for (File file : files_2) {

        }


        b("OK");
    }


}
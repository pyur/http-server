package ru.pyur.tst.resources;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class Md_MakeSpriteModules extends HtmlContent {

    public static final String CONFIG_MODULE_ICON_UPD = "module_icon_upd";


    private class ModuleDesc {
        public int id;
        public String name;

        public ModuleDesc(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }


    @Override
    public void makeHtml() throws Exception {

        heading("Генерация спрайта...");


        // ---- get list of installed modules ---- //

        Statement stmt = getConfigStatement();

        String query = "SELECT `id`, `name` FROM `module`";

        ArrayList<ModuleDesc> modules = new ArrayList<>();

//        try {
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);

                modules.add(new ModuleDesc(id, name));
            }
//        } catch (Exception e) { e.printStackTrace(); }


        // -------- make sprite -------- //

        int sprite_width = SPRITE_MODULE_WIDTH;
        int sprite_height = ((modules.size() / SPRITE_MODULE_COUNT) + 1) * SPRITE_MODULE_ICON_SIZE;  // 32 icons of 32x32 per 1024 line
        int[] sprite_matrix = new int[sprite_width * sprite_height];
        DataBufferInt sprite_buffer = new DataBufferInt(sprite_matrix, sprite_matrix.length);

        int sprite_2_width = SPRITE_MODULE2_WIDTH;
        int sprite_2_height = ((modules.size() / SPRITE_MODULE2_COUNT) + 1) * SPRITE_MODULE2_ICON_SIZE;
        int[] sprite_2_matrix = new int[sprite_2_width * sprite_2_height];
        DataBufferInt sprite_2_buffer = new DataBufferInt(sprite_2_matrix, sprite_2_matrix.length);

        //BufferedImage sprite = new BufferedImage(1024, sprite_height, BufferedImage.TYPE_INT_ARGB);

        int[] bandMasks = {0xFF0000, 0xFF00, 0xFF, 0xFF000000}; // ARGB (yes, ARGB, as the masks are R, G, B, A always) order
        //SinglePixelPackedSampleModel sppsm = new SinglePixelPackedSampleModel(TYPE_BYTE, 1024, 16, bandMasks);

        //WritableRaster sprite = new WritableRaster(sppsm, new Point(0,0));
        //WritableRaster sprite = Raster.createWritableRaster(sppsm, new Point(0,0));
        WritableRaster sprite = Raster.createPackedRaster(sprite_buffer, sprite_width, sprite_height, sprite_width, bandMasks, null);
        WritableRaster sprite_2 = Raster.createPackedRaster(sprite_2_buffer, sprite_2_width, sprite_2_height, sprite_2_width, bandMasks, null);

        for (ModuleDesc md : modules) {
            File file = new File("./resources/module_icon/" + md.name + ".png");
            if (!file.exists()) continue;

            BufferedImage img;
//            try {
                img = ImageIO.read(file);
//            } catch (Exception e) {
//                e.printStackTrace();
//                text("Failed. read image " + file.getAbsoluteFile());
//                return;
//            }

            if (img.getWidth() != SPRITE_MODULE_ICON_SIZE || img.getHeight() != SPRITE_MODULE_ICON_SIZE) {
//                text("Failed. wrong image dimensions " + img.getWidth() + " x " + img.getHeight() + ". " + file.getAbsoluteFile());
//                return;
                throw new Exception("Failed. wrong image dimensions " + img.getWidth() + " x " + img.getHeight() + ". " + file.getAbsoluteFile());
            }

            Raster raster = img.getData();
            //WritableRaster sprite = raster.createCompatibleWritableRaster(1024, 16);

            int y = ((md.id - 1) / SPRITE_MODULE_COUNT) * SPRITE_MODULE_ICON_SIZE;
            int x = ((md.id - 1) % SPRITE_MODULE_COUNT) * SPRITE_MODULE_ICON_SIZE;
            sprite.setRect(x, y, raster);
        }

            // ---------------- size 2x ---------------- //

        for (ModuleDesc md : modules) {
            File file = new File("./resources/module_icon_2/" + md.name + ".png");
            if (!file.exists())  continue;

            BufferedImage img;
//            try {
                img = ImageIO.read(file);
//            } catch (Exception e) {
//                e.printStackTrace();
//                text("Failed. read image " + file.getAbsoluteFile());
//                return;
//            }

            if (img.getWidth() != SPRITE_MODULE2_ICON_SIZE || img.getHeight() != SPRITE_MODULE2_ICON_SIZE) {
//                text("Failed. wrong image dimensions " + img.getWidth() + " x " + img.getHeight() + ". " + file.getAbsoluteFile());
//                return;
                throw new Exception("Failed. wrong image dimensions " + img.getWidth() + " x " + img.getHeight() + ". " + file.getAbsoluteFile());
            }

            Raster raster = img.getData();
            //WritableRaster sprite = raster.createCompatibleWritableRaster(1024, 16);

            int y = ((md.id-1) / SPRITE_MODULE2_COUNT) * SPRITE_MODULE2_ICON_SIZE;
            int x = ((md.id-1) % SPRITE_MODULE2_COUNT) * SPRITE_MODULE2_ICON_SIZE;
            sprite_2.setRect(x, y, raster);
        }

        ColorModel cm = ColorModel.getRGBdefault();
        BufferedImage image = new BufferedImage(cm, sprite, cm.isAlphaPremultiplied(), null);

//        try {
            File output_file = new File("sprite_modules.png");
            ImageIO.write(image, "png", output_file);
//        } catch (Exception e) { e.printStackTrace(); }


        //ColorModel cm = ColorModel.getRGBdefault();
        BufferedImage image2 = new BufferedImage(cm, sprite_2, cm.isAlphaPremultiplied(), null);

//        try {
            output_file = new File("sprite_modules_2.png");
            ImageIO.write(image2, "png", output_file);
//        } catch (Exception e) { e.printStackTrace(); }



        long timestamp_ms = System.currentTimeMillis();
        int timestamp = (int)(timestamp_ms / 1000);

        configSet(CONFIG_MODULE_ICON_UPD, timestamp);


        text("OK");
    }


}
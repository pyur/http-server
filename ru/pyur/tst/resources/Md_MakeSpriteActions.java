package ru.pyur.tst.resources;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.Util;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.sql.Statement;
import java.util.ArrayList;


public class Md_MakeSpriteActions extends HtmlContent {

    public static final String CONFIG_ACTION_ICON_UPD = "action_icon_upd";


//    public Md_MakeSpriteActions(HttpSession session) { initHtml(session); }



    @Override
    public void makeHtml() {

        text("Генерация спрайта...");


        File directory = new File("./resources/action_icon/");

        File[] files = directory.listFiles();

        if (files == null) {
            //System.out.println("directory.files == null");
            text("Failed. directory not exists.");
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
        int sprite_height = ((files_2.size() / 64) + 1) * 16;  // 64 icons of 16x16 per 1024 line
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
                text("Failed. read image " + file.getAbsoluteFile());
                return;
            }

            if (img.getWidth() != 16 || img.getHeight() != 16) {
                text("Failed. wrong image dimensions " + img.getWidth() + " x " + img.getHeight() + ". " + file.getAbsoluteFile());
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



        // ---------------- insert into db ---------------- //

//x        connectConfigDb();
        Statement stmt = getConfigStatement();

        String query = "DELETE FROM `action_icon`";

        try {
            int delete_result = stmt.executeUpdate(query);
            //System.out.println("delete result: " + delete_result);
        } catch (Exception e) { e.printStackTrace(); }

        int pos = 0;
        for (File file : files_2) {
            String name_ext = file.getName();
            String name = Util.stripExtension(name_ext);
            //todo: filter name. or use frameworked abstract db layer, with embedded filter abilities
//            PreparedStatement ps = m_conn.prepareStatement("SELECT `desc` FROM `item` WHERE `id` = ?");
//            ps.setString(1, "10");
//            ResultSet rs = ps.executeQuery();
//            int result = ps.updateQuery();


            query = "INSERT INTO `action_icon` (`name`, `position`) VALUES ('" + name + "', " + pos + ")";

            try {
                int insert_result = stmt.executeUpdate(query);
                //System.out.println("insert result: " + insert_result);
            } catch (Exception e) { e.printStackTrace(); }

            pos++;
        }


        long timestamp_ms = System.currentTimeMillis();
        //System.out.println("System.currentTimeMillis(): " + timestamp_ms);
        int timestamp = (int)(timestamp_ms / 1000);
        //System.out.println("timestamp: " + timestamp);

        query = "UPDATE `config` SET `value` = " + timestamp + " WHERE `key` = '" + CONFIG_ACTION_ICON_UPD + "'";

        int update_result = 0;
        try {
            update_result = stmt.executeUpdate(query);
        } catch (Exception e) { e.printStackTrace(); }

        if (update_result == 0) {
            query = "INSERT INTO `config` (`key`, `value`) VALUES ('" + CONFIG_ACTION_ICON_UPD + "', " + timestamp + ")";
            try {
                int insert_result = stmt.executeUpdate(query);
                //System.out.println("config insert result: " + insert_result);
            } catch (Exception e) { e.printStackTrace(); }
        }


        text("OK");
    }


}
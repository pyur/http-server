package ru.pyur.tst.dbedit.resources;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;


public class Md_MakeSpriteActions extends HtmlContent {

//x    public static final String CONFIG_ACTION_ICON_UPD = "action_icon_upd";


    @Override
    public void makeHtml() throws Exception {

        heading("Генерация спрайта действий");


        File directory = new File("./" + getHostDir() + "/resources/action_icon/");

        File[] files = directory.listFiles();

        if (files == null) {
            //System.out.println("directory.files == null");
            add("Failed. directory not exists.");
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
//            try {
                img = ImageIO.read(file);
//            } catch (Exception e) {
//                e.printStackTrace();
//                text("Failed. read image " + file.getAbsoluteFile());
//                return;
//            }

            if (img.getWidth() != 16 || img.getHeight() != 16) {
                //text("Failed. wrong image dimensions " + img.getWidth() + " x " + img.getHeight() + ". " + file.getAbsoluteFile());
                //return;
                throw new Exception("Failed. wrong image dimensions " + img.getWidth() + " x " + img.getHeight() + ". " + file.getAbsoluteFile());
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

//        try {
            File output_file = new File(getHostDir() + "/sprite_actions.png");
            ImageIO.write(image, "png", output_file);
//        } catch (Exception e) { e.printStackTrace(); }



        // ---------------- insert into db ---------------- //

        Statement stmt = getConfigStatement();

        String query = "DELETE FROM `action_icon`";

//        try {
            int delete_result = stmt.executeUpdate(query);
            //System.out.println("delete result: " + delete_result);
//        } catch (Exception e) { e.printStackTrace(); }

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

//            try {
                int insert_result = stmt.executeUpdate(query);
                //System.out.println("insert result: " + insert_result);
//            } catch (Exception e) { e.printStackTrace(); }

            pos++;
        }


        long timestamp_ms = System.currentTimeMillis();
        //System.out.println("System.currentTimeMillis(): " + timestamp_ms);
        int timestamp = (int)(timestamp_ms / 1000);
        //System.out.println("timestamp: " + timestamp);

//        configSet(CONFIG_ACTION_ICON_UPD, timestamp);
        String key = "sprite_actions";

        query = "UPDATE `res_ts` SET `ts` = ? WHERE `name` = ?";

        int update_result = 0;
        try {
            PreparedStatement ps = getConfigStatement(query);
            ps.setInt(1, timestamp);
            ps.setString(2, key);
            update_result = ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }

        if (update_result == 0) {
            query = "INSERT INTO `res_ts` (`name`, `ts`) VALUES (?, ?)";
            try {
                PreparedStatement ps = getConfigStatement(query);
                ps.setString(1, key);
                ps.setInt(2, timestamp);
                int insert_result = ps.executeUpdate();
            } catch (Exception e) { e.printStackTrace(); }
        }


        add("OK");
    }


}
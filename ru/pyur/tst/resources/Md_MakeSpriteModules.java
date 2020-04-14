package ru.pyur.tst.resources;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import ru.pyur.tst.Util;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class Md_MakeSpriteModules extends Module {

    public static final String CONFIG_MODULE_ICON_UPD = "module_icon_upd";


    public Md_MakeSpriteModules(Session session) { initHtml(session); }


    private class ModuleDesc {
        public int id;
        public String name;

        public ModuleDesc(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }


    @Override
    public void makeContent() {

        b("Генерация спрайта...");


        // ---- get list of installed modules ---- //

        getConfigDb();
        Statement stmt = getConfigStatement();

        String query = "SELECT `id`, `name` FROM `module`";

        ArrayList<ModuleDesc> modules = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);

                modules.add(new ModuleDesc(id, name));
            }
        } catch (Exception e) { e.printStackTrace(); }



//        File directory = new File("./resources/module_icon/");

//        File[] files = directory.listFiles();

//        if (files == null) {
//            //System.out.println("directory.files == null");
//            b("Failed. directory not exists.");
//            return;
//        }


//        ArrayList<File> files_2 = new ArrayList<>();
//
//        for (File file : files) {
//            if (file.isFile()) {
//                if (file.getName().matches(".+\\.png")) {
//                    files_2.add(file);
//                }
//            }
//        }


        // -------- make sprite -------- //

        int sprite_width = 1024;
        int sprite_height = ((modules.size() / 32) + 1) * 32;  // 32 icons of 32x32 per 1024 line
        int[] sprite_matrix = new int[sprite_width * sprite_height];
        DataBufferInt sprite_buffer = new DataBufferInt(sprite_matrix, sprite_matrix.length);

        //BufferedImage sprite = new BufferedImage(1024, sprite_height, BufferedImage.TYPE_INT_ARGB);

        int[] bandMasks = {0xFF0000, 0xFF00, 0xFF, 0xFF000000}; // ARGB (yes, ARGB, as the masks are R, G, B, A always) order
        //SinglePixelPackedSampleModel sppsm = new SinglePixelPackedSampleModel(TYPE_BYTE, 1024, 16, bandMasks);

        //WritableRaster sprite = new WritableRaster(sppsm, new Point(0,0));
        //WritableRaster sprite = Raster.createWritableRaster(sppsm, new Point(0,0));
        WritableRaster sprite = Raster.createPackedRaster(sprite_buffer, sprite_width, sprite_height, sprite_width, bandMasks, null);

//x        int i = 0;
        //for (File file : files_2) {
        for (ModuleDesc md : modules) {
            File file = new File("./resources/module_icon/" + md.name + ".png");
            if (!file.exists())  continue;

            BufferedImage img;
            try {
                img = ImageIO.read(file);
            } catch (Exception e) {
                e.printStackTrace();
                b("Failed. read image " + file.getAbsoluteFile());
                return;
            }

            if (img.getWidth() != 32 || img.getHeight() != 32) {
                b("Failed. wrong image dimensions " + img.getWidth() + " x " + img.getHeight() + ". " + file.getAbsoluteFile());
                return;
            }

            Raster raster = img.getData();
            //WritableRaster sprite = raster.createCompatibleWritableRaster(1024, 16);

            int y = ((md.id-1) / 32) * 32;
            int x = ((md.id-1) % 32) * 32;
            sprite.setRect(x, y, raster);

//x            i++;
        }

        ColorModel cm = ColorModel.getRGBdefault();
        BufferedImage image = new BufferedImage(cm, sprite, cm.isAlphaPremultiplied(), null);

        try {
            File output_file = new File("sprite_modules.png");
            ImageIO.write(image, "png", output_file);
        } catch (Exception e) { e.printStackTrace(); }



        // ---------------- insert into db ---------------- //
/*
        getConfigDb();
        Statement stmt = getConfigStatement();

        String query = "DELETE FROM `module_icon`";

        try {
            int delete_result = stmt.executeUpdate(query);
            //System.out.println("delete result: " + delete_result);
        } catch (Exception e) { e.printStackTrace(); }

        int pos = 0;
        for (File file : files_2) {
            String name_ext = file.getName();
            String name = Util.stripExtension(name_ext);

            query = "INSERT INTO `module_icon` (`name`, `position`) VALUES (?, ?)";

            try {
                PreparedStatement ps = getConfigStatement(query);
                ps.setString(1, name);
                ps.setInt(2, pos);
                int insert_result = ps.executeUpdate();
                //System.out.println("insert result: " + insert_result);
            } catch (Exception e) { e.printStackTrace(); }

            pos++;
        }
*/

        long timestamp_ms = System.currentTimeMillis();
        int timestamp = (int)(timestamp_ms / 1000);

        query = "UPDATE `config` SET `value` = ? WHERE `key` = '" + CONFIG_MODULE_ICON_UPD + "'";

        int update_result = 0;
        try {
            PreparedStatement ps = getConfigStatement(query);
            ps.setInt(1, timestamp);
            //ps.setString(2, CONFIG_MODULE_ICON_UPD);
            update_result = ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }

        if (update_result == 0) {
            query = "INSERT INTO `config` (`key`, `value`) VALUES ('" + CONFIG_MODULE_ICON_UPD + "', ?)";
            try {
                PreparedStatement ps = getConfigStatement(query);
                ps.setInt(1, timestamp);
                int insert_result = ps.executeUpdate();
            } catch (Exception e) { e.printStackTrace(); }
        }


        b("OK");
    }


}
package model.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

final class Resources {
    private static final String[] IMAGE_DIRS = {
            "view/ressources",
            "Peggle/src/model/view/ressources",
            "model/view/ressources"
    };

    private Resources() {
    }

    static File imageFile(String name) {
        for (String dir : IMAGE_DIRS) {
            File file = name.isEmpty() ? new File(dir) : new File(dir, name);
            if (file.exists()) {
                return file;
            }
        }
        return name.isEmpty() ? new File(IMAGE_DIRS[0]) : new File(IMAGE_DIRS[0], name);
    }

    static String imagePath(String name) {
        File file = imageFile(name);
        if (name.isEmpty()) {
            return file.getPath() + File.separator;
        }
        return file.getPath();
    }

    static BufferedImage readImage(String name) throws IOException {
        File file = imageFile(name);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath());
        }
        return ImageIO.read(file);
    }

    static File soundFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        File fromProjectParent = new File("Peggle", path);
        if (fromProjectParent.exists()) {
            return fromProjectParent;
        }
        return file;
    }
}

package co.ff36.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Helper class to zip directories with all their content
 *
 * Created by tarka on 28/03/2016.
 */
public class ZipUtil {

    /**
     * Public method to zip a directory with all its content.
     *
     * @param srcFolder The source folder to zip up
     * @param destZipFile The destination of the zipped file.
     * @throws Exception
     */
    static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;

        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);

        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
    }

    /**
     * Recursively add files inside a directory to the parent zip file
     * @param path The path to the inner file
     * @param srcFile The path to the original source file
     * @param zip The zip file to add the resource file to.
     * @throws Exception
     */
    static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {

        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip);
        } else {
            byte[] buf = new byte[1024];
            int len;
            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
        }
    }

    /**
     * Recursively add folder inside a directory to the parent zip file
     * @param path The path to the inner file
     * @param srcFolder The path to the original source foulder
     * @param zip The zip file to add the resource file to.
     * @throws Exception
     */
    static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
        File folder = new File(srcFolder);

        for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
            }
        }
    }

}

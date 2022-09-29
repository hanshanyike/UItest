package org.example.soot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import org.example.Main;
import org.example.parser.ManifestParser;

public class Repackage {

    private String apkPath = Main.apkPath;
    private String resourcesFilePath = ManifestParser.resourcesResultDirectoryPath + "resources.zip";
    public static String repackageDirectoryPath = "RepackageOutput/";
    public static String modifiedApkPath = null;

    public void exec() throws IOException {
        init();
        overrideManifest(apkPath, resourcesFilePath);
    }

    private void init() throws IOException {

        apkPath = Main.apkPath;
        resourcesFilePath = ManifestParser.resourcesResultDirectoryPath + "resources.zip";
        modifiedApkPath = null;

        File repackageDirectory = new File(repackageDirectoryPath);
        if (repackageDirectory.exists()) {
            FileUtils.deleteDirectory(repackageDirectory);
        }
        repackageDirectory.mkdir();
    }

    public void overrideManifest(String srcApkPath, String modifiedResourcesFilePath) throws IOException {
        File srcApk = new File(srcApkPath);
        if (!srcApk.exists()) {
            System.err.println("src apk does not exist ...");
            System.exit(0);
        }

        // generate an intermedia
        String tempApkPath = srcApkPath.replace(".apk", "_tmp.apk");
        File tempApk = new File(tempApkPath);
        FileUtils.copyFile(srcApk, tempApk);

        modifiedApkPath = repackageDirectoryPath + srcApk.getName();
        File tgtApk = new File(modifiedApkPath);
        if (tgtApk.exists()) {
            tgtApk.delete();
        }
        tgtApk.createNewFile();

        byte[] buf = new byte[4096 * 1024];
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempApk));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tgtApk));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String entryName = entry.getName();
            // Add ZIP entry to output stream.
            if (entryName.equals("AndroidManifest.xml")) {
                entry = zin.getNextEntry();
                continue;
            }
            out.putNextEntry(new ZipEntry(entryName));
            // Transfer bytes from the ZIP file to the output file
            int len;
            while ((len = zin.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            entry = zin.getNextEntry();
        }
        zin.close();

        File modifiedResourcesFile = new File(modifiedResourcesFilePath);
        zin = new ZipInputStream(new FileInputStream(modifiedResourcesFile));

        entry = zin.getNextEntry();
        while (entry != null) {
            String entryName = entry.getName();
            // Add ZIP entry to output stream.
            if (!entryName.equals("AndroidManifest.xml")) {
                entry = zin.getNextEntry();
                continue;
            }
            out.putNextEntry(new ZipEntry(entryName));
            // Transfer bytes from the ZIP file to the output file
            int len;
            while ((len = zin.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            entry = zin.getNextEntry();
        }
        zin.close();

        // Complete the ZIP file
        out.close();
        tempApk.delete();
    }

}

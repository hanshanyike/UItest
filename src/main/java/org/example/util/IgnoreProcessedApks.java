package org.example.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.example.Main;

public class IgnoreProcessedApks {

    private static String successProcessedFilePath = Main.successProcessedFile;
    private static String errorProcessedFilePath = Main.errorProcessedFile;

    private static String resultDirectoryPath = "UIOutput";

    public static HashSet<String> calcProcessedApkFile() {
        HashSet<String> processedApks = new HashSet<String>();

        try {
            File successProcessedFile = new File(successProcessedFilePath);
            if (successProcessedFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(successProcessedFile));
                String line = null;
                while((line = br.readLine()) != null) {
                    if (line.contains(".apk") && line.contains("/")) {
                        String apkName = line.split("/")[line.split("/").length - 1].replace(".apk", "");
                        processedApks.add(apkName);
                    }
                }
            }

            File errorProcessedFile = new File(errorProcessedFilePath);
            if (errorProcessedFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(errorProcessedFile));
                String line = null;
                while((line = br.readLine()) != null) {
                    if (line.contains(".apk") && line.contains("/")) {
                        String apkName = line.split("/")[line.split("/").length - 1].replace(".apk", "");
                        processedApks.add(apkName);
                    }
                }
            }

            File resultDirectory = new File(resultDirectoryPath);
            if (resultDirectory.exists() && resultDirectory.isDirectory()) {
                File[] resultSubFolders = resultDirectory.listFiles();
                for (File resultSubFolder : resultSubFolders) {
                    processedApks.add(resultSubFolder.getName());
                }
            }

            return processedApks;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(0);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        }

        System.exit(0);
        return null;
    }

    public static void showProcessedApks(HashSet<String> processedApks) {
        for (String apkName : processedApks) {
            System.out.println(apkName);
        }
    }

}


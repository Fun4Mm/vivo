package com.htetznaing.vivomyanmarfonts.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class myWorker {
    private static final String TAG = "myWorker";
    private static String SOURCE_FOLDER = "";
    public boolean Assets2SD(Context context, String inputFileName, String OutputDir, String OutputFileName) {
        boolean lol = false;
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            try {
                in = assetManager.open(inputFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            out = new FileOutputStream(OutputDir + OutputFileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(OutputDir+OutputFileName);
        if (file.exists()!=false){
            lol=true;
        }else{
            lol=false;
        }
        return lol;
    }

    public boolean isFileExist(String file){
        File n = new File(file);
        boolean check = false;
        if (n.exists()){
            check = true;
        }
        return check;
    }

    public boolean createDirectory(String path){
        File n = new File(path);
        boolean check = false;
        if (!n.exists()){
            check = n.mkdirs();
        }else{
            check = true;
        }
        return check;
    }

    public boolean zipDirectory(String sourceDir, String zipFile) {
        SOURCE_FOLDER = sourceDir;

        try {
            boolean ioe = false;
            FileOutputStream fout = new FileOutputStream(zipFile);
            ZipOutputStream zout = new ZipOutputStream(fout);
            File fileSource = new File(sourceDir);
            ioe = addDirectory(zout, fileSource, true);
            zout.close();
            System.out.println("Zip file has been created!");
            return ioe;
        } catch (IOException var7) {
            System.out.println("IOException :" + var7);
            return false;
        }
    }

    private static boolean addDirectory(ZipOutputStream zout, File fileSource, boolean isRoot) {
        boolean wasOK = false;
        File[] files = fileSource.listFiles();
        System.out.println("Adding directory " + fileSource.getName());

        for(int i = 0; i < files.length; ++i) {
            if(files[i].isDirectory()) {
                wasOK = addDirectory(zout, files[i], false);
                if(!wasOK) {
                    return false;
                }
            } else {
                try {
                    System.out.println("Adding file " + files[i].getAbsolutePath());
                    byte[] ioe = new byte[1024];
                    FileInputStream fin = new FileInputStream(files[i].getAbsoluteFile());
                    if(isRoot) {
                        zout.putNextEntry(new ZipEntry(generateZipEntry(files[i].getAbsoluteFile().toString())));
                    } else {
                        zout.putNextEntry(new ZipEntry(generateZipEntry(files[i].getAbsoluteFile().toString())));
                    }

                    int length;
                    while((length = fin.read(ioe)) > 0) {
                        zout.write(ioe, 0, length);
                    }

                    zout.closeEntry();
                    fin.close();
                } catch (IOException var9) {
                    System.out.println("IOException :" + var9);
                    return false;
                }
            }
        }

        return true;
    }

    private static String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }

    public boolean copy(String fromPath, String toPath) {
        File file = new File(fromPath);
        if (!file.isFile()) {
            return false;
        }

        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(file);
            outStream = new FileOutputStream(new File(toPath));
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            Log.e(TAG, "Failed copy", e);
            return false;
        } finally {
            closeSilently(inStream);
            closeSilently(outStream);
        }
        return true;
    }

    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void zipSubFolder(ZipOutputStream out, File folder,
                             int basePathLength) throws IOException {
        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

    private void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public void writeTextFile(String path, String texts){
        try{
            FileWriter writer = new FileWriter(path);
            writer.append(texts);
            writer.flush();
            writer.close();

        }catch (Exception e){
        }
    }

    public String readTextFile(String path) {
        File file = new File(path);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
        }

        return text.toString();
    }

    public boolean deleteFile(String path){
        boolean b = false;
        File file = new File(path);
        if (file.exists()) {
            b = file.delete();
        }else{
            b = true;
        }

        return b;
    }

    public String getXML(String fontName, String ttfName){
        String s1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<font displayname=\"Htetz(zFont)\">\n" +
                "\t<sans>\n" +
                "\t\t<file>\n" +
                "\t\t\t<filename>htetz.ttf</filename>\n" +
                "\t\t\t<droidname>DroidSans.ttf</droidname>\n" +
                "\t\t</file>\n" +
                "\t\t<file>\n" +
                "\t\t\t<filename>htetz.ttf</filename>\n" +
                "\t\t\t<droidname>DroidSans-Bold.ttf</droidname>\n" +
                "\t\t</file>\n" +
                "\t</sans>\n" +
                "</font>";

        s1 = s1.replace("Htetz",fontName);
        s1 = s1.replace("htetz.ttf",ttfName);

        return s1;
    }

    public void deleteAllInDirectory(String path){
        File file[] = new File(path).listFiles();
        if (file!=null && file.length>1) {
            for (int i = 0; i < file.length; i++) {
                file[i].delete();
            }
        }
    }

    public boolean deleteDirectory(String path) {
        return deleteDirectoryImpl(path);
    }

    private boolean deleteDirectoryImpl(String path) {
        File directory = new File(path);

        // If the directory exists then delete
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files == null) {
                return true;
            }
            // Run on all sub files and folders and delete them
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectoryImpl(files[i].getAbsolutePath());
                } else {
                    files[i].delete();
                }
            }
        }
        return directory.delete();
    }

    public boolean unZip(String zipFile, String targetPath)
    {
        if ((zipFile == null) || (zipFile.equals(""))) {
            System.out.println("Invalid source file");
            return false;
        }
        System.out.println("Zip file extracted!");
        return unzip(zipFile, targetPath);
    }

    private static boolean unzip(String zipFile, String targetPath){
        try
        {
            File fSourceZip = new File(zipFile);
            File temp = new File(targetPath);
            temp.mkdir();
            System.out.println(targetPath + " created");
            ZipFile zFile = new ZipFile(fSourceZip);
            Enumeration e = zFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)e.nextElement();
                File destinationFilePath = new File(targetPath, entry.getName());
                destinationFilePath.getParentFile().mkdirs();
                if (!entry.isDirectory())
                {
                    System.out.println("Extracting " + destinationFilePath);
                    BufferedInputStream bis = new BufferedInputStream(zFile.getInputStream(entry));

                    byte[] buffer = new byte['Ѐ'];
                    FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
                    int b;
                    while ((b = bis.read(buffer, 0, 1024)) != -1) {
                        bos.write(buffer, 0, b);
                    }
                    bos.flush();
                    bos.close();
                    bis.close();
                }
            }
        }
        catch (IOException ioe) {
            System.out.println("IOError :" + ioe);
            return false;
        }
        return true;
    }
}

package com.htetznaing.vivomyanmarfonts.Utils.FontToolkit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadTTF {
    public static String getNames(String font){
        String fontName = null;
        try {
            InputStream inputStream = new FileInputStream(font);
            TTFFile ttfFile = FontFileReader.readTTF(inputStream);
            fontName = ttfFile.getFullName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontName;
    }

    public static String checkFont(String font){
        String fontName = null;
        try {
            InputStream inputStream = new FileInputStream(font);
            TTFFile ttfFile = FontFileReader.readTTF(inputStream);
            fontName = ttfFile.getFullName();
            if (fontName.isEmpty() || fontName.length()<1){
                fontName = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontName;
    }
}

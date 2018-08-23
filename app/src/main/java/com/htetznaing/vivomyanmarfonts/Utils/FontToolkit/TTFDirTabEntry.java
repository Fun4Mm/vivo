package com.htetznaing.vivomyanmarfonts.Utils.FontToolkit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * This class represents an entry to a TrueType font's Dir Tab.
 */
public class TTFDirTabEntry {

    private final byte[] tag = new byte[4];

    private long offset;

    private long length;

    TTFDirTabEntry() {
    }

    public TTFDirTabEntry(long offset, long length) {
        this.offset = offset;
        this.length = length;
    }

    /**
     * Returns the bytesToUpload.
     *
     * @return long
     */
    public long getLength() {
        return length;
    }

    /**
     * Returns the offset.
     *
     * @return long
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Returns the tag bytes.
     *
     * @return byte[]
     */
    public byte[] getTag() {
        return tag;
    }

    /**
     * Returns the tag bytes.
     *
     * @return byte[]
     */
    public String getTagString() {
        try {
            return new String(tag, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return toString(); // Should never happen.
        }
    }

    /**
     * Read Dir Tab.
     *
     * @param in
     *         font file reader
     * @return tag name
     * @throws IOException
     *         upon I/O exception
     */
    public String read(FontFileReader in) throws IOException {
        tag[0] = in.readTTFByte();
        tag[1] = in.readTTFByte();
        tag[2] = in.readTTFByte();
        tag[3] = in.readTTFByte();

        in.skip(4); // Skip checksum

        offset = in.readTTFULong();
        length = in.readTTFULong();
        String tagStr = new String(tag, "ISO-8859-1");

        return tagStr;
    }

    @Override
    public String toString() {
        return "Read dir tab [" + tag[0] + " " + tag[1] + " " + tag[2] + " " + tag[3] + "]"
                + " offset: " + offset + " bytesToUpload: " + length + " name: " + tag;
    }

}

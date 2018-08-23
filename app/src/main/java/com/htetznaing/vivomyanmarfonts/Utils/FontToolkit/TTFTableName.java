package com.htetznaing.vivomyanmarfonts.Utils.FontToolkit;

public final class TTFTableName {

    /** The first table in a TrueType font file containing metadata about other tables. */
    public static final TTFTableName TABLE_DIRECTORY = new TTFTableName("tableDirectory");

    /** Naming table. */
    public static final TTFTableName NAME = new TTFTableName("name");

    /**
     * Returns an instance of this class corresponding to the given string representation.
     *
     * @param tableName
     *         table name as in the Table Directory
     * @return TTFTableName
     */
    public static TTFTableName getValue(String tableName) {
        if (tableName != null) {
            return new TTFTableName(tableName);
        }
        throw new IllegalArgumentException("A TrueType font table name must not be null");
    }

    private final String name;

    private TTFTableName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TTFTableName)) {
            return false;
        }
        TTFTableName to = (TTFTableName) o;
        return name.equals(to.getName());
    }

    /**
     * Returns the name of the table as it should be in the Directory Table.
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

}

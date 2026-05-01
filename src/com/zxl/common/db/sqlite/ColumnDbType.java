package com.zxl.common.db.sqlite;

/**
 * Created by wyouflf on 14-2-20.
 */
public enum ColumnDbType
{
    
    /**
     * INTEGER
     */
    INTEGER("INTEGER"),
    /**
     * REAL
     */
    REAL("REAL"),
    /**
     * TEXT
     */
    TEXT("TEXT"),
    /**
     * BLOB
     */
    BLOB("BLOB");
    
    private String value;
    
    ColumnDbType(final String value)
    {
        this.value = value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return value;
    }
}

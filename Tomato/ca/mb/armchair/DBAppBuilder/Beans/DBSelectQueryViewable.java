package ca.mb.armchair.DBAppBuilder.Beans;


/** Select query intended for use in human-viewed display, via various widgets */ 
public class DBSelectQueryViewable extends DBSelectQuery
{
    private String DisplayColumns = "";

    /** Creates a new instance of DBSelectQueryViewable */
    public DBSelectQueryViewable(DatabaseConnection db, String displaycolumns, String SQL)
    {
        super(db, SQL);
        DisplayColumns = displaycolumns;
    }

    public DBSelectQueryViewable() {
    }
    
    /** Getter for property displayColumns.
     * @return Value of property displayColumns.
     */
    public String getDisplayColumns() {
        return this.DisplayColumns;
    }
    
    /** Setter for property displayColumns.
     * @param displayColumns New value of property displayColumns.
     */
    public void setDisplayColumns(String displayColumns) {
        this.DisplayColumns = displayColumns;
        /* There oughta be an event fired here to indicate display change! */
    }
}

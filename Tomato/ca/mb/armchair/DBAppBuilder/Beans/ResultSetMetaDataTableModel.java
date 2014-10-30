package ca.mb.armchair.DBAppBuilder.Beans;

/*
 * MetaDataTableModel.java
 *
 * Created on December 24, 2001, 5:55 AM
 */

import java.sql.*;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.*;


/**
 *
 * @author  creatist
 */
public class ResultSetMetaDataTableModel extends AbstractTableModel {

    private Vector Rows = new Vector();
    private ResultSetMetaData resultSetMetaData = null;
    private String output = "";
    
    /** Given nothing, create an error instance of MetaDataTableModel */
    public ResultSetMetaDataTableModel() {
    }
    
    /** Given a resultset, creates a new instance of MetaDataTableModel */
    public ResultSetMetaDataTableModel(ResultSet rS) {
        if (rS==null)
            return;
        try {
            resultSetMetaData=rS.getMetaData();
            Object msg;
            for (int i=1; i<=resultSetMetaData.getColumnCount(); i++)
            {
                  Vector v = new Vector();

                  msg = new Integer(i);
                  output = output +
                          "Column index i=" + i + "\n\n";
                  v.add(msg);
                          
                  output = output + "String getCatalogName(i) = ";
                  try {
                      msg = (new String("'" + resultSetMetaData.getCatalogName(i) + "'"));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nGets the designated column's table's catalog name.\n\n";
                  v.add(msg);
                  
                  output = output + "String getColumnClassName(i) = ";
                  try {
                    msg = (new String("'" + resultSetMetaData.getColumnClassName(i) + "'"));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nReturns the fully-qualified name of the Java class whose instances\nare manufactured if the method ResultSet.getObject is called to retrieve\na value from the column.\n\n";
                  v.add(msg);
  
                  output = output + "int getColumnCount() = ";
                  try {
                    msg = (new Integer(resultSetMetaData.getColumnCount()));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nReturns the number of columns in this ResultSet object.\n\n";
                  v.add(msg);

                  output = output + "int getColumnDisplaySize(i) = ";
                  try {
                    msg = (new Integer(resultSetMetaData.getColumnDisplaySize(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nIndicates the designated column's normal maximum width in characters.\n\n";
                  v.add(msg);

                  output = output + "String getColumnLabel(i) = ";
                  try {
                    msg = (new String("'" + resultSetMetaData.getColumnLabel(i) + "'"));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nGets the designated column's suggested title for use in printouts and displays.\n\n";
                  v.add(msg);

                  output = output + "String getColumnName(i) = ";
                  try {
                    msg = (new String("'" + resultSetMetaData.getColumnName(i) + "'"));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nGet the designated column's name.\n\n";
                  v.add(msg);

                  output = output + "int getColumnType(i) = ";
                  try {
                    msg = (new Integer(resultSetMetaData.getColumnType(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nRetrieves the designated column's SQL type.\n\n";
                  v.add(msg);

                  output = output + "String getColumnTypeName(i) = ";
                  try {
                    msg = (new String("'" + resultSetMetaData.getColumnTypeName(i) + "'"));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nRetrieves the designated column's database-specific type name.\n\n";
                  v.add(msg);

                  output = output + "int getPrecision(i) = ";
                  try {
                    msg = (new Integer(resultSetMetaData.getPrecision(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nGet the designated column's number of decimal digits.\n\n";
                  v.add(msg);

                  output = output + "int getScale(i) = ";
                  try {
                    msg = (new Integer(resultSetMetaData.getScale(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nGets the designated column's number of digits to right of the decimal point.\n\n";
                  v.add(msg);

                  output = output + "String getSchemaName(i) = ";
                  try {
                    msg = (new String("'" + resultSetMetaData.getSchemaName(i) + "'"));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nGet the designated column's table's schema.\n\n";
                  v.add(msg);

                  output = output + "String getTableName(i) = ";
                  try {
                    msg = (new String("'" + resultSetMetaData.getTableName(i) + "'"));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nGets the designated column's table name.\n\n";
                  v.add(msg);

                  output = output + "boolean isAutoIncrement(i) = ";
                  try {
                    msg = (new Boolean(resultSetMetaData.isAutoIncrement(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nIndicates whether the designated column is automatically numbered, thus read-only.\n\n";
                  v.add(msg);

                  output = output + "boolean isCaseSensitive(i) = ";
                  try {
                    msg = (new Boolean(resultSetMetaData.isCaseSensitive(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nIndicates whether a column's case matters.\n\n";
                  v.add(msg);

                  output = output + "boolean isCurrency(i) = ";
                  try {
                    msg = (new Boolean(resultSetMetaData.isCurrency(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nIndicates whether the designated column is a cash value.\n\n";
                  v.add(msg);

                  output = output + "boolean isDefinitelyWritable(i) = ";
                  try {
                    msg = (new Boolean(resultSetMetaData.isDefinitelyWritable(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nIndicates whether a write on the designated column will definitely succeed.\n\n";
                  v.add(msg);

                  output = output + "int isNullable(i) = ";
                  try {
                    msg = (new Integer(resultSetMetaData.isNullable(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nIndicates the nullability of values in the designated column.\n\n";
                  v.add(msg);

                  output = output + "boolean isReadOnly(i) = ";
                  try {
                    msg = (new Boolean(resultSetMetaData.isReadOnly(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nIndicates whether the designated column is definitely not writable.\n\n";
                  v.add(msg);

                  output = output + "boolean isSearchable(i) = ";
                  try {
                    msg = (new Boolean(resultSetMetaData.isSearchable(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nIndicates whether the designated column can be used in a where clause.\n\n";
                  v.add(msg);

                  output = output + "boolean isSigned(i) = ";
                  try {
                    msg = (new Boolean(resultSetMetaData.isSigned(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nIndicates whether values in the designated column are signed numbers.\n\n";
                  v.add(msg);

                  output = output + "boolean isWritable(i) = ";
                  try {
                    msg = (new Boolean(resultSetMetaData.isWritable(i)));
                  } catch (SQLException e) {
                      msg = (new String("unimplemented"));
                  }
                  output = output + msg.toString() + "\nIndicates whether it is possible for a write on the designated column to succeed.\n\n";
                  v.add(msg);

                  Rows.add(v);
            }                
        } catch (SQLException e) {
            resultSetMetaData=null;
        }
    }
    
    /** Return textual equivalent to table contents.  Nice for logs and such. */
    public String getText() {
        return output;
    }
    
    public int getRowCount() {
        if (resultSetMetaData==null)
            return 1;
        return Rows.size();
    }
    
    public int getColumnCount() {
        if (resultSetMetaData==null)
            return 1;
        return 22;
    }
    
    public String getColumnName(int column)
    {
        if (resultSetMetaData==null)
            return "Error";
        switch (column)
        {
            case 0:  return "i = Column #";
            case 1:  return "String getCatalogName(i) = ";
            case 2:  return "String getColumnClassName(i) = ";
            case 3:  return "int getColumnCount() = ";
            case 4:  return "int getColumnDisplaySize(i) = ";
            case 5:  return "String getColumnLabel(i) = ";
            case 6:  return "String getColumnName(i) = ";
            case 7:  return "int getColumnType(i) = ";
            case 8:  return "String getColumnTypeName(i) = ";
            case 9:  return "int getPrecision(i) = ";
            case 10: return "int getScale(i) = ";
            case 11: return "String getSchemaName(i) = ";
            case 12: return "String getTableName(i) = ";
            case 13: return "boolean isAutoIncrement(i) = ";
            case 14: return "boolean isCaseSensitive(i) = ";
            case 15: return "boolean isCurrency(i) = ";
            case 16: return "boolean isDefinitelyWritable(i) = ";
            case 17: return "int isNullable(i) = ";
            case 18: return "boolean isReadOnly(i) = ";
            case 19: return "boolean isSearchable(i) = ";
            case 20: return "boolean isSigned(i) = ";
            case 21: return "boolean isWritable(i) = ";
            default: return "???";
        }
    }
    
    public Object getValueAt(int row, int column) {
        if (resultSetMetaData==null)
            return new String("Error");
        try {
            return (((Vector)Rows.get(row)).get(column));
        } catch (Exception e) {
            return new String("???");
        }
    }
    
    /** Get metadata associated with this object */
    public ResultSetMetaData getResultSetMetaData() {
        return resultSetMetaData;
    }
}

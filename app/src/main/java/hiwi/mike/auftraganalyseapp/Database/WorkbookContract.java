package hiwi.mike.auftraganalyseapp.Database;

import android.provider.BaseColumns;

/**
 * Created by dave on 08.06.16.
 */


public final class WorkbookContract {
    public WorkbookContract() {}

    public static final int     VERSION = 13;

    private static final String TEXT_TYPE          = " TEXT";
    private static final String COMMA_SEP          = ",";
    private static final String AS                  = " as ";

    /* Inner class that defines the table contents of Workbooks */
    public static abstract class WorkbookEntry implements BaseColumns {
        public static final String TABLE_NAME = "Workbooks";
        public static final String COLUMN_NAME_ENTRY_ID = "_id";
        public static final String COLUMN_NAME_ENTRY_NAME = "name";
        public static final String COLUMN_NAME_LAST_OPENED = "lastOpened";
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_ENTRY_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LAST_OPENED + TEXT_TYPE + " DEFAULT CURRENT_TIMESTAMP" +
                ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    }

    /* Inner class that defines the table contents of Workstations */
    public static abstract class WorkstationEntry implements BaseColumns {
        public static final String TABLE_NAME = "Workstations";
        public static final String COLUMN_NAME_ENTRY_ID = "_id";
        public static final String COLUMN_NAME_WORKBOOK_ID = "wb_id";
        public static final String COLUMN_NAME_ENTRY_NAME = "name";
        public static final String COLUMN_NAME_LAST_OPENED = "lastOpened";
        public static final String COLUMN_NAME_OUTPUT = "output";
        public static final String COLUMN_NAME_REIHENFOLGE = "reihenfolge";
        public static final String COLUMN_NAME_KAPSTRG = "kapstrg";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_WORKBOOK_ID + " INTEGER" + COMMA_SEP +
                COLUMN_NAME_ENTRY_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LAST_OPENED + TEXT_TYPE + " DEFAULT CURRENT_TIMESTAMP" + COMMA_SEP +
                COLUMN_NAME_OUTPUT + " REAL " + COMMA_SEP +
                COLUMN_NAME_REIHENFOLGE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_KAPSTRG + TEXT_TYPE + COMMA_SEP +
                "FOREIGN KEY (" + COLUMN_NAME_WORKBOOK_ID + ") " +
                "REFERENCES " + WorkbookEntry.TABLE_NAME + " (" + WorkbookEntry.COLUMN_NAME_ENTRY_ID + ") ON DELETE CASCADE" +
                ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    }

    /* Inner class that defines the table contents of Orders */
    public static abstract class OrderEntry implements BaseColumns {
        public static final String TABLE_NAME = "Orders";
        public static final String COLUMN_NAME_ENTRY_ID = "_id";
        public static final String COLUMN_NAME_WORKSTATION_ID = "w_id";
        public static final String COLUMN_NAME_ENTRY_NR = "nr";
        public static final String COLUMN_NAME_ENTRY_TARGET_DATE = "targetDate";
        public static final String COLUMN_NAME_ENTRY_TIME = "givenTime";
        public static final String COLUMN_NAME_ENTRY_DOCUMENTED_DATE = "documentedDate";
        public static final String COLUMN_NAME_WIP = "wip";
        public static final String COLUMN_NAME_LAST_OPENED = "lastOpened";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_WORKSTATION_ID + " INTEGER" + COMMA_SEP +
                COLUMN_NAME_ENTRY_NR + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ENTRY_TARGET_DATE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ENTRY_DOCUMENTED_DATE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ENTRY_TIME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_WIP + " INTEGER" + COMMA_SEP +
                COLUMN_NAME_LAST_OPENED + TEXT_TYPE + COMMA_SEP +
                "FOREIGN KEY (" + COLUMN_NAME_WORKSTATION_ID + ") " +
                "REFERENCES " + WorkstationEntry.TABLE_NAME + " (" + WorkstationEntry.COLUMN_NAME_ENTRY_ID + ") ON DELETE CASCADE" +
                ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    }

    public static final String INSERT_WORKBOOK(String name)
    {
        return "INSERT INTO " + WorkbookEntry.TABLE_NAME + "(" + WorkbookEntry.COLUMN_NAME_ENTRY_NAME + ")" +
                " VALUES ('" + name +
                "');";
    }

    public static final String INSERT_WORKSTATION(String name, double output, String reihenfolge,int wb_id)
    {
        return "INSERT INTO " + WorkstationEntry.TABLE_NAME + "(" +
                    WorkstationEntry.COLUMN_NAME_ENTRY_NAME + COMMA_SEP +
                    WorkstationEntry.COLUMN_NAME_OUTPUT + COMMA_SEP +
                    WorkstationEntry.COLUMN_NAME_REIHENFOLGE + COMMA_SEP +
                    WorkstationEntry.COLUMN_NAME_WORKBOOK_ID + ")" +
                " VALUES (" +
                    "'" + name + "'" + COMMA_SEP +
                    "'" + output + "'" + COMMA_SEP +
                    reihenfolge + COMMA_SEP +
                    "'" + wb_id + "'" +
                ");";
    }

    public static final String GET_ALL_WORKBOOKS()
    {
        return GET_ALL_WORKBOOKS(WorkbookEntry.COLUMN_NAME_LAST_OPENED + " DESC");
    }

    public static final String GET_ALL_WORKBOOKS(String sortBy)
    {
        return "SELECT " + WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_ENTRY_ID + " as _id" + COMMA_SEP +
                WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_ENTRY_NAME + AS + WorkbookEntry.COLUMN_NAME_ENTRY_NAME  + COMMA_SEP +
                " COUNT(" + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID + ") as count" + //COMMA_SEP +
                //WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_LAST_OPENED + AS + WorkbookEntry.COLUMN_NAME_LAST_OPENED +
                " FROM " + WorkbookEntry.TABLE_NAME +
                " LEFT OUTER JOIN " + WorkstationEntry.TABLE_NAME +
                " ON " + WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_ENTRY_ID + " = " +
                WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_WORKBOOK_ID +
                " GROUP BY " + WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_ENTRY_ID + COMMA_SEP +
                WorkbookEntry.TABLE_NAME + "." + WorkbookEntry.COLUMN_NAME_ENTRY_NAME +
                " ORDER BY " + WorkbookEntry.TABLE_NAME + "." + sortBy + ";";
    }

    public static final String GET_WORKBOOKS_BY_ID(int id)
    {
        return "SELECT * FROM " + WorkbookEntry.TABLE_NAME + " WHERE " + WorkbookEntry.COLUMN_NAME_ENTRY_ID + " = " + id + ";";
    }

    public static final String GET_WORKSTATION_BY_ID(int id)
    {
        return "SELECT *" + COMMA_SEP +
                "COUNT(" + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_ID + ") as count" +
                " FROM " + WorkstationEntry.TABLE_NAME +
                " LEFT OUTER JOIN " + OrderEntry.TABLE_NAME +
                " ON " + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID + " = " +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WORKSTATION_ID +
                " Where " + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID + " = " + id +
                " GROUP BY " + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID + ";";
    }

    public static final String GET_WORKSTATIONS_BY_WORKBOOK(int id)
    {
        return GET_WORKSTATIONS_BY_WORKBOOK(id, WorkstationEntry.COLUMN_NAME_LAST_OPENED + " DESC");
    }

    public static final String GET_WORKSTATIONS_BY_WORKBOOK(int id, String sortBy)
    {
        return "SELECT " + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID + " as _id" + COMMA_SEP +
                WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_NAME + AS + WorkstationEntry.COLUMN_NAME_ENTRY_NAME  + COMMA_SEP +
                WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_OUTPUT + AS + WorkstationEntry.COLUMN_NAME_OUTPUT + COMMA_SEP +
                WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_REIHENFOLGE + AS + WorkstationEntry.COLUMN_NAME_REIHENFOLGE + COMMA_SEP +
                WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_KAPSTRG + AS + WorkstationEntry.COLUMN_NAME_KAPSTRG + COMMA_SEP +
                " COUNT(" + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_ID + ") as count" +
                //ProjectEntry.TABLE_NAME + "." + ProjectEntry.COLUMN_NAME_LAST_OPENED + AS + ProjectEntry.COLUMN_NAME_LAST_OPENED +
                " FROM " + WorkstationEntry.TABLE_NAME +
                " LEFT OUTER JOIN " + OrderEntry.TABLE_NAME +
                " ON " + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID + " = " +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WORKSTATION_ID +
                " WHERE " + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_WORKBOOK_ID + "=" + id +
                " GROUP BY " + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID +
                " ORDER BY " + WorkstationEntry.TABLE_NAME + "." + sortBy + ";";
    }


    public static final String GET_ORDERS_BY_WORKSTATIONS(int ws_id)
    {
        return GET_ORDERS_BY_WORKSTATIONS(ws_id, OrderEntry.COLUMN_NAME_LAST_OPENED + " DESC");
    }

    public static final String GET_ORDERS_BY_WORKSTATIONS(int ws_id, String sortBy)
    {
        return "SELECT " + OrderEntry.TABLE_NAME  + "." + OrderEntry.COLUMN_NAME_ENTRY_ID + " as _id" + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_NR + AS + OrderEntry.COLUMN_NAME_ENTRY_NR + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE + AS + OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_TIME + AS + OrderEntry.COLUMN_NAME_ENTRY_TIME + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WIP + AS + OrderEntry.COLUMN_NAME_WIP + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE + AS + OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE + COMMA_SEP +
                WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_NAME + AS +  WorkstationEntry.COLUMN_NAME_ENTRY_NAME +
                " FROM " + OrderEntry.TABLE_NAME + " INNER JOIN " + WorkstationEntry.TABLE_NAME + " ON " +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WORKSTATION_ID + "=" + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID +
                " WHERE " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WORKSTATION_ID + "=" + ws_id +
                " ORDER BY " + OrderEntry.TABLE_NAME + "." + sortBy + ";";
    }

    public static final String GET_ORDER_BY_ID(int order_id)
    {
        return "SELECT " + OrderEntry.TABLE_NAME  + "." + OrderEntry.COLUMN_NAME_ENTRY_ID + " as _id" + COMMA_SEP +
        OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_NR + AS + OrderEntry.COLUMN_NAME_ENTRY_NR + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE + AS + OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_TIME + AS + OrderEntry.COLUMN_NAME_ENTRY_TIME + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WIP + AS + OrderEntry.COLUMN_NAME_WIP + COMMA_SEP +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE + AS + OrderEntry.COLUMN_NAME_ENTRY_DOCUMENTED_DATE + COMMA_SEP +
                WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_NAME + AS +  WorkstationEntry.COLUMN_NAME_ENTRY_NAME + COMMA_SEP +
                WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID + AS + WorkstationEntry.COLUMN_NAME_ENTRY_ID +
                " FROM " + OrderEntry.TABLE_NAME + " INNER JOIN " + WorkstationEntry.TABLE_NAME + " ON " +
                OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_WORKSTATION_ID + "=" + WorkstationEntry.TABLE_NAME + "." + WorkstationEntry.COLUMN_NAME_ENTRY_ID +
                " WHERE " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_NAME_ENTRY_ID + "=" + order_id + ";";
    }
}

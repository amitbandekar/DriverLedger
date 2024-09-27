package com.example.driverledger;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DriverLedger.db";

    // Table Names
    private static final String TABLE_DRIVER_COMPLAINTS = "tblDriverComplaints";
    private static final String TABLE_SERVICING_DETAILS = "tblServicingDetails";
    private static final String TABLE_MAINTENANCE_DETAILS = "tblMaintenanceDetails";
    private static final String TABLE_TYRE_REPAIRS = "tblTyreRepairs";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_COMPLAINTS + "(id INTEGER PRIMARY KEY, vehicleNo TEXT, modelName TEXT, details TEXT, remarks TEXT, problemClosed INTEGER, currentDateTime TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SERVICING_DETAILS + "(id INTEGER PRIMARY KEY, vehicleNo TEXT, modelName TEXT, currentDateTime TEXT, runningKm INTEGER, nextServiceKm INTEGER, dieselFilterChange INTEGER, breakOilChange INTEGER, coolantChange INTEGER, remark TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MAINTENANCE_DETAILS + "(id INTEGER PRIMARY KEY, vehicleNo TEXT, modelName TEXT, details TEXT, currentDateTime TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TYRE_REPAIRS + "(id INTEGER PRIMARY KEY, vehicleNo TEXT, modelName TEXT, tyreQty INTEGER, alignmentBalancing INTEGER, alignmentKm INTEGER, nextAlignmentKm INTEGER, remark TEXT, currentDateTime TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER_COMPLAINTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICING_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAINTENANCE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYRE_REPAIRS);
        onCreate(db);
    }

    // Method to insert or update driver complaints
    public boolean saveDriverComplaints(int id, String vehicleNo, String modelName, String details, String remarks, String problemClosed, String currentDateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("vehicleNo", vehicleNo);
        contentValues.put("modelName", modelName);
        contentValues.put("details", details);
        contentValues.put("remarks", remarks);
        contentValues.put("problemClosed", problemClosed);
        contentValues.put("currentDateTime", currentDateTime);

        if (id != 0) {
            // Update record
            int result = db.update(TABLE_DRIVER_COMPLAINTS, contentValues, "id = ?", new String[]{String.valueOf(id)});
            return result > 0;
        } else {
            // Insert record
            long result = db.insert(TABLE_DRIVER_COMPLAINTS, null, contentValues);
            return result != -1;
        }
    }

    // Method to insert or update oil change service
    public boolean saveOilChangeService(int id, String vehicleNo, String modelName, String currentDateTime, String runningKm, String nextServiceKm, String dieselFilterChange, String breakOilChange, String coolantChange, String remark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("vehicleNo", vehicleNo);
        contentValues.put("modelName", modelName);
        contentValues.put("currentDateTime", currentDateTime);
        contentValues.put("runningKm", runningKm);
        contentValues.put("nextServiceKm", nextServiceKm);
        contentValues.put("dieselFilterChange", dieselFilterChange);
        contentValues.put("breakOilChange", breakOilChange);
        contentValues.put("coolantChange", coolantChange);
        contentValues.put("remark", remark);

        if (id != 0) {
            // Update record
            int result = db.update(TABLE_SERVICING_DETAILS, contentValues, "id = ?", new String[]{String.valueOf(id)});
            return result > 0;
        } else {
            // Insert record
            long result = db.insert(TABLE_SERVICING_DETAILS, null, contentValues);
            return result != -1;
        }
    }

    // Method to insert or update other maintenance data
    public boolean saveOtherMaintenanceData(int id, String vehicleNo, String modelName, String details, String currentDateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("vehicleNo", vehicleNo);
        contentValues.put("modelName", modelName);
        contentValues.put("details", details);
        contentValues.put("currentDateTime", currentDateTime);

        if (id != 0) {
            // Update record
            int result = db.update(TABLE_MAINTENANCE_DETAILS, contentValues, "id = ?", new String[]{String.valueOf(id)});
            return result > 0;
        } else {
            // Insert record
            long result = db.insert(TABLE_MAINTENANCE_DETAILS, null, contentValues);
            return result != -1;
        }
    }

    // Method to insert or update tyre change data
    public boolean saveTyreChangeData(int id, String vehicleNo, String modelName, int tyreQty, String alignmentBalancing, int alignmentKm, int nextAlignmentKm, String remark, String currentDateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("vehicleNo", vehicleNo);
        contentValues.put("modelName", modelName);
        contentValues.put("tyreQty", tyreQty);
        contentValues.put("alignmentBalancing", alignmentBalancing);
        contentValues.put("alignmentKm", alignmentKm);
        contentValues.put("nextAlignmentKm", nextAlignmentKm);
        contentValues.put("remark", remark);
        contentValues.put("currentDateTime", currentDateTime);

        if (id != 0) {
            // Update record
            int result = db.update(TABLE_TYRE_REPAIRS, contentValues, "id = ?", new String[]{String.valueOf(id)});
            return result > 0;
        } else {
            // Insert record
            long result = db.insert(TABLE_TYRE_REPAIRS, null, contentValues);
            return result != -1;
        }
    }

    // Method to get all data from a specific table
    public Cursor getAllData(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + tableName, null);
    }

    // Method to get specific data by ID
    public Cursor getDataById(String tableName, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + tableName + " WHERE id = ?", new String[]{String.valueOf(id)});
    }

    // Method to delete a record by ID from any table
    public boolean DeleteRecordById(String tableName, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(tableName, "id = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

}


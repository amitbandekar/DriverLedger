package com.example.driverledger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PDFHandler {

    private DatabaseHelper dbHelper;

    public PDFHandler(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Method to export data from the specified table to a PDF file
    public void exportDataToPDF(String tableName, Context context) {
        File pdfFile = new File(Environment.getExternalStorageDirectory(), "ExportedData.pdf");

        // Create PDF document
        try {
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Table table = new Table(new float[]{1, 2, 2, 2, 2, 2}); // Adjust the number of columns as needed

            // Set column headers based on the table name
            String[] headers = getColumnHeaders(tableName);
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(header));
            }

            // Fetch data from the database
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            while (cursor.moveToNext()) {
                for (int i = 1; i < cursor.getColumnCount(); i++) { // Skip 'id'
                    table.addCell(new Cell().add(cursor.getString(i)));
                }
            }
            cursor.close();

            pdfDocument.add(table);
            pdfDocument.close();
            Toast.makeText(context, "Data exported successfully to PDF.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error exporting data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method to import data from a PDF file into the specified table
    public void importDataFromPDF(String tableName, Context context) {
        File pdfFile = new File(Environment.getExternalStorageDirectory(), "ExportedData.pdf");

        if (!pdfFile.exists()) {
            Toast.makeText(context, "PDF file not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Open the PDF document
            PdfReader pdfReader = new PdfReader(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(pdfReader);

            // Read the first page (assuming data is on the first page)
            Table table = pdfDocument.getPage(1).getTable(0);
            int rowCount = table.getNumberOfRows();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            try {
                // Prepare column mapping for insertion
                Map<String, String> columnMapping = getColumnMapping(tableName);
                int columnCount = table.getNumberOfColumns();

                // Read the table data from the PDF
                for (int i = 1; i < rowCount; i++) { // Start from 1 to skip header
                    String[] rowData = new String[columnCount - 1]; // Skip 'id'

                    for (int j = 0; j < columnCount; j++) {
                        String header = table.getCell(0, j).getText(); // Get the header from the first row
                        if (!header.equalsIgnoreCase("id")) {
                            Cell cell = table.getCell(i, j);
                            rowData[j] = cell.getText();
                        }
                    }

                    // Insert data into the corresponding table
                    insertDataIntoTable(db, tableName, rowData, columnMapping);
                }
                db.setTransactionSuccessful();
                Toast.makeText(context, "Data imported successfully.", Toast.LENGTH_SHORT).show();
            } finally {
                db.endTransaction();
                pdfDocument.close();
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(context, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error reading PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void insertDataIntoTable(SQLiteDatabase db, String tableName, String[] rowData, Map<String, String> columnMapping) {
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");

        for (String column : columnMapping.keySet()) {
            if (!column.equalsIgnoreCase("id")) {
                query.append(column).append(", ");
            }
        }

        // Remove the last comma and space
        query.setLength(query.length() - 2);
        query.append(") VALUES (");

        for (String value : rowData) {
            query.append("'").append(value).append("', ");
        }

        // Remove the last comma and space
        query.setLength(query.length() - 2);
        query.append(");");

        db.execSQL(query.toString());
    }

    private Map<String, String> getColumnMapping(String tableName) {
        Map<String, String> columnMapping = new HashMap<>();

        switch (tableName) {
            case "TABLE_DRIVER_COMPLAINTS":
                columnMapping.put("vehicleNo", "Vehicle Number");
                columnMapping.put("modelName", "Model Name");
                columnMapping.put("details", "Complaint Details");
                columnMapping.put("remarks", "Remarks");
                columnMapping.put("problemClosed", "Problem Closed");
                columnMapping.put("currentDateTime", "Date and Time");
                break;

            case "TABLE_SERVICING_DETAILS":
                columnMapping.put("vehicleNo", "Vehicle Number");
                columnMapping.put("modelName", "Model Name");
                columnMapping.put("currentDateTime", "Service Date and Time");
                columnMapping.put("runningKm", "Running KM");
                columnMapping.put("nextServiceKm", "Next Service KM");
                columnMapping.put("dieselFilterChange", "Diesel Filter Change");
                columnMapping.put("breakOilChange", "Brake Oil Change");
                columnMapping.put("coolantChange", "Coolant Change");
                columnMapping.put("remark", "Remarks");
                break;

            case "TABLE_MAINTENANCE_DETAILS":
                columnMapping.put("vehicleNo", "Vehicle Number");
                columnMapping.put("modelName", "Model Name");
                columnMapping.put("details", "Maintenance Details");
                columnMapping.put("currentDateTime", "Date and Time");
                break;

            case "TABLE_TYRE_REPAIRS":
                columnMapping.put("vehicleNo", "Vehicle Number");
                columnMapping.put("modelName", "Model Name");
                columnMapping.put("tyreQty", "Tyre Quantity");
                columnMapping.put("alignmentBalancing", "Alignment and Balancing");
                columnMapping.put("alignmentKm", "Alignment KM");
                columnMapping.put("nextAlignmentKm", "Next Alignment KM");
                columnMapping.put("remark", "Remarks");
                columnMapping.put("currentDateTime", "Date and Time");
                break;

            default:
                throw new IllegalArgumentException("Invalid table name: " + tableName);
        }

        return columnMapping;
    }

    private String[] getColumnHeaders(String tableName) {
        switch (tableName) {
            case "TABLE_DRIVER_COMPLAINTS":
                return new String[]{"Vehicle Number", "Model Name", "Complaint Details", "Remarks", "Problem Closed", "Date and Time"};

            case "TABLE_SERVICING_DETAILS":
                return new String[]{"Vehicle Number", "Model Name", "Service Date and Time", "Running KM", "Next Service KM", "Diesel Filter Change", "Brake Oil Change", "Coolant Change", "Remarks"};

            case "TABLE_MAINTENANCE_DETAILS":
                return new String[]{"Vehicle Number", "Model Name", "Maintenance Details", "Date and Time"};

            case "TABLE_TYRE_REPAIRS":
                return new String[]{"Vehicle Number", "Model Name", "Tyre Quantity", "Alignment and Balancing", "Alignment KM", "Next Alignment KM", "Remarks", "Date and Time"};

            default:
                throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
    }
}

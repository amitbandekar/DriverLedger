package com.example.driverledger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.driverledger.DatabaseHelper;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

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
            Document document = new Document(pdfDocument);

            // Set column headers based on the table name
            String[] headers = getColumnHeaders(tableName);
            float[] columnWidths = new float[headers.length];
            for (int i = 0; i < headers.length; i++) {
                columnWidths[i] = 1; // Customize column widths if necessary
            }
            Table table = new Table(columnWidths);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setFont(font)));
            }

            // Fetch data from the database
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            while (cursor.moveToNext()) {
                for (int i = 1; i < cursor.getColumnCount(); i++) { // Skip 'id'
                    table.addCell(new Cell().add(new Paragraph(cursor.getString(i)).setFont(font)));
                }
            }
            cursor.close();

            document.add(table);
            document.close();
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

            // PDF reading logic should be added based on table structure or format
            // (Note: iText 7 doesn't have direct table extraction methods, so custom logic is needed)

            pdfReader.close();
            pdfDocument.close();

        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(context, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error reading PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

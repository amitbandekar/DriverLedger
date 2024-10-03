package com.example.driverledger;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.driverledger.DatabaseHelper;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PDFHandler {

    private DatabaseHelper dbHelper;
    private static final int PICK_PDF_FILE = 2;

    public PDFHandler(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Method to export data from the specified table to a PDF file
    public void exportDataToPDF(String tableName, Context context) {
        // Create a unique PDF file name with current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateTime = sdf.format(new Date());
        String pdfName = "DriverMate_" + currentDateTime + ".pdf";

        // Define the directory
        File directory = new File(Environment.getExternalStorageDirectory() + "/DriverMate");
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        File pdfFile = new File(directory, pdfName);

        // Create PDF document
        try {
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4.rotate());

            // Get the proper heading from the predefined array (HashMap)
            String properHeading = tableNameHeadings.get(tableName);

            // If no heading is found for the table name, use a default value
            if (properHeading == null) {
                properHeading = "Report";
            }

            // Add heading with the user-friendly table name
            Paragraph heading = new Paragraph(properHeading)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20);
            document.add(heading);

            // Set column headers based on the table name
            String[] headers = getColumnHeaders(tableName);
            float[] columnWidths = new float[headers.length];
            for (int i = 0; i < headers.length; i++) {
                columnWidths[i] = 1; // Customize column widths if necessary
            }
            Table table = new Table(UnitValue.createPercentArray(headers.length));
            table.setWidth(UnitValue.createPercentValue(100));

            // Set header row style: Black background, white text
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            Color headerBackgroundColor = new DeviceRgb(0, 0, 0);
            Color headerTextColor = new DeviceRgb(255, 255, 255);

            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header).setFont(font).setFontColor(headerTextColor));
                cell.setBackgroundColor(headerBackgroundColor);
                cell.setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }

            // Fetch data from the database
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);

            // Add table data to PDF
            while (cursor.moveToNext()) {
                for (int i = 1; i < cursor.getColumnCount(); i++) { // Skip 'id'
                    table.addCell(new Cell().add(new Paragraph(cursor.getString(i))));
                }
            }
            cursor.close();

            document.add(table);
            document.close();
            pdfDocument.close();

            // Show SweetAlert success message with 'Open PDF' button
            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success!")
                    .setContentText("Data exported successfully to PDF.")
                    .setConfirmText("Open PDF")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        // Open the PDF using an Intent
                        openPDF(context, pdfFile);
                    })
                    .show();

        } catch (IOException e) {
            e.printStackTrace();
            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText("Error exporting data: " + e.getMessage())
                    .show();
        }
    }

    // HashMap to store user-friendly headings for each table
    private static final Map<String, String> tableNameHeadings = new HashMap<>();
    static {
        tableNameHeadings.put("tblDriverComplaints", "Driver Complaints Report");
        tableNameHeadings.put("tblServicingDetails", "Servicing Details Report");
        tableNameHeadings.put("tblMaintenanceDetails", "Maintenance Details Report");
        tableNameHeadings.put("tblTyreRepairs", "Tyre Repairs Report");
    }

    // Method to open the PDF file using an Intent
    private void openPDF(Context context, File pdfFile) {
        // Get URI using FileProvider
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", pdfFile);

        Intent pdfOpenIntent = new Intent(Intent.ACTION_VIEW);
        pdfOpenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pdfOpenIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenIntent.setDataAndType(uri, "application/pdf");

        try {
            context.startActivity(pdfOpenIntent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where no PDF reader is installed
            Toast.makeText(context, "No PDF viewer installed", Toast.LENGTH_SHORT).show();
        }
    }


    // Column headers for each table
    private String[] getColumnHeaders(String tableName) {
        switch (tableName) {
            case "tblDriverComplaints":
                return new String[]{"Vehicle Number", "Model Name", "Complaint Details", "Remarks", "Problem Closed", "Date and Time"};

            case "tblServicingDetails":
                return new String[]{"Vehicle Number", "Model Name", "Service Date and Time", "Running KM", "Next Service KM", "Diesel Filter Change", "Brake Oil Change", "Coolant Change", "Remarks"};

            case "tblMaintenanceDetails":
                return new String[]{"Vehicle Number", "Model Name", "Maintenance Details", "Date and Time"};

            case "tblTyreRepairs":
                return new String[]{"Vehicle Number", "Model Name", "Tyre Quantity", "Alignment and Balancing", "Alignment KM", "Next Alignment KM", "Remarks", "Date and Time"};

            default:
                throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
    }

    public void importDataFromPDF(String tableName, Activity activity) {
        // Open a file chooser to allow the user to pick a PDF file
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, PICK_PDF_FILE); // Start the activity to pick a PDF
    }

    // Handle the result from the file chooser
    public void handleFilePicked(int requestCode, int resultCode, Intent data, Context context, String tableName) {
        if (requestCode == PICK_PDF_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData(); // Get the Uri of the selected file
            if (uri != null) {
                try {
                    // Open the PDF file from the Uri
                    PdfReader pdfReader = new PdfReader(context.getContentResolver().openInputStream(uri));
                    PdfDocument pdfDocument = new PdfDocument(pdfReader);

                    // TODO: Extract data from the PDF file based on its content.
                    // For demonstration, let's assume we've extracted data into a List<String[]> where each entry represents a row.
                    List<String[]> extractedData = new ArrayList<>();
                    // Simulate extracted data (this should be parsed from the PDF)
                    extractedData.add(new String[]{"V001", "ModelX", "Details...", "Remarks...", "1", "2023-09-03"});
                    extractedData.add(new String[]{"V002", "ModelY", "Details...", "Remarks...", "0", "2023-09-04"});

                    // Now call the insert function to insert extracted data into the corresponding table
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    for (String[] rowData : extractedData) {
                        insertIntoTable(tableName, rowData, db);  // Insert each row into the correct table
                    }

                    db.close();
                    pdfReader.close();
                    pdfDocument.close();

                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setContentText("PDF file imported successfully.")
                            .show();

                } catch (IOException e) {
                    e.printStackTrace();
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Error reading PDF: " + e.getMessage())
                            .show();
                }
            } else {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("No file selected.")
                        .show();
            }
        }
    }

    // Method to insert data into the correct table based on tableName
    private void insertIntoTable(String tableName, String[] rowData, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        switch (tableName) {
            case "tblDriverComplaints":
                values.put("vehicleNo", rowData[0]);
                values.put("modelName", rowData[1]);
                values.put("details", rowData[2]);
                values.put("remarks", rowData[3]);
                values.put("problemClosed", Integer.parseInt(rowData[4]));
                values.put("currentDateTime", rowData[5]);
                db.insert("tblDriverComplaints", null, values);
                break;

            case "tblServicingDetails":
                values.put("vehicleNo", rowData[0]);
                values.put("modelName", rowData[1]);
                values.put("currentDateTime", rowData[2]);
                values.put("runningKm", Integer.parseInt(rowData[3]));
                values.put("nextServiceKm", Integer.parseInt(rowData[4]));
                values.put("dieselFilterChange", Integer.parseInt(rowData[5]));
                values.put("breakOilChange", Integer.parseInt(rowData[6]));
                values.put("coolantChange", Integer.parseInt(rowData[7]));
                values.put("remark", rowData[8]);
                db.insert("tblServicingDetails", null, values);
                break;

            case "tblMaintenanceDetails":
                values.put("vehicleNo", rowData[0]);
                values.put("modelName", rowData[1]);
                values.put("details", rowData[2]);
                values.put("currentDateTime", rowData[3]);
                db.insert("tblMaintenanceDetails", null, values);
                break;

            case "tblTyreRepairs":
                values.put("vehicleNo", rowData[0]);
                values.put("modelName", rowData[1]);
                values.put("tyreQty", Integer.parseInt(rowData[2]));
                values.put("alignmentBalancing", Integer.parseInt(rowData[3]));
                values.put("alignmentKm", Integer.parseInt(rowData[4]));
                values.put("nextAlignmentKm", Integer.parseInt(rowData[5]));
                values.put("remark", rowData[6]);
                values.put("currentDateTime", rowData[7]);
                db.insert("tblTyreRepairs", null, values);
                break;

            default:
                throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
    }
}

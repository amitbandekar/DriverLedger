package com.example.driverledger;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.example.driverledger.DatabaseHelper;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
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

            // Check if the cursor is empty
            if (cursor.getCount() == 0) {
                cursor.close();
                // Show SweetAlert for no data
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("No Data Found")
                        .setContentText("Add data first to export to PDF.")
                        .show();
                return; // Exit the method since there is no data
            }
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
    public void generateVehicleServiceReport(Context context, ArrayList<HashMap<String, String>> dataList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateTime = sdf.format(new Date());
        String pdfName = "DriverMate_VehicleReport_" + currentDateTime + ".pdf";

        // Define the directory
        File directory = new File(Environment.getExternalStorageDirectory() + "/DriverMate");
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        File pdfFile = new File(directory, pdfName);

        try {
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            DeviceRgb primaryColor = new DeviceRgb(63, 81, 181);
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold", true);
            PdfFont regularFont = PdfFontFactory.createFont("Helvetica", true);

            // Header
            Table header = new Table(1).useAllAvailableWidth();
            Cell headerCell = new Cell()
                    .setBackgroundColor(primaryColor)
                    .setPadding(20);

            Paragraph headerText = new Paragraph("Vehicle Service Report")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER);

            headerCell.add(headerText);
            header.addCell(headerCell);
            document.add(header);

            if (!dataList.isEmpty()) {
                HashMap<String, String> data = dataList.get(0);

                // Vehicle Details Section
                document.add(new Paragraph("Vehicle Details").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph().add(new Text("Vehicle Number: ").setFont(boldFont)).add(new Text(data.get("vehicleNo")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Model Name: ").setFont(boldFont)).add(new Text(data.get("modelName")).setFont(regularFont)));

                // Service Information Section
                document.add(new Paragraph("Service Information").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph().add(new Text("Date and Time: ").setFont(boldFont)).add(new Text(data.get("currentDateTime")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Running KM: ").setFont(boldFont)).add(new Text(data.get("runningKm")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Next Service KM: ").setFont(boldFont)).add(new Text(data.get("nextServiceKm")).setFont(regularFont)));

                // Service Details Section
                document.add(new Paragraph("Service Details").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                Table serviceTable = new Table(2).useAllAvailableWidth();
                serviceTable.addCell(new Cell().add(new Paragraph("Diesel Filter Change").setFont(boldFont)));
                serviceTable.addCell(new Cell().add(new Paragraph(data.get("dieselFilterChange").equals("Yes") ? "Yes " : "No").setFont(regularFont)));
                serviceTable.addCell(new Cell().add(new Paragraph("Brake Oil Change").setFont(boldFont)));
                serviceTable.addCell(new Cell().add(new Paragraph(data.get("breakOilChange").equals("Yes") ? "Yes " : "No").setFont(regularFont)));
                serviceTable.addCell(new Cell().add(new Paragraph("Coolant Change").setFont(boldFont)));
                serviceTable.addCell(new Cell().add(new Paragraph(data.get("coolantChange").equals("Yes") ? "Yes " : "No").setFont(regularFont)));
                document.add(serviceTable);

                // Remarks Section
                document.add(new Paragraph("Remarks").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph(data.get("remark")).setFont(regularFont));
            }

            document.close();

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
            // Handle exception (e.g., show an error message)
            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText("Error exporting data: " + e.getMessage())
                    .show();
        }
    }

    public void generateTyreServiceReport(Context context, ArrayList<HashMap<String, String>> dataList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateTime = sdf.format(new Date());
        String pdfName = "DriverMate_TyreServiceReport_" + currentDateTime + ".pdf";

        // Define the directory
        File directory = new File(Environment.getExternalStorageDirectory() + "/DriverMate");
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        File pdfFile = new File(directory, pdfName);

        try {
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            DeviceRgb primaryColor = new DeviceRgb(63, 81, 181);
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold", true);
            PdfFont regularFont = PdfFontFactory.createFont("Helvetica", true);

            // Header
            Table header = new Table(1).useAllAvailableWidth();
            Cell headerCell = new Cell()
                    .setBackgroundColor(primaryColor)
                    .setPadding(20);

            Paragraph headerText = new Paragraph("Tyre Service Report")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER);

            headerCell.add(headerText);
            header.addCell(headerCell);
            document.add(header);

            if (!dataList.isEmpty()) {
                HashMap<String, String> data = dataList.get(0);

                // Vehicle Details Section
                document.add(new Paragraph("Vehicle Details").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph().add(new Text("Vehicle Number: ").setFont(boldFont)).add(new Text(data.get("vehicleNo")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Model Name: ").setFont(boldFont)).add(new Text(data.get("modelName")).setFont(regularFont)));

                // Tyre Service Information Section
                document.add(new Paragraph("Tyre Service Information").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph().add(new Text("Tyre Quantity: ").setFont(boldFont)).add(new Text(data.get("tyreQty")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Alignment & Balancing: ").setFont(boldFont)).add(new Text(data.get("alignmentBalancing").equals("1") ? "Yes" : "No").setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Alignment KM: ").setFont(boldFont)).add(new Text(data.get("alignmentKm")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Next Alignment KM: ").setFont(boldFont)).add(new Text(data.get("nextAlignmentKm")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Date and Time: ").setFont(boldFont)).add(new Text(data.get("currentDateTime")).setFont(regularFont)));

                // Remarks Section
                document.add(new Paragraph("Remarks").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph(data.get("remark")).setFont(regularFont));

                // Date & Time Section
            }

            document.close();

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
            // Handle exception (e.g., show an error message)
            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText("Error exporting data: " + e.getMessage())
                    .show();
        }
    }

    public void generateMaintenanceReport(Context context, ArrayList<HashMap<String, String>> dataList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateTime = sdf.format(new Date());
        String pdfName = "DriverMate_MaintenanceReport_" + currentDateTime + ".pdf";

        // Define the directory
        File directory = new File(Environment.getExternalStorageDirectory() + "/DriverMate");
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        File pdfFile = new File(directory, pdfName);

        try {
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            DeviceRgb primaryColor = new DeviceRgb(63, 81, 181);
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold", true);
            PdfFont regularFont = PdfFontFactory.createFont("Helvetica", true);

            // Header
            Table header = new Table(1).useAllAvailableWidth();
            Cell headerCell = new Cell()
                    .setBackgroundColor(primaryColor)
                    .setPadding(20);

            Paragraph headerText = new Paragraph("Maintenance Report")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER);

            headerCell.add(headerText);
            header.addCell(headerCell);
            document.add(header);

            if (!dataList.isEmpty()) {
                HashMap<String, String> data = dataList.get(0);

                // Vehicle Details Section
                document.add(new Paragraph("Vehicle Details").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph().add(new Text("Vehicle Number: ").setFont(boldFont)).add(new Text(data.get("vehicleNo")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Model Name: ").setFont(boldFont)).add(new Text(data.get("modelName")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Date and Time: ").setFont(boldFont)).add(new Text(data.get("currentDateTime")).setFont(regularFont)));

                // Maintenance Information Section
                document.add(new Paragraph("Maintenance Information").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph().add(new Text("Details: ").setFont(boldFont)).add(new Text(data.get("details")).setFont(regularFont)));

                // Date & Time Section
            }

            document.close();

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
            // Handle exception (e.g., show an error message)
            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText("Error exporting data: " + e.getMessage())
                    .show();
        }
    }

    public void generateDriverComplaintReport(Context context, ArrayList<HashMap<String, String>> dataList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateTime = sdf.format(new Date());
        String pdfName = "DriverMate_ComplaintReport_" + currentDateTime + ".pdf";

        // Define the directory
        File directory = new File(Environment.getExternalStorageDirectory() + "/DriverMate");
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        File pdfFile = new File(directory, pdfName);

        try {
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            DeviceRgb primaryColor = new DeviceRgb(63, 81, 181);
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold", true);
            PdfFont regularFont = PdfFontFactory.createFont("Helvetica", true);

            // Header
            Table header = new Table(1).useAllAvailableWidth();
            Cell headerCell = new Cell()
                    .setBackgroundColor(primaryColor)
                    .setPadding(20);

            Paragraph headerText = new Paragraph("Driver Complaint Report")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER);

            headerCell.add(headerText);
            header.addCell(headerCell);
            document.add(header);

            if (!dataList.isEmpty()) {
                HashMap<String, String> data = dataList.get(0);

                // Vehicle Details Section
                document.add(new Paragraph("Vehicle Details").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph().add(new Text("Vehicle Number: ").setFont(boldFont)).add(new Text(data.get("vehicleNo")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Model Name: ").setFont(boldFont)).add(new Text(data.get("modelName")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Date and Time: ").setFont(boldFont)).add(new Text(data.get("currentDateTime")).setFont(regularFont)));

                // Complaint Information Section
                document.add(new Paragraph("Complaint Information").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph().add(new Text("Details: ").setFont(boldFont)).add(new Text(data.get("details")).setFont(regularFont)));
                document.add(new Paragraph().add(new Text("Remarks: ").setFont(boldFont)).add(new Text(data.get("remarks")).setFont(regularFont)));

                // Problem Closed Section
                document.add(new Paragraph("Problem Status").setFont(boldFont).setFontSize(18).setFontColor(primaryColor).setMarginTop(20));
                document.add(new Paragraph().add(new Text("Problem Closed: ").setFont(boldFont)).add(new Text(data.get("problemClosed")).setFont(regularFont)));

                // Date & Time Section
            }

            document.close();

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
            // Handle exception (e.g., show an error message)
            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error!")
                    .setContentText("Error exporting data: " + e.getMessage())
                    .show();
        }
    }

}

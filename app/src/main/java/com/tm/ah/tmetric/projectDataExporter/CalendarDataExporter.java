package com.tm.ah.tmetric.projectDataExporter;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by AH on 2/10/2018.
 */

public class CalendarDataExporter {

    private PDFMaker pdfMaker;
    private Context context;
    private String dataToConvert;

    public CalendarDataExporter(Context context, PDFMaker pdfMaker, String dataToConvert) {
        this.pdfMaker = pdfMaker;
        this.context = context;
        this.dataToConvert = dataToConvert;
    }

    public void exportAsPDF() {
        if (pdfMaker.write("Project_Data_Export", "content :)")) {
            Toast.makeText(context, "Pdf exported succesfully!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "An error while exporting PDF, please try again!", Toast.LENGTH_LONG).show();
        }

    }
}

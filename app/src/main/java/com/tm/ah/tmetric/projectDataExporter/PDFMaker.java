package com.tm.ah.tmetric.projectDataExporter;

/**
 * Created by AH on 2/10/2018.
 */

public class PDFMaker {

    public Boolean write(String fname, String fcontent) {
            /*
        try {
            //Create file path for Pdf
            String fpath = "/sdcard/" + fname + ".pdf";
            File file = new File(fpath);
            if (!file.exists()) {
                file.createNewFile();
            }
            // create an instance of itext document
            Document document = new Document();
            PdfWriter.getInstance(document,
                    new FileOutputStream(file.getAbsoluteFile()));
            document.open();
            //using add method in document to insert a paragraph
            document.add(new Paragraph("My First Pdf !"));
            document.add(new Paragraph("Hello World"));
            // close document
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
        */
        return true;
    }
}

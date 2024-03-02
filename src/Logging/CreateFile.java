/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author a.gabr
 */
public class CreateFile {

    String FolderLocation = "..\\Logs\\";
    File txtFile;

    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    String currentDate = df.format(new Date());

    public FileOutputStream getFile(String writeDayName) throws IOException {
        Format format = new SimpleDateFormat("EEEE");
        String currentDayName = format.format(new Date());
        FileOutputStream fos = null;

        File folder = new File(FolderLocation);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        txtFile = new File(folder, currentDayName + ".txt");
        
        if (txtFile.createNewFile()) {
            fos = new FileOutputStream(txtFile, true);
            return fos;
        } else {
            BasicFileAttributes attr
                    = Files.readAttributes(txtFile.toPath(), BasicFileAttributes.class);

            FileTime fileTime = attr.creationTime();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            String dateCreated = df.format(fileTime.toMillis());
            
             if (currentDate.equals(dateCreated)) {
            fos = new FileOutputStream(txtFile, true);
        } else {
            fos = new FileOutputStream(txtFile);
        }

        }
        return fos;
    }
}

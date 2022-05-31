package com.example.fitnessappformyself;

import java.io.File;
import java.io.FileOutputStream;

public class ReadFileFormat {
    private final FileFormat fileFormat;
    private final String filePath;

    public ReadFileFormat(FileFormat fileFormat, String filePath){
        this.fileFormat = fileFormat;
        this.filePath = filePath;
    }

    public void ReadFromFile(String filePath){
        File file = new File(filePath);

        StringBuilder text = new StringBuilder();
    }

}

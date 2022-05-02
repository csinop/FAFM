package com.example.fitnessappformyself;

import java.util.ArrayList;

//this class is a custom format
public class FileFormat {

    private final String title;
    private final String spacing;
    private final ArrayList<String> subGroupList;

    //text to be written into the file
    private final String finalString;

    public FileFormat(String title, String spacing, ArrayList<String> subGroupList){
        this.title = title;
        this.spacing = spacing;
        this.subGroupList = subGroupList;

        this.finalString = buildFileStringFromFields();
    }

    public String buildFileStringFromFields(){
        StringBuilder finalString = new StringBuilder(title).append(spacing);

        for(int i = 0; i < subGroupList.size(); i++){
            finalString.append(subGroupList.get(i)).append(spacing);
        }
        finalString.append(spacing);

        return finalString.toString();
    }

    //getters
    public String getFinalString() {
        return finalString;
    }

    public String getTitle() {
        return title;
    }

    public String getSpacing() {
        return spacing;
    }

    public ArrayList<String> getSubGroupList() {
        return subGroupList;
    }
}


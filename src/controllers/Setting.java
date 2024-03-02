/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

/**
 *
 * @author Ahmed Gabr
 */
public class Setting {
    
    String partation ;
    String pdfPath;
    String backupPath;
    String companyName;
    String companyLogo;

    public String getPartation() {
        return partation;
    }

    public void setPartation(String partation) {
        this.partation = partation;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    @Override
    public String toString() {
        return "Setting{" + "partation=" + partation + ", pdfPath=" + pdfPath + ", backupPath=" + backupPath + ", companyName=" + companyName + ", companyLogo=" + companyLogo + '}';
    }
    
    
}

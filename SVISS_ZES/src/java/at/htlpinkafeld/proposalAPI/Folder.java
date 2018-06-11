/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.proposalAPI;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author SVISS_NES
 */
@XmlRootElement
public class Folder {
    String folderName;

    public Folder(String folderName) {
        this.folderName = folderName;
    }

    public Folder() {
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

/**
 *
 * @author SVISS_NES
 */
public class UserOvertimeRow {
    String mitarbeiter;
    double mehrstunden;
    double vormonat;
    double gesamt;

    public UserOvertimeRow(String mitarbeiter, double mehrstunden, double vormonat, double gesamt) {
        this.mitarbeiter = mitarbeiter;
        this.mehrstunden = mehrstunden;
        this.vormonat = vormonat;
        this.gesamt = gesamt;
    }

    public String getMitarbeiter() {
        return mitarbeiter;
    }

    public void setMitarbeiter(String mitarbeiter) {
        this.mitarbeiter = mitarbeiter;
    }

    public double getMehrstunden() {
        return mehrstunden;
    }

    public void setMehrstunden(double mehrstunden) {
        this.mehrstunden = mehrstunden;
    }

    public double getVormonat() {
        return vormonat;
    }

    public void setVormonat(double vormonat) {
        this.vormonat = vormonat;
    }

    public double getGesamt() {
        return gesamt;
    }

    public void setGesamt(double gesamt) {
        this.gesamt = gesamt;
    }

    @Override
    public String toString() {
        return "UserOvertimeRow{" + "mitarbeiter=" + mitarbeiter + ", mehrstunden=" + mehrstunden + ", vormonat=" + vormonat + ", gesamt=" + gesamt + '}';
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

/**
 *
 * @author masix
 */
public enum AbsenceTypeNew {
    HOLIDAY, TIME_COMPENSATION, MEDICAL_LEAVE, BUSINESSRELATED_ABSENCE;

    @Override
    public String toString() {
        switch (this) {
            case HOLIDAY:
                return "Urlaub";
            case MEDICAL_LEAVE:
                return "Krankenstand";
            case TIME_COMPENSATION:
                return "Zeitausgleich";
            case BUSINESSRELATED_ABSENCE:
                return "unternehmensbedingte Abwesenheit";
            default:
                return super.toString();
        }
    }

    public String getMyString() {
        switch (this) {
            case HOLIDAY:
                return "Urlaub";
            case MEDICAL_LEAVE:
                return "Krankenstand";
            case TIME_COMPENSATION:
                return "Zeitausgleich";
            case BUSINESSRELATED_ABSENCE:
                return "unternehmensbedingte Abwesenheit";
            default:
                return super.toString();
        }
    }

}

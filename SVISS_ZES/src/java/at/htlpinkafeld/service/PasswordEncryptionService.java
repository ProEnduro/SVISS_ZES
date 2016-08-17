/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Martin Six
 */
public class PasswordEncryptionService {

    public static String digestPassword(String s) {
        return DigestUtils.sha512Hex(s);
    }
}

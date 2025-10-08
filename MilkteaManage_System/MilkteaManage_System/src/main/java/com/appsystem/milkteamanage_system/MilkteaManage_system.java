/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.appsystem.milkteamanage_system;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Admin
 */
public class MilkteaManage_system {

    public static void main(String[] args) {
        String password = "Trasuangon123!";
        String storedHash = "$2a$12$K3if8MfW36Km.C.YNt7ucu0NhUnLda50wV.l8j/HpQtdVg3Jl.v1S                                                                                                                                            "; // Thay bằng chuỗi băm từ cơ sở dữ liệu
        boolean isMatch = BCrypt.checkpw(password, storedHash);
        System.out.println("Password matches: " + isMatch);
    }
}

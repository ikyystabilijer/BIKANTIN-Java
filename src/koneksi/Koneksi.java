/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ikyys
 */

public class Koneksi {

    public static Connection getKoneksi() {
        try {
            String url = "jdbc:mysql://localhost:3306/db_bikantin";
            String user = "root";
            String password = "";

            return DriverManager.getConnection(
                url, user, password
            );

        } catch (SQLException e) {
            System.out.println(
                "Koneksi gagal: " + e.getMessage()
            );
            return null;
        }
    }
}



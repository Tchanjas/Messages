/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Clara
 */
public class ThreadAuth extends Thread {

    String dbDriver = "org.apache.derby.jdbc.ClientDriver";
    String dbUrl = "jdbc:derby://localhost:1527/Messages;autoReconnect=true'";

    Socket socket;
    Connection conn;
    ObjectOutputStream out;
    ObjectInputStream in;

    public ThreadAuth(Socket socket) {
        try {
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            conn = DriverManager.getConnection(dbUrl);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(ThreadAuth.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        try {
            String message = in.readObject().toString();
            String[] fields = message.split(",");
            String username = fields[1];
            String hash = fields[2];
            try {
                if (message.startsWith("register")) {
                    register(username, hash);
                } else if (message.startsWith("authenticate")) {
                    authenticate(username, hash);
                }
                conn.close();
                socket.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception ex) {
            Logger.getLogger(ThreadAuth.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void register(String username, String hash) throws IOException {
        try {
            Class.forName(dbDriver).newInstance();
            Statement stmnt = conn.createStatement();
            ResultSet existingUser = stmnt.executeQuery("select * from APP.USERS where USERNAME like '" + username + "'");
            if (!existingUser.next()) {
                stmnt.execute("insert into APP.USERS(USERNAME,PASSWORD) values('" + username + "','" + hash + "')");
                out.writeObject("1"); //Succesfull code
            } else {
                out.writeObject("0"); //Error code
            }
            stmnt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void authenticate(String username, String hash) throws IOException {
        try {
            Class.forName(dbDriver).newInstance();
            Connection conn = DriverManager.getConnection(dbUrl);
            Statement stmnt = conn.createStatement();
            ResultSet existingUser = stmnt.executeQuery("select * from APP.USERS where USERNAME like '" + username
                    + "' and PASSWORD like '" + hash + "'");
            if (existingUser.next()) {
                out.writeObject("3"); //Succesful code
            } else {
                out.writeObject("2"); //Error code
            }
            stmnt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}

package Server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements ServerInterface {

    String dbDriver = "org.apache.derby.jdbc.ClientDriver";
    static String dbUrl = "jdbc:derby://localhost:1527/Messages;autoReconnect=true'";
    static Connection conn;

    public static void main(String args[]) {
        try {
            // connnect to database
            conn = DriverManager.getConnection(dbUrl);

            Server obj = new Server();
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(10000);
            registry.bind("Server", stub);

            System.out.println("Server running");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public boolean register(String username, String password) throws RemoteException {
        boolean result = false;
        
        try {
            Class.forName(dbDriver).newInstance();
            Statement stmnt = conn.createStatement();
            ResultSet existingUser = stmnt.executeQuery("select * from APP.USERS where USERNAME like '" + username + "'");
            if (!existingUser.next()) {
                stmnt.execute("insert into APP.USERS(USERNAME,PASSWORD) values('" + username + "','" + password + "')");
                result = true;
                System.out.println("User " + username +  " created at " + new Date());
            } else {
                result = false;
            }
            stmnt.close();
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

    @Override
    public boolean authenticate(String username, String password) throws RemoteException {
        boolean result = false;
        
        try {
            Class.forName(dbDriver).newInstance();
            Connection conn = DriverManager.getConnection(dbUrl);
            Statement stmnt = conn.createStatement();
            ResultSet existingUser = stmnt.executeQuery("select * from APP.USERS where USERNAME like '" + username
                    + "' and PASSWORD like '" + password + "'");
            if (existingUser.next()) {
                result = true;
                System.out.println("User " + username +  " logged at " + new Date());
            } else {
                result = false;
            }
            stmnt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        return result;
    }
}

package Server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements ServerInterface {

    String dbDriver = "org.apache.derby.jdbc.ClientDriver";
    static String dbUrl = "jdbc:derby://localhost:1527/Messages;autoReconnect=true'";
    static Connection conn;
    ConcurrentHashMap<String, String> onlineUsers = new ConcurrentHashMap<>();

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
        } catch (AlreadyBoundException | RemoteException | SQLException e) {
            System.err.println("Server exception: " + e.toString());
        }
    }

    /**
     * This method will function aswell as a keepAlive for each client
     */
    @Override
    public Object onlineFriends(String username) throws RemoteException {
        HashMap users = new HashMap();
        try {
            Class.forName(dbDriver).newInstance();
            Statement stmnt = conn.createStatement();
            ResultSet friends = stmnt.executeQuery("select u.USERNAME "
                    + "from USERS u "
                    + "where u.ID in(select f.IDFRIEND "
                    + "from FRIENDS f "
                    + "where f.IDUSER in (select u.ID "
                    + "from USERS u "
                    + "where USERNAME like '" + username + "'))");
            while (friends.next()) {
                if (onlineUsers.get(friends.getString("USERNAME")) != null) {
                    users.put(friends.getString("USERNAME"), onlineUsers.get(friends.getString("USERNAME")));
                }
            }
            stmnt.close();
            return users;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return users;
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
                System.out.println("User " + username + " created at " + new Date());
            } else {
                result = false;
            }
            stmnt.close();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }

    @Override
    public boolean authenticate(String username, String password, String ip, String port) throws RemoteException {
        boolean result = false;
        try {
            Class.forName(dbDriver).newInstance();
            Connection conn = DriverManager.getConnection(dbUrl);
            Statement stmnt = conn.createStatement();
            ResultSet existingUser = stmnt.executeQuery("select * from APP.USERS where USERNAME like '" + username
                    + "' and PASSWORD like '" + password + "'");
            if (existingUser.next()) {
                result = true;
                System.out.println("User " + username + " logged at " + new Date());
                onlineUsers.put(username, ip + ":" + port);
            } else {
                result = false;
            }
            stmnt.close();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}

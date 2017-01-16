package Server;

import Utils.BCrypt;
import Utils.Crypto;
import java.io.UnsupportedEncodingException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements ServerInterface {

    String dbDriver = "org.apache.derby.jdbc.ClientDriver";
    static String dbUrl = "jdbc:derby://localhost:1527/Messages;autoReconnect=true'";
    static Connection conn;
    ConcurrentHashMap<String, List<String>> onlineUsers = new ConcurrentHashMap<String, List<String>>();
    ConcurrentHashMap<String, Key> sessionKeys = new ConcurrentHashMap<String, Key>();
    static KeyPair keys;

    public static void main(String args[]) {
        try {
            // connnect to database
            conn = DriverManager.getConnection(dbUrl);

            Server obj = new Server();
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(10000);
            registry.bind("Server", stub);

            keys = Crypto.generateKeypair("RSA");
            System.out.println("Server running");
        } catch (AlreadyBoundException | RemoteException | SQLException e) {
            System.err.println("Server exception: " + e.toString());
        }
    }

    @Override
    public PublicKey getPublicKey() throws RemoteException {
        return keys.getPublic();
    }

    /**
     * This method will function aswell as a keepAlive for each client
     */
    @Override
    public HashMap onlineFriends(String username) throws RemoteException {
        HashMap users = new HashMap();
        try {
            long currentTimeStamp = System.currentTimeMillis() / 1000;
            if (onlineUsers.get(username) != null) {
                onlineUsers.get(username).set(2, currentTimeStamp + "");
            }

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
                    long timeStamp = Long.parseLong(onlineUsers.get(friends.getString("USERNAME")).get(2));

                    if ((currentTimeStamp - timeStamp) < 15) {
                        users.put(friends.getString("USERNAME"), onlineUsers.get(friends.getString("USERNAME")));
                    } else {
                        onlineUsers.remove(friends.getString("USERNAME"));
                    }
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

            byte[] newUser = Base64.getDecoder().decode(username);
            byte[] newPass = Base64.getDecoder().decode(password);

            username = new String(Crypto.decypher(newUser, keys.getPrivate()), "UTF-8");
            password = new String(Crypto.decypher(newPass, keys.getPrivate()), "UTF-8");

            ResultSet existingUser = stmnt.executeQuery("select * "
                    + "from APP.USERS "
                    + "where USERNAME like '" + username + "'");
            if (!existingUser.next()) {
                stmnt.execute("insert into APP.USERS(USERNAME,PASSWORD) values('" + username + "','" + password + "')");
                result = true;
                System.out.println("User " + username + " created at " + new Date());
            } else {
                result = false;
            }
            stmnt.close();
        } catch (Exception ex) {
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

            byte[] authUser = Base64.getDecoder().decode(username);
            byte[] authPass = Base64.getDecoder().decode(password);
            byte[] authIP = Base64.getDecoder().decode(ip);
            byte[] authPort = Base64.getDecoder().decode(port);

            username = new String(Crypto.decypher(authUser, keys.getPrivate()), "UTF-8");
            password = new String(Crypto.decypher(authPass, keys.getPrivate()), "UTF-8");
            ip = new String(Crypto.decypher(authIP, keys.getPrivate()), "UTF-8");
            port = new String(Crypto.decypher(authPort, keys.getPrivate()), "UTF-8");

            ResultSet existingUser = stmnt.executeQuery("select * "
                    + "from APP.USERS "
                    + "where USERNAME like '" + username + "'");

            if (existingUser.next()) {
                String hashCheck = existingUser.getString("PASSWORD");
                if (BCrypt.checkpw(password, hashCheck)) {
                    result = true;
                    System.out.println("User " + username + " at " + ip + ":" + port + " logged at " + new Date());
                    List<String> list = new ArrayList<>();
                    list.add(ip);
                    list.add(port);
                    long unixTime = System.currentTimeMillis() / 1000;
                    list.add(unixTime + "");
                    if (onlineUsers.get(username) != null) {
                        onlineUsers.remove(username);
                    }
                    onlineUsers.put(username, list);
                } else {
                    result = false;
                }
            } else {
                result = false;
            }
            stmnt.close();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public boolean addFriend(String user, String friend) throws RemoteException {
        try {
            Class.forName(dbDriver).newInstance();
            Connection conn = DriverManager.getConnection(dbUrl);
            Statement stmnt = conn.createStatement();
            ResultSet result = stmnt.executeQuery("SELECT id "
                    + "FROM USERS "
                    + "where username "
                    + "like '" + user + "' or username like '" + friend + "'");
            if (result.next()) {
                int idUser = Integer.parseInt(result.getString("ID"));
                if (result.next()) {
                    int idFriend = Integer.parseInt(result.getString("ID"));
                    result = stmnt.executeQuery("select * "
                            + "from friends "
                            + "where IDUSER = " + idUser + " and IDFRIEND = " + idFriend + "");
                    if (result.next()) {
                        return false;
                    } else {
                        stmnt.execute("insert into APP.FRIENDS(IDUSER,IDFRIEND) values(" + idUser + "," + idFriend + ")");
                        stmnt.execute("insert into APP.FRIENDS(IDUSER,IDFRIEND) values(" + idFriend + "," + idUser + ")");
                        return true;
                    }
                } else {
                    return false;
                }
            }
            stmnt.close();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public void addSessionKey(String username, Key sessionKey) throws RemoteException {
        try {
            sessionKeys.put(username, sessionKey);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

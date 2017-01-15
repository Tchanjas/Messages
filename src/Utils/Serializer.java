/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Clara
 */
public class Serializer {

    public static byte[] toBytes(Object obj) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(obj);
            return byteOut.toByteArray();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static Object toObject(byte[] data) {
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            return objIn.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

}

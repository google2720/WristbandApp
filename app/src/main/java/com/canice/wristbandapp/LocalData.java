package com.canice.wristbandapp;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class LocalData {

    @SuppressWarnings("unchecked")
    public static <T> T get(Context context, String key, Class<T> c) {
        ObjectInputStream in = null;
        try {
            File file = context.getFileStreamPath(key);
            in = new ObjectInputStream(new FileInputStream(file));
            Object o = in.readObject();
            if (o != null && c.isInstance(o)) {
                return (T) o;
            }
        } catch (Exception e) {
            // ignore
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return null;
    }

    public static boolean save(Context context, String key, Serializable obj) {
        ObjectOutputStream out = null;
        try {
            File file = context.getFileStreamPath(key);
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(obj);
            out.flush();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}

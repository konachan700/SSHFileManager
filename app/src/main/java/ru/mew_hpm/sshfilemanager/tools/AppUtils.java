package ru.mew_hpm.sshfilemanager.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppUtils {
    public static final String InputStreamToString(InputStream stream) {
        final InputStreamReader ir = new InputStreamReader(stream);
        final BufferedReader br = new BufferedReader(ir);
        final StringBuilder scriptSB = new StringBuilder();

        String line;

        try {
            while ((line = br.readLine()) != null) {
                scriptSB.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ir.close();
            br.close();
        } catch (IOException e) { }

        return scriptSB.toString();
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}

package org.example;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Vérifie si le nombre d'arguments est correct
        if (args.length < 6) {
            System.out.println("Usage: java -jar yourapp.jar <server> <port> <user> <pass> <remoteDirPath> <saveDirPath>");
            return;
        }

        // Lecture des arguments
        String server = args[0];
        int port = Integer.parseInt(args[1]);
        String user = args[2];
        String pass = args[3];
        String remoteDirPath = args[4];
        String saveDirPath = args[5];

        try(FileDownloader fileDownloader = new FileDownloader(server,port, user, pass,"files.db")) {
            fileDownloader.downloadDirectory("/files/Finis", "test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
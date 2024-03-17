package org.example;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class FileDownloader implements AutoCloseable{
    private final FTPClient ftpClient;
    private final SQLiteFile sqliteFile;

    public FileDownloader(String server, int port, String user, String pass,String sqlFilename) throws IOException, SQLException {
        this.ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        sqliteFile = new SQLiteFile(sqlFilename);
    }

    public FileDownloader(FTPClient ftpClient) throws SQLException {
        this.ftpClient = ftpClient;
        sqliteFile = new SQLiteFile();
    }

    public void downloadDirectory(String remoteDirPath, String localSaveDirPath) throws IOException {
        FTPFile[] subFiles = ftpClient.listFiles(remoteDirPath);

        for (FTPFile aFile : subFiles) {
            String remoteFilePath = remoteDirPath + "/" + aFile.getName();
            Path localFilePath = Paths.get(localSaveDirPath, aFile.getName());

            if (aFile.isDirectory()) {
                // Assurez-vous que le dossier local existe ou créez-le
                if (!Files.exists(localFilePath)) {
                    Files.createDirectories(localFilePath);
                    System.out.println("Le dossier " + localFilePath + " a été créé avec succès.");
                }
                // Appel récursif pour le sous-dossier
                downloadDirectory(remoteFilePath, localFilePath.toString());
            } else {
                if(!sqliteFile.fileNameExists(aFile.getName()))
                {// Téléchargement du fichier
                    System.out.println("Début du téléchargement du fichier: " + aFile.getName());
                    if (downloadSingleFile(remoteFilePath, localFilePath)) {
                        this.sqliteFile.saveFileName(aFile.getName());
                        System.out.println("Le fichier " + aFile.getName() + " a été téléchargé avec succès.");
                    } else {
                        System.out.println("Erreur lors du téléchargement du fichier " + aFile.getName());
                    }
                } else {
                    System.out.println("Le fichier " + aFile.getName() + " a déjà été téléchargé.");
                }
            }
        }
    }

    private boolean downloadSingleFile(String remoteFilePath, Path savePath) throws IOException {
        Files.createDirectories(savePath.getParent());

        try (var outputStream = Files.newOutputStream(savePath)) {
            return ftpClient.retrieveFile(remoteFilePath, outputStream);
        }
    }



    @Override
    public void close() throws Exception {
        if(ftpClient != null) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }
}

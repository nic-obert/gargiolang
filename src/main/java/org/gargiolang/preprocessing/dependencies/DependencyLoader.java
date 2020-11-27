package org.gargiolang.preprocessing.dependencies;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DependencyLoader {

    private File path = new File("GargioLang", "libraries");

    public DependencyLoader(){
        if(!path.exists()){
            if(!path.mkdirs()) System.out.println("Failed to create dirs at " + path.getPath());
        }
    }

    public void loadDependency(String name) {
        path = new File(path.getAbsolutePath(), name);
        path.mkdirs();
        File f = new File(path, "library.zip");

        if(!f.exists()){
            String u = "http://searchforme.cf/gargiolang/libraries/" + name + "/library.zip";

            System.out.println("Couldn't find dependency " + name + " in the local files (" + f.getAbsolutePath() + "). " +
                    "Downloading it from " + u + "...");

            try {
                URL website = new URL(u);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                File outputFile = new File(path, "library.zip");
                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                System.out.println("Downloaded dependency " + name);

                System.out.println("Unpacking zip archive...");
                {
                    ZipFile zipFile = new ZipFile(outputFile);
                    Enumeration<?> enu = zipFile.entries();
                    while (enu.hasMoreElements()) {
                        ZipEntry zipEntry = (ZipEntry) enu.nextElement();

                        String zipName = zipEntry.getName();
                        long size = zipEntry.getSize();
                        long compressedSize = zipEntry.getCompressedSize();
                        System.out.println("name: " + zipName + ", size: " + size + ", compressedSize:" + compressedSize);

                        File file = new File(zipName);
                        if (zipName.endsWith("/")) {
                            file.mkdirs();
                            continue;
                        }

                        File parent = file.getParentFile();
                        if (parent != null) {
                            parent.mkdirs();
                        }

                        InputStream is = zipFile.getInputStream(zipEntry);
                        FileOutputStream outputStream = new FileOutputStream(new File(path, file.getName()));
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = is.read(bytes)) >= 0) {
                            outputStream.write(bytes, 0, length);
                        }
                        is.close();
                        outputStream.close();

                    }
                    zipFile.close();
                    System.out.println("Unpacked " + name + " successfully.");
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            System.out.println("Found dependency: " + name);
        }
    }

}

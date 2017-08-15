package me.rowkey.pje.advancejava.newjava;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.AccessException;

/**
 * Created by Bryant.Hang on 2017/8/6.
 */
public class Java7Example {
    public static void main(String[] args) throws IOException {
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(""))) {
                br.read();
            }
            testJava7();
        } catch (FileNotFoundException | AccessException e) {
            e.printStackTrace();
        }

        String type = "text";
        switch (type) {
            case "text":
                System.out.println("text type");
                break;
            case "image":
                System.out.println("image type");
                break;
        }
    }

    public static void testJava7() throws IOException {
        Path path = Paths.get("/data/test.dat");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Path path1 = FileSystems.getDefault().getPath("/data", "test.dat");

        try {
//            Charset.forName("GBK")
            BufferedReader reader = Files.newBufferedReader(path1, StandardCharsets.UTF_8);
            String str = null;
            while ((str = reader.readLine()) != null) {
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

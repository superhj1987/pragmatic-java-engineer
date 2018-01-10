package me.rowkey.pje.advancejava.newjava;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by Bryant.Hang on 2017/8/6.
 */
public class Java7Example {

    public static void tryWith() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(""))) {
            br.read();
        }
    }

    public static void stringSwitch() {

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

    public static void watchService() throws IOException {

        Path path = Paths.get("/Users/BryantHang/testhj");
        Files.readAllLines(path)
                .forEach(System.out::println);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Paths.get("/Users/BryantHang").register(watchService, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);

        Executors.newSingleThreadExecutor().execute(() -> {
            while (true) {
                // 等待直到获得事件信号
                WatchKey signal;
                try {
                    signal = watchService.take();
                } catch (InterruptedException x) {
                    return;
                }

                for (WatchEvent<?> event : signal.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // TBD - provide example of how OVERFLOW event is handled
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> ev = (WatchEvent<Path>) (event);
                    if (kind == ENTRY_MODIFY) {
                        System.out.println(ev.context().getFileName() + "content changed");
                    }
                }
                //    为监控下一个通知做准备
                signal.reset();
            }

        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    watchService.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        while (true) ;
    }

    public static void main(String[] args) throws IOException {

        watchService();
    }
}

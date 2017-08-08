package me.rowkey.pje.javaadvance.newjava;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Bryant.Hang on 2017/8/6.
 */
public class Java8Test {
    public static int cal(int loop) {
        while (loop-- > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return 100;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(() ->
                cal(10))
                .thenCompose((i) -> CompletableFuture.supplyAsync(() -> cal(i)))
                .thenApply((i) -> Integer.toString(i))
                .thenApply((str) -> "result : " + str)
                .thenAccept(System.out::println)
                .get();
    }
}

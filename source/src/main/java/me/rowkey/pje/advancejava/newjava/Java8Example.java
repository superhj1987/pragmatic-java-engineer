package me.rowkey.pje.advancejava.newjava;

import me.rowkey.pje.common.meta.TestUser;
import me.rowkey.pje.common.meta.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Bryant.Hang on 2017/8/6.
 */
public class Java8Example {
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

    public static void completableFuture() throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(() ->
                cal(10))
                .thenCompose((i) -> CompletableFuture.supplyAsync(() -> cal(i)))
                .thenApply((i) -> Integer.toString(i))
                .thenApply((str) -> "result : " + str)
                .thenAccept(System.out::println)
                .get();
    }

    public static void main(String[] args) {

        User user = new User();
        user.setName("testUser");

        Optional<User> optional = Optional.of(user);
        user = optional.orElse(new User());
        user = optional.orElseThrow(RuntimeException::new);
        user = optional.orElseGet(User::new);

        Optional<TestUser> testUserOptional =
                optional.filter(u -> u.getName() != null)
                        .map(u -> {
                            TestUser testUser = new TestUser();
                            testUser.setName(u.getName());
                            return testUser;
                        });

        Optional<User> userOptional = testUserOptional.flatMap(tu -> {
            User curUser = new User();
            curUser.setName(tu.getName());

            return Optional.of(curUser);
        });
    }
}

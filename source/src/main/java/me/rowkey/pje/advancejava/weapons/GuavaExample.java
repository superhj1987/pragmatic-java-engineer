package me.rowkey.pje.advancejava.weapons;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.*;

import javax.swing.event.ChangeEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Guava使用示例
 */
public class GuavaExample {

    public static void basic() {

        String str = ",a,,b,";

        Optional<String> optional = Optional.fromNullable(str);
        if (optional.isPresent()) {
            System.out.println(optional.get());
        }
        str = optional.or("default string");
        str = optional.or(new Supplier<String>() {
            @Override
            public String get() {
                return "default string";
            }
        });
        str = optional.orNull();
        optional.transform(new Function<String, Object>() {
            @Override
            public Object apply(String input) {
                return "transformed string";
            }
        });

        str = MoreObjects.firstNonNull(str, "");

        System.out.println(Strings.repeat("str", 3));

        System.out.println(
                CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "CONSTANT_NAME")); // returns "constantName"

        Joiner joiner = Joiner.on("; ").skipNulls();
        joiner.join("Harry", null, "Ron", "Hermione");

        Splitter.on(';')
                .trimResults()
                .omitEmptyStrings()
                .split("1;2;3;4");

        Preconditions.checkArgument(!Strings.isNullOrEmpty(str), "user null error");
    }

    public static void collection() {
        ImmutableList<String> list = ImmutableList.of("1", "2", "3");

        Multiset<String> set = HashMultiset.create();
        set.add("1");
        set.add("1");
        set.count("1");

        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("test", "1");
        multimap.put("test", "2");
        multimap.get("test");
    }

    public static void future() {
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
        ListenableFuture future = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "result";
            }
        });
        Futures.transform(future, new AsyncFunction() {
            @Override
            public ListenableFuture apply(Object input) throws Exception {
                return null;
            }
        }, new Executor() {
            @Override
            public void execute(Runnable command) {

            }
        });

        Futures.addCallback(future, new FutureCallback() {
            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public static void eventBus() {
        EventBus eventBus = new EventBus();
        eventBus.register(new EventBusListener());

        eventBus.post(new ChangeEvent("a event"));
    }
}

class EventBusListener {

    @Subscribe
    public void recordCustomerChange(ChangeEvent e) {
        System.out.println(e.getSource());
    }
}


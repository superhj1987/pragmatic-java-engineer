package me.rowkey.pje.javaadvance.weapons;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.event.ChangeEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Bryant.Hang on 2017/8/6.
 */
public class GuavaTest {
    public void a() {
        Multiset<String> set = HashMultiset.create();
        set.add("1");
        set.add("1");
        System.out.println(set.count("1"));

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

    public static void maina(String[] args) {
        String str = ",a,,b,";
        String[] tmp = str.split(",");
        tmp = StringUtils.split(str, ",");

        System.out.println(StringEscapeUtils.escapeHtml4("<span>aa</span>"));

        Optional<String> optional = Optional.fromNullable(str);
        if (optional.isPresent()) {
            System.out.println(optional.get());
        }

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

        Preconditions.checkArgument("" != null, "user null error");

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

    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        eventBus.register(new EventBusListener());

        eventBus.post(new ChangeEvent("aa"));
    }
}

class EventBusListener {

    @Subscribe
    public void recordCustomerChange(ChangeEvent e) {
        System.out.println(e.getSource());
    }
}


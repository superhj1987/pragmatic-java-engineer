package me.rowkey.pje.datatrans.message;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DisruptorExample {
    public void test() throws Exception {
        TestEventFactory factory = new TestEventFactory();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        Disruptor<TestEvent> disruptor = new Disruptor<>(factory, 1024, threadFactory);

        disruptor.handleEventsWith(new TestEventHandler());

        disruptor.start();

        RingBuffer<TestEvent> ringBuffer = disruptor.getRingBuffer();

        TestEventProducerWithTranslator producer = new TestEventProducerWithTranslator(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            producer.onData(bb);
            Thread.sleep(1000);
        }
    }
}

class TestEventProducer {
    private final RingBuffer<TestEvent> ringBuffer;

    public TestEventProducer(RingBuffer<TestEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer bb) {
        long sequence = ringBuffer.next();  // Grab the next sequence
        try {
            TestEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor
            // for the sequence
            event.set(bb.getLong(0));  // Fill with data
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}


class TestEventProducerWithTranslator {
    private final RingBuffer<TestEvent> ringBuffer;

    public TestEventProducerWithTranslator(RingBuffer<TestEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<TestEvent, ByteBuffer> TRANSLATOR =
            new EventTranslatorOneArg<TestEvent, ByteBuffer>() {
                public void translateTo(TestEvent event, long sequence, ByteBuffer bb) {
                    event.set(bb.getLong(0));
                }
            };

    public void onData(ByteBuffer bb) {
        ringBuffer.publishEvent(TRANSLATOR, bb);
    }
}

class TestEvent {
    private long value;

    public void set(long value) {
        this.value = value;
    }
}

class TestEventFactory implements EventFactory<TestEvent> {
    public TestEvent newInstance() {
        return new TestEvent();
    }
}

class TestEventHandler implements EventHandler<TestEvent> {
    public void onEvent(TestEvent event, long sequence, boolean endOfBatch) {
        System.out.println("Event: " + event);
    }
}
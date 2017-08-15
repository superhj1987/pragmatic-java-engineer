package me.rowkey.pje.datatrans.message;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Bryant.Hang on 2017/8/5.
 */
public class KafkaExample {
    public void producer() {
        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");
        props.put("request.required.acks", "0");
        props.put("request.timeout.ms", "10000");
        props.put("producer.type", "sync");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("partitioner.class", "kafka.producer.DefaultPartitioner");
        props.put("compression.codec", "none");
        props.put("message.send.max.retries", 10);
        props.put("retry.backoff.ms", 100);
        props.put("topic.metadata.refresh.interval.ms", 600 * 1000);
        props.put("send.buffer.bytes", 5000);

        String topic = "test_topic";

        Producer<String, String> producer = new Producer<>(new ProducerConfig(props));
        KeyedMessage<String, String> message = new KeyedMessage<>(topic, "aa");
        producer.send(message);
    }

    public static void consumer() {
        Properties props = new Properties();
        props.put("zookeeper.connect", "zk1.dmp.com:2181,zk2.dmp.com:2181,zk3.dmp.com:2181");
        props.put("zookeeper.session.timeout.ms", "3000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("group.id", "test_group");
        props.put("auto.commit.interval.ms", "600");

        String topic = "test_topic";
        ConsumerConnector connector = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        Map<String, Integer> topics = new HashMap<String, Integer>();
        int partitionNum = 3;//分区数目
        topics.put(topic, partitionNum);
        Map<String, List<KafkaStream<byte[], byte[]>>> streams = connector.createMessageStreams(topics);
        List<KafkaStream<byte[], byte[]>> partitions = streams.get(topic);
        Executor threadPool = Executors.newFixedThreadPool(partitionNum);
        for (final KafkaStream<byte[], byte[]> partition : partitions) {
            threadPool.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            ConsumerIterator<byte[], byte[]> it = partition.iterator();
                            while (it.hasNext()) {
                                MessageAndMetadata<byte[], byte[]> item = it.next();
                                byte[] messageBody = item.message();
                            }
                        }
                    });
        }
    }
}

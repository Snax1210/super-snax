package org.snax.supersnax.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.LoggingCommitCallback;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

/**
 * @author maoth
 * @date 2022/1/5 21:59
 * @description
 */
@Service
public class TimerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerService.class);

    String preTime = null;

    int i = 0;

    public void getNextSecondData() throws InterruptedException {
        KafkaProducer<String, String> producer = getProvider();

        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.51:9093");
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        p.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(p);
        kafkaConsumer.subscribe(Collections.singleton("20210330_Transaction_202201061145"));
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                //                LOGGER.info("get message from kafka topic is : {},partitionis : {},offset is : {},
                //                value is : {}",
                //                    record.topic(),
                //                    record.partition(),
                //                    record.offset(),
                //                    record.value());
                JSONObject json = JSON.parseObject(record.value(), JSONObject.class);
                String nowTime = json.get("LocalTime").toString().split("\\.")[0];
                if (preTime == null) {
                    preTime = nowTime;
                    LOGGER.info("preTime is : {}", preTime);

                }

                if (nowTime.equals(preTime)) {
                    producer.send(new ProducerRecord<>("20210330_Transaction_202201061316_with_time", record.value()));
                    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>(16);
                    TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
                    OffsetAndMetadata offsetsAndMetadata = new OffsetAndMetadata(record.offset());
                    offsets.put(topicPartition, offsetsAndMetadata);
                    OffsetCommitCallback callback = new LoggingCommitCallback();
                    callback.onComplete(offsets, null);
                    kafkaConsumer.commitAsync(callback);
                } else {
                    LOGGER.info("nowTime is : {}", nowTime);
                    preTime = nowTime;
                    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>(16);
                    TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
                    OffsetAndMetadata offsetsAndMetadata = new OffsetAndMetadata(record.offset());
                    offsets.put(topicPartition, offsetsAndMetadata);
                    OffsetCommitCallback callback = new LoggingCommitCallback();
                    callback.onComplete(offsets,null);
                    kafkaConsumer.commitAsync(callback);
                    break;
                }
            }
        }
    }

    private KafkaProducer<String, String> getProvider() {
        Properties props = new Properties();
        //xxx服务器ip
        props.put("bootstrap.servers", "192.168.200.51:9093");
        //所有follower都响应了才认为消息提交成功，即"committed"
        props.put("acks", "all");
        //retries = MAX 无限重试，直到你意识到出现了问题:)
        props.put("retries", 0);
        props.put("batch.size", 16384);//producer将试图批处理消息记录，以减少请求次数.默认的批量处理消息字节数
        //batch.size当批量的数据大小达到设定值后，就会立即发送，不顾下面的linger.ms
        props.put("linger.ms", 1);//延迟1ms发送，这项设置将通过增加小的延迟来完成--即，不是立即发送一条记录，producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理
        props.put("buffer.memory", 33554432);//producer可以用来缓存数据的内存大小。
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<String, String>(props);
    }
}

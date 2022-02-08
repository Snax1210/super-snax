package org.snax.supersnax.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.snax.supersnax.mapper.UserDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;

/**
 * @author maoth
 * @date 2021/10/8 16:20
 * @description
 */
@Component
public class Provider {
    @Resource
    UserDao userDao;

    private final KafkaProducer<String, String> producer;

    public final static String TOPIC = "spider_db_cyb";

    Provider() {
        Properties props = new Properties();
        //xxx服务器ip
        props.put("bootstrap.servers", "192.168.4.53:9092");
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
        producer = new KafkaProducer<String, String>(props);
    }

    public void produce() {
        int offset = 0;
        while (offset < 10000) {
            List<String> messageList = userDao.getMessages(offset);
            for (String message : messageList) {
                producer.send(new ProducerRecord<>(TOPIC, message));
            }
            System.out.println(offset);
            offset += 1000;
        }
        producer.close();
    }

    public void produceSingle(String topic, String message) {
        producer.send(new ProducerRecord<>(topic, message));
    }

}

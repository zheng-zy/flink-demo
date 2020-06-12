package com.example.job;

import com.example.config.NacosConfig;
import com.example.function.InsertMapFunction;
import com.example.function.RedisSinkFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.example.config.Constant.DATA_ID;
import static com.example.config.Constant.GROUP_BASE_SERVICE;

/**
 * <p></p>
 * Created by @author zhezhiyong@163.com on 2019/12/9.
 */
@Slf4j
public class StreamingJob {

    public static void main(String[] args) throws Exception {
        NacosConfig.initConfigIns(GROUP_BASE_SERVICE, DATA_ID);
        if (Objects.isNull(NacosConfig.properties)) {
            log.info("[{}]获取配置文件失败,退出...", StreamingJob.class.getName());
            System.exit(-1);
        }
        String topic = NacosConfig.properties.getProperty("streamingJob.topic", "test");
        String brokers = NacosConfig.properties.getProperty("streamingJob.brokers", "");

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 重启策略
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.of(10, TimeUnit.SECONDS)));
        // 定期checkpoint，必须保证拥有足够的slots，才可以restart topology。
        env.enableCheckpointing(5000); // checkpoint every 5000 msecs
        env.setParallelism(1);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", brokers);
        properties.setProperty("group.id", StreamingJob.class.getName());
        // 自动提交当前消费指针
        // 如果启用了save checkpoint，那么在保存checkpoint的时候，会自动提交kafka的消费指针，确保kafka中提交的偏移量和检查点状态一致
        properties.setProperty("enable.auto.commit", "true");
        properties.setProperty("auto.commit.interval.ms", "5000");
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), properties);
//        consumer.setStartFromEarliest();     // start from the earliest record possible
//        consumer.setStartFromLatest();       // start from the latest record
//        consumer.setStartFromTimestamp(...); // start from specified epoch timestamp (milliseconds)
        consumer.setStartFromGroupOffsets(); // the default behaviour
//        consumer.setStartFromLatest();
        // 设置指定offset
//        Map<KafkaTopicPartition, Long> specificStartOffsets = new HashMap<>();
//        specificStartOffsets.put(new KafkaTopicPartition("myTopic", 0), 23L);
//        specificStartOffsets.put(new KafkaTopicPartition("myTopic", 1), 31L);
//        specificStartOffsets.put(new KafkaTopicPartition("myTopic", 2), 43L);
//        consumer.setStartFromSpecificOffsets(specificStartOffsets);

        // 输出到kafka
//        String topicPro = "event";
//        FlinkKafkaProducer<String> producer = new FlinkKafkaProducer<>(topicPro, new SimpleStringSchema(), CommonUtil.kafkaConfigInit(""));

        DataStream<String> stream = env.addSource(consumer);
        stream.map(new InsertMapFunction()).name("insertTest").addSink(new RedisSinkFunction()).name("redisSink");

        // execute program
        env.execute("StreamingJob");
    }

}

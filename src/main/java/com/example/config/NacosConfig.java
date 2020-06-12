package com.example.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * <p></p>
 * Created by @author zhezhiyong@163.com on 2019/7/24.
 */
@Slf4j
public class NacosConfig implements Runnable {
    public static Properties properties = new Properties();
    private String group;
    private String dataId;

    public NacosConfig(String group, String dataId) {
        this.group = group;
        this.dataId = dataId;
    }

    public static void initConfigIns(String group, String dataId) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("nacos-pool-%d").build();
        //Common Thread Pool
        ExecutorService pool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        NacosConfig nacosConfig = new NacosConfig(group, dataId);
        pool.execute(nacosConfig);
//        pool.execute(()-> System.out.println(Thread.currentThread().getName()));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }
    }

    public static Properties getConfig(String dataId, String group) {
        Properties properties = new Properties();
        try {
            Map<String, String> map = System.getenv();
            String serverAddr = map.getOrDefault("NACOS_SERVER_ADDR", "192.168.10.140:8848,192.168.10.126:8848");
            log.info("读取环境变量NACOS_SERVER_ADDR:{}", serverAddr);
            Properties pros = new Properties();
            pros.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
            ConfigService configService = NacosFactory.createConfigService(pros);
            String content = configService.getConfig(dataId, group, 3000);
            log.info("获取到配置文件信息:{}", content);
            try {
                properties.load(new StringReader(content));
            } catch (IOException | NullPointerException e) {
                log.error("解析配置文件异常:{}", e.getMessage(), e);
            }
        } catch (NacosException e) {
            log.info("获取配置异常:{}", e.getMessage(), e);
        }
        return properties;
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("nacos-pool-%d").build();

        //Common Thread Pool
        ExecutorService pool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        NacosConfig nacosConfig = new NacosConfig("flink-config.properties", "BaseService");

        pool.execute(nacosConfig);
//        pool.execute(()-> System.out.println(Thread.currentThread().getName()));

        Thread.sleep(3000);
        System.out.println("nacosConfig = " + JSON.toJSONString(NacosConfig.properties));
        Thread.sleep(30000);

        pool.shutdown();//gracefully shutdown

    }

    @Override
    public void run() {
        log.info("获取nacos文件信息...");
        try {
            Map<String, String> map = System.getenv();
            String serverAddr = map.getOrDefault("NACOS_SERVER_ADDR", "192.168.10.140:8848,192.168.10.126:8848");
            log.info("读取环境变量NACOS_SERVER_ADDR:{}", serverAddr);
            Properties pros = new Properties();
            pros.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
            ConfigService configService = NacosFactory.createConfigService(pros);
            String content = configService.getConfig(dataId, group, 3000);
            log.info("获取到配置文件信息:{}", content);
            try {
                NacosConfig.properties.load(new StringReader(content));
            } catch (IOException | NullPointerException e) {
                log.error("解析配置文件异常:{}", e.getMessage(), e);
            }
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String content) {
                    log.info("接收到配置发生变化: {}", content);
                    try {
                        NacosConfig.properties.load(new StringReader(content));
                    } catch (IOException | NullPointerException e) {
                        log.error("解析配置文件异常:{}", e.getMessage(), e);
                    }
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }
            });


        } catch (NacosException e) {
            log.info("获取配置异常:{}", e.getMessage(), e);
        }
    }
}

package com.example.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import static com.example.config.Constant.DATA_ID;
import static com.example.config.Constant.GROUP;

/**
 * <p></p>
 * Created by @author zhezhiyong@163.com on 2020/6/10.
 */
@Slf4j
public class Config extends Properties {

    public Config() {
    }

    public void init() {
        log.info("获取nacos文件信息...");
        String group = GROUP;
        String dataId = DATA_ID;
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
                this.load(new StringReader(content));
            } catch (IOException | NullPointerException e) {
                log.error("解析配置文件异常:{}", e.getMessage(), e);
            }
            log.info("当前spring文件配置:{}", JSON.toJSONString(this));
        } catch (NacosException e) {
            log.info("获取配置异常:{}", e.getMessage(), e);
        }
    }

}

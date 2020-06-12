# flink示例

1.flink+spring
2.kafka数据源
3.数据持久化mysql
4.数据缓存redis

```
CREATE TABLE `user_click_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `msg` varchar(50) DEFAULT NULL COMMENT '消息',
  `click_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```
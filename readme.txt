http://redirect.viglink.com?key=7b2d14e2afe3c3bdb83c57bcbd485b1f&u=http://www.6pm.com/p/keds-champion-wool-forest-green/product/8732018/color/955


最新更新:
商品列表抓取思路：
1、以商家进行分类，比如美亚、德亚、6pm
2、每个商家的商品有商品分类，比如服装箱包、电子产品等

以什么值得买为例，抓取商品列表的步骤如下：
1、创建抓取规则
1.1、创建商家名称列表（美亚、德亚、6pm等）List
1.2、根据商家分类列表获取商家默认域名，merHost
1.3、设置抓取规则名称(对应商家名称)
1.4、设置http请求类型(get、post)
1.5、设置抓取规则参考标示,比如html代码中的id、class、tag等
1.6、设置抓取规则参考标示的具体值，比如标志为id，id=d_title
1.7、根据商家设置当前抓取规则的host列表，比如美亚包含了http://www.amazon.com、https://www.amazon.com，这一步比较鸡肋，现在好像已经用不到了，需要确认
1.8、设置抓取规则 被抓取网站(数据源)的域名
1.9、设置抓取规则具体的商品列表页url
1.10、设置抓取规则对应的被抓网站索引(int类型)



现在的模式抓取规则模式如下：
逛丢（只抓美亚）：
商品列表页对应商品类型数量。

============================
tomcat最低要求:tomcat6

 ### set log levels ###
log4j.rootLogger = debug , stdout , D , E

### 输出到控制台 ###
log4j.appender.stdout = org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target = System.out
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern = %d{ABSOLUTE} %5p %c{ 1 }:%L - %m%n

### 输出到日志文件 ###
log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
log4j.appender.D.File = logs/log.log
log4j.appender.D.Append = true
log4j.appender.D.Threshold = DEBUG ## 输出DEBUG级别以上的日志
log4j.appender.D.layout = org.apache.log4j.PatternLayout
log4j.appender.D.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss} [ %t:%r ] - [ %p ] %m%n

### 保存异常信息到单独文件 ###
log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
log4j.appender.D.File = logs/error.log ## 异常日志文件名
log4j.appender.D.Append = true
log4j.appender.D.Threshold = ERROR ## 只输出ERROR级别以上的日志!!!
log4j.appender.D.layout = org.apache.log4j.PatternLayout
log4j.appender.D.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss} [ %t:%r ] - [ %p ] %m%n

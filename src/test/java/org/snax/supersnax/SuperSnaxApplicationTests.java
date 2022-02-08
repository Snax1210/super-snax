package org.snax.supersnax;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snax.supersnax.entity.GoRule;
import org.snax.supersnax.entity.JavaRule;
import org.snax.supersnax.entity.Stock;
import org.snax.supersnax.entity.TestDecimal;
import org.snax.supersnax.mapper.RuleDao;
import org.snax.supersnax.service.KubernetesService;
import org.snax.supersnax.service.Provider;
import org.snax.supersnax.service.impl.StockService;
import org.snax.supersnax.util.HttpClientUtil;
import org.snax.supersnax.util.Point;
import org.snax.supersnax.util.RedisService;
import org.snax.supersnax.util.WLS;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SuperSnaxApplication.class)
@MapperScan("org.snax.supersnax.mapper")
class SuperSnaxApplicationTests {

    public static final String TOPIC = "test_transform_";

    private static final Logger LOGGER = LoggerFactory.getLogger(SuperSnaxApplicationTests.class);

    @Resource
    Provider provider;

    @Resource
    RedisService redisService;

    @Resource
    RuleDao ruleDao;

    @Resource
    StockService stockService;

    @Resource
    KubernetesService kubernetesService;

    @Test
    void contextLoads() {
        provider.produce();
    }

    @Test
    void deleteConsumer() throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.4.51:9093");
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "1000");
        properties.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "5000");
        AdminClient adminClient = AdminClient.create(properties);
        adminClient.deleteConsumerGroups(Collections.singletonList("12312r12241")).all().get();
        adminClient.close();
    }

    @Test
    void createTopic() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.4.53:9092");
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "1000");
        properties.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "5000");
        AdminClient adminClient = AdminClient.create(properties);

        adminClient.createTopics(Lists.newArrayList(new NewTopic("a", 1, (short)1)));
    }

    @Test
    void test() {
        redisService.addSortSet("applicationFlowIn:1455832261158580225", "deprecated", 0);
    }

    @Test
    void closeConsumer() {
        Properties properties = new Properties();
        //xxx是服务器集群的ip
        properties.put("bootstrap.servers", "192.168.4.51:9093");
        properties.put("group.id", "fishing-consumer1455706986395545601]");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.unsubscribe();
        kafkaConsumer.close();
    }

    @Test
    void testDecimal() {
        String testString = "{\n" + "\"value\":312124812.1238174891241,\n" + "\"name\":\"12312\"\n" + "}";
        System.out.println(JSON.parseObject(testString, TestDecimal.class));
        System.out.println(BigDecimal.valueOf(123141241.1123412412f));
    }

    @Test
    void dataTransfer() {
        List<GoRule> goRules = ruleDao.getGoRules();
        goRules.forEach(goRule -> {
            JavaRule javaRule = new JavaRule();
            javaRule.setRuleId(System.nanoTime());
            javaRule.setRuleName(goRule.getPatternId() + "-" + goRule.getRuleId());
            JSONObject xInfo = JSON.parseObject(goRule.getXInfo());
            JSONObject yInfo = JSON.parseObject(goRule.getYInfo());
            JSONObject dataInfo = new JSONObject();
            dataInfo.put("x_info", xInfo);
            dataInfo.put("y_info", yInfo);
            javaRule.setDataInfo(dataInfo);
            javaRule.setId((long)goRule.getRuleId());
            javaRule.setPatternId((long)goRule.getPatternId());
            javaRule.setEdgeNum(goRule.getEdgeNum());
            javaRule.setYSupportSingle(goRule.getYSupportSingle());
            javaRule.setAttributeNum(goRule.getAttributeNum());
            javaRule.setXSupportSingle(goRule.getXSupportSingle());
            javaRule.setConfidence(goRule.getConfidence());
            javaRule.setLift(goRule.getLift());
            javaRule.setXSupportMultiple(goRule.getXSupportMultiple());
            javaRule.setYSupportMultiple(goRule.getYSupportMultiple());
            ruleDao.insertJavaRules(javaRule);
        });
    }

    @Test
    void testSend() {
        provider.produceSingle("UID_Test",
            "{\"e\":[{\"dst\":1,\"src\":0,\"attribute\":{\"u→i→d\": [6379422713123131]},\"label\":\"参演\"}],"
                + "\"v\":[{\"attribute\":{\"u→i→d\": [271883557623412412411241241412411]},\"id\":0,\"label\":\"明星\"},"
                + "{\"attribute\":{\"u→i→d\": [4688799]},\"id\":1,\"label\":\"电影\"}]}");
    }

    @Test
    void getMessage() throws ParseException {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.51:9094");
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 30000000);
        p.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(p);
        kafkaConsumer.subscribe(Collections.singleton(TOPIC + "204001"));
        Date startSecond = null;
        Stock oldStock = new Stock();
        Date closeSecond = null;
        Date startMinute = null;
        List<JSONObject> jsonList = new ArrayList<>();
        List<Stock> stockList = new ArrayList<>(20);
        SimpleDateFormat simpleDateFormatSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormatMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                JSONObject json = JSON.parseObject(record.value());
                //计算三秒内产生的数据
                //初始赋值
                if (startSecond == null) {
                    startSecond = simpleDateFormatSecond.parse(json.get("TradTime").toString());
                    //设置阈值
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(startSecond);
                    calendar.add(Calendar.SECOND, 3);
                    closeSecond = calendar.getTime();

                }
                jsonList.add(json);
                if (simpleDateFormatSecond.parse(json.get("TradTime").toString()).getTime() >= closeSecond.getTime()) {
                    Stock stock = stockService.getAmountAndOpeningAndClosingMaxAndMin(jsonList);
                    stockService.getAmplitudeAndApplies(oldStock, stock);
                    //                        producer.send(new ProducerRecord<>("Stock_202201241048_" + json.get
                    //                        ("SecurityID"),
                    //                            JSON.toJSONString(stock)));
                    oldStock = stock;
                    stockList.add(stock);
                    jsonList = new ArrayList<>();
                    jsonList.add(json);
                    startSecond = simpleDateFormatSecond.parse(json.get("TradTime").toString());
                    //设置阈值
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(startSecond);
                    calendar.add(Calendar.SECOND, 3);
                    closeSecond = calendar.getTime();
                }

                if (startMinute == null) {
                    startMinute = simpleDateFormatMinute.parse(json.get("TradTime").toString());
                }
                if (simpleDateFormatMinute.parse(json.get("TradTime").toString()).getTime() != startMinute.getTime()) {
                    //                        LOGGER.info("close Minute is {}, start minute is {}",
                    //                            simpleDateFormatMinute.parse(json.get("TradTime").toString()),
                    //                            startMinute);
                    stockList = stockService.get50PercentData(stockList);
                    double result = stockService.getMeanAndStd(stockList);
                    json.put("result", result);
                    startMinute = simpleDateFormatMinute.parse(json.get("TradTime").toString());
                }
                json.put("result", 0d);
                KafkaProducer<String, String> producer = getProvider();
                producer.send(new ProducerRecord<>("Transaction_202201111000_" + json.get("SecurityID"),
                    json.toJSONString()));
                producer.close();
            }
        }
    }

    @Test
    void deleteTopic() {
        Properties p = new Properties();
        p.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.4.53:9092");
        p.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, String.valueOf(Integer.MAX_VALUE));
        p.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, String.valueOf(Integer.MAX_VALUE));
        AdminClient adminClient = AdminClient.create(p);
        DeleteTopicsResult deleteTopicsResult =
            adminClient.deleteTopics(Collections.singleton("SaveRule_1469136760704339969"));
        try {
            long startTime = System.currentTimeMillis();
            LOGGER.info("start time {}", startTime);
            deleteTopicsResult.all().get();
            LOGGER.info("cost time is {}", System.currentTimeMillis() - startTime);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        adminClient.close();
    }

    @Test
    public void sendCsvFileToKafka() {
        Date startSecond = null;
        Stock oldStock = new Stock();
        Date closeSecond = null;
        Date startMinute = null;
        List<JSONObject> jsonList = new ArrayList<>();
        List<Stock> stockList = new ArrayList<>(20);
        SimpleDateFormat simpleDateFormatSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormatMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        double result = 0d;
        KafkaProducer<String, String> producer = getProvider();
        String path = "D:\\download\\20210330\\20210330_Transaction.csv";
        String[] securityIds =
            //            {"204001", "601012", "600438", "600111", "601899", "600031", "600744", "601016", "600010",
            //            "113557"};
            {"600438"};
        File file = new File(path);
        try {
            FileReader reader = new FileReader(file);
            BufferedReader in = new BufferedReader(reader);
            String line;
            int lineNum = 0;
            String oldTime = null;
            String[] headerNames = in.readLine().split(",");
            while ((line = in.readLine()) != null) {
                JSONObject json = new JSONObject();
                Object[] values = line.split(",");
                for (int i = 0; i < headerNames.length; i++) {
                    json.put(headerNames[i], values[i]);
                }
                lineNum++;
                if (Arrays.stream(securityIds).anyMatch(e -> e.equals(json.get("SecurityID")))) {
                    String tradTime = json.get("LocalTime").toString().split("\\.")[0];
                    if (!(tradTime.equals(oldTime))) {
                        oldTime = tradTime;
                        System.out.println(tradTime + "," + lineNum);
                        Thread.sleep(1000);
                    }

                    json.put("LocalTime", "2021-03-30 " + json.get("LocalTime"));
                    json.put("TradTime", "2021-03-30 " + json.get("TradTime"));
                    //计算三秒内产生的数据
                    //初始赋值
                    if (startSecond == null) {
                        startSecond = simpleDateFormatSecond.parse(json.get("TradTime").toString());
                        //设置阈值
                        Calendar oldCalendar = new GregorianCalendar();
                        oldCalendar.setTime(startSecond);
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(startSecond);
                        calendar.add(Calendar.SECOND, 3);

                        if (oldCalendar.get(Calendar.MINUTE) != calendar.get(Calendar.MINUTE)) {
                            calendar.set(Calendar.SECOND, 60);
                        }
                        closeSecond = calendar.getTime();

                    }
                    jsonList.add(json);
                    if (simpleDateFormatSecond.parse(json.get("TradTime").toString()).getTime()
                        >= closeSecond.getTime()) {
                        Stock stock = stockService.getAmountAndOpeningAndClosingMaxAndMin(jsonList);
                        stockService.getAmplitudeAndApplies(oldStock, stock);
                        //                        producer.send(new ProducerRecord<>("Stock_202201241048_" + json.get
                        //                        ("SecurityID"),
                        //                            JSON.toJSONString(stock)));
                        oldStock = stock;
                        stockList.add(stock);
                        jsonList = new ArrayList<>();
                        jsonList.add(json);
                        startSecond = simpleDateFormatSecond.parse(json.get("TradTime").toString());
                        //设置阈值
                        Calendar oldCalendar = new GregorianCalendar();
                        oldCalendar.setTime(startSecond);
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(startSecond);
                        calendar.add(Calendar.SECOND, 3);

                        if (oldCalendar.get(Calendar.MINUTE) != calendar.get(Calendar.MINUTE)) {
                            calendar.set(Calendar.SECOND, 60);
                        }
                        closeSecond = calendar.getTime();
                    }

                    if (startMinute == null) {
                        startMinute = simpleDateFormatMinute.parse(json.get("TradTime").toString());
                    }
                    if (simpleDateFormatMinute.parse(json.get("TradTime").toString()).getTime()
                        > startMinute.getTime()) {
                        System.out.println("startTime is :" + startMinute + "," + "endTime is :"
                            + simpleDateFormatMinute.parse(json.get("TradTime").toString()));
                        //                        LOGGER.info("close Minute is {}, start minute is {}",
                        //                            simpleDateFormatMinute.parse(json.get("TradTime").toString()),
                        //                            startMinute);
                        stockList = stockService.get50PercentData(stockList);
                        if (stockList.size() > 2) {
                            result = stockService.getMeanAndStd(stockList);
                        }
                        System.out.println("result: " + result);
                        startMinute = simpleDateFormatMinute.parse(json.get("TradTime").toString());
                    }
                    json.put("result", result);
                    producer.send(new ProducerRecord<>("Transaction_202201251551_" + json.get("SecurityID"),
                        json.toJSONString()));
                }
            }
            in.close();
            reader.close();
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendDataToKafka_204001() {
        String[] securityIds =
            {"204001", "601012", "600438", "600111", "601899", "600031", "600744", "601016", "600010", "113557"};
        sendCsvFileToKafka("600111");
    }
    @Test
    public void sendDataToKafka_601012() {
        String[] securityIds =
            {"204001", "601012", "600438", "600111", "601899", "600031", "600744", "601016", "600010", "113557"};
        sendCsvFileToKafka("601012");
    }
    @Test
    public void sendDataToKafka_600438() {
        String[] securityIds =
            {"204001", "601012", "600438", "600111", "601899", "600031", "600744", "601016", "600010", "113557"};
        sendCsvFileToKafka("600438");
    }


    private void sendCsvFileToKafka(String number) {
        Date startSecond = null;
        Stock oldStock = new Stock();
        Date closeSecond = null;
        Date startMinute = null;
        List<JSONObject> jsonList = new ArrayList<>();
        List<Stock> stockList = new ArrayList<>(20);
        SimpleDateFormat simpleDateFormatSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormatMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        double result = 0d;
        KafkaProducer<String, String> producer = getProvider();
        String path = "D:\\download\\20210330\\20210330_Transaction.csv";
        String securityIds = number;
        //            {"204001", "601012", "600438", "600111", "601899", "600031", "600744", "601016", "600010",
        //            "113557"};
        File file = new File(path);
        try {
            FileReader reader = new FileReader(file);
            BufferedReader in = new BufferedReader(reader);
            String line;
            int lineNum = 0;
            String oldTime = null;
            String[] headerNames = in.readLine().split(",");
            while ((line = in.readLine()) != null) {
                JSONObject json = new JSONObject();
                Object[] values = line.split(",");
                for (int i = 0; i < headerNames.length; i++) {
                    json.put(headerNames[i], values[i]);
                }
                lineNum++;
                if (securityIds.equals(json.get("SecurityID"))){
                    String tradTime = json.get("LocalTime").toString().split("\\.")[0];
                    if (!(tradTime.equals(oldTime))) {
                        oldTime = tradTime;
                        System.out.println(tradTime + "," + lineNum);
                        Thread.sleep(1000);
                    }

                    json.put("LocalTime", "2021-03-30 " + json.get("LocalTime"));
                    json.put("TradTime", "2021-03-30 " + json.get("TradTime"));
                    //计算三秒内产生的数据
                    //初始赋值
                    if (startSecond == null) {
                        startSecond = simpleDateFormatSecond.parse(json.get("TradTime").toString());
                        //设置阈值
                        Calendar oldCalendar = new GregorianCalendar();
                        oldCalendar.setTime(startSecond);
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(startSecond);
                        calendar.add(Calendar.SECOND, 3);

                        if (oldCalendar.get(Calendar.MINUTE) != calendar.get(Calendar.MINUTE)) {
                            calendar.set(Calendar.SECOND, 60);
                        }
                        closeSecond = calendar.getTime();

                    }
                    jsonList.add(json);
                    if (simpleDateFormatSecond.parse(json.get("TradTime").toString()).getTime()
                        >= closeSecond.getTime()) {
                        Stock stock = stockService.getAmountAndOpeningAndClosingMaxAndMin(jsonList);
                        stockService.getAmplitudeAndApplies(oldStock, stock);
                        //                        producer.send(new ProducerRecord<>("Stock_202201241048_" + json.get
                        //                        ("SecurityID"),
                        //                            JSON.toJSONString(stock)));
                        oldStock = stock;
                        stockList.add(stock);
                        jsonList = new ArrayList<>();
                        jsonList.add(json);
                        startSecond = simpleDateFormatSecond.parse(json.get("TradTime").toString());
                        //设置阈值
                        Calendar oldCalendar = new GregorianCalendar();
                        oldCalendar.setTime(startSecond);
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(startSecond);
                        calendar.add(Calendar.SECOND, 3);

                        if (oldCalendar.get(Calendar.MINUTE) != calendar.get(Calendar.MINUTE)) {
                            calendar.set(Calendar.SECOND, 60);
                        }
                        closeSecond = calendar.getTime();
                    }

                    if (startMinute == null) {
                        startMinute = simpleDateFormatMinute.parse(json.get("TradTime").toString());
                    }
                    if (simpleDateFormatMinute.parse(json.get("TradTime").toString()).getTime()
                        > startMinute.getTime()) {
                        System.out.println("startTime is :" + startMinute + "," + "endTime is :"
                            + simpleDateFormatMinute.parse(json.get("TradTime").toString()));
                        //                        LOGGER.info("close Minute is {}, start minute is {}",
                        //                            simpleDateFormatMinute.parse(json.get("TradTime").toString()),
                        //                            startMinute);
                        stockList = stockService.get50PercentData(stockList);
                        if (stockList.size() > 2) {
                            result = stockService.getMeanAndStd(stockList);
                        }
                        System.out.println("result: " + result);
                        startMinute = simpleDateFormatMinute.parse(json.get("TradTime").toString());
                    }
                    json.put("result", result);
                    producer.send(new ProducerRecord<>("Transaction_202201251556_" + json.get("SecurityID"),
                        json.toJSONString()));
                }
            } in.close();
            reader.close();
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendCsvFileToKafkaTradMoney() {
        SimpleDateFormat simpleDateFormatSecond = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat simpleDateFormatMinute = new SimpleDateFormat("HH:mm");
        String path = "D:\\download\\20210330\\20210330_Transaction.csv";
        String securityIds = "204001";
        File file = new File(path);
        try {
            FileReader reader = new FileReader(file);
            BufferedReader in = new BufferedReader(reader);
            String line;
            //旧时间
            Date startSecond = null;
            Stock oldStock = new Stock();
            Date closeSecond = null;
            Date startMinute = null;
            List<JSONObject> jsonList = new ArrayList<>();
            List<Stock> stockList = new ArrayList<>(20);
            String[] headerNames = in.readLine().split(",");
            while ((line = in.readLine()) != null) {
                JSONObject json = new JSONObject();
                Object[] values = line.split(",");
                for (int i = 0; i < headerNames.length; i++) {
                    json.put(headerNames[i], values[i]);
                }
                if (json.get("SecurityID").equals(securityIds)) {

                    //计算三秒内产生的数据
                    //初始赋值
                    if (startSecond == null) {
                        startSecond = simpleDateFormatSecond.parse(json.get("TradTime").toString());
                        //设置阈值
                        Calendar oldCalendar = new GregorianCalendar();
                        oldCalendar.setTime(startSecond);
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(startSecond);
                        calendar.add(Calendar.SECOND, 3);

                        if (oldCalendar.get(Calendar.MINUTE) != calendar.get(Calendar.MINUTE)) {
                            calendar.set(Calendar.SECOND, 60);
                        }
                        closeSecond = calendar.getTime();

                    }
                    jsonList.add(json);
                    if (simpleDateFormatSecond.parse(json.get("TradTime").toString()).getTime()
                        != closeSecond.getTime()) {
                        Stock stock = stockService.getAmountAndOpeningAndClosingMaxAndMin(jsonList);
                        stockService.getAmplitudeAndApplies(oldStock, stock);
                        //                        producer.send(new ProducerRecord<>("Stock_202201241048_" + json.get
                        //                        ("SecurityID"),
                        //                            JSON.toJSONString(stock)));
                        oldStock = stock;
                        stockList.add(stock);
                        jsonList = new ArrayList<>();
                        jsonList.add(json);
                        startSecond = simpleDateFormatSecond.parse(json.get("TradTime").toString());
                        //设置阈值
                        Calendar oldCalendar = new GregorianCalendar();
                        oldCalendar.setTime(startSecond);
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(startSecond);
                        calendar.add(Calendar.SECOND, 3);
                        if (oldCalendar.get(Calendar.MINUTE) != calendar.get(Calendar.MINUTE)) {
                            calendar.set(Calendar.SECOND, 60);
                        }
                        closeSecond = calendar.getTime();
                    }

                    if (startMinute == null) {
                        startMinute = simpleDateFormatMinute.parse(json.get("TradTime").toString());
                    }
                    if (simpleDateFormatMinute.parse(json.get("TradTime").toString()).getTime()
                        > startMinute.getTime()) {
                        System.out.println("startTime is :" + startMinute + "," + "endTime is :"
                            + simpleDateFormatMinute.parse(json.get("TradTime").toString()));
                        //                        LOGGER.info("close Minute is {}, start minute is {}",
                        //                            simpleDateFormatMinute.parse(json.get("TradTime").toString()),
                        //                            startMinute);
                        stockList = stockService.get50PercentData(stockList);
                        double result = stockService.getMeanAndStd(stockList);
                        System.out.println(result);
                        startMinute = simpleDateFormatMinute.parse(json.get("TradTime").toString());
                    }

                }
            }
            in.close();
            reader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendCsvFileToKafkaAndCreateTasks() {
        KafkaProducer<String, String> producer = getProvider();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String path = "D:\\download\\20210330\\20210330_Transaction.csv";
        File file = new File(path);
        try {
            FileReader reader = new FileReader(file);
            BufferedReader in = new BufferedReader(reader);
            String line;
            int lineNum = 0;
            String oldTime = null;
            String[] headerNames = in.readLine().split(",");
            Map<String, Boolean> securityIdMap = new HashMap<>(3000);
            while ((line = in.readLine()) != null) {
                JSONObject json = new JSONObject();
                Object[] values = line.split(",");
                for (int i = 0; i < headerNames.length; i++) {
                    json.put(headerNames[i], values[i]);
                }
                lineNum++;
                //                    System.out.println(json.get("SecurityID"));
                String tradTime = json.get("LocalTime").toString().split("\\.")[0];
                String securityId = json.get("SecurityID").toString();
                //                if (!securityIdMap.containsKey(securityId)) {
                //                    securityIdMap.put(securityId, true);
                //                    createDruidTask(securityId);
                //                }
                if (!(tradTime.equals(oldTime))) {
                    oldTime = tradTime;
                    System.out.println(tradTime + "," + lineNum);
                    Thread.sleep(1000);
                }

                json.put("LocalTime", "2021-03-30 " + json.get("LocalTime"));
                json.put("TradTime", "2021-03-30 " + json.get("TradTime"));
                producer.send(new ProducerRecord<>("20210330_Transaction_202201111000_" + securityId,
                    json.toJSONString()));
            }
            in.close();
            reader.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testWLSModelStableWeight() {
        double[] x = {1, 2, 3, 4, 5, 6, 7};
        double[] y = {1, 3, 4, 5, 2, 3, 4};

        WLS wls = new WLS(x, y);
        Point point = wls.fitLinearRegression();
        System.out.println(point.getIntercept());
        System.out.println(point.getSlope());
        //        assertEquals(2.14285714, point.getIntercept(), 6);
        //        assertEquals(0.25, point.getSlope(), 6);
    }

    @Test
    void testWLSModelWeight() {
        double[] x = {1, 2, 3, 4, 5, 6, 7};
        double[] y = {1, 3, 4, 5, 2, 3, 4};

        WLS wls = new WLS(x, y, 0.9);
        Point point = wls.fitLinearRegression();

        //        assertEquals(2.14285714, point.getIntercept(), 6);
        //        assertEquals(0.25, point.getSlope(), 6);
    }

    @Test()
    void testSinglePointDissallowed() {
        double[] x = {10};
        double[] y = {1};
        assertThrows(AssertionError.class, () -> {
            WLS wls = new WLS(x, y);
        });
    }

    @Test

    public void testCreateTask() {
        createDruidTask("123");
    }

    private void createDruidTask(String securityId) {
        String url = "http://192.168.200.51:8888/druid/indexer/v1/supervisor";
        String param = "{\n" + "  \"type\": \"kafka\",\n" + "  \"spec\": {\n" + "    \"ioConfig\": {\n"
            + "      \"type\": \"kafka\",\n" + "      \"consumerProperties\": {\n"
            + "        \"bootstrap.servers\": \"192.168.200.51:9093\"\n" + "      },\n"
            + "      \"topic\": \"20210330_Transaction_202201101600_" + securityId + "\",\n"
            + "      \"inputFormat\": {\n" + "        \"type\": \"json\"\n" + "      },\n"
            + "      \"useEarliestOffset\": true\n" + "    },\n" + "    \"tuningConfig\": {\n"
            + "      \"type\": \"kafka\",\n" + "      \"logParseExceptions\": true\n" + "    },\n"
            + "    \"dataSchema\": {\n" + "      \"dataSource\": \"HX_0110_1600_" + securityId + "\",\n"
            + "      \"timestampSpec\": {\n" + "        \"column\": \"LocalTime\",\n" + "        \"format\": \"auto\"\n"
            + "      },\n" + "      \"granularitySpec\": {\n" + "        \"queryGranularity\": \"three_second\",\n"
            + "        \"rollup\": false,\n" + "        \"segmentGranularity\": \"hour\"\n" + "      },\n"
            + "      \"dimensionsSpec\": {\n" + "        \"dimensions\": [\n" + "          {\n"
            + "            \"type\": \"long\",\n" + "            \"name\": \"SeqNo\"\n" + "          },\n"
            + "          {\n" + "            \"type\": \"long\",\n" + "            \"name\": \"TradeMoney\"\n"
            + "          },\n" + "          {\n" + "            \"type\": \"string\",\n"
            + "            \"name\": \"TradeBuyNo\"\n" + "          },\n" + "          {\n"
            + "            \"type\": \"long\",\n" + "            \"name\": \"TradeIndex\"\n" + "          },\n"
            + "          {\n" + "            \"type\": \"string\",\n" + "            \"name\": \"TradeSellNo\"\n"
            + "          },\n" + "          \"TradeBSFlag\",\n" + "          {\n"
            + "            \"type\": \"double\",\n" + "            \"name\": \"TradPrice\"\n" + "          },\n"
            + "          \"TradTime\",\n" + "          {\n" + "            \"type\": \"string\",\n"
            + "            \"name\": \"SecurityID\"\n" + "          },\n" + "          {\n"
            + "            \"type\": \"long\",\n" + "            \"name\": \"TradVolume\"\n" + "          },\n"
            + "          {\n" + "            \"type\": \"long\",\n" + "            \"name\": \"DataStatus\"\n"
            + "          },\n" + "          {\n" + "            \"type\": \"long\",\n"
            + "            \"name\": \"TradeChan\"\n" + "          }\n" + "        ]\n" + "      },\n"
            + "      \"metricsSpec\": [\n" + "        {\n" + "          \"type\": \"longSum\",\n"
            + "          \"name\": \"SumTradeMoney\",\n" + "          \"fieldName\": \"TradeMoney\"\n" + "        },\n"
            + "        {\n" + "          \"type\": \"longSum\",\n" + "          \"name\": \"SumTradeVolume\",\n"
            + "          \"fieldName\": \"TradVolume\"\n" + "        }\n" + "      ]\n" + "    }\n" + "  }\n" + "}";
        JSONObject json = JSON.parseObject(param);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        HttpClientUtil.httpPostForm(url, json.toJSONString(), headers, null);
    }

    @Test
    public void testJson() {
        String a = "{\"e\":[{\"dst\":1,'src':0,\"attribute\":{\"u→i→d\": [3166360514]},\"label\":\"参演\"}],"
            + "\"v\":[{\"attribute\":{\"u→i→d\": [2505284174]},\"id\":0,\"label\":\"明星\"},{\"attribute\":{\"u→i→d\": "
            + "[15531648]},\"id\":1,\"label\":'电影'}]}";
        JSONObject b = JSON.parseObject(a);
        System.out.println(b);
    }

    private KafkaProducer<String, String> getProvider() {
        Properties props = new Properties();
        //xxx服务器ip
        props.put("bootstrap.servers", "192.168.200.51:9094");
        //所有follower都响应了才认为消息提交成功，即"committed"
        props.put("acks", "all");
        //retries = MAX 无限重试，直到你意识到出现了问题:)
        props.put("retries", 0);
        props.put("batch.size", 16384);//producer将试图批处理消息记录，以减少请求次数.默认的批量处理消息字节数
        //batch.size当批量的数据大小达到设定值后，就会立即发送，不顾下面的linger.ms
        props.put("linger.ms", 1);//延迟1ms发送，这项设置将通过增加小的延迟来完成--即，不是立即发送一条记录，producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理
        props.put("buffer.memory", 999999999999999999L);//producer可以用来缓存数据的内存大小。
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<String, String>(props);
    }

    @Test
    public void getAllPods(){
        kubernetesService.getAllPods();
    }
}

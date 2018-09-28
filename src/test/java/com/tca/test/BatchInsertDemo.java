package com.tca.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

public class BatchInsertDemo {
	
	private static InfluxDB client;
	
	private static final Random RAND = new Random(100);
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddhhmmssssssss");
	
	
	public static void main(String[] args) throws InterruptedException {
		
		for (;;) {
			//Thread.sleep(1L);
			
			// 获取客户端对象
			client =  InfluxDBFactory.connect("http://127.0.0.1:8086", "admin", "123456");
			
			// 创建Point
			Point point = Point.measurement("cpu")// 对应的表
					.tag("ip", "127.0.0." + RAND.nextInt(10))// 对应的tag
					.tag("hostname", "localhost-" + RAND.nextInt(2))
					.addField("idle", RAND.nextInt(100))// 对应的field
					.addField("user", RAND.nextInt(100))
					.addField("hot", RAND.nextBoolean())
					.addField("datetime", getDate())
					.build();
			System.out.println(point);
			
			// 写数据
			client.write("mydb180927", "retent_1h", point);
			//client.write("mydb180927", "autogen", point);
			
			System.out.println("写入成功");
		}
	}
	
	/**
	 * @return
	 */
	private static String getDate() {
		return SDF.format(new Date());
	}
	
}

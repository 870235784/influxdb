package com.tca.test;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.junit.Test;

public class ClientTest {
	
	private static InfluxDB client;
	
	/**
	 * 获取client对象
	 */
	@Before
	public void getClient() {
		// 获取客户端对象
		client =  InfluxDBFactory.connect("http://127.0.0.1:8086", "admin", "123456");
		if (null == client.ping()) {//测试客户端连接
			System.out.println("客户端连接失败!");
		}
	}
	
	/**
	 * 数据插入
	 */
	@Test
	public void testInsert() {
		// 创建数据库
		client.createDatabase("mydb180927");
		
		// 创建Point对象
		Point point = Point.measurement("cpu")// 对应的表
			.tag("ip", "127.0.0.1")// 对应的tag
			.tag("hostname", "localhost")
			.addField("idle", 81L)// 对应的field
			.addField("user", 12L)
			.addField("hot", true)
			.build();
		System.out.println(point);
		
		// 将Point写入数据库
		client.write("mydb180927", "autogen", point);
	}
	
	/**
	 * 查询
	 */
	@Test
	public void testQuery() {
		Query queryObj = new Query("select * from cpu", "mydb180927");
		QueryResult result = client.query(queryObj);
		System.out.println(result);
	}
	
	/**
	 * 批量插入 -- 在批量插入时, 所有tag相同的数据只能插入成功一条
	 * 如:在cpu中, 有两个tag key:ip和hostname, 因而当这两条数据相同的point只能插入成功一条
	 */
	@Test
	public void testBatchInsert() {
		// 创建Point对象
		Point point01 = Point.measurement("cpu")// 对应的表
			.tag("ip", "127.0.0.1")// 对应的tag
			.tag("hostname", "localhost")
			.addField("idle", 210L)// 对应的field
			.addField("user", 21L)
			.addField("hot", true)
			.build();
		Point point02 = Point.measurement("cpu")// 对应的表
				.tag("ip", "127.0.0.1")// 对应的tag
				.tag("hostname", "localhost")
				.addField("idle", 200L)// 对应的field
				.addField("user", 20L)
				.addField("hot", true)
				.build();
		BatchPoints points = BatchPoints.database("mydb180927").retentionPolicy("autogen").points(point01, point02)
				.build();
		
		// 将Point批量写入数据库
		client.write(points);
	}
}

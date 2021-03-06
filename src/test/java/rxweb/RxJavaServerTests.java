/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rxweb;


import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rxweb.engine.server.netty.NettyServer;

import java.io.IOException;

/**
 * 单元测试
 *
 * @author Sebastien Deleuze
 * @author zhangjessey
 */
public class RxJavaServerTests {

	private static NettyServer nettyServer;
	private static final Logger logger = LoggerFactory.getLogger(RxJavaServerTests.class);

	@BeforeClass
	public static void setup() {
		nettyServer = new NettyServer();

		nettyServer.get("/functionalRoute/{a}/{b}", (request, response) -> response.writeString(Observable.<String>just("this is functionalRoute".concat(request.getUrlParams().toString())))).
				post("/functionalRoutePost", (request, response) -> response.writeString(Observable.<String>just("this is functionalRoutePost"))).
				put("/functionalRoutePut", (request, response) -> response.writeString(Observable.<String>just("this is functionalRoutePut"))).
				delete("/functionalRouteDelete", (request, response) -> response.writeString(Observable.<String>just("this is functionalRouteDelete")));

		new Thread(() -> {
			try {
				nettyServer.start();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}).start();

	}

	@AfterClass
	public static void tearDown() {
		nettyServer.stop();
	}

	@Test
	public void functionalRoute() throws Exception {

		String content = Request.Get("http://localhost:8080/functionalRoute/3/4").execute().returnContent().asString();
		Assert.assertEquals("this is functionalRoute[3, 4]", content);
		content = Request.Post("http://localhost:8080/functionalRoutePost").execute().returnContent().asString();
		Assert.assertEquals("this is functionalRoutePost", content);
	}

	@Test
	public void notfound() throws IOException {

		int code = Request.Get("http://localhost:8080/notfound").execute().returnResponse().getStatusLine().getStatusCode();
		Assert.assertEquals(404, code);
	}


	@Test
	public void noParam() throws IOException {
		String content = Request.Get("http://localhost:8080/hi").execute().returnContent().asString();
		Assert.assertEquals("hello world", content);
	}

	@Test
	public void withQueryParam() throws IOException {
		String content = Request.Get("http://localhost:8080/withQueryParam?p1=1&p2=2").execute().returnContent().asString();
		Assert.assertEquals("query name:p1:p2", content);
	}

	@Test
	public void post() throws IOException {
		String content = Request.Post("http://localhost:8080/post/10?a=1").execute().returnContent().asString();
		Assert.assertEquals("query name:a,pathValue:10", content);
	}

	@Test
	public void delete() throws IOException {
		String content = Request.Delete("http://localhost:8080/delete/10?a=1").execute().returnContent().asString();
		Assert.assertEquals("query name:a,pathValue:10", content);
	}

	@Test
	public void put() throws IOException {
		String content = Request.Put("http://localhost:8080/put/10?a=1").execute().returnContent().asString();
		Assert.assertEquals("query name:a,pathValue:10", content);
	}

	@Test
	public void postAndReturnBean() throws IOException {
		String content = Request.Post("http://localhost:8080/postAndReturnBean/10?a=1").execute().returnContent().asString();
		Assert.assertEquals("{\"map\":{\"a\":\"1\",\"b\":\"2\",\"c\":\"3\"}}", content);
	}

	@Test
	public void postBean() throws Exception {
		StringEntity stringEntity = new StringEntity("{\"id\":1,\"name\":\"hehe\"}");
		stringEntity.setContentEncoding("UTF-8");
		stringEntity.setContentType("application/json");//发送json数据需要设置contentType

		String content = Request.Post("http://localhost:8080/postBean/10?a=1").body(stringEntity).execute().returnContent().asString();
		Assert.assertEquals("{\"map\":{\"result\":\"success\",\"id\":\"1\",\"name\":\"hehe\",\"pathValue\":10,\"param_a_value\":\"1\"}}", content);
	}



}

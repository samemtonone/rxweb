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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import reactor.io.buffer.Buffer;
import rx.Observable;
import rxweb.http.Status;
import rxweb.rx.rxjava.RxJavaNettyServer;
import rxweb.rx.rxjava.RxJavaServer;

/**
 * @author Sebastien Deleuze
 */
public class RxJavaServerTests {

	private RxJavaServer server;

	@Before
	public void setup() {
		server = new RxJavaNettyServer();
		server.start().toBlocking();
	}

	@After
	public void tearDown() {
		server.stop().toBlocking();
	}


	@Test
	public void writeBuffer() throws IOException {
		server.get("/test", (request, response) -> response.status(Status.OK).content(Observable.just(Buffer.wrap("This is a test!").byteBuffer())));
		String content = Request.Get("http://localhost:8080/test").execute().returnContent().asString();
		Assert.assertEquals("This is a test!", content);
	}

	@Test
	public void echo() throws IOException {
		server.post("/test", (request, response) -> response.content(request.getContent()));
		String content = Request.Post("http://localhost:8080/test").bodyString("This is a test!", ContentType.TEXT_PLAIN).execute().returnContent().asString();
		Assert.assertEquals("This is a test!", content);
	}

	@Test
	public void echoCapitalizedStream() throws IOException {
		server.post("/test", (request, response) -> response.content(request.getContent().map(data -> Buffer.wrap(new String(new Buffer(data).asBytes(), StandardCharsets.UTF_8).toUpperCase()).byteBuffer())));
		String content = Request.Post("http://localhost:8080/test").bodyString("This is a test!", ContentType.TEXT_PLAIN).execute().returnContent().asString();
		Assert.assertEquals("THIS IS A TEST!", content);
	}

}

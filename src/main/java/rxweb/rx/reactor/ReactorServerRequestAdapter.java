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

package rxweb.rx.reactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.io.buffer.Buffer;
import reactor.rx.Stream;
import reactor.rx.Streams;
import rxweb.http.Method;
import rxweb.http.Protocol;
import rxweb.server.ServerRequest;
import rxweb.server.ServerRequestHeaders;

import java.nio.ByteBuffer;

/**
 * @author Sebastien Deleuze
 */
public class ReactorServerRequestAdapter implements ReactorServerRequest {

	private ServerRequest serverRequest;

	public ReactorServerRequestAdapter(ServerRequest serverRequest) {
		this.serverRequest = serverRequest;
	}

	@Override
	public ServerRequestHeaders getHeaders() {
		return this.serverRequest.getHeaders();
	}

	@Override
	public Stream<Buffer> getContent() {
		return Streams.create(this.serverRequest).map(Buffer::new);
	}

	@Override
	public void subscribe(Subscriber<? super Buffer> s) {
		this.serverRequest.subscribe(new Subscriber<ByteBuffer>() {
			@Override
			public void onSubscribe(Subscription subscription) {
				s.onSubscribe(subscription);
			}

			@Override
			public void onNext(ByteBuffer buffer) {
				s.onNext(new Buffer(buffer));
			}

			@Override
			public void onError(Throwable t) {
				s.onError(t);
			}

			@Override
			public void onComplete() {
				s.onComplete();
			}
		});
	}

	@Override
	public Protocol getProtocol() {
		return this.serverRequest.getProtocol();
	}

	@Override
	public String getUri() {
		return this.serverRequest.getUri();
	}

	@Override
	public Method getMethod() {
		return this.serverRequest.getMethod();
	}
}

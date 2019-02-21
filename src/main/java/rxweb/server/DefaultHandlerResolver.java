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

package rxweb.server;

import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rxweb.bean.WebRequest;
import rxweb.mapping.Condition;
import rxweb.mapping.HandlerResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sebastien Deleuze
 * @author zhangjessey
 */
public class DefaultHandlerResolver implements HandlerResolver {

	private static DefaultHandlerResolver defaultHandlerResolver = new DefaultHandlerResolver();

	public static DefaultHandlerResolver getSingleton() {
		return defaultHandlerResolver;
	}

	@Override
	public void addHandler(final Condition<HttpServerRequest> condition, final Handler handler) {
		Init.handlers.put(condition, handler);
	}

	@Override
	public List<RequestHandler> resolve(WebRequest webRequest) {
		List<RequestHandler> requestHandlers = new ArrayList<>();
		for (Map.Entry<Condition<HttpServerRequest>, Handler> entry : Init.handlers.entrySet()) {
			String path = entry.getKey().getUrl();
			if (path.matches(".+\\{\\w+\\}.*")) {
				// 将请求路径中的占位符 {\w+} 转换为正则表达式 (\\w+)
				//path = StringUtil.replaceAll(path, "\\{\\w+\\}", "(\\\\w+)");
				path = path.replaceAll("\\{\\w+\\}", "(\\\\w+)");
			}
			//TODO
			path = path.concat(".*");

			Matcher matcher = Pattern.compile(path).matcher(webRequest.getHttpServerRequest().getUri());
			if (entry.getKey().getHttpMethod().equals(webRequest.getHttpServerRequest().getHttpMethod()) && matcher.matches()) {
				Handler value = entry.getValue();
				value.setRequestPathMatcher(matcher);
				requestHandlers.add(value);
			}
		}
		return requestHandlers;
	}


}

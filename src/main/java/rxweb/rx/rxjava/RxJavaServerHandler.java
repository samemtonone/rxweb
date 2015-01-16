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

package rxweb.rx.rxjava;

import rx.Observable;
import rxweb.server.ServerResponse;

/**
 * Handle the HTTP request to produce a response.
 * @author Sebastien Deleuze
 */
public interface RxJavaServerHandler {

	/**
	 * @param request  The request object providing information about the HTTP request
	 * @param response The response object providing functionality for modifying the response
	 */
	Observable<?> handle(final RxJavaServerRequest request, RxJavaServerResponse response);

}
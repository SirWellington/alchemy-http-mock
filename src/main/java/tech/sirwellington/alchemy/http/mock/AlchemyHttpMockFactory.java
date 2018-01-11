/*
 * Copyright © 2018. Sir Wellington.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.sirwellington.alchemy.http.mock;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.http.AlchemyHttp;
import tech.sirwellington.alchemy.http.HttpRequest;
import tech.sirwellington.alchemy.http.HttpResponse;

import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;
import static tech.sirwellington.alchemy.http.mock.MockRequest.Method.DELETE;
import static tech.sirwellington.alchemy.http.mock.MockRequest.Method.GET;
import static tech.sirwellington.alchemy.http.mock.MockRequest.Method.POST;
import static tech.sirwellington.alchemy.http.mock.MockRequest.Method.PUT;

class AlchemyHttpMockFactory implements AlchemyHttpMock,
                                     AlchemyHttpMock.When,
                                     AlchemyHttpMock.Body,
                                     AlchemyHttpMock.At,
                                     AlchemyHttpMock.Then
{

    private final static Logger LOG = LoggerFactory.getLogger(AlchemyHttpMockFactory.class);
    private final Gson gson = new Gson();

    private final Map<MockRequest, Callable<?>> actions = Maps.newConcurrentMap();

    private MockRequest currentExpectedRequest;
    private HttpRequest httpRequest;

    @Override
    public AlchemyHttp build()
    {
        return new MockAlchemyHttp(actions);
    }

    @Override
    public Body whenPost()
    {
        currentExpectedRequest = new MockRequest();
        currentExpectedRequest.method = POST;
        httpRequest = HttpRequest.Builder.newInstance().build();

        return this;
    }

    @Override
    public Body whenGet()
    {
        currentExpectedRequest = new MockRequest();
        currentExpectedRequest.method = GET;

        return this;
    }

    @Override
    public Body whenPut()
    {
        currentExpectedRequest = new MockRequest();
        currentExpectedRequest.method = PUT;

        return this;
    }

    @Override
    public Body whenDelete()
    {
        currentExpectedRequest = new MockRequest();
        currentExpectedRequest.method = DELETE;

        return this;
    }

    @Override
    public At noBody()
    {
        currentExpectedRequest.body = MockRequest.NO_BODY;

        return this;
    }

    @Override
    public At anyBody()
    {
        currentExpectedRequest.body = MockRequest.ANY_BODY;

        return this;
    }

    @Override
    public At body(Object pojo)
    {
        currentExpectedRequest.body = pojo;

        return this;
    }

    @Override
    public At body(JsonElement jsonBody)
    {
        checkThat(jsonBody)
            .usingMessage("jsonBody cannot be null")
            .is(notNull());

        currentExpectedRequest.body = jsonBody;

        return this;
    }

    @Override
    public At body(String jsonString)
    {
        checkThat(jsonString)
            .usingMessage("jsonString cannot be empty")
            .is(nonEmptyString());

        currentExpectedRequest.body = jsonString;

        return this;
    }

    @Override
    public Then at(URL url)
    {
        checkThat(url)
            .usingMessage("url cannot be null")
            .is(notNull());

        currentExpectedRequest.url = url;

        return this;
    }

    @Override
    public When thenDo(Callable<?> operation)
    {
        checkThat(operation)
            .usingMessage("operation cannot be null")
            .is(notNull());

        actions.put(currentExpectedRequest, operation);
        currentExpectedRequest = null;

        return this;
    }

    @Override
    public When thenThrow(Exception ex)
    {
        actions.put(currentExpectedRequest, Actions.throwException(ex));
        currentExpectedRequest = null;

        return this;
    }

    @Override
    public When thenReturnPOJO(Object pojo)
    {
        actions.put(currentExpectedRequest, Actions.returnPojo(pojo));
        currentExpectedRequest = null;

        return this;
    }

    @Override
    public When thenReturnPOJOAsJSON(Object pojo)
    {
        actions.put(currentExpectedRequest, Actions.returnPojoAsJSON(pojo, gson));
        currentExpectedRequest = null;

        return this;
    }

    @Override
    public When thenReturnJson(JsonElement json)
    {
        checkThat(json)
            .usingMessage("json cannot be null")
            .is(notNull());

        actions.put(currentExpectedRequest, Actions.returnJson(json));
        currentExpectedRequest = null;

        return this;
    }

    @Override
    public When thenReturnResponse(HttpResponse response)
    {
        checkThat(response)
            .usingMessage("response cannot be null")
            .is(notNull());

        actions.put(currentExpectedRequest, Actions.returnResponse(response));
        currentExpectedRequest = null;

        return this;
    }

}

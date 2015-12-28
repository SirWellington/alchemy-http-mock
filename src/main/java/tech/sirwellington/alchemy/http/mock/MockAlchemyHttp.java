/*
 * Copyright 2015 SirWellington Tech.
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

package tech.sirwellington.alchemy.http.mock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.annotations.designs.StepMachineDesign;
import tech.sirwellington.alchemy.http.AlchemyHttp;
import tech.sirwellington.alchemy.http.AlchemyRequest;
import tech.sirwellington.alchemy.http.HttpResponse;
import tech.sirwellington.alchemy.http.exceptions.AlchemyHttpException;

import static java.lang.String.format;
import static junit.framework.Assert.fail;
import static tech.sirwellington.alchemy.annotations.designs.StepMachineDesign.Role.MACHINE;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.instanceOf;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.CollectionAssertions.keyInMap;

/**
 *
 * @author SirWellington
 */
@Internal
@StepMachineDesign(role = MACHINE)
class MockAlchemyHttp implements AlchemyHttp
{

    private final static Logger LOG = LoggerFactory.getLogger(MockAlchemyHttp.class);

    private final Map<MockRequest, Callable<?>> expectedActions = Maps.newConcurrentMap();

    private final List<MockRequest> requestsMade = Lists.newArrayList();

    MockAlchemyHttp(Map<MockRequest, Callable<?>> expectedActions)
    {
        checkThat(expectedActions)
            .is(notNull());

        this.expectedActions.putAll(expectedActions);
    }

    @Override
    public AlchemyHttp usingDefaultHeader(String key, String value)
    {
        return this;
    }

    @Override
    public Map<String, String> getDefaultHeaders()
    {
        return Collections.emptyMap();
    }

    @Override
    public AlchemyRequest.Step1 go()
    {
        return new MockSteps.MockStep1(this);
    }

    @Internal
    HttpResponse getResponseFor(MockRequest request) throws AlchemyHttpException
    {
        checkThat(request)
            .is(notNull())
            .usingMessage("unexpected request: " + request)
            .is(keyInMap(expectedActions));

        Callable<?> action = expectedActions.get(request);

        Object response;
        try
        {
            response = action.call();
        }
        catch (Exception ex)
        {
            throw new AlchemyHttpException(ex);
        }

        checkThat(response)
            .usingMessage(format("Response Type Wanted: %s but actual: null", HttpResponse.class))
            .is(notNull())
            .usingMessage(format("Response Type Wanted: %s but actual: %s", HttpResponse.class, response.getClass()))
            .is(instanceOf(HttpResponse.class));

        return (HttpResponse) response;
    }

    @Internal
    <T> T getResponseFor(MockRequest request, Class<T> expectedClass) throws AlchemyHttpException
    {
        checkThat(request, expectedClass)
            .are(notNull());

        checkThat(request)
            .usingMessage("Request not expected: " + request)
            .is(keyInMap(expectedActions));

        Callable<?> operation = expectedActions.get(request);

        Object responseObject;
        try
        {
            responseObject = operation.call();
        }
        catch (Exception ex)
        {
            throw new AlchemyHttpException(ex);
        }

        if (responseObject == null)
        {
            return (T) responseObject;
        }

        checkThat(responseObject)
            .usingMessage(format("Response Type Wanted: %s but actual: %s", responseObject.getClass(), expectedClass))
            .is(instanceOf(expectedClass));

        return (T) responseObject;

    }

    @Internal
    void verifyAllRequestsMade()
    {
        for (MockRequest request : expectedActions.keySet())
        {
            if (!requestsMade.contains(request))
            {
                fail(format("Request never made: %s", request));
            }
        }
    }

}

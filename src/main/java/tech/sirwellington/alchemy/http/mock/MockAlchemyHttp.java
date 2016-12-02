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
import java.util.Objects;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.annotations.designs.StepMachineDesign;
import tech.sirwellington.alchemy.arguments.AlchemyAssertion;
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
import static tech.sirwellington.alchemy.http.mock.MockRequest.ANY_BODY;

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
            .is(expectedRequest());
        
        requestsMade.add(request);
        
        Callable<?> action = findMatchingActionFor(request);

        Object response;
        try
        {
            response = action.call();
        }
        catch(AlchemyHttpException ex)
        {
            throw ex;
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

        requestsMade.add(request);

        checkThat(request)
            .usingMessage("Unexpected Request: " + request)
            .is(expectedRequest());

        Callable<?> operation = findMatchingActionFor(request);

        Object responseObject;
        try
        {
            responseObject = operation.call();
        }
        catch(AlchemyHttpException ex)
        {
            throw ex;
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
        expected:
        for (MockRequest expectedRequest : expectedActions.keySet())
        {
            made:
            for (MockRequest requestMade : requestsMade)
            {
                if (requestsMatch(expectedRequest, requestMade))
                {
                    continue expected;
                }
            }

            //Reaching here means no match was found
            fail("Request never made: " + expectedRequest);
        }
    }

    private AlchemyAssertion<MockRequest> expectedRequest()
    {
        return request ->
        {
            checkThat(request)
                .is(notNull());
            
            Callable<?> action = findMatchingActionFor(request);
            
            checkThat(action)
                .usingMessage("Request was unexpected: " + request)
                .is(notNull());
        };
    }

    private Callable<?> findMatchingActionFor(MockRequest request)
    {
        Callable<?> foundInMap = expectedActions.get(request);

        if (foundInMap != null)
        {
            return foundInMap;
        }

        for (MockRequest element : expectedActions.keySet())
        {
            if (requestsMatch(element, request))
            {
                return expectedActions.get(element);
            }
        }

        return null;
    }

    private boolean requestsMatch(MockRequest expected, MockRequest actual)
    {
        boolean matchEverythingBesidesTheBody = matchEverythingBesidesTheBody(expected, actual);

        if (!matchEverythingBesidesTheBody)
        {
            return false;
        }

        if (expected.body == ANY_BODY)
        {
            return true;
        }

        /*
         * The bodies will be both null, or both set to NO_BODY. == is intentionally used to compare instances.
         */
        return expected.body == actual.body;
    }

    private boolean matchEverythingBesidesTheBody(MockRequest first, MockRequest second)
    {
        return Objects.equals(first.method, second.method) &&
               Objects.equals(first.url, second.url) &&
               Objects.equals(first.queryParams, second.queryParams);
    }

}

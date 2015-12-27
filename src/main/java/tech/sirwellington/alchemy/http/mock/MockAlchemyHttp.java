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

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.http.AlchemyHttp;
import tech.sirwellington.alchemy.http.AlchemyRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;

/**
 *
 * @author SirWellington
 */
@Internal
class MockAlchemyHttp implements AlchemyHttp
{

    private final static Logger LOG = LoggerFactory.getLogger(MockAlchemyHttp.class);

    MockRequest currentRequest = new MockRequest();

    private final Map<MockRequest, Callable<?>> expectedActions = Maps.newConcurrentMap();

    private final Map<MockRequest, Callable<?>> actionsMade = Maps.newConcurrentMap();

    MockAlchemyHttp(Map<MockRequest, Callable<?>> expectedActions)
    {
        checkThat(expectedActions)
            .is(notNull());
        
        this.expectedActions.putAll(expectedActions);
    }

    
    
    /**
     * This operation allows the Mock internal steps to signal when a request is done. A Request is done when one of the {@link AlchemyRequest}
     * {@link AlchemyRequest.Step3#at(java.lang.String) } methods have been called.
     */
    @Internal
    void done()
    {
        
        
        //Current Request is now null
        currentRequest = null;
    }
    
    @Internal
    void addActionMade(Callable<?> action)
    {
        checkThat(action).is(notNull());
        
        actionsMade.put(currentRequest, action);
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

    void verifyAllRequestsMade()
    {
        for (MockRequest request : expectedActions.keySet())
        {
            assertThat(actionsMade, hasKey(request));
        }
    }

}

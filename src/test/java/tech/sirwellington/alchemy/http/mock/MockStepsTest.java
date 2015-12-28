/*
 * Copyright 2015 Aroma Tech.
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.sirwellington.alchemy.http.AlchemyRequest;
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep1;
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep2;
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep3;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;


/**
 *
 * @author SirWellington
 */
@Repeat(10)
@RunWith(AlchemyTestRunner.class)
public class MockStepsTest 
{
    @Mock
    private MockAlchemyHttp mockHttp;
    
    @GeneratePojo
    private MockRequest request;
    
    
    @Before
    public void setUp()
    {
    }

    @Test
    public void testStep1()
    {
        //Test constructor
        assertThrows(() -> new MockStep1(null)).isInstanceOf(IllegalArgumentException.class);
        MockStep1 step1 = new MockStep1(mockHttp);
        
        AlchemyRequest.Step3 get = step1.get();
        assertThat(get, notNullValue());
        assertThat(get, is(instanceOf(MockStep3.class)));
        MockStep3 mockStep3 = (MockStep3) get;
        assertThat(mockStep3.request.method, is(MockRequest.Method.GET));
        
        AlchemyRequest.Step2 post = step1.post();
        assertThat(post, notNullValue());
        assertThat(post, is(instanceOf(MockStep2.class)));
        MockStep2 mockStep2 = (MockStep2) post;
        assertThat(mockStep2.request.method, is(MockRequest.Method.POST));
    }

}
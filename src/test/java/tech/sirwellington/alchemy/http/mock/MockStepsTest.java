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
import tech.sirwellington.alchemy.test.junit.runners.DontRepeat;
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

    @DontRepeat
    @Test
    public void testStep1Constructor()
    {
        assertThrows(() -> new MockStep1(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testStep1Get()
    {
        MockStep1 step1 = new MockStep1(mockHttp);

        AlchemyRequest.Step3 get = step1.get();
        assertThat(get, notNullValue());
        assertThat(get, is(instanceOf(MockStep3.class)));
        MockStep3 mockStep3 = (MockStep3) get;
        assertThat(mockStep3.request.method, is(MockRequest.Method.GET));
    }

    @Test
    public void testStep1Post()
    {
        MockStep1 step1 = new MockStep1(mockHttp);

        AlchemyRequest.Step2 post = step1.post();
        assertThat(post, notNullValue());
        assertThat(post, is(instanceOf(MockStep2.class)));
        MockStep2 mockStep2 = (MockStep2) post;
        assertThat(mockStep2.request.method, is(MockRequest.Method.POST));
    }

    @Test
    public void testStep1Put()
    {
        MockStep1 step1 = new MockStep1(mockHttp);
        
        AlchemyRequest.Step2 put = step1.put();
        assertThat(put, notNullValue());
        assertThat(put, is(instanceOf(MockStep2.class)));
        MockStep2 mockStep2 = (MockStep2) put;
        assertThat(mockStep2.request.method, is(MockRequest.Method.PUT));
    }

    @Test
    public void testStep1Delete()
    {
        MockStep1 step1 = new MockStep1(mockHttp);
        
        AlchemyRequest.Step2 delete = step1.delete();
        assertThat(delete, notNullValue());
        assertThat(delete, is(instanceOf(MockStep2.class)));
        MockStep2 mockStep2 = (MockStep2) delete;
        assertThat(mockStep2.request.method, is(MockRequest.Method.DELETE));
    }
    
    @DontRepeat
    @Test
    public void testStep2Constructor()
    {
        assertThrows(() -> new MockStep2(null, request))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThrows(() -> new MockStep2(mockHttp, null))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void testStep2WithNoBody()
    {
        MockStep2 step2 = new MockStep2(mockHttp, request);
        
        AlchemyRequest.Step3 nothing = step2.nothing();
        assertThat(nothing, notNullValue());
        assertThat(nothing, is(instanceOf(MockStep3.class)));
        MockStep3 step3 = (MockStep3) nothing;
        assertThat(step3.mockAlchemyHttp, is(mockHttp));
        assertThat(step3.request.method, is(request.method));
        assertThat(step3.request.body, is(MockRequest.NO_BODY));
    }
    
    @Test
    public void testStep2WithString()
    {
        MockStep2 step2 = new MockStep2(mockHttp, request);
        
    }
    
    @Test
    public void testStep2WithPojo()
    {
        MockStep2 step2 = new MockStep2(mockHttp, request);
    }
    

}
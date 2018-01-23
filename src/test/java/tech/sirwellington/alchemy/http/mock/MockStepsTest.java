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

import java.net.URL;
import java.time.Instant;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.sirwellington.alchemy.http.AlchemyRequestSteps;
import tech.sirwellington.alchemy.http.AlchemyRequestSteps.OnFailure;
import tech.sirwellington.alchemy.http.AlchemyRequestSteps.OnSuccess;
import tech.sirwellington.alchemy.http.HttpResponse;
import tech.sirwellington.alchemy.http.exceptions.AlchemyHttpException;
import tech.sirwellington.alchemy.http.mock.MockSteps.*;
import tech.sirwellington.alchemy.test.junit.runners.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.NetworkGenerators.httpUrls;
import static tech.sirwellington.alchemy.generator.StringGenerators.strings;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.*;

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

    @Mock
    private OnSuccess<HttpResponse> successCallback;

    @Mock
    private OnFailure failureCallback;

    @GeneratePojo
    private MockRequest request;

    @GenerateDate
    private Date date;

    @Mock
    private HttpResponse httpResponse;

    @Before
    public void setUp()
    {

        when(mockHttp.getResponseFor(request))
            .thenReturn(httpResponse);
    }

    @DontRepeat
    @Test
    public void testCannotInstantiateClass()
    {
        assertThrows(() -> new MockSteps());
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

        AlchemyRequestSteps.Step3 get = step1.get();
        assertThat(get, notNullValue());
        assertThat(get, is(instanceOf(MockStep3.class)));
        MockStep3 mockStep3 = (MockStep3) get;
        assertThat(mockStep3.getRequest().method, is(MockRequest.Method.GET));
    }

    @Test
    public void testStep1Post()
    {
        MockStep1 step1 = new MockStep1(mockHttp);

        AlchemyRequestSteps.Step2 post = step1.post();
        assertThat(post, notNullValue());
        assertThat(post, is(instanceOf(MockStep2.class)));
        MockStep2 mockStep2 = (MockStep2) post;
        assertThat(mockStep2.getRequest().method, is(MockRequest.Method.POST));
    }

    @Test
    public void testStep1Put()
    {
        MockStep1 step1 = new MockStep1(mockHttp);

        AlchemyRequestSteps.Step2 put = step1.put();
        assertThat(put, notNullValue());
        assertThat(put, is(instanceOf(MockStep2.class)));
        MockStep2 mockStep2 = (MockStep2) put;
        assertThat(mockStep2.getRequest().method, is(MockRequest.Method.PUT));
    }

    @Test
    public void testStep1Delete()
    {
        MockStep1 step1 = new MockStep1(mockHttp);

        AlchemyRequestSteps.Step2 delete = step1.delete();
        assertThat(delete, notNullValue());
        assertThat(delete, is(instanceOf(MockStep2.class)));
        MockStep2 mockStep2 = (MockStep2) delete;
        assertThat(mockStep2.getRequest().method, is(MockRequest.Method.DELETE));
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

        AlchemyRequestSteps.Step3 nothing = step2.nothing();
        assertThat(nothing, notNullValue());
        assertThat(nothing, is(instanceOf(MockStep3.class)));

        MockStep3 step3 = (MockStep3) nothing;
        assertThat(step3.getMockAlchemyHttp(), is(mockHttp));
        assertThat(step3.getRequest().method, is(request.method));
        assertThat(step3.getRequest().body, is(MockRequest.NO_BODY));
    }

    @Test
    public void testStep2WithPojo()
    {
        MockStep2 step2 = new MockStep2(mockHttp, request);

        AlchemyRequestSteps.Step3 body = step2.body(date);
        assertThat(body, notNullValue());
        assertThat(body, is(instanceOf(MockStep3.class)));

        MockStep3 step3 = (MockStep3) body;
        assertThat(step3.getMockAlchemyHttp(), is(mockHttp));
        assertThat(step3.getRequest().method, is(request.method));
        assertThat(step3.getRequest().body, is(date));
    }

    @DontRepeat
    @Test
    public void testStep2WithNullPojo()
    {
        MockStep2 step2 = new MockStep2(mockHttp, request);

        assertThrows(() -> step2.body(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testStep2WithString()
    {
        MockStep2 step2 = new MockStep2(mockHttp, request);

        String string = one(strings());
        AlchemyRequestSteps.Step3 body = step2.body(string);
        assertThat(body, notNullValue());
        assertThat(body, is(instanceOf(MockStep3.class)));

        MockStep3 step3 = (MockStep3) body;
        assertThat(step3.getMockAlchemyHttp(), is(mockHttp));
        assertThat(step3.getRequest().method, is(request.method));
        assertThat(step3.getRequest().body, is(string));
    }

    @DontRepeat
    @Test
    public void testStep3Constructor()
    {
        assertThrows(() -> new MockStep3(mockHttp, null))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> new MockStep3(null, request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testStep3()
    {
        MockStep3 step3 = new MockStep3(mockHttp, request);

    }

    @Test
    public void testStep3Expecting()
    {
        MockStep3 step3 = new MockStep3(mockHttp, request);
        List<Class<?>> classes = Arrays.asList(String.class, Date.class, Instant.class);
        Class<?> expected = classes.stream().findAny().get();

        AlchemyRequestSteps.Step4<?> expecting = step3.expecting(expected);
        assertThat(expecting, notNullValue());
        assertThat(expecting, is(instanceOf(MockStep4.class)));

        MockStep4<?> step4 = (MockStep4<?>) expecting;
        assertThat(step4.getExpectedClass(), sameInstance(expected));
        assertThat(step4.getMockAlchemyHttp(), is(mockHttp));
        assertThat(step4.getRequest(), is(request));
    }

    @Test
    public void testStep3At()
    {
        MockStep3 step3 = new MockStep3(mockHttp, request);
        URL url = one(httpUrls());

        when(mockHttp.getResponseFor(request))
            .thenReturn(httpResponse);

        HttpResponse response = step3.at(url);
        assertThat(request.url, is(url));
        verify(mockHttp).getResponseFor(request);

        assertThrows(() -> step3.at((URL) null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testStep4()
    {
        MockStep4<HttpResponse> step4 = new MockStep4<>(mockHttp, request, HttpResponse.class);

        AlchemyRequestSteps.Step5 response = step4.onSuccess(successCallback);

        assertThat(response, notNullValue());
        assertThat(response, is(instanceOf(MockSteps.MockStep5.class)));

        MockSteps.MockStep5 step5 = (MockSteps.MockStep5) response;
        assertThat(step5.getMockAlchemyHttp(), is(mockHttp));
        assertThat(step5.getExpectedClass(), equalTo(HttpResponse.class));
        assertThat(step5.getOnSuccessCallback(), is(successCallback));
        assertThat(step5.getRequest(), is(request));

    }

    @DontRepeat
    @Test
    public void testStep4WithBadArgs()
    {
        assertThrows(() -> new MockStep4<>(null, request, HttpResponse.class));
        assertThrows(() -> new MockStep4<>(mockHttp, null, HttpResponse.class));
        assertThrows(() -> new MockStep4<>(mockHttp, request, null));

        MockStep4<HttpResponse> instance = new MockStep4<>(mockHttp, request, HttpResponse.class);
        assertThrows(() -> instance.onSuccess(null));
    }

    @Test
    public void testStep5()
    {

        MockSteps.MockStep5<HttpResponse> instance = new MockSteps.MockStep5<>(mockHttp, successCallback, HttpResponse.class, request);
        AlchemyRequestSteps.Step6<HttpResponse> response = instance.onFailure(failureCallback);
        assertThat(response, notNullValue());
        assertThat(response, is(instanceOf(MockSteps.MockStep6.class)));

        MockSteps.MockStep6 step6 = (MockSteps.MockStep6) response;
        assertThat(step6.getExpectedClass(), equalTo(HttpResponse.class));
        assertThat(step6.getMockAlchemyHttp(), is(mockHttp));
        assertThat(step6.getOnSuccessCallback(), is(successCallback));
        assertThat(step6.getOnFailureCallback(), is(failureCallback));
        assertThat(step6.getRequest(), is(request));
    }

    @DontRepeat
    @Test
    public void testStep5WithBadArgs()
    {
        assertThrows(() -> new MockSteps.MockStep5<>(null, OnSuccess.Companion.getNO_OP(), Object.class, request));
        assertThrows(() -> new MockSteps.MockStep5<>(mockHttp, null, String.class, request));
        assertThrows(() -> new MockSteps.MockStep5<>(mockHttp, AlchemyRequestSteps.OnSuccess.INSTANCES.NO_OP, null, request));
        assertThrows(() -> new MockSteps.MockStep5<>(mockHttp, AlchemyRequestSteps.OnSuccess.INSTANCES.NO_OP, Object.class, null));

        MockSteps.MockStep5<Object> instance = new MockSteps.MockStep5<>(mockHttp, OnSuccess.INSTANCES.NO_OP, Object.class, request);
        assertThrows(() -> instance.onFailure(null))
            .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    public void testStep6()
    {
        MockSteps.MockStep6 instance = new MockSteps.MockStep6(mockHttp,
                                                               successCallback,
                                                               failureCallback,
                                                               HttpResponse.class,
                                                               request);

        when(mockHttp.getResponseFor(request, HttpResponse.class))
            .thenReturn(httpResponse);

        instance.at(request.url);
        verify(mockHttp).getResponseFor(request, HttpResponse.class);
        verify(successCallback).processResponse(httpResponse);
    }

    @Test
    public void testStep6WhenFails()
    {

        when(mockHttp.getResponseFor(request, HttpResponse.class))
            .thenThrow(new AlchemyHttpException());

        MockSteps.MockStep6 instance = new MockSteps.MockStep6(mockHttp,
                                                               successCallback,
                                                               failureCallback,
                                                               HttpResponse.class,
                                                               request);

        instance.at(request.url);
        verifyZeroInteractions(successCallback);
        verify(failureCallback).handleError(any());
    }

    @DontRepeat
    @Test
    public void testStep6WithBadArgs()
    {
        assertThrows(() -> new MockSteps.MockStep6(null, successCallback, failureCallback, HttpResponse.class, request));
        assertThrows(() -> new MockSteps.MockStep6(mockHttp, null, failureCallback, HttpResponse.class, request));
        assertThrows(() -> new MockSteps.MockStep6(mockHttp, successCallback, null, HttpResponse.class, request));
        assertThrows(() -> new MockSteps.MockStep6(mockHttp, successCallback, failureCallback, null, request));
        assertThrows(() -> new MockSteps.MockStep6(mockHttp, successCallback, failureCallback, HttpResponse.class, null));

        MockSteps.MockStep6 instance = new MockSteps.MockStep6(mockHttp, successCallback, failureCallback, HttpResponse.class, request);
        assertThrows(() -> instance.at((URL) null))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> instance.at((String) null))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> instance.at(""))
            .isInstanceOf(IllegalArgumentException.class);


    }
}

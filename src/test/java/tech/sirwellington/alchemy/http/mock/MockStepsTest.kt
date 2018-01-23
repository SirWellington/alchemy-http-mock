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

package tech.sirwellington.alchemy.http.mock

import com.natpryce.hamkrest.assertion.assertThat
import com.nhaarman.mockito_kotlin.isA
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.sameInstance
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.verifyZeroInteractions
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.NetworkGenerators.Companion.httpUrls
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.strings
import tech.sirwellington.alchemy.http.AlchemyRequestSteps
import tech.sirwellington.alchemy.http.AlchemyRequestSteps.OnFailure
import tech.sirwellington.alchemy.http.AlchemyRequestSteps.OnSuccess
import tech.sirwellington.alchemy.http.HttpResponse
import tech.sirwellington.alchemy.http.RequestMethod
import tech.sirwellington.alchemy.http.exceptions.AlchemyHttpException
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep1
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep2
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep3
import tech.sirwellington.alchemy.test.hamcrest.notNull
import tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner
import tech.sirwellington.alchemy.test.junit.runners.DontRepeat
import tech.sirwellington.alchemy.test.junit.runners.GenerateDate
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo
import tech.sirwellington.alchemy.test.junit.runners.Repeat
import java.net.URL
import java.time.Instant
import java.util.Arrays
import java.util.Date

/**
 *
 * @author SirWellington
 */
@Repeat(10)
@RunWith(AlchemyTestRunner::class)
class MockStepsTest
{

    @Mock
    private lateinit var  mockHttp: MockAlchemyHttp

    @Mock
    private lateinit var  successCallback: OnSuccess<HttpResponse>

    @Mock
    private lateinit var  failureCallback: OnFailure

    @GeneratePojo
    private lateinit var  request: MockRequest

    @GenerateDate
    private lateinit var  date: Date

    @Mock
    private lateinit var httpResponse: HttpResponse

    @Before
    fun setUp()
    {

        whenever(mockHttp.getResponseFor(request))
                .thenReturn(httpResponse)
    }

    @DontRepeat
    @Test
    fun testCannotInstantiateClass()
    {
        assertThrows { MockSteps::class.java.newInstance() }
    }

    @Test
    fun testStep1Get()
    {
        val step1 = MockStep1(mockHttp)

        val get = step1.get()
        assertThat(get, notNull)
        assertThat(get, isA<MockStep3>())
        val mockStep3 = get as MockStep3
        assertThat(mockStep3.request.method, equalTo<RequestMethod>(MockRequest.Method.GET))
    }

    @Test
    fun testStep1Post()
    {
        val step1 = MockStep1(mockHttp)

        val post = step1.post()
        assertThat(post, notNullValue())
        assertThat(post, equalTo(instanceOf<Any>(MockStep2::class.java)))
        val mockStep2 = post as MockStep2
        assertThat(mockStep2.request.method, equalTo(MockRequest.Method.POST))
    }

    @Test
    fun testStep1Put()
    {
        val step1 = MockStep1(mockHttp)

        val put = step1.put()
        assertThat(put, notNullValue())
        assertThat(put, equalTo(instanceOf<Any>(MockStep2::class.java)))
        val mockStep2 = put as MockStep2
        assertThat(mockStep2.request.method, equalTo(MockRequest.Method.PUT))
    }

    @Test
    fun testStep1Delete()
    {
        val step1 = MockStep1(mockHttp)

        val delete = step1.delete()
        assertThat(delete, notNullValue())
        assertThat(delete, equalTo(instanceOf<Any>(MockStep2::class.java)))
        val mockStep2 = delete as MockStep2
        assertThat(mockStep2.request.method, equalTo(MockRequest.Method.DELETE))
    }

    @Test
    fun testStep2WithNoBody()
    {
        val step2 = MockStep2(mockHttp, request)

        val nothing = step2.nothing()
        assertThat(nothing, notNullValue())
        assertThat(nothing, equalTo(instanceOf<Any>(MockStep3::class.java)))

        val step3 = nothing as MockStep3
        assertThat(step3.mockAlchemyHttp, equalTo(mockHttp))
        assertThat(step3.request.method, equalTo(request.method))
        assertThat(step3.request.body, equalTo(MockRequest.NO_BODY))
    }

    @Test
    fun testStep2WithPojo()
    {
        val step2 = MockStep2(mockHttp, request)

        val body = step2.body(date)
        assertThat(body, notNull)
        assertThat(body, isA<MockStep3>())

        val step3 = body as MockStep3
        assertThat(step3.mockAlchemyHttp, equalTo<MockAlchemyHttp>(mockHttp))
        assertThat(step3.request.method, equalTo(request.method))
        assertThat(step3.request.body, equalTo(date))
    }

    @DontRepeat
    @Test
    fun testStep2WithNullPojo()
    {
        val step2 = MockStep2(mockHttp, request)

        assertThrows { step2.body(null) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testStep2WithString()
    {
        val step2 = MockStep2(mockHttp, request)

        val string = one<String>(strings())
        val body = step2.body(string)
        assertThat<Step3>(body, notNullValue())
        assertThat<Step3>(body, equalTo(instanceOf<Any>(MockStep3::class.java)))

        val step3 = body as MockStep3
        assertThat(step3.mockAlchemyHttp, equalTo<MockAlchemyHttp>(mockHttp))
        assertThat<Method>(step3.request.method, equalTo<Method>(request.method))
        assertThat(step3.request.body, equalTo(string))
    }

    @DontRepeat
    @Test
    fun testStep3Constructor()
    {
        assertThrows { MockStep3(mockHttp, null) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { MockStep3(null, request) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testStep3()
    {
        val step3 = MockStep3(mockHttp, request)

    }

    @Test
    fun testStep3Expecting()
    {
        val step3 = MockStep3(mockHttp, request)
        val classes = Arrays.asList<Class<out Serializable>>(String::class.java, Date::class.java, Instant::class.java)
        val expected = classes.stream().findAny().get()

        val expecting = step3.expecting(expected)
        assertThat<Step4<*>>(expecting, notNullValue())
        assertThat<Step4<*>>(expecting, equalTo(instanceOf<Any>(MockStep4<*>::class.java)))

        val step4 = expecting as MockStep4<*>
        assertThat(step4.expectedClass, sameInstance<Class<*>>(expected))
        assertThat(step4.mockAlchemyHttp, equalTo<MockAlchemyHttp>(mockHttp))
        assertThat(step4.request, equalTo<MockRequest>(request))
    }

    @Test
    fun testStep3At()
    {
        val step3 = MockStep3(mockHttp, request)
        val url = one<URL>(httpUrls())

        whenever(mockHttp.getResponseFor(request))
                .thenReturn(httpResponse)

        val response = step3.at(url)
        assertThat(request.url, equalTo(url))
        verify(mockHttp).getResponseFor(request)

        assertThrows { step3.at((null as URL?)) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testStep4()
    {
        val step4 = MockStep4<HttpResponse>(mockHttp, request, HttpResponse::class.java)

        val response = step4.onSuccess(successCallback)

        assertThat<Step5>(response, notNullValue())
        assertThat<Step5>(response, equalTo(instanceOf<Any>(MockSteps.MockStep5<*>::class.java)))

        val step5 = response as MockSteps.MockStep5<*>
        assertThat(step5.mockAlchemyHttp, equalTo<MockAlchemyHttp>(mockHttp))
        assertThat(step5.expectedClass, equalTo(HttpResponse::class.java))
        assertThat(step5.onSuccessCallback, equalTo(successCallback))
        assertThat(step5.request, equalTo<MockRequest>(request))

    }

    @DontRepeat
    @Test
    fun testStep4WithBadArgs()
    {
        assertThrows { MockStep4<HttpResponse>(null, request, HttpResponse::class.java) }
        assertThrows { MockStep4<HttpResponse>(mockHttp, null, HttpResponse::class.java) }
        assertThrows { MockStep4<Any>(mockHttp, request, null) }

        val instance = MockStep4<HttpResponse>(mockHttp, request, HttpResponse::class.java)
        assertThrows { instance.onSuccess(null) }
    }

    @Test
    fun testStep5()
    {

        val instance = MockSteps.MockStep5(mockHttp, successCallback, HttpResponse::class.java, request)
        val response = instance.onFailure(failureCallback)
        assertThat<Step6<HttpResponse>>(response, notNullValue())
        assertThat<Step6<HttpResponse>>(response, equalTo(instanceOf<Any>(MockSteps.MockStep6<*>::class.java)))

        val step6 = response as MockSteps.MockStep6<*>
        assertThat(step6.expectedClass, equalTo(HttpResponse::class.java))
        assertThat(step6.mockAlchemyHttp, equalTo(mockHttp))
        assertThat(step6.onSuccessCallback, equalTo(successCallback))
        assertThat(step6.onFailureCallback, equalTo(failureCallback))
        assertThat(step6.request, equalTo(request))
    }

    @DontRepeat
    @Test
    fun testStep5WithBadArgs()
    {
        assertThrows { MockSteps.MockStep5<Any>(null, OnSuccess.NO_OP, Any::class.java, request) }
        assertThrows { MockSteps.MockStep5(mockHttp, null, String::class.java, request) }
        assertThrows { MockSteps.MockStep5<Any>(mockHttp, AlchemyRequestSteps.OnSuccess.INSTANCES.NO_OP, null, request) }
        assertThrows { MockSteps.MockStep5<Any>(mockHttp, AlchemyRequestSteps.OnSuccess.INSTANCES.NO_OP, Any::class.java, null) }

        val instance = MockSteps.MockStep5<Any>(mockHttp, OnSuccess.INSTANCES.NO_OP, Any::class.java, request)
        assertThrows { instance.onFailure(null) }
                .isInstanceOf(IllegalArgumentException::class.java)

    }

    @Test
    fun testStep6()
    {
        val instance = MockSteps.MockStep6(mockHttp,
                                           successCallback,
                                           failureCallback,
                                           HttpResponse::class.java,
                                           request)

        whenever(mockHttp.getResponseFor(request, HttpResponse::class.java))
                .thenReturn(httpResponse)

        instance.at(request.url)
        verify(mockHttp).getResponseFor(request, HttpResponse::class.java)
        verify(successCallback).processResponse(httpResponse)
    }

    @Test
    fun testStep6WhenFails()
    {

        whenever(mockHttp.getResponseFor(request, HttpResponse::class.java))
                .thenThrow(AlchemyHttpException())

        val instance = MockSteps.MockStep6(mockHttp,
                                           successCallback,
                                           failureCallback,
                                           HttpResponse::class.java,
                                           request)

        instance.at(request.url)
        verifyZeroInteractions(successCallback)
        verify(failureCallback).handleError(ArgumentMatchers.any())
    }

    @DontRepeat
    @Test
    fun testStep6WithBadArgs()
    {
        val instance = MockSteps.MockStep6(mockHttp, successCallback, failureCallback, HttpResponse::class.java, request)

        assertThrows { instance.at("") }
                .isInstanceOf(IllegalArgumentException::class.java)


    }
}

/*
 * Copyright © 2019. Sir Wellington.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.sirwellington.alchemy.http.mock

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isA
import com.natpryce.hamkrest.present
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.verifyZeroInteractions
import sir.wellington.alchemy.collections.lists.Lists
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.NetworkGenerators.Companion.httpUrls
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.strings
import tech.sirwellington.alchemy.http.AlchemyRequestSteps.OnFailure
import tech.sirwellington.alchemy.http.AlchemyRequestSteps.OnSuccess
import tech.sirwellington.alchemy.http.HttpResponse
import tech.sirwellington.alchemy.http.RequestMethod
import tech.sirwellington.alchemy.http.exceptions.AlchemyHttpException
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep1
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep2
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep3
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep4
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep5
import tech.sirwellington.alchemy.http.mock.MockSteps.MockStep6
import tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows
import tech.sirwellington.alchemy.test.junit.runners.*
import java.net.URL
import java.time.Instant
import java.util.Date

/**
 *
 * @author SirWellington
 */
@Repeat(10)
@RunWith(AlchemyTestRunner::class)
class MockStepsTest
{

    @Mock(answer = Answers.RETURNS_MOCKS)
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
        assertThat(get, present())
        assertThat(get, isA<MockStep3>())
        val mockStep3 = get as MockStep3
        assertThat(mockStep3.request.method, present(equalTo(RequestMethod.GET)))
    }

    @Test
    fun testStep1Post()
    {
        val step1 = MockStep1(mockHttp)

        val post = step1.post()
        assertThat(post, present())
        assertThat(post, isA<MockStep2>())
        val mockStep2 = post as MockStep2
        assertThat(mockStep2.request.method, equalTo(RequestMethod.POST))
    }

    @Test
    fun testStep1Put()
    {
        val step1 = MockStep1(mockHttp)

        val put = step1.put()
        assertThat(put, present())
        assertThat(put, isA<MockStep2>())
        val mockStep2 = put as MockStep2
        assertThat(mockStep2.request.method!!, equalTo(RequestMethod.PUT))
    }

    @Test
    fun testStep1Delete()
    {
        val step1 = MockStep1(mockHttp)

        val delete = step1.delete()
        assertThat(delete, present())
        assertThat(delete, isA<MockStep2>())
        val mockStep2 = delete as MockStep2
        assertThat(mockStep2.request.method, equalTo(RequestMethod.DELETE))
    }

    @Test
    fun testStep2WithNoBody()
    {
        val step2 = MockStep2(mockHttp, request)

        val nothing = step2.nothing()
        assertThat(nothing, present())
        assertThat(nothing, isA<MockStep3>())

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
        assertThat(body, present())
        assertThat(body, isA<MockStep3>())

        val step3 = body as MockStep3
        assertThat(step3.mockAlchemyHttp, equalTo(mockHttp))
        assertThat(step3.request.method, equalTo(request.method))
        assertThat(step3.request.body, present<Any>(equalTo(date)))
    }


    @Test
    fun testStep2WithString()
    {
        val step2 = MockStep2(mockHttp, request)

        val string = one<String>(strings())
        val body = step2.body(string)
        assertThat(body, present())
        assertThat(body, isA<MockStep3>())

        val step3 = body as MockStep3
        assertThat(step3.mockAlchemyHttp, equalTo(mockHttp))
        assertThat(step3.request.method, equalTo(request.method))
        assertThat(step3.request.body, present<Any>(equalTo(string)))
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
        val classes = Lists.createFrom(String::class.java, Date::class.java, Instant::class.java)
        val expected = classes.stream().findAny().get()

        val expecting = step3.expecting(expected)
        assertThat(expecting, present())
        assertThat(expecting, isA<MockStep4<*>>())

        val step4 = expecting as MockStep4<*>
        assertThat(step4.expectedClass, present<Class<*>>(equalTo(expected)))
        assertThat(step4.mockAlchemyHttp, equalTo(mockHttp))
        assertThat(step4.request, equalTo(request))
    }

    @Test
    fun testStep3At()
    {
        val step3 = MockStep3(mockHttp, request)
        val url = one<URL>(httpUrls())

        whenever(mockHttp.getResponseFor(request))
                .thenReturn(httpResponse)

        val response = step3.at(url)
        assertThat(response, equalTo(httpResponse))
        assertThat(request.url, equalTo(url))
        verify(mockHttp).getResponseFor(request)

    }

    @Test
    fun testStep4()
    {
        val step4 = MockStep4(mockHttp, request, HttpResponse::class.java)

        val response = step4.onSuccess(successCallback)

        assertThat(response, present())
        assertThat(response, isA<MockStep5<*>>())

        val step5 = response as MockSteps.MockStep5<*>
        assertThat(step5.mockAlchemyHttp, equalTo(mockHttp))
        assertThat(step5.expectedClass, present<Class<*>>(equalTo(HttpResponse::class.java)))
        assertThat(step5.onSuccessCallback, present<Any>(equalTo(successCallback)))
        assertThat(step5.request, equalTo(request))

    }

    @Test
    fun testStep5()
    {

        val instance = MockSteps.MockStep5(mockHttp, successCallback, HttpResponse::class.java, request)
        val response = instance.onFailure(failureCallback)
        assertThat(response, present())
        assertThat(response, isA<MockStep6<*>>())

        val step6 = response as MockSteps.MockStep6<*>
        assertThat(step6.expectedClass, equalTo<Class<*>>(HttpResponse::class.java))
        assertThat(step6.mockAlchemyHttp, equalTo(mockHttp))
        assertThat(step6.onSuccessCallback, present<Any>(equalTo(successCallback)))
        assertThat(step6.onFailureCallback, equalTo(failureCallback))
        assertThat(step6.request, equalTo(request))
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

        instance.at(request.url!!)
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

        instance.at(request.url!!)
        verifyZeroInteractions(successCallback)
        verify(failureCallback).handleError(any())
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

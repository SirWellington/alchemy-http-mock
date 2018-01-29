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

import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.designs.StepMachineDesign
import tech.sirwellington.alchemy.annotations.designs.StepMachineDesign.Role.STEP
import tech.sirwellington.alchemy.arguments.Arguments.checkThat
import tech.sirwellington.alchemy.arguments.assertions.nonEmptyString
import tech.sirwellington.alchemy.arguments.assertions.nonNullReference
import tech.sirwellington.alchemy.arguments.assertions.notNull
import tech.sirwellington.alchemy.arguments.assertions.validURL
import tech.sirwellington.alchemy.http.AlchemyRequestSteps
import tech.sirwellington.alchemy.http.HttpResponse
import tech.sirwellington.alchemy.http.RequestMethod
import tech.sirwellington.alchemy.http.exceptions.AlchemyHttpException
import tech.sirwellington.alchemy.http.exceptions.OperationFailedException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

/**
 *
 * @author SirWellington
 */
@NonInstantiable
internal object MockSteps
{


    @StepMachineDesign(role = STEP)
    internal class MockStep1(val mockHttp: MockAlchemyHttp) : AlchemyRequestSteps.Step1
    {

        internal val request = MockRequest()

        init
        {
            checkThat(mockHttp).isA(nonNullReference())
        }

        override fun get(): AlchemyRequestSteps.Step3
        {
            request.method = RequestMethod.GET
            return MockStep3(mockHttp, request)
        }

        override fun post(): AlchemyRequestSteps.Step2
        {
            request.method = RequestMethod.POST

            return MockStep2(mockHttp, request)
        }

        override fun put(): AlchemyRequestSteps.Step2
        {
            request.method = RequestMethod.PUT

            return MockStep2(mockHttp, request)
        }

        override fun delete(): AlchemyRequestSteps.Step2
        {
            request.method = RequestMethod.DELETE

            return MockStep2(mockHttp, request)
        }

        @Throws(IllegalArgumentException::class, AlchemyHttpException::class)
        override fun download(url: URL): ByteArray
        {
            try
            {
                return url.openStream().readBytes(1024 * 4)
            }
            catch (ex: IOException)
            {
                throw OperationFailedException(ex)
            }

        }

        @Throws(IllegalArgumentException::class)
        override fun download(s: String): ByteArray
        {
            try
            {
                return download(URL(s))
            }
            catch (ex: MalformedURLException)
            {
                throw IllegalArgumentException(ex)
            }

        }
    }

    @StepMachineDesign(role = STEP)
    internal class MockStep2(internal var mockAlchemyHttp: MockAlchemyHttp,
                             internal var request: MockRequest) : AlchemyRequestSteps.Step2
    {

        init
        {
            checkThat(mockAlchemyHttp, request)
                    .are(notNull())
        }

        override fun noBody(): AlchemyRequestSteps.Step3
        {
            request.body = MockRequest.NO_BODY

            return MockStep3(mockAlchemyHttp, request)
        }

        @Throws(IllegalArgumentException::class)
        override fun body(jsonString: String): AlchemyRequestSteps.Step3
        {
            checkThat(jsonString)
                    .usingMessage("jsonString cannot be empty")
                    .isA(nonEmptyString())

            request.body = jsonString

            return MockStep3(mockAlchemyHttp, request)
        }

        @Throws(IllegalArgumentException::class)
        override fun body(pojo: Any): AlchemyRequestSteps.Step3
        {
            request.body = pojo

            return MockStep3(mockAlchemyHttp, request)
        }

    }

    @StepMachineDesign(role = STEP)
    internal class MockStep3(val mockAlchemyHttp: MockAlchemyHttp,
                             val request: MockRequest) : AlchemyRequestSteps.Step3
    {

        init
        {
            checkThat(mockAlchemyHttp, request)
                    .are(notNull())
        }

        @Throws(IllegalArgumentException::class)
        override fun accept(mediaType: String, vararg others: String): AlchemyRequestSteps.Step3
        {
            val tail = others.joinToString(", ")
            val header = arrayOf(mediaType, tail).joinToString(", ")

            return this.usingHeader("Accept", header)
        }

        @Throws(IllegalArgumentException::class)
        override fun usingQueryParam(name: String, value: Number): AlchemyRequestSteps.Step3
        {
            return usingQueryParam(name, value.toString())
        }

        @Throws(IllegalArgumentException::class)
        override fun usingQueryParam(name: String, value: Boolean): AlchemyRequestSteps.Step3
        {
            return this
        }

        override fun followRedirects(): AlchemyRequestSteps.Step3
        {
            return this
        }

        @Throws(IllegalArgumentException::class, AlchemyHttpException::class, MalformedURLException::class)
        override fun at(url: String): HttpResponse
        {
            return this.at(URL(url))
        }

        @Throws(IllegalArgumentException::class)
        override fun usingHeader(key: String, value: String): AlchemyRequestSteps.Step3
        {
            return this
        }

        @Throws(IllegalArgumentException::class)
        override fun usingQueryParam(name: String, value: String): AlchemyRequestSteps.Step3
        {
            return this
        }

        @Throws(IllegalArgumentException::class)
        override fun followRedirects(maxNumberOfTimes: Int): AlchemyRequestSteps.Step3
        {
            return this
        }

        @Throws(AlchemyHttpException::class)
        override fun at(url: URL): HttpResponse
        {
            checkThat(url)
                    .usingMessage("missing url")
                    .isA(nonNullReference())

            request.url = url
            return mockAlchemyHttp.getResponseFor(request)
        }

        override fun onSuccess(onSuccessCallback: AlchemyRequestSteps.OnSuccess<HttpResponse>): AlchemyRequestSteps.Step5<HttpResponse>
        {
            checkThat(onSuccessCallback)
                    .usingMessage("Callback cannot be null")
                    .isA(nonNullReference())

            return MockStep5(mockAlchemyHttp, onSuccessCallback, HttpResponse::class.java, request)

        }

        @Throws(IllegalArgumentException::class)
        override fun <ResponseType> expecting(classOfResponseType: Class<ResponseType>): AlchemyRequestSteps.Step4<ResponseType>
        {
            return MockStep4(mockAlchemyHttp, this.request, classOfResponseType)
        }

    }

    @StepMachineDesign(role = STEP)
    internal class MockStep4<R>(val mockAlchemyHttp: MockAlchemyHttp, val request: MockRequest, val expectedClass: Class<R>) : AlchemyRequestSteps.Step4<R>
    {

        init
        {
            checkThat(mockAlchemyHttp, request, expectedClass)
                    .are(notNull())
        }

        @Throws(IllegalArgumentException::class, AlchemyHttpException::class)
        override fun at(url: URL): R
        {
            checkThat(url)
                    .usingMessage("url cannot be null")
                    .isA(nonNullReference())

            request.url = url

            return mockAlchemyHttp.getResponseFor(request, expectedClass)
        }

        override fun onSuccess(onSuccessCallback: AlchemyRequestSteps.OnSuccess<R>): AlchemyRequestSteps.Step5<R>
        {
            checkThat(onSuccessCallback)
                    .usingMessage("callback cannot be null")
                    .isA(nonNullReference())

            return MockStep5(mockAlchemyHttp, onSuccessCallback, expectedClass, request)
        }

        @Throws(AlchemyHttpException::class, MalformedURLException::class)
        override fun at(url: String): R
        {
            checkThat(url).isA(validURL())
            return at(URL(url))
        }
    }

    @StepMachineDesign(role = STEP)
    internal class MockStep5<R>(val mockAlchemyHttp: MockAlchemyHttp,
                                val onSuccessCallback: AlchemyRequestSteps.OnSuccess<R>,
                                val expectedClass: Class<R>,
                                val request: MockRequest) : AlchemyRequestSteps.Step5<R>
    {

        init
        {
            checkThat(mockAlchemyHttp, onSuccessCallback, expectedClass, request)
                    .are(notNull())
        }

        override fun onFailure(onFailureCallback: AlchemyRequestSteps.OnFailure): AlchemyRequestSteps.Step6<R>
        {
            checkThat(onFailureCallback)
                    .usingMessage("callback cannot be null")
                    .isA(nonNullReference())

            return MockStep6(mockAlchemyHttp,
                             onSuccessCallback,
                             onFailureCallback,
                             expectedClass,
                             request)
        }

    }

    @StepMachineDesign(role = STEP)
    internal class MockStep6<R>(val mockAlchemyHttp: MockAlchemyHttp,
                                val onSuccessCallback: AlchemyRequestSteps.OnSuccess<R>,
                                val onFailureCallback: AlchemyRequestSteps.OnFailure,
                                val expectedClass: Class<R>,
                                val request: MockRequest) : AlchemyRequestSteps.Step6<R>
    {

        init
        {
            checkThat(mockAlchemyHttp, onSuccessCallback, onFailureCallback, expectedClass, request)
                    .are(notNull())
        }

        override fun at(url: URL)
        {
            checkThat(url)
                    .usingMessage("url cannot be null")
                    .isA(nonNullReference())

            request.url = url

            val response: R?
            try
            {
                response = mockAlchemyHttp.getResponseFor(request, expectedClass)
                onSuccessCallback.processResponse(response)
            }
            catch (ex: Exception)
            {
                val alchemyException = AlchemyHttpException(ex)
                onFailureCallback.handleError(alchemyException)
            }

        }

        @Throws(IllegalArgumentException::class, MalformedURLException::class)
        override fun at(url: String)
        {
            checkThat(url).isA(validURL())
            at(URL(url))
        }
    }

}

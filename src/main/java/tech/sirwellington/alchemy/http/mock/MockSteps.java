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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import kotlin.io.ByteStreamsKt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.designs.StepMachineDesign;
import tech.sirwellington.alchemy.http.AlchemyRequestSteps;
import tech.sirwellington.alchemy.http.HttpResponse;
import tech.sirwellington.alchemy.http.exceptions.AlchemyHttpException;
import tech.sirwellington.alchemy.http.exceptions.OperationFailedException;

import static tech.sirwellington.alchemy.annotations.designs.StepMachineDesign.Role.STEP;
import static tech.sirwellington.alchemy.arguments.Arguments.*;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;
import static tech.sirwellington.alchemy.arguments.assertions.NetworkAssertions.validURL;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.*;
import static tech.sirwellington.alchemy.http.mock.MockRequest.NO_BODY;

/**
 *
 * @author SirWellington
 */
@NonInstantiable
final class MockSteps
{

    private final static Logger LOG = LoggerFactory.getLogger(MockSteps.class);

    MockSteps() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate");
    }

    @StepMachineDesign(role = STEP)
    static class MockStep1 implements AlchemyRequestSteps.Step1
    {

        final MockAlchemyHttp mockHttp;
        final MockRequest request = new MockRequest();

        MockStep1(MockAlchemyHttp mockHttp)
        {
            checkThat(mockHttp).is(notNull());

            this.mockHttp = mockHttp;
        }

        @Override
        public AlchemyRequestSteps.Step3 get()
        {
            request.method = MockRequest.Method.GET;
            return new MockStep3(mockHttp, request);
        }

        @Override
        public AlchemyRequestSteps.Step2 post()
        {
            request.method = MockRequest.Method.POST;

            return new MockStep2(mockHttp, request);
        }

        @Override
        public AlchemyRequestSteps.Step2 put()
        {
            request.method = MockRequest.Method.PUT;

            return new MockStep2(mockHttp, request);
        }

        @Override
        public AlchemyRequestSteps.Step2 delete()
        {
            request.method = MockRequest.Method.DELETE;

            return new MockStep2(mockHttp, request);
        }

        @NotNull
        @Override
        public byte[] download(URL url) throws IllegalArgumentException, AlchemyHttpException
        {
            try
            {
                return ByteStreamsKt.readBytes(url.openStream(), 1024 * 4);
            }
            catch (IOException ex)
            {
                throw new OperationFailedException(ex);
            }
        }

        @NotNull
        @Override
        public byte[] download(String s) throws IllegalArgumentException
        {
            try
            {
                return download(new URL(s));
            }
            catch (MalformedURLException ex)
            {
                throw new IllegalArgumentException(ex);
            }
        }
    }

    @StepMachineDesign(role = STEP)
    static class MockStep2 implements AlchemyRequestSteps.Step2
    {

        MockAlchemyHttp mockAlchemyHttp;
        MockRequest request;

        MockStep2(MockAlchemyHttp mockAlchemyHttp, MockRequest request)
        {
            checkThat(mockAlchemyHttp, request)
                .are(notNull());

            this.mockAlchemyHttp = mockAlchemyHttp;
            this.request = request;
        }

        @Override
        public AlchemyRequestSteps.Step3 nothing()
        {
            request.body = NO_BODY;

            return new MockStep3(mockAlchemyHttp, request);
        }

        @Override
        public AlchemyRequestSteps.Step3 body(String jsonString) throws IllegalArgumentException
        {
            checkThat(jsonString)
                .usingMessage("jsonString cannot be empty")
                .is(nonEmptyString());

            request.body = jsonString;

            return new MockStep3(mockAlchemyHttp, request);
        }

        @Override
        public AlchemyRequestSteps.Step3 body(Object pojo) throws IllegalArgumentException
        {
            request.body = pojo;

            return new MockStep3(mockAlchemyHttp, request);
        }

    }

    @StepMachineDesign(role = STEP)
    static class MockStep3 implements AlchemyRequestSteps.Step3
    {

        final MockAlchemyHttp mockAlchemyHttp;
        final MockRequest request;

        MockStep3(MockAlchemyHttp mockAlchemyHttp, MockRequest request)
        {
            checkThat(mockAlchemyHttp, request)
                .are(notNull());

            this.mockAlchemyHttp = mockAlchemyHttp;
            this.request = request;
        }

        @NotNull
        @Override
        public AlchemyRequestSteps.Step3 accept(String first, String... others) throws IllegalArgumentException
        {
            String tail = String.join(", ", others);
            String header = String.join(", ", first, tail);

            return this.usingHeader("Accept", header);
        }

        @NotNull
        @Override
        public AlchemyRequestSteps.Step3 usingQueryParam(String s, Number number) throws IllegalArgumentException
        {
            return usingQueryParam(s, number.toString());
        }

        @NotNull
        @Override
        public AlchemyRequestSteps.Step3 usingQueryParam(String s, boolean b) throws IllegalArgumentException
        {
            return this;
        }

        @NotNull
        @Override
        public AlchemyRequestSteps.Step3 followRedirects()
        {
            return this;
        }

        @NotNull
        @Override
        public HttpResponse at(String s) throws IllegalArgumentException, AlchemyHttpException, MalformedURLException
        {
            return this.at(new URL(s));
        }

        @Override
        public AlchemyRequestSteps.Step3 usingHeader(String key, String value) throws IllegalArgumentException
        {
            return this;
        }

        @Override
        public AlchemyRequestSteps.Step3 usingQueryParam(String name, String value) throws IllegalArgumentException
        {
            return this;
        }

        @Override
        public AlchemyRequestSteps.Step3 followRedirects(int maxNumberOfTimes) throws IllegalArgumentException
        {
            return this;
        }

        @Override
        public HttpResponse at(URL url) throws AlchemyHttpException
        {
            checkThat(url)
                .usingMessage("missing url")
                .is(notNull());

            request.url = url;
            return mockAlchemyHttp.getResponseFor(request);
        }

        @NotNull
        @Override
        public AlchemyRequestSteps.Step5<HttpResponse> onSuccess(AlchemyRequestSteps.OnSuccess<HttpResponse> onSuccess)
        {
            checkThat(onSuccess)
                    .usingMessage("Callback cannot be null")
                    .is(notNull());

            return new MockStep5<>(mockAlchemyHttp, onSuccess, HttpResponse.class, request);

        }

        @Override
        public <ResponseType> AlchemyRequestSteps.Step4<ResponseType> expecting(Class<ResponseType> classOfResponseType) throws
            IllegalArgumentException
        {
            return new MockStep4<>(mockAlchemyHttp, this.request, classOfResponseType);
        }

    }

    @StepMachineDesign(role = STEP)
    static class MockStep4<R> implements AlchemyRequestSteps.Step4<R>
    {

        final MockAlchemyHttp mockAlchemyHttp;
        final MockRequest request;
        final Class<R> expectedClass;

        MockStep4(MockAlchemyHttp mockAlchemyHttp, MockRequest request, Class<R> expectedClass)
        {
            checkThat(mockAlchemyHttp, request, expectedClass)
                .are(notNull());

            this.mockAlchemyHttp = mockAlchemyHttp;
            this.request = request;
            this.expectedClass = expectedClass;
        }

        @Override
        public R at(URL url) throws IllegalArgumentException, AlchemyHttpException
        {
            checkThat(url).usingMessage("url cannot be null").is(notNull());
            request.url = url;

            return mockAlchemyHttp.getResponseFor(request, expectedClass);
        }

        @NotNull
        @Override
        public AlchemyRequestSteps.Step5<R> onSuccess(AlchemyRequestSteps.OnSuccess<R> onSuccess)
        {
            checkThat(onSuccess)
                    .usingMessage("callback cannot be null")
                    .is(notNull());

            return new MockStep5<>(mockAlchemyHttp, onSuccess, expectedClass, request);
        }

        @Override
        public R at(String s) throws AlchemyHttpException, MalformedURLException
        {
            checkThat(s).isA(validURL());
            return at(new URL(s));
        }
    }

    @StepMachineDesign(role = STEP)
    static class MockStep5<R> implements AlchemyRequestSteps.Step5<R>
    {

        final MockAlchemyHttp mockAlchemyHttp;
        final AlchemyRequestSteps.OnSuccess<R> onSuccessCallback;
        final Class<R> expectedClass;
        final MockRequest request;

        MockStep5(MockAlchemyHttp mockAlchemyHttp,
                  AlchemyRequestSteps.OnSuccess<R> onSuccessCallback,
                  Class<R> expectedClass,
                  MockRequest request)
        {
            checkThat(mockAlchemyHttp, onSuccessCallback, expectedClass, request)
                .are(notNull());

            this.mockAlchemyHttp = mockAlchemyHttp;
            this.onSuccessCallback = onSuccessCallback;
            this.expectedClass = expectedClass;
            this.request = request;
        }

        @Override
        public AlchemyRequestSteps.Step6<R> onFailure(AlchemyRequestSteps.OnFailure onFailureCallback)
        {
            checkThat(onFailureCallback)
                .usingMessage("callback cannot be null")
                .is(notNull());

            return new MockStep6<>(mockAlchemyHttp,
                                   onSuccessCallback,
                                   onFailureCallback,
                                   expectedClass,
                                   request);
        }

    }

    static class MockStep6<R> implements AlchemyRequestSteps.Step6<R>
    {

        final MockAlchemyHttp mockAlchemyHttp;
        final AlchemyRequestSteps.OnSuccess<R> onSuccessCallback;
        final AlchemyRequestSteps.OnFailure onFailureCallback;
        final Class<R> expectedClass;
        final MockRequest request;

        public MockStep6(MockAlchemyHttp mockAlchemyHttp,
                         AlchemyRequestSteps.OnSuccess<R> onSuccessCallback,
                         AlchemyRequestSteps.OnFailure onFailureCallback,
                         Class<R> expectedClass,
                         MockRequest request)
        {
            checkThat(mockAlchemyHttp, onSuccessCallback, onFailureCallback, expectedClass, request)
                .are(notNull());

            this.mockAlchemyHttp = mockAlchemyHttp;
            this.onSuccessCallback = onSuccessCallback;
            this.onFailureCallback = onFailureCallback;
            this.expectedClass = expectedClass;
            this.request = request;
        }

        @Override
        public void at(URL url)
        {
            checkThat(url)
                .usingMessage("url cannot be null")
                .is(notNull());

            request.url = url;

            R response;
            try
            {
                response = mockAlchemyHttp.getResponseFor(request, expectedClass);
                onSuccessCallback.processResponse(response);
            }
            catch (Exception ex)
            {
                AlchemyHttpException alchemyException = new AlchemyHttpException(ex);
                onFailureCallback.handleError(alchemyException);
            }
        }

        @Override
        public void at(String s) throws IllegalArgumentException, MalformedURLException
        {
            checkThat(s).isA(validURL());
            at(new URL(s));
        }
    }

}

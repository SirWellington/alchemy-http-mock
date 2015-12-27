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

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.http.AlchemyHttp;
import tech.sirwellington.alchemy.http.AlchemyRequest;
import tech.sirwellington.alchemy.http.HttpResponse;
import tech.sirwellington.alchemy.http.exceptions.AlchemyHttpException;

import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;

/**
 *
 * @author SirWellington
 */
@Internal
final class MockAlchemyHttp implements AlchemyHttp
{

    private final static Logger LOG = LoggerFactory.getLogger(MockAlchemyHttp.class);

    private MockRequest currentRequest;

    /**
     * This operation allows the Mock internal steps to signal when a request is done. A Request is done when one of the {@link AlchemyRequest}
     * {@link AlchemyRequest.Step3#at(java.lang.String) } methods have been called.
     */
    @Internal
    void done()
    {

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static class MockStep1 implements AlchemyRequest.Step1
    {

        private final MockAlchemyHttp mockHttp;

        MockStep1(MockAlchemyHttp mockHttp)
        {
            this.mockHttp = mockHttp;
        }

        @Override
        public AlchemyRequest.Step3 get()
        {
            mockHttp.currentRequest.method = MockRequest.Method.GET;
            return new MockStep3(mockHttp);
        }

        @Override
        public AlchemyRequest.Step2 post()
        {
            mockHttp.currentRequest.method = MockRequest.Method.POST;

            return new MockStep2(mockHttp);
        }

        @Override
        public AlchemyRequest.Step2 put()
        {
            mockHttp.currentRequest.method = MockRequest.Method.PUT;

            return new MockStep2(mockHttp);
        }

        @Override
        public AlchemyRequest.Step2 delete()
        {
            mockHttp.currentRequest.method = MockRequest.Method.DELETE;

            return new MockStep2(mockHttp);
        }

    }

    static class MockStep2 implements AlchemyRequest.Step2
    {

        private final MockAlchemyHttp mockAlchemyHttp;

        MockStep2(MockAlchemyHttp mockAlchemyHttp)
        {
            this.mockAlchemyHttp = mockAlchemyHttp;
        }

        @Override
        public AlchemyRequest.Step3 nothing()
        {
            mockAlchemyHttp.currentRequest.body = null;

            return new MockStep3(mockAlchemyHttp);
        }

        @Override
        public AlchemyRequest.Step3 body(String jsonString) throws IllegalArgumentException
        {
            checkThat(jsonString)
                .usingMessage("jsonString cannot be empty")
                .is(nonEmptyString());

            mockAlchemyHttp.currentRequest.body = jsonString;

            return new MockStep3(mockAlchemyHttp);
        }

        @Override
        public AlchemyRequest.Step3 body(Object pojo) throws IllegalArgumentException
        {
            mockAlchemyHttp.currentRequest.body = pojo;
            
            return new MockStep3(mockAlchemyHttp);
        }

    }

    static class MockStep3 implements AlchemyRequest.Step3
    {

        private final MockAlchemyHttp mockAlchemyHttp;

        MockStep3(MockAlchemyHttp mockAlchemyHttp)
        {
            this.mockAlchemyHttp = mockAlchemyHttp;
        }

        @Override
        public AlchemyRequest.Step3 usingHeader(String key, String value) throws IllegalArgumentException
        {
            return this;
        }

        @Override
        public AlchemyRequest.Step3 usingQueryParam(String name, String value) throws IllegalArgumentException
        {
            return this;
        }

        @Override
        public AlchemyRequest.Step3 followRedirects(int maxNumberOfTimes) throws IllegalArgumentException
        {
            return this;
        }

        @Override
        public HttpResponse at(URL url) throws AlchemyHttpException
        {
            return null;
        }

        @Override
        public AlchemyRequest.Step5<HttpResponse> onSuccess(AlchemyRequest.OnSuccess<HttpResponse> onSuccessCallback)
        {
            return null;
        }

        @Override
        public <ResponseType> AlchemyRequest.Step4<ResponseType> expecting(Class<ResponseType> classOfResponseType) throws
            IllegalArgumentException
        {
            return null;
        }

    }

}

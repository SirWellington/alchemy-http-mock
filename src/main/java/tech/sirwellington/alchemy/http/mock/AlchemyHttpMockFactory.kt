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

import com.google.gson.Gson
import com.google.gson.JsonElement
import sir.wellington.alchemy.collections.maps.Maps
import tech.sirwellington.alchemy.annotations.access.Internal
import tech.sirwellington.alchemy.annotations.designs.FluidAPIDesign
import tech.sirwellington.alchemy.arguments.Arguments.checkThat
import tech.sirwellington.alchemy.arguments.assertions.nonEmptyString
import tech.sirwellington.alchemy.arguments.assertions.nonNullReference
import tech.sirwellington.alchemy.arguments.assertions.notNull
import tech.sirwellington.alchemy.arguments.assertions.validURL
import tech.sirwellington.alchemy.http.AlchemyHttp
import tech.sirwellington.alchemy.http.HttpRequest
import tech.sirwellington.alchemy.http.HttpResponse
import tech.sirwellington.alchemy.http.mock.AlchemyHttpMock.At
import tech.sirwellington.alchemy.http.mock.AlchemyHttpMock.Body
import tech.sirwellington.alchemy.http.mock.AlchemyHttpMock.Then
import tech.sirwellington.alchemy.http.mock.AlchemyHttpMock.When
import tech.sirwellington.alchemy.http.mock.MockRequest.Method.DELETE
import tech.sirwellington.alchemy.http.mock.MockRequest.Method.GET
import tech.sirwellington.alchemy.http.mock.MockRequest.Method.POST
import tech.sirwellington.alchemy.http.mock.MockRequest.Method.PUT
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Callable

@Internal
@FluidAPIDesign
internal class AlchemyHttpMockFactory : AlchemyHttpMock.When, AlchemyHttpMock.Body, AlchemyHttpMock.At, AlchemyHttpMock.Then
{

    private val gson = Gson()

    private val actions: MutableMap<MockRequest, Callable<*>> = Maps.createSynchronized()

    private var currentExpectedRequest: MockRequest? = null
    private var httpRequest: HttpRequest? = null

    override fun build(): AlchemyHttp
    {
        return MockAlchemyHttp(actions)
    }

    override fun whenPost(): Body
    {
        currentExpectedRequest = MockRequest()
        currentExpectedRequest!!.method = POST
        httpRequest = HttpRequest.Builder.newInstance().build()

        return this
    }

    override fun whenGet(): Body
    {
        currentExpectedRequest = MockRequest()
        currentExpectedRequest!!.method = GET

        return this
    }

    override fun whenPut(): Body
    {
        currentExpectedRequest = MockRequest()
        currentExpectedRequest!!.method = PUT

        return this
    }

    override fun whenDelete(): Body
    {
        currentExpectedRequest = MockRequest()
        currentExpectedRequest!!.method = DELETE

        return this
    }

    override fun noBody(): At
    {
        currentExpectedRequest!!.body = MockRequest.NO_BODY

        return this
    }

    override fun anyBody(): At
    {
        currentExpectedRequest!!.body = MockRequest.ANY_BODY

        return this
    }

    override fun body(pojo: Any): At
    {
        currentExpectedRequest!!.body = pojo

        return this
    }

    override fun body(jsonBody: JsonElement): At
    {
        checkThat(jsonBody)
                .usingMessage("jsonBody cannot be null")
                .isA(notNull())

        currentExpectedRequest!!.body = jsonBody

        return this
    }

    override fun body(jsonString: String): At
    {
        checkThat(jsonString)
                .usingMessage("jsonString cannot be empty")
                .isA(nonEmptyString())

        currentExpectedRequest!!.body = jsonString

        return this
    }

    @Throws(MalformedURLException::class)
    override fun at(url: String): Then
    {
        checkThat(url)
                .usingMessage("empty url")
                .isA(nonEmptyString())
                .isA(validURL())

        return at(URL(url))
    }

    override fun at(url: URL): Then
    {
        currentExpectedRequest!!.url = url

        return this
    }

    override fun thenDo(operation: Callable<*>): When
    {
        checkThat(operation)
                .usingMessage("operation cannot be null")
                .isA(nonNullReference())

        actions[currentExpectedRequest!!] = operation
        currentExpectedRequest = null

        return this
    }

    override fun thenThrow(ex: Exception): When
    {
        actions[currentExpectedRequest!!] = Actions.throwException<Any>(ex)
        currentExpectedRequest = null

        return this
    }

    override fun thenReturnPOJO(pojo: Any): When
    {
        actions[currentExpectedRequest!!] = Actions.returnPojo(pojo)
        currentExpectedRequest = null

        return this
    }

    override fun thenReturnPOJOAsJSON(pojo: Any): When
    {
        actions[currentExpectedRequest!!] = Actions.returnPojoAsJSON(pojo, gson)
        currentExpectedRequest = null

        return this
    }

    override fun thenReturnJson(json: JsonElement): When
    {
        checkThat(json)
                .usingMessage("json cannot be null")
                .isA(notNull())

        actions[currentExpectedRequest!!] = Actions.returnJson(json)
        currentExpectedRequest = null

        return this
    }

    override fun thenReturnResponse(response: HttpResponse): When
    {
        checkThat(response)
                .usingMessage("response cannot be null")
                .isA(notNull())

        actions[currentExpectedRequest!!] = Actions.returnResponse(response)
        currentExpectedRequest = null

        return this
    }

}

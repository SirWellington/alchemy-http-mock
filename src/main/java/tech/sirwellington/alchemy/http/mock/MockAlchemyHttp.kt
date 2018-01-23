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

import junit.framework.Assert.fail
import sir.wellington.alchemy.collections.lists.Lists
import sir.wellington.alchemy.collections.maps.Maps
import tech.sirwellington.alchemy.annotations.access.Internal
import tech.sirwellington.alchemy.annotations.arguments.Required
import tech.sirwellington.alchemy.annotations.designs.StepMachineDesign
import tech.sirwellington.alchemy.annotations.designs.StepMachineDesign.Role.MACHINE
import tech.sirwellington.alchemy.arguments.AlchemyAssertion
import tech.sirwellington.alchemy.arguments.Arguments.checkThat
import tech.sirwellington.alchemy.arguments.assertions.instanceOf
import tech.sirwellington.alchemy.arguments.assertions.nonNullReference
import tech.sirwellington.alchemy.arguments.assertions.notNull
import tech.sirwellington.alchemy.http.AlchemyHttp
import tech.sirwellington.alchemy.http.AlchemyRequestSteps
import tech.sirwellington.alchemy.http.HttpResponse
import tech.sirwellington.alchemy.http.exceptions.AlchemyHttpException
import tech.sirwellington.alchemy.http.mock.MockRequest.Companion.ANY_BODY
import tech.sirwellington.alchemy.http.mock.MockRequest.Companion.NO_BODY
import java.lang.String.format
import java.util.concurrent.Callable

/**
 *
 * @author SirWellington
 */
@Internal
@StepMachineDesign(role = MACHINE)
internal open class MockAlchemyHttp(expectedActions: Map<MockRequest, Callable<*>>) : AlchemyHttp
{

    private val expectedActions: MutableMap<MockRequest, Callable<*>> = Maps.createSynchronized()

    private val requestsMade = Lists.create<MockRequest>()

    override val defaultHeaders: Map<String, String>
        @Required
        get() = emptyMap()

    init
    {
        checkThat(expectedActions).isA(nonNullReference())
        this.expectedActions.putAll(expectedActions)
    }

    @Required
    override fun usingDefaultHeader(key: String, value: String): AlchemyHttp
    {
        return this
    }

    override fun go(): AlchemyRequestSteps.Step1
    {
        return MockSteps.MockStep1(this)
    }

    @Internal
    @Throws(AlchemyHttpException::class)
    open fun getResponseFor(request: MockRequest): HttpResponse
    {
        checkThat(request).isA(expectedRequest())

        requestsMade.add(request)

        val action = findMatchingActionFor(request)!!

        val response: Any
        try
        {
            response = action.call()
        }
        catch (ex: AlchemyHttpException)
        {
            throw ex
        }
        catch (ex: Exception)
        {
            throw AlchemyHttpException(ex)
        }

        checkThat(response)
                .usingMessage(format("Response Type Wanted: %s but actual: null", HttpResponse::class.java))
                .isA(notNull())
                .usingMessage(format("Response Type Wanted: %s but actual: %s", HttpResponse::class.java, response.javaClass))
                .isA(instanceOf(HttpResponse::class.java))

        return response as HttpResponse
    }

    @Internal
    @Throws(AlchemyHttpException::class)
    open fun <T> getResponseFor(request: MockRequest, expectedClass: Class<T>): T
    {
        checkThat(request, expectedClass)
                .are(notNull())

        requestsMade.add(request)

        checkThat(request)
                .usingMessage("Unexpected Request: " + request)
                .isA(expectedRequest())

        val operation = findMatchingActionFor(request)

        val responseObject: Any
        try
        {
            responseObject = operation!!.call()
        }
        catch (ex: AlchemyHttpException)
        {
            throw ex
        }
        catch (ex: Exception)
        {
            throw AlchemyHttpException(ex)
        }


        checkThat(responseObject)
                .usingMessage(format("Response Type Wanted: %s but actual: %s", responseObject.javaClass, expectedClass))
                .isA(instanceOf(expectedClass))

        return responseObject as T

    }

    @Internal
    open fun verifyAllRequestsMade()
    {
        expected@ for (expectedRequest in expectedActions.keys)
        {
            made@ for (requestMade in requestsMade)
            {
                if (requestsMatch(expectedRequest, requestMade))
                {
                    continue@expected
                }
            }

            //Reaching here means no match was found
            fail("Request never made: " + expectedRequest)
        }
    }

    private fun expectedRequest(): AlchemyAssertion<MockRequest>
    {
        return AlchemyAssertion { request ->

            checkThat(request).isA(nonNullReference())

            val action = findMatchingActionFor(request)
            checkThat(action).isA(nonNullReference())
        }
    }

    private fun findMatchingActionFor(request: MockRequest): Callable<*>?
    {
        val foundInMap = expectedActions[request]

        if (foundInMap != null)
        {
            return foundInMap
        }

        for (element in expectedActions.keys)
        {
            if (requestsMatch(element, request))
            {
                return expectedActions[element]
            }
        }

        return null
    }

    private fun requestsMatch(expected: MockRequest, actual: MockRequest): Boolean
    {
        val matchEverythingBesidesTheBody = matchEverythingBesidesTheBody(expected, actual)

        if (!matchEverythingBesidesTheBody)
        {
            return false
        }

        if (expected.body === ANY_BODY)
        {
            return true
        }

        return if (expected.body === NO_BODY)
        {
            actual.body == null || actual.body === NO_BODY
        }
        else
        {
            expected.body === actual.body
        }

        /*
         * The bodies will be both null, or both set to NO_BODY. == is intentionally used to compare instances.
         */
    }

    private fun matchEverythingBesidesTheBody(first: MockRequest, second: MockRequest): Boolean
    {
        return first.method == second.method &&
               first.url == second.url &&
               first.queryParams == second.queryParams
    }


}

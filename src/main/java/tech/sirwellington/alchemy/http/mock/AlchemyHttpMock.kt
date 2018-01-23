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

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty
import tech.sirwellington.alchemy.annotations.arguments.Optional
import tech.sirwellington.alchemy.annotations.arguments.Required
import tech.sirwellington.alchemy.annotations.designs.FluidAPIDesign
import tech.sirwellington.alchemy.annotations.designs.patterns.FactoryMethodPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.FactoryMethodPattern.Role
import tech.sirwellington.alchemy.arguments.Arguments.checkThat
import tech.sirwellington.alchemy.arguments.assertions.instanceOf
import tech.sirwellington.alchemy.http.AlchemyHttp
import tech.sirwellington.alchemy.http.HttpResponse
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Callable

/**
 *
 * @author SirWellington
 */
@FluidAPIDesign
@NonInstantiable
open class AlchemyHttpMock
{

    interface When
    {

        fun build(): AlchemyHttp

        fun whenPost(): Body

        fun whenGet(): Body

        fun whenPut(): Body

        fun whenDelete(): Body

    }

    interface Body
    {

        fun noBody(): At

        fun anyBody(): At

        fun body(@Required pojo: Any): At

        fun body(@Required jsonBody: JsonElement): At

        fun body(@NonEmpty jsonString: String): At

    }

    interface At
    {

        @Throws(MalformedURLException::class)
        fun at(@NonEmpty url: String): Then

        fun at(@Required url: URL): Then
    }

    interface Then
    {

        /**
         * Calls the specified function when the current request is executed, and returns the
         * output of this function.
         *
         * @param operation
         * @return
         */
        fun thenDo(@Required operation: Callable<*>): When

        /**
         * Throws the specified Exception for the current request.
         * @param ex
         * @return
         */
        fun thenThrow(@Required ex: Exception): When

        /**
         * Returns the specified POJO as is for the current request.
         *
         * @param pojo
         * @return
         */
        fun thenReturnPOJO(@Optional pojo: Any): When

        /**
         * Converts and returns the specified POJO as a [JSON Object][JsonObject].
         *
         * @param pojo The POJO to return, can be null.
         * @return
         */
        fun thenReturnPOJOAsJSON(@Optional pojo: Any): When

        /**
         * Returns the specified JSON as is for the current request.
         *
         * @param json
         * @return
         */
        fun thenReturnJson(json: JsonElement): When

        /**
         * Returns the specified [HttpResponse] for the current request.
         *
         * @param response
         * @return
         */
        fun thenReturnResponse(response: HttpResponse): When

    }

    companion object
    {

        @JvmStatic
        @FactoryMethodPattern(role = Role.FACTORY_METHOD)
        fun begin(): When
        {
            return AlchemyHttpMockFactory()
        }

        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun verifyAllRequestsMade(@Required mockHttp: AlchemyHttp)
        {
            checkThat(mockHttp)
                    .usingMessage("Can only verify with AlchemyHttp generated from AlchemyHttpMock")
                    .`is`(instanceOf(MockAlchemyHttp::class.java))

            val mock = mockHttp as MockAlchemyHttp
            mock.verifyAllRequestsMade()
        }
    }

}

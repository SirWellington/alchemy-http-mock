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

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.alphabeticStrings
import tech.sirwellington.alchemy.http.HttpResponse
import tech.sirwellington.alchemy.test.hamcrest.isNull
import tech.sirwellington.alchemy.test.hamcrest.notNull
import tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner
import tech.sirwellington.alchemy.test.junit.runners.GenerateDate
import tech.sirwellington.alchemy.test.junit.runners.Repeat
import java.util.Date

/**
 *
 * @author SirWellington
 */
@Repeat(20)
@RunWith(AlchemyTestRunner::class)
class ActionsTest
{

    @Mock
    private lateinit var  httpResponse: HttpResponse

    @GenerateDate
    private lateinit var date: Date

    private lateinit var  json: JsonObject

    private lateinit var  gson: Gson

    @Before
    fun setUp()
    {
        json = JsonObject()
        gson = Gson()
    }

    @Test
    @Throws(Exception::class)
    fun testReturnPojo()
    {
        val action = Actions.returnPojo(date)
        assertThat(action, notNull)

        val result = action.call()
        assertThat(result, notNull)
        assertThat(result, equalTo(date))
    }

    @Test
    @Throws(Exception::class)
    fun testReturnPojoAsJSON()
    {
        val action = Actions.returnPojoAsJSON(date, gson)
        assertThat(action, notNull)

        val result = action.call()
        assertThat(result, notNull)
        assertThat(result, equalTo(gson.toJsonTree(date)))
    }

    @Test
    @Throws(Exception::class)
    fun testReturnNullPojo()
    {
        val action = Actions.returnPojo(null)
        assertThat(action, notNull)

        val result = action.call()
        assertThat(result, isNull)
    }

    @Test
    @Throws(Exception::class)
    fun testReturnNullPojoAsJSON()
    {
        val action = Actions.returnPojoAsJSON(null, gson)
        assertThat(action, notNull)

        val result = action.call()
        assertThat(result, notNull)
        assertThat(result, equalTo<JsonElement>(JsonNull.INSTANCE))
    }

    @Test
    @Throws(Exception::class)
    fun testReturnNull()
    {
        val action = Actions.returnNull<Any>()
        assertThat(action, notNull)

        val result = action.call()
        assertThat(result, isNull)
    }

    @Test
    @Throws(Exception::class)
    fun testReturnJson()
    {
        val action = Actions.returnJson(json)
        assertThat(action, notNull)

        val result = action.call()
        assertThat(result, equalTo<JsonElement>(json))
    }

    @Test
    @Throws(Exception::class)
    fun testReturnResponse()
    {
        val action = Actions.returnResponse(httpResponse)
        assertThat(action, notNull)

        val result = action.call()
        assertThat(result, equalTo(httpResponse))

    }

    @Test
    fun testThrowException()
    {
        val message = one(alphabeticStrings())
        val ex = RuntimeException(message)

        val action = Actions.throwException<Any>(ex)
        assertThat(action, notNull)

        assertThrows { action.call() }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessage(message)

    }

}

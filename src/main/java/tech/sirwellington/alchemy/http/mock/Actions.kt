/*
 * Copyright © 2018. Sir Wellington.
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
import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.Internal
import tech.sirwellington.alchemy.http.HttpResponse
import java.util.concurrent.Callable

/**
 *
 * @author SirWellington
 */
@Internal
internal object Actions
{

    private val LOG = LoggerFactory.getLogger(Actions::class.java)

    @JvmStatic
    fun <T: Any?> returnNull(): Callable<T>
    {
        return Callable {  null as T }
    }

    @JvmStatic
    fun <T: Any?> returnPojo(pojo: T): Callable<T>
    {
        return Callable { pojo }
    }

    @JvmStatic
    fun <T: Any?> returnPojoAsJSON(pojo: T, gson: Gson): Callable<JsonElement>
    {
        return Callable { gson.toJsonTree(pojo) }
    }

    @JvmStatic
    fun returnJson(json: JsonElement): Callable<JsonElement>
    {
        return Callable { json }
    }

    @JvmStatic
    fun returnResponse(response: HttpResponse): Callable<HttpResponse>
    {
        return Callable { response }
    }

    fun <T: Any?> throwException(ex: Exception): Callable<T>
    {
        return Callable { throw ex }
    }

}

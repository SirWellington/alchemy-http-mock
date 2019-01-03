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

import tech.sirwellington.alchemy.annotations.objects.Pojo
import tech.sirwellington.alchemy.http.RequestMethod
import java.net.URL

/**
 *
 * @author SirWellington
 */
@Pojo
internal data class MockRequest(internal var url: URL? = null,
                                internal var method: RequestMethod? = null,
                                internal var body: Any? = null,
                                internal var queryParams: Map<String, String>? = null,
                                internal var requestHeaders: Map<String, String>? = null)
{

    companion object
    {

        @JvmField
        val NO_BODY = Any()

        @JvmField
        val ANY_BODY = Any()

        @JvmField
        val ANY_URL = URL("file://ALCHEMY/HTTP/MOCK/ANY")
    }

}

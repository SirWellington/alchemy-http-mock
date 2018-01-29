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

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tech.sirwellington.alchemy.test.hamcrest.notNull
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo
import tech.sirwellington.alchemy.test.junit.runners.GenerateString
import tech.sirwellington.alchemy.test.junit.runners.GenerateURL
import java.net.URL

@RunWith(AlchemyTestRunner::class)
class AlchemyHttpMockFactoryTest
{

    @GenerateURL
    private lateinit var url: URL

    @GenerateString
    private lateinit var bodyString: String

    @GeneratePojo
    private lateinit var bodyPojo: SamplePojo

    @GeneratePojo
    private lateinit var responsePojo: SamplePojo

    private lateinit var instance: AlchemyHttpMockFactory

    @Before
    fun setUp()
    {

        setupData()
        setupMocks()

        instance = AlchemyHttpMockFactory()
    }

    private fun setupData()
    {

    }

    private fun setupMocks()
    {

    }

    @Test
    fun testWhenGetAtUrl()
    {
        val http = instance.whenGet()
                .noBody()
                .at(url)
                .thenReturnPOJO(responsePojo)
                .build()

        assertThat(http, notNull)

        Assert.assertTrue(http is MockAlchemyHttp)

        val mockHttp = http as MockAlchemyHttp

        val resultPojo: SamplePojo = mockHttp.go()
                                             .get()
                                             .expecting(SamplePojo::class.java)
                                             .at(url)

        assertThat(resultPojo, equalTo(responsePojo))
    }

    data class SamplePojo(val name: String,
                          val age: Int,
                          val description: String)
}


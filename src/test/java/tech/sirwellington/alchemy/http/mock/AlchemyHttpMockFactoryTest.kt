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
import org.mockito.Mock
import tech.sirwellington.alchemy.generator.NetworkGenerators
import tech.sirwellington.alchemy.http.HttpResponse
import tech.sirwellington.alchemy.http.expecting
import tech.sirwellington.alchemy.test.hamcrest.notNull
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner
import tech.sirwellington.alchemy.test.junit.runners.GeneratePojo
import tech.sirwellington.alchemy.test.junit.runners.GenerateString
import tech.sirwellington.alchemy.test.junit.runners.GenerateURL
import tech.sirwellington.alchemy.test.junit.runners.Repeat
import java.net.URL

@RunWith(AlchemyTestRunner::class)
@Repeat(25)
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

    @Mock
    private lateinit var httpResponse: HttpResponse

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
    fun testGetAtUrl()
    {
        val http = instance.whenGet()
                .noBody()
                .at(url)
                .thenReturnResponse(httpResponse)
                .build()

        val response = http.go()
                .get()
                .at(url)

        assertThat(response, equalTo(httpResponse))
    }

    @Test
    fun testGetAtUrlExpectingPojo()
    {
        val http = instance.whenGet()
                .noBody()
                .at(url)
                .thenReturnPOJO(responsePojo)
                .build()

        assertThat(http, notNull)

        Assert.assertTrue(http is MockAlchemyHttp)

        val mockHttp = http as MockAlchemyHttp

        val resultPojo = mockHttp.go()
                .get()
                .expecting<SamplePojo>()
                .at(url)

        assertThat(resultPojo, equalTo(responsePojo))
    }

    @Test
    fun testGetAtAnyURL()
    {
        val randomUrl = NetworkGenerators.httpUrls().get()

        val http = instance.whenGet()
                .noBody()
                .atAnyURL()
                .thenReturnPOJO(responsePojo)
                .build()

        val response = http.go().get().expecting<SamplePojo>().at(randomUrl)

        assertThat(response, equalTo(responsePojo))
    }

    @Test
    fun testPostBody()
    {
        val http = instance.whenPost()
                .body(bodyPojo)
                .at(url)
                .thenReturnResponse(httpResponse)
                .build()

        val response = http.go().post().body(bodyPojo).at(url)

        assertThat(response, equalTo(httpResponse))
    }

    @Test
    fun testPostBodyExpectingPojo()
    {
        val http = instance.whenPost()
                .body(bodyPojo)
                .at(url)
                .thenReturnPOJO(responsePojo)
                .build()

        val response = http.go()
                .post()
                .body(bodyPojo)
                .expecting<SamplePojo>()
                .at(url)

        assertThat(response, equalTo(responsePojo))
    }

    @Test
    fun testPostBodyAtAnyURL()
    {
        val http = instance.whenPost()
                .anyBody()
                .atAnyURL()
                .thenReturnResponse(httpResponse)
                .build()

        val response = http.go()
                .post()
                .body(bodyString)
                .at(url)

        assertThat(response, equalTo(httpResponse))
    }

    @Test
    fun testPutBody()
    {
        val http = instance.whenPut()
                .body(bodyPojo)
                .at(url)
                .thenReturnPOJO(responsePojo)
                .build()

        val response = http.go()
                .put()
                .body(bodyPojo)
                .expecting<SamplePojo>()
                .at(url)

        assertThat(response, equalTo(responsePojo))

    }


    data class SamplePojo(val name: String,
                          val age: Int,
                          val description: String)
}


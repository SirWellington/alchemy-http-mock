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

import com.natpryce.hamkrest.assertion.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import tech.sirwellington.alchemy.http.AlchemyHttp
import tech.sirwellington.alchemy.test.hamcrest.notNull
import tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner
import tech.sirwellington.alchemy.test.junit.runners.Repeat

/**
 *
 * @author SirWellington
 */
@Repeat(10)
@RunWith(AlchemyTestRunner::class)
class AlchemyHttpMockTest
{

    @Mock
    private lateinit var mockitoMock: AlchemyHttp

    private lateinit var  alchemyMock: AlchemyHttp

    @Before
    fun setUp()
    {
        alchemyMock = AlchemyHttpMock.begin().build()
    }

    @Test
    fun testBegin()
    {
        val result = AlchemyHttpMock.begin()
        assertThat(result, notNull)
    }

    @Test
    fun testVerifyAllRequestsMade()
    {
        assertThrows { AlchemyHttpMock.verifyAllRequestsMade(mockitoMock) }
                .isInstanceOf(IllegalArgumentException::class.java)

        AlchemyHttpMock.verifyAllRequestsMade(alchemyMock)
    }

}
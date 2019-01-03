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

import com.natpryce.hamkrest.assertion.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tech.sirwellington.alchemy.test.hamcrest.notNull
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner

@RunWith(AlchemyTestRunner::class)
class MockRequestTest
{

    @Before
    fun setUp()
    {

        setupData()
        setupMocks()

    }

    private fun setupData()
    {

    }

    private fun setupMocks()
    {

    }

    @Test
    fun testAnyBody()
    {
        val anyBody = MockRequest.ANY_BODY
        assertThat(anyBody, notNull)
    }

    @Test
    fun testNoBody()
    {
        val noBody = MockRequest.NO_BODY
        assertThat(noBody, notNull)
    }

    @Test
    fun testAnyURL()
    {
        val anyUrl = MockRequest.ANY_URL
        assertThat(anyUrl, notNull)
    }
}
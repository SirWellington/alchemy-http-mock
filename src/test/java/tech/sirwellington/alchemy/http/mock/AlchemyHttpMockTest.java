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

package tech.sirwellington.alchemy.http.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.sirwellington.alchemy.http.AlchemyHttp;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.*;

/**
 *
 * @author SirWellington
 */
@Repeat(10)
@RunWith(AlchemyTestRunner.class)
public class AlchemyHttpMockTest
{
   @Mock
   private AlchemyHttp mockitoMock;

   private AlchemyHttp alchemyMock;

    @Before
    public void setUp()
    {
        alchemyMock = AlchemyHttpMock.Companion.begin().build();
    }

    @Test
    public void testBegin()
    {
        AlchemyHttpMock.When result = AlchemyHttpMock.Companion.begin();
        assertThat(result, notNullValue());
    }

    @Test
    public void testVerifyAllRequestsMade()
    {
        assertThrows(() -> AlchemyHttpMock.Companion.verifyAllRequestsMade(mockitoMock))
            .isInstanceOf(IllegalArgumentException.class);

        AlchemyHttpMock.Companion.verifyAllRequestsMade(alchemyMock);
    }

}
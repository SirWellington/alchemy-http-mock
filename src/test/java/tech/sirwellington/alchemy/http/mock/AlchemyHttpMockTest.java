/*
 * Copyright 2015 Aroma Tech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;

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
        alchemyMock = AlchemyHttpMock.begin().build();
    }

    @Test
    public void testBegin()
    {
        AlchemyHttpMock.When result = AlchemyHttpMock.begin();
        assertThat(result, notNullValue());
    }

    @Test
    public void testVerifyAllRequestsMade()
    {
        assertThrows(() -> AlchemyHttpMock.verifyAllRequestsMade(mockitoMock))
            .isInstanceOf(IllegalArgumentException.class);
        
        AlchemyHttpMock.verifyAllRequestsMade(alchemyMock);
    }

}
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.concurrent.Callable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.sirwellington.alchemy.http.HttpResponse;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;
import tech.sirwellington.alchemy.test.junit.runners.GenerateDate;
import tech.sirwellington.alchemy.test.junit.runners.Repeat;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphabeticString;
import static tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows;

/**
 *
 * @author SirWellington
 */
@Repeat(10)
@RunWith(AlchemyTestRunner.class)
public class ActionsTest
{

    @Mock
    private HttpResponse httpResponse;

    @GenerateDate
    private Date date;

    private JsonObject json;

    private Gson gson;

    @Before
    public void setUp()
    {
        json = new JsonObject();
        gson = new Gson();
    }

    @Test
    public void testReturnPojo() throws Exception
    {
        Callable<Date> action = Actions.returnPojo(date);
        assertThat(action, notNullValue());

        Date result = action.call();
        assertThat(result, notNullValue());
        assertThat(result, is(date));
    }

    @Test
    public void testReturnPojoAsJSON() throws Exception
    {
        Callable<JsonElement> action = Actions.returnPojoAsJSON(date, gson);
        assertThat(action, notNullValue());

        JsonElement result = action.call();
        assertThat(result, notNullValue());
        assertThat(result, is(gson.toJsonTree(date)));
    }

    @Test
    public void testReturnNullPojo() throws Exception
    {
        Callable<Object> action = Actions.returnPojo(null);
        assertThat(action, notNullValue());

        Object result = action.call();
        assertThat(result, nullValue());
    }

    @Test
    public void testReturnNullPojoAsJSON() throws Exception
    {
        Callable<JsonElement> action = Actions.returnPojoAsJSON(null, gson);
        assertThat(action, notNullValue());

        JsonElement result = action.call();
        assertThat(result, notNullValue());
        assertThat(result, is(JsonNull.INSTANCE));
    }

    @Test
    public void testReturnNull() throws Exception
    {
        Callable<Object> action = Actions.returnNull();
        assertThat(action, notNullValue());

        Object result = action.call();
        assertThat(result, nullValue());
    }

    @Test
    public void testReturnJson() throws Exception
    {
        Callable<JsonElement> action = Actions.returnJson(json);
        assertThat(action, notNullValue());

        JsonElement result = action.call();
        assertThat(result, is(json));
    }

    @Test
    public void testReturnResponse() throws Exception
    {
        Callable<HttpResponse> action = Actions.returnResponse(httpResponse);
        assertThat(action, notNullValue());

        HttpResponse result = action.call();
        assertThat(result, is(httpResponse));

        assertThrows(() -> Actions.returnResponse(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testThrowException()
    {
        String message = one(alphabeticString());
        Exception ex = new RuntimeException(message);

        Callable<Object> action = Actions.throwException(ex);
        assertThat(action, notNullValue());

        assertThrows(() -> action.call())
            .isInstanceOf(RuntimeException.class)
            .hasMessage(message);

        //Edge case
        assertThrows(() -> Actions.throwException(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

}

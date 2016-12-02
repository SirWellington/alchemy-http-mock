/*
 * Copyright 2015 SirWellington Tech.
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.http.HttpResponse;

import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;

/**
 *
 * @author SirWellington
 */
@Internal
final class Actions
{

    private final static Logger LOG = LoggerFactory.getLogger(Actions.class);

    static <T> Callable<T> returnNull()
    {
        return () -> null;
    }
    
    static <T> Callable<T> returnPojo(T pojo)
    {
        return () -> pojo;
    }
    
    static <T> Callable<JsonElement> returnPojoAsJSON(T pojo, Gson gson)
    {
        return () -> gson.toJsonTree(pojo);
    }
    
    static Callable<JsonElement> returnJson(JsonElement json) 
    {
        checkThat(json)
            .is(notNull());
        
        return () -> json;
    }
    
    static Callable<HttpResponse> returnResponse(HttpResponse response)
    {
        checkThat(response)
            .usingMessage("Response cannot be null")
            .is(notNull());
        
        return () -> response;
    }

    static <T> Callable<T> throwException(Exception ex)
    {
        checkThat(ex)
            .usingMessage("Exception cannot be null")
            .is(notNull());

        return () ->
        {
            throw ex;
        };
    }

}

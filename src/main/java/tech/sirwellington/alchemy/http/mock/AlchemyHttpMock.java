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

import com.google.gson.JsonElement;
import tech.sirwellington.alchemy.http.AlchemyHttp;
import tech.sirwellington.alchemy.http.HttpResponse;
import tech.sirwellington.alchemy.test.junit.ExceptionOperation;

/**
 *
 * @author SirWellington
 */
public interface AlchemyHttpMock
{ 
    
    static When begin()
    {
        
        return null;
    }

    interface When
    {

        AlchemyHttp build();

        Then whenPost();

        Then whenGet();

        Then whenPut();

        Then whenDelete();

    }
    
    interface Body
    {
        At body(Object pojo);
        
        At body(JsonElement jsonBody);
        
        At body(String jsonString);
        
        At noBody();
    }
    
    interface At
    {
        Then at(String url);
    }

    interface Then
    {

        When thenDo(ExceptionOperation operation);

        When thenThrow(Throwable ex);

        When thenReturn(Object pojo);

        When thenReturnJson(JsonElement json);

        When thenReturnResponse(HttpResponse response);

    }

}

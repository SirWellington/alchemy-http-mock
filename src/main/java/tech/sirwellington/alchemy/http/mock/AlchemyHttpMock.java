
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;
import tech.sirwellington.alchemy.annotations.arguments.Optional;
import tech.sirwellington.alchemy.annotations.arguments.Required;
import tech.sirwellington.alchemy.annotations.designs.FluidAPIDesign;
import tech.sirwellington.alchemy.http.AlchemyHttp;
import tech.sirwellington.alchemy.http.HttpResponse;

import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.instanceOf;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.nonEmptyString;

/**
 *
 * @author SirWellington
 */
@FluidAPIDesign
public interface AlchemyHttpMock
{

    static When begin()
    {
        return new AlchemyHttpMockFactory();
    }

    static void verifyAllRequestsMade(@Required AlchemyHttp mockHttp) throws IllegalArgumentException
    {
        checkThat(mockHttp)
            .usingMessage("Can only verify with AlchemyHttp generated from AlchemyHttpMock")
            .is(instanceOf(MockAlchemyHttp.class));

        MockAlchemyHttp mock = (MockAlchemyHttp) mockHttp;
        mock.verifyAllRequestsMade();
    }

    interface When
    {

        AlchemyHttp build();

        Body whenPost();

        Body whenGet();

        Body whenPut();

        Body whenDelete();

    }

    interface Body
    {

        At noBody();

        At anyBody();

        At body(@Required Object pojo);

        At body(@Required JsonElement jsonBody);

        At body(@NonEmpty String jsonString);

    }

    interface At
    {

        default Then at(@NonEmpty String url) throws MalformedURLException
        {
            checkThat(url).usingMessage("empty url").is(nonEmptyString());

            return at(new URL(url));
        }

        Then at(@Required URL url);
    }

    interface Then
    {

        /**
         * Calls the specified function when the current request is executed, and returns the
         * output of this function.
         *
         * @param operation
         * @return
         */
        When thenDo(@Required Callable<?> operation);

        /**
         * Throws the specified Exception for the current request.
         * @param ex
         * @return
         */
        When thenThrow(@Required Exception ex);

        /**
         * Returns the specified POJO as is for the current request.
         *
         * @param pojo
         * @return
         */
        When thenReturnPOJO(@Optional Object pojo);

        /**
         * Converts and returns the specified POJO as a {@linkplain JsonObject JSON Object}.
         *
         * @param pojo The POJO to return, can be null.
         * @return
         */
        When thenReturnPOJOAsJSON(@Optional Object pojo);

        /**
         * Returns the specified JSON as is for the current request.
         *
         * @param json
         * @return
         */
        When thenReturnJson(JsonElement json);

        /**
         * Returns the specified {@link HttpResponse} for the current request.
         *
         * @param response
         * @return
         */
        When thenReturnResponse(HttpResponse response);

    }

}

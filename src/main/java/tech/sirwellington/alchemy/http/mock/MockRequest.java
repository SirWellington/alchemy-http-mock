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

import java.net.URL;
import java.util.Map;
import java.util.Objects;
import tech.sirwellington.alchemy.annotations.objects.Pojo;

/**
 *
 * @author SirWellington
 */
@Pojo
class MockRequest
{
    
    enum Method
    {
        GET,
        POST,
        PUT,
        DELETE
    }

    static final Object NO_BODY = new Object();
    
    static final Object ANY_BODY = new Object();
   
    URL url;
    Method method;
    Object body;
    Map<String, String> queryParams;

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.url);
        hash = 59 * hash + Objects.hashCode(this.method);
        hash = 59 * hash + Objects.hashCode(this.body);
        hash = 59 * hash + Objects.hashCode(this.queryParams);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final MockRequest other = (MockRequest) obj;
        if (!Objects.equals(this.url, other.url))
        {
            return false;
        }
        if (this.method != other.method)
        {
            return false;
        }
        if (!Objects.equals(this.body, other.body))
        {
            return false;
        }
        if (!Objects.equals(this.queryParams, other.queryParams))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "MockRequest{" + "url=" + url + ", method=" + method + ", body=" + body + ", queryParams=" + queryParams + '}';
    }

}

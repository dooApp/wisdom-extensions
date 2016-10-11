/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2016 Wisdom Framework
 * %%
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
 * #L%
 */
package org.wisdom.monitor.extensions.requests.model;

import java.io.Serializable;

/**
 * Created by KEVIN on 06/10/2016.
 */
public class RouteRequest implements Serializable, Comparable<RouteRequest> {
    private String route;
    private String method;

    public RouteRequest(String route, String method) {
        this.route = route;
        this.method = method;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteRequest that = (RouteRequest) o;

        if (!getRoute().equals(that.getRoute())) return false;
        return getMethod().equals(that.getMethod());

    }

    @Override
    public int hashCode() {
        int result = getRoute().hashCode();
        result = 31 * result + getMethod().hashCode();
        return result;
    }

    @Override
    public int compareTo(RouteRequest o) {
        return (getRoute() + getMethod()).compareTo(o.getRoute() + o.getMethod());
    }
}

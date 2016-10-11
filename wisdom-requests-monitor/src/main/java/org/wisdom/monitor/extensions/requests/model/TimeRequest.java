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

public class TimeRequest extends RouteRequest implements Serializable {
    private String url;
    private long executionTime;
    private long duration;

    public TimeRequest(long executionTime, String url, String route, String method, long duration) {
        super(route, method);
        this.executionTime = executionTime;
        this.url = url;
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TimeRequest that = (TimeRequest) o;

        if (getExecutionTime() != that.getExecutionTime()) return false;
        if (getDuration() != that.getDuration()) return false;
        return getUrl().equals(that.getUrl());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getUrl().hashCode();
        result = 31 * result + (int) (getExecutionTime() ^ (getExecutionTime() >>> 32));
        result = 31 * result + (int) (getDuration() ^ (getDuration() >>> 32));
        return result;
    }

    @Override
    public int compareTo(RouteRequest o) {
        return Long.compare(getExecutionTime(), ((TimeRequest) o).getExecutionTime());
    }
}
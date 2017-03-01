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
public class TimeRequestStats implements Serializable, Comparable<TimeRequestStats> {
    private final long minDuration;
    private final long averageDuration;
    private final long maxDuration;
    private final int numberOfRequests;

    public TimeRequestStats(long minDuration, long averageDuration, long maxDuration, int numberOfRequests) {
        this.minDuration = minDuration;
        this.averageDuration = averageDuration;
        this.maxDuration = maxDuration;
        this.numberOfRequests = numberOfRequests;
    }


    public long getMinDuration() {
        return minDuration;
    }

    public long getAverageDuration() {
        return averageDuration;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    @Override
    public int compareTo(TimeRequestStats o) {
        return Long.compare(getAverageDuration(), o.getAverageDuration());
    }
}

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
package org.wisdom.monitor.extensions.requests;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.wisdom.api.http.AsyncResult;
import org.wisdom.api.http.Result;
import org.wisdom.api.interception.Filter;
import org.wisdom.api.interception.RequestContext;
import org.wisdom.api.router.Route;
import org.wisdom.monitor.extensions.requests.model.TimeRequest;
import org.wisdom.monitor.extensions.requests.service.RequestStorageService;

import java.util.concurrent.Callable;
import java.util.regex.Pattern;

/**
 * A filter measuring the time spent to compute the time spent to handle an action
 */
@Component
@Provides
@Instantiate
public class TimeFilter implements Filter {

    @Requires
    private RequestStorageService requestStorageService;

    private static final Pattern DEFAULT_REGEX = Pattern.compile(".*");

    @Override
    public Result call(Route route, RequestContext context) throws Exception {
        final long begin = System.currentTimeMillis();
        Result result = context.proceed();
        if (result instanceof AsyncResult) {
            Callable<Result> originalCallable = ((AsyncResult) result).callable();
            Callable<Result> wrapped = () -> {
                Result originalResult = originalCallable.call();
                final long end = System.currentTimeMillis();
                TimeRequest timeRequest = new TimeRequest(begin, context.request().uri(), route.getUrl(), context.request().method(), end - begin);
                requestStorageService.getRequests().add(timeRequest);
                requestStorageService.commit();
                return originalResult;
            };
            return new AsyncResult(wrapped);
        } else {
            final long end = System.currentTimeMillis();
            TimeRequest timeRequest = new TimeRequest(begin, context.request().uri(), route.getUrl(), context.request().method(), end - begin);
            requestStorageService.getRequests().add(timeRequest);
            requestStorageService.commit();
        }
        return result;
    }

    /**
     * Gets the Regex Pattern used to determine whether the route
     * is handled by the filter or not.
     * Notice that the router are caching these patterns and so
     * cannot changed.
     */
    @Override
    public Pattern uri() {
        try {
            return Pattern.compile(requestStorageService.getMetadatas().get("filterURI"));
        } catch (Exception e) {
            return DEFAULT_REGEX;
        }
    }

    /**
     * Gets the filter priority, determining the position of the
     * filter in the filter chain. Filter with a high
     * priority are called first. Notice that the router are caching
     * these priorities and so cannot changed.
     * <p>
     * It is heavily recommended to allow configuring the priority
     * from the Application Configuration.
     *
     * @return the priority
     */
    @Override
    public int priority() {
        return -Integer.MAX_VALUE;
    }

}

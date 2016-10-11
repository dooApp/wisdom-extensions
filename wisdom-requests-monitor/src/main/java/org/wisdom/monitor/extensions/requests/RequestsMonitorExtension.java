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

import org.apache.felix.ipojo.annotations.Requires;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.*;
import org.wisdom.api.annotations.scheduler.Every;
import org.wisdom.api.concurrent.ManagedScheduledExecutorService;
import org.wisdom.api.concurrent.ManagedScheduledFutureTask;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.scheduler.Scheduled;
import org.wisdom.api.security.Authenticated;
import org.wisdom.api.templates.Template;
import org.wisdom.monitor.extensions.requests.model.Metadata;
import org.wisdom.monitor.extensions.requests.model.RouteRequest;
import org.wisdom.monitor.extensions.requests.model.TimeRequest;
import org.wisdom.monitor.extensions.requests.model.TimeRequestStats;
import org.wisdom.monitor.extensions.requests.service.RequestStorageService;
import org.wisdom.monitor.service.MonitorExtension;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by KEVIN on 22/01/2016.
 */
@Controller
@Path("/monitor/requests")
@Authenticated("Monitor-Authenticator")
public class RequestsMonitorExtension extends DefaultController implements MonitorExtension, Scheduled {

    @Requires
    private RequestStorageService requestStorageService;

    @Requires(filter = "(name=" + ManagedScheduledExecutorService.SYSTEM + ")", proxy = false)
    ManagedScheduledExecutorService service;

    @View("requests")
    Template requestsTemplate;

    private ManagedScheduledFutureTask<?> computeStatsTask;
    private ManagedScheduledFutureTask<?> cleanTask;

    @Route(method = HttpMethod.GET, uri = "")
    public Result index() throws Exception {
        Map<RouteRequest, TimeRequestStats> stats = requestStorageService.getStats();
        Set<Map.Entry<RouteRequest, TimeRequestStats>> entries = new TreeSet<>((Comparator<Map.Entry<RouteRequest, TimeRequestStats>>) (o1, o2) -> -1 * Long.compare(o1.getValue().getAverageDuration(), o2.getValue().getAverageDuration()));
        entries.addAll(stats.entrySet());

        Set<TimeRequest> requests = new TreeSet<>((Comparator<TimeRequest>) (o1, o2) -> -1 * Long.compare(o1.getExecutionTime(), o2.getExecutionTime()));
        requests.addAll(requestStorageService.getRequests());
        return ok(render(requestsTemplate, "requests", requests, "entries", entries, "filterURI", requestStorageService.getMetadatas().get("filterURI")));
    }

    @Route(method = HttpMethod.POST, uri = "/configure")
    public Result configure(@Body Metadata metadata) throws Exception {
        requestStorageService.getMetadatas().put("filterURI", metadata.getFilterURI());
        requestStorageService.commit();
        return index();
    }


    @Every(period = 1, unit = TimeUnit.DAYS)
    private void clearRequests() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, -2);
        TimeRequest twoDaysAgoRequests = new TimeRequest(calendar.getTimeInMillis(), "", "", "", 0);
        SortedSet<TimeRequest> requestsToRemove = requestStorageService.getRequests().headSet(twoDaysAgoRequests);
        requestStorageService.getRequests().removeAll(requestsToRemove);
        requestStorageService.commit();
    }


    @Every(period = 2, unit = TimeUnit.HOURS)
    @Route(method = HttpMethod.POST, uri = "/compute")
    public Result computeStats() throws Exception {
        Map<RouteRequest, TimeRequestStats> stats = requestStorageService.getStats();
        stats.clear();
        NavigableSet<TimeRequest> requests = requestStorageService.getRequests();
        for (TimeRequest timeRequest : requests) {
            RouteRequest routeRequest = new RouteRequest(timeRequest.getRoute(), timeRequest.getMethod());
            if (!stats.containsKey(routeRequest)) {
                List<TimeRequest> timeRequestsForRoute = requests.stream().filter(t -> t.getRoute().equals(routeRequest.getRoute()) && t.getMethod().equals(routeRequest.getMethod())).collect(Collectors.toList());
                long averageDuration = 0;
                long minDuration = -1;
                long maxDuration = 0;
                for (TimeRequest t : timeRequestsForRoute) {
                    averageDuration += t.getDuration();
                    if (minDuration == -1 || minDuration > t.getDuration()) {
                        minDuration = t.getDuration();
                    }
                    if (maxDuration < t.getDuration()) {
                        maxDuration = t.getDuration();
                    }
                }
                averageDuration = averageDuration / timeRequestsForRoute.size();
                stats.put(routeRequest, new TimeRequestStats(minDuration, averageDuration, maxDuration, timeRequestsForRoute.size()));
            }
        }
        requestStorageService.commit();
        return index();
    }

    @Route(method = HttpMethod.POST, uri = "/clear")
    public Result clearRequestsManually() throws Exception {
        requestStorageService.getRequests().clear();
        requestStorageService.commit();
        return index();
    }

    @Override
    public String label() {
        return "Requests Monitor";
    }

    @Override
    public String url() {
        return "/monitor/requests";
    }

    @Override
    public String category() {
        return "Wisdom";
    }

}

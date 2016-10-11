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
package org.wisdom.monitor.extensions.requests.service;

/**
 * Created by KEVIN on 06/10/2016.
 */

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.wisdom.monitor.extensions.requests.model.RouteRequest;
import org.wisdom.monitor.extensions.requests.model.TimeRequest;
import org.wisdom.monitor.extensions.requests.model.TimeRequestStats;

import java.util.Map;
import java.util.NavigableSet;

@Provides
@Component
@Instantiate
public class RequestStorageService {

    private NavigableSet<TimeRequest> requests;
    private DB database;
    private BTreeMap<RouteRequest, TimeRequestStats> stats;
    private Map<String, String> metadatas;

    public NavigableSet<TimeRequest> getRequests() {
        if (requests == null) {
            if (!database.exists("requests")) {
                database.createTreeSet("requests");
            }
            requests = database.getTreeSet("requests");
        }
        return requests;
    }

    public void setDatabase(DB database) {
        this.database = database;
    }

    public Map<RouteRequest, TimeRequestStats> getStats() {
        if (stats == null) {
            if (!database.exists("stats")) {
                database.createTreeMap("stats");
            }
            stats = database.getTreeMap("stats");
        }
        return stats;
    }

    public void commit() {
        if (database != null) {
            database.commit();
        }
    }

    public Map<String, String> getMetadatas() {
        if (metadatas == null) {
            if (!database.exists("metadatas")) {
                database.createHashMap("metadatas");
            }
            metadatas = database.getHashMap("metadatas");
        }
        return metadatas;
    }
}

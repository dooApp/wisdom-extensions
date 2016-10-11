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

import org.apache.felix.ipojo.annotations.*;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;

/**
 * Created by KEVIN on 06/10/2016.
 */

@Component
@Instantiate
public class DBInit {

    @Requires
    RequestStorageService requestStorageService;

    private DB db;

    @Validate
    public void start() {
        final ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            db = DBMaker.newFileDB(new File("requests.db")).make();
            requestStorageService.setDatabase(db);
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    @Invalidate
    public void stop() throws Exception {
        db.close();
    }
}

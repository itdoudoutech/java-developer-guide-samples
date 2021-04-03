/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doudou.rest.client;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;

class HttpPostInvocation extends AbstractHttpInvocation {

    private final Entity<?> entity;

    HttpPostInvocation(URI uri, MultivaluedMap<String, Object> headers, Entity<?> entity) {
        super(uri, headers);
        this.entity = entity;
    }

    @Override
    protected String getMethod() {
        return HttpMethod.POST;
    }

    @Override
    protected void process(HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        OutputStream ops = connection.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(ops, "UTF-8");
        osw.write(entity.getEntity().toString());
        osw.flush();
    }
}

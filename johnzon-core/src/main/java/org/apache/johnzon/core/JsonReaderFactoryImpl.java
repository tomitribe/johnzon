/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.johnzon.core;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

import static java.util.Arrays.asList;

class JsonReaderFactoryImpl implements JsonReaderFactory, Serializable {
    private static final Logger LOGGER = Logger.getLogger(JsonReaderFactoryImpl.class.getName());

    private final Map<String, Object> internalConfig = new HashMap<String, Object>();
    private static final Collection<String> SUPPORTED_CONFIG_KEYS = asList(
        JsonParserFactoryImpl.BUFFER_STRATEGY, JsonParserFactoryImpl.MAX_STRING_LENGTH,
        JsonParserFactoryImpl.BUFFER_LENGTH, JsonParserFactoryImpl.SUPPORTS_COMMENTS
    );
    private final JsonParserFactoryImpl parserFactory;

    JsonReaderFactoryImpl(final Map<String, ?> config) {

        if(config != null) {
            
            for (String configKey : config.keySet()) {
                if(SUPPORTED_CONFIG_KEYS.contains(configKey)) {
                    internalConfig.put(configKey, config.get(configKey));
                } else {
                    LOGGER.warning(configKey + " not supported by " + getClass().getName());
                }
            }
            
            this.parserFactory = new JsonParserFactoryImpl(internalConfig);
            
        } else {
            this.parserFactory = new JsonParserFactoryImpl(null);
        }
        
    }

    @Override
    public JsonReader createReader(final Reader reader) {
        return new JsonReaderImpl(parserFactory.createInternalParser(reader));
    }

    @Override
    public JsonReader createReader(final InputStream in) {
        return new JsonReaderImpl(parserFactory.createInternalParser(in));
    }

    @Override
    public JsonReader createReader(final InputStream in, final Charset charset) {
        return new JsonReaderImpl(parserFactory.createInternalParser(in, charset));
    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return Collections.unmodifiableMap(internalConfig);
    }
}

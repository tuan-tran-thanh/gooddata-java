/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Asynchronous task containing link for polling.
 * Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("asyncTask")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AsyncTask {

    @JsonProperty
    private Link link;

    @JsonCreator
    private AsyncTask(@JsonProperty("link") Link link) {
        this.link = link;
    }

    public AsyncTask(final String uri) {
        this.link = new Link(uri);
    }

    @JsonIgnore
    public String getUri() {
        return link.getPoll();
    }

    private static class Link {

        private final String poll;

        @JsonCreator
        private Link(@JsonProperty("poll") String poll) {
            this.poll = poll;
        }

        public String getPoll() {
            return poll;
        }
    }

}

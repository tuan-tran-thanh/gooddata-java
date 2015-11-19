/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

/**
 * Deserializes to Joda's {@link LocalDate} fields from the GoodData date time format (yyyy-MM-dd).
 */
public class GDDateDeserializer extends JsonDeserializer<LocalDate> {

    static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .append(ISODateTimeFormat.date())
            .toFormatter().withZone(DateTimeZone.UTC);

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        final JsonNode root = jp.readValueAsTree();
        if (root == null || root.isNull()) {
            return null;
        }
        return FORMATTER.parseLocalDate(root.textValue());
    }

}

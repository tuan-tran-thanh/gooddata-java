package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashSet;
import java.util.Set;

import static com.gooddata.Validate.noNullElements;
import static com.gooddata.Validate.notNull;

/**
 * UsedBy/Using result
 */
@JsonTypeName("inUse")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
class InUse {

    public static final String USEDBY_URI = "/gdc/md/{projectId}/usedby2";
    public static final String USING_URI = "/gdc/md/{projectId}/using2";

    private final String uri;

    private final Set<String> type;

    public InUse(final String uri, final Set<String> type) {
        this.uri = notNull(uri, "uri");
        this.type = type;
    }

    @SafeVarargs
    public InUse(final String uri, final Class<? extends Obj>... type) {
        this.uri = notNull(uri, "uri");
        noNullElements(type, "type");
        this.type = new HashSet<>();
        for (Class<? extends Obj> t: type) {
            this.type.add(t.getSimpleName()); // todo lower case first letter
        }
    }

    public String getUri() {
        return uri;
    }

    public Set<String> getType() {
        return type;
    }
}

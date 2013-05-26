/**
 * Copyright (c) 2013, bibrarian.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the bibrarian.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.bibrarian.dyn;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.bibrarian.dynamo.Cursor;
import com.bibrarian.om.Query;
import com.bibrarian.om.Queryable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Query.
 *
 * @param <T> Type of encapsulated elements
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "cursor", "conditions" })
final class DynQuery<T> implements Query<T> {

    /**
     * Cursor at work.
     */
    private final transient Cursor<T> cursor;

    /**
     * Conditions.
     */
    private final transient Map<String, Condition> conditions;

    /**
     * Public ctor.
     * @param cur Cursor
     */
    protected DynQuery(@NotNull final Cursor<T> cur) {
        this(cur, new HashMap<String, Condition>(0));
    }

    /**
     * Public ctor.
     * @param cur Cursor we're at
     * @param query Query
     */
    @SuppressWarnings("unchecked")
    protected DynQuery(@NotNull final Cursor<T> cur,
        @NotNull final Query<T> query) {
        this(cur, DynQuery.class.cast(query).conditions);
    }

    /**
     * Public ctor.
     * @param cur Cursor we're at
     * @param cnd Conditions
     */
    private DynQuery(@NotNull final Cursor<T> cur,
        @NotNull final Map<String, Condition> cnd) {
        this.cursor = cur;
        this.conditions = cnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> with(String key, String value) {
        final Map<String, Condition> cnd = new HashMap<String, Condition>(0);
        cnd.putAll(this.conditions);
        cnd.put(
            key,
            new Condition()
                .withAttributeValueList(new AttributeValue(value))
                .withComparisonOperator(ComparisonOperator.EQ)
        );
        return new DynQuery<T>(this.cursor, cnd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queryable<T> refine() {
        return new DynQueryable<T>(this.cursor, this);
    }

}

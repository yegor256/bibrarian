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

import com.bibrarian.dynamo.Conditions;
import com.bibrarian.dynamo.Frame;
import com.bibrarian.om.Query;
import com.bibrarian.om.Queryable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Query.
 *
 * @param <T> Type of encapsulated elements
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "frame")
final class DynQuery<T> implements Query<T> {

    /**
     * Frame at work.
     */
    private final transient Frame frame;

    /**
     * Queryable.
     */
    private final transient AbstractQueryable<T> queryable;

    /**
     * Public ctor.
     * @param frm Frame
     * @param qry Query to encapsulate
     */
    protected DynQuery(@NotNull final Frame frm,
        @NotNull final AbstractQueryable<T> qry) {
        this.frame = frm;
        this.queryable = qry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> with(final String key, final String value) {
        return new DynQuery<T>(
            this.frame.where(key, Conditions.equalTo(value)),
            this.queryable
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queryable<T> refine() {
        return this.queryable.with(this.frame);
    }

}

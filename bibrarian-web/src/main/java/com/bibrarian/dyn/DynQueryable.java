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

import com.bibrarian.dynamo.Cursor;
import com.bibrarian.om.Query;
import com.bibrarian.om.Queryable;
import com.google.common.collect.Iterators;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.util.AbstractCollection;
import java.util.Iterator;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Queryable.
 *
 * @param <T> Type of encapsulated elements
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(callSuper = false, of = { "cursor", "qry" })
public final class DynQueryable<T> extends
    AbstractCollection<T> implements Queryable<T> {

    /**
     * Cursor.
     */
    private final transient Cursor<T> cursor;

    /**
     * Query encapsulated.
     */
    private final transient Query<T> qry;

    /**
     * Public ctor.
     * @param cur Cursor
     */
    public DynQueryable(@NotNull final Cursor<T> cur) {
        this(cur, new DynQuery<T>(cur));
    }

    /**
     * Public ctor.
     * @param cur Cursor
     * @param query Query to encapsulate
     */
    protected DynQueryable(@NotNull final Cursor<T> cur,
        @NotNull final Query<T> query) {
        this.cursor = cur;
        this.qry = query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return new Cursor.Mapped<T>(this.cursor).iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return Iterators.size(this.iterator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(final T item) {
        this.cursor.add(item);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(final Object object) {
        this.cursor.iterator().remove();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> query() {
        return new DynQuery<T>(this.cursor, this.qry);
    }


}

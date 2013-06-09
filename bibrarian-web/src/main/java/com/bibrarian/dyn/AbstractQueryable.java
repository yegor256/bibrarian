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

import com.bibrarian.om.Artifact;
import com.bibrarian.om.Discovery;
import com.bibrarian.om.Hypothesis;
import com.bibrarian.om.Query;
import com.bibrarian.om.Queryable;
import com.google.common.reflect.TypeToken;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Frame;
import com.jcabi.dynamo.Item;
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
@EqualsAndHashCode(callSuper = false, of = "frm")
abstract class AbstractQueryable<T> extends
    AbstractCollection<T> implements Queryable<T> {

    /**
     * Frame of items.
     */
    private final transient Frame frm;

    /**
     * Public ctor.
     * @param frame Frame to encapsulate
     */
    protected AbstractQueryable(@NotNull final Frame frame) {
        super();
        this.frm = frame;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        final Iterator<Item> items = this.frm.iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return items.hasNext();
            }
            @Override
            public T next() {
                return AbstractQueryable.this.toObject(items.next());
            }
            @Override
            public void remove() {
                items.remove();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return this.frm.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> query() {
        return new DynQuery<T>(this.frm, this);
    }

    /**
     * Get access to encapsulated.
     * @return Frame
     */
    protected Frame frame() {
        return this.frm;
    }

    /**
     * With this frame.
     * @param frame New frame
     * @return Queryable
     */
    protected abstract Queryable<T> with(final Frame frame);

    /**
     * Item to object.
     * @param item Item
     * @return Object
     */
    @SuppressWarnings("unchecked")
    private T toObject(final Item item) {
        final Class<?> type = new TypeToken<T>(this.getClass()) {
            private static final long serialVersionUID = 1L;
        } .getRawType();
        Object object;
        if (type.equals(Artifact.class)) {
            object = new DynArtifact(item);
        } else if (type.equals(Discovery.class)) {
            object = new DynDiscovery(item);
        } else if (type.equals(Hypothesis.class)) {
            object = new DynHypothesis(item);
        } else {
            throw new IllegalArgumentException(
                String.format("invalid type %s", type)
            );
        }
        return (T) object;
    }

}

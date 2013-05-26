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
package com.bibrarian.dynamo;

import com.jcabi.aspects.Immutable;
import java.util.Iterator;
import javax.validation.constraints.NotNull;

/**
 * DynamoDB cursor.
 *
 * @param <T> Type of item
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
public interface Cursor<T> extends Iterable<Cursor<T>> {

    /**
     * Mapped to values.
     * @param <X> Type of items
     */
    final class Mapped<X> implements Iterable<X> {
        /**
         * Cursor.
         */
        private final transient Cursor<X> cursor;
        /**
         * Public ctor.
         * @param cur Cursor to encapsulate
         */
        public Mapped(final Cursor<X> cur) {
            this.cursor = cur;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<X> iterator() {
            final Iterator<Cursor<X>> iterator = this.cursor.iterator();
            return new Iterator<X>() {
                @Override
                public boolean hasNext() {
                   return iterator.hasNext();
                }
                @Override
                public X next() {
                   return iterator.next().load();
                }
                @Override
                public void remove() {
                    iterator.remove();
                }
            };
        }
    }

    /**
     * Refine scan conditions.
     * @param ref Refinement
     * @return New cursor
     */
    @NotNull
    Cursor<T> refine(Refinement ref);

    /**
     * Load item.
     * @param <T> Type of result
     * @return New cursor
     */
    @NotNull
    <T> T load();

    /**
     * Follow the link from current element to another table.
     * @param <X> Type of result
     * @param attribute Attribute to use to cross-link
     * @return New cursor
     */
    @NotNull
    <X> Cursor<X> follow(String attribute);

    /**
     * Inversive follow.
     * @param <X> Type of result
     * @param table Table name
     * @param attribute Attribute to use to cross-link
     * @return New cursor
     */
    @NotNull
    <X> Cursor<X> inverse(String table, String attribute);

    /**
     * Alter item at the current cursor location (or create a new one).
     * @param alt Alteration to use
     * @return TRUE if altered successfully
     */
    @NotNull
    boolean alter(Alteration alt);


    /**
     * Add new item to the table.
     * @param item Item to add
     * @return New cursor, pointing at the item added
     */
    @NotNull
    Cursor<T> add(T item);
}

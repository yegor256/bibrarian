/**
 * Copyright (c) 2013-2014, bibrarian.com
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

import com.bibrarian.om.Book;
import com.bibrarian.om.Pageable;
import com.bibrarian.om.Quote;
import com.bibrarian.om.Tag;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Item;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Quote in Dynamo.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "item")
final class DyQuote implements Quote {

    /**
     * Table name.
     */
    public static final String TABLE = "quotes";

    /**
     * Hash.
     */
    public static final String HASH = "id";

    /**
     * Range.
     */
    public static final String RANGE = "book";

    /**
     * Text of the quote.
     */
    public static final String ATTR_TEXT = "text";

    /**
     * Pages of the quote.
     */
    public static final String ATTR_PAGES = "pages";

    /**
     * Item.
     */
    private final transient Item item;

    /**
     * Public ctor.
     * @param itm Item
     */
    DyQuote(final Item itm) {
        this.item = itm;
    }

    @Override
    public Pageable<Tag> tags() {
        throw new UnsupportedOperationException("#tags()");
    }

    @Override
    public Book book() {
        throw new UnsupportedOperationException("#book()");
    }

    @Override
    public String text() {
        throw new UnsupportedOperationException("#text()");
    }

    @Override
    public void text(final String text) {
        throw new UnsupportedOperationException("#text()");
    }

    @Override
    public String pages() {
        throw new UnsupportedOperationException("#pages()");
    }

    @Override
    public void pages(final String pages) {
        throw new UnsupportedOperationException("#pages()");
    }
}

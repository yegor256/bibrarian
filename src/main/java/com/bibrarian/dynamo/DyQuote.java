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
import com.bibrarian.om.Quote;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.AttributeUpdates;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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
@EqualsAndHashCode(of = { "region", "number" })
final class DyQuote implements Quote {

    /**
     * Region.
     */
    private final transient Region region;

    /**
     * Number.
     */
    private final transient long number;

    /**
     * Public ctor.
     * @param reg Region
     * @param num Number
     */
    DyQuote(final Region reg, final long num) {
        this.region = reg;
        this.number = num;
    }

    @Override
    public Collection<String> tags() {
        return Collections.emptyList();
    }

    @Override
    public Book book() throws IOException {
        final Iterator<String> books = new Refs(this.region).forward(
            String.format("Q:%d", this.number),
            Collections.singleton(Refs.withPrefix("B:"))
        ).iterator();
        if (!books.hasNext()) {
            throw new IllegalStateException(
                String.format("book not found for quote #%d", this.number)
            );
        }
        return new DyBook(this.region, books.next().substring(2));
    }

    @Override
    public String text() throws IOException {
        return this.item().get(DyQuotes.ATTR_TEXT).getS();
    }

    @Override
    public void text(final String text) throws IOException {
        this.item().put(
            new AttributeUpdates().with(
                DyQuotes.ATTR_TEXT, text
            )
        );
    }

    @Override
    public String pages() throws IOException {
        return this.item().get(DyQuotes.ATTR_PAGES).getS();
    }

    @Override
    public void pages(final String pages) throws IOException {
        this.item().put(
            new AttributeUpdates().with(
                DyQuotes.ATTR_PAGES, pages
            )
        );
    }

    /**
     * Get its item.
     * @return Item
     */
    private Item item() {
        final Iterator<Item> items = this.region.table(DyQuotes.TABLE)
            .frame()
            .through(new QueryValve().withLimit(1))
            .where(DyQuotes.HASH, Conditions.equalTo(this.number))
            .iterator();
        if (!items.hasNext()) {
            throw new IllegalStateException(
                String.format("quote #%d not found", this.number)
            );
        }
        return items.next();
    }

}

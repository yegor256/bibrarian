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
import com.bibrarian.om.Tag;
import com.bibrarian.om.Tags;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.AttributeUpdates;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import java.io.IOException;
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
@EqualsAndHashCode(of = { "region", "num" })
final class DyQuote implements Quote {

    /**
     * Format.
     */
    public static final String FMT = "Q:%08d";

    /**
     * Region.
     */
    private final transient Region region;

    /**
     * Number.
     */
    private final transient long num;

    /**
     * Public ctor.
     * @param reg Region
     * @param number Number
     */
    DyQuote(final Region reg, final long number) {
        this.region = reg;
        this.num = number;
    }

    @Override
    public long number() {
        return this.num;
    }

    @Override
    public Tags tags() {
        return new DyQuoteTags(this.region, this.num);
    }

    @Override
    public void tag(final Tag tag) throws IOException {
        if (!tag.name().matches("[a-zA-Z0-9\\-]{3,100}")) {
            throw new Quote.IncorrectTagException(
                // @checkstyle LineLength (1 line)
                "tag must contain 3..100 English letters, numbers, spaces or dashes"
            );
        }
        new Refs(this.region).put(
            String.format(DyQuote.FMT, this.num),
            new Tag.Simple(tag).ref()
        );
        new Refs(this.region).put(
            String.format("U:%s", tag.login()),
            new Tag.Simple(tag).ref()
        );
    }

    @Override
    public Book book() {
        final Iterator<String> books = new Refs(this.region).forward(
            String.format(DyQuote.FMT, this.num),
            Collections.singleton(Refs.withPrefix("B:"))
        ).iterator();
        if (!books.hasNext()) {
            throw new IllegalStateException(
                String.format("book not found for quote #%d", this.num)
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
            .where(DyQuotes.HASH, Conditions.equalTo(this.num))
            .iterator();
        if (!items.hasNext()) {
            throw new IllegalStateException(
                String.format("quote #%d not found", this.num)
            );
        }
        return items.next();
    }

}

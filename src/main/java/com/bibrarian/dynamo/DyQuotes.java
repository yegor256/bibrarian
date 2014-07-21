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

import co.stateful.Counter;
import com.bibrarian.om.Book;
import com.bibrarian.om.Quote;
import com.bibrarian.om.Quotes;
import com.bibrarian.om.Term;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Region;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Quotes in DynamoDB.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "region", "counter" })
final class DyQuotes implements Quotes {

    /**
     * Table name.
     */
    public static final String TABLE = "quotes";

    /**
     * Hash.
     */
    public static final String HASH = "id";

    /**
     * Text of the quote.
     */
    public static final String ATTR_TEXT = "text";

    /**
     * Pages of the quote.
     */
    public static final String ATTR_PAGES = "pages";

    /**
     * Region.
     */
    private final transient Region region;

    /**
     * Counter.
     */
    private final transient Counter counter;

    /**
     * Public ctor.
     * @param reg Region
     * @param cnt Counter
     */
    DyQuotes(final Region reg, final Counter cnt) {
        this.region = reg;
        this.counter = cnt;
    }

    @Override
    public Quotes refine(final Term term) {
        return this;
    }

    @Override
    public Collection<Term> terms() {
        return Collections.emptyList();
    }

    @Override
    public Quote add(final Book book, final String text,
        final String pages) throws IOException {
        final long number = this.counter.incrementAndGet(1L);
        new Refs(this.region).add(String.format("Q:%d", number), "-");
        this.region.table(DyQuotes.TABLE).put(
            new Attributes()
                .with(DyQuotes.HASH, number)
                .with(DyQuotes.ATTR_TEXT, text)
                .with(DyQuotes.ATTR_PAGES, pages)
        );
        return new DyQuote(this.region, number);
    }

    @Override
    public Iterable<Quote> iterate() {
        return Iterables.transform(
            new Refs(this.region).reverse("-", "Q:"),
            new Function<String, Quote>() {
                @Override
                public Quote apply(final String input) {
                    return new DyQuote(
                        DyQuotes.this.region,
                        Long.parseLong(input.substring(2))
                    );
                }
            }
        );
    }
}

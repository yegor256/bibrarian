/**
 * Copyright (c) 2013-2017, bibrarian.com
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
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.bibrarian.bib.Bibitem;
import com.bibrarian.om.Book;
import com.bibrarian.om.Quote;
import com.bibrarian.om.Quotes;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.Tv;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Quotes in DynamoDB.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "region", "counter", "term" })
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
     * Stub for search.
     */
    private static final String STUB = "-";

    /**
     * Region.
     */
    private final transient Region region;

    /**
     * Counter.
     */
    private final transient Counter counter;

    /**
     * Term to stick to.
     */
    private final transient String term;

    /**
     * Public ctor.
     * @param reg Region
     * @param cnt Counter
     */
    DyQuotes(final Region reg, final Counter cnt) {
        this(reg, cnt, DyQuotes.STUB);
    }

    /**
     * Public ctor.
     * @param reg Region
     * @param cnt Counter
     * @param trm Term
     */
    DyQuotes(final Region reg, final Counter cnt, final String trm) {
        this.region = reg;
        this.counter = cnt;
        this.term = trm.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public Quotes refine(final String trm) {
        return new DyQuotes(this.region, this.counter, trm);
    }

    @Override
    public Quote add(final Book book, final String text,
        final String pages) throws IOException {
        if (text.trim().length() < Tv.TEN) {
            throw new Quotes.InvalidQuoteException(
                "quote text can't be empty or shorter than 10 characters"
            );
        }
        if (text.length() > Tv.FIVE * Tv.HUNDRED) {
            throw new Quotes.InvalidQuoteException(
                "quote text can't be longer than 500 characters"
            );
        }
        final long number = this.counter.incrementAndGet(1L);
        this.region.table(DyQuotes.TABLE).put(
            new Attributes()
                .with(DyQuotes.HASH, number)
                .with(DyQuotes.ATTR_TEXT, text.trim())
                .with(DyQuotes.ATTR_PAGES, new Pages(pages).normalized())
        );
        new Refs(this.region).put(
            String.format(DyQuote.FMT, number),
            Iterables.concat(
                Arrays.asList(
                    DyQuotes.STUB,
                    String.format(DyBook.FMT, book.name())
                ),
                new Tokens(
                    Joiner.on(' ').join(
                        new Bibitem(book.bibitem()).cite(),
                        book.name(),
                        text
                    )
                ).iterate()
            )
        );
        Logger.info(this, "quote #%d created", number);
        return new DyQuote(this.region, number);
    }

    @Override
    public Quote get(final long number) throws Quotes.QuoteNotFoundException {
        final Iterator<Item> items = this.region.table(DyQuotes.TABLE)
            .frame()
            .through(new QueryValve().withLimit(1))
            .where(DyQuotes.HASH, Conditions.equalTo(number))
            .iterator();
        if (!items.hasNext()) {
            throw new Quotes.QuoteNotFoundException(
                String.format("quote #%d not found", number)
            );
        }
        return new DyQuote(this.region, number);
    }

    @Override
    public void delete(final long number) throws IOException {
        Iterables.removeIf(
            this.region.table(DyQuotes.TABLE)
                .frame()
                .through(new QueryValve().withLimit(1))
                .where(DyQuotes.HASH, Conditions.equalTo(number)),
            new Predicate<Item>() {
                @Override
                public boolean apply(final Item item) {
                    try {
                        Logger.info(
                            this, "quote #%s deleted",
                            item.get(DyQuotes.HASH).getN()
                        );
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                    return true;
                }
            }
        );
        new Refs(this.region).remove(
            String.format(DyQuote.FMT, number),
            Collections.<Condition>emptyList()
        );
    }

    @Override
    public Iterable<Quote> iterate() {
        return Iterables.transform(
            new Refs(this.region).reverse(
                this.term,
                Collections.singleton(Refs.withPrefix("q:"))
            ),
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

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
import com.bibrarian.tex.Bibitem;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.Tv;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

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
     * Pages pattern.
     */
    private static final Pattern PTN = Pattern.compile("(\\d+)(\\-\\d+)?");

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
        this.term = trm;
    }

    @Override
    public Quotes refine(final String trm) {
        return new DyQuotes(this.region, this.counter, trm);
    }

    @Override
    public Quote add(final Book book, final String text,
        final String pages) throws IOException {
        if (text.trim().length() < Tv.TWENTY) {
            throw new Quotes.InvalidQuoteException(
                "quote text can't be empty or shorter than 20 characters"
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
                .with(DyQuotes.ATTR_PAGES, DyQuotes.pages(pages))
        );
        new Refs(this.region).put(
            String.format("Q:%08d", number),
            Iterables.concat(
                Arrays.asList(
                    DyQuotes.STUB,
                    String.format("B:%s", book.name())
                ),
                DyQuotes.words(
                    Joiner.on(' ').join(
                        new Bibitem(book.bibitem()).cite(),
                        book.name(),
                        text
                    )
                )
            )
        );
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
    public Iterable<Quote> iterate() {
        return Iterables.transform(
            new Refs(this.region).reverse(
                this.term,
                Collections.singleton(Refs.withPrefix("Q:"))
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

    /**
     * Fetch words from text.
     * @param text Text
     * @return All words found
     */
    private static Iterable<String> words(final String text) {
        return Arrays.asList(
            StringUtils.split(
                text.replaceAll("(\\s+|^[\\P{Alnum}]+)", " "),
                ' '
            )
        );
    }

    /**
     * Normalize pages.
     * @param text Text
     * @return Pages in "pp.34-38" or "p.56, p.78" format
     * @throws Quotes.InvalidQuoteException If can't parse
     */
    private static String pages(final CharSequence text)
        throws Quotes.InvalidQuoteException {
        final Matcher matcher = DyQuotes.PTN.matcher(text);
        final Collection<String> found = new LinkedList<String>();
        while (matcher.find()) {
            if (matcher.group(2) == null) {
                found.add(String.format("p.%s", matcher.group(1)));
            } else {
                found.add(String.format("pp.%s", matcher.group(0)));
            }
        }
        if (found.isEmpty()) {
            throw new Quotes.InvalidQuoteException(
                "at least one page should be referenced"
            );
        }
        if (found.size() > Tv.FIVE) {
            throw new Quotes.InvalidQuoteException(
                "too many page references, maximum is five"
            );
        }
        return Joiner.on(" and ").join(found);
    }

}

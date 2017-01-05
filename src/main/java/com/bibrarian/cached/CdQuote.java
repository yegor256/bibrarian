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
package com.bibrarian.cached;

import com.bibrarian.om.Book;
import com.bibrarian.om.Quote;
import com.bibrarian.om.Tag;
import com.bibrarian.om.Tags;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Cached quote.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.3
 */
@Immutable
@ToString
@EqualsAndHashCode(of = "origin")
public final class CdQuote implements Quote {

    /**
     * Original.
     */
    private final transient Quote origin;

    /**
     * Public ctor.
     * @param orgn Original object
     */
    public CdQuote(final Quote orgn) {
        this.origin = orgn;
    }

    @Override
    public long number() {
        return this.origin.number();
    }

    @Override
    public Tags tags() {
        return new CdTags(this.origin.tags());
    }

    @Override
    public void tag(final Tag tag) throws IOException {
        this.origin.tag(tag);
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public Book book() throws IOException {
        return new CdBook(this.origin.book());
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public String text() throws IOException {
        return this.origin.text();
    }

    @Override
    @Cacheable.FlushAfter
    public void text(final String text) throws IOException {
        this.origin.text(text);
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public String pages() throws IOException {
        return this.origin.pages();
    }

    @Override
    @Cacheable.FlushAfter
    public void pages(final String pages) throws IOException {
        this.origin.pages(pages);
    }
}

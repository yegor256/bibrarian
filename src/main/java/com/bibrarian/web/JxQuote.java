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
package com.bibrarian.web;

import com.bibrarian.om.Quote;
import com.bibrarian.om.Tag;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.rexsl.page.Link;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Jaxb Quote.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@XmlRootElement(name = "quote")
@XmlAccessorType(XmlAccessType.NONE)
final class JxQuote {

    /**
     * Quote.
     */
    private final transient Quote quote;

    /**
     * Uri Info.
     */
    private final transient UriInfo info;

    /**
     * Ctor.
     */
    JxQuote() {
        throw new UnsupportedOperationException("#JxQuote()");
    }

    /**
     * Ctor.
     * @param qte Quote
     * @param inf Info
     */
    JxQuote(final Quote qte, final UriInfo inf) {
        this.quote = qte;
        this.info = inf;
    }

    /**
     * Its number.
     * @return Number
     */
    @XmlAttribute(name = "id")
    public long getId() {
        return this.quote.number();
    }

    /**
     * Its text.
     * @return Text
     * @throws IOException If fails
     */
    @XmlElement(name = "text")
    public String getText() throws IOException {
        return this.quote.text();
    }

    /**
     * Its pages.
     * @return Pages
     * @throws IOException If fails
     */
    @XmlElement(name = "pages")
    public String getPages() throws IOException {
        return this.quote.pages();
    }

    /**
     * Its book.
     * @return Book
     * @throws IOException If fails
     */
    @XmlElement(name = "book")
    public JxBook getBook() throws IOException {
        return new JxBook(this.quote.book(), this.info);
    }

    /**
     * Its links.
     * @return Links
     */
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    public Collection<Link> getLinks() {
        return Collections.singleton(
            new Link(
                "open",
                this.info.getBaseUriBuilder().clone()
                    .path(QuoteRs.class)
                    .build(this.quote.number())
            )
        );
    }

    /**
     * Its tags.
     * @return Tags
     */
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tag")
    public Collection<JxTag> getTags() {
        return Lists.newArrayList(
            Iterables.transform(
                this.quote.tags().iterate(),
                new Function<Tag, JxTag>() {
                    @Override
                    public JxTag apply(final Tag input) {
                        return new JxTag(input, JxQuote.this.info);
                    }
                }
            )
        );
    }

}

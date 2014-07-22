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

import com.bibrarian.bib.BibSyntaxException;
import com.bibrarian.bib.Bibitem;
import com.bibrarian.om.Book;
import com.rexsl.page.Link;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Jaxb Book.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@XmlRootElement(name = "book")
@XmlAccessorType(XmlAccessType.NONE)
final class JxBook {

    /**
     * Book.
     */
    private final transient Book book;

    /**
     * Uri Info.
     */
    private final transient UriInfo info;

    /**
     * Ctor.
     */
    JxBook() {
        throw new UnsupportedOperationException("#JxBook()");
    }

    /**
     * Ctor.
     * @param bok Book
     * @param inf URI info
     */
    JxBook(final Book bok, final UriInfo inf) {
        this.book = bok;
        this.info = inf;
    }

    /**
     * Name of it.
     * @return Name
     */
    @XmlElement(name = "name")
    public String getName() {
        return this.book.name();
    }

    /**
     * Cite of it.
     * @return Cite
     * @throws IOException If fails
     */
    @XmlElement(name = "cite")
    public String getCite() throws IOException {
        try {
            return new Bibitem(this.book.bibitem()).cite();
        } catch (final BibSyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Its links.
     * @return Links
     */
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    public Collection<Link> getLinks() {
        return Arrays.asList(
            new Link(
                "open",
                this.info.getBaseUriBuilder().clone()
                    .path(HomeRs.class)
                    .queryParam("q", "{term}")
                    .build(String.format("B:%s", this.book.name()))
            ),
            new Link(
                "add-quote",
                this.info.getBaseUriBuilder().clone()
                    .path(AddQuoteRs.class)
                    .build(this.book.name())
            )
        );
    }

}

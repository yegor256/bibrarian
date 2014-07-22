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

import com.bibrarian.bib.Bibitem;
import com.bibrarian.om.Book;
import com.bibrarian.om.Books;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import java.io.IOException;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Books in DynamoDB.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "region")
final class DyBooks implements Books {

    /**
     * Table in DynamoDB.
     */
    public static final String TABLE = "books";

    /**
     * Hash.
     */
    public static final String HASH = "name";

    /**
     * Bibitem.
     */
    public static final String ATTR_BIBITEM = "bibitem";

    /**
     * Region.
     */
    private final transient Region region;

    /**
     * Public ctor.
     * @param reg Region
     */
    DyBooks(final Region reg) {
        this.region = reg;
    }

    @Override
    public Book get(final String name) throws Books.BookNotFoundException {
        final Iterator<Item> items = this.region.table(DyBooks.TABLE)
            .frame()
            .through(new QueryValve().withLimit(1))
            .where(DyBooks.HASH, name)
            .iterator();
        if (!items.hasNext()) {
            throw new Books.BookNotFoundException(
                String.format("book '%s' not found", name)
            );
        }
        return new DyBook(this.region, name);
    }

    @Override
    public Book add(final String bibtex) throws IOException {
        final Bibitem bib = new Bibitem(bibtex);
        if (!bib.name().matches("[a-zA-Z0-9]{3,40}")) {
            throw new IllegalArgumentException(
                String.format("invalid book name [%s]", bib.name())
            );
        }
        final Iterator<Item> items = this.region.table(DyBooks.TABLE)
            .frame()
            .through(new QueryValve().withLimit(1))
            .where(DyBooks.HASH, bib.name())
            .iterator();
        if (items.hasNext()) {
            throw new Books.DuplicateBookException(
                String.format("book [%s] already exists", bib.name())
            );
        }
        this.region.table(DyBooks.TABLE).put(
            new Attributes()
                .with(DyBooks.HASH, bib.name())
                .with(DyBooks.ATTR_BIBITEM, bib.tex())
        );
        return new DyBook(this.region, bib.name());
    }
}

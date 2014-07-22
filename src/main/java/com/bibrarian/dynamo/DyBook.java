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
import com.bibrarian.om.Books;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.AttributeUpdates;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import java.io.IOException;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Artifact in Dynamo.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "region", "label" })
final class DyBook implements Book {

    /**
     * Format.
     */
    public static final String FMT = "b:%s";

    /**
     * Region.
     */
    private final transient Region region;

    /**
     * Name.
     */
    private final transient String label;

    /**
     * Public ctor.
     * @param reg Region
     * @param name Name
     */
    DyBook(final Region reg, final String name) {
        this.region = reg;
        this.label = name;
    }

    @Override
    public String name() {
        return this.label;
    }

    @Override
    public String bibitem() throws IOException {
        return this.item().get(DyBooks.ATTR_BIBITEM).getS();
    }

    @Override
    public void bibitem(final String tex) throws IOException {
        this.item().put(new AttributeUpdates().with(DyBooks.ATTR_BIBITEM, tex));
    }

    /**
     * Get item.
     * @return Item
     * @throws Books.BookNotFoundException If not found
     */
    private Item item() throws Books.BookNotFoundException {
        final Iterator<Item> items = this.region.table(DyBooks.TABLE)
            .frame()
            .through(new QueryValve().withLimit(1))
            .where(DyBooks.HASH, this.label)
            .iterator();
        if (!items.hasNext()) {
            throw new Books.BookNotFoundException(
                String.format("book '%s' not found", this.label)
            );
        }
        return items.next();
    }

}

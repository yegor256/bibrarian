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
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.AttributeUpdates;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import java.io.IOException;
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
        return this.region.table(DyBook.TABLE)
            .frame()
            .through(new QueryValve().withLimit(1))
            .where(DyBook.HASH, this.label)
            .iterator().next()
            .get(DyBook.ATTR_BIBITEM).getS();
    }

    @Override
    public void bibitem(final String tex) throws IOException {
        this.region.table(DyBook.TABLE)
            .frame()
            .through(new QueryValve().withLimit(1))
            .where(DyBook.HASH, this.label)
            .iterator().next()
            .put(new AttributeUpdates().with(DyBook.ATTR_BIBITEM, tex));
    }

}

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
import com.bibrarian.om.Pageable;
import com.bibrarian.om.Quote;
import com.bibrarian.om.User;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Region;
import com.jcabi.urn.URN;
import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * User in DynamoDB.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "region", "urn" })
final class DyUser implements User {

    /**
     * Region.
     */
    private final transient Region region;

    /**
     * URN of it.
     */
    private final transient URN urn;

    /**
     * Public ctor.
     * @param reg Region
     * @param name URN of him
     */
    DyUser(final Region reg, final URN name) {
        this.region = reg;
        this.urn = name;
    }

    @Override
    public String name() {
        throw new UnsupportedOperationException("#name()");
    }

    @Override
    public URI photo() {
        throw new UnsupportedOperationException("#photo()");
    }

    @Override
    public Pageable<String> tags() {
        throw new UnsupportedOperationException("#tags()");
    }

    @Override
    public Book add(final String bibtex) {
        throw new UnsupportedOperationException("#add()");
    }

    @Override
    public Quote add(final String book, final String text, final String pages) {
        throw new UnsupportedOperationException("#add()");
    }

}

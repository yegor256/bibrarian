/**
 * Copyright (c) 2013, bibrarian.com
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
package com.bibrarian.dyn;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.bibrarian.dynamo.Alteration;
import com.bibrarian.dynamo.Cursor;
import com.bibrarian.dynamo.Reverse;
import com.bibrarian.om.Artifact;
import com.bibrarian.om.Bibitem;
import com.bibrarian.om.Discovery;
import com.bibrarian.om.Queryable;
import com.google.common.collect.Iterators;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.net.URI;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Artifact in Dynamo.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "cursor", "label", "rfrt" })
final class DynArtifact implements Artifact {

    /**
     * Reverse mapping.
     */
    protected static final class Mapping implements Reverse<Artifact> {
        @Override
        public Artifact revert(final Cursor<Artifact> cursor,
            final Map<String, AttributeValue> attributes) {
            return new DynArtifact(
                cursor,
                attributes.get("bibitem").getS(),
                attributes.get("referat").getS()
            );
        }
    }

    /**
     * Cursor.
     */
    private final transient Cursor<Artifact> cursor;

    /**
     * Bibitem label.
     */
    private final transient String label;

    /**
     * Referat text.
     */
    private final transient String rfrt;

    /**
     * Public ctor.
     * @param cur Cursor
     * @param item Bibitem label
     * @param lbl Label
     */
    private DynArtifact(final Cursor<Artifact> cur, final String item,
        final String referat) {
        this.cursor = cur;
        this.label = item;
        this.rfrt = referat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bibitem bibitem() {
        return this.cursor.<Bibitem>follow("label").iterator().next().load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<URI> hardcopies() {
        return new AbstractCollection<URI>() {
            @Override
            public Iterator<URI> iterator() {
                return new Cursor.Mapped<URI>(
                    DynArtifact.this.cursor.<URI>inverse(
                        "hardcopies", "artifact"
                    )
                ).iterator();
            }
            @Override
            public int size() {
                return Iterators.size(this.iterator());
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String referat() {
        return this.rfrt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void referat(final String text) {
        this.cursor.alter(
            new Alteration() {
                @Override
                public void alter(final PutItemRequest request) {
                    request.getExpected().put(
                        "bibitem",
                        new ExpectedAttributeValue(
                            new AttributeValue(DynArtifact.this.label)
                        ).withExists(true)
                    );
                    request.getItem().put(
                        "referat",
                        new AttributeValue(DynArtifact.this.rfrt)
                    );
                }
            }
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queryable<Discovery> discoveries() {
        return new DynQueryable<Discovery>(
            this.cursor.<Discovery>inverse("discoveries", "artifact")
        );
    }

}

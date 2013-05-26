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
import com.bibrarian.om.Bibitem;
import com.bibrarian.om.Bibtex;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Bibitem in Dynamo.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "cursor")
final class DynBibitem implements Bibitem {

    /**
     * Reverse mapping.
     */
    protected static final class Mapping implements Reverse<Bibitem> {
        @Override
        public Bibitem revert(final Cursor<Bibitem> cursor,
            final Map<String, AttributeValue> attributes) {
            return new DynBibitem(
                cursor,
                attributes.get("label").getS(),
                new Bibtex(attributes.get("bibtex").getS())
            );
        }
    }

    /**
     * Cursor.
     */
    private final transient Cursor<Bibitem> cursor;

    /**
     * Label.
     */
    private final transient String label;

    /**
     * BibTeX.
     */
    private final transient Bibtex tex;

    /**
     * Public ctor.
     * @param lbl Label
     * @param cur Cursor
     * @param bib Bibtex
     */
    private DynBibitem(final Cursor<Bibitem> cur, final String lbl,
        final Bibtex bib) {
        this.label = lbl;
        this.cursor = cur;
        this.tex = bib;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bibtex load() {
        return this.tex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(final Bibtex text) {
        this.cursor.alter(
            new Alteration() {
                @Override
                public void alter(final PutItemRequest request) {
                    request.getExpected().put(
                        "label",
                        new ExpectedAttributeValue(
                            new AttributeValue(DynBibitem.this.label)
                        ).withExists(true)
                    );
                    request.getItem().put(
                        "bibtex",
                        new AttributeValue(DynBibitem.this.tex.toString())
                    );
                }
            }
        );
    }

}

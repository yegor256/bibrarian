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

import com.bibrarian.dynamo.Attributes;
import com.bibrarian.dynamo.Frame;
import com.bibrarian.om.Hypothesis;
import com.bibrarian.om.Queryable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Artifacts.
 *
 * @param <T> Type of encapsulated elements
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(callSuper = true, of = "owner")
final class DynHypothesizes extends AbstractQueryable<Hypothesis> {

    /**
     * Owner of the collection.
     */
    private final transient String owner;

    /**
     * Public ctor.
     * @param frame Frame
     * @param urn Owner of them
     */
    protected DynHypothesizes(final Frame frame, final String urn) {
        super(frame);
        this.owner = urn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(final Hypothesis hypothesis) {
        this.frame().table().put(
            new Attributes()
                .with("bibrarian", this.owner)
                .with("label", hypothesis.label())
                .with("description", hypothesis.description())
        );
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Queryable<Hypothesis> with(final Frame frame) {
        return new DynHypothesizes(frame, this.owner);
    }

}

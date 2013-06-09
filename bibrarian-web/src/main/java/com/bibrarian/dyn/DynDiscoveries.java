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

import com.bibrarian.om.Discovery;
import com.bibrarian.om.Queryable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Frame;
import com.jcabi.urn.URN;
import java.util.Date;
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
final class DynDiscoveries extends AbstractQueryable<Discovery> {

    /**
     * Owner of the collection.
     */
    private final transient URN owner;

    /**
     * Public ctor.
     * @param frame Frame
     * @param urn Owner of them
     */
    protected DynDiscoveries(final Frame frame, final URN urn) {
        super(frame);
        this.owner = urn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(final Discovery discovery) {
        this.frame().table().put(
            new Attributes()
                .with(DynDiscovery.BIBRARIAN, this.owner)
                .with(DynDiscovery.ARTIFACT_FIELD, discovery.artifact().label())
                .with(
                    DynDiscovery.HYPOTHESIS_FIELD,
                    discovery.hypothesis().label()
                )
                .with(DynDiscovery.DATE_FIELD, new Date().getTime())
        );
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Queryable<Discovery> with(final Frame frame) {
        return new DynDiscoveries(frame, this.owner);
    }

}

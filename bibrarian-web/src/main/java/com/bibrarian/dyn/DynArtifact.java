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
import com.bibrarian.dynamo.Conditions;
import com.bibrarian.dynamo.Frame;
import com.bibrarian.dynamo.Item;
import com.bibrarian.om.Artifact;
import com.bibrarian.om.Bibitem;
import com.bibrarian.om.Discovery;
import com.bibrarian.om.Queryable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
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
@EqualsAndHashCode(of = "item")
final class DynArtifact implements Artifact {

    /**
     * Item.
     */
    private final transient Item item;

    /**
     * Public ctor.
     * @param itm Item
     */
    protected DynArtifact(final Item itm) {
        this.item = itm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String label() {
        return this.item.get("label").getS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bibitem bibitem() {
        return new DynBibitem(
            this.item.frame().table().region().table("bibitems").frame()
                .where("label", Conditions.equalTo(this.label()))
                .iterator().next()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<URI> hardcopies() {
        final Frame frame = this.item.frame().table().region()
            .table("hardcopies").frame()
            .where("artifact", Conditions.equalTo(this.label()));
        final Collection<URI> hardcopies = new LinkedList<URI>();
        for (Item copy : frame) {
            hardcopies.add(URI.create(this.item.get("uri").getS()));
        }
        return hardcopies;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String referat() {
        return this.item.get("referat").getS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void referat(final String text) {
        this.item.put("referat", new AttributeValue(text));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queryable<Discovery> discoveries() {
        return new DynDiscoveries(
            this.item.frame().table().region().table("discoveries")
                .frame().where("artifact", Conditions.equalTo(this.label())),
            this.item.get("bibrarian").getS()
        );
    }

}

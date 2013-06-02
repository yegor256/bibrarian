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
import com.bibrarian.dynamo.Item;
import com.bibrarian.om.Artifact;
import com.bibrarian.om.Discovery;
import com.bibrarian.om.Hypothesis;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Discovery in Dynamo.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "item")
final class DynDiscovery implements Discovery {

    /**
     * Column in Dynamo table.
     */
    public static final String BIBRARIAN = "bibrarian";

    /**
     * Column in Dynamo table.
     */
    public static final String ARTIFACT_FIELD = "artifact";

    /**
     * Column in Dynamo table.
     */
    public static final String HYPOTHESIS_FIELD = "hypothesis";

    /**
     * Column in Dynamo table.
     */
    public static final String DATE_FIELD = "date";

    /**
     * Column in Dynamo table.
     */
    public static final String QUOTE_FIELD = "quote";

    /**
     * Column in Dynamo table.
     */
    public static final String PAGES_FIELD = "pages";

    /**
     * Column in Dynamo table.
     */
    public static final String RELEVANCE_FIELD = "relevance";

    /**
     * Item.
     */
    private final transient Item item;

    /**
     * Public ctor.
     * @param itm Item
     */
    protected DynDiscovery(final Item itm) {
        this.item = itm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date date() {
        return new Date(
            Long.parseLong(this.item.get(DynDiscovery.DATE_FIELD).getS())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Hypothesis hypothesis() {
        final String label = this.item
            .get(DynDiscovery.HYPOTHESIS_FIELD).getS();
        return new DynHypothesis(
            this.item.frame().table().region().table("hypothesizes").frame()
                .where(DynHypothesis.LABEL_FIELD, Conditions.equalTo(label))
                .iterator().next()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Artifact artifact() {
        final String label = this.item.get(DynDiscovery.ARTIFACT_FIELD).getS();
        return new DynArtifact(
            this.item.frame().table().region().table("artifacts").frame()
                .where(DynArtifact.LABEL_FIELD, Conditions.equalTo(label))
                .iterator().next()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String quote() {
        return this.item.get(DynDiscovery.QUOTE_FIELD).getS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void quote(final String text) {
        this.item.put(DynDiscovery.QUOTE_FIELD, new AttributeValue(text));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String pages() {
        return this.item.get(DynDiscovery.PAGES_FIELD).getS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pages(final String pages) {
        this.item.put(DynDiscovery.PAGES_FIELD, new AttributeValue(pages));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double relevance() {
        return Double.parseDouble(
            this.item.get(DynDiscovery.RELEVANCE_FIELD).getS()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void relevance(final double relevance) {
        this.item.put(
            DynDiscovery.RELEVANCE_FIELD,
            new AttributeValue(Double.toString(relevance))
        );
    }

}

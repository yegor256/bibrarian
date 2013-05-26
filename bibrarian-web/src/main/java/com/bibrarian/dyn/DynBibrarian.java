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
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.bibrarian.dynamo.Item;
import com.bibrarian.om.Artifact;
import com.bibrarian.om.Bibrarian;
import com.bibrarian.om.Discovery;
import com.bibrarian.om.Hypothesis;
import com.bibrarian.om.Queryable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Bibrarian in Dynamo DB.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: BaseRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "item")
final class DynBibrarian implements Bibrarian {

    /**
     * Item.
     */
    private final transient Item item;

    /**
     * Public ctor.
     * @param cur Cursor
     * @param urn Name of him
     */
    protected DynBibrarian(@NotNull final Item itm) {
        this.item = itm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queryable<Artifact> artifacts() {
        return new DynArtifacts(
            this.item.region().table("artifacts").frame().where(
                "bibrarian",
                new Condition()
                    .withAttributeValueList(new AttributeValue(this.name()))
                    .withComparisonOperator(ComparisonOperator.EQ)
            ),
            this.name()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queryable<Hypothesis> hypothesizes() {
        return new DynHypothesizes(
            this.item.region().table("hypothesizes").frame().where(
                "bibrarian",
                new Condition()
                    .withAttributeValueList(new AttributeValue(this.name()))
                    .withComparisonOperator(ComparisonOperator.EQ)
            ),
            this.name()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queryable<Discovery> discoveries() {
        return new DynDiscoveries(
            this.item.region().table("discoveries").frame().where(
                "bibrarian",
                new Condition()
                    .withAttributeValueList(new AttributeValue(this.name()))
                    .withComparisonOperator(ComparisonOperator.EQ)
            ),
            this.name()
        );
    }

    /**
     * His name.
     * @return Name
     */
    private String name() {
        return this.item.get("name").getS();
    }
}

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
import com.bibrarian.dynamo.Item;
import com.bibrarian.om.Hypothesis;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Hypothesis in Dynamo.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "item")
final class DynHypothesis implements Hypothesis {

    /**
     * Column in Dynamo table.
     */
    public static final String BIBRARIAN = "bibrarian";

    /**
     * Column in Dynamo table.
     */
    public static final String LABEL_FIELD = "label";

    /**
     * Column in Dynamo table.
     */
    public static final String DESCRIPTION_LABEL = "description";

    /**
     * Item.
     */
    private final transient Item item;

    /**
     * Public ctor.
     * @param itm Item
     */
    protected DynHypothesis(final Item itm) {
        this.item = itm;
    }

    /**
     * Map an object to attributes.
     * @param hypothesis Hypothesis
     * @param attributes Where to map to
     */
    public static void toItem(final Hypothesis hypothesis,
        final Map<String, AttributeValue> attributes) {
        attributes.put(
            DynHypothesis.LABEL_FIELD,
            new AttributeValue(hypothesis.label())
        );
        attributes.put(
            DynHypothesis.DESCRIPTION_LABEL,
            new AttributeValue(hypothesis.description())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String label() {
        return this.item.get(DynHypothesis.LABEL_FIELD).getS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String description() {
        return this.item.get(DynHypothesis.DESCRIPTION_LABEL).getS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void description(final String text) {
        this.item.put(
            DynHypothesis.DESCRIPTION_LABEL,
            new AttributeValue(text)
        );
    }

}

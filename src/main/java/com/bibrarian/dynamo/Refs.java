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

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Frame;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Refs.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "region")
final class Refs {

    /**
     * Table name.
     */
    public static final String TABLE = "refs";

    /**
     * Index name.
     */
    public static final String IDX = "reverse";

    /**
     * Hash.
     */
    public static final String HASH = "left";

    /**
     * Range.
     */
    public static final String RANGE = "right";

    /**
     * Region.
     */
    private final transient Region region;

    /**
     * Public ctor.
     * @param reg Region
     */
    Refs(final Region reg) {
        this.region = reg;
    }

    /**
     * With prefix.
     * @param prefix Prefix
     * @return Condition
     */
    public static Condition withPrefix(final String prefix) {
        return new Condition()
            .withComparisonOperator(ComparisonOperator.BEGINS_WITH)
            .withAttributeValueList(new AttributeValue(prefix));
    }

    /**
     * Add new reference.
     * @param left Left
     * @param right Right
     * @throws IOException If fails
     */
    public void put(final String left, final String right) throws IOException {
        this.region.table(Refs.TABLE).put(
            new Attributes()
                .with(Refs.HASH, left)
                .with(Refs.RANGE, right)
        );
    }

    /**
     * Add new reference.
     * @param left Left
     * @param rights Rights
     * @throws IOException If fails
     */
    public void put(final String left, final Iterable<String> rights)
        throws IOException {
        for (final String right : rights) {
            this.put(left, right);
        }
    }

    /**
     * Forward search.
     * @param left Left
     * @param cnds Conditions
     * @return Rights
     */
    public Iterable<String> forward(final String left,
        final Iterable<Condition> cnds) {
        Frame frame = this.region.table(Refs.TABLE)
            .frame()
            .through(new QueryValve().withScanIndexForward(false))
            .where(Refs.HASH, left);
        for (final Condition cnd : cnds) {
            frame = frame.where(Refs.RANGE, cnd);
        }
        return Iterables.transform(
            frame,
            new Function<Item, String>() {
                @Override
                public String apply(final Item input) {
                    try {
                        return input.get(Refs.RANGE).getS();
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
    }

    /**
     * Forward search.
     * @param right Right
     * @param cnds Conditions
     * @return Lefts
     */
    public Iterable<String> reverse(final String right,
        final Iterable<Condition> cnds) {
        Frame frame = this.region.table(Refs.TABLE)
            .frame()
            .through(
                new QueryValve()
                    .withIndexName(Refs.IDX)
                    .withConsistentRead(false)
                    .withScanIndexForward(false)
            )
            .where(Refs.RANGE, right);
        for (final Condition cnd : cnds) {
            frame = frame.where(Refs.HASH, cnd);
        }
        return Iterables.transform(
            frame,
            new Function<Item, String>() {
                @Override
                public String apply(final Item input) {
                    try {
                        return input.get(Refs.HASH).getS();
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
    }

}

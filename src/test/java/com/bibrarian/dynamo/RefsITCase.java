/**
 * Copyright (c) 2013-2017, bibrarian.com
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
import com.jcabi.dynamo.Conditions;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration test for {@link Refs}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
public final class RefsITCase {

    /**
     * Dynamo rule.
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Rule
    public transient DynamoRule dynamo = new DynamoRule();

    /**
     * Refs can index and retrieve.
     * @throws Exception If some problem inside
     */
    @Test
    public void addsAndFetches() throws Exception {
        final Refs refs = new Refs(this.dynamo.region());
        final String name = "test10";
        final String alpha = "alpha:124";
        refs.put(name, alpha);
        refs.put(name, "alpha:899");
        refs.put(name, "beta:600");
        MatcherAssert.assertThat(
            refs.forward(
                name,
                Arrays.asList(
                    new Condition()
                        .withComparisonOperator(ComparisonOperator.BEGINS_WITH)
                        .withAttributeValueList(new AttributeValue("alpha:"))
                )
            ),
            Matchers.allOf(
                Matchers.<String>iterableWithSize(2),
                Matchers.hasItem(alpha)
            )
        );
    }

    /**
     * Refs can index and remove.
     * @throws Exception If some problem inside
     */
    @Test
    public void indexesAndRemoves() throws Exception {
        final Refs refs = new Refs(this.dynamo.region());
        final String left = "left:10";
        final String right = "right:33";
        refs.put(left, right);
        MatcherAssert.assertThat(
            refs.forward(
                left, Collections.singleton(Conditions.equalTo(right))
            ),
            Matchers.<String>iterableWithSize(1)
        );
        refs.remove(left, Collections.singleton(Conditions.equalTo(right)));
        MatcherAssert.assertThat(
            refs.forward(
                left, Collections.singleton(Conditions.equalTo(right))
            ),
            Matchers.<String>iterableWithSize(0)
        );
    }

}

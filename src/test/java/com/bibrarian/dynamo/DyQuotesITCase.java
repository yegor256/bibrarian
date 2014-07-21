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

import co.stateful.mock.MkSttc;
import com.bibrarian.om.Base;
import com.bibrarian.om.Book;
import com.bibrarian.om.Quote;
import com.bibrarian.om.Quotes;
import com.jcabi.dynamo.Credentials;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.retry.ReRegion;
import com.jcabi.manifests.Manifests;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration test for {@link DyQuotes}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
public final class DyQuotesITCase {

    /**
     * DyQuotes can list quotes.
     * @throws Exception If some problem inside
     */
    @Test
    public void addsAndListsQuotes() throws Exception {
        final Base base = new DyBase(
            this.dynamo(), new MkSttc().counters().get("cnt")
        );
        final String name = "test10";
        final Book book = base.books().add(name, "@book {}");
        final Quotes quotes = base.quotes();
        final Quote quote = quotes.add(book, "hey", "5-8");
        MatcherAssert.assertThat(
            quotes.iterate(),
            Matchers.hasItem(quote)
        );
        MatcherAssert.assertThat(
            quote.book().name(),
            Matchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            quote.book().bibitem(),
            Matchers.notNullValue()
        );
    }

    /**
     * DynamoDB region for tests.
     * @return Region
     */
    private Region dynamo() {
        final String key = Manifests.read("Bibrarian-DynamoKey");
        Assume.assumeTrue(key.startsWith("AAAA"));
        return new Region.Prefixed(
            new ReRegion(
                new Region.Simple(
                    new Credentials.Direct(
                        new Credentials.Simple(
                            key,
                            Manifests.read("Bibrarian-DynamoSecret")
                        ),
                        Integer.parseInt(System.getProperty("dynamo.port"))
                    )
                )
            ),
            "rt-"
        );
    }

}

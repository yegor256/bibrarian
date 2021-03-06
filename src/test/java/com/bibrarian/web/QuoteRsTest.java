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
package com.bibrarian.web;

import com.bibrarian.om.Base;
import com.bibrarian.om.Quote;
import com.bibrarian.om.Quotes;
import com.bibrarian.om.mock.MkQuote;
import com.jcabi.matchers.JaxbConverter;
import com.jcabi.matchers.XhtmlMatchers;
import com.rexsl.mock.MkServletContext;
import com.rexsl.page.mock.ResourceMocker;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link QuoteRs}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.11
 */
public final class QuoteRsTest {

    /**
     * QuoteRs can render an XML.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersXml() throws Exception {
        final Base base = Mockito.mock(Base.class);
        final Quotes quotes = Mockito.mock(Quotes.class);
        Mockito.doReturn(quotes).when(base).quotes();
        final Quote quote = new MkQuote();
        Mockito.doReturn(quote).when(quotes).get(1L);
        final QuoteRs home = new ResourceMocker().mock(QuoteRs.class);
        home.setServletContext(
            new MkServletContext().withAttr(Base.class.getName(), base)
        );
        home.setNumber(1L);
        MatcherAssert.assertThat(
            JaxbConverter.the(home.index().getEntity()),
            XhtmlMatchers.hasXPaths(
                String.format("/page/quote[pages='%s']", quote.pages()),
                String.format("/page/quote[text='%s']", quote.text()),
                "/page/quote/book/cite",
                "/page/quote/book/name",
                "/page/quote/book/links/link[@rel='open']",
                "/page/links/link[@rel='share-facebook']",
                "/page/links/link[@rel='share-twitter']",
                "/page/links/link[@rel='share-google']",
                "/page/links/link[@rel='share-linkedin']"
            )
        );
    }

}

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
package com.bibrarian.web;

import com.bibrarian.om.Base;
import com.bibrarian.om.Book;
import com.bibrarian.om.Quote;
import com.bibrarian.om.Quotes;
import com.google.common.base.Joiner;
import com.rexsl.mock.MkServletContext;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link BannerRs}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.7
 */
public final class BannerRsTest {

    /**
     * SvgRs can render an SVG.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersSvg() throws Exception {
        final Base base = Mockito.mock(Base.class);
        final Quotes quotes = Mockito.mock(Quotes.class);
        Mockito.doReturn(quotes).when(base).quotes();
        final Quote quote = Mockito.mock(Quote.class);
        Mockito.doReturn(quote).when(quotes).get(1L);
        final Book book = Mockito.mock(Book.class);
        Mockito.doReturn("Myers2012").when(book).name();
        Mockito.doReturn(book).when(quote).book();
        Mockito.doReturn(
            Joiner.on(' ').join(
                "Testing is a destructive, even sadistic,",
                " process, which explains why most people find it difficult"
            )
        ).when(quote).text();
        Mockito.doReturn("pp.3-5").when(quote).pages();
        Mockito.doReturn("@book{test2012,year=2012}").when(book).bibitem();
        final BannerRs home = new BannerRs();
        home.setServletContext(
            new MkServletContext().withAttr(Base.class.getName(), base)
        );
        home.setNumber(1L);
        final byte[] png = IOUtils.toByteArray(
            InputStream.class.cast(home.index().getEntity())
        );
        MatcherAssert.assertThat(png, Matchers.notNullValue());
    }

}

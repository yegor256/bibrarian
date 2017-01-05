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
package com.bibrarian.bib;

import com.google.common.base.Joiner;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Bibitem}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
public final class BibitemTest {

    /**
     * Bibitem can parse and print BibTeX.
     * @throws Exception If some problem inside
     */
    @Test
    public void parsesAndPrintsBibTeX() throws Exception {
        final String tex = Joiner.on(' ').join(
            "@article{test2014,\ntitle=\"How are you?\",\nyear=2014,\n",
            "publisher={Microsoft Publishing},isbn={9780735619654},\n",
            "url={http://books.google.nl/books?id=-eJQAAAAMAAJ}}"
        );
        MatcherAssert.assertThat(
            new Bibitem(new Bibitem(tex).tex()).tex(),
            Matchers.allOf(
                Matchers.containsString("@article{test2014,\n"),
                Matchers.containsString("title=\"How are you?\","),
                Matchers.containsString("year=\"2014\"")
            )
        );
    }

    /**
     * Bibitem can reject broken BibTeX.
     * @throws Exception If some problem inside
     */
    @Test(expected = BibSyntaxException.class)
    public void rejectsBrokenBibTeX() throws Exception {
        new Bibitem("broken text");
    }

    /**
     * Bibitem can render cite.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersBibTeXCite() throws Exception {
        final String tex = Joiner.on(' ').join(
            "@article{test2014,\ntitle=\"Good Article\",\nyear=2013,",
            "author={Jeff Lebowski}}"
        );
        MatcherAssert.assertThat(
            new Bibitem(tex).cite(),
            Matchers.allOf(
                Matchers.containsString("Jeff Lebowski, "),
                Matchers.containsString("\"Good Article\""),
                Matchers.containsString(", 2013")
            )
        );
    }

    /**
     * Bibitem can parse multiple authors.
     * @throws Exception If some problem inside
     */
    @Test
    public void parsesMultipleAuthors() throws Exception {
        MatcherAssert.assertThat(
            new Bibitem(
                Joiner.on(' ').join(
                    "@article{test2006,\ntitle=\"Good Article\",\nyear=2013,",
                    "author={Jeff Lebowski and Walter Sobchak}}"
                )
            ).author(),
            Matchers.equalTo("Jeff Lebowski et al.")
        );
        MatcherAssert.assertThat(
            new Bibitem(
                Joiner.on(' ').join(
                    "@article{test2002,\ntitle=\"Good Article\",\nyear=2013,",
                    "author={Walter Sobchak}}"
                )
            ).author(),
            Matchers.equalTo("Walter Sobchak")
        );
    }

    /**
     * Bibitem can parse empty entry.
     * @throws Exception If some problem inside
     */
    @Test
    public void parsesEmptyEntry() throws Exception {
        new Bibitem(new Bibitem("@book{dude2014}").tex());
    }

}

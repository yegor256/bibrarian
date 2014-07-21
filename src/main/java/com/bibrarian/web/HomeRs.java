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

import com.bibrarian.om.Quote;
import com.jcabi.aspects.Loggable;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Home.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @since 1.0
 */
@Path("/")
@Loggable(Loggable.DEBUG)
public final class HomeRs extends BaseRs {

    /**
     * Show it.
     * @return The JAX-RS response
     */
    @GET
    @Path("/")
    public Response index() {
        return new PageBuilder()
            .stylesheet("/xsl/home.xsl")
            .build(EmptyPage.class)
            .init(this)
            .link(
                new Link(
                    "add-book",
                    this.uriInfo().getBaseUriBuilder()
                        .clone()
                        .path(AddBookRs.class)
                        .build()
                )
            )
            .append(this.jaxb(this.base().quotes(null)))
            .render()
            .build();
    }

    /**
     * Convert quotes to a JAXB element.
     * @param quotes The list of them
     * @return JAXB object
     */
    private JaxbBundle jaxb(final Iterable<Quote> quotes) {
        return new JaxbBundle("quotes").add(
            new JaxbBundle.Group<Quote>(quotes) {
                @Override
                public JaxbBundle bundle(final Quote quote) {
                    return HomeRs.this.bundle(quote);
                }
            }
        );
    }

    /**
     * Convert discovery to a JAXB element.
     * @param quote The discovery
     * @return JAXB object
     */
    private JaxbBundle bundle(final Quote quote) {
        return new JaxbBundle("quote")
            .add("book", quote.book().bibitem())
            .attr("name", quote.book().label())
            .up()
            .add("text", quote.text())
            .up()
            .add("pages", quote.pages())
            .up();
    }

}

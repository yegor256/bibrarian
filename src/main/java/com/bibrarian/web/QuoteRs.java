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
import com.bibrarian.om.Quotes;
import com.bibrarian.om.Tag;
import com.jcabi.aspects.Loggable;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import java.io.IOException;
import java.util.logging.Level;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
@Path("/q/{number : \\d+}")
@Loggable(Loggable.DEBUG)
public final class QuoteRs extends BaseRs {

    /**
     * Quote number.
     */
    private transient long number;

    /**
     * Set quote number.
     * @param num Number
     */
    @PathParam("number")
    public void setNumber(final Long num) {
        this.number = num;
    }

    /**
     * Show it.
     * @return The JAX-RS response
     * @throws IOException If fails
     */
    @GET
    @Path("/")
    public Response index() throws IOException {
        return new PageBuilder()
            .stylesheet("/xsl/quote.xsl")
            .build(EmptyPage.class)
            .init(this)
            .append(new JxQuote(this.quote(), this))
            .link(new Link("add-tag", "./add-tag"))
            .render()
            .build();
    }

    /**
     * Add new tag.
     * @param tag Tag to add
     * @throws IOException If fails
     */
    @POST
    @Path("/add-tag")
    public void tag(@FormParam("tag") final String tag) throws IOException {
        try {
            this.quote().tag(new Tag.Simple(this.user().name(), tag));
        } catch (final Quote.IncorrectTagException ex) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUri(), ex
            );
        }
        throw this.flash().redirect(
            this.uriInfo().getBaseUriBuilder()
                .clone()
                .path(QuoteRs.class)
                .build(this.number),
            String.format("quote #%d tagged for \"%s\"", this.number, tag),
            Level.INFO
        );
    }

    /**
     * Get quote.
     * @return Quote
     * @throws IOException If fails
     */
    private Quote quote() throws IOException {
        try {
            return this.base().quotes().get(this.number);
        } catch (final Quotes.QuoteNotFoundException ex) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUri(), ex
            );
        }
    }

}

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

import com.jcabi.aspects.Loggable;
import com.rexsl.page.PageBuilder;
import java.util.logging.Level;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Add.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @since 1.0
 */
@Path("/add")
@Loggable(Loggable.DEBUG)
public final class AddRs extends BaseRs {

    /**
     * First form.
     * @return The JAX-RS response
     */
    @GET
    @Path("/first")
    public Response first() {
        return new PageBuilder()
            .stylesheet("/xsl/add-first.xsl")
            .build(EmptyPage.class)
            .init(this)
            .render()
            .build();
    }

    /**
     * Second form.
     * @param book Book name
     * @return The JAX-RS response
     */
    @GET
    @Path("/second")
    public Response second(@QueryParam("book") final String book) {
        return new PageBuilder()
            .stylesheet("/xsl/add-second.xsl")
            .build(EmptyPage.class)
            .init(this)
            .render()
            .build();
    }

    /**
     * Result.
     * @param book Book name
     * @param text Text of quote
     * @param pages Pages
     * @return The JAX-RS response
     */
    @POST
    @Path("/post")
    public Response third(@FormParam("book") final String book,
        @FormParam("text") final String text,
        @FormParam("pages") final String pages) {
        throw this.flash().redirect(
            this.uriInfo().getBaseUri(),
            String.format("quote added to book \"%s\"", book),
            Level.INFO
        );
    }

}

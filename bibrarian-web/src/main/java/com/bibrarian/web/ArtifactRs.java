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
package com.bibrarian.web;

import com.bibrarian.om.Artifact;
import com.bibrarian.om.Book;
import com.bibrarian.om.Discovery;
import com.bibrarian.om.Hypothesis;
import com.jcabi.aspects.Loggable;
import com.rexsl.page.PageBuilder;
import com.rexsl.page.inset.FlashInset;
import java.util.logging.Level;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Single artifact.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: IndexRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Path("/a")
@Loggable(Loggable.DEBUG)
public final class ArtifactRs extends BaseRs {

    /**
     * Artifact to work with.
     */
    private transient Artifact artifact;

    /**
     * Set artifact.
     * @param label The label of it
     */
    @QueryParam("label")
    public void setArtifact(@NotNull final String label) {
        this.artifact = this.bibrarian().artifacts()
            .fetch(new Book.Simple(label));
    }

    /**
     * Show full details.
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/")
    public Response index() throws Exception {
        return new PageBuilder()
            .stylesheet("/xsl/artifact.xsl")
            .build(EmptyPage.class)
            .init(this)
            .append(new JaxbArtifact(this.artifact))
            .render()
            .build();
    }

    /**
     * Add new discovery.
     * @param label Book label
     * @param quote The quote
     * @param pages The pages
     * @param relevance The relevance
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/add")
    public Response add(@QueryParam("label") @NotNull final String label,
        @QueryParam("quote") @NotNull final String quote,
        @QueryParam("pages") @NotNull final String pages,
        @QueryParam("relevance") @NotNull final String relevance)
        throws Exception {
        final Discovery discovery = new Discovery.Simple(
            new Hypothesis.Simple(label),
            quote,
            pages,
            Float.parseFloat(relevance)
        );
        if (!this.artifact.discoveries().add(discovery)) {
            throw FlashInset.forward(
                this.indexUri(),
                "discovery was NOT added",
                Level.WARNING
            );
        }
        throw FlashInset.forward(
            this.indexUri(),
            "discovery was added successfully",
            Level.INFO
        );
    }

}

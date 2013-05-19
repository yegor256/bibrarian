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
import com.jcabi.aspects.Loggable;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import com.rexsl.page.inset.FlashInset;
import java.net.URI;
import java.util.logging.Level;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Path("/ar")
@Loggable(Loggable.DEBUG)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class ArtifactRs extends BaseRs {

    /**
     * Query ID.
     */
    public static final String QUERY_LABEL = "label";

    /**
     * Artifact to work with.
     */
    private transient Artifact artifact;

    /**
     * Set artifact.
     * @param label The label of it
     */
    @QueryParam(ArtifactRs.QUERY_LABEL)
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
            .append(this.jaxb(this.artifact))
            .render()
            .build();
    }

    /**
     * Set new text of referat.
     * @param text Text of it
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @POST
    @Path("/referat")
    public Response referat(@FormParam("text") @NotNull final String text)
        throws Exception {
        this.artifact.referat(text);
        throw FlashInset.forward(
            this.indexUri(),
            "referat was changed successfully",
            Level.INFO
        );
    }

    /**
     * Convert artifact to a JAXB element.
     * @param artfct The artifact
     * @return JAXB object
     */
    private JaxbBundle jaxb(final Artifact artfct) {
        return new JaxbBundle("artifact")
            .add("book")
                .add("label", artfct.book().label())
                .up()
                .add("bibitem", artfct.book().bibitem().toString())
                .up()
            .up()
            .add("hardcopies").add(
                new JaxbBundle.Group<URI>(artfct.hardcopies()) {
                    @Override
                    public JaxbBundle bundle(final URI uri) {
                        return new JaxbBundle("hardcopy")
                            .add("uri", uri.toString()).up();
                    }
                }
            )
            .up()
            .add("discoveries").add(
                new JaxbBundle.Group<Discovery>(artfct.discoveries()) {
                    @Override
                    public JaxbBundle bundle(final Discovery discovery) {
                        return ArtifactRs.this.bundle(discovery);
                    }
                }
            )
            .add("referat", artfct.referat())
            .up()
            .link(
                new Link(
                    "referat",
                    this.uriInfo().getBaseUriBuilder().clone()
                        .path(ArtifactRs.class)
                        .path(ArtifactRs.class, "referat")
                        .queryParam(ArtifactRs.QUERY_LABEL, "{x1}")
                        .build(this.artifact.book().label())
                )
            )
            .link(
                new Link(
                    "add",
                    this.uriInfo().getBaseUriBuilder().clone()
                        .path(ArtifactRs.class)
                        .path(ArtifactRs.class, "add")
                        .queryParam(ArtifactRs.QUERY_LABEL, "{x2}")
                        .build(this.artifact.book().label())
                )
            );
    }

    /**
     * Convert to JAXB bundle.
     * @param discovery The discovery
     * @return Bundle
     */
    private JaxbBundle bundle(final Discovery discovery) {
        return new JaxbBundle("discovery")
            .add("label", discovery.hypothesis().label())
            .up()
            .add("pages", discovery.pages())
            .up()
            .add("quote", discovery.quote())
            .up()
            .add("relevance", Double.toString(discovery.relevance()))
            .up()
            .link(
                new Link(
                    "remove",
                    ArtifactRs.this.uriInfo()
                        .getBaseUriBuilder()
                        .clone()
                        .path(ArtifactRs.class)
                        .path(ArtifactRs.class, "remove")
                        .queryParam(ArtifactRs.QUERY_LABEL, "{x3}")
                        .build(discovery.hypothesis().label())
                )
            );
    }

}

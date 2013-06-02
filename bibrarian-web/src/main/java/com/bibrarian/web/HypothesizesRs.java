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

import com.bibrarian.om.Hypothesis;
import com.jcabi.aspects.Loggable;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import com.rexsl.page.inset.FlashInset;
import java.util.Collection;
import java.util.logging.Level;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * CRUD of Hypothesizes.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Path("/h")
@Loggable(Loggable.DEBUG)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class HypothesizesRs extends BaseRs {

    /**
     * Get them all.
     * @return The JAX-RS response
     */
    @GET
    @Path("/")
    public Response index() {
        return new PageBuilder()
            .stylesheet("/xsl/hypothesizes.xsl")
            .build(EmptyPage.class)
            .init(this)
            .append(this.jaxb(this.bibrarian().hypothesizes()))
            .link(new Link("add", "./add"))
            .render()
            .build();
    }

    /**
     * Add new hypothesis.
     * @param label The label to use
     * @param desc The description to use
     * @return The JAX-RS response
     */
    @GET
    @Path("/add")
    public Response add(@QueryParam("label") @NotNull final String label,
        @QueryParam("description") @NotNull final String desc) {
        final Hypothesis hypothesis = new Hypothesis.Simple(label, desc);
        if (!this.bibrarian().hypothesizes().add(hypothesis)) {
            throw FlashInset.forward(
                this.indexUri(),
                "hypothesis was NOT added",
                Level.WARNING
            );
        }
        throw FlashInset.forward(
            this.indexUri(),
            "hypothesis was added successfully",
            Level.INFO
        );
    }

    /**
     * Convert hypothesizes to a JAXB element.
     * @param hypothesizes The list of them
     * @return JAXB object
     */
    private JaxbBundle jaxb(final Collection<Hypothesis> hypothesizes) {
        return new JaxbBundle("hypothesizes").add(
            new JaxbBundle.Group<Hypothesis>(hypothesizes) {
                @Override
                public JaxbBundle bundle(final Hypothesis hypothesis) {
                    return HypothesizesRs.this.bundle(hypothesis);
                }
            }
        );
    }

    /**
     * Convert hypothesis to a JAXB element.
     * @param hypothesis The element
     * @return JAXB object
     */
    private JaxbBundle bundle(final Hypothesis hypothesis) {
        return new JaxbBundle("hypothesis")
            .add("label", hypothesis.label())
            .up()
            .add("description", hypothesis.description())
            .up()
            .link(
                new Link(
                    "see",
                    HypothesizesRs.this.uriInfo()
                        .getBaseUriBuilder()
                        .clone()
                        .path(HypothesisRs.class)
                        .path(HypothesisRs.class, "index")
                        .queryParam(HypothesisRs.QUERY_LABEL, "{x}")
                        .build(hypothesis.label())
                )
            );
    }

}

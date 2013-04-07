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
import java.util.Set;
import java.util.logging.Level;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.w3c.dom.Element;

/**
 * CRUD of Hypothesizes.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: IndexRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Path("/h")
@Loggable(Loggable.DEBUG)
public final class HypothesizesRs extends BaseRs {

    /**
     * Get them all.
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/")
    public Response index() throws Exception {
        return new PageBuilder()
            .stylesheet("/xsl/hypothesizes.xsl")
            .build(EmptyPage.class)
            .init(this)
            .append(this.jaxb(this.bibrarian().hypothesizes()))
            .append(new Link("add", "./add"))
            .render()
            .build();
    }

    /**
     * Remove by label.
     * @param label The label to use
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/remove")
    public Response remove(@QueryParam("label") @NotNull final String label)
        throws Exception {
        if (!this.bibrarian().hypothesizes().remove(label)) {
            throw FlashInset.forward(
                this.indexUri(),
                "hypothesis was NOT deleted",
                Level.WARNING
            );
        }
        throw FlashInset.forward(
            this.indexUri(),
            "hypothesis was deleted successfully",
            Level.INFO
        );
    }

    /**
     * Add new hypothesis.
     * @param label The label to use
     * @param description The description to use
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/add")
    public Response add(@QueryParam("label") @NotNull final String label,
        @QueryParam("description") @NotNull final String description)
        throws Exception {
        if (!this.bibrarian().hypothesizes()
            .add(new Hypothesis.Simple(label, description))) {
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
    private JaxbBundle jaxb(final Set<Hypothesis> hypothesizes) {
        return new JaxbBundle("hypothesizes").add(
            new JaxbBundle.Group<Hypothesis>(hypothesizes) {
                @Override
                public JaxbBundle bundle(final Hypothesis hypothesis) {
                    return new JaxbBundle("hypothesis")
                        .add("label", hypothesis.label())
                        .up()
                        .add("description", hypothesis.description())
                        .up()
                        .link(
                            new Link(
                                "remove",
                                HypothesizesRs.this.uriInfo()
                                    .getBaseUriBuilder()
                                    .clone()
                                    .path(HypothesizesRs.class)
                                    .path(HypothesizesRs.class, "remove")
                                    .queryParam("label", "{label}")
                                    .build(hypothesis.label())
                            )
                        );
                }
            }
        );
    }

}

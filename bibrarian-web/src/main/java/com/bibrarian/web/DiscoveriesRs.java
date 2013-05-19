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
import com.bibrarian.om.Discovery;
import com.jcabi.aspects.Loggable;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import com.rexsl.page.inset.FlashInset;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * List of all discoveries.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: DiscoveriesRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Path("/")
@Loggable(Loggable.DEBUG)
public final class DiscoveriesRs extends BaseRs {

    /**
     * List of them.
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/")
    public Response index() throws Exception {
        return new PageBuilder()
            .stylesheet("/xsl/discoveries.xsl")
            .build(EmptyPage.class)
            .init(this)
            .append(this.jaxb(this.bibrarian().discoveries()))
            .append(new Link("add", "./add"))
            .render()
            .build();
    }

    /**
     * Remove by book label and date.
     * @param label The book label to use
     * @param date When it happened
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/remove")
    public Response remove(@QueryParam("label") @NotNull final String label,
        @QueryParam("date") @NotNull final String date)
        throws Exception {
        final Artifact artifact = this.bibrarian().artifacts().fetch(
            this.bibrarians().books().fetch(label)
        );
        final Discovery discovery =
            new Discovery.Simple(new Date(Long.parseLong(date)));
        if (!artifact.discoveries().remove(discovery)) {
            throw FlashInset.forward(
                this.indexUri(),
                "discovery was NOT deleted",
                Level.WARNING
            );
        }
        throw FlashInset.forward(
            this.indexUri(),
            "discovery was deleted successfully",
            Level.INFO
        );
    }

    /**
     * Add new discovery.
     * @param label The label of the hypothesis
     * @param quote Quote
     * @param pages Pages
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/add")
    public Response add(@QueryParam("label") @NotNull final String label,
        @QueryParam("quote") @NotNull final String quote,
        @QueryParam("pages") @NotNull final String pages)
        throws Exception {
        final Discovery discovery = new Discovery.Simple(
            new Date(),
            this.bibrarian().hypothesizes().fetch(label),
            quote,
            pages,
            1d
        );
        if (!this.bibrarian().discoveries().add(discovery)) {
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

    /**
     * Convert discoveries to a JAXB element.
     * @param hypothesizes The list of them
     * @return JAXB object
     */
    private JaxbBundle jaxb(final Collection<Discovery> discoveries) {
        return new JaxbBundle("discoveries").add(
            new JaxbBundle.Group<Discovery>(discoveries) {
                @Override
                public JaxbBundle bundle(final Discovery discovery) {
                    return DiscoveriesRs.this.bundle(discovery);
                }
            }
        );
    }

    /**
     * Convert discovery to a JAXB element.
     * @param discovery The discovery
     * @return JAXB object
     */
    private JaxbBundle bundle(final Discovery discovery) {
        return new JaxbBundle("discovery")
            .add("hypothesis")
                .add("label", discovery.hypothesis().label())
                .up()
            .up()
            .add("quotes", discovery.quote())
            .up()
            .add("pages", discovery.pages())
            .up()
            .add("relevance", Double.toString(discovery.relevance()))
            .up()
            .link(
                new Link(
                    "remove",
                    DiscoveriesRs.this.uriInfo()
                        .getBaseUriBuilder()
                        .clone()
                        .path(DiscoveriesRs.class)
                        .path(DiscoveriesRs.class, "remove")
                        .queryParam("label", "{l}")
                        .queryParam("date", "{d}")
                        .build(
                            discovery.hypothesis().label(),
                            Long.toString(discovery.date().getTime())
                        )
                )
            );
    }

}

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
 * @version $Id$
 */
@Path("/")
@Loggable(Loggable.DEBUG)
public final class DiscoveriesRs extends BaseRs {

    /**
     * Query label.
     */
    private static final String QUERY_LABEL = "label";

    /**
     * Query item.
     */
    private static final String QUERY_ITEM = "item";

    /**
     * List of them.
     * @return The JAX-RS response
     */
    @GET
    @Path("/")
    public Response index() {
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
     * Remove by bibitem label and date.
     * @param label Label of hypothesis
     * @param item The bibitem label to use
     * @param date When it happened
     * @return The JAX-RS response
     */
    @GET
    @Path("/remove")
    public Response remove(@QueryParam(DiscoveriesRs.QUERY_LABEL)
        @NotNull final String label,
        @QueryParam(DiscoveriesRs.QUERY_ITEM) @NotNull final String item,
        @QueryParam("date") @NotNull final String date) {
        final Discovery discovery = new Discovery.Simple(
            new Date(Long.parseLong(date)),
            this.bibrarian().hypothesizes()
                .query().with("label", label).refine()
                .iterator().next(),
            this.bibrarian().artifacts()
                .query().with("bibitem", item).refine()
                .iterator().next(),
            "", "", 1d
        );
        if (!this.bibrarian().discoveries().remove(discovery)) {
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
     * @param item Item name/label
     * @param quote Quote
     * @param pages Pages
     * @return The JAX-RS response
     * @checkstyle ParameterNumber (8 lines)
     */
    @GET
    @Path("/add")
    public Response add(@QueryParam(DiscoveriesRs.QUERY_LABEL)
        @NotNull final String label,
        @QueryParam(DiscoveriesRs.QUERY_ITEM) @NotNull final String item,
        @QueryParam("quote") @NotNull final String quote,
        @QueryParam("pages") @NotNull final String pages) {
        final Discovery discovery = new Discovery.Simple(
            new Date(),
            this.bibrarian().hypothesizes()
                .query().with("label", label).refine()
                .iterator().next(),
            this.bibrarian().artifacts()
                .query().with("bibitem", item).refine()
                .iterator().next(),
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
     * @param discoveries The list of them
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
                        .queryParam(DiscoveriesRs.QUERY_LABEL, "{l}")
                        .queryParam("date", "{d}")
                        .build(
                            discovery.hypothesis().label(),
                            Long.toString(discovery.date().getTime())
                        )
                )
            );
    }

}

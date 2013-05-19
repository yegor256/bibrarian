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
import com.bibrarian.om.Hypothesis;
import com.bibrarian.om.Query;
import com.jcabi.aspects.Loggable;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * One Hypothesis.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: IndexRs.java 2344 2013-01-13 18:28:44Z guard $
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Path("/hp")
@Loggable(Loggable.DEBUG)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class HypothesisRs extends BaseRs {

    /**
     * Query ID.
     */
    public static final String QUERY_LABEL = "label";

    /**
     * The hypothesis under work.
     */
    private transient Hypothesis hypothesis;

    /**
     * Set it from query string.
     * @param label Name of it
     */
    @QueryParam(HypothesisRs.QUERY_LABEL)
    public void setHypothesis(final String label) {
        this.hypothesis = this.bibrarian().hypothesizes().fetch(label);
    }

    /**
     * Show it.
     * @return The JAX-RS response
     */
    @GET
    @Path("/")
    public Response index() {
        return new PageBuilder()
            .stylesheet("/xsl/hypothesis.xsl")
            .build(EmptyPage.class)
            .init(this)
            .append(
                new JaxbBundle("hypothesis")
                    .add("label", this.hypothesis.label())
                    .up()
                    .add("description", this.hypothesis.description())
                    .up()
            )
            .append(
                this.jaxb(
                    this.bibrarian().discoveries().query(
                        new Query.Simple(
                            String.format(
                                "hypothesis:%s",
                                this.hypothesis.label()
                            )
                        )
                    )
                )
            )
            .render()
            .build();
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
                    return HypothesisRs.this.bundle(discovery);
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
            .add("quotes", discovery.quote())
            .up()
            .add("pages", discovery.pages())
            .up()
            .add("relevance", Double.toString(discovery.relevance()))
            .up()
            .link(
                new Link(
                    "remove",
                    HypothesisRs.this.uriInfo()
                        .getBaseUriBuilder()
                        .clone()
                        .path(HypothesisRs.class)
                        .path(HypothesisRs.class, "remove")
                        .queryParam("date", "{d}")
                        .build(Long.toString(discovery.date().getTime()))
                )
            );
    }

}

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
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * CRUD of Artifacts.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: IndexRs.java 2344 2013-01-13 18:28:44Z guard $
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Path("/a")
@Loggable(Loggable.DEBUG)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class ArtifactsRs extends BaseRs {

    /**
     * Get them all.
     * @return The JAX-RS response
     */
    @GET
    @Path("/")
    public Response index() {
        return new PageBuilder()
            .stylesheet("/xsl/artifacts.xsl")
            .build(EmptyPage.class)
            .init(this)
            .append(this.jaxb(this.bibrarian().artifacts()))
            .render()
            .build();
    }

    /**
     * Convert hypothesizes to a JAXB element.
     * @param hypothesizes The list of them
     * @return JAXB object
     */
    private JaxbBundle jaxb(final Collection<Artifact> artifacts) {
        return new JaxbBundle("artifacts").add(
            new JaxbBundle.Group<Artifact>(artifacts) {
                @Override
                public JaxbBundle bundle(final Artifact artifact) {
                    return ArtifactsRs.this.bundle(artifact);
                }
            }
        );
    }

    /**
     * Convert artifact to a JAXB element.
     * @param artifact The artifact
     * @return JAXB object
     */
    private JaxbBundle bundle(final Artifact artifact) {
        return new JaxbBundle("artifact")
            .add("bibitem")
                .add("label", artifact.bibitem().load().label())
                .up()
                .add("bibitem", artifact.bibitem().toString())
                .up()
            .up()
            .add("referat", artifact.referat())
            .up()
            .add("discoveries").add(
                new JaxbBundle.Group<Discovery>(artifact.discoveries()) {
                    @Override
                    public JaxbBundle bundle(final Discovery discovery) {
                        return ArtifactsRs.this.bundle(discovery);
                    }
                }
            )
            .up()
            .link(
                new Link(
                    "see",
                    ArtifactsRs.this.uriInfo()
                        .getBaseUriBuilder()
                        .clone()
                        .path(ArtifactRs.class)
                        .path(ArtifactRs.class, "index")
                        .queryParam(ArtifactRs.QUERY_LABEL, "{x}")
                        .build(artifact.bibitem().load().label())
                )
            );
    }

    /**
     * Convert discovery to a JAXB bundle.
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
                    this.uriInfo().getBaseUriBuilder().clone()
                        .path(ArtifactRs.class)
                        .path(ArtifactRs.class, "remove")
                        .queryParam("label", "{label}")
                        .build(discovery.hypothesis().label())
                )
            );
    }

}

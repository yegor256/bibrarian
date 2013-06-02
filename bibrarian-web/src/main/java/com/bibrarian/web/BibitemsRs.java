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

import com.bibrarian.om.Bibitem;
import com.bibrarian.om.Bibtex;
import com.jcabi.aspects.Loggable;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import com.rexsl.page.inset.FlashInset;
import java.util.logging.Level;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * List of all bibitems.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@Path("/b")
@Loggable(Loggable.DEBUG)
public final class BibitemsRs extends BaseRs {

    /**
     * List of them.
     * @return The JAX-RS response
     */
    @GET
    @Path("/")
    public Response index() {
        return new PageBuilder()
            .stylesheet("/xsl/bibitems.xsl")
            .build(EmptyPage.class)
            .init(this)
            .append(this.jaxb(this.bibrarians().bibitems()))
            .link(new Link("add", "./add"))
            .render()
            .build();
    }

    /**
     * Add new bibitem.
     * @param tex Text in BibTeX format
     * @return The JAX-RS response
     */
    @POST
    @Path("/add")
    public Response add(@QueryParam("tex") @NotNull final String tex) {
        final Bibitem item = new Bibitem.Simple(new Bibtex(tex));
        if (!this.bibrarians().bibitems().add(item)) {
            throw FlashInset.forward(
                this.indexUri(),
                "bibitem was NOT added",
                Level.WARNING
            );
        }
        throw FlashInset.forward(
            this.indexUri(),
            "bibitem was added successfully",
            Level.INFO
        );
    }

    /**
     * Convert bibitems to a JAXB element.
     * @param bibitems List of them
     * @return JAXB object
     */
    private JaxbBundle jaxb(final Iterable<Bibitem> bibitems) {
        return new JaxbBundle("bibitems").add(
            new JaxbBundle.Group<Bibitem>(bibitems) {
                @Override
                public JaxbBundle bundle(final Bibitem bibitem) {
                    return BibitemsRs.this.bundle(bibitem);
                }
            }
        );
    }

    /**
     * Convert bibitem to a JAXB element.
     * @param bibitem The bibitem
     * @return JAXB object
     */
    private JaxbBundle bundle(final Bibitem bibitem) {
        return new JaxbBundle("discovery")
            .add("label", bibitem.load().label())
            .up()
            .add("tex", bibitem.toString())
            .up();
    }

}

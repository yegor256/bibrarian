/**
 * Copyright (c) 2013-2017, bibrarian.com
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

import com.bibrarian.om.Quote;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.Tv;
import java.io.IOException;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Sitemap.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @since 1.10
 */
@Path("/sitemap.xml")
@Loggable(Loggable.DEBUG)
public final class SitemapRs extends BaseRs {

    /**
     * Show it.
     * @return The JAX-RS response
     * @throws IOException If fails
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    public String xml() throws IOException {
        final StringBuilder doc = new StringBuilder(Tv.THOUSAND).append(
            "<urlset xmlns='http://www.sitemaps.org/schemas/sitemap/0.9'>"
        );
        for (final Quote quote : this.base().quotes().iterate()) {
            doc.append(this.toXML(quote));
        }
        return doc.append("</urlset>").toString();
    }

    /**
     * Convert quote into xml.
     * @param quote Quote
     * @return XML text
     * @throws IOException If fails
     */
    private String toXML(final Quote quote) throws IOException {
        return new StringBuilder(Tv.HUNDRED)
            .append("<url><loc>")
            .append(
                this.uriInfo().getBaseUriBuilder()
                    .clone()
                    .path(QuoteRs.class)
                    .build(quote.number())
            )
            .append("</loc><lastmod>")
            .append(
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT
                    .format(new Date())
            )
            .append("</lastmod></url>")
            .toString();
    }

}

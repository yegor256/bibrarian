/**
 * Copyright (c) 2013-2014, bibrarian.com
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

import co.stateful.RtSttc;
import co.stateful.Sttc;
import co.stateful.cached.CdSttc;
import co.stateful.retry.ReSttc;
import com.bibrarian.cached.CdBase;
import com.bibrarian.dynamo.DyBase;
import com.bibrarian.om.Base;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Credentials;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.retry.ReRegion;
import com.jcabi.log.Logger;
import com.jcabi.manifests.Manifests;
import com.jcabi.urn.URN;
import java.io.IOException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Lifespan controller.
 *
 * @author Yegor Bugayenko (yegor@woquo.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Loggable(Loggable.INFO)
public final class Lifespan implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        try {
            Manifests.append(event.getServletContext());
            event.getServletContext().setAttribute(
                Base.class.getName(),
                new CdBase(
                    new DyBase(
                        this.dynamo(),
                        this.sttc().counters().get("bib-quote")
                    )
                )
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        // nothing to do
    }

    /**
     * Dynamo DB region.
     * @return Region
     */
    private Region dynamo() {
        final String key = Manifests.read("Bib-DynamoKey");
        Credentials creds = new Credentials.Simple(
            key,
            Manifests.read("Bib-DynamoSecret")
        );
        if (key.startsWith("AAAAA")) {
            final int port = Integer.parseInt(
                System.getProperty("dynamo.port")
            );
            creds = new Credentials.Direct(creds, port);
            Logger.warn(this, "test DynamoDB at port #%d", port);
        }
        return new Region.Prefixed(
            new ReRegion(new Region.Simple(creds)), "bib-"
        );
    }

    /**
     * Sttc.
     * @return Sttc
     */
    @Cacheable(forever = true)
    private Sttc sttc() {
        Logger.warn(this, "Sttc connected");
        return new CdSttc(
            new ReSttc(
                RtSttc.make(
                    URN.create(Manifests.read("Bib-SttcUrn")),
                    Manifests.read("Bib-SttcToken")
                )
            )
        );
    }

}

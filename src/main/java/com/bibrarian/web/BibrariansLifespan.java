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

import com.bibrarian.dyn.DynBibrarians;
import com.bibrarian.om.Bibitem;
import com.bibrarian.om.Bibrarian;
import com.bibrarian.om.Bibrarians;
import com.bibrarian.om.Queryable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Credentials;
import com.jcabi.manifests.Manifests;
import com.jcabi.urn.URN;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Lifespan controller of the {@link Bibrarians}.
 *
 * @author Yegor Bugayenko (yegor@woquo.com)
 * @version $Id$
 */
@Loggable(Loggable.INFO)
public final class BibrariansLifespan implements ServletContextListener {

    /**
     * {@inheritDoc}
     *
     * <p>These attributes is used later in
     * {@link com.woquo.www.BaseRs#setServletContext(ServletContext)}.
     */
    @Override
    public void contextInitialized(final ServletContextEvent event) {
        try {
            Manifests.append(event.getServletContext());
        } catch (java.io.IOException ex) {
            throw new IllegalStateException(ex);
        }
        final String key = Manifests.read("Bibrarian-DynamoKey");
        final Bibrarians bibrarians;
        if (key.matches("[A-Z0-9]{20}")) {
            bibrarians = new DynBibrarians(
                new Credentials.Simple(
                    key,
                    Manifests.read("Bibrarian-DynamoSecret")
                ),
                Manifests.read("Bibrarian-DynamoPrefix")
            );
        } else {
            bibrarians = new Bibrarians() {
                @Override
                public Bibrarian fetch(final URN urn) {
                    throw new UnsupportedOperationException();
                }
                @Override
                public Queryable<Bibitem> bibitems() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        event.getServletContext().setAttribute(
            Bibrarians.class.getName(), bibrarians
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        // nothing to do
    }

}

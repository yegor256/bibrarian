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

import com.bibrarian.om.Base;
import com.bibrarian.om.User;
import com.jcabi.aspects.Loggable;
import com.jcabi.manifests.Manifests;
import com.jcabi.urn.URN;
import com.rexsl.page.BasePage;
import com.rexsl.page.BaseResource;
import com.rexsl.page.Inset;
import com.rexsl.page.Link;
import com.rexsl.page.Resource;
import com.rexsl.page.auth.AuthInset;
import com.rexsl.page.auth.Github;
import com.rexsl.page.auth.Identity;
import com.rexsl.page.auth.Provider;
import com.rexsl.page.inset.FlashInset;
import com.rexsl.page.inset.LinksInset;
import com.rexsl.page.inset.VersionInset;
import java.net.URI;
import java.util.logging.Level;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Abstract RESTful resource.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@Resource.Forwarded
@Inset.Default(LinksInset.class)
@Loggable(Loggable.DEBUG)
public class BaseRs extends BaseResource {

    /**
     * Inset with a version of the product.
     * @return The inset
     */
    @Inset.Runtime
    public final Inset insetVersion() {
        return new VersionInset(
            Manifests.read("Bibrarian-Version"),
            // @checkstyle MultipleStringLiterals (1 line)
            Manifests.read("Bibrarian-Revision"),
            Manifests.read("Bibrarian-Date")
        );
    }

    /**
     * Flash.
     * @return The inset with flash
     */
    @Inset.Runtime
    public final FlashInset flash() {
        return new FlashInset(this);
    }

    /**
     * Supplementary inset.
     * @return The inset
     */
    @Inset.Runtime
    public final Inset insetSupplementary() {
        return new Inset() {
            @Override
            public void render(final BasePage<?, ?> page,
                final Response.ResponseBuilder builder) {
                builder.type(MediaType.TEXT_XML);
                builder.header(HttpHeaders.VARY, "Cookie");
            }
        };
    }

    /**
     * Links if the user is logged in.
     * @return The inset
     */
    @Inset.Runtime
    public final Inset insetLinks() {
        // @checkstyle AnonInnerLengthCheck (50 lines)
        return new Inset() {
            @Override
            public void render(final BasePage<?, ?> page,
                final Response.ResponseBuilder builder) {
                if (!BaseRs.this.auth().identity().equals(Identity.ANONYMOUS)) {
                    page.link(
                        new Link(
                            "add",
                            BaseRs.this.uriInfo().getBaseUriBuilder()
                                .clone()
                                .path(AddBookRs.class)
                                .build()
                        )
                    );
                    page.link(
                        new Link(
                            "my-tags",
                            BaseRs.this.uriInfo().getBaseUriBuilder()
                                .clone()
                                .path(TagsRs.class)
                                .build()
                        )
                    );
                }
            }
        };
    }

    /**
     * Authentication inset.
     * @return The inset
     */
    @Inset.Runtime
    public final AuthInset auth() {
        // @checkstyle LineLength (3 lines)
        final AuthInset auth = new AuthInset(this, Manifests.read("Bibrarian-SecurityKey"))
            .with(new Github(this, Manifests.read("Bibrarian-GithubId"), Manifests.read("Bibrarian-GithubSecret")));
        if (Manifests.read("Bibrarian-DynamoKey").startsWith("AAAA")) {
            auth.with(
                new Provider.Always(
                    new Identity.Simple(
                        URN.create("urn:test:1"),
                        "localhost",
                        URI.create("http://img.bibrarian.com/localhost.jpg")
                    )
                )
            );
        }
        return auth;
    }

    /**
     * Find and return an authenticated bibrarian.
     * @return The bibrarian
     */
    protected final User user() {
        final Identity identity = this.auth().identity();
        if (identity.equals(Identity.ANONYMOUS)) {
            throw FlashInset.forward(
                this.uriInfo().getBaseUri(),
                "permission denied, authenticate yourself first",
                Level.WARNING
            );
        }
        return this.base().user(identity.name());
    }

    /**
     * Return base.
     * @return The base
     */
    protected final Base base() {
        final Base base = Base.class.cast(
            this.servletContext().getAttribute(Base.class.getName())
        );
        if (base == null) {
            throw new IllegalStateException("BASE is not initialized");
        }
        return base;
    }

}

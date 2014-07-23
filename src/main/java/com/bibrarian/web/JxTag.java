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

import com.bibrarian.om.Tag;
import com.rexsl.page.Link;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Jaxb Tag.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.NONE)
final class JxTag {

    /**
     * Tag.
     */
    private final transient Tag tag;

    /**
     * Quote we're in.
     */
    private final transient long quote;

    /**
     * BaseRs.
     */
    private final transient BaseRs base;

    /**
     * Ctor.
     */
    JxTag() {
        throw new UnsupportedOperationException("#JxTag()");
    }

    /**
     * Ctor.
     * @param tgg Tag
     * @param qte Quote
     * @param res BaseRs
     */
    JxTag(final Tag tgg, final long qte, final BaseRs res) {
        this.tag = tgg;
        this.quote = qte;
        this.base = res;
    }

    /**
     * Its owner.
     * @return Owner
     */
    @XmlElement(name = "user")
    public String getUser() {
        return this.tag.login();
    }

    /**
     * Its name.
     * @return Name
     */
    @XmlElement(name = "name")
    public String getName() {
        return this.tag.name();
    }

    /**
     * Its links.
     * @return Links
     */
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    public Collection<Link> getLinks() {
        final Collection<Link> links = new LinkedList<Link>();
        links.add(
            new Link(
                "open",
                this.base.uriInfo().getBaseUriBuilder().clone()
                    .path(HomeRs.class)
                    .queryParam("q", "{term}")
                    .build(new Tag.Simple(this.tag).ref())
            )
        );
        if (this.base.auth().identity().name().equals(this.tag.login())
            && this.quote > 0L) {
            links.add(
                new Link(
                    "delete",
                    this.base.uriInfo().getBaseUriBuilder().clone()
                        .path(QuoteRs.class)
                        .path(QuoteRs.class, "remove")
                        .queryParam("tag", "{t}")
                        .build(this.quote, new Tag.Simple(this.tag).toString())
                )
            );
        }
        return links;
    }

}

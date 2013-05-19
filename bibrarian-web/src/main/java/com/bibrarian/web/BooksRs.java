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
import com.bibrarian.om.Book;
import com.jcabi.aspects.Loggable;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import com.rexsl.page.inset.FlashInset;
import java.util.Collection;
import java.util.logging.Level;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * List of all books.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: DiscoveriesRs.java 2344 2013-01-13 18:28:44Z guard $
 */
@Path("/")
@Loggable(Loggable.DEBUG)
public final class BooksRs extends BaseRs {

    /**
     * List of them.
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/")
    public Response index() throws Exception {
        return new PageBuilder()
            .stylesheet("/xsl/books.xsl")
            .build(EmptyPage.class)
            .init(this)
            .append(this.jaxb(this.bibrarians().books()))
            .append(new Link("add", "./add"))
            .render()
            .build();
    }

    /**
     * Remove by label.
     * @param label The book label to use
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/remove")
    public Response remove(@QueryParam("label") @NotNull final String label)
        throws Exception {
        final Book book = new Book.Simple(label);
        if (!this.bibrarians().books().remove(book)) {
            throw FlashInset.forward(
                this.indexUri(),
                "book was NOT deleted",
                Level.WARNING
            );
        }
        throw FlashInset.forward(
            this.indexUri(),
            "book was deleted successfully",
            Level.INFO
        );
    }

    /**
     * Add new book.
     * @param label The label of the book
     * @param bibitem Bibitem
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/add")
    public Response add(@QueryParam("label") @NotNull final String label,
        @QueryParam("bibitem") @NotNull final String bibitem)
        throws Exception {
        final Book book = new Book.Simple(label, new Bibitem.Simple(bibitem));
        if (!this.bibrarians().books().add(book)) {
            throw FlashInset.forward(
                this.indexUri(),
                "book was NOT added",
                Level.WARNING
            );
        }
        throw FlashInset.forward(
            this.indexUri(),
            "book was added successfully",
            Level.INFO
        );
    }

    /**
     * Convert books to a JAXB element.
     * @param books List of them
     * @return JAXB object
     */
    private JaxbBundle jaxb(final Collection<Book> books) {
        return new JaxbBundle("books").add(
            new JaxbBundle.Group<Book>(books) {
                @Override
                public JaxbBundle bundle(final Book book) {
                    return BooksRs.this.bundle(book);
                }
            }
        );
    }

    /**
     * Convert book to a JAXB element.
     * @param book The book
     * @return JAXB object
     */
    private JaxbBundle bundle(final Book book) {
        return new JaxbBundle("discovery")
            .add("label", book.label())
            .up()
            .add("bibitem", book.bibitem().toString())
            .up()
            .link(
                new Link(
                    "remove",
                    BooksRs.this.uriInfo()
                        .getBaseUriBuilder()
                        .clone()
                        .path(BooksRs.class)
                        .path(BooksRs.class, "remove")
                        .queryParam("label", "{l}")
                        .build(book.label())
                )
            );
    }

}

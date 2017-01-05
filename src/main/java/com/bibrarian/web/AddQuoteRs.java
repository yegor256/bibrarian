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

import com.bibrarian.bib.Bibitem;
import com.bibrarian.om.Book;
import com.bibrarian.om.Books;
import com.bibrarian.om.Quote;
import com.bibrarian.om.Quotes;
import com.bibrarian.om.Tag;
import com.jcabi.aspects.Loggable;
import com.jcabi.manifests.Manifests;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Add quote.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @since 1.0
 */
@Path("/add-quote/{name}")
@Loggable(Loggable.DEBUG)
public final class AddQuoteRs extends BaseRs {

    /**
     * Twitter key.
     */
    private static final String KEY = Manifests.read("Bib-TwitterKey");

    /**
     * Book name.
     */
    private transient String name;

    /**
     * Set book name.
     * @param book Name of the book
     */
    @PathParam("name")
    public void setName(final String book) {
        this.name = book;
    }

    /**
     * Entry page.
     * @return The JAX-RS response
     * @throws IOException If fails
     */
    @GET
    @Path("/")
    public Response entry() throws IOException {
        return new PageBuilder()
            .stylesheet("/xsl/add-quote.xsl")
            .build(EmptyPage.class)
            .init(this)
            .link(new Link("save", "./save"))
            .append(new JxBook(this.book(), this))
            .render()
            .build();
    }

    /**
     * Save.
     * @param text Text of quote
     * @param pages Pages
     * @param tag Tag to add
     * @return The JAX-RS response
     * @throws IOException If fails
     */
    @POST
    @Path("/save")
    public Response save(@FormParam("text") final String text,
        @FormParam("pages") final String pages,
        @FormParam("tag") final String tag) throws IOException {
        final Quote quote;
        try {
            quote = this.base().quotes().add(this.book(), text, pages);
        } catch (final Quotes.InvalidQuoteException ex) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUriBuilder()
                    .clone().path(AddQuoteRs.class).build(this.name),
                ex
            );
        }
        try {
            quote.tag(new Tag.Simple(this.user().name(), tag));
        } catch (final Quote.IncorrectTagException ex) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUriBuilder()
                    .clone().path(AddQuoteRs.class).build(this.name),
                ex
            );
        }
        this.tweet(quote);
        throw this.flash().redirect(
            this.uriInfo().getBaseUriBuilder()
                .clone()
                .path(QuoteRs.class)
                .build(quote.number()),
            String.format(
                "quote #%d added to [%s]",
                quote.number(), quote.book().name()
            ),
            Level.INFO
        );
    }

    /**
     * Tweet about it.
     * @param quote Quote to tweet
     * @throws IOException If fails
     * @since 1.13
     */
    private void tweet(final Quote quote) throws IOException {
        if (!"test".equals(AddQuoteRs.KEY)) {
            final TwitterFactory factory = new TwitterFactory();
            final Twitter twitter = factory.getInstance();
            twitter.setOAuthConsumer(
                AddQuoteRs.KEY, Manifests.read("Bib-TwitterSecret")
            );
            twitter.setOAuthAccessToken(
                new AccessToken(
                    Manifests.read("Bib-TwitterToken"),
                    Manifests.read("Bib-TwitterTsecret")
                )
            );
            final StatusUpdate update = new StatusUpdate(
                String.format(
                    "#quote of %s %s",
                    new Bibitem(quote.book().bibitem()).author(),
                    this.uriInfo().getBaseUriBuilder().clone()
                        .path(QuoteRs.class)
                        .path(QuoteRs.class, "index")
                        .build(quote.number())
                )
            );
            update.media(
                "image",
                new ByteArrayInputStream(new Banner(quote).png())
            );
            try {
                twitter.updateStatus(update);
            } catch (final TwitterException ex) {
                throw new IOException(ex);
            }
        }
    }

    /**
     * Get book.
     * @return Book
     * @throws IOException If fails
     */
    private Book book() throws IOException {
        try {
            return this.base().books().get(this.name);
        } catch (final Books.BookNotFoundException ex) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUri(), ex
            );
        }
    }

}

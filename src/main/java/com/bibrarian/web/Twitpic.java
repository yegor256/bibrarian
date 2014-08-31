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

import com.bibrarian.om.Quote;
import com.jcabi.aspects.Cacheable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

/**
 * Twitpic.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @since 1.12
 */
final class Twitpic {

    /**
     * Quote.
     */
    private final transient Quote quote;

    /**
     * Ctor.
     * @param qte Quote
     */
    Twitpic(final Quote qte) {
        this.quote = qte;
    }

    /**
     * Upload to twitpic.
     * @return URI
     * @throws IOException If fails
     */
    @Cacheable(forever = true)
    public String upload() throws IOException {
        final Configuration conf = new ConfigurationBuilder()
            .setMediaProviderAPIKey("d73bec4b19a0394cc0c60751d1c72840")
            .setOAuthConsumerKey("XMVeJt6kLyil9XFAJVxirKfkG")
            .setOAuthConsumerSecret("QreJTRbYTcxZSWHIBICd2AxqA1bUetREhJfGJVn4wCEwmL6RaU")
            .setOAuthAccessToken("225097272-55Go7UfWX2MoTT9eariSfApnyrePyVVsxVjagsf6")
            .setOAuthAccessTokenSecret("On7uFSLlOcliUpVrVGXSY52sCSGSDXzdW1dpMEy3TpMrp")
            .build();
        final ImageUpload upload = new ImageUploadFactory(conf)
            .getInstance(MediaProvider.TWITPIC);
        try {
            return upload.upload(
                String.format("quote-%d.png", this.quote.number()),
                new ByteArrayInputStream(new Banner(this.quote).png())
            );
        } catch (final TwitterException ex) {
            throw new IOException(ex);
        }
    }

}

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

import com.bibrarian.bib.Bibitem;
import com.bibrarian.om.Quote;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.Tv;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.ws.rs.core.CacheControl;

/**
 * Banner.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.7
 */
final class Banner {

    /**
     * Width.
     */
    private static final int WIDTH = 1024;

    /**
     * Height.
     */
    private static final int HEIGHT = 512;

    /**
     * Padding.
     */
    private static final int PADDING = 35;

    /**
     * Font size.
     */
    private static final float MAIN_FONT = 80.0f;

    /**
     * Author font size.
     */
    private static final float AUTHOR_FONT = 35.0f;

    /**
     * Quote.
     */
    private final transient Quote quote;

    /**
     * Ctor.
     * @param qte Quote
     */
    Banner(final Quote qte) {
        this.quote = qte;
    }

    /**
     * Create PNG.
     * @return The PNG
     * @throws IOException If fails
     */
    public byte[] png() throws IOException {
        final BufferedImage img = new BufferedImage(
            Banner.WIDTH, Banner.HEIGHT, BufferedImage.TYPE_INT_RGB
        );
        final Graphics graph = Banner.graphics(img);
        final List<String> lines = this.compact(graph);
        for (int idx = 0; idx < lines.size(); ++idx) {
            graph.drawString(
                lines.get(idx), Banner.PADDING,
                Banner.PADDING + (idx + 1) * graph.getFontMetrics().getHeight()
            );
        }
        graph.setFont(Banner.font().deriveFont(Banner.AUTHOR_FONT));
        final String author = new Bibitem(this.quote.book().bibitem()).author();
        graph.drawString(
            author,
            Banner.WIDTH - Banner.PADDING
                - graph.getFontMetrics().stringWidth(author),
            Banner.HEIGHT - Banner.PADDING
        );
        return Banner.bytes(img);
    }

    /**
     * Get Graph from image.
     * @param img Image
     * @return Graph
     */
    private static Graphics2D graphics(final BufferedImage img) {
        final Graphics2D graph = Graphics2D.class.cast(img.getGraphics());
        graph.setFont(Banner.font());
        graph.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );
        graph.setColor(Color.WHITE);
        graph.fillRect(0, 0, img.getWidth(), img.getHeight());
        graph.setColor(Color.BLACK);
        return graph;
    }

    /**
     * Make bytes from Graph.
     * @param img Image
     * @return Bytes
     * @throws IOException If fails
     */
    private static byte[] bytes(final RenderedImage img) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        final CacheControl cache = new CacheControl();
        cache.setMaxAge((int) TimeUnit.HOURS.toSeconds(1L));
        cache.setPrivate(false);
        return baos.toByteArray();
    }

    /**
     * Compact lines.
     * @param graph Graph
     * @return Lines
     * @throws IOException If fails
     */
    private List<String> compact(final Graphics graph) throws IOException {
        List<String> lines;
        while (true) {
            lines = this.lines(graph);
            final int height = graph.getFontMetrics().getHeight();
            if (lines.size() * height < Banner.HEIGHT - (Banner.PADDING << 2)) {
                break;
            }
            final Font font = graph.getFont();
            // @checkstyle MagicNumber (1 line)
            graph.setFont(font.deriveFont(0.9f * (float) font.getSize()));
        }
        return lines;
    }

    /**
     * Make lines.
     * @param graph Graph
     * @return Lines
     * @throws IOException If fails
     */
    private List<String> lines(final Graphics graph) throws IOException {
        final FontMetrics metrics = graph.getFontMetrics();
        final List<String> lines = new LinkedList<String>();
        final StringBuilder line = new StringBuilder(Tv.THOUSAND);
        for (final String word : this.quote.text().split(" ")) {
            final String ext = String.format("%s %s", line, word);
            if (metrics.stringWidth(ext) > Banner.WIDTH - Banner.PADDING) {
                lines.add(line.toString().trim());
                line.setLength(0);
                line.append(word);
            } else {
                line.append(' ').append(word);
            }
        }
        lines.add(line.toString());
        return lines;
    }

    /**
     * Make font.
     * @return Font
     */
    @Cacheable(forever = true)
    private static Font font() {
        final Font cmu;
        try {
            cmu = Font.createFont(
                Font.TRUETYPE_FONT,
                new URL(
                    "http://img.bibrarian.com/fonts/cm/cmunbx.ttf"
                ).openStream()
            );
        } catch (final FontFormatException ex) {
            throw new IllegalStateException(ex);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return cmu.deriveFont(Banner.MAIN_FONT);
    }

}

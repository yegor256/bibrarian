<?xml version="1.0"?>
<!--
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
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" version="2.0">
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template match="page" mode="head">
        <title><xsl:value-of select="quote/book/author"/></title>
        <meta name="description" content="{quote/text}"/>
        <meta name="keywords" content="{quote/text}"/>
        <script type="text/javascript" src="//code.jquery.com/jquery-2.1.1-rc1.min.js">
            <xsl:text> </xsl:text>
        </script>
        <script type="text/javascript">
            //<![CDATA[
            $(
                function () {
                    $('.button').click(
                        function (event) {
                            event.preventDefault();
                            var $this = $(this);
                            window.open(
                                $this.attr('href'),
                                $this.attr('title'),
                                'width=640,height=300'
                            );
                        }
                    );
                }
            );
            //
            ]]>
        </script>
    </xsl:template>
    <xsl:template match="page" mode="body">
        <div class="social">
            <nav role="navigation">
                <a href="{links/link[@rel='share-facebook']/@href}"
                    title="" class="button">
                    <span class="ico">circlefacebook</span>
                </a>
                <a href="{links/link[@rel='share-twitter']/@href}"
                    title="Share on Twitter" class="button">
                    <span class="ico">circletwitterbird</span>
                </a>
                <a href="{links/link[@rel='share-google']/@href}"
                    title="Share on Google+" class="button">
                    <span class="ico">circlegoogleplus</span>
                </a>
                <a href="{links/link[@rel='share-linkedin']/@href}"
                    title="Share on LinkedIn" class="button">
                    <span class="ico">circlelinkedin</span>
                </a>
            </nav>
        </div>
        <xsl:apply-templates select="quote"/>
    </xsl:template>
    <xsl:template match="quote">
        <div class="quote" itemscope="" itemtype="http://schema.org/CreativeWork">
            <div class="text" itemprop="text">
                <xsl:value-of select="text"/>
            </div>
        </div>
        <xsl:if test="links/link[@rel='edit']">
            <p>
                <a href="{links/link[@rel='edit']/@href}" class="opt">
                    <xsl:text>edit</xsl:text>
                </a>
                <xsl:text> | </xsl:text>
                <a href="{links/link[@rel='delete']/@href}" class="opt">
                    <xsl:text>delete</xsl:text>
                </a>
            </p>
        </xsl:if>
        <p itemprop="author">
            <span class="abbr">
                <a href="{book/links/link[@rel='open']/@href}">
                    <xsl:value-of select="book/name"/>
                </a>
            </span>
            <xsl:value-of select="book/cite"/>
            <xsl:text>, </xsl:text>
            <xsl:value-of select="pages"/>
        </p>
        <xsl:if test="links/link[@rel='add-tag']">
            <form id="add-tag" method="post" action="{links/link[@rel='add-tag']/@href}">
                <fieldset class="inline">
                    <input name="tag" id="tag" size="35" maxlength="100" autocomplete="off"
                        placeholder="e.g. priceless-quotes"/>
                    <button type="submit">
                        <xsl:text>Add tag</xsl:text>
                    </button>
                </fieldset>
            </form>
        </xsl:if>
        <xsl:if test="tags[tag]">
            <ul>
                <xsl:apply-templates select="tags/tag"/>
            </ul>
        </xsl:if>
    </xsl:template>
    <xsl:template match="tags/tag">
        <li id="{user}_{name}">
            <a href="{links/link[@rel='open']/@href}">
                <xsl:value-of select="user"/>
                <xsl:text>/</xsl:text>
                <xsl:value-of select="name"/>
            </a>
            <xsl:if test="links/link[@rel='delete']">
                <a class="opt"
                    onclick="return confirm('Are you sure you want to delete this tag?');"
                    href="{links/link[@rel='delete']/@href}">
                    <xsl:text>remove</xsl:text>
                </a>
            </xsl:if>
        </li>
    </xsl:template>
</xsl:stylesheet>

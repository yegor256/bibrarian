<?xml version="1.0"?>
<!--
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
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" version="2.0">
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template match="page" mode="head">
        <title><xsl:text>bibrarian</xsl:text></title>
        <meta name="description" content="Scientific Quotes Organized Right"/>
        <meta name="keywords" content="quotes"/>
    </xsl:template>
    <xsl:template match="page" mode="body">
        <xsl:if test="not(quotes/quote)">
            <p>
                <xsl:text>Nothing found, try to refine your search criteria.</xsl:text>
            </p>
        </xsl:if>
        <xsl:apply-templates select="book"/>
        <xsl:apply-templates select="quotes/quote"/>
    </xsl:template>
    <xsl:template match="quote">
        <div class="quote" itemscope="" itemtype="http://schema.org/CreativeWork">
            <div class="text" itemprop="text">
                <a title="see its full page"
                    href="{links/link[@rel='open']/@href}">
                    <xsl:value-of select="text"/>
                </a>
            </div>
            <xsl:apply-templates select="." mode="book"/>
            <xsl:if test="tags[tag]">
                <ul class="tags">
                    <xsl:apply-templates select="tags/tag"/>
                </ul>
            </xsl:if>
        </div>
    </xsl:template>
    <xsl:template match="quote" mode="book">
        <div class="book" itemprop="author">
            <span class="abbr">
                <a title="find all quotes of this book"
                    href="{book/links/link[@rel='open']/@href}">
                    <xsl:value-of select="book/name"/>
                </a>
            </span>
            <xsl:value-of select="book/cite"/>
            <xsl:text>, </xsl:text>
            <xsl:value-of select="pages"/>
            <xsl:if test="book/links/link[@rel='add-quote']">
                <a class="opt" title="add new quote to this book"
                    href="{book/links/link[@rel='add-quote']/@href}">
                    <xsl:text>+quote</xsl:text>
                </a>
            </xsl:if>
        </div>
    </xsl:template>
    <xsl:template match="tags/tag">
        <li>
            <a title="find all quotes in this tag"
                href="{links/link[@rel='open']/@href}">
                <xsl:value-of select="user"/>
                <xsl:text>/</xsl:text>
                <xsl:value-of select="name"/>
            </a>
            <xsl:if test="links/link[@rel='delete']">
                <a title="remove this tag"
                    onclick="return confirm('Are you sure you want to delete this tag?');"
                    class="opt" href="{links/link[@rel='delete']/@href}">
                    <xsl:text>remove</xsl:text>
                </a>
            </xsl:if>
        </li>
    </xsl:template>
    <xsl:template match="book">
        <p class="book-header">
            <xsl:value-of select="cite"/>
            <xsl:if test="links/link[@rel='add-quote']">
                <a class="opt" title="add new quote to this book"
                    href="{links/link[@rel='add-quote']/@href}">
                    <xsl:text>+quote</xsl:text>
                </a>
            </xsl:if>
            <xsl:if test="links/link[@rel='edit']">
                <a class="opt" title="edit BibTeX entry of the book"
                    href="{links/link[@rel='edit']/@href}">
                    <xsl:text>edit</xsl:text>
                </a>
            </xsl:if>
        </p>
    </xsl:template>
</xsl:stylesheet>

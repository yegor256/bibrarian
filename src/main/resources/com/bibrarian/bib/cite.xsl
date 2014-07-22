<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="text"/>
    <xsl:template match="/bib">
        <xsl:value-of select="author"/>
        <xsl:text>, &quot;</xsl:text>
        <xsl:value-of select="title"/>
        <xsl:text>&quot;, </xsl:text>
        <xsl:value-of select="year"/>
    </xsl:template>
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>

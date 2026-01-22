/*
 * Copyright (C) 2011 RenÃ© Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zetra.econsig.helper.markdown;

/**
 * Default Decorator implementation.
 *
 * <p>
 * Example for a user Decorator having a class attribute on &lt;p> tags.
 * </p>
 *
 * <pre>
 * <code>public class MyDecorator extends DefaultDecorator
 * {
 *     &#64;Override
 *     public void openParagraph(StringBuilder out)
 *     {
 *         out.append("&lt;p class=\"myclass\">");
 *     }
 * }
 * </code>
 * </pre>
 *
 * @author RenÃ© Jeschke <rene_jeschke@yahoo.de>
 */
public class DefaultDecoratorExtended implements DecoratorExtended
{
    /** Constructor. */
    public DefaultDecoratorExtended()
    {
        // empty
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openParagraph(StringBuilder) */
    @Override
    public void openParagraph(StringBuilder out)
    {
        out.append("<p>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeParagraph(StringBuilder) */
    @Override
    public void closeParagraph(StringBuilder out)
    {
        out.append("</p>\n");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openBlockquote(StringBuilder) */
    @Override
    public void openBlockquote(StringBuilder out)
    {
        out.append("<blockquote>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeBlockquote(StringBuilder) */
    @Override
    public void closeBlockquote(StringBuilder out)
    {
        out.append("</blockquote>\n");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openCodeBlock(StringBuilder) */
    @Override
    public void openCodeBlock(StringBuilder out)
    {
        out.append("<pre><code>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeCodeBlock(StringBuilder) */
    @Override
    public void closeCodeBlock(StringBuilder out)
    {
        out.append("</code></pre>\n");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openCodeSpan(StringBuilder) */
    @Override
    public void openCodeSpan(StringBuilder out)
    {
        out.append("<code>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeCodeSpan(StringBuilder) */
    @Override
    public void closeCodeSpan(StringBuilder out)
    {
        out.append("</code>");
    }

    /**
     * @see com.github.rjeschke.txtmark.Decorator#openHeadline(StringBuilder,
     *      int)
     */
    @Override
    public void openHeadline(StringBuilder out, int level)
    {
        out.append("<h");
        out.append(level);
        out.append(" style=\"font-weight:normal;margin-bottom:0px;margin-top:0px\"");
    }

    /**
     * @see com.github.rjeschke.txtmark.Decorator#closeHeadline(StringBuilder,
     *      int)
     */
    @Override
    public void closeHeadline(StringBuilder out, int level)
    {
        out.append("</h");
        out.append(level);
        out.append(">\n");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openStrong(StringBuilder) */
    @Override
    public void openStrong(StringBuilder out)
    {
        out.append("<strong>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeStrong(StringBuilder) */
    @Override
    public void closeStrong(StringBuilder out)
    {
        out.append("</strong>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openStrong(StringBuilder) */
    @Override
    public void openStrike(StringBuilder out)
    {
        out.append("<s>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeStrong(StringBuilder) */
    @Override
    public void closeStrike(StringBuilder out)
    {
        out.append("</s>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openEmphasis(StringBuilder) */
    @Override
    public void openEmphasis(StringBuilder out)
    {
        out.append("<em>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeEmphasis(StringBuilder) */
    @Override
    public void closeEmphasis(StringBuilder out)
    {
        out.append("</em>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openSuper(StringBuilder) */
    @Override
    public void openSuper(StringBuilder out)
    {
        out.append("<sup>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeSuper(StringBuilder) */
    @Override
    public void closeSuper(StringBuilder out)
    {
        out.append("</sup>");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openOrderedList(StringBuilder) */
    @Override
    public void openOrderedList(StringBuilder out)
    {
        out.append("<ol>\n");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeOrderedList(StringBuilder) */
    @Override
    public void closeOrderedList(StringBuilder out)
    {
        out.append("</ol>\n");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openUnorderedList(StringBuilder) */
    @Override
    public void openUnorderedList(StringBuilder out)
    {
        out.append("<ul>\n");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeUnorderedList(StringBuilder) */
    @Override
    public void closeUnorderedList(StringBuilder out)
    {
        out.append("</ul>\n");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openListItem(StringBuilder) */
    @Override
    public void openListItem(StringBuilder out)
    {
        out.append("<li");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#closeListItem(StringBuilder) */
    @Override
    public void closeListItem(StringBuilder out)
    {
        out.append("</li>\n");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#horizontalRuler(StringBuilder) */
    @Override
    public void horizontalRuler(StringBuilder out)
    {
        out.append("<hr />\n");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openLink(StringBuilder) */
    @Override
    public void openLink(StringBuilder out)
    {
        out.append("<a");
    }

    /** @see com.github.rjeschke.txtmark.Decorator#openImage(StringBuilder) */
    @Override
    public void openImage(StringBuilder out)
    {
        out.append("<img");
    }

    @Override
    public void openUnderline(StringBuilder out) {
        out.append("<u>");
    }

    @Override
    public void closeUnderline(StringBuilder out) {
        out.append("</u>");

    }

    @Override
    public void openColorRed(StringBuilder out) {
        out.append("<font color=\"#ff0000\">");

    }

    @Override
    public void closeColorRed(StringBuilder out) {
        out.append("</font>");

    }

    @Override
    public void openColorYellow(StringBuilder out) {
        out.append("<font color=\"#cccc00\">");

    }

    @Override
    public void closeColorYellow(StringBuilder out) {
        out.append("</font>");

    }

    @Override
    public void openColorGreen(StringBuilder out) {
        out.append("<font color=\"#008000\">");

    }

    @Override
    public void closeColorGreen(StringBuilder out) {
        out.append("</font>");

    }

    @Override
    public void openColorBlue(StringBuilder out) {
        out.append("<font color=\"#0000ff\">");

    }

    @Override
    public void closeColorBlue(StringBuilder out) {
        out.append("</font>");

    }

    @Override
    public void openAlignLeft(StringBuilder out) {
        out.append("<div align=\"left\">");

    }

    @Override
    public void closeAlignLeft(StringBuilder out) {
        out.append("</div>");

    }

    @Override
    public void openAlignCenter(StringBuilder out) {
        out.append("<div align=\"center\">");

    }

    @Override
    public void closeAlignCenter(StringBuilder out) {
        out.append("</div>");

    }

    @Override
    public void openAlignRight(StringBuilder out) {
        out.append("<div align=\"right\">");

    }

    @Override
    public void closeAlignRight(StringBuilder out) {
        out.append("</div>");

    }

    @Override
    public void openEstiloTitulo(StringBuilder out) {
        out.append("<font style=\"font-size:14px\" ><b>");
    }

    @Override
    public void closeEstiloTitulo(StringBuilder out) {
        out.append("</b></font>");
    }

    @Override
    public void openEstiloSubtitulo(StringBuilder out) {
        out.append("<font style=\"font-size:12px\"><b>");
    }

    @Override
    public void closeEstiloSubtitulo(StringBuilder out) {
        out.append("</b></font>");
    }

    @Override
    public void openEstiloTexto(StringBuilder out) {
        out.append("<font style=\"font-size:11px\">");
    }

    @Override
    public void closeEstiloTexto(StringBuilder out) {
        out.append("</font>");
    }

}

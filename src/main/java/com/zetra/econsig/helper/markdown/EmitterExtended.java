package com.zetra.econsig.helper.markdown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.markdown4j.Plugin;

import com.zetra.econsig.helper.texto.TextHelper;

/**
 * Emitter para funções novas
 */
public class EmitterExtended {
    /** Link references. */
    private final HashMap<String, LinkRef> linkRefs = new HashMap<String, LinkRef>();

    /** The configuration. */
    private final Configuration config;

    /** Extension flag. */
    public boolean useExtensions = false;

    /** Newline flag. */
    public boolean convertNewline2Br = false;

    /** Plugins references **/
    private final Map<String, Plugin> plugins = new HashMap<String, Plugin>();

    /** Constructor. */
    public EmitterExtended(final Configuration config) {
        this.config = config;
        useExtensions = config.forceExtendedProfile;
        convertNewline2Br = config.convertNewline2Br;
        for (Plugin plugin : config.plugins) {
            register(plugin);
        }
    }

    public void register(Plugin plugin) {
        plugins.put(plugin.getIdPlugin(), plugin);
    }

    /**
     * Adds a LinkRef to this set of LinkRefs.
     *
     * @param key
     *            The key/id.
     * @param linkRef
     *            The LinkRef.
     */
    public void addLinkRef(final String key, final LinkRef linkRef) {
        linkRefs.put(key.toLowerCase(), linkRef);
    }

    /**
     * Transforms the given block recursively into HTML.
     *
     * @param out
     *            The StringBuilder to write to.
     * @param root
     *            The Block to process.
     */
    public void emit(final StringBuilder out, final Block root) {
        root.removeSurroundingEmptyLines();

        switch (root.type) {
            case RULER:
                config.decorator.horizontalRuler(out);
                return;
            case NONE:
            case XML:
                break;
            case HEADLINE:
                config.decorator.openHeadline(out, root.hlDepth);
                if (useExtensions && root.id != null) {
                    out.append(" id=\"");
                    Utils.appendCode(out, root.id, 0, root.id.length());
                    out.append('"');
                }
                out.append('>');
                break;
            case PARAGRAPH:
                config.decorator.openParagraph(out);
                break;
            case CODE:
            case FENCED_CODE:
                if (config.codeBlockEmitter == null) {
                    config.decorator.openCodeBlock(out);
                }
                break;
            case BLOCKQUOTE:
                config.decorator.openBlockquote(out);
                break;
            case UNORDERED_LIST:
                config.decorator.openUnorderedList(out);
                break;
            case ORDERED_LIST:
                config.decorator.openOrderedList(out);
                break;
            case LIST_ITEM:
                config.decorator.openListItem(out);
                if (useExtensions && root.id != null) {
                    out.append(" id=\"");
                    Utils.appendCode(out, root.id, 0, root.id.length());
                    out.append('"');
                }
                out.append('>');
                break;
            default:
                break;
        }

        if (root.hasLines()) {
            emitLines(out, root);
        } else {
            Block block = root.blocks;
            while (block != null) {
                emit(out, block);
                block = block.next;
            }
        }

        switch (root.type) {
            case RULER:
            case NONE:
            case XML:
                break;
            case HEADLINE:
                config.decorator.closeHeadline(out, root.hlDepth);
                break;
            case PARAGRAPH:
                config.decorator.closeParagraph(out);
                break;
            case CODE:
            case FENCED_CODE:
                if (config.codeBlockEmitter == null) {
                    config.decorator.closeCodeBlock(out);
                }
                break;
            case BLOCKQUOTE:
                config.decorator.closeBlockquote(out);
                break;
            case UNORDERED_LIST:
                config.decorator.closeUnorderedList(out);
                break;
            case ORDERED_LIST:
                config.decorator.closeOrderedList(out);
                break;
            case LIST_ITEM:
                config.decorator.closeListItem(out);
                break;
            default:
                break;
        }
    }

    /**
     * Transforms lines into HTML.
     *
     * @param out
     *            The StringBuilder to write to.
     * @param block
     *            The Block to process.
     */
    private void emitLines(final StringBuilder out, final Block block) {
        switch (block.type) {
            case CODE:
                emitCodeLines(out, block.lines, block.meta, true);
                break;
            case FENCED_CODE:
                emitCodeLines(out, block.lines, block.meta, false);
                break;
            case PLUGIN:
                emitPluginLines(out, block.lines, block.meta);
                break;
            case XML:
                emitRawLines(out, block.lines);
                break;
            case PARAGRAPH:
                emitMarkedLines(out, block.lines);
                break;
            default:
                emitMarkedLines(out, block.lines);
                break;
        }
    }

    /**
     * Checks if there is a valid markdown link definition.
     *
     * @param out
     *            The StringBuilder containing the generated output.
     * @param in
     *            Input String.
     * @param start
     *            Starting position.
     * @param token
     *            Either LINK or IMAGE.
     * @return The new position or -1 if there is no valid markdown link.
     */
    private int checkLink(final StringBuilder out, final String in, int start, MarkTokenExtended token) {
        int pos = start + (token == MarkTokenExtended.LINK ? 1 : 2);
        final StringBuilder temp = new StringBuilder();

        temp.setLength(0);
        pos = Utils.readMdLinkId(temp, in, pos);
        if (pos < start) {
            return -1;
        }

        String name = temp.toString(), link = null, comment = null;
        final int oldPos = pos++;
        pos = Utils.skipSpaces(in, pos);
        if (pos < start) {
            final LinkRef lr = linkRefs.get(name.toLowerCase());
            if (lr != null) {
                link = lr.link;
                comment = lr.title;
                pos = oldPos;
            } else {
                return -1;
            }
        } else if (in.charAt(pos) == '(') {
            pos++;
            pos = Utils.skipSpaces(in, pos);
            if (pos < start) {
                return -1;
            }
            temp.setLength(0);
            boolean useLt = in.charAt(pos) == '<';
            pos = useLt ? Utils.readUntil(temp, in, pos + 1, '>') : Utils.readMdLink(temp, in, pos);
            if (pos < start) {
                return -1;
            }
            if (useLt) {
                pos++;
            }
            link = temp.toString();

            if (in.charAt(pos) == ' ') {
                pos = Utils.skipSpaces(in, pos);
                if (pos > start && in.charAt(pos) == '"') {
                    pos++;
                    temp.setLength(0);
                    pos = Utils.readUntil(temp, in, pos, '"');
                    if (pos < start) {
                        return -1;
                    }
                    comment = temp.toString();
                    pos++;
                    pos = Utils.skipSpaces(in, pos);
                    if (pos == -1) {
                        return -1;
                    }
                }
            }
            if (in.charAt(pos) != ')') {
                return -1;
            }
        } else if (in.charAt(pos) == '[') {
            pos++;
            temp.setLength(0);
            pos = Utils.readRawUntil(temp, in, pos, ']');
            if (pos < start) {
                return -1;
            }
            final String id = temp.length() > 0 ? temp.toString() : name;
            final LinkRef lr = linkRefs.get(id.toLowerCase());
            if (lr != null) {
                link = lr.link;
                comment = lr.title;
            }
        } else {
            final LinkRef lr = linkRefs.get(name.toLowerCase());
            if (lr != null) {
                link = lr.link;
                comment = lr.title;
                pos = oldPos;
            } else {
                return -1;
            }
        }

        if (link == null) {
            return -1;
        }

        if (token == MarkTokenExtended.LINK) { /* DESENV-4226
                                               if(isAbbrev && comment != null)
                                               {
                                                   if(!useExtensions) {
                                                       return -1;
                                                   }
                                                   out.append("<abbr title=\"");
                                                   Utils.appendValue(out, comment, 0, comment.length());
                                                   out.append("\">");
                                                   recursiveEmitLine(out, name, 0, MarkTokenExtended.NONE);
                                                   out.append("</abbr>");
                                               }
                                               else
                                               {
                                                   config.decorator.openLink(out);
                                                   out.append(" href=\"");
                                                   Utils.appendValue(out, link, 0, link.length());
                                                   out.append('"');
                                                   if(comment != null)
                                                   {
                                                       out.append(" title=\"");
                                                       Utils.appendValue(out, comment, 0, comment.length());
                                                       out.append('"');
                                                   }
                                                   out.append('>');
                                                   recursiveEmitLine(out, name, 0, MarkTokenExtended.NONE);
                                                   out.append("</a>");
                                               } */
        } else {
            config.decorator.openImage(out);
            out.append(" src=\"");
            Utils.appendValue(out, TextHelper.forHtmlAttribute(link), 0, link.length());
            out.append("\" alt=\"");
            Utils.appendValue(out, TextHelper.forHtmlAttribute(name), 0, name.length());
            out.append('"');
            if (comment != null) {
                out.append(" title=\"");
                Utils.appendValue(out, TextHelper.forHtmlAttribute(comment), 0, comment.length());
                out.append('"');
            }
            out.append(" />");
        }

        return pos;
    }

    /**
     * Recursively scans through the given line, taking care of any markdown
     * stuff.
     *
     * @param out
     *            The StringBuilder to write to.
     * @param in
     *            Input String.
     * @param start
     *            Start position.
     * @param token
     *            The matching Token (for e.g. '*')
     * @return The position of the matching Token or -1 if token was NONE or no
     *         Token could be found.
     */
    private int recursiveEmitLine(final StringBuilder out, final String in, int start, MarkTokenExtended token) {
        int pos = start, b;
        final StringBuilder temp = new StringBuilder();
        while (pos < in.length()) {
            final MarkTokenExtended mt = getToken(in, pos);
            if (token != MarkTokenExtended.NONE && (mt == token || token == MarkTokenExtended.EM_STAR && mt == MarkTokenExtended.STRONG_STAR || token == MarkTokenExtended.EM_UNDERSCORE && mt == MarkTokenExtended.STRONG_UNDERSCORE)) {
                return pos;
            }

            switch (mt) {
                case IMAGE:
                    /* DESENV-4226
                                case LINK:
                                */
                    temp.setLength(0);
                    b = checkLink(temp, in, pos, mt);
                    if (b > 0) {
                        out.append(temp);
                        pos = b;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                case EM_STAR:
                case EM_UNDERSCORE:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 1, mt);
                    if (b > 0) {
                        config.decorator.openEmphasis(out);
                        out.append(temp);
                        config.decorator.closeEmphasis(out);
                        pos = b;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                case STRONG_STAR:
                case STRONG_UNDERSCORE:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 2, mt);
                    if (b > 0) {
                        config.decorator.openStrong(out);
                        out.append(temp);
                        config.decorator.closeStrong(out);
                        pos = b + 1;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                /* DESENV-4226
                            case STRIKE:
                temp.setLength(0);
                b = recursiveEmitLine(temp, in, pos + 2, mt);
                if(b > 0)
                {
                    config.decorator.openStrike(out);
                    out.append(temp);
                    config.decorator.closeStrike(out);
                    pos = b + 1;
                }
                else
                {
                    out.append(in.charAt(pos));
                }
                break; */
                /* DESENV-4226
                            case SUPER:
                temp.setLength(0);
                b = recursiveEmitLine(temp, in, pos + 1, mt);
                if(b > 0)
                {
                    config.decorator.openSuper(out);
                    out.append(temp);
                    config.decorator.closeSuper(out);
                    pos = b;
                }
                else
                {
                    out.append(in.charAt(pos));
                }
                break; */
                /* DESENV-4226
                            case CODE_SINGLE:
                            case CODE_DOUBLE:
                a = pos + (mt == MarkTokenExtended.CODE_DOUBLE ? 2 : 1);
                b = findToken(in, a, mt);
                if(b > 0)
                {
                    pos = b + (mt == MarkTokenExtended.CODE_DOUBLE ? 1 : 0);
                    while(a < b && in.charAt(a) == ' ') {
                        a++;
                    }
                    if(a < b)
                    {
                        while(in.charAt(b - 1) == ' ') {
                            b--;
                        }
                        config.decorator.openCodeSpan(out);
                        Utils.appendCode(out, in, a, b);
                        config.decorator.closeCodeSpan(out);
                    }
                }
                else
                {
                    out.append(in.charAt(pos));
                }
                break; */
                /* DESENV-4226
                            case HTML:
                temp.setLength(0);
                b = checkHtml(temp, in, pos);
                if(b > 0)
                {
                    out.append(temp);
                    pos = b;
                }
                else
                {
                    out.append("&lt;");
                }
                break; */
                /* DESENV-4226
                            case ENTITY:
                temp.setLength(0);
                b = checkEntity(temp, in, pos);
                if(b > 0)
                {
                    out.append(temp);
                    pos = b;
                }
                else
                {
                    out.append("&amp;");
                }
                break; */
                /* DESENV-4226
                           case X_LINK_OPEN:
                temp.setLength(0);
                b = recursiveEmitLine(temp, in, pos + 2, MarkTokenExtended.X_LINK_CLOSE);
                if(b > 0 && config.specialLinkEmitter != null)
                {
                    config.specialLinkEmitter.emitSpan(out, temp.toString());
                    pos = b + 1;
                }
                else
                {
                    out.append(in.charAt(pos));
                }
                break; */
                /* DESENV-4226
                            case X_COPY:
                out.append("&copy;");
                pos += 2;
                break;
                            case X_REG:
                out.append("&reg;");
                pos += 2;
                break;
                            case X_TRADE:
                out.append("&trade;");
                pos += 3;
                break;
                            case X_NDASH:
                out.append("&ndash;");
                pos++;
                break;
                            case X_MDASH:
                out.append("&mdash;");
                pos += 2;
                break;
                            case X_HELLIP:
                out.append("&hellip;");
                pos += 2;
                break;
                            case X_LAQUO:
                out.append("&laquo;");
                pos++;
                break;
                            case X_RAQUO:
                out.append("&raquo;");
                pos++;
                break;
                            case X_RDQUO:
                out.append("&rdquo;");
                break;
                            case X_LDQUO:
                out.append("&ldquo;");
                break; */
                case UNDERLINE:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 1, mt);
                    if (b > 0) {
                        config.decorator.openUnderline(out);
                        out.append(temp);
                        config.decorator.closeUnderline(out);
                        pos = b;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                case COLOR_RED:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 1, mt);
                    if (b > 0) {
                        config.decorator.openColorRed(out);
                        out.append(temp);
                        config.decorator.closeColorRed(out);
                        pos = b;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                case COLOR_YELLOW:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 2, mt);
                    if (b > 0) {
                        config.decorator.openColorYellow(out);
                        out.append(temp);
                        config.decorator.closeColorYellow(out);
                        pos = b + 1;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                case COLOR_GREEN:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 3, mt);
                    if (b > 0) {
                        config.decorator.openColorGreen(out);
                        out.append(temp);
                        config.decorator.closeColorGreen(out);
                        pos = b + 2;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                case COLOR_BLUE:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 4, mt);
                    if (b > 0) {
                        config.decorator.openColorBlue(out);
                        out.append(temp);
                        config.decorator.closeColorBlue(out);
                        pos = b + 3;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                case ESTILO_TITULO:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 2, mt);
                    if (b > 0) {
                        config.decorator.openEstiloTitulo(out);
                        out.append(temp);
                        config.decorator.closeEstiloTitulo(out);
                        pos = b + 1;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                case ESTILO_SUBTITULO:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 2, mt);
                    if (b > 0) {
                        config.decorator.openEstiloSubtitulo(out);
                        out.append(temp);
                        config.decorator.closeEstiloSubtitulo(out);
                        pos = b + 1;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                case ESTILO_TEXTO:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 2, mt);
                    if (b > 0) {
                        config.decorator.openEstiloTexto(out);
                        out.append(temp);
                        config.decorator.closeEstiloTexto(out);
                        pos = b + 1;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;

                case ALIGN_LEFT:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 2, mt);
                    if (b > 0) {
                        config.decorator.openAlignLeft(out);
                        out.append(temp);
                        config.decorator.closeAlignLeft(out);
                        pos = b + 2;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;

                //                temp.setLength(0);
                //                b = this.checkLink(temp, in, pos, mt);
                //                if(b > 0)
                //                {
                //                    out.append(temp);
                //                    pos = b;
                //                }
                //                else
                //                {
                //                    out.append(in.charAt(pos));
                //                }
                //                break;
                case ALIGN_CENTER:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 2, mt);
                    if (b > 0) {
                        config.decorator.openAlignCenter(out);
                        out.append(temp);
                        config.decorator.closeAlignCenter(out);
                        pos = b + 2;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                case ALIGN_RIGHT:
                    temp.setLength(0);
                    b = recursiveEmitLine(temp, in, pos + 2, mt);
                    if (b > 0) {
                        config.decorator.openAlignRight(out);
                        out.append(temp);
                        config.decorator.closeAlignRight(out);
                        pos = b + 2;
                    } else {
                        out.append(in.charAt(pos));
                    }
                    break;
                /* DESENV-4226
                            case ESCAPE:
                pos++;
                //$FALL-THROUGH$ */
                default:
                    out.append(in.charAt(pos));
                    break;
            }
            pos++;
        }
        return -1;
    }

    /**
     * Turns every whitespace character into a space character.
     *
     * @param c
     *            Character to check
     * @return 32 is c was a whitespace, c otherwise
     */
    private static char whitespaceToSpace(char c) {
        return Character.isWhitespace(c) ? ' ' : c;
    }

    /**
     * Check if there is any markdown Token.
     *
     * @param in
     *            Input String.
     * @param pos
     *            Starting position.
     * @return The Token.
     */
    private MarkTokenExtended getToken(final String in, final int pos) {
        final char c0 = pos > 0 ? whitespaceToSpace(in.charAt(pos - 1)) : ' ';
        final char c = whitespaceToSpace(in.charAt(pos));
        final char c1 = pos + 1 < in.length() ? whitespaceToSpace(in.charAt(pos + 1)) : ' ';
        final char c2 = pos + 2 < in.length() ? whitespaceToSpace(in.charAt(pos + 2)) : ' ';
        final char c3 = pos + 3 < in.length() ? whitespaceToSpace(in.charAt(pos + 3)) : ' ';
        final char c4 = pos + 4 < in.length() ? whitespaceToSpace(in.charAt(pos + 4)) : ' ';

        switch (c) {
            case '|':
                return MarkTokenExtended.UNDERLINE;
            case '+':
                if (c3 == '+' && c2 == '+' && c1 == '+' && (c0 != ' ' || c4 != ' ')) {
                    return MarkTokenExtended.COLOR_BLUE;
                } else if (c2 == '+' && c1 == '+' && (c0 != ' ' || c3 != ' ')) {
                    return MarkTokenExtended.COLOR_GREEN;
                } else if (c1 == '+' && (c0 != ' ' || c2 != ' ')) {
                    return MarkTokenExtended.COLOR_YELLOW;
                }
                return c0 != ' ' || c1 != ' ' ? MarkTokenExtended.COLOR_RED : MarkTokenExtended.NONE;

            case '$':
                if (c1 == '!' && (c0 != ' ' || c2 != ' ')) {
                    return MarkTokenExtended.ESTILO_TITULO;
                } else if (c1 == '@' && (c0 != ' ' || c2 != ' ')) {
                    return MarkTokenExtended.ESTILO_SUBTITULO;
                } else if (c1 == '%' && (c0 != ' ' || c2 != ' ')) {
                    return MarkTokenExtended.ESTILO_TEXTO;
                }
                return MarkTokenExtended.NONE;

            case '*':
                if (c1 == '*') {
                    return c0 != ' ' || c2 != ' ' ? MarkTokenExtended.STRONG_STAR : MarkTokenExtended.EM_STAR;
                }
                return c0 != ' ' || c1 != ' ' ? MarkTokenExtended.EM_STAR : MarkTokenExtended.NONE;
            case '(':
                if (c1 == '(') {
                    return MarkTokenExtended.ALIGN_LEFT;
                }
                return MarkTokenExtended.NONE;
            case ')':
                if (c1 == '(') {
                    return MarkTokenExtended.ALIGN_CENTER;
                } else if (c1 == ')') {
                    return MarkTokenExtended.ALIGN_RIGHT;
                }
                return MarkTokenExtended.NONE;
            case '_':
                if (c1 == '_') {
                    return c0 != ' ' || c2 != ' ' ? MarkTokenExtended.STRONG_UNDERSCORE : MarkTokenExtended.EM_UNDERSCORE;
                }
                if (useExtensions) {
                    return Character.isLetterOrDigit(c0) && c0 != '_' && Character.isLetterOrDigit(c1) ? MarkTokenExtended.NONE : MarkTokenExtended.EM_UNDERSCORE;
                }
                return c0 != ' ' || c1 != ' ' ? MarkTokenExtended.EM_UNDERSCORE : MarkTokenExtended.NONE;
            /* DESENV-4226
                    case '~':
            if(useExtensions && c1 == '~')
            {
                return MarkTokenExtended.STRIKE;
            }
            return MarkTokenExtended.NONE; */
            case '!':
                if (c1 == '[') {
                    return MarkTokenExtended.IMAGE;
                }
                return MarkTokenExtended.NONE;
            /* DESENV-4226
                    case '[':
            if(useExtensions && c1 == '[') {
                return MarkTokenExtended.X_LINK_OPEN;
            }
            return MarkTokenExtended.LINK;
                    case ']':
            if(useExtensions && c1 == ']') {
                return MarkTokenExtended.X_LINK_CLOSE;
            }
            return MarkTokenExtended.NONE;
                    case '`':
            return c1 == '`' ? MarkTokenExtended.CODE_DOUBLE : MarkTokenExtended.CODE_SINGLE;
                    case '\\':
            switch(c1)
            {
            case '\\':
            case '[':
            case ']':
            case '{':
            case '}':
            case '#':
            case '"':
            case '\'':
            case '.':
            case '>':
            case '<':
            case '*':
            case '+':
            case '-':
            case '_':
            case '!':
            case '`':
            case '^':
                return MarkTokenExtended.ESCAPE;
            default:
                return MarkTokenExtended.NONE;
            }
                    case '<':
            if(useExtensions && c1 == '<') {
                return MarkTokenExtended.X_LAQUO;
            }
            return MarkTokenExtended.HTML;
                    case '&':
            return MarkTokenExtended.ENTITY; */
            default:
                /* DESENV-4226
                            if(useExtensions)
                            {
                switch(c)
                {
                case '-':
                    if(c1 == '-') {
                        return c2 == '-' ? MarkTokenExtended.X_MDASH : MarkTokenExtended.X_NDASH;
                    }
                    break;
                case '^':
                    return c0 == '^' || c1 == '^' ? MarkTokenExtended.NONE : MarkTokenExtended.SUPER;
                case '>':
                    if(c1 == '>') {
                        return MarkTokenExtended.X_RAQUO;
                    }
                    break;
                case '.':
                    if(c1 == '.' && c2 == '.') {
                        return MarkTokenExtended.X_HELLIP;
                    }
                    break;
                case '(':
                    if(c1 == 'C' && c2 == ')') {
                        return MarkTokenExtended.X_COPY;
                    }
                    if(c1 == 'R' && c2 == ')') {
                        return MarkTokenExtended.X_REG;
                    }
                    if(c1 == 'T' & c2 == 'M' & c3 == ')') {
                        return MarkTokenExtended.X_TRADE;
                    }
                    break;
                case '"':
                    if(!Character.isLetterOrDigit(c0) && c1 != ' ') {
                        return MarkTokenExtended.X_LDQUO;
                    }
                    if(c0 != ' ' && !Character.isLetterOrDigit(c1)) {
                        return MarkTokenExtended.X_RDQUO;
                    }
                    break;
                }
                            } */
                return MarkTokenExtended.NONE;
        }
    }

    /**
     * Writes a set of markdown lines into the StringBuilder.
     *
     * @param out
     *            The StringBuilder to write to.
     * @param lines
     *            The lines to write.
     */
    private void emitMarkedLines(final StringBuilder out, final Line lines) {
        final StringBuilder in = new StringBuilder();
        Line line = lines;
        while (line != null) {
            if (!line.isEmpty) {
                in.append(line.value.substring(line.leading, line.value.length() - line.trailing));
                if (line.trailing >= 2 && !convertNewline2Br) {
                    in.append("<br />");
                }
            }
            if (line.next != null) {
                in.append('\n');
                if (convertNewline2Br) {
                    in.append("<br />");
                }
            }
            line = line.next;
        }

        recursiveEmitLine(out, in.toString(), 0, MarkTokenExtended.NONE);
    }

    /**
     * Writes a set of raw lines into the StringBuilder.
     *
     * @param out
     *            The StringBuilder to write to.
     * @param lines
     *            The lines to write.
     */
    private void emitRawLines(final StringBuilder out, final Line lines) {
        Line line = lines;
        if (config.safeMode) {
            final StringBuilder temp = new StringBuilder();
            while (line != null) {
                if (!line.isEmpty) {
                    temp.append(line.value);
                }
                temp.append('\n');
                line = line.next;
            }
            final String in = temp.toString();
            for (int pos = 0; pos < in.length(); pos++) {
                if (in.charAt(pos) == '<') {
                    temp.setLength(0);
                    final int t = Utils.readXML(temp, in, pos, config.safeMode);
                    if (t != -1) {
                        out.append(temp);
                        pos = t;
                    } else {
                        out.append(in.charAt(pos));
                    }
                } else {
                    out.append(in.charAt(pos));
                }
            }
        } else {
            while (line != null) {
                if (!line.isEmpty) {
                    out.append(line.value);
                }
                out.append('\n');
                line = line.next;
            }
        }
    }

    /**
     * Writes a code block into the StringBuilder.
     *
     * @param out
     *            The StringBuilder to write to.
     * @param lines
     *            The lines to write.
     * @param meta
     *            Meta information.
     */
    private void emitCodeLines(final StringBuilder out, final Line lines, final String meta, final boolean removeIndent) {
        Line line = lines;
        if (config.codeBlockEmitter != null) {
            final ArrayList<String> list = new ArrayList<String>();
            while (line != null) {
                if (line.isEmpty) {
                    list.add("");
                } else {
                    list.add(removeIndent ? line.value.substring(4) : line.value);
                }
                line = line.next;
            }
            config.codeBlockEmitter.emitBlock(out, list, meta);
        } else {
            while (line != null) {
                if (!line.isEmpty) {
                    for (int i = 4; i < line.value.length(); i++) {
                        final char c;
                        switch (c = line.value.charAt(i)) {
                            case '&':
                                out.append("&amp;");
                                break;
                            case '<':
                                out.append("&lt;");
                                break;
                            case '>':
                                out.append("&gt;");
                                break;
                            default:
                                out.append(c);
                                break;
                        }
                    }
                }
                out.append('\n');
                line = line.next;
            }
        }
    }

    /**
     * interprets a plugin block into the StringBuilder.
     *
     * @param out
     *            The StringBuilder to write to.
     * @param lines
     *            The lines to write.
     * @param meta
     *            Meta information.
     */
    protected void emitPluginLines(final StringBuilder out, final Line lines, final String meta) {
        Line line = lines;

        String idPlugin = meta;
        String sparams = null;
        Map<String, String> params = null;
        int iow = meta.indexOf(' ');
        if (iow != -1) {
            idPlugin = meta.substring(0, iow);
            sparams = meta.substring(iow + 1);
            if (sparams != null) {
                params = parsePluginParams(sparams);
            }
        }

        if (params == null) {
            params = new HashMap<String, String>();
        }
        final ArrayList<String> list = new ArrayList<String>();
        while (line != null) {
            if (line.isEmpty) {
                list.add("");
            } else {
                list.add(line.value);
            }
            line = line.next;
        }

        Plugin plugin = plugins.get(idPlugin);
        if (plugin != null) {
            plugin.emit(out, list, params);
        }
    }

    protected Map<String, String> parsePluginParams(String s) {
        Map<String, String> params = new HashMap<String, String>();
        Pattern p = Pattern.compile("(\\w+)=\"*((?<=\")[^\"]+(?=\")|([^\\s]+))\"*");

        Matcher m = p.matcher(s);

        while (m.find()) {
            params.put(m.group(1), m.group(2));
        }

        return params;
    }

}

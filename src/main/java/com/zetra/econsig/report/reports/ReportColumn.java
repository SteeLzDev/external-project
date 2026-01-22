package com.zetra.econsig.report.reports;

import java.awt.Color;
import java.io.Serializable;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;

/**
 * <p>Title: ReportColumn</p>
 * <p>Description: Informações necessárias para construção de colunas num JasperReport.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReportColumn implements Serializable {
    private String bandName;
    private Class<?> fieldClass;
    private String fieldName;
    private JRStyle style;
    private String pattern;
    private HorizontalTextAlignEnum horizontalAlignment;
    private VerticalTextAlignEnum verticalAlignment;
    private JRLineBox box;
    private int x;
    private int y;
    private int width;
    private int heigth;
    private Color backcolor;

    private String title;
    private String titleElementKey;
    private String titleBandName;
    private Color titleBackcolor;
    private HorizontalTextAlignEnum titleHorizontalAlignment;
    private VerticalTextAlignEnum titleVerticalAlignment;
    private JRLineBox titleBox;
    private int titleX;
    private int titleY;
    private int titleWidth;
    private int titleHeigth;

    public String getBandName() {
        return bandName;
    }
    public void setBandName(String bandName) {
        this.bandName = bandName;
    }
    public JRLineBox getBox() {
        return box;
    }
    public void setBox(JRLineBox box) {
        this.box = box;
    }
    public Class<?> getFieldClass() {
        return fieldClass;
    }
    public void setFieldClass(Class<?> fieldClass) {
        this.fieldClass = fieldClass;
    }
    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    public int getHeigth() {
        return heigth;
    }
    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }
    public Color getBackcolor() {
        return backcolor;
    }
    public void setBackcolor(Color backcolor) {
        this.backcolor = backcolor;
    }
    public HorizontalTextAlignEnum getHorizontalAlignment() {
        return horizontalAlignment;
    }
    public void setHorizontalAlignment(HorizontalTextAlignEnum right) {
        horizontalAlignment = right;
    }
    public String getPattern() {
        return pattern;
    }
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    public JRStyle getStyle() {
        return style;
    }
    public void setStyle(JRStyle style) {
        this.style = style;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitleElementKey() {
        return titleElementKey;
    }
    public void setTitleElementKey(String titleElementKey) {
        this.titleElementKey = titleElementKey;
    }
    public Color getTitleBackcolor() {
        return titleBackcolor;
    }
    public void setTitleBackcolor(Color titleBackcolor) {
        this.titleBackcolor = titleBackcolor;
    }
    public String getTitleBandName() {
        return titleBandName;
    }
    public void setTitleBandName(String titleBandName) {
        this.titleBandName = titleBandName;
    }
    public JRLineBox getTitleBox() {
        return titleBox;
    }
    public void setTitleBox(JRLineBox titleBox) {
        this.titleBox = titleBox;
    }
    public int getTitleHeigth() {
        return titleHeigth;
    }
    public void setTitleHeigth(int titleHeigth) {
        this.titleHeigth = titleHeigth;
    }
    public HorizontalTextAlignEnum getTitleHorizontalAlignment() {
        return titleHorizontalAlignment;
    }
    public void setTitleHorizontalAlignment(HorizontalTextAlignEnum center) {
        titleHorizontalAlignment = center;
    }
    public VerticalTextAlignEnum getTitleVerticalAlignment() {
        return titleVerticalAlignment;
    }
    public void setTitleVerticalAlignment(VerticalTextAlignEnum middle) {
        titleVerticalAlignment = middle;
    }
    public int getTitleWidth() {
        return titleWidth;
    }
    public void setTitleWidth(int titleWidth) {
        this.titleWidth = titleWidth;
    }
    public int getTitleX() {
        return titleX;
    }
    public void setTitleX(int titleX) {
        this.titleX = titleX;
    }
    public int getTitleY() {
        return titleY;
    }
    public void setTitleY(int titleY) {
        this.titleY = titleY;
    }
    public VerticalTextAlignEnum getVerticalAlignment() {
        return verticalAlignment;
    }
    public void setVerticalAlignment(VerticalTextAlignEnum middle) {
        verticalAlignment = middle;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
}

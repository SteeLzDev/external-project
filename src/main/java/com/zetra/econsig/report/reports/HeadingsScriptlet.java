package com.zetra.econsig.report.reports;

/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * JasperReports - Free Java report-generating library.
 * Copyright (C) 2001-2006 JasperSoft Corporation http://www.jaspersoft.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * JasperSoft Corporation
 * 303 Second Street, Suite 450 North
 * San Francisco, CA 94107
 * http://www.jaspersoft.com
 */

import java.io.Serializable;
import java.util.Collection;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

/**
 * <p>Title: HeadingBean</p>
 * <p>Description: Mapeamento do sumário de um relatório.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HeadingsScriptlet extends JRDefaultScriptlet implements Serializable {

    private static Integer totalPaginas = Integer.valueOf(2);
    /**
     *
     */
    public Boolean addHeading(String groupName) throws JRScriptletException {
        Collection<HeadingBean> headings;
        try {
            headings = (Collection<HeadingBean>) getVariableValue("HeadingsCollection");
        } catch (JRScriptletException e) {
            headings = (Collection<HeadingBean>) this.getParameterValue("HeadingsCollection");
        }

        Integer type = null;
        String text = null;
        String reference = null;
        Integer pageIndex = (Integer) getVariableValue("PAGE_NUMBER");
        Integer pageCount = (Integer) getVariableValue("PAGE_COUNT");

        pageIndex += totalPaginas;
        totalPaginas += pageCount;

        if ("FirstLetterGroup".equals(groupName)) {
            type = Integer.valueOf(1);
            text = "Letter " + " Objetivo";
            reference = "FirstLetterGroup_" + "Objetivo"; //this.getVariableValue("FirstLetter");
        } else if ("ShipCountryGroup".equals(groupName)) {
            type = Integer.valueOf(2);
            text = (String) getFieldValue("ShipCountry");
            reference = "ShipCountryGroup_" + getVariableValue("ShipCountryNumber");
        } else {
            type = Integer.valueOf(1);
            text = groupName;
            reference = "";
        }
        headings.add(new HeadingBean(type, text, reference, pageIndex));

        return Boolean.TRUE;
    }

}

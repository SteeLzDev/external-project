package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: CalendarioTO</p>
 * <p>Description: Transfer Object da tabela de calendaraio</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CalendarioTO extends CustomTransferObject {

    public CalendarioTO() {
        super();
    }
    
    public CalendarioTO(Date calData) {
        this();
        setAttribute(Columns.CAL_DATA, calData);
    }
    
    public CalendarioTO(CalendarioTO calendario) {
        this();
        setAtributos(calendario.getAtributos());
    }
    
    // Getter
    public Date getCalData() {
        return (Date) getAttribute(Columns.CAL_DATA);
    }
    
    public String getCalDescricao() {
        return (String) getAttribute(Columns.CAL_DESCRICAO);
    }
    
    public String getCalDiaUtil() {
        return (String) getAttribute(Columns.CAL_DIA_UTIL);
    }
    
    // Setter
    public void setCalDescricao(String calDescricao) {
        setAttribute(Columns.CAL_DESCRICAO, calDescricao);
    }
    
    public void setCalDiaUtil(String calDiaUtil) {
        setAttribute(Columns.CAL_DIA_UTIL, calDiaUtil);
    }    
}

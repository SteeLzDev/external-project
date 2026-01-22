package com.zetra.econsig.values;

/**
 * <p>Title: TipoNotaFiscalEnum </p>
 * <p>Description: Classe Enum que contem os tipos de nota fiscal </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum TipoNotaFiscalEnum {

    SUBSIDIO("S", "Subsídio"),
    MC_MNC("M", "MC e MNC"),
    COPART("C", "Copart");
    
    String codigo;
    String descricao;
    
    TipoNotaFiscalEnum (String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }
}

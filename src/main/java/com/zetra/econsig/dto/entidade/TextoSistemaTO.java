package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: TextoSistemaTO</p>
 * <p>Description: Transfer Object da tabela de TextoSistema</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TextoSistemaTO extends CustomTransferObject {
    /** Serialization class version number. */
    private static final long serialVersionUID = 1L;

    public TextoSistemaTO() {
        super();
    }

    public TextoSistemaTO(String texChave) {
        this();
        setAttribute(Columns.TEX_CHAVE, texChave);
    }

    public TextoSistemaTO(String texChave, String texTexto) {
        this();
        setAttribute(Columns.TEX_CHAVE, texChave);
        setAttribute(Columns.TEX_TEXTO, texTexto);
    }

    public TextoSistemaTO(TextoSistemaTO textoSistema) {
        this();
        setAtributos(textoSistema.getAtributos());
    }

    // Getter
    public String getTexChave() {
        return (String) getAttribute(Columns.TEX_CHAVE);
    }

    public String getTexTexto() {
        return (String) getAttribute(Columns.TEX_TEXTO);
    }

    public Date getTexDataAlteracao() {
        return (Date) getAttribute(Columns.TEX_DATA_ALTERACAO);
    }

    // Setter
    public void setTexChave(String texChave) {
        setAttribute(Columns.TEX_CHAVE, texChave);
    }

    public void setTexTexto(String texTexto) {
        setAttribute(Columns.TEX_TEXTO, texTexto);
    }

    public void setTexDataAlteracao(Date texDataAlteracao) {
        setAttribute(Columns.TEX_DATA_ALTERACAO, texDataAlteracao);
    }
}

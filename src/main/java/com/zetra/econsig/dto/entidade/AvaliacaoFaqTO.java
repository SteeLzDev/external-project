package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AvaliacaoFaqTO</p>
 * <p>Description: Transfer Object da tabela de AvaliacaoFaq</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public class AvaliacaoFaqTO extends CustomTransferObject {

    public AvaliacaoFaqTO() {
        super();
    }

    public AvaliacaoFaqTO(String avfCodigo) {
        this();
        setAttribute(Columns.AVF_CODIGO, avfCodigo);
    }

    public AvaliacaoFaqTO(AvaliacaoFaqTO avaliacaoFaq) {
        this();
        setAtributos(avaliacaoFaq.getAtributos());
    }

   // Getter
    public String getAvfCodigo() {
        return (String) getAttribute(Columns.AVF_CODIGO);
    }
    
    public String getUsuCodigo() {
        return (String) getAttribute(Columns.AVF_USU_CODIGO);
    }
    
    public String getFaqCodigo() {
        return (String) getAttribute(Columns.AVF_FAQ_CODIGO);
    }

    public String getAvfNota() {
        return (String) getAttribute(Columns.AVF_NOTA);
    }

    public Date getAvfData() {
        return (Date) getAttribute(Columns.AVF_DATA);
    }

    public String getAvfComentario() {
        return (String) getAttribute(Columns.AVF_COMENTARIO);
    }

    // Setter
    public void setAvfCodigo(String avfCodigo) {
        setAttribute(Columns.AVF_CODIGO, avfCodigo);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.AVF_USU_CODIGO, usuCodigo);
    }

    public void setAvfFaqCodigo(String avfFaqCodigo) {
        setAttribute(Columns.AVF_FAQ_CODIGO, avfFaqCodigo);
    }

    public void setAvfNota(String avfNota) {
        setAttribute(Columns.AVF_NOTA, avfNota);
    }

    public void setAvfData(Date avfData) {
        setAttribute(Columns.AVF_DATA, avfData);
    }

    public void setAvfComentario(String avfComentario) {
        setAttribute(Columns.AVF_COMENTARIO, avfComentario);
    }

}

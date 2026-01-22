package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ItemMenuTO</p>
 * <p>Description: Transfer Object de item de menu.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ItemMenuTO extends CustomTransferObject {

    public ItemMenuTO() {
        super();
    }

    public ItemMenuTO(String itmCodigo) {
        this();
        setAttribute(Columns.ITM_CODIGO, itmCodigo);
    }

    public ItemMenuTO(TransferObject to) {
        this();
        setAtributos(to.getAtributos());
    }

    public String getItmCodigo() {
        return (String) getAttribute(Columns.ITM_CODIGO);
    }

    public void setItmCodigo(String itmCodigo) {
        setAttribute(Columns.ITM_CODIGO, itmCodigo);
    }

    public String getItmCodigoPai() {
        return (String) getAttribute(Columns.ITM_CODIGO_PAI);
    }

    public void setItmCodigoPai(String itmCodigoPai) {
        setAttribute(Columns.ITM_CODIGO_PAI, itmCodigoPai);
    }

    public String getItmDescricao() {
        return (String) getAttribute(Columns.ITM_DESCRICAO);
    }

    public void setItmDescricao(String itmDescricao) {
        setAttribute(Columns.ITM_DESCRICAO, itmDescricao);
    }

    public Short getItmAtivo() {
        return (Short) getAttribute(Columns.ITM_ATIVO);
    }

    public void setItmAtivo(Short itmAtivo) {
        setAttribute(Columns.ITM_ATIVO, itmAtivo);
    }

    public Short getItmSequencia() {
        return (Short) getAttribute(Columns.ITM_SEQUENCIA);
    }

    public void setItmSequencia(Short itmSequencia) {
        setAttribute(Columns.ITM_SEQUENCIA, itmSequencia);
    }

    public String getItmSeparador() {
        return (String) getAttribute(Columns.ITM_SEPARADOR);
    }

    public void setItmSeparador(String itmSeparador) {
        setAttribute(Columns.ITM_SEPARADOR, itmSeparador);
    }

    public String getItmImagem() {
        return (String) getAttribute(Columns.ITM_IMAGEM);
    }

    public void setItmImagem(String itmImagem) {
        setAttribute(Columns.ITM_IMAGEM, itmImagem);
    }

    public String getItmTexChave() {
        return (String) getAttribute(Columns.ITM_TEX_CHAVE);
    }

    public void setItmTexChave(String itmTexChave) {
        setAttribute(Columns.ITM_TEX_CHAVE, itmTexChave);
    }

    public String getAcrRecurso() {
        return (String) getAttribute(Columns.ACR_RECURSO);
    }

    public void setAcrRecurso(String acrRecurso) {
        setAttribute(Columns.ACR_RECURSO, acrRecurso);
    }

    public Short getAcrMetodoHttp() {
        return (Short) getAttribute(Columns.ACR_METODO_HTTP);
    }

    public void setAcrMetodoHttp(Short acrMetodoHttp) {
        setAttribute(Columns.ACR_METODO_HTTP, acrMetodoHttp);
    }

    public String getFunCodigo() {
        return (String) getAttribute(Columns.FUN_CODIGO);
    }

    public void setFunCodigo(String funCodigo) {
        setAttribute(Columns.FUN_CODIGO, funCodigo);
    }

    public String getGrfCodigo() {
        return (String) getAttribute(Columns.GRF_CODIGO);
    }

    public void setGrfCodigo(String grfCodigo) {
        setAttribute(Columns.GRF_CODIGO, grfCodigo);
    }

    public Short getImfSequencia() {
        return (Short) getAttribute(Columns.IMF_SEQUENCIA);
    }

    public void setImfSequencia(Short imfSequencia) {
        setAttribute(Columns.IMF_SEQUENCIA, imfSequencia);
    }

    public Integer getMnuCodigo() {
        return (Integer) getAttribute(Columns.MNU_CODIGO);
    }

    public void setMnuCodigo(Integer mnuCodigo) {
        setAttribute(Columns.MNU_CODIGO, mnuCodigo);
    }

    public String getMnuImagem() {
        return (String) getAttribute(Columns.MNU_IMAGEM);
    }

    public void setMnuImagem(String mnuImagem) {
        setAttribute(Columns.MNU_IMAGEM, mnuImagem);
    }
}

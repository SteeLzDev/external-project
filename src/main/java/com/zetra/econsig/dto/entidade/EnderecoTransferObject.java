package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: EnderecoTransferObject</p>
 * <p>Description: Transfer Object do Endereco</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author junio.goncalves
 * $Author: junio.goncalves $
 * $Revision:  $
 * $Date: 2012-12-05 11:00:00 -0200 (qua, 05 dez 2012) $
 */
public class EnderecoTransferObject extends CustomTransferObject {

    public EnderecoTransferObject() {
        super();
    }

    public EnderecoTransferObject(String echCodigo) {
        this();
        setAttribute(Columns.ECH_CODIGO, echCodigo);
    }

    public EnderecoTransferObject(EnderecoTransferObject endereco) {
        this();
        setAtributos(endereco.getAtributos());
    }

    public String getEchCodigo(){
    	return (String) getAttribute(Columns.ECH_CODIGO);
    }

    public String getConsignataria(){
    	return (String) getAttribute(Columns.ECH_CSA_CODIGO);
    }

    public String getEchIdentificador(){
    	return (String) getAttribute(Columns.ECH_IDENTIFICADOR);
    }

    public String getEchDescricao(){
    	return (String) getAttribute(Columns.ECH_DESCRICAO);
    }

    public String getEchCondominio(){
    	return (String) getAttribute(Columns.ECH_CONDOMINIO);
    }

    public Short getEchQtdUnidades(){
    	return (Short) getAttribute(Columns.ECH_QTD_UNIDADES);
    }

    public void setEchCodigo(String echCodigo) {
        setAttribute(Columns.ECH_CODIGO, echCodigo);
    }

    public void setConsignataria(String consignataria) {
        setAttribute(Columns.ECH_CSA_CODIGO, consignataria);
    }

    public void setEchIdentificador(String echIdentificador) {
        setAttribute(Columns.ECH_IDENTIFICADOR, echIdentificador);
    }

    public void setEchDescricao(String echDescricao) {
        setAttribute(Columns.ECH_DESCRICAO, echDescricao);
    }

    public void setEchCondominio(String echCondominio) {
        setAttribute(Columns.ECH_CONDOMINIO, echCondominio);
    }

    public void setEchQtdUnidades(Short echQtdUnidades) {
        setAttribute(Columns.ECH_QTD_UNIDADES, echQtdUnidades);
    }
}
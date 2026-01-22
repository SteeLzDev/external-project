package com.zetra.econsig.service.sdp;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.PlanoDescontoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PlanoDescontoController</p>
 * <p>Description: Interface de manutenção de planos de desconto.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface PlanoDescontoController {
    public TransferObject buscaPlanoDesconto(TransferObject criterio, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public List<TransferObject> lstPlanoDescontoSemRateio(TransferObject criterios, int offset, int count, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public List<TransferObject> lstPlanoDesconto(TransferObject criterios, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public List<TransferObject> lstPlanoDesconto(TransferObject criterios, int offset, int count, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public List<TransferObject> lstPlanoDescontoTaxaUso(String csaCodigo, String svcCodigo, String rseCodigo, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public int countPlanosDesconto(TransferObject criterio, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public void removePlanoDesconto(TransferObject planoDesconto, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public String createPlanoDesconto(TransferObject planoDesconto, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public String createPlanoDesconto(TransferObject planoDesconto, List<TransferObject> lstParamPlano, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public void updatePlanoDesconto(TransferObject planoDesconto, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public void updatePlanoDesconto(TransferObject planoDesconto, List<TransferObject> lstParamPlano, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public TransferObject findPlanoDesconto(TransferObject planoDesconto, AcessoSistema responsavel) throws PlanoDescontoControllerException;

    public List<TransferObject> lstNaturezasPlanos() throws PlanoDescontoControllerException;
}

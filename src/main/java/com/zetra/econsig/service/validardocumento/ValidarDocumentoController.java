package com.zetra.econsig.service.validardocumento;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ValidarDocumentoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;

/**
 * <p>Title: ValidarDocumentoController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ValidarDocumentoController {

    public void aprovarContrato(String soaCodigo, String adeCodigo, String observacao, AcessoSistema responsavel) throws ValidarDocumentoControllerException;
    public void reprovarContrato(String soaCodigo, String adeCodigo, String observacao, AcessoSistema responsavel) throws ValidarDocumentoControllerException;
    public void submeterContratoNovaAnalise(String soaCodigo, String adeCodigo, Date periodo, String observacao, AcessoSistema responsavel) throws ValidarDocumentoControllerException;
    public SolicitacaoAutorizacao listUltSolicitacaoValidacao(String adeCodigo, AcessoSistema responsavel) throws ValidarDocumentoControllerException;
    public List<TransferObject> listarContratosStatusSolicitacaoIniFimPeriodo(String ssoCodigo, Date periodo, AcessoSistema responsavel) throws ValidarDocumentoControllerException;
    public List<TransferObject> auditoriaContratos(Date periodo, boolean incluiOrgao, AcessoSistema responsavel) throws ValidarDocumentoControllerException;
    public List<TransferObject> lstSolicitacaoAutorizacaoValidarDocumentos(String adeCodigo, AcessoSistema responsavel) throws ValidarDocumentoControllerException;
    public List<TransferObject> auditoriaUsuarios(Date periodo, AcessoSistema responsavel) throws ValidarDocumentoControllerException;
    public void submeterContratoAguardandoDocumentacao(String soaCodigo, String adeCodigo, Date periodo, String observacao, AcessoSistema responsavel) throws ValidarDocumentoControllerException;

}

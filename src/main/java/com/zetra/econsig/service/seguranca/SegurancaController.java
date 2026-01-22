package com.zetra.econsig.service.seguranca;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SegurancaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: SegurancaController</p>
 * <p>Description: Interface Remota do Session Bean para operações de segurança</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface SegurancaController {

    public List<TransferObject> lstNivelSeguranca(AcessoSistema responsavel) throws SegurancaControllerException;

    public String obtemNivelSeguranca(AcessoSistema responsavel) throws SegurancaControllerException;

    public List<Map<String, String>> detalharNivelSegurancaParamSist(AcessoSistema responsavel) throws SegurancaControllerException;

    public void removerOperacoesLiberacaoMargemPosPrazo(AcessoSistema responsavel) throws SegurancaControllerException;

    public void registrarOperacoesLiberacaoMargem(String rseCodigo, String adeCodigo, AcessoSistema responsavel) throws SegurancaControllerException;

    public void confirmarOperacoesLiberacaoMargem(AcessoSistema responsavel) throws SegurancaControllerException;

    public boolean exigirCaptchaOperacoesLiberacaoMargem(AcessoSistema responsavel);

    public boolean exigirSegundaSenhaOperacoesLiberacaoMargem(AcessoSistema responsavel);
}

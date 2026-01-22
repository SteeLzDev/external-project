package com.zetra.econsig.service.beneficios;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CalcularSubsidioBeneficioController</p>
 * <p>Description: Interface Remota para a rotina de cálculo de subsídio para módulo de benefícios</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CalcularSubsidioBeneficioController {

    public void definirPeriodoCalculoSubsidio(Date periodo, Date dataIni, Date dataFim, AcessoSistema responsavel) throws BeneficioControllerException;

    public void calcularSubsidioContratosBeneficios(Date periodoReferencia, boolean validar, String nomeArqCritica, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws BeneficioControllerException;

    public void calcularOrdemDependenciaBeneficiario(String tipoEntidade, List<String> entCodigos, List<String> bfcCodigos, boolean simulacao, AcessoSistema responsavel) throws BeneficioControllerException;

    public void calcularSubsidioContratosBeneficiosProRata(boolean validar, String adeCodigo, String orgCodigo, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> simularCalculoSubsidio(Map<String, List<String>> dadosSimulacao, String rseCodigo, boolean incluirDependente , AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> ordenarBeneficiariosDireitoSubsidio(List<TransferObject> beneficiarios, boolean ordemPrioridadeGpFamiliarDependencia, boolean ordemPrioridadeGpFamiliarParentesco, AcessoSistema responsavel) throws BeneficioControllerException;
}

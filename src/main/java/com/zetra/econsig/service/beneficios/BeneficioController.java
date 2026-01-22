package com.zetra.econsig.service.beneficios;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.BeneficioServico;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.NaturezaServico;

/**
 * <p>Title: BeneficioController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface BeneficioController {

    public List<TransferObject> listaBeneficio(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> listaTipoBeneficiario(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> lstBeneficioCsaOperadora(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public int lstCountBeneficioCsaOperadora(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> lstBeneficioCsaOperadoraPaginacao(TransferObject criterio, int offset, int valorFinal, AcessoSistema responsavel) throws BeneficioControllerException;

    public Beneficio findBeneficioByCodigo(String benCodigo, AcessoSistema responsavel) throws BeneficioControllerException;

    public void update(Beneficio beneficio, AcessoSistema responsavel) throws BeneficioControllerException;

    public Beneficio create(Consignataria consignataria, NaturezaServico naturezaServico, String descricao, String codigoPlano, String codigoRegistro, String codigoContrato, AcessoSistema responsavel) throws BeneficioControllerException;

    public void remove(Beneficio beneficio, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> findRelacaoBeneficioByRseCodigo(CustomTransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public Beneficio create(Consignataria consignataria, NaturezaServico naturezaServico, String descricao, String codigoPlano, String codigoRegistro, String codigoContrato, Map<String, List<BeneficioServico>> relacionamentos, AcessoSistema responsavel) throws BeneficioControllerException;

    public void update(Beneficio beneficio, Map<String, List<BeneficioServico>> relacionamentos, AcessoSistema responsavel) throws BeneficioControllerException;

    public Beneficio findBeneficioFetchBeneficioServicoByCodigo(String benCodigo, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<Beneficio> lstBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServico(String csaCodigo, String nseCodigo, boolean benAtivo, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> lstBeneficioByCsaCodigoAndNaturezaServico(String csaCodigo, String corCodigo, String nseCodigo, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> findRelacaoBeneficioObitoDependente(CustomTransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public void bloqueioBeneficio(Beneficio beneficio, AcessoSistema responsavel) throws BeneficioControllerException;

    public void desbloqueioBeneficio(Beneficio beneficio, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> lstBeneficioByCsaCodigoAndNaturezaServicoCorrespondentes(String csaCodigo, List<String> corCodigos, String nseCodigo, AcessoSistema responsavel) throws BeneficioControllerException;

}

package com.zetra.econsig.service.consignante;


import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Banco;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCse;
import com.zetra.econsig.persistence.entity.Estabelecimento;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.TipoConsignante;

/**
 * <p>Title: ConsignanteController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ConsignanteController  {
    // Órgão
    public OrgaoTransferObject findOrgao(String orgCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public OrgaoTransferObject findOrgaoByIdn(String orgIdentificador, String estCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public OrgaoTransferObject findOrgao(OrgaoTransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    public String createOrgao(OrgaoTransferObject orgao, AcessoSistema responsavel) throws ConsignanteControllerException;

    public String createOrgao(OrgaoTransferObject orgao, boolean criarConvenio, String orgCodigoACopiar, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void updateOrgao(OrgaoTransferObject orgao, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void removeOrgao(OrgaoTransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstOrgaos(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstOrgaos(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignanteControllerException;

    public int countOrgaos(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    public Map<String, Integer> getOrgDiaRepasse(String orgCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<OrgaoTransferObject> listarOrgaosDirf(AcessoSistema responsavel) throws ConsignanteControllerException;

    public void createOcorrenciaOrg(String orgCodigo, String tocCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public int countOcorrenciaOrgao(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    // Estabelecimento
    public EstabelecimentoTransferObject findEstabelecimento(EstabelecimentoTransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    public EstabelecimentoTransferObject findEstabelecimento(String estCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public EstabelecimentoTransferObject findEstabelecimentoByIdn(String estIdentificador, AcessoSistema responsavel) throws ConsignanteControllerException;

    public EstabelecimentoTransferObject findEstabelecimentoByOrgao(String orgCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public String createEstabelecimento(EstabelecimentoTransferObject estabelecimento, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void updateEstabelecimento(EstabelecimentoTransferObject estabelecimento, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void removeEstabelecimento(EstabelecimentoTransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstEstabelecimentos(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstEstabelecimentos(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignanteControllerException;

    public int countEstabelecimentos(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    // Consignante
    public ConsignanteTransferObject findConsignante(String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public ConsignanteTransferObject findConsignanteByIdn(String cseIdentificador, AcessoSistema responsavel) throws ConsignanteControllerException;

    public ConsignanteTransferObject findConsignante(ConsignanteTransferObject consignante, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void updateConsignante(ConsignanteTransferObject consignante, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void updateConsignante(ConsignanteTransferObject consignante, String msg, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void createOcorrenciaCse(String tocCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void createOcorrenciaCse(String tocCodigo, String msg, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstOcorrenciaConsignante(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignanteControllerException;

    public int countOcorrenciaConsignante(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    public String dataUltimaAtualizacaoSistema() throws ConsignanteControllerException;

    public List<TipoConsignante> lstTipoCse(AcessoSistema responsavel) throws ConsignanteControllerException;

    public void limparCodigoFolha(AcessoSistema responsavel) throws ConsignanteControllerException;

    public void enviaNotificacaoEnvioArquivosFolha(AcessoSistema responsavel) throws ConsignanteControllerException;

    public Orgao findByOrgCnpj(String orgCnpj, AcessoSistema responsavel) throws ConsignanteControllerException;

    public Estabelecimento findByEstCnpj(String estCnpj, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<Banco> lstBanco(AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<Banco> lstBancoFolha(AcessoSistema responsavel) throws ConsignanteControllerException;

    public void setBancosCse(List<String> cseBancos) throws ConsignanteControllerException;

    public String findDadoAdicionalConsignante(String cseCodigo, String tdaCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstFuncoesEnvioEmailCse(String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void salvarFuncoesEnvioEmailCse(List<DestinatarioEmailCse> listaInc, List<DestinatarioEmailCse> listaAlt, List<DestinatarioEmailCse> listaExc, AcessoSistema responsavel) throws ConsignanteControllerException;

    public String getEmailCseNotificacaoOperacao(String funCodigo, String papCodigoOperador, String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;
}
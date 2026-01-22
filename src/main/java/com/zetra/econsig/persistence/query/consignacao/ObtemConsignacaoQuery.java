package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemConsignacaoQuery</p>
 * <p>Description: Busca de Consignação</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConsignacaoQuery extends HQuery {

    public List<String> adeCodigos;
    public boolean arquivado = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "ade.adeCodigo, " +
                       "ade.adeData, " +
                       "ade.adeVlr, " +
                       "ade.adeVlrRef, " +
                       "ade.adeNumero, " +
                       "ade.adePrazo, " +
                       "ade.adePrazoRef, " +
                       "ade.adePrdPagas, " +
                       "ade.adePrdPagasTotal, " +
                       "ade.adeIdentificador, " +
                       "ade.adeAnoMesIni, " +
                       "ade.adeAnoMesFim, " +
                       "ade.adeAnoMesIniRef, " +
                       "ade.adeAnoMesFimRef, " +
                       "ade.adeTipoVlr, " +
                       "ade.adeTipoTaxa, " +
                       "ade.adeIncMargem, " +
                       "ade.adeIntFolha, " +
                       "ade.adeIndice, " +
                       "ade.adeCodReg, " +
                       "ade.adeVlrTac, " +
                       "ade.adeVlrIof, " +
                       "ade.adeVlrMensVinc, " +
                       "ade.adeVlrLiquido, " +
                       "ade.adeDataHoraOcorrencia, " +
                       "ade.adeVlrSegPrestamista, " +
                       "ade.adeTaxaJuros, " +
                       "ade.adeBanco, " +
                       "ade.adeAgencia, " +
                       "ade.adeConta, " +
                       "ade.adeVlrSdoRet, " +
                       "ade.adeCarencia, " +
                       "ade.adeVlrPercentual, " +
                       "ade.adeVlrParcelaFolha, " +
                       "ade.adePeriodicidade, " +
                       "ade.adeVlrFolha, " +
                       "ade.adeDataReativacaoAutomatica, " +
                       "ade.adeDataStatus, " +
                       "sad.sadCodigo, " +
                       "sad.sadDescricao, " +
                       "usu.usuCodigo, " +
                       "usu.usuLogin, " +
                       "usu.usuNome, " +
                       "usu.usuTipoBloq, " +
                       "usuarioCsa.csaCodigo, " +
                       "usuarioCse.cseCodigo, " +
                       "usuarioCor.corCodigo, " +
                       "usuarioOrg.orgCodigo, " +
                       "usuarioSer.serCodigo, " +
                       "usuarioSup.cseCodigo, " +
                       "crs.crsIdentificador, " +
                       "crs.crsDescricao, " +
                       "prs.prsIdentificador, " +
                       "prs.prsDescricao, " +
                       "sbo.sboIdentificador, " +
                       "sbo.sboDescricao, " +
                       "uni.uniIdentificador, " +
                       "uni.uniDescricao, " +
                       "cnv.cnvCodigo, " +
                       "cnv.cnvCodVerba, " +
                       "cnv.statusConvenio.scvCodigo, " +
                       "csa.csaCodigo, " +
                       "csa.csaIdentificador, " +
                       "csa.csaNomeAbrev, " +
                       "csa.csaNome, " +
                       "csa.csaCnpj, " +
                       "csa.csaLogradouro, " +
                       "csa.csaNro, " +
                       "csa.csaCompl, " +
                       "csa.csaBairro, " +
                       "csa.csaEmail, " +
                       "csa.csaTel, " +
                       "csa.csaCidade, " +
                       "csa.csaCep, " +
                       "csa.csaResponsavel, " +
                       "csa.csaTxtContato, " +
                       "est.consignante.cseCodigo, " +
                       "est.estIdentificador, " +
                       "est.estNome, " +
                       "org.orgCodigo, " +
                       "org.orgIdentificador, " +
                       "org.orgNome, " +
                       "org.orgCidade, " +
                       "org.orgTel, " +
                       "org.orgEmail, " +
                       "org.orgEmailValidarServidor, " +
                       "rse.rseCodigo, " +
                       "rse.rseMatricula, " +
                       "rse.rseMatriculaInst, " +
                       "rse.rseTipo, " +
                       "rse.rsePrazo, " +
                       "rse.rseDataAdmissao, " +
                       "rse.rseClt, " +
                       "rse.rseBancoSal, " +
                       "rse.rseAgenciaSal, " +
                       "rse.rseAgenciaDvSal, " +
                       "rse.rseContaSal, " +
                       "rse.rseContaDvSal, " +
                       "rse.rseBancoSal2, " +
                       "rse.rseAgenciaSal2, " +
                       "rse.rseAgenciaDvSal2, " +
                       "rse.rseContaSal2, " +
                       "rse.rseContaDvSal2, " +
                       "rse.rseDataFimEngajamento, " +
                       "rse.rseDataLimitePermanencia, " +
                       "rse.rseEstabilizado, " +
                       "rse.rseObs, " +
                       "rse.rseMunicipioLotacao, " +
                       "rse.rseBeneficiarioFinanDvCart, " +
                       "rse.rsePontuacao, " +
                       "rse.rseSalario, " +
                       "rse.rseProventos, " +
                       "rse.rseDataSaida, " +
                       "rse.rseDataUltSalario, " +
                       "rse.rsePedidoDemissao, " +
                       "rse.rseDataRetorno, " +
                       "pos.posDescricao, "+
                       "ser.serCodigo, " +
                       "ser.serNome, " +
                       "ser.serCpf, " +
                       "ser.serDataNasc, " +
                       "ser.serNroIdt, " +
                       "ser.serEmissorIdt, " +
                       "ser.serUfIdt, " +
                       "ser.serDataIdt, " +
                       "ser.serPis, " +
                       "ser.serEnd, " +
                       "ser.serCompl, " +
                       "ser.serCep, " +
                       "ser.serNro, " +
                       "ser.serBairro, " +
                       "ser.serCidade, " +
                       "ser.serUf, " +
                       "ser.serTel, " +
                       "ser.serEmail, " +
                       "ser.serCelular, " +
                       "ser.serSexo, " +
                       "ser.serEstCivil," +
                       "pos.posDescricao, " +
                       "trs.trsDescricao, " +
                       "cap.capCodigo, " +
                       "cap.capDescricao, " +
                       "srs.srsCodigo, " +
                       "srs.srsDescricao, " +
                       "svc.naturezaServico.nseCodigo, " +
                       "svc.svcCodigo, " +
                       "svc.svcIdentificador, " +
                       "svc.svcDescricao, " +
                       "cor.corNome, " +
                       "cor.corIdentificador, " +
                       "cor.corIdentificadorAntigo, " +
                       "cor.corEmail, " +
                       "cft.cftVlr, " +
                       "cde.cdeVlrIof, " +
                       "cde.cdeVlrTac, " +
                       "cde.cdeVlrMensVinc, " +
                       "cde.cdeVlrLiberado, " +
                       "cde.cdeVlrLiberadoCalc, " +
                       "cde.cdeRanking, " +
                       "pla.plaIdentificador, " +
                       "pla.plaDescricao, " +
                       "ech.echDescricao, " +
                       "per.prmComplEndereco, " +

                       (arquivado ? "'' as prdAdeCodigo " :
                       // Flag de Parcela em processamento
                       "(select prd.autDesconto.adeCodigo " +
                       "from ade.parcelaDescontoPeriodoSet prd " +
                       "where prd.statusParcelaDesconto.spdCodigo " +
                       " IN ('" + CodedValues.SPD_EMPROCESSAMENTO + "','" + CodedValues.SPD_SEM_RETORNO + "') " +
                       "group by prd.autDesconto.adeCodigo) as prdAdeCodigo ")
                       ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(arquivado ? "FROM HtAutDesconto ade " : "FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.statusAutorizacaoDesconto sad");
        corpoBuilder.append(" INNER JOIN ade.usuario usu");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" INNER JOIN cnv.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");
        corpoBuilder.append(" LEFT OUTER JOIN ade.correspondente cor");
        corpoBuilder.append(" LEFT OUTER JOIN rse.cargoRegistroServidor crs");
        corpoBuilder.append(" LEFT OUTER JOIN rse.padraoRegistroServidor prs");
        corpoBuilder.append(" LEFT OUTER JOIN rse.subOrgao sbo");
        corpoBuilder.append(" LEFT OUTER JOIN rse.unidade uni");
        corpoBuilder.append(" LEFT OUTER JOIN rse.postoRegistroServidor pos");
        corpoBuilder.append(" LEFT OUTER JOIN rse.tipoRegistroServidor trs");
        corpoBuilder.append(" LEFT OUTER JOIN rse.capacidadeRegistroSer cap");
        corpoBuilder.append(" LEFT OUTER JOIN ade.coeficienteDescontoSet cde");
        corpoBuilder.append(" LEFT OUTER JOIN cde.coeficiente cft");

        corpoBuilder.append(" LEFT OUTER JOIN ade.despesaIndividualSet din ");
        corpoBuilder.append(" LEFT OUTER JOIN din.plano pla ");
        corpoBuilder.append(" LEFT OUTER JOIN din.permissionario per ");
        corpoBuilder.append(" LEFT OUTER JOIN per.enderecoConjHabitacional ech ");

        corpoBuilder.append("LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append("LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSupSet usuarioSup ");

        corpoBuilder.append(" WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigos));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_DATA,
                Columns.ADE_VLR,
                Columns.ADE_VLR_REF,
                Columns.ADE_NUMERO,
                Columns.ADE_PRAZO,
                Columns.ADE_PRAZO_REF,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_PRD_PAGAS_TOTAL,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_ANO_MES_INI_REF,
                Columns.ADE_ANO_MES_FIM_REF,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_TIPO_TAXA,
                Columns.ADE_INC_MARGEM,
                Columns.ADE_INT_FOLHA,
                Columns.ADE_INDICE,
                Columns.ADE_COD_REG,
                Columns.ADE_VLR_TAC,
                Columns.ADE_VLR_IOF,
                Columns.ADE_VLR_MENS_VINC,
                Columns.ADE_VLR_LIQUIDO,
                Columns.ADE_DATA_HORA_OCORRENCIA,
                Columns.ADE_VLR_SEG_PRESTAMISTA,
                Columns.ADE_TAXA_JUROS,
                Columns.ADE_BANCO,
                Columns.ADE_AGENCIA,
                Columns.ADE_CONTA,
                Columns.ADE_VLR_SDO_RET,
                Columns.ADE_CARENCIA,
                Columns.ADE_VLR_PERCENTUAL,
                Columns.ADE_VLR_PARCELA_FOLHA,
                Columns.ADE_PERIODICIDADE,
                Columns.ADE_VLR_FOLHA,
                Columns.ADE_DATA_REATIVACAO_AUTOMATICA,
                Columns.ADE_DATA_STATUS,
                Columns.ADE_SAD_CODIGO,
                Columns.SAD_DESCRICAO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_NOME,
                Columns.USU_TIPO_BLOQ,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.CRS_IDENTIFICADOR,
                Columns.CRS_DESCRICAO,
                Columns.PRS_IDENTIFICADOR,
                Columns.PRS_DESCRICAO,
                Columns.SBO_IDENTIFICADOR,
                Columns.SBO_DESCRICAO,
                Columns.UNI_IDENTIFICADOR,
                Columns.UNI_DESCRICAO,
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.CNV_SCV_CODIGO,
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_NOME,
                Columns.CSA_CNPJ,
                Columns.CSA_LOGRADOURO,
                Columns.CSA_NRO,
                Columns.CSA_COMPL,
                Columns.CSA_BAIRRO,
                Columns.CSA_EMAIL,
                Columns.CSA_TEL,
                Columns.CSA_CIDADE,
                Columns.CSA_CEP,
                Columns.CSA_RESPONSAVEL,
                Columns.CSA_TXT_CONTATO,
                Columns.EST_CSE_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.ORG_CIDADE,
                Columns.ORG_TEL,
                Columns.ORG_EMAIL,
                Columns.ORG_EMAIL_VALIDAR_SERVIDOR,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.RSE_MATRICULA_INST,
                Columns.RSE_TIPO,
                Columns.RSE_PRAZO,
                Columns.RSE_DATA_ADMISSAO,
                Columns.RSE_CLT,
                Columns.RSE_BANCO_SAL,
                Columns.RSE_AGENCIA_SAL,
                Columns.RSE_AGENCIA_DV_SAL,
                Columns.RSE_CONTA_SAL,
                Columns.RSE_CONTA_DV_SAL,
                Columns.RSE_BANCO_SAL_2,
                Columns.RSE_AGENCIA_SAL_2,
                Columns.RSE_AGENCIA_DV_SAL_2,
                Columns.RSE_CONTA_SAL_2,
                Columns.RSE_CONTA_DV_SAL_2,
                Columns.RSE_DATA_FIM_ENGAJAMENTO,
                Columns.RSE_DATA_LIMITE_PERMANENCIA,
                Columns.RSE_ESTABILIZADO,
                Columns.RSE_OBS,
                Columns.RSE_MUNICIPIO_LOTACAO,
                Columns.RSE_BENEFICIARIO_FINAN_DV_CART,
                Columns.RSE_PONTUACAO,
                Columns.RSE_SALARIO,
                Columns.RSE_PROVENTOS,
                Columns.RSE_DATA_SAIDA,
                Columns.RSE_DATA_ULT_SALARIO,
                Columns.RSE_PEDIDO_DEMISSAO,
                Columns.RSE_DATA_RETORNO,
                Columns.POS_DESCRICAO,
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SER_DATA_NASC,
                Columns.SER_NRO_IDT,
                Columns.SER_EMISSOR_IDT,
                Columns.SER_UF_IDT,
                Columns.SER_DATA_IDT,
                Columns.SER_PIS,
                Columns.SER_END,
                Columns.SER_COMPL,
                Columns.SER_CEP,
                Columns.SER_NRO,
                Columns.SER_BAIRRO,
                Columns.SER_CIDADE,
                Columns.SER_UF,
                Columns.SER_TEL,
                Columns.SER_EMAIL,
                Columns.SER_CELULAR,
                Columns.SER_SEXO,
                Columns.SER_EST_CIVIL,
                Columns.POS_DESCRICAO,
                Columns.TRS_DESCRICAO,
                Columns.CAP_CODIGO,
                Columns.CAP_DESCRICAO,
                Columns.SRS_CODIGO,
                Columns.SRS_DESCRICAO,
                Columns.NSE_CODIGO,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.COR_NOME,
                Columns.COR_IDENTIFICADOR,
                Columns.COR_IDENTIFICADOR_ANTIGO,
                Columns.COR_EMAIL,
                Columns.CFT_VLR,
                Columns.CDE_VLR_IOF,
                Columns.CDE_VLR_TAC,
                Columns.CDE_VLR_MENS_VINC,
                Columns.CDE_VLR_LIBERADO,
                Columns.CDE_VLR_LIBERADO_CALC,
                Columns.CDE_RANKING,
                Columns.PLA_IDENTIFICADOR,
                Columns.PLA_DESCRICAO,
                Columns.ECH_DESCRICAO,
                Columns.PRM_COMPL_ENDERECO,
                Columns.PRD_ADE_CODIGO
        };
    }
}
package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaConsignacaoQuery</p>
 * <p>Description: Listagem de Consignações</p>
 * <p>Copyright: Copyright (c) 2008-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonardo Angoti, Marcos Nolasco, Igor Lucas, Leonel Martins
 */
public class ListaConsignacaoQuery extends HQuery {

    public String tipo;
    public String codigo;
    public String rseCodigo;
    public Object adeCodigo;
    public List<Long> adeNumero;
    public List<String> adeIdentificador;

    public String serCpf;
    public String rseMatricula;
    public String tipoOperacao;
    public String tpsCodigo;
    public String tgcCodigo;
    public String csaCodigo;
    public String corCodigo;
    public String estCodigo;
    public String orgCodigo;
    public String tmoCodigo;
    public String tgsCodigo;
    public String svcCodigo;
    public String cnvCodVerba;
    public Date adeAnoMesIni;
    public String tipoOcorrenciaPeriodo;
    public String infSaldoDevedor;
    public int diasSolicitacaoSaldo = -1;
    public int diasSolicitacaoSaldoPagaAnexo = -1;
    public Date periodoIni;
    public Date periodoFim;
    public Short adeIntFolha;
    public Short adeIncMargem;
    public String adeIndice;
    public List<String> srsCodigo;
    public List<String> sadCodigos;
    public List<String> svcCodigos;
    public List<String> nseCodigos;
    public List<String> csaCodigos;
    public List<Short> marCodigos;
    public Date ocaDataIni;
    public Date ocaDataFim;
    public String decCodigo;
    public String prmCodigo;
    public boolean planoExclusaoAutomatica = false; // lista contratos configurados para exclusão automática
    public boolean transferencia = false;             //lista contratos de serviço cuja natureza de serviço permite transferência
    public Object dataConciliacao;                      //lista contratos verificados por processo de conciliação
    public boolean temAnexoPendenteValidacao = false;      // para solicitações. Verifica se possui anexos pendentes de validação
    private final AcessoSistema responsavel;

    public boolean count = false;
    public Boolean adePropria = false;
    public boolean retornaSomenteAdeCodigo = false;
    public boolean arquivado = false;

    public String tipoOrdenacao;
    public String ordenacao;

    private static final String ASC = "ASC";
    private static final String DESC = "DESC";

    public boolean validarInexistenciaConfLiquidacao = false;
    public boolean validarInexistenciaConfLiquidacaoRelacionamento = false;
    public boolean usaModuloBeneficio = false; // True ele vai passar pela a estrutura de tabelas do modulo de beneficio.
    public List<String> tntCodigos;

    public List<String> existeTipoOcorrencias;

    public List<String> listaTipoSolicitacaoSaldo;
    public boolean operacaoSOAPEditarSaldoDevedor;

    public ListaConsignacaoQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;

        // Tipo operação não pode ser nulo, pois é consultado em vários pontos
        tipoOperacao = "";
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        tipo = (tipo == null) ? "" : tipo;
        tipoOperacao = (tipoOperacao == null) ? "" : tipoOperacao;

        // Parâmetro para verificar se tem saldo devedor para exclusão de servidor
        final boolean temModuloSaldoDevedorExclusao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SALDO_DEVEDOR_EXCLUSAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        String corpo = "";

        if (count) {
            corpo = "select count(distinct ade.adeCodigo) as total ";
        } else if (retornaSomenteAdeCodigo) {
            corpo = "select distinct ade.adeCodigo ";
        } else {
            corpo = "select distinct " +
                    "ade.adeCodigo, " +
                    "ade.adeNumero, " +
                    "ade.adeIdentificador, " +
                    "ade.adeData, " +
                    "ade.adeVlr, " +
                    "ade.adeVlrFolha, " +
                    "ade.adeVlrLiquido, " +
                    "ade.adeTaxaJuros, " +
                    "ade.adePrazo, " +
                    "ade.adePrdPagas, " +
                    "ade.adeAnoMesIni, " +
                    "ade.adeAnoMesFim, " +
                    "ade.adeTipoVlr, " +
                    "ade.adeIndice, " +
                    "ade.adeCodReg, " +
                    "ade.adeIncMargem, " +
                    "ade.adeIntFolha, " +
                    "ade.adeCarencia, " +
                    "ade.adeVlrPercentual, " +
                    "ade.adeVlrParcelaFolha, " +
                    "ade.adeDataStatus, " +
                    "ade.adePeriodicidade, " +
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
                    "rse.rseCodigo, " +
                    "rse.orgao.orgCodigo, " +
                    "rse.rseMatricula, " +
                    "rse.rseSalario, " +
                    "rse.rseDataCarga, " +
                    "rse.rseDataAlteracao, " +
                    "rse.rseBaseCalculo," +
                    "rse.rsePrazo," +
                    "ser.serCodigo, " +
                    "ser.serNome, " +
                    "ser.serCpf, " +
                    "ser.serTel, " +
                    "ser.serEmail, " +
                    "svc.svcCodigo, " +
                    "svc.svcIdentificador, " +
                    "svc.svcDescricao, " +
                    "svc.nseCodigo, " +
                    "csa.csaCodigo, " +
                    "csa.csaIdentificador, " +
                    "csa.csaNome, " +
                    "csa.csaNomeAbrev, " +
                    "cnv.cnvCodigo, " +
                    "cnv.cnvCodVerba, " +
                    "cnv.consignataria.csaCodigo, " +
                    "cnv.orgao.orgCodigo, " +
                    "cft.cftVlr, " +
                    "cde.cdeVlrLiberado, " +
                    "cde.cdeVlrLiberadoCalc, " +
                    "sdv.sdvValor, " +
                    "sdv.sdvDataMod, " +
                    "cor.corCodigo, " +
                    "cor.corNome, " +
                    "cor.corIdentificador, " +
                    "dad46.dadValor, " +
                    "dad87.dadValor, " +
                    "ade.adeDataNotificacaoCse, " +
                    "ade.adeDataLiberacaoValor, " +
                    "to_numeric(COALESCE(svc.svcPrioridade, '9999999')) as SVC_PRIORIDADE, " +
                    "to_numeric(COALESCE(cnv.cnvPrioridade, 9999999)) as CNV_PRIORIDADE, " +
                    "COALESCE(ade.adeAnoMesIniRef, ade.adeAnoMesIni) as ADE_ANO_MES_INI_PRIORIDADE, " +
                    "COALESCE(ade.adeDataRef, ade.adeData) as ADE_DATA_PRIORIDADE, " +
                    " ade.adeNumero as ADE_NUMERO_PRIORIDADE ";

            if ("consultar".equalsIgnoreCase(tipoOperacao) &&
                    (AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipo) ||
                     AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipo) ||
                     AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipo) ||
                     AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipo))) {
                corpo+= ", CASE " +
                        "WHEN (ade.adeCodigo in (select distinct oca.adeCodigo from DecisaoJudicial dju inner join OcorrenciaAutorizacao oca on (oca.ocaCodigo = dju.ocaCodigo))) THEN '" + CodedValues.DECISAO_JUDICIAL_SIM + "' " +
                        "WHEN (ade.adeCodigo in (select distinct oca.adeCodigo from OcorrenciaAutorizacao oca inner join TipoMotivoOperacao tmo on (oca.tmoCodigo = tmo.tmoCodigo and tmo.tmoDecisaoJudicial = 'S'))) THEN '" + CodedValues.DECISAO_JUDICIAL_SIM + "' " +
                        "ELSE '"+ CodedValues.DECISAO_JUDICIAL_NAO + "' " +
                        "END as POSSUI_DECISAO_JUDICIAL ";
            }

            if (AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(tipo)) {
                corpo += ", cor.corIdentificador " +
                         ", cor.corNome ";
            }

            if ("cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao)) {
                corpo += ", prm.prmComplEndereco ";

            } else if ("solicitacao".equalsIgnoreCase(tipoOperacao) && responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, CodedValues.TPC_SIM, responsavel)){
                corpo += ", arr.arrRisco ";
            } else if ("deferir".equalsIgnoreCase(tipoOperacao) || "indeferir".equalsIgnoreCase(tipoOperacao)) {
                corpo += ", (" +
                         "select sum(adeOrigem.adeVlr) " +
                         "from ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad " +
                         "inner join rad.autDescontoByAdeCodigoOrigem adeOrigem " +
                         "where rad.tipoNatureza.tntCodigo = '" + CodedValues.TNT_CONTROLE_RENEGOCIACAO + "'" +
                         ") ";

                if (ParamSist.paramEquals(CodedValues.TPC_ALERTA_DEFER_INDEFER_MANUAL_CONTRATO, CodedValues.TPC_SIM, responsavel)) {
                    corpo += ", (select count(adeAguardLid.adeCodigo) from rse.autDescontoSet adeAguardLid where adeAguardLid.statusAutorizacaoDesconto.sadCodigo = '" + CodedValues.SAD_AGUARD_LIQUIDACAO + "') ";
                }

            } else if ("reativar".equalsIgnoreCase(tipoOperacao)) {
                corpo += ", (case when sad.sadCodigo = '" + CodedValues.SAD_SUSPENSA_CSE + "'"
                       + " and coalesce(ade.adeIncMargem, 0) = 0 "
                       + " and coalesce((select pse3.pseVlr from svc.paramSvcConsignanteSet pse3 where pse3.tipoParamSvc.tpsCodigo = '" + CodedValues.TPS_INCIDE_MARGEM + "'), '1') <> '0' "
                       + " then '1' else '0' end)";
            } else if ("consultar_historico_pagamento".equalsIgnoreCase(tipoOperacao)) {
                corpo += ", dad48.dadValor " +
                		 ", dad90.dadValor " +
                		 ", dad91.dadValor ";
            }

            if (usaModuloBeneficio) {
                corpo += ", cbe.cbeNumero " +
                         ", bfc.bfcCpf ";
            }

            if ("confirmar_liquidacao".equalsIgnoreCase(tipoOperacao)) {
                corpo += ",  case when ocaSolicitacao.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_SOLICITAR_LIQUIDACAO_CONSIGNACAO + "' then 'true' else 'false' end ";
            }
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(arquivado ? "from HtAutDesconto ade " : "from AutDesconto ade ");
        corpoBuilder.append("inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append("inner join ade.usuario usu ");
        corpoBuilder.append("inner join ade.registroServidor rse ");
        corpoBuilder.append("inner join rse.servidor ser ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("inner join org.estabelecimento est ");
        corpoBuilder.append("inner join est.consignante cse ");
        if (AcessoSistema.ENTIDADE_COR.equalsIgnoreCase(tipo) && !"retirar_contrato_compra".equalsIgnoreCase(tipoOperacao)) {
            // Se é usuário de COR sem opção de 'Acessar Consignações da Consignatária'
            corpoBuilder.append(" inner join ade.correspondente cor ");
        } else {
            corpoBuilder.append(" left outer join ade.correspondente cor ");
        }
        corpoBuilder.append("LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append("LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSupSet usuarioSup ");

        if (transferencia) {
            corpoBuilder.append("left outer join svc.naturezaServico nse ");
        }

        if (AcessoSistema.ENTIDADE_SER.equalsIgnoreCase(tipo)) {
            corpoBuilder.append(" inner join ser.usuarioSerSet usr ");
            corpoBuilder.append(" left outer join svc.paramSvcConsignanteSet pse64 with ");
            corpoBuilder.append(" pse64.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_EXIBE_CONTRATO_SERVIDOR).append("' ");
        }

        if (responsavel.isCor() && !"retirar_contrato_compra".equalsIgnoreCase(tipoOperacao)) {
            // Se o responsável é correspondente (com ou sem permissão de 'Acessar Consignações da Consignatária',
            // ou seja tipo = COR/CSA ou CSE quando compra) faz left com convênio de correspondente.
            corpoBuilder.append(" left outer join cnv.correspondenteConvenioSet crc with ");
            corpoBuilder.append(" crc.correspondente.corCodigo = :correspondente AND ");
            corpoBuilder.append(" crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        }

        if ("cancelar_compra".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" inner join ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad with ");
            corpoBuilder.append(" rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");

        } else if ("comprar".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" inner join svc.relacionamentoServicoByDestinoSet rsv with ");
            corpoBuilder.append(" rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_COMPRA).append("' AND ");
            corpoBuilder.append(" rsv.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));

        } else if ("renegociar".equalsIgnoreCase(tipoOperacao) || "simular_renegociacao".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" inner join svc.relacionamentoServicoByDestinoSet rsv with ");
            corpoBuilder.append(" rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_RENEGOCIACAO).append("' AND ");
            corpoBuilder.append(" rsv.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));

        } else if ("cancelar_renegociacao".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" inner join ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad with ");
            corpoBuilder.append(" rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");

        } else if ("retirar_contrato_compra".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" inner join ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rad with ");
            corpoBuilder.append(" rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
            corpoBuilder.append(" inner join rad.autDescontoByAdeCodigoDestino adeDestino ");
            corpoBuilder.append(" inner join adeDestino.verbaConvenio vcoDestino ");
            corpoBuilder.append(" inner join vcoDestino.convenio cnvDestino ");
            corpoBuilder.append(" inner join cnvDestino.consignataria csaDestino ");
            corpoBuilder.append(" inner join cnvDestino.orgao orgDestino ");
            corpoBuilder.append(" inner join orgDestino.estabelecimento estDestino ");
            corpoBuilder.append(" inner join estDestino.consignante cseDestino ");

        } else if ("cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" inner join ade.despesaIndividualSet din ");
            corpoBuilder.append(" inner join rse.permissionarioSet prm ");
            if (planoExclusaoAutomatica) {
                corpoBuilder.append(" inner join din.plano pla ");
                corpoBuilder.append(" left outer join pla.parametroPlanoSet ppl with ");
                corpoBuilder.append(" ppl.tppCodigo = '").append(CodedValues.TPP_EXCLUSAO_AUTOMATICA).append("' ");
            }
        } else if ("solicitacao_renegociacao".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" inner join usu.usuarioSerSet usuSer ");
            corpoBuilder.append(" inner join ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad with ");
            corpoBuilder.append(" rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");

        } else if ("solicitacao".equalsIgnoreCase(tipoOperacao) && responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, CodedValues.TPC_SIM, responsavel)){
            corpoBuilder.append(" left outer join rse.analiseRiscoRegistroSerSet arr");
            corpoBuilder.append(" WITH arr.consignataria.csaCodigo = :csaCodigoArr");
        } else if ("consultar_historico_pagamento".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" left outer join ade.dadosAutorizacaoDescontoSet dad48 with dad48.tipoDadoAdicional.tdaCodigo = '").append(CodedValues.TDA_NOME_ESTABELECIMENTO_CARTAO).append("'");
            corpoBuilder.append(" left outer join ade.dadosAutorizacaoDescontoSet dad90 with dad90.tipoDadoAdicional.tdaCodigo = '").append(CodedValues.TDA_TRANSFERENCIA_TAXA).append("'");
            corpoBuilder.append(" left outer join ade.dadosAutorizacaoDescontoSet dad91 with dad91.tipoDadoAdicional.tdaCodigo = '").append(CodedValues.TDA_TRANSFERENCIA_VALOR_CREDITADO).append("'");
        }

        if ("confirmar_liquidacao".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" left join ade.ocorrenciaAutorizacaoSet ocaSolicitacao with  ade.statusAutorizacaoDesconto.sadCodigo = '" + CodedValues.SAD_AGUARD_LIQUIDACAO + "'").append(" and ocaSolicitacao.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_SOLICITAR_LIQUIDACAO_CONSIGNACAO).append("'");
        }

        if ("cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao) && !TextHelper.isNull(decCodigo)) {
            corpoBuilder.append(" inner join din.despesaComum dec ");
        }

        if (!"cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao) && !TextHelper.isNull(prmCodigo)) {
            corpoBuilder.append(" inner join rse.permissionarioSet prm ");
        }

        if (usaModuloBeneficio) {
            corpoBuilder.append(" inner join ade.contratoBeneficio cbe ");
            corpoBuilder.append(" inner join cbe.beneficiario bfc ");
        }
        if ((tntCodigos != null) && (tntCodigos.size() > 0)) {
            corpoBuilder.append(" inner join ade.tipoLancamento tla ");
        }

        corpoBuilder.append(" left outer join ade.coeficienteDescontoSet cde ");
        corpoBuilder.append(" left outer join cde.coeficiente cft ");
        corpoBuilder.append(" left outer join ade.saldoDevedorSet sdv ");
        corpoBuilder.append(" left outer join ade.dadosAutorizacaoDescontoSet dad46 with dad46.tipoDadoAdicional.tdaCodigo='").append(CodedValues.TDA_AFETADA_DECISAO_JUDICIAL).append("'");
        corpoBuilder.append(" left outer join ade.dadosAutorizacaoDescontoSet dad87 with dad87.tipoDadoAdicional.tdaCodigo='").append(CodedValues.TDA_MARGEM_LIMITE_DECISAO_JUDICIAL).append("'");

        if (!TextHelper.isNull(tpsCodigo)) {
            corpoBuilder.append(" left outer join svc.paramSvcConsignanteSet pseXX with ");
            corpoBuilder.append(" pseXX.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigo));
        }

        if ("comprar".equalsIgnoreCase(tipoOperacao) || "retirar_contrato_compra".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" WHERE 1=1");
        } else if (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipo) || AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipo)) {
            corpoBuilder.append(" WHERE cse.cseCodigo = :codigoEntidade");
        } else if (AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(tipo)) {
            if (!TextHelper.isNull(codigo)) {
                corpoBuilder.append(" WHERE csa.csaCodigo = :codigoEntidade");
            } else {
                corpoBuilder.append(" WHERE 1=1");
            }
        } else if (AcessoSistema.ENTIDADE_COR.equalsIgnoreCase(tipo)) {
            corpoBuilder.append(" WHERE ade.correspondente.corCodigo = :codigoEntidade");
            corpoBuilder.append(" AND cor.consignataria.csaCodigo = csa.csaCodigo ");
        } else if (AcessoSistema.ENTIDADE_SER.equalsIgnoreCase(tipo)) {
            corpoBuilder.append(" WHERE usr.usuario.usuCodigo = :codigoEntidade");
            corpoBuilder.append(" AND (pse64.pseCodigo IS NULL OR pse64.pseVlr='").append(CodedValues.PSE_EXIBIR_TODOS_CONTRATOS_SERVIDOR).append("'");
            corpoBuilder.append(" OR (pse64.pseVlr='").append(CodedValues.PSE_EXIBIR_SOMENTE_CONTRATOS_ATIVOS_SERVIDOR).append("'");
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SAD_CODIGOS_INATIVOS, "' , '")).append("')))");
        } else if (AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipo)) {
            corpoBuilder.append(" WHERE org.orgCodigo = :codigoEntidade");
        } else if (AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipo)) {
            corpoBuilder.append(" WHERE est.estCodigo = :codigoEntidade");
        } else {
            throw new HQueryException("mensagem.erroInternoSistema", responsavel);
        }

        if (responsavel.isSer() && "reativar".equalsIgnoreCase(tipoOperacao)) {
            throw new HQueryException("mensagem.erroInternoSistema", responsavel);
        }

        // Cláusula de ligação do órgão ao registro servidor, apesar de redundante resolve questões de performance
        // na construção do plano de execução no mysql
        corpoBuilder.append(" and rse.orgao.orgCodigo = org.orgCodigo ");

        // Servidores excluídos não serão listados na pesquisa
        if (ParamSist.paramEquals(CodedValues.TPC_IGNORA_SERVIDORES_EXCLUIDOS, CodedValues.TPC_SIM, responsavel)) {
            corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        // DESENV-20106 : Omitir servidores que não aceitaram o termo de uso quando a pesquisa é feita por CSA/COR
        corpoBuilder.append(ListaServidorQuery.gerarClausulaServidorComTermoUso(responsavel));

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }
        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        }
        if ((adeNumero != null) && (adeNumero.size() > 0)) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }
        if ((adeIdentificador != null) && (adeIdentificador.size() > 0)) {
            corpoBuilder.append(" AND ade.adeIdentificador ").append(criaClausulaNomeada("adeIdentificador", adeIdentificador));
        }
        if ((sadCodigos != null) && (sadCodigos.size() > 0)) {
            corpoBuilder.append(" AND ");

            if ("liquidar".equalsIgnoreCase(tipoOperacao)) {
                corpoBuilder.append("(");
            }

            corpoBuilder.append(" ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));

            if ("liquidar".equalsIgnoreCase(tipoOperacao)) {
                // or exists (select 1 from tb_param_svc_consignante pse227 where svc.svc_codigo = pse227.svc_codigo and pse227.tps_codigo = '227' and pse227.pse_vlr = '1' and ade.sad_codigo IN ('6', '10'))
                corpoBuilder.append(" OR EXISTS (SELECT 1 FROM svc.paramSvcConsignanteSet pse227 WHERE ");
                corpoBuilder.append(" pse227.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PERMITE_LIQUIDAR_ADE_SUSPENSA).append("' AND pse227.pseVlr = '1' ");
                corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SAD_CODIGOS_SUSPENSOS, "' , '")).append("')) ");
                corpoBuilder.append(")");
            }
        }
        if ((svcCodigos != null) && (svcCodigos.size() > 0)) {
            corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }
        if (!TextHelper.isNull(svcCodigo) && !"comprar".equalsIgnoreCase(tipoOperacao)
                && !"renegociar".equalsIgnoreCase(tipoOperacao) && !"simular_renegociacao".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if (!TextHelper.isNull(csaCodigo) && !"comprar".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if ((csaCodigos != null) && (csaCodigos.size() > 0)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigos", csaCodigos));
        }
        if ((tntCodigos != null) && (tntCodigos.size() > 0)) {
            corpoBuilder.append(" AND tla.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigos", tntCodigos));
        }

        if (responsavel.isCor()) {
            // Se o responsável é correspondente (com ou sem permissão de 'Acessar Consignações da Consignatária')
            if ("comprar".equalsIgnoreCase(tipoOperacao)) {
                // Se é compra de contrato para usuário de COR com ou sem opção de 'Acessar Consignações da Consignatária',
                // então lista os contratos onde o correspondente possui convênio, junto com os
                // contratos das outras consignatárias
                corpoBuilder.append(" AND (crc.convenio.cnvCodigo IS NOT NULL OR ");
                corpoBuilder.append(" cnv.consignataria.csaCodigo <> (select c.consignataria.csaCodigo from Correspondente c where c.corCodigo = :correspondente))");
            } else if (!"retirar_contrato_compra".equalsIgnoreCase(tipoOperacao)) {
                // Para as demais operações, exceto retirar_contrato_compra, os usuários de correspondente
                // só podem consultar consignações de convênios que estão ativos para ele.
                corpoBuilder.append(" AND (crc.convenio.cnvCodigo IS NOT NULL)");
            }
        }

        if ("solicitar_portabilidade".equalsIgnoreCase(tipoOperacao)) {
            // DESENV-17933 : Que o serviço permite portabilidade/compra
            corpoBuilder.append(" AND EXISTS (SELECT 1 FROM svc.relacionamentoServicoByDestinoSet rsv WHERE ");
            corpoBuilder.append(" rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_COMPRA).append("'");
            corpoBuilder.append(") ");
            // DESENV-17933 : Que a consignação não possui relacionamento de solicitação de portabilidade aberto
            corpoBuilder.append(" AND NOT EXISTS (select 1 from ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rad");
            corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_SOLICITACAO_PORTABILIDADE).append("'");
            corpoBuilder.append(")");
            if (!ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_PORTABILIDADE_CARTAO, responsavel)) {
                corpoBuilder.append(" AND svc.nseCodigo != '").append(CodedValues.NSE_CARTAO).append("' ");
            }
        } else if ("cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" AND csa.csaCodigo = prm.consignataria.csaCodigo ");
            corpoBuilder.append(" AND prm.prmCodigo = din.permissionario.prmCodigo ");
            if (planoExclusaoAutomatica) {
                corpoBuilder.append(" AND (ppl.pplValor IS NULL OR ppl.pplValor <> '").append(CodedValues.TPP_NAO).append("') ");
            }
        } else if ("retirar_contrato_compra".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" AND adeDestino.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_CONF).append("'");
            if (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipo) || AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND cseDestino.cseCodigo = :codigoEntidade");
            } else if (AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND csaDestino.csaCodigo = :codigoEntidade");
            } else if (AcessoSistema.ENTIDADE_COR.equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND adeDestino.correspondente.corCodigo = :codigoEntidade");
            } else if (AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND orgDestino.orgCodigo = :codigoEntidade");
            } else if (AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND estDestino.estCodigo = :codigoEntidade");
            } else {
                throw new HQueryException("mensagem.erroInternoSistema", responsavel);
            }
        } else if ("confirmar_liquidacao".equalsIgnoreCase(tipoOperacao)) {
            // Operação de confirmação de liquidação deve listar contratos na situação Aguard. Liquidação que não sejam origem
            // de processo de renegociação pendente, ou seja o destino esteja Aguard. Confirmação ou Aguard. Deferimento
            corpoBuilder.append(" AND NOT EXISTS (select 1 from ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rad");
            corpoBuilder.append(" inner join rad.autDescontoByAdeCodigoDestino adeDestino");
            corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");
            corpoBuilder.append(" and adeDestino.statusAutorizacaoDesconto.sadCodigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_AGUARD_CONF, "','")).append("')");
            corpoBuilder.append(")");
        } else if ("solicitacao".equalsIgnoreCase(tipoOperacao) && temAnexoPendenteValidacao) {
            corpoBuilder.append(" AND EXISTS (select 1 from ade.solicitacaoAutorizacaoSet sso");
            corpoBuilder.append(" where sso.statusSolicitacao.id = '").append(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo()).append("'");
            corpoBuilder.append(")");
        } else if ("confirmar".equalsIgnoreCase(tipoOperacao)) {
            //DESENV-14162 - pesquisas de ades para confirmar reserva não retornam mais aqueles que são destinos de renegociação/compra/alongamento.
            //               Estas serão retornadas nas pesquisas de confirmar renegociação.
            corpoBuilder.append(" AND NOT EXISTS (select 1 from ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad");
            corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");
            corpoBuilder.append(" or rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
            corpoBuilder.append(")");
        } else if ("confirmar_renegociacao".equalsIgnoreCase(tipoOperacao)) {
            //DESENV-14162 - pesquisas de ades para confirmar renegociação retornam apenas aqueles que são destinos de renegociação/compra/alongamento.
            corpoBuilder.append(" AND EXISTS (select 1 from ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad");
            corpoBuilder.append(" where rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("'");
            corpoBuilder.append(" or rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
            corpoBuilder.append(")");
        }

        // Adiciona a query os critérios da busca avançada, apenas se não for compra
        // , renegociação ou retirada de contrato de compra (São operações que não utilizam a busca avançada)
        if (!"comprar".equalsIgnoreCase(tipoOperacao) && !"renegociar".equalsIgnoreCase(tipoOperacao) && !"retirar_contrato_compra".equalsIgnoreCase(tipoOperacao) && !"simular_renegociacao".equalsIgnoreCase(tipoOperacao) && !"solicitar_portabilidade".equalsIgnoreCase(tipoOperacao)) {
            if (!TextHelper.isNull(tgcCodigo)) {
                corpoBuilder.append(" AND csa.tipoGrupoConsignataria.tgcCodigo ").append(criaClausulaNomeada("tgcCodigo", tgcCodigo));
            }
            if (!TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            }
            if (!TextHelper.isNull(corCodigo)) {
                corpoBuilder.append(" AND ade.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
            }
            if (!TextHelper.isNull(estCodigo) && (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipo) || AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipo))) {
                corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
            }
            if (!TextHelper.isNull(orgCodigo)) {
                corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
            }
            if (!TextHelper.isNull(tgsCodigo)) {
                corpoBuilder.append(" AND svc.tipoGrupoSvc.tgsCodigo ").append(criaClausulaNomeada("tgsCodigo", tgsCodigo));
            }
            if (!TextHelper.isNull(svcCodigo)) {
                corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
            }
            if (!TextHelper.isNull(cnvCodVerba)) {
                corpoBuilder.append(" AND cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
            }
            if (!TextHelper.isNull(tmoCodigo)) {
                corpoBuilder.append(" AND EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet ocaTmo ");
                corpoBuilder.append(" WHERE ocaTmo.tipoMotivoOperacao.tmoCodigo ").append(criaClausulaNomeada("tmoCodigo", tmoCodigo));
                if (adeAnoMesIni != null) {
                    corpoBuilder.append(" AND ocaTmo.ocaPeriodo = :paramAdeAnoMesIni");
                }
                corpoBuilder.append(")");
            }
            if (adeAnoMesIni != null) {
                if (TextHelper.isNull(tipoOcorrenciaPeriodo) || "inclusao".equals(tipoOcorrenciaPeriodo)) {
                    // verifica contratos incluídos para o período informado
                    corpoBuilder.append(" AND ade.adeAnoMesIni = :paramAdeAnoMesIni ");
                } else if ("alteracao".equals(tipoOcorrenciaPeriodo)) {
                    // verifica a existência de ocorrência de alteração no período informado
                    corpoBuilder.append(" AND EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet oca ");
                    corpoBuilder.append(" WHERE oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("'");
                    corpoBuilder.append(" AND oca.ocaPeriodo = :paramAdeAnoMesIni)");
                } else if ("suspensao".equals(tipoOcorrenciaPeriodo)) {
                    // verifica a existência de ocorrência de suspensão no período informado
                    corpoBuilder.append(" AND EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet oca ");
                    corpoBuilder.append(" WHERE oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_SUSPENSAO_CONTRATO).append("'");
                    corpoBuilder.append(" AND oca.ocaPeriodo = :paramAdeAnoMesIni)");
                }
            }
            if (periodoIni != null) {
                corpoBuilder.append(" AND ade.adeData >= :paramPeriodoIni ");
            }
            if (periodoFim != null) {
                corpoBuilder.append(" AND ade.adeData <= :paramPeriodoFim ");
            }
            if (adeIntFolha != null) {
                corpoBuilder.append(" AND ade.adeIntFolha ").append(criaClausulaNomeada("adeIntFolha", adeIntFolha));
            }
            if (adeIncMargem != null) {
                corpoBuilder.append(" AND ade.adeIncMargem ").append(criaClausulaNomeada("adeIncMargem", adeIncMargem));
            }
            if (!TextHelper.isNull(adeIndice)) {
                corpoBuilder.append(" AND ade.adeIndice ").append(criaClausulaNomeada("adeIndice", adeIndice));
            }
            if ((srsCodigo != null) && (srsCodigo.size() > 0)) {
                corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigo", srsCodigo));
            }

            if (!TextHelper.isNull(infSaldoDevedor) && ((listaTipoSolicitacaoSaldo == null) || listaTipoSolicitacaoSaldo.isEmpty())) {
                /**
                 * Opções do parâmetro:
                 * infSaldoDevedor = bloq -> Listagem de bloqueios (informação, liquidação exclusao que já expiraram)
                 * infSaldoDevedor = sdv  -> Listagem de solicitação de saldo p/ informação
                 * infSaldoDevedor = liq  -> Listagem de solicitação de saldo p/ liquidação
                 * infSaldoDevedor = exc  -> Listagem de solicitação de saldo p/ exclusão de servidor
                 * infSaldoDevedor = 1    -> Pesquisa avançada: Foi solicitado pelo servidor
                 * infSaldoDevedor = 2    -> Pesquisa avançada: Foi solicitado pelo servidor, mas ainda não foi cadastrado
                 * infSaldoDevedor = 3    -> Pesquisa avançada: Foi solicitado pelo servidor e já foi cadastrado
                 * infSaldoDevedor = 4    -> Pesquisa avançada: Ainda não foi solicitado pelo servidor
                 */

                corpoBuilder.append(" AND ").append("4".equals(infSaldoDevedor) ? "NOT EXISTS" : "EXISTS").append(" (");
                corpoBuilder.append(" SELECT 1 FROM ade.solicitacaoAutorizacaoSet soa");
                corpoBuilder.append(" WHERE soa.tipoSolicitacao.tisCodigo");

                if ("sdv".equals(infSaldoDevedor)) {
                    corpoBuilder.append(" = '").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR).append("'");
                } else if ("liq".equals(infSaldoDevedor) && (diasSolicitacaoSaldoPagaAnexo >= 0)) {
                    corpoBuilder.append(" = '").append(TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO).append("'");
                } else if ("liq".equals(infSaldoDevedor) && (diasSolicitacaoSaldo >= 0)) {
                    corpoBuilder.append(" = '").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO).append("'");
                } else if ("liq".equals(infSaldoDevedor)) {
                    corpoBuilder.append(" IN ('").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO).append("',");
                    corpoBuilder.append(" '").append(TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO).append("')");
                } else if ("exc".equals(infSaldoDevedor) && temModuloSaldoDevedorExclusao) {
                    corpoBuilder.append(" = '").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO).append("'");
                } else {
                    corpoBuilder.append(" IN ('").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR).append("',");
                    corpoBuilder.append(" '").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO).append("',");
                    corpoBuilder.append(" '").append(TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO).append("'");
                    if ("bloq".equals(infSaldoDevedor) && temModuloSaldoDevedorExclusao) {
                        corpoBuilder.append(" ,'").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO).append("'");
                    }
                    corpoBuilder.append(" )");
                }

                if ("1".equals(infSaldoDevedor) || "4".equals(infSaldoDevedor)) {
                    corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo IN ('").append(StatusSolicitacaoEnum.PENDENTE).append("',");
                    corpoBuilder.append(" '").append(StatusSolicitacaoEnum.FINALIZADA).append("')");
                } else if ("2".equals(infSaldoDevedor) || "sdv".equals(infSaldoDevedor) || "liq".equals(infSaldoDevedor) || "bloq".equals(infSaldoDevedor) || "exc".equals(infSaldoDevedor)) {
                    corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo = '").append(StatusSolicitacaoEnum.PENDENTE).append("'");

                    if (((diasSolicitacaoSaldo >= 0) && (diasSolicitacaoSaldoPagaAnexo >= 0)) || ((diasSolicitacaoSaldo >= 0) && "bloq".equals(infSaldoDevedor) && temModuloSaldoDevedorExclusao)) {
                        corpoBuilder.append(" AND ((soa.tipoSolicitacao.tisCodigo IN ('");
                        corpoBuilder.append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR).append("','");
                        corpoBuilder.append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO).append("') ");
                    }

                    if (diasSolicitacaoSaldo >= 0) {
                        corpoBuilder.append(" AND (");
                        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_SOLICIT_SALDO_DEVEDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                            corpoBuilder.append("(SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(soa.soaData) and current_date()) > ");
                        } else {
                            corpoBuilder.append("(TO_DAYS(CURRENT_DATE()) - TO_DAYS(soa.soaData)) >= ");
                        }
                        corpoBuilder.append("(SELECT to_numeric(coalesce(nullif(trim(pse161.pseVlr), ''), '99999')) - :diasSolicitacaoSaldo FROM ParamSvcConsignante pse161 ");
                        corpoBuilder.append("WHERE pse161.servico.svcCodigo = svc.svcCodigo ");
                        corpoBuilder.append("AND pse161.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PRAZO_ATEND_SOLICIT_SALDO_DEVEDOR).append("') ");
                        corpoBuilder.append(")");
                    }

                    if ((diasSolicitacaoSaldo >= 0) && (diasSolicitacaoSaldoPagaAnexo >= 0)) {
                        corpoBuilder.append(" ) OR (soa.tipoSolicitacao.tisCodigo = '");
                        corpoBuilder.append(TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO).append("'");
                    }

                    // Lista solicitação de liquidação de contrato com saldo pago e anexo e não liquidado
                    if (diasSolicitacaoSaldoPagaAnexo >= 0) {
                        corpoBuilder.append(" AND (");
                        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_ENTRE_COMP_SALDO_LIQ_ADE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                            corpoBuilder.append("(SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(soa.soaData) and current_date()) > ");
                        } else {
                            corpoBuilder.append("(TO_DAYS(CURRENT_DATE()) - TO_DAYS(soa.soaData)) >= ");
                        }
                        corpoBuilder.append("(SELECT to_numeric(coalesce(nullif(trim(pse221.pseVlr), ''), '99999')) - :diasSolicitacaoSaldoPagaAnexo FROM ParamSvcConsignante pse221 ");
                        corpoBuilder.append("WHERE pse221.servico.svcCodigo = svc.svcCodigo ");
                        corpoBuilder.append("AND pse221.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_QTD_DIAS_BLOQ_CSA_APOS_INF_SALDO_SER).append("') ");
                        corpoBuilder.append(")");
                    }

                    if ((diasSolicitacaoSaldo >= 0) && "bloq".equals(infSaldoDevedor) && temModuloSaldoDevedorExclusao) {
                        corpoBuilder.append(" ) OR (soa.tipoSolicitacao.tisCodigo = '");
                        corpoBuilder.append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO).append("'");
                    }

                    // Lista solicitação de saldo devedor para exclusão de servidor
                    if ("bloq".equals(infSaldoDevedor) && temModuloSaldoDevedorExclusao) {
                        corpoBuilder.append(" AND (");
                        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_SOLICIT_SALDO_DEVEDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                            corpoBuilder.append("(SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(soa.soaData) and current_date()) > ");
                        } else {
                            corpoBuilder.append("(TO_DAYS(CURRENT_DATE()) - TO_DAYS(soa.soaData)) >= ");
                        }
                        corpoBuilder.append("(SELECT to_numeric(coalesce(nullif(trim(pse223.pseVlr), ''), '99999')) - :diasSolicitacaoSaldo FROM ParamSvcConsignante pse223 ");
                        corpoBuilder.append("WHERE pse223.servico.svcCodigo = svc.svcCodigo ");
                        corpoBuilder.append("AND pse223.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PRAZO_ATEND_SALDO_DEVEDOR_EXCLUSAO).append("') ");
                        corpoBuilder.append(")");
                    }

                    if (((diasSolicitacaoSaldo >= 0) && (diasSolicitacaoSaldoPagaAnexo >= 0)) || ((diasSolicitacaoSaldo >= 0) && "bloq".equals(infSaldoDevedor) && temModuloSaldoDevedorExclusao)) {
                        corpoBuilder.append(" ))");
                    }

                } else if ("3".equals(infSaldoDevedor)) {
                    corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo = '").append(StatusSolicitacaoEnum.FINALIZADA).append("'");
                }

                if (ocaDataIni != null) {
                    corpoBuilder.append(" AND soa.soaData >= :ocaDataIni ");
                }
                if (ocaDataFim != null) {
                    corpoBuilder.append(" AND soa.soaData <= :ocaDataFim ");
                }

                corpoBuilder.append(")");
            } else if (TextHelper.isNull(infSaldoDevedor) && (listaTipoSolicitacaoSaldo != null) && !listaTipoSolicitacaoSaldo.isEmpty()) {
                corpoBuilder.append(" AND ").append("4".equals(infSaldoDevedor) ? "NOT EXISTS" : "EXISTS").append(" (");
                corpoBuilder.append(" SELECT 1 FROM ade.solicitacaoAutorizacaoSet soa");
                corpoBuilder.append(" WHERE soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", listaTipoSolicitacaoSaldo));

                if (operacaoSOAPEditarSaldoDevedor) {
                    corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo = '").append(StatusSolicitacaoEnum.PENDENTE).append("'");
                }

                if (ocaDataIni != null) {
                    corpoBuilder.append(" AND soa.soaData >= :ocaDataIni ");
                }
                if (ocaDataFim != null) {
                    corpoBuilder.append(" AND soa.soaData <= :ocaDataFim ");
                }

                corpoBuilder.append(")");
            }
        }

        if (!TextHelper.isNull(tpsCodigo)) {
            // Faz a restrição pelo parâmetro de servico
            corpoBuilder.append(" AND (pseXX.pseVlr IS NULL OR pseXX.pseVlr = '1')");

        }

        if ("cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao) && !TextHelper.isNull(decCodigo)) {
            corpoBuilder.append(" AND dec.decCodigo ").append(criaClausulaNomeada("decCodigo", decCodigo));
        }

        if ("cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao) && !TextHelper.isNull(prmCodigo)) {
            corpoBuilder.append(" AND prm.prmCodigo ").append(criaClausulaNomeada("prmCodigo", prmCodigo));
        }

        if (transferencia) {
            corpoBuilder.append(" AND nse.nseTransferirAde = '").append(CodedValues.TPC_SIM).append("'");
        }

        if (dataConciliacao != null) {
            corpoBuilder.append(" AND (ade.adeDataUltConciliacao ").append(criaClausulaNomeada("dataConciliacao", dataConciliacao));
            corpoBuilder.append(" OR ade.adeDataUltConciliacao IS NULL").append(")");
        }

        if (((("cancelar".equalsIgnoreCase(tipoOperacao) || "consultar".equalsIgnoreCase(tipoOperacao)) && adePropria) ||
                "cancelarminhas".equalsIgnoreCase(tipoOperacao)) && responsavel.isCsaCor()) {
            corpoBuilder.append(" AND usu.usuCodigo = :usuarioCancelamento");
        }

        if ((nseCodigos != null) && !nseCodigos.isEmpty()) {
            corpoBuilder.append(" AND svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigos", nseCodigos));
        }

        if ((marCodigos != null) && !marCodigos.isEmpty()) {
            // Cláusula para pegar qualquer incidência de margem
            if (marCodigos.contains(CodedValues.INCIDE_MARGEM_QQ)) {
                corpoBuilder.append(" AND ade.adeIncMargem <> ").append(CodedValues.INCIDE_MARGEM_NAO);
            } else {
                corpoBuilder.append(" AND ade.adeIncMargem ").append(criaClausulaNomeada("marCodigos", marCodigos));
            }
        }

        if ("ajustar_consignacoes_margem".equals(tipoOperacao)) {
            corpoBuilder.append(" AND (svc.naturezaServico.nseCodigo ='").append(CodedValues.NSE_EMPRESTIMO).append("'");
            corpoBuilder.append(" OR (svc.naturezaServico.nseCodigo ='").append(CodedValues.NSE_CARTAO).append("'");
            corpoBuilder.append(" AND ade.adeIntFolha= ").append(CodedValues.INTEGRA_FOLHA_NAO).append(")) ");
        }

        if ((existeTipoOcorrencias != null) && !existeTipoOcorrencias.isEmpty()) {
            corpoBuilder.append(" AND EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet oca ");
            corpoBuilder.append(" WHERE oca.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigos", existeTipoOcorrencias));
            corpoBuilder.append(" )");
        }

        if (validarInexistenciaConfLiquidacao) {
            corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet ocaConfLiq");
            corpoBuilder.append(" WHERE ocaConfLiq.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_CONFIRMACAO_LIQUIDACAO_ADE).append("'");
            corpoBuilder.append(" AND ocaConfLiq.usuario.usuCodigo = :usuarioOcaConfLiq)");
        } else if (validarInexistenciaConfLiquidacaoRelacionamento) {
            corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad");
            corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem");
            corpoBuilder.append(" INNER JOIN adeOrigem.ocorrenciaAutorizacaoSet ocaConfLiq");
            corpoBuilder.append(" WHERE rad.tipoNatureza.tntCodigo IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("', '").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
            corpoBuilder.append(" AND ocaConfLiq.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_CONFIRMACAO_LIQUIDACAO_ADE).append("'");
            corpoBuilder.append(" AND ocaConfLiq.usuario.usuCodigo = :usuarioOcaConfLiq)");
        }

        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCpf, true));

        if (!count) {
            if (TextHelper.isNull(tipoOrdenacao) || "1".equals(tipoOrdenacao)) {
                if ("deferir".equalsIgnoreCase(tipoOperacao) || "indeferir".equalsIgnoreCase(tipoOperacao)) {
                    corpoBuilder.append(" ORDER BY ade.adeData, ade.adeCodigo");
                } else if (TextHelper.isNull(tipoOrdenacao) && !TextHelper.isNull(ordenacao)) {
                    corpoBuilder.append(" ORDER BY ").append(recuperaOrdenacao());
                } else {
                    corpoBuilder.append(" ORDER BY ade.adeData DESC, ade.adeCodigo DESC");
                }

            } else if ("2".equals(tipoOrdenacao)) {
                corpoBuilder.append(" ORDER BY");
                corpoBuilder.append(" to_numeric(COALESCE(svc.svcPrioridade, '9999999')),");
                corpoBuilder.append(" to_numeric(COALESCE(cnv.cnvPrioridade, 9999999)),");
                corpoBuilder.append(" COALESCE(ade.adeAnoMesIniRef, ade.adeAnoMesIni),");
                corpoBuilder.append(" COALESCE(ade.adeDataRef, ade.adeData),");
                corpoBuilder.append(" ade.adeNumero");

            } else if ("3".equals(tipoOrdenacao)) {
                corpoBuilder.append(" ORDER BY");
                corpoBuilder.append(" to_numeric(COALESCE(svc.svcPrioridade, '9999999')) DESC,");
                corpoBuilder.append(" to_numeric(COALESCE(cnv.cnvPrioridade, 9999999)) DESC,");
                corpoBuilder.append(" COALESCE(ade.adeAnoMesIniRef, ade.adeAnoMesIni) DESC,");
                corpoBuilder.append(" COALESCE(ade.adeDataRef, ade.adeData) DESC,");
                corpoBuilder.append(" ade.adeNumero DESC");

            } else if ("4".equals(tipoOrdenacao) && !TextHelper.isNull(ordenacao)) {
                corpoBuilder.append(" ORDER BY ").append(recuperaOrdenacao());

            } else {
                corpoBuilder.append(" ORDER BY ade.adeData DESC, ade.adeCodigo DESC");
            }
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, true, query);

        if (!TextHelper.isNull(tpsCodigo)) {
            defineValorClausulaNomeada("tpsCodigo", tpsCodigo, query);
        }
        if (diasSolicitacaoSaldo >= 0) {
            defineValorClausulaNomeada("diasSolicitacaoSaldo", Integer.toString(diasSolicitacaoSaldo) , query);
        }
        if (diasSolicitacaoSaldoPagaAnexo >= 0) {
            defineValorClausulaNomeada("diasSolicitacaoSaldoPagaAnexo", Integer.toString(diasSolicitacaoSaldoPagaAnexo) , query);
        }
        if (!"comprar".equalsIgnoreCase(tipoOperacao) && !TextHelper.isNull(codigo)) {
            defineValorClausulaNomeada("codigoEntidade", codigo, query);
        }
        if (periodoIni != null) {
            defineValorClausulaNomeada("paramPeriodoIni", periodoIni, query);
        }
        if (periodoFim != null) {
            defineValorClausulaNomeada("paramPeriodoFim", periodoFim, query);
        }
        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if ((srsCodigo != null) && (srsCodigo.size() > 0)) {
            defineValorClausulaNomeada("srsCodigo", srsCodigo, query);
        }
        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }
        if ((adeNumero != null) && (adeNumero.size() > 0)) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }
        if ((adeIdentificador != null) && (adeIdentificador.size() > 0)) {
            defineValorClausulaNomeada("adeIdentificador", adeIdentificador, query);
        }
        if (adeIntFolha != null) {
            defineValorClausulaNomeada("adeIntFolha", adeIntFolha, query);
        }
        if (adeIncMargem != null) {
            defineValorClausulaNomeada("adeIncMargem", adeIncMargem, query);
        }
        if (!TextHelper.isNull(adeIndice)) {
            defineValorClausulaNomeada("adeIndice", adeIndice, query);
        }
        if (adeAnoMesIni != null) {
            defineValorClausulaNomeada("paramAdeAnoMesIni", adeAnoMesIni, query);
        }
        if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }
        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }
        if (!TextHelper.isNull(tmoCodigo)) {
            defineValorClausulaNomeada("tmoCodigo", tmoCodigo, query);
        }
        if (!TextHelper.isNull(tgcCodigo)) {
            defineValorClausulaNomeada("tgcCodigo", tgcCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo) && !"comprar".equalsIgnoreCase(tipoOperacao)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(corCodigo) && !"comprar".equalsIgnoreCase(tipoOperacao) && !"renegociar".equalsIgnoreCase(tipoOperacao) && !"retirar_contrato_compra".equalsIgnoreCase(tipoOperacao) && ! "simular_renegociacao".equalsIgnoreCase(tipoOperacao)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }
        if (!TextHelper.isNull(estCodigo) && (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipo) || AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipo))) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(tgsCodigo)) {
            defineValorClausulaNomeada("tgsCodigo", tgsCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if ((svcCodigos != null) && (svcCodigos.size() > 0)) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }
        if ((csaCodigos != null) && (csaCodigos.size() > 0)) {
            defineValorClausulaNomeada("csaCodigos", csaCodigos, query);
        }
        if ("cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao) && !TextHelper.isNull(decCodigo)) {
            defineValorClausulaNomeada("decCodigo", decCodigo, query);
        }
        if ("cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao) && !TextHelper.isNull(prmCodigo)) {
            defineValorClausulaNomeada("prmCodigo", prmCodigo, query);
        }
        if (dataConciliacao != null) {
            defineValorClausulaNomeada("dataConciliacao", dataConciliacao, query);
        }
        if ((nseCodigos != null) && !nseCodigos.isEmpty()) {
            defineValorClausulaNomeada("nseCodigos", nseCodigos, query);
        }
        if ((marCodigos != null) && !marCodigos.isEmpty() && !marCodigos.contains(CodedValues.INCIDE_MARGEM_QQ)) {
            defineValorClausulaNomeada("marCodigos", marCodigos, query);
        }
        if (!TextHelper.isNull(infSaldoDevedor) || ((listaTipoSolicitacaoSaldo != null) && !listaTipoSolicitacaoSaldo.isEmpty())) {
            if ((listaTipoSolicitacaoSaldo != null) && !listaTipoSolicitacaoSaldo.isEmpty()) {
                defineValorClausulaNomeada("tisCodigo", listaTipoSolicitacaoSaldo, query);
            }
            if (ocaDataIni != null) {
                defineValorClausulaNomeada("ocaDataIni", ocaDataIni, query);
            }
            if (ocaDataFim != null) {
                defineValorClausulaNomeada("ocaDataFim", ocaDataFim, query);
            }
        }
        if ("solicitacao".equalsIgnoreCase(tipoOperacao) && responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, CodedValues.TPC_SIM, responsavel)){
            defineValorClausulaNomeada("csaCodigoArr", responsavel.getCsaCodigo(), query);
        }
        if ((tntCodigos != null) && (tntCodigos.size() > 0)) {
            defineValorClausulaNomeada("tntCodigos", tntCodigos, query);
        }
        if ((existeTipoOcorrencias != null) && !existeTipoOcorrencias.isEmpty()) {
            defineValorClausulaNomeada("tocCodigos", existeTipoOcorrencias, query);
        }
        if (validarInexistenciaConfLiquidacao || validarInexistenciaConfLiquidacaoRelacionamento) {
            defineValorClausulaNomeada("usuarioOcaConfLiq", responsavel.getUsuCodigo(), query);
        }
        if (((("cancelar".equalsIgnoreCase(tipoOperacao) || "consultar".equalsIgnoreCase(tipoOperacao)) && adePropria) ||
                "cancelarminhas".equalsIgnoreCase(tipoOperacao)) && responsavel.isCsaCor()) {
            defineValorClausulaNomeada("usuarioCancelamento", responsavel.getUsuCodigo(), query);
        }
        if (responsavel.isCor() && !"retirar_contrato_compra".equalsIgnoreCase(tipoOperacao)) {
            defineValorClausulaNomeada("correspondente", responsavel.getCodigoEntidade(), query);
        }


        return query;
    }

    private String recuperaOrdenacao() {
        String order = "";

        final Map<String, String> ordenacoesPossiveis = new HashMap<>();
        ordenacoesPossiveis.put("ORD01", "ade.adeData");
        ordenacoesPossiveis.put("ORD02", "ser.serCpf");
        ordenacoesPossiveis.put("ORD03", "rse.rseMatricula");
        ordenacoesPossiveis.put("ORD04", "ser.serNome");

        List<String> lstOrdenacaoAux = new ArrayList<>();
        if (!TextHelper.isNull(ordenacao)) {
            lstOrdenacaoAux = Arrays.asList(ordenacao.split(","));
        }

        if ((lstOrdenacaoAux != null) && !lstOrdenacaoAux.isEmpty()) {
            int contador = 0;
            for (String ordernacaoAux : lstOrdenacaoAux) {
                ordernacaoAux = ordernacaoAux.replaceAll("\\[|\\]", "").trim();

                for (final String chave : ordenacoesPossiveis.keySet()) {
                    if ((chave + ";" + ASC).equalsIgnoreCase(ordernacaoAux)) {
                        order += ordenacoesPossiveis.get(chave) + " " + ASC;
                    } else if ((chave + ";" + DESC).equalsIgnoreCase(ordernacaoAux)) {
                        order += ordenacoesPossiveis.get(chave) + " " + DESC;
                    }
                }

                if (contador < (lstOrdenacaoAux.size() - 1)) {
                    contador++;
                    order += ", ";
                }
            }
        }

        while (order.trim().endsWith(",")) {
            order = order.substring(0, order.lastIndexOf(","));
        }

        return order;
    }

    @Override
    protected String[] getFields() {
        String[] fields = {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_DATA,
                Columns.ADE_VLR,
                Columns.ADE_VLR_FOLHA,
                Columns.ADE_VLR_LIQUIDO,
                Columns.ADE_TAXA_JUROS,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_INDICE,
                Columns.ADE_COD_REG,
                Columns.ADE_INC_MARGEM,
                Columns.ADE_INT_FOLHA,
                Columns.ADE_CARENCIA,
                Columns.ADE_VLR_PERCENTUAL,
                Columns.ADE_VLR_PARCELA_FOLHA,
                Columns.ADE_DATA_STATUS,
                Columns.ADE_PERIODICIDADE,
                Columns.SAD_CODIGO,
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
                Columns.RSE_CODIGO,
                Columns.RSE_ORG_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.RSE_SALARIO,
                Columns.RSE_DATA_CARGA,
                Columns.RSE_DATA_ALTERACAO,
                Columns.RSE_BASE_CALCULO,
                Columns.RSE_PRAZO,
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SER_TEL,
                Columns.SER_EMAIL,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.SVC_NSE_CODIGO,
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.CNV_CSA_CODIGO,
                Columns.ORG_CODIGO,
                Columns.CFT_VLR,
                Columns.CDE_VLR_LIBERADO,
                Columns.CDE_VLR_LIBERADO_CALC,
                Columns.SDV_VALOR,
                Columns.SDV_DATA_MOD,
                Columns.COR_CODIGO,
                Columns.COR_NOME,
                Columns.COR_IDENTIFICADOR,
                Columns.DAD_VALOR + CodedValues.TDA_AFETADA_DECISAO_JUDICIAL,
                Columns.DAD_VALOR + CodedValues.TDA_MARGEM_LIMITE_DECISAO_JUDICIAL,
                Columns.ADE_DATA_NOTIFICACAO_CSE,
                Columns.ADE_DATA_LIBERACAO_VALOR,
                "SVC_PRIORIDADE",
                "CNV_PRIORIDADE",
                "ADE_ANO_MES_INI_PRIORIDADE",
                "ADE_DATA_PRIORIDADE",
                "ADE_NUMERO_PRIORIDADE"
        };

        if("consultar".equalsIgnoreCase(tipoOperacao) &&
                (AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipo) ||
                 AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipo) ||
                 AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipo) ||
                 AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipo))) {
            final String[] fields5 = Arrays.copyOf(fields, fields.length + 1);
            fields5[fields.length] = "POSSUI_DECISAO_JUDICIAL";
            return fields5;
        }

        if (usaModuloBeneficio) {
            final String[] fields2 = Arrays.copyOf(fields, fields.length + 2);
            fields2[fields.length] = Columns.CBE_NUMERO;
            fields2[fields.length + 1] = Columns.BFC_CPF;
            fields = Arrays.copyOf(fields2, fields2.length);
        }

        if ("confirmar_liquidacao".equalsIgnoreCase(tipoOperacao) && !AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(tipo)) {
            final String[] fields2 = Arrays.copyOf(fields, fields.length + 1);
            fields2[fields.length] = "VRF_SOLICITACAO_LIQUIDACAO";
            return fields2;
        }

        if (retornaSomenteAdeCodigo) {
            return new String[] {Columns.ADE_CODIGO};
        } else if (AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(tipo)) {
            if ("cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao)) {
                // Se tipo Consignatária, adiciona a lista de campos o id e nome dos correspondentes
                final String[] fields2 = Arrays.copyOf(fields, fields.length + 3);
                fields2[fields.length] = Columns.COR_IDENTIFICADOR;
                fields2[fields.length + 1] = Columns.COR_NOME;
                fields2[fields.length + 2] = Columns.PRM_COMPL_ENDERECO;
                return fields2;
            } else {
                // Se tipo Consignatária, adiciona a lista de campos o id e nome dos correspondentes
                final String[] fields2 = Arrays.copyOf(fields, fields.length + 2);
                fields2[fields.length] = Columns.COR_IDENTIFICADOR;
                fields2[fields.length + 1] = Columns.COR_NOME;
                if ("confirmar_liquidacao".equalsIgnoreCase(tipoOperacao)) {
                    final String[] fields4 = Arrays.copyOf(fields2, fields2.length + 1);
                    fields4[fields2.length] = "VRF_SOLICITACAO_LIQUIDACAO";
                    return fields4;
                }

                if ("solicitacao".equalsIgnoreCase(tipoOperacao) && responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, CodedValues.TPC_SIM, responsavel)){
                    final String[] fields3 = Arrays.copyOf(fields2, fields2.length + 1);
                    fields3[fields2.length] = Columns.ARR_RISCO;
                    return fields3;
                }

                return fields2;
            }

        } else if ("cons_despesa_permissionario".equalsIgnoreCase(tipoOperacao)) {
            final String[] fields2 = Arrays.copyOf(fields, fields.length + 1);
            fields2[fields.length] = Columns.PRM_COMPL_ENDERECO;
            return fields2;
        } else if ("deferir".equalsIgnoreCase(tipoOperacao) || "indeferir".equalsIgnoreCase(tipoOperacao)) {
            String[] fields2;
            if (ParamSist.paramEquals(CodedValues.TPC_ALERTA_DEFER_INDEFER_MANUAL_CONTRATO, CodedValues.TPC_SIM, responsavel)) {
                fields2 = Arrays.copyOf(fields, fields.length + 2);
                fields2[fields.length] = "SOMA_VLR_RENEGOCIADO";
                fields2[fields.length + 1] = "QTD_ADE_AGUARD_LIQUIDACAO";
            } else {
                fields2 = Arrays.copyOf(fields, fields.length + 1);
                fields2[fields.length] = "SOMA_VLR_RENEGOCIADO";
            }
            return fields2;
        } else if ("reativar".equalsIgnoreCase(tipoOperacao)) {
            final String[] fields2 = Arrays.copyOf(fields, fields.length + 1);
            fields2[fields.length] = "INC_MARGEM_INCONSISTENTE";
            return fields2;
        } else if ("consultar_historico_pagamento".equalsIgnoreCase(tipoOperacao)) {
            final String[] fields2 = Arrays.copyOf(fields, fields.length + 3);
            fields2[fields.length] = Columns.DAD_VALOR + CodedValues.TDA_NOME_ESTABELECIMENTO_CARTAO;
            fields2[fields.length + 1] = Columns.DAD_VALOR + CodedValues.TDA_TRANSFERENCIA_TAXA;
            fields2[fields.length + 2] = Columns.DAD_VALOR + CodedValues.TDA_TRANSFERENCIA_VALOR_CREDITADO;
            return fields2;
        } else {
            return fields;
        }
    }
}

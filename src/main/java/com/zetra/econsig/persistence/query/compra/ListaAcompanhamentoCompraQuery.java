package com.zetra.econsig.persistence.query.compra;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusCompraEnum;

/**
 * <p>Title: ListaAcompanhamentoCompraQuery</p>
 * <p>Description: Pesquisa os contratos envolvidos em processo de compra.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAcompanhamentoCompraQuery extends HQuery {
    /**
     * O Map de parâmetros deve conter a configuração para:
     *   temSaldoDevedor      : NAO | SIM | TODOS
     *   saldoDevedorAprovado : NAO | SIM | TODOS
     *   saldoDevedorPago     : NAO | SIM | TODOS
     *   liquidado            : NAO | SIM | TODOS
     *   diasSemSaldoDevedor          : número inteiro
     *   diasSemAprovacaoSaldoDevedor : número inteiro
     *   diasSemPagamentoSaldoDevedor : número inteiro
     *   diasSemLiquidacao            : número inteiro
     *   origem : 0 (Meus contratos comprados) | 1 (Contratos comprados por mim)
     *   periodoIni : Data limite mínima para a compra do contrato
     *   periodoFim : Data limite máxima para a compra do contrato
     *   bloqueio: Listar bloqueio por: 0 (Não informação de saldo devedor) 1 (Não informação de pagamento de saldo devedor) 2 (Não liquidação de contrato) 3 (Não aprovação de saldo devedor)
     *   diasBloqueio: Listar contratos que estarão bloqueados daqui N dias
     */
    public TransferObject parametrosTO;

    public AcessoSistema responsavel;

    public String csaCodigo;
    public String corCodigo;
    public List<String> orgCodigos;
    public boolean matriculaExataSoap = false;
    public boolean orderByCsaNome = false;

    // Tipos de Bloqueio
    public static final String BLOQUEIO_FALTA_INF_SALDO_DEVEDOR   = "0";
    public static final String BLOQUEIO_FALTA_PAGTO_SALDO_DEVEDOR = "1";
    public static final String BLOQUEIO_FALTA_LIQUIDACAO          = "2";
    public static final String BLOQUEIO_FALTA_APROVACAO_SALDO     = "3";

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // Garante que os parametros não são nulos
        Map<String, Object> parametros = null;
        if (parametrosTO == null || parametrosTO.getAtributos() == null) {
            parametros = new HashMap<>();
        } else {
            parametros = parametrosTO.getAtributos();
        }

        // Status
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
        sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
        sadCodigos.add(CodedValues.SAD_DEFERIDA);

        // Status dos contratos que foram comprados (contratos originais)
        List<String> sadCodigosLiq = new ArrayList<>();
        sadCodigosLiq.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
        sadCodigosLiq.add(CodedValues.SAD_LIQUIDADA);

        // Obtém os parâmetros necessários
        String temSaldoDevedor = parametros.get("temSaldoDevedor") != null ? parametros.get("temSaldoDevedor").toString().toUpperCase() : "TODOS";
        String saldoDevedorAprovado = parametros.get("saldoDevedorAprovado") != null ? parametros.get("saldoDevedorAprovado").toString().toUpperCase() : "TODOS";
        String saldoDevedorPago = parametros.get("saldoDevedorPago") != null ? parametros.get("saldoDevedorPago").toString().toUpperCase() : "TODOS";
        String liquidado = parametros.get("liquidado") != null ? parametros.get("liquidado").toString().toUpperCase() : "TODOS";

        long diasSemSaldoDevedor = parametros.get("diasSemSaldoDevedor") != null ? Long.parseLong(parametros.get("diasSemSaldoDevedor").toString()) : 0;
        long diasSemAprovacaoSaldoDevedor = parametros.get("diasSemAprovacaoSaldoDevedor") != null ? Long.parseLong(parametros.get("diasSemAprovacaoSaldoDevedor").toString()) : 0;
        long diasSemPagamentoSaldoDevedor = parametros.get("diasSemPagamentoSaldoDevedor") != null ? Long.parseLong(parametros.get("diasSemPagamentoSaldoDevedor").toString()) : 0;
        long diasSemLiquidacao = parametros.get("diasSemLiquidacao") != null ? Long.parseLong(parametros.get("diasSemLiquidacao").toString()) : 0;
        boolean meusContratosComprados = !(parametros.get("origem") != null && parametros.get("origem").toString().equals("1"));

        String adeNumero = parametros.get("ADE_NUMERO") != null ? parametros.get("ADE_NUMERO").toString() : (parametros.get(Columns.ADE_NUMERO) != null ? parametros.get(Columns.ADE_NUMERO).toString() : null );
        String rseMatricula = parametros.get("RSE_MATRICULA") != null ? parametros.get("RSE_MATRICULA").toString() : (parametros.get(Columns.RSE_MATRICULA) != null ? parametros.get(Columns.RSE_MATRICULA).toString() : null );
        String serCpf = parametros.get("SER_CPF") != null ? parametros.get("SER_CPF").toString() : (parametros.get(Columns.SER_CPF) != null ? parametros.get(Columns.SER_CPF).toString() : null );

        String tipoPeriodo = parametros.get("tipoPeriodo") != null ? parametros.get("tipoPeriodo").toString() : "IC";

        String periodoIni = parametros.get("periodoIni") != null ? parametros.get("periodoIni").toString() : null;
        String periodoFim = parametros.get("periodoFim") != null ? parametros.get("periodoFim").toString() : null;

        int diasBloqueio = parametros.get("diasBloqueio") != null ? Integer.parseInt(parametros.get("diasBloqueio").toString()) : 0;
        String bloqueio = !TextHelper.isNull(parametros.get("bloqueio")) ? parametros.get("bloqueio").toString() : null;

        boolean usaDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel);
        boolean exibeEmCarencia = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CARENCIA_DESBL_AUT_CSA_COMPRA, CodedValues.TPC_SIM, responsavel);
        boolean matriculaExata = matriculaExataSoap || ParamSist.paramEquals(CodedValues.TPC_PESQUISA_MATRICULA_INTEIRA, CodedValues.TPC_SIM, responsavel);

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");

        corpoBuilder.append("adeOrigem.adeCodigo AS ADE_CODIGO, ");
        corpoBuilder.append("adeOrigem.adeNumero AS ADE_NUMERO, ");
        corpoBuilder.append("adeOrigem.adeIdentificador AS ADE_IDENTIFICADOR, ");
        corpoBuilder.append("adeOrigem.adeData AS ADE_DATA, ");
        corpoBuilder.append("adeOrigem.adeVlr AS ADE_VLR, ");
        corpoBuilder.append("adeOrigem.adeTipoVlr AS ADE_TIPO_VLR, ");
        corpoBuilder.append("adeOrigem.adePrazo AS ADE_PRAZO, ");
        corpoBuilder.append("adeOrigem.adeIndice AS ADE_INDICE, ");
        corpoBuilder.append("adeOrigem.adePrdPagas AS ADE_PRD_PAGAS, ");
        corpoBuilder.append("sad.sadCodigo AS SAD_CODIGO, ");
        corpoBuilder.append("sad.sadDescricao AS SAD_DESCRICAO, ");
        corpoBuilder.append("rse.rseCodigo AS RSE_CODIGO, ");
        corpoBuilder.append("rse.rseMatricula AS RSE_MATRICULA, ");
        corpoBuilder.append("ser.serCpf AS SER_CPF, ");
        corpoBuilder.append("srs.srsDescricao AS SRS_DESCRICAO, ");
        corpoBuilder.append("ser.serNome AS SER_NOME, ");
        corpoBuilder.append("csa.csaIdentificador AS CSA_IDENTIFICADOR, ");
        corpoBuilder.append("csa.csaNome AS CSA_NOME, ");
        corpoBuilder.append("csa.csaNomeAbrev AS CSA_NOME_ABREV, ");
        corpoBuilder.append("csa.csaCodigo AS CSA_CODIGO_ORIGEM, ");
        corpoBuilder.append("csaDestino.csaCodigo AS CSA_CODIGO_DESTINO, ");
        corpoBuilder.append("concatenar(concatenar(csa.csaIdentificador, ' - '), case when nullif(trim(csa.csaNomeAbrev), '') is null then csa.csaNome else csa.csaNomeAbrev end) AS CONSIGNATARIA, ");
        corpoBuilder.append("csaDestino.csaIdentificador AS CSA_IDENTIFICADOR_DESTINO, ");
        corpoBuilder.append("csaDestino.csaNome AS CSA_NOME_DESTINO, ");
        corpoBuilder.append("csaDestino.csaNomeAbrev AS CSA_NOME_ABREV_DESTINO, ");
        corpoBuilder.append("concatenar(concatenar(csaDestino.csaIdentificador, ' - '), case when nullif(trim(csaDestino.csaNomeAbrev), '') is null then csaDestino.csaNome else csaDestino.csaNomeAbrev end) AS CONSIGNATARIA_DESTINO, ");
        corpoBuilder.append("corDestino.corIdentificador AS COR_IDENTIFICADOR_DESTINO, ");
        corpoBuilder.append("corDestino.corNome AS COR_NOME_DESTINO, ");
        corpoBuilder.append("concatenar(concatenar(corDestino.corIdentificador, ' - '), corDestino.corNome) AS CORRESPONDENTE_DESTINO, ");
        corpoBuilder.append("concatenar(concatenar(concatenar(case when nullif(trim(cnv.cnvCodVerba), '') is not null then cnv.cnvCodVerba else svc.svcIdentificador end, case when nullif(trim(adeOrigem.adeIndice), '') is not null then adeOrigem.adeIndice else '' end), ' - '), svc.svcDescricao) AS SERVICO, ");
        corpoBuilder.append("cnv.cnvCodVerba AS CNV_COD_VERBA, ");
        corpoBuilder.append("svc.svcCodigo AS SVC_CODIGO, ");
        corpoBuilder.append("svc.svcIdentificador AS SVC_IDENTIFICADOR, ");
        corpoBuilder.append("svc.svcDescricao AS SVC_DESCRICAO, ");
        corpoBuilder.append("svcDestino.svcCodigo AS SVC_CODIGO_DESTINO, ");
        corpoBuilder.append("sdv.sdvValor AS SDV_VALOR, ");
        corpoBuilder.append("sdv.banco.bcoCodigo AS BCO_CODIGO, ");
        corpoBuilder.append("sdv.sdvAgencia AS SDV_AGENCIA, ");
        corpoBuilder.append("sdv.sdvConta AS SDV_CONTA, ");
        corpoBuilder.append("sdv.sdvNomeFavorecido AS SDV_NOME_FAVORECIDO, ");
        corpoBuilder.append("sdv.sdvCnpj AS SDV_CNPJ, ");
        corpoBuilder.append("sdv.sdvNumeroContrato AS SDV_NUMERO_CONTRATO, ");
        corpoBuilder.append("rad.adeCodigoDestino AS ADE_CODIGO_DESTINO, ");
        corpoBuilder.append("rad.radData AS RAD_DATA, ");
        corpoBuilder.append("rad.radDataInfSaldo AS RAD_DATA_INF_SALDO, ");
        corpoBuilder.append("rad.radDataAprSaldo AS RAD_DATA_APR_SALDO, ");
        corpoBuilder.append("rad.radDataPgtSaldo AS RAD_DATA_PGT_SALDO, ");
        corpoBuilder.append("rad.radDataLiquidacao AS RAD_DATA_LIQUIDACAO, ");
        corpoBuilder.append("stc.stcCodigo AS STC_CODIGO, ");
        corpoBuilder.append("stc.stcDescricao AS STC_DESCRICAO, ");

        corpoBuilder.append("MAX(CASE WHEN dad.tdaCodigo = '5' THEN dad.dadValor END) AS SALDO_DEVEDOR_1, ");
        corpoBuilder.append("MAX(CASE WHEN dad.tdaCodigo = '6' THEN dad.dadValor END) AS DATA_SALDO_DEVEDOR_1, ");
        corpoBuilder.append("MAX(CASE WHEN dad.tdaCodigo = '7' THEN dad.dadValor END) AS SALDO_DEVEDOR_2, ");
        corpoBuilder.append("MAX(CASE WHEN dad.tdaCodigo = '8' THEN dad.dadValor END) AS DATA_SALDO_DEVEDOR_2, ");
        corpoBuilder.append("MAX(CASE WHEN dad.tdaCodigo = '9' THEN dad.dadValor END) AS SALDO_DEVEDOR_3, ");
        corpoBuilder.append("MAX(CASE WHEN dad.tdaCodigo = '10' THEN dad.dadValor END) AS DATA_SALDO_DEVEDOR_3 ");

        corpoBuilder.append(" FROM AutDesconto adeOrigem ");
        corpoBuilder.append(" INNER JOIN adeOrigem.statusAutorizacaoDesconto sad ");
        corpoBuilder.append(" INNER JOIN adeOrigem.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.orgao org ");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append(" INNER JOIN adeOrigem.registroServidor rse ");
        corpoBuilder.append(" INNER JOIN rse.servidor ser ");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs ");
        corpoBuilder.append(" INNER JOIN adeOrigem.relacionamentoAutorizacaoByAdeCodigoOrigemSet rad ");
        corpoBuilder.append(" INNER JOIN rad.statusCompra stc ");

        // Destino do relacionamento é o novo contrato (Provavelmente em Aguard. Confirmação)
        corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoDestino adeDestino ");
        corpoBuilder.append(" INNER JOIN adeDestino.verbaConvenio vcoDestino ");
        corpoBuilder.append(" INNER JOIN vcoDestino.convenio cnvDestino ");
        corpoBuilder.append(" INNER JOIN cnvDestino.consignataria csaDestino ");
        corpoBuilder.append(" INNER JOIN cnvDestino.servico svcDestino ");

        if (meusContratosComprados) {
            if (responsavel.isCor()) {
                corpoBuilder.append(" INNER JOIN cnv.correspondenteConvenioSet crc WITH ");
                corpoBuilder.append(" crc.corCodigo = :corCodigoEntidade AND ");
                corpoBuilder.append(" crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("' ");
            }
        } else if (responsavel.isCor()) {
            corpoBuilder.append(" INNER JOIN cnvDestino.correspondenteConvenioSet crcDestino ");
            corpoBuilder.append(" WITH crcDestino.corCodigo = :corCodigoEntidade AND ");
            corpoBuilder.append(" crcDestino.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("' ");
        }

        if (BLOQUEIO_FALTA_INF_SALDO_DEVEDOR.equals(bloqueio)) {
            corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse149");
            corpoBuilder.append(" WITH pse149.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA).append("' ");
            corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse152");
            corpoBuilder.append(" WITH pse152.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV).append("' ");
        } else if (BLOQUEIO_FALTA_APROVACAO_SALDO.equals(bloqueio)) {
            corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse193");
            corpoBuilder.append(" WITH pse193.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_APR_SALDO_DV_CONTROLE_COMPRA).append("' ");
            corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse194");
            corpoBuilder.append(" WITH pse194.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_ACAO_PARA_NAO_APR_SALDO_DV).append("' ");
        } else if (BLOQUEIO_FALTA_PAGTO_SALDO_DEVEDOR.equals(bloqueio)) {
            corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse150");
            corpoBuilder.append(" WITH pse150.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA).append("' ");
            corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse153");
            corpoBuilder.append(" WITH pse153.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_ACAO_PARA_NAO_INF_PGT_SALDO).append("' ");
        } else if (BLOQUEIO_FALTA_LIQUIDACAO.equals(bloqueio)) {
            corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse151");
            corpoBuilder.append(" WITH pse151.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_LIQUIDACAO_ADE_CONTROLE_COMPRA).append("' ");
            corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse154");
            corpoBuilder.append(" WITH pse154.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE).append("' ");
        }

        corpoBuilder.append(" LEFT OUTER JOIN adeOrigem.saldoDevedorSet sdv ");
        corpoBuilder.append(" LEFT OUTER JOIN adeOrigem.dadosAutorizacaoDescontoSet dad ");
        corpoBuilder.append(" LEFT OUTER JOIN adeDestino.correspondente corDestino ");
        corpoBuilder.append(" WHERE (1 = 1)");

        // Filtra pelo contrato comprador, nos status abaixo
        corpoBuilder.append(" AND adeDestino.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));

        // Relacionamento de controle de compra
        corpoBuilder.append(" AND rad.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");

        // Status das compras que serão listados (Cláusula para que o índice em STC_CODIGO seja usado)
        List<String> stcCodigos = new ArrayList<>();
        if (temSaldoDevedor.equals("NAO") || temSaldoDevedor.equals("TODOS")) {
            stcCodigos.add(StatusCompraEnum.AGUARDANDO_INF_SALDO.getCodigo());
        }
        if (saldoDevedorAprovado.equals("NAO") || saldoDevedorAprovado.equals("TODOS")) {
            stcCodigos.add(StatusCompraEnum.AGUARDANDO_APR_SALDO.getCodigo());
        }
        if (saldoDevedorPago.equals("NAO") || saldoDevedorPago.equals("TODOS")) {
            stcCodigos.add(StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo());
        }
        if (liquidado.equals("NAO") || liquidado.equals("TODOS")) {
            stcCodigos.add(StatusCompraEnum.AGUARDANDO_LIQUIDACAO.getCodigo());
        }
        if (liquidado.equals("SIM") || liquidado.equals("TODOS")) {
            stcCodigos.add(StatusCompraEnum.LIQUIDADO.getCodigo());
            stcCodigos.add(StatusCompraEnum.FINALIZADO.getCodigo());
        }
        corpoBuilder.append(" AND rad.statusCompra.stcCodigo ").append(criaClausulaNomeada("stcCodigos", stcCodigos));

        // Se o número da ADE foi informado
        if (!TextHelper.isNull(adeNumero)) {
            corpoBuilder.append(" AND adeOrigem.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero)).append(" ");
        }

        if (responsavel.isSer()) {
            // Se for usuário servidor, lista apenas seus contratos
            corpoBuilder.append(" AND rse.rseCodigo = :rseCodigo");
        } else {
            // Adiciona cláusula de matricula e cpf
            corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCpf, !matriculaExata));
        }

        // Adiciona cláusula para data da compra do contrato. Não são obrigatórias, e uma não depende da outra.

        if (!TextHelper.isNull(periodoIni)) {
            if (!tipoPeriodo.equals("T")) {
                if (tipoPeriodo.equals("IC")) {
                    corpoBuilder.append(" AND rad.radData >= :periodoIni");
                } else if (tipoPeriodo.equals("I")) {
                    corpoBuilder.append(" AND rad.radDataInfSaldo >= :periodoIni");
                } else if (tipoPeriodo.equals("A")) {
                    corpoBuilder.append(" AND rad.radDataAprSaldo >= :periodoIni");
                } else if (tipoPeriodo.equals("P")) {
                    corpoBuilder.append(" AND rad.radDataPgtSaldo >= :periodoIni");
                } else if (tipoPeriodo.equals("L")) {
                    corpoBuilder.append(" AND rad.radDataLiquidacao >= :periodoIni");
                }
            } else {
                corpoBuilder.append(" AND (rad.radData >= :periodoIni");
                corpoBuilder.append(" OR rad.radDataInfSaldo >= :periodoIni");
                corpoBuilder.append(" OR rad.radDataAprSaldo >= :periodoIni");
                corpoBuilder.append(" OR rad.radDataPgtSaldo >= :periodoIni");
                corpoBuilder.append(" OR rad.radDataLiquidacao >= :periodoIni");
                corpoBuilder.append(" ) ");
            }
        }

        if (!TextHelper.isNull(periodoFim)) {
            if (!tipoPeriodo.equals("T")) {
                if (tipoPeriodo.equals("IC")) {
                    corpoBuilder.append(" AND rad.radData <= :periodoFim");
                } else if (tipoPeriodo.equals("I")) {
                    corpoBuilder.append(" AND rad.radDataInfSaldo <= :periodoFim");
                } else if (tipoPeriodo.equals("A")) {
                    corpoBuilder.append(" AND rad.radDataAprSaldo <= :periodoFim");
                } else if (tipoPeriodo.equals("P")) {
                    corpoBuilder.append(" AND rad.radDataPgtSaldo <= :periodoFim");
                } else if (tipoPeriodo.equals("L")) {
                    corpoBuilder.append(" AND rad.radDataLiquidacao <= :periodoFim");
                }
            } else if (!TextHelper.isNull(periodoFim)) {
                corpoBuilder.append(" AND (rad.radData <= :periodoFim");
                corpoBuilder.append(" OR rad.radDataInfSaldo <= :periodoFim");
                corpoBuilder.append(" OR rad.radDataAprSaldo <= :periodoFim");
                corpoBuilder.append(" OR rad.radDataPgtSaldo <= :periodoFim");
                corpoBuilder.append(" OR rad.radDataLiquidacao <= :periodoFim");
                corpoBuilder.append(" ) ");
            }
        }

        if (meusContratosComprados) {
            // Filtra pela consignatária / correspondente
            if (!TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
                corpoBuilder.append(" AND rad.consignatariaByCsaCodigoOrigem.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            }
            if (!TextHelper.isNull(corCodigo)) {
                corpoBuilder.append(" AND adeOrigem.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
            }
        } else {
            // Filtra pela consignatária / correspondente
            if (!TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" AND csaDestino.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
                corpoBuilder.append(" AND rad.consignatariaByCsaCodigoDestino.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            }
            if (!TextHelper.isNull(corCodigo)) {
                corpoBuilder.append(" AND adeDestino.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
            }
        }

        // Verifica Informacao de Saldo Devedor
        if (temSaldoDevedor.equals("NAO") || BLOQUEIO_FALTA_INF_SALDO_DEVEDOR.equals(bloqueio)) {
            String campoDataRef = "coalesce(rad.radDataRefInfSaldo, rad.radData)";

            // Saldo devedor nao informado
            corpoBuilder.append(" AND ((rad.radDataInfSaldo IS NULL ");

            // X dias sem informacao de saldo devedor
            if (usaDiasUteis) {
                corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(").append(campoDataRef).append(") and current_date()) > ");
            } else {
                corpoBuilder.append(" AND TO_LONG(TO_DAYS(CURRENT_DATE()) - TO_DAYS(").append(campoDataRef).append(")) >= ");
            }
            if (BLOQUEIO_FALTA_INF_SALDO_DEVEDOR.equals(bloqueio)) {
                corpoBuilder.append(" to_numeric(coalesce(nullif(trim(pse149.pseVlr), ''), '99999')) - :diasBloqueio)");

                if (exibeEmCarencia) {
                    corpoBuilder.append(" OR (rad.radDataInfSaldo IS NOT NULL ");

                    if (usaDiasUteis) {
                        corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(rad.radDataRefInfSaldo) and TO_DATE(rad.radDataInfSaldo)) > ");
                    } else {
                        corpoBuilder.append(" AND TO_LONG(TO_DAYS(rad.radDataInfSaldo) - TO_DAYS(rad.radDataRefInfSaldo)) >= ");
                    }
                    corpoBuilder.append(" to_numeric(coalesce(nullif(trim(pse149.pseVlr), ''), '99999')) ");

                    if (usaDiasUteis) {
                        corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(rad.radDataInfSaldo) and current_date()) <= ");
                    } else {
                        corpoBuilder.append(" AND TO_LONG(TO_DAYS(CURRENT_DATE()) - TO_DAYS(rad.radDataInfSaldo)) < ");
                    }
                    corpoBuilder.append(" to_numeric(coalesce(nullif(trim(pse152.pseVlrRef), ''), '0')) ");
                    corpoBuilder.append(" AND coalesce(pse152.pseVlr, '0') = '1') ");
                }
            } else {
                corpoBuilder.append(":diasSemSaldoDevedor)");
            }
            corpoBuilder.append(")");
        } else if (temSaldoDevedor.equals("SIM")) {
            // Saldo devedor ja informado
            corpoBuilder.append(" AND rad.radDataInfSaldo IS NOT NULL ");
        }

        // Verifica Pagamento de Saldo Devedor
        if (saldoDevedorAprovado.equals("NAO") || BLOQUEIO_FALTA_APROVACAO_SALDO.equals(bloqueio)) {
            String campoDataRef = "coalesce(coalesce(rad.radDataRefAprSaldo, rad.radDataRefInfSaldo), rad.radData)";

            // Saldo devedor nao aprovado
            corpoBuilder.append(" AND rad.radDataAprSaldo IS NULL ");

            // X dias sem aprovação de saldo devedor
            if (usaDiasUteis) {
                corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(").append(campoDataRef).append(") and current_date()) > ");
            } else {
                corpoBuilder.append(" AND TO_LONG(TO_DAYS(CURRENT_DATE()) - TO_DAYS(").append(campoDataRef).append(")) >= ");
            }
            if (BLOQUEIO_FALTA_APROVACAO_SALDO.equals(bloqueio)) {
                corpoBuilder.append(" to_numeric(coalesce(nullif(trim(pse193.pseVlr), ''), '99999')) - :diasBloqueio");
            } else {
                corpoBuilder.append(":diasSemAprovacaoSaldoDevedor");
            }
        } else if (saldoDevedorAprovado.equals("SIM")) {
            // Saldo devedor ja aprovado
            corpoBuilder.append(" AND rad.radDataAprSaldo IS NOT NULL ");
        }

        // Verifica Pagamento de Saldo Devedor
        if (saldoDevedorPago.equals("NAO") || BLOQUEIO_FALTA_PAGTO_SALDO_DEVEDOR.equals(bloqueio)) {
            String campoDataRef = "coalesce(coalesce(coalesce(rad.radDataRefPgtSaldo, rad.radDataRefAprSaldo), rad.radDataRefInfSaldo), rad.radData)";

            // Sem informacao de pagamento de saldo devedor
            corpoBuilder.append(" AND ((rad.radDataPgtSaldo IS NULL ");

            // X dias sem informação de pagamento de saldo devedor
            if (usaDiasUteis) {
                corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(").append(campoDataRef).append(") and current_date()) > ");
            } else {
                corpoBuilder.append(" AND TO_LONG(TO_DAYS(CURRENT_DATE()) - TO_DAYS(").append(campoDataRef).append(")) >= ");
            }
            if (BLOQUEIO_FALTA_PAGTO_SALDO_DEVEDOR.equals(bloqueio)) {
                corpoBuilder.append(" to_numeric(coalesce(nullif(trim(pse150.pseVlr), ''), '99999')) - :diasBloqueio)");

                if (exibeEmCarencia) {
                    corpoBuilder.append(" OR (rad.radDataPgtSaldo IS NOT NULL ");

                    if (usaDiasUteis) {
                        corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(rad.radDataRefPgtSaldo) and TO_DATE(rad.radDataPgtSaldo)) > ");
                    } else {
                        corpoBuilder.append(" AND TO_LONG(TO_DAYS(rad.radDataPgtSaldo) - TO_DAYS(rad.radDataRefPgtSaldo)) >= ");
                    }
                    corpoBuilder.append(" to_numeric(coalesce(nullif(trim(pse150.pseVlr), ''), '99999')) ");

                    if (usaDiasUteis) {
                        corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(rad.radDataPgtSaldo) and current_date()) <= ");
                    } else {
                        corpoBuilder.append(" AND TO_LONG(TO_DAYS(CURRENT_DATE()) - TO_DAYS(rad.radDataPgtSaldo)) < ");
                    }
                    corpoBuilder.append(" to_numeric(coalesce(nullif(trim(pse153.pseVlrRef), ''), '0')) ");
                    corpoBuilder.append(" AND coalesce(pse153.pseVlr, '0') = '1') ");
                }
            } else {
                corpoBuilder.append(":diasSemPagamentoSaldoDevedor)");
            }
            corpoBuilder.append(")");
        } else if (saldoDevedorPago.equals("SIM")) {
            // Com informacao de pagamento de saldo devedor
            corpoBuilder.append(" AND rad.radDataPgtSaldo IS NOT NULL ");
        }

        // Verifica Liquidacao
        if (liquidado.equals("NAO") || BLOQUEIO_FALTA_LIQUIDACAO.equals(bloqueio)) {
            String campoDataRef = "coalesce(coalesce(coalesce(coalesce(rad.radDataRefLiquidacao, rad.radDataRefPgtSaldo), rad.radDataRefAprSaldo), rad.radDataRefInfSaldo), rad.radData)";

            // Contrato ainda nao liquidado (aguardando liquidacao de compra)
            corpoBuilder.append(" AND ((sad.sadCodigo = '").append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("'");
            corpoBuilder.append(" AND rad.radDataLiquidacao IS NULL ");

            // X dias sem a liquidacao do contrato
            if (usaDiasUteis) {
                corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(").append(campoDataRef).append(") and current_date()) > ");
            } else {
                corpoBuilder.append(" AND TO_LONG(TO_DAYS(CURRENT_DATE()) - TO_DAYS(").append(campoDataRef).append(")) >= ");
            }
            if (BLOQUEIO_FALTA_LIQUIDACAO.equals(bloqueio)) {
                corpoBuilder.append(" to_numeric(coalesce(nullif(trim(pse151.pseVlr), ''), '99999')) - :diasBloqueio)");

                if (exibeEmCarencia) {
                    corpoBuilder.append(" OR (rad.radDataLiquidacao IS NOT NULL ");

                    if (usaDiasUteis) {
                        corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(rad.radDataRefLiquidacao) and TO_DATE(rad.radDataLiquidacao)) > ");
                    } else {
                        corpoBuilder.append(" AND TO_LONG(TO_DAYS(rad.radDataLiquidacao) - TO_DAYS(rad.radDataRefLiquidacao)) >= ");
                    }
                    corpoBuilder.append(" to_numeric(coalesce(nullif(trim(pse151.pseVlr), ''), '99999')) ");

                    if (usaDiasUteis) {
                        corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(rad.radDataLiquidacao) and current_date()) <= ");
                    } else {
                        corpoBuilder.append(" AND TO_LONG(TO_DAYS(CURRENT_DATE()) - TO_DAYS(rad.radDataLiquidacao)) < ");
                    }
                    corpoBuilder.append(" to_numeric(coalesce(nullif(trim(pse154.pseVlrRef), ''), '0')) ");
                    corpoBuilder.append(" AND coalesce(pse154.pseVlr, '0') = '1') ");
                }
            } else {
                corpoBuilder.append(":diasSemLiquidacao)");
            }
            corpoBuilder.append(")");
        } else if (liquidado.equals("SIM")) {
            // Contrato ja liquidado
            corpoBuilder.append(" AND sad.sadCodigo = '").append(CodedValues.SAD_LIQUIDADA).append("'");
        } else {
            corpoBuilder.append(" AND sad.sadCodigo ").append(criaClausulaNomeada("sadCodigosLiq", sadCodigosLiq));
        }

        // Servidores excluídos não serão listados na pesquisa
        boolean ignoraServExcluidos = ParamSist.paramEquals(CodedValues.TPC_IGNORA_SERVIDORES_EXCLUIDOS, CodedValues.TPC_SIM, responsavel);
        if (ignoraServExcluidos) {
            corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", responsavel.getCodigoEntidadePai()));
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        corpoBuilder.append(" GROUP BY ");
        corpoBuilder.append("adeOrigem.adeCodigo, ");
        corpoBuilder.append("adeOrigem.adeNumero, ");
        corpoBuilder.append("adeOrigem.adeIdentificador, ");
        corpoBuilder.append("adeOrigem.adeData, ");
        corpoBuilder.append("adeOrigem.adeVlr, ");
        corpoBuilder.append("adeOrigem.adeTipoVlr, ");
        corpoBuilder.append("adeOrigem.adePrazo, ");
        corpoBuilder.append("adeOrigem.adeIndice, ");
        corpoBuilder.append("adeOrigem.adePrdPagas, ");
        corpoBuilder.append("sad.sadCodigo, ");
        corpoBuilder.append("sad.sadDescricao, ");
        corpoBuilder.append("rse.rseCodigo, ");
        corpoBuilder.append("rse.rseMatricula, ");
        corpoBuilder.append("ser.serCpf, ");
        corpoBuilder.append("srs.srsDescricao, ");
        corpoBuilder.append("ser.serNome, ");
        corpoBuilder.append("csa.csaIdentificador, ");
        corpoBuilder.append("csa.csaNome, ");
        corpoBuilder.append("csa.csaNomeAbrev, ");
        corpoBuilder.append("csa.csaCodigo, ");
        corpoBuilder.append("csaDestino.csaCodigo, ");
        corpoBuilder.append("concatenar(concatenar(csa.csaIdentificador, ' - '), case when nullif(trim(csa.csaNomeAbrev), '') is null then csa.csaNome else csa.csaNomeAbrev end), ");
        corpoBuilder.append("csaDestino.csaIdentificador, ");
        corpoBuilder.append("csaDestino.csaNome, ");
        corpoBuilder.append("csaDestino.csaNomeAbrev, ");
        corpoBuilder.append("concatenar(concatenar(csaDestino.csaIdentificador, ' - '), case when nullif(trim(csaDestino.csaNomeAbrev), '') is null then csaDestino.csaNome else csaDestino.csaNomeAbrev end), ");
        corpoBuilder.append("corDestino.corIdentificador, ");
        corpoBuilder.append("corDestino.corNome, ");
        corpoBuilder.append("concatenar(concatenar(corDestino.corIdentificador, ' - '), corDestino.corNome), ");
        corpoBuilder.append("concatenar(concatenar(concatenar(case when nullif(trim(cnv.cnvCodVerba), '') is not null then cnv.cnvCodVerba else svc.svcIdentificador end, case when nullif(trim(adeOrigem.adeIndice), '') is not null then adeOrigem.adeIndice else '' end), ' - '), svc.svcDescricao), ");
        corpoBuilder.append("cnv.cnvCodVerba, ");
        corpoBuilder.append("svc.svcCodigo, ");
        corpoBuilder.append("svc.svcIdentificador, ");
        corpoBuilder.append("svc.svcDescricao, ");
        corpoBuilder.append("svcDestino.svcCodigo, ");
        corpoBuilder.append("sdv.sdvValor, ");
        corpoBuilder.append("sdv.banco.bcoCodigo, ");
        corpoBuilder.append("sdv.sdvAgencia, ");
        corpoBuilder.append("sdv.sdvConta, ");
        corpoBuilder.append("sdv.sdvNomeFavorecido, ");
        corpoBuilder.append("sdv.sdvCnpj, ");
        corpoBuilder.append("sdv.sdvNumeroContrato, ");
        corpoBuilder.append("rad.adeCodigoDestino, ");
        corpoBuilder.append("rad.radData, ");
        corpoBuilder.append("rad.radDataInfSaldo, ");
        corpoBuilder.append("rad.radDataAprSaldo, ");
        corpoBuilder.append("rad.radDataPgtSaldo, ");
        corpoBuilder.append("rad.radDataLiquidacao, ");
        corpoBuilder.append("stc.stcCodigo, ");
        corpoBuilder.append("stc.stcDescricao ");

        corpoBuilder.append(" ORDER BY ").append(orderByCsaNome ? "csa.csaNome ASC, " : "").append("adeOrigem.adeData DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, !matriculaExata, query);

        if (!TextHelper.isNull(adeNumero)) {
            defineValorClausulaNomeada("adeNumero", Long.valueOf(adeNumero), query);
        }
        if (!TextHelper.isNull(periodoIni)) {
            try {
                defineValorClausulaNomeada("periodoIni", DateHelper.parse(periodoIni + " 00:00:00", LocaleHelper.getDateTimePattern()), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.inicio.parse.invalido", (AcessoSistema) null);
            }
        }
        if (!TextHelper.isNull(periodoFim)) {
            try {
                defineValorClausulaNomeada("periodoFim", DateHelper.parse(periodoFim + " 23:59:59", LocaleHelper.getDateTimePattern()), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.fim.parse.invalido",  (AcessoSistema) null);
            }
        }

        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        defineValorClausulaNomeada("stcCodigos", stcCodigos, query);

        if (query.getQueryString().contains(":sadCodigosLiq")) {
            defineValorClausulaNomeada("sadCodigosLiq", sadCodigosLiq, query);
        }
        if (query.getQueryString().contains(":csaCodigo")) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (query.getQueryString().contains(":corCodigo")) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }
        if (query.getQueryString().contains(":corCodigoEntidade")) {
            defineValorClausulaNomeada("corCodigoEntidade", responsavel.getCodigoEntidade(), query);
        }
        if (query.getQueryString().contains(":orgCodigo")) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (query.getQueryString().contains(":diasBloqueio")) {
            defineValorClausulaNomeada("diasBloqueio", diasBloqueio, query);
        }
        if (query.getQueryString().contains(":diasSemSaldoDevedor")) {
            defineValorClausulaNomeada("diasSemSaldoDevedor", diasSemSaldoDevedor, query);
        }
        if (query.getQueryString().contains(":diasSemAprovacaoSaldoDevedor")) {
            defineValorClausulaNomeada("diasSemAprovacaoSaldoDevedor", diasSemAprovacaoSaldoDevedor, query);
        }
        if (query.getQueryString().contains(":diasSemPagamentoSaldoDevedor")) {
            defineValorClausulaNomeada("diasSemPagamentoSaldoDevedor", diasSemPagamentoSaldoDevedor, query);
        }
        if (query.getQueryString().contains(":diasSemLiquidacao")) {
            defineValorClausulaNomeada("diasSemLiquidacao", diasSemLiquidacao, query);
        }
        if (responsavel.isSer()) {
            defineValorClausulaNomeada("rseCodigo", responsavel.getRseCodigo(), query);
        }
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            defineValorClausulaNomeada("estCodigo", responsavel.getCodigoEntidadePai(), query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_DATA,
                Columns.ADE_VLR,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_PRAZO,
                Columns.ADE_INDICE,
                Columns.ADE_PRD_PAGAS,
                Columns.SAD_CODIGO,
                Columns.SAD_DESCRICAO,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_CPF,
                Columns.SRS_DESCRICAO,
                Columns.SER_NOME,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                "CSA_CODIGO_ORIGEM",
                "CSA_CODIGO_DESTINO",
                "CONSIGNATARIA",
                "CSA_IDENTIFICADOR_DESTINO",
                "CSA_NOME_DESTINO",
                "CSA_NOME_ABREV_DESTINO",
                "CONSIGNATARIA_DESTINO",
                "COR_IDENTIFICADOR_DESTINO",
                "COR_NOME_DESTINO",
                "CORRESPONDENTE_DESTINO",
                "SERVICO",
                Columns.CNV_COD_VERBA,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                "SVC_CODIGO_DESTINO",
                Columns.SDV_VALOR,
                Columns.SDV_BCO_CODIGO,
                Columns.SDV_AGENCIA,
                Columns.SDV_CONTA,
                Columns.SDV_NOME_FAVORECIDO,
                Columns.SDV_CNPJ,
                Columns.SDV_NUMERO_CONTRATO,
                Columns.RAD_ADE_CODIGO_DESTINO,
                Columns.RAD_DATA,
                Columns.RAD_DATA_INF_SALDO,
                Columns.RAD_DATA_APR_SALDO,
                Columns.RAD_DATA_PGT_SALDO,
                Columns.RAD_DATA_LIQUIDACAO,
                Columns.STC_CODIGO,
                Columns.STC_DESCRICAO,
                "SALDO_DEVEDOR_1",
                "DATA_SALDO_DEVEDOR_1",
                "SALDO_DEVEDOR_2",
                "DATA_SALDO_DEVEDOR_2",
                "SALDO_DEVEDOR_3",
                "DATA_SALDO_DEVEDOR_3"
        };
    }
}

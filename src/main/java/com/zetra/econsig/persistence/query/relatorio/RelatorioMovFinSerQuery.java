package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasEntrePeriodosQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasEntreQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioMovFinSerQuery</p>
 * <p>Description: Relatório de Movimentação financeira do servidor</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioMovFinSerQuery extends ReportHQuery {
    public String tipoEntidade;
    public String periodoIni;
    public String periodoFim;
    public List<String> orgCodigos;
    public String csaCodigo;
    public List<String> corCodigos;
    public String estCodigo;
    public List<String> svcCodigos;
    public List<String> sadCodigos;
    public List<String> spdCodigos;
    public String order;
    private List<String> srsCodigos;
    private List<String> lstPrdRealizado;
    public String sboCodigo;
    public String uniCodigo;
    public List<String> nseCodigos;

    private boolean periodoUnico = false;
    private boolean useParcelaPeriodo;
    private boolean useParcela;
    private boolean innerJoin = true;

    protected boolean relatorioDescontos;
    public String echCodigo;
    public String plaCodigo;
    public String cnvCodVerba;

    public String matricula;
    public String cpf;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        tipoEntidade = (String) criterio.getAttribute("TIPO_ENTIDADE");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
        corCodigos = (List<String>) criterio.getAttribute(Columns.COR_CODIGO);
        estCodigo = (String) criterio.getAttribute("EST_CODIGO");
        svcCodigos = (List<String>) criterio.getAttribute("SVC_CODIGO");
        periodoIni = (String) criterio.getAttribute("PERIODOINI");
        periodoFim = (String) criterio.getAttribute("PERIODOFIM");
        sadCodigos = (List<String>) criterio.getAttribute("SAD_CODIGO");
        spdCodigos = (List<String>) criterio.getAttribute("SPD_CODIGO");
        order = (String) criterio.getAttribute("ORDER");
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
        lstPrdRealizado = (List<String>) criterio.getAttribute("PRD_REALIZADO");
        sboCodigo = (String) criterio.getAttribute("SBO_CODIGO");
        uniCodigo = (String) criterio.getAttribute("UNI_CODIGO");
        matricula = (String) criterio.getAttribute("RSE_MATRICULA");
        cpf = (String) criterio.getAttribute("CPF");
        nseCodigos = (List<String>) criterio.getAttribute("NSE_CODIGO");


        // DESENV-14093: O filtro se aplica apenas às parcelas nos status liquidada folha (6) e liquidada manual (7), ou seja caso alguma das opções seja marcada, somente parcelas pagas serão listadas.
        if (lstPrdRealizado != null && !lstPrdRealizado.isEmpty()) {
            spdCodigos = new ArrayList<>();
            spdCodigos.add(CodedValues.SPD_LIQUIDADAFOLHA);
            spdCodigos.add(CodedValues.SPD_LIQUIDADAMANUAL);
        }
    }

    /**
     * verifica se há parcelas para o período para incluir na busca do relatório
     *
     * @param session
     * @throws HQueryException
     */
    private void setUsoPeriodo(Session session) throws HQueryException {
        periodoUnico = periodoIni.equals(periodoFim);

        final ObtemTotalParcelasEntreQuery totalParcelas = new ObtemTotalParcelasEntreQuery();

        totalParcelas.periodoIni = periodoIni;
        totalParcelas.periodoFim = periodoUnico ? null : periodoFim;
        totalParcelas.relatorio = true;
        final int totalPrd = totalParcelas.executarContador(session);
        useParcela = (totalPrd > 0);

        final ObtemTotalParcelasEntrePeriodosQuery totalParcelasPer = new ObtemTotalParcelasEntrePeriodosQuery();

        totalParcelasPer.periodoIni = periodoIni;
        totalParcelasPer.periodoFim = periodoUnico ? null : periodoFim;
        final int totalPdp = totalParcelasPer.executarContador(session);
        useParcelaPeriodo = (totalPdp > 0);

        final boolean processamentoFerias = ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        final boolean permiteLiquidarParcelaFutura = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_FUTURA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        if (processamentoFerias || permiteLiquidarParcelaFutura) {
            if (useParcelaPeriodo && useParcela) {
                // Se existe parcela nas duas tabelas, e o sistema possui processamento
                // de férias, utiliza ambas, fazendo "Left Join" pois as parcelas
                // existentes podem apenas ser de contratos que tiveram pagamento de férias.
                // Neste caso, não deixa usar o hint de indice, pois fará terá performance pior.
                indexHintEnabled = false;
            } else if (useParcelaPeriodo || useParcela) {
                // Se não existe parcela nas duas tabelas, e o sistema possui processamento
                // de férias, caso exista parcela em uma das tabelas, não faz inner join
                // pois as parcelas existentes podem ser apenas as de férias.
                // DESENV-14394 - Quando o sistema também permita liquidação de futura é necessário utilizar o left
                innerJoin = false;
            }

        } else if (useParcelaPeriodo && useParcela) {
            // Se existe parcela nas duas tabelas, verifica em qual tem mais parcelas, e
            // utiliza apenas uma das tabelas
            useParcela = (totalPrd >= totalPdp);
            useParcelaPeriodo = !useParcela;
        }
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                "CAMPO_GRUPO",
                "SIT_PARCELA",
                "PARCELA",
                "VALOR",
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_VLR_REALIZADO,
                Columns.SPD_DESCRICAO,
                Columns.PRD_NUMERO,
                Columns.OCP_OBS,
                "ADE_NUM",
                "SITUACAO",
                Columns.ADE_VLR,
                Columns.ADE_DATA,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.CDE_VLR_LIBERADO,
                Columns.ORG_IDENTIFICADOR,
                Columns.SER_CPF,
                Columns.RSE_MATRICULA,
                Columns.RSE_TIPO,
                Columns.SER_NOME,
                Columns.SRS_DESCRICAO,
                Columns.CSA_NOME,
                "CONSIGNATARIA",
                "CORRESPONDENTE",
                "SERVIDOR",
                "ORGAO",
                "CODIGO_SERVICO",
                "SERVICO"
        };
    }


    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final boolean temStatus = (srsCodigos != null && !srsCodigos.isEmpty());
        final boolean permiteCadIndice = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CAD_INDICE, AcessoSistema.getAcessoUsuarioSistema());
        setUsoPeriodo(session);

        final String descParcelaEmAberto = ApplicationResourcesHelper.getMessage("rotulo.em.aberto", responsavel);
        String fields = null;

        if (TextHelper.isNull(order) || order.equals("CONSIGNATARIA")) {
            fields = "concat(concat(csa.csaIdentificador, ' - '),case when nullif(trim(csa.csaNomeAbrev), '') is null then csa.csaNome else csa.csaNomeAbrev end) as CAMPO_GRUPO,";
        } else if (order.equals("ORGAO")) {
            fields = "concat(concat(org.orgIdentificador, ' - '),org.orgNome) as CAMPO_GRUPO,";
        }


        if (useParcelaPeriodo && useParcela) {
            fields += "concat(coalesce(coalesce(spd1.spdDescricao, spd2.spdDescricao), '" + descParcelaEmAberto + "'), case when coalesce(coalesce(tde1.tdeCodigo, tde2.tdeCodigo), '000') = '000' then '' else concat(' - ', coalesce(tde1.tdeDescricao, tde2.tdeDescricao)) end) as SIT_PARCELA,"
                    + "coalesce(case when sad.sadCodigo in ('1','2') then '1' else str(coalesce(prd.prdNumero, pdp.prdNumero)) end, '-') as PARCELA,"
                    + "coalesce(coalesce(prd.prdVlrPrevisto,pdp.prdVlrPrevisto),ade.adeVlr) as VALOR,"
                    + "coalesce(prd.prdVlrPrevisto, pdp.prdVlrPrevisto) AS prd_vlr_previsto,"
                    + "coalesce(prd.prdVlrRealizado, pdp.prdVlrRealizado) AS prd_vlr_realizado,"
                    + "coalesce(spd1.spdDescricao, spd2.spdDescricao) AS spd_descricao,"
                    + "coalesce(prd.prdNumero, pdp.prdNumero) AS prd_numero,"
                    + "coalesce(coalesce(text_to_string(ocp1.ocpObs), text_to_string(ocp2.ocpObs)), '-') AS ocp_obs,"
            ;

        } else if (useParcela) {
            fields += "concat(coalesce(spd1.spdDescricao, '" + descParcelaEmAberto + "'), case when coalesce(tde1.tdeCodigo, '000') = '000' then '' else concat(' - ', tde1.tdeDescricao) end) as SIT_PARCELA,"
                    + "coalesce(case when sad.sadCodigo in ('1','2') then '1' else str(prd.prdNumero) end, '-') as PARCELA,"
                    + "coalesce(prd.prdVlrPrevisto,ade.adeVlr) as VALOR,"
                    + "prd.prdVlrPrevisto AS prd_vlr_previsto,"
                    + "prd.prdVlrRealizado AS prd_vlr_realizado,"
                    + "spd1.spdDescricao AS spd_descricao,"
                    + "prd.prdNumero AS prd_numero,"
                    + "coalesce(text_to_string(ocp1.ocpObs), '-') AS ocp_obs,"
            ;

        } else if (useParcelaPeriodo) {
            fields += "concat(coalesce(spd2.spdDescricao, '" + descParcelaEmAberto + "'), case when coalesce(tde2.tdeCodigo, '000') = '000' then '' else concat(' - ', tde2.tdeDescricao) end) as SIT_PARCELA,"
                    + "coalesce(case when sad.sadCodigo in ('1','2') then '1' else str(pdp.prdNumero) end, '-') as PARCELA,"
                    + "coalesce(pdp.prdVlrPrevisto,ade.adeVlr) as VALOR,"
                    + "pdp.prdVlrPrevisto AS prd_vlr_previsto,"
                    + "pdp.prdVlrRealizado AS prd_vlr_realizado,"
                    + "spd2.spdDescricao AS spd_descricao,"
                    + "pdp.prdNumero AS prd_numero,"
                    + "coalesce(text_to_string(ocp2.ocpObs), '-') AS ocp_obs,"
            ;

        } else {
            fields += "'" + descParcelaEmAberto + "' as SIT_PARCELA,"
                    + "'-' as PARCELA,"
                    + "ade.adeVlr as VALOR,"
                    + "ade.adeVlr AS prd_vlr_previsto,"
                    + "to_decimal(0, 13, 2) AS prd_vlr_realizado,"
                    + "'" + descParcelaEmAberto + "' AS spd_descricao,"
                    + "'-' AS prd_numero,"
                    + "'-' as ocp_obs,"
            ;

        }

        fields += "str(ade.adeNumero) as ADE_NUM,"
                + "sad.sadDescricao as SITUACAO,"
                + "ade.adeVlr AS ade_vlr,"
                + "ade.adeData AS ade_data,"
                + "ade.adeAnoMesIni AS ade_ano_mes_ini,"
                + "ade.adeAnoMesFim AS ade_ano_mes_fim,"
                + "ade.adeNumero AS ade_numero,"
                + "ade.adeIdentificador AS ade_identificador,"
                + "coalesce(str(ade.adePrazo), 'Indeter.') AS ade_prazo,"
                + "coalesce(str(ade.adePrdPagas), '0') AS ade_prd_pagas,"
                + "cde.cdeVlrLiberado AS cde_vlr_liberado,"
                + "org.orgIdentificador AS org_identificador,"
                + "ser.serCpf AS ser_cpf,"
                + "rse.rseMatricula AS rse_matricula,"
                + "rse.rseTipo AS rse_tipo,"
                + "ser.serNome AS ser_nome,"
                + "srs.srsDescricao AS srs_descricao,"
                + "csa.csaNome AS csa_nome,"
                + "concat(concat(csa.csaIdentificador, ' - '),case when nullif(trim(csa.csaNomeAbrev), '') is null then csa.csaNome else csa.csaNomeAbrev end) as consignataria,"
                + "concat(concat(cor.corIdentificador, ' - '),cor.corNome) as correspondente,"
                + "concat(concat(rse.rseMatricula, ' - '),ser.serNome) as servidor,"
                + "concat(concat(org.orgIdentificador, ' - '),org.orgNome) as orgao,"
        ;

        if (permiteCadIndice) {
            fields += "concat(coalesce(concat(cnv.cnvCodVerba,coalesce(ade.adeIndice,'')),svc.svcIdentificador), case when coalesce(ade.adeCodReg, '6') = '4' then ' - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) + "' else '' end) as CODIGO_SERVICO,"
                    + "concat(concat(coalesce(concat(cnv.cnvCodVerba,coalesce(ade.adeIndice,'')),svc.svcIdentificador), ' - '), concat(svc.svcDescricao, case when coalesce(ade.adeCodReg, '6') = '4' then ' - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) + "' else '' end)) as SERVICO";
        } else {
            fields += "concat(coalesce(cnv.cnvCodVerba,svc.svcIdentificador), case when coalesce(ade.adeCodReg, '6') = '4' then ' - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) + "' else '' end) as CODIGO_SERVICO,"
                    + "concat(concat(coalesce(cnv.cnvCodVerba,svc.svcIdentificador), ' - '),concat(svc.svcDescricao, case when coalesce(ade.adeCodReg, '6') = '4' then ' - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) + "' else '' end)) as SERVICO";
        }

        final StringBuilder sql = new StringBuilder();
        sql.append("select ").append(fields);
        sql.append(" from AutDesconto ade");
        sql.append(" inner join ade.statusAutorizacaoDesconto sad");
        sql.append(" inner join ade.verbaConvenio vco");
        sql.append(" inner join vco.convenio cnv");
        sql.append(" inner join cnv.servico svc");
        sql.append(" inner join cnv.consignataria csa");
        sql.append(" inner join ade.registroServidor rse");
        sql.append(" inner join rse.statusRegistroServidor srs");
        sql.append(" inner join rse.servidor ser");

        if (tipoEntidade != null && tipoEntidade.equals("EST")) {
            sql.append(" inner join rse.orgao org2");
            sql.append(" inner join org2.estabelecimento est");
            sql.append(" inner join est.orgaoSet org ");
        } else {
            sql.append(" inner join rse.orgao org");
            sql.append(" inner join org.estabelecimento est");
        }

        if (!TextHelper.isNull(sboCodigo)) {
            sql.append(" inner join rse.subOrgao sbo");
        }

        if (!TextHelper.isNull(uniCodigo)) {
            sql.append(" inner join rse.unidade uni");
        }

        if ((useParcelaPeriodo && useParcela) || ((useParcelaPeriodo || useParcela) && !innerJoin)) {
            if (useParcela) {
                sql.append(" left outer join ade.parcelaDescontoSet prd");
                if (!periodoUnico) {
                    sql.append(" with prd.prdDataDesconto between :periodoIni and :periodoFim");
                } else {
                    sql.append(" with prd.prdDataDesconto = :periodoIni");
                }
                sql.append(" and prd.statusParcelaDesconto.spdCodigo in ('");
                sql.append(CodedValues.SPD_REJEITADAFOLHA).append("','");
                sql.append(CodedValues.SPD_LIQUIDADAFOLHA).append("','");
                sql.append(CodedValues.SPD_LIQUIDADAMANUAL).append("')");
                sql.append(" left outer join prd.statusParcelaDesconto spd1");
                sql.append(" left outer join prd.tipoDesconto tde1");
                sql.append(" left outer join prd.ocorrenciaParcelaSet ocp1");
            }

            if (useParcelaPeriodo) {
                sql.append(" left outer join ade.parcelaDescontoPeriodoSet pdp");
                if (!periodoUnico) {
                    sql.append(" with pdp.prdDataDesconto between :periodoIni and :periodoFim");
                } else {
                    sql.append(" with pdp.prdDataDesconto = :periodoIni");
                }
                sql.append(" and pdp.statusParcelaDesconto.spdCodigo in ('");
                sql.append(CodedValues.SPD_EMABERTO).append("','");
                sql.append(CodedValues.SPD_EMPROCESSAMENTO).append("','");
                sql.append(CodedValues.SPD_SEM_RETORNO).append("')");
                sql.append(" left outer join pdp.statusParcelaDesconto spd2");
                sql.append(" left outer join pdp.tipoDesconto tde2");
                sql.append(" left outer join pdp.ocorrenciaParcelaPeriodoSet ocp2");
            }

        } else if (useParcela) {
            sql.append(" inner join ade.parcelaDescontoSet prd");
            if (!periodoUnico) {
                sql.append(" with prd.prdDataDesconto between :periodoIni and :periodoFim");
            } else {
                sql.append(" with prd.prdDataDesconto = :periodoIni");
            }
            sql.append(" and prd.statusParcelaDesconto.spdCodigo in ('");
            sql.append(CodedValues.SPD_REJEITADAFOLHA).append("','");
            sql.append(CodedValues.SPD_LIQUIDADAFOLHA).append("','");
            sql.append(CodedValues.SPD_LIQUIDADAMANUAL).append("')");
            sql.append(" inner join prd.statusParcelaDesconto spd1");
            sql.append(" left outer join prd.tipoDesconto tde1");
            sql.append(" left outer join prd.ocorrenciaParcelaSet ocp1");

        } else if (useParcelaPeriodo) {
            sql.append(" inner join ade.parcelaDescontoPeriodoSet pdp");
            if (!periodoUnico) {
                sql.append(" with pdp.prdDataDesconto between :periodoIni and :periodoFim");
            } else {
                sql.append(" with pdp.prdDataDesconto = :periodoIni");
            }
            sql.append(" and pdp.statusParcelaDesconto.spdCodigo in ('");
            sql.append(CodedValues.SPD_EMABERTO).append("','");
            sql.append(CodedValues.SPD_EMPROCESSAMENTO).append("','");
            sql.append(CodedValues.SPD_SEM_RETORNO).append("')");
            sql.append(" inner join pdp.statusParcelaDesconto spd2");
            sql.append(" left outer join pdp.tipoDesconto tde2");
            sql.append(" left outer join pdp.ocorrenciaParcelaPeriodoSet ocp2");
        }

        sql.append(" left outer join ade.correspondente cor");
        sql.append(" left outer join ade.coeficienteDescontoSet cde");

        if (relatorioDescontos) {
            sql.append(" inner join ade.despesaIndividualSet des ");

            if (!TextHelper.isNull(plaCodigo)) {
                sql.append(" inner join des.plano pla ");
            }

            if (!TextHelper.isNull(echCodigo)) {
                sql.append(" inner join des.permissionario per ");
            }
        }

        sql.append(" where (ade.adeIntFolha = ").append(CodedValues.INTEGRA_FOLHA_SIM);
        sql.append(" or (ade.adeIntFolha = ").append(CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO);
        if (useParcelaPeriodo && useParcela) {
            sql.append(" and (prd.autDesconto.adeCodigo is not null or pdp.autDesconto.adeCodigo is not null)");
        } else if (useParcela) {
            sql.append(" and prd.autDesconto.adeCodigo is not null");
        } else if (useParcelaPeriodo) {
            sql.append(" and pdp.autDesconto.adeCodigo is not null");
        } else {
            sql.append(" and 1=2");
        }
        sql.append("))");

        sql.append(" and ((sad.sadCodigo in ('");
        sql.append(CodedValues.SAD_DEFERIDA).append("','");
        sql.append(CodedValues.SAD_EMANDAMENTO).append("','");
        sql.append(CodedValues.SAD_ESTOQUE).append("','");
        sql.append(CodedValues.SAD_ESTOQUE_MENSAL).append("','");
        sql.append(CodedValues.SAD_AGUARD_LIQUIDACAO).append("','");
        sql.append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("'");
        if (useParcelaPeriodo || useParcela) {
            sql.append(",'").append(CodedValues.SAD_ESTOQUE_NAO_LIBERADO).append("'");
        }
        sql.append(")) or (sad.sadCodigo in ('");
        sql.append(CodedValues.SAD_SUSPENSA).append("','");
        sql.append(CodedValues.SAD_INDEFERIDA).append("','");
        sql.append(CodedValues.SAD_SUSPENSA_CSE).append("','");
        sql.append(CodedValues.SAD_CANCELADA).append("','");
        sql.append(CodedValues.SAD_LIQUIDADA).append("','");
        sql.append(CodedValues.SAD_CONCLUIDO).append("','");
        sql.append(CodedValues.SAD_ENCERRADO).append("','");
        sql.append(CodedValues.SAD_EMCARENCIA).append("')");

        if (useParcelaPeriodo && useParcela) {
            sql.append(" and (prd.autDesconto.adeCodigo is not null or pdp.autDesconto.adeCodigo is not null)))");
        } else if (useParcela) {
            sql.append(" and prd.autDesconto.adeCodigo is not null))");
        } else if (useParcelaPeriodo) {
            sql.append(" and pdp.autDesconto.adeCodigo is not null))");
        } else {
            sql.append(" and 1=2))");
        }

        if (temStatus) {
            sql.append(" AND srs.srsCodigo").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        }

        // Contratos do periodo informado, ou anteriores, ...
        if (!periodoUnico) {
            sql.append(" and (ade.adeAnoMesIni between :periodoIni and :periodoFim");
        } else {
            sql.append(" and (ade.adeAnoMesIni = :periodoIni");
        }
        // Ou contratos reimplantados que tem parcela criada no período solicitado.
        if (useParcelaPeriodo && useParcela) {
            sql.append(" or prd.autDesconto.adeCodigo is not null or pdp.autDesconto.adeCodigo is not null)");
        } else if (useParcela) {
            sql.append(" or prd.autDesconto.adeCodigo is not null)");
        } else if (useParcelaPeriodo) {
            sql.append(" or pdp.autDesconto.adeCodigo is not null)");
        } else {
            sql.append(")");
        }

        // Restrição para pegar somente a última ocorrência da parcela
        if (useParcela) {
            sql.append(" and not exists (");
            sql.append(" select 1 from OcorrenciaParcela ocpMaior1");
            sql.append(" where ocp1.parcelaDesconto.prdCodigo = ocpMaior1.parcelaDesconto.prdCodigo");
            sql.append("   and ocpMaior1.ocpData > ocp1.ocpData");
            sql.append(")");
        }
        if (useParcelaPeriodo) {
            sql.append(" and not exists (");
            sql.append(" select 1 from OcorrenciaParcelaPeriodo ocpMaior2");
            sql.append(" where ocp2.parcelaDescontoPeriodo.prdCodigo = ocpMaior2.parcelaDescontoPeriodo.prdCodigo");
            sql.append("   and ocpMaior2.ocpData > ocp2.ocpData");
            sql.append(")");
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            if (tipoEntidade != null && tipoEntidade.equals("EST")) {
                sql.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
            } else {
                sql.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
            }
        }

        if (!TextHelper.isNull(estCodigo)) {
            sql.append(" and est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if ((corCodigos != null && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            sql.append(" and ade.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigos", corCodigos));
        }

        if (!TextHelper.isNull(sboCodigo)) {
            sql.append(" and sbo.sboCodigo ").append(criaClausulaNomeada("sboCodigo", sboCodigo));
        }

        if (!TextHelper.isNull(uniCodigo)) {
            sql.append(" and uni.uniCodigo ").append(criaClausulaNomeada("uniCodigo", uniCodigo));
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            sql.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            sql.append(" and sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if (nseCodigos != null && !nseCodigos.isEmpty()) {
            sql.append(" and cnv.servico.nseCodigo ").append(criaClausulaNomeada("nseCodigos", nseCodigos));
        }

        if (spdCodigos != null && !spdCodigos.isEmpty()) {
            if (spdCodigos.contains(CodedValues.SPD_EMABERTO)) {
                // Se o usuário selecionou o status em aberto, faz uma cláusula para pegar contratos
                // que ainda não tem parcela para o período
                if (useParcelaPeriodo && useParcela) {
                    sql.append(" and (spd1.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
                    sql.append(" or spd2.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
                    sql.append(" or (prd.autDesconto.adeCodigo is null and pdp.autDesconto.adeCodigo is null))");
                } else if (useParcela) {
                    sql.append(" and (spd1.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
                    sql.append(" or prd.autDesconto.adeCodigo is null)");
                } else if (useParcelaPeriodo) {
                    sql.append(" and (spd2.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
                    sql.append(" or pdp.autDesconto.adeCodigo is null)");
                } else {
                    sql.append(" and 1=1");
                }
            } else if (useParcelaPeriodo && useParcela) {
                sql.append(" and (spd1.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
                sql.append(" or spd2.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos)).append(")");
            } else if (useParcela) {
                sql.append(" and spd1.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
            } else if (useParcelaPeriodo) {
                sql.append(" and spd2.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
            } else {
                sql.append(" and 1=2");
            }
        }

        if (tipoEntidade != null && tipoEntidade.equals("EST")) {
            sql.append(" and cnv.orgao.orgCodigo = org2.orgCodigo");
        } else {
            sql.append(" and cnv.orgao.orgCodigo = org.orgCodigo");
        }

        if (relatorioDescontos) {
            if (!TextHelper.isNull(plaCodigo)) {
                sql.append(" and pla.plaCodigo ").append(criaClausulaNomeada("plaCodigo", plaCodigo));
            }

            if (!TextHelper.isNull(echCodigo)) {
                sql.append(" and per.enderecoConjHabitacional.echCodigo ").append(criaClausulaNomeada("echCodigo", echCodigo));
            }

            if (!TextHelper.isNull(cnvCodVerba)) {
                sql.append(" and cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
            }
        }

        if (lstPrdRealizado != null && !lstPrdRealizado.isEmpty()) {
            // Se não possui parcela no período, não deve retornar nada e considera somente tb_parcela_desconto
            if (!useParcela) {
                sql.append(" and 1 = 2 ");
            } else {
                boolean filtrarRealizadosDiferentes = false;
                sql.append(" and (");
                if (lstPrdRealizado.contains(CodedValues.REL_FILTRO_VLR_REALIZADO_MENOR_PREVISTO)) {
                    filtrarRealizadosDiferentes = true;
                    sql.append(" prd.prdVlrRealizado < prd.prdVlrPrevisto ");
                }
                if (lstPrdRealizado.contains(CodedValues.REL_FILTRO_VLR_REALIZADO_IGUAL_PREVISTO)) {
                    if (filtrarRealizadosDiferentes) {
                        sql.append(" or ");
                    }
                    filtrarRealizadosDiferentes = true;
                    sql.append(" prd.prdVlrRealizado = prd.prdVlrPrevisto ");
                }
                if (lstPrdRealizado.contains(CodedValues.REL_FILTRO_VLR_REALIZADO_MAIOR_PREVISTO)) {
                    if (filtrarRealizadosDiferentes) {
                        sql.append(" or ");
                    }
                    filtrarRealizadosDiferentes = true;
                    sql.append(" prd.prdVlrRealizado > prd.prdVlrPrevisto ");
                }
                sql.append(") ");
            }
        }

        if (!TextHelper.isNull(matricula)) {
            sql.append(" and rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", matricula));
        }

        if (!TextHelper.isNull(cpf)) {
            sql.append(" and ser.serCpf ").append(criaClausulaNomeada("serCpf", cpf));
        }

        // ORDER BY CAMPO_GRUPO, SIT_PARCELA, SER_NOME
        final String ordenacao = " order by 1";

        sql.append(ordenacao);

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        }

        if (!periodoUnico) {
            defineValorClausulaNomeada("periodoIni", parseDateString(periodoIni), query);
            defineValorClausulaNomeada("periodoFim", parseDateString(periodoFim), query);
        } else {
            defineValorClausulaNomeada("periodoIni", parseDateString(periodoIni), query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }

        if ((corCodigos != null && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            defineValorClausulaNomeada("corCodigos", corCodigos, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (!TextHelper.isNull(sboCodigo)) {
            defineValorClausulaNomeada("sboCodigo", sboCodigo, query);
        }

        if (!TextHelper.isNull(uniCodigo)) {
            defineValorClausulaNomeada("uniCodigo", uniCodigo, query);
        }

        if (spdCodigos != null && !spdCodigos.isEmpty() && (useParcela || useParcelaPeriodo)) {
            defineValorClausulaNomeada("spdCodigos", spdCodigos, query);
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        if (relatorioDescontos && !TextHelper.isNull(plaCodigo)) {
            defineValorClausulaNomeada("plaCodigo", plaCodigo, query);
        }

        if (relatorioDescontos && !TextHelper.isNull(echCodigo)) {
            defineValorClausulaNomeada("echCodigo", echCodigo, query);
        }

        if (relatorioDescontos && !TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }

        if (!TextHelper.isNull(matricula)) {
            defineValorClausulaNomeada("rseMatricula", matricula, query);
        }

        if (!TextHelper.isNull(cpf)) {
            defineValorClausulaNomeada("serCpf", cpf, query);
        }

        if (nseCodigos != null && !nseCodigos.isEmpty()) {
            defineValorClausulaNomeada("nseCodigos", nseCodigos, query);
        }

        return query;
    }
}
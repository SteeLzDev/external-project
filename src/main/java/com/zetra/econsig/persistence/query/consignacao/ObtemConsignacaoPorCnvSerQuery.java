package com.zetra.econsig.persistence.query.consignacao;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemConsignacaoPorCnvSerQuery</p>
 * <p>Description: Recupera consignação de acordo com convênio e servidor dados</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConsignacaoPorCnvSerQuery extends HQuery {

    public String codVerba;
    public String cnvCodigo;
    public String csaCodigo;
    public String rseCodigo;
    public String rseMatricula;
    public String serCpf;
    public String orgIdentificador;
    public String svcIdentificador;
    public String estIdentificador;
    public List<String> sadCodigos;
    public boolean cnvAtivo;
    public TransferObject criterio;
    public String nseCodigo;
    public Object adeDataUltConciliacao;

    public boolean buscaContratoBeneficio = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String tipo = null;
        String codigo = null;

        Long adeNumero = null;
        String adeIndice = null;
        Date adeAnoMesIni = null;
        BigDecimal adeVlr = null;
        String adeCodReg = null;
        String adeIdentificador = null;

        // Modulo Beneficio
        String numeroContratoBenificio = null;
        String tipoLancamento = null;

        if (criterio != null) {
            // Critérios Gerais
            if (!TextHelper.isNull(criterio.getAttribute("tipo"))) {
                tipo = criterio.getAttribute("tipo").toString();
            }
            if (!TextHelper.isNull(criterio.getAttribute("codigo"))) {
                codigo = criterio.getAttribute("codigo").toString();
            }

            // Critérios baseados nos dados da autorização
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ADE_NUMERO))) {
                adeNumero = Long.valueOf(criterio.getAttribute(Columns.ADE_NUMERO).toString());
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ADE_INDICE))) {
                adeIndice = criterio.getAttribute(Columns.ADE_INDICE).toString();
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ADE_ANO_MES_INI))) {
                try {
                    adeAnoMesIni = DateHelper.toSQLDate(DateHelper.parse(criterio.getAttribute(Columns.ADE_ANO_MES_INI).toString(), "yyyy-MM-dd"));
                } catch (ParseException ex) {
                    throw new HQueryException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema(), ex);
                }
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ADE_VLR))) {
                adeVlr = new BigDecimal(criterio.getAttribute(Columns.ADE_VLR).toString());
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ADE_COD_REG))) {
                adeCodReg = criterio.getAttribute(Columns.ADE_COD_REG).toString();
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ADE_IDENTIFICADOR))) {
                adeIdentificador = criterio.getAttribute(Columns.ADE_IDENTIFICADOR).toString();
            }

            if (!TextHelper.isNull(criterio.getAttribute("numeroContratoBenificio"))) {
                numeroContratoBenificio = criterio.getAttribute("numeroContratoBenificio").toString();
            }
            if (!TextHelper.isNull(criterio.getAttribute("tipoLancamento"))) {
                tipoLancamento = criterio.getAttribute("tipoLancamento").toString();
            }
        }

        String corpo =
            "select " +
            "rse.rseCodigo, " +
            "rse.rseMatricula, " +
            "ser.serCodigo, " +
            "ser.serCpf, " +
            "ser.serNome, " +
            "org.orgCodigo, " +
            "org.orgIdentificador, " +
            "cnv.cnvCodigo, " +
            "cnv.cnvCodVerba, " +
            "cnv.cnvCodVerbaRef, " +
            "cnv.statusConvenio.scvCodigo, " +
            "cnv.consignataria.csaCodigo, " +
            "svc.svcCodigo, " +
            "svc.svcIdentificador, " +
            "svc.svcDescricao, " +
            "ade.adeCodigo, " +
            "ade.adeNumero, " +
            "ade.adeIncMargem, " +
            "ade.adeIndice, " +
            "ade.adeIntFolha, " +
            "ade.adeIdentificador, " +
            "ade.adeVlrLiquido, " +
            "ade.adeVlrIof, " +
            "ade.adeVlrTac, " +
            "ade.adeVlrMensVinc, " +
            "ade.adeTaxaJuros, " +
            "ade.adeData, " +
            "ade.adeAnoMesIni, " +
            "ade.adeAnoMesFim, " +
            "ade.adeAnoMesIniRef, " +
            "ade.adeAnoMesFimRef, " +
            "ade.adePrazo, " +
            "to_numeric(coalesce((ade.adePrazo - coalesce(ade.adePrdPagas, 0)), 9999)) as PRAZO_RESTANTE," +
            "coalesce(ade.adePrdPagas, 0), " +
            "ade.statusAutorizacaoDesconto.sadCodigo, " +
            "ade.statusAutorizacaoDesconto.sadDescricao, " +
            "ade.adeVlr, " +
            "ade.adePeriodicidade ";

        if (buscaContratoBeneficio) {
            corpo += ", tla.tlaCodigoPai ";
            corpo += ", cbe.cbeValorTotal ";
            corpo += ", bfc.bfcCpf ";
            corpo += ", cbe.cbeNumero ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from AutDesconto ade");
        corpoBuilder.append(" inner join ade.registroServidor rse");
        corpoBuilder.append(" inner join rse.servidor ser");
        corpoBuilder.append(" inner join ade.verbaConvenio vco");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join rse.orgao org ");

        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" inner join org.estabelecimento est ");
        }

        if (tipo != null && tipo.equalsIgnoreCase("COR")) {
            corpoBuilder.append(" inner join ade.correspondente cor ");
            corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc ");
        }
        corpoBuilder.append(" inner join cnv.servico svc ");

        if (buscaContratoBeneficio) {
            corpoBuilder.append(" inner join ade.contratoBeneficio cbe ");
            corpoBuilder.append(" inner join cbe.beneficiario bfc ");
            corpoBuilder.append(" inner join ade.tipoLancamento tla ");
        }

        corpoBuilder.append(" where 1=1 ");

        if (buscaContratoBeneficio) {
            if (!TextHelper.isNull(numeroContratoBenificio)) {
                corpoBuilder.append(" and cbe.cbeNumero = :numerContratoBeneficio ");
            }
            if (!TextHelper.isNull(tipoLancamento)) {
                corpoBuilder.append(" and tla.tlaCodigo = :tipoLancamento ");
            }
        }

        if (tipo != null && tipo.equalsIgnoreCase("COR")) {
            corpoBuilder.append(" and cnv.consignataria.csaCodigo = cor.consignataria.csaCodigo ");
            corpoBuilder.append(" and ade.correspondente.corCodigo ").append(criaClausulaNomeada("codigo", codigo));
        }

        if (!TextHelper.isNull(codVerba)) {
            corpoBuilder.append(" AND cnv.cnvCodVerba ").append(criaClausulaNomeada("codVerba", codVerba));
        }

        if (!TextHelper.isNull(cnvCodigo)) {
            corpoBuilder.append(" AND cnv.cnvCodigo ").append(criaClausulaNomeada("cnvCodigo", cnvCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" AND est.estIdentificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" AND org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        }

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" AND rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(serCpf)) {
            if (buscaContratoBeneficio) {
                corpoBuilder.append(" AND bfc.bfcCpf ").append(criaClausulaNomeada("serCpf", serCpf));
            } else {
                corpoBuilder.append(" AND ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
            }
        }

        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append(" AND svc.svcIdentificador ").append(criaClausulaNomeada("svcIdentificador", svcIdentificador));
        }

        if (!TextHelper.isNull(adeDataUltConciliacao)) {
            corpoBuilder.append(" AND (ade.adeDataUltConciliacao ").append(criaClausulaNomeada("adeDataUltConciliacao", adeDataUltConciliacao));
            corpoBuilder.append(" OR  ade.adeDataUltConciliacao IS NULL").append(")");
        }

        if (tipo != null) {
            if (tipo.equalsIgnoreCase("CSA")) {
                if (cnvAtivo) {
                    corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                }
            } else if (tipo.equalsIgnoreCase("COR")) {
                corpoBuilder.append(" AND crc.corCodigo ").append(criaClausulaNomeada("codigo", codigo));
                corpoBuilder.append(" AND crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                if (cnvAtivo) {
                    corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                }
            } else if (tipo.equalsIgnoreCase("ORG")) {
                corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("codigo", codigo));
            } else if (tipo.equalsIgnoreCase("EST")) {
                corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("codigo", codigo));
            }
        }

        if (sadCodigos != null && sadCodigos.size() > 0) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }
        if (adeNumero != null) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }
        if (adeIndice != null && !adeIndice.equals("")) {
            corpoBuilder.append(" AND ade.adeIndice ").append(criaClausulaNomeada("adeIndice", adeIndice));
        }
        if (adeAnoMesIni != null) {
            corpoBuilder.append(" AND ade.adeAnoMesIni ").append(criaClausulaNomeada("adeAnoMesIni", adeAnoMesIni));
        }
        if (adeVlr != null) {
            corpoBuilder.append(" AND ade.adeVlr ").append(criaClausulaNomeada("adeVlr", adeVlr));
        }
        if (adeIdentificador != null && !adeIdentificador.equals("")) {
            corpoBuilder.append(" AND ade.adeIdentificador").append(criaClausulaNomeada("adeIdentificador", adeIdentificador));
        }

        if (adeCodReg != null && !adeCodReg.equals("")) {
            if (adeCodReg.equals(CodedValues.COD_REG_DESCONTO)) {
                // Se codReg igual a 6 - Desconto, por compatibilidade pesquisa
                // também os registros com codReg nulo ou vazio.
                corpoBuilder.append(" AND (NULLIF(TRIM(ade.adeCodReg), '') IS NULL");
                corpoBuilder.append(" OR ade.adeCodReg = '").append(CodedValues.COD_REG_DESCONTO).append("')");
            } else {
                corpoBuilder.append(" AND ade.adeCodReg ").append(criaClausulaNomeada("adeCodReg", adeCodReg));
            }
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" AND svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }

        if (!TextHelper.isNull(codVerba)) {
            defineValorClausulaNomeada("codVerba", codVerba, query);
        }

        if (!TextHelper.isNull(cnvCodigo)) {
            defineValorClausulaNomeada("cnvCodigo", cnvCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (sadCodigos != null && sadCodigos.size() > 0) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        if (!TextHelper.isNull(adeNumero)) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }

        if (!TextHelper.isNull(adeIndice)) {
            defineValorClausulaNomeada("adeIndice", adeIndice, query);
        }

        if (!TextHelper.isNull(adeAnoMesIni)) {
            defineValorClausulaNomeada("adeAnoMesIni", adeAnoMesIni, query);
        }

        if (!TextHelper.isNull(adeVlr)) {
            defineValorClausulaNomeada("adeVlr", adeVlr, query);
        }

        if (!TextHelper.isNull(adeIdentificador)) {
            defineValorClausulaNomeada("adeIdentificador", adeIdentificador, query);
        }

        if (!TextHelper.isNull(adeCodReg) && !adeCodReg.equals(CodedValues.COD_REG_DESCONTO)) {
            defineValorClausulaNomeada("adeCodReg", adeCodReg, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if (!TextHelper.isNull(adeDataUltConciliacao)) {
            defineValorClausulaNomeada("adeDataUltConciliacao", adeDataUltConciliacao, query);
        }

        if (buscaContratoBeneficio) {
            if (!TextHelper.isNull(numeroContratoBenificio)) {
                defineValorClausulaNomeada("numerContratoBeneficio", numeroContratoBenificio, query);
            }

            if (!TextHelper.isNull(tipoLancamento)) {
                defineValorClausulaNomeada("tipoLancamento", tipoLancamento, query);
            }
        }

        if (!TextHelper.isNull(codigo) && query.getQueryString().contains(":codigo")) {
            defineValorClausulaNomeada("codigo", codigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        String[] retorno = {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_CODIGO,
                Columns.SER_CPF,
                Columns.SER_NOME,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.CNV_COD_VERBA_REF,
                Columns.CNV_SCV_CODIGO,
                Columns.CSA_CODIGO,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_INC_MARGEM,
                Columns.ADE_INDICE,
                Columns.ADE_INT_FOLHA,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_VLR_LIQUIDO,
                Columns.ADE_VLR_IOF,
                Columns.ADE_VLR_TAC,
                Columns.ADE_VLR_MENS_VINC,
                Columns.ADE_TAXA_JUROS,
                Columns.ADE_DATA,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_ANO_MES_INI_REF,
                Columns.ADE_ANO_MES_FIM_REF,
                Columns.ADE_PRAZO,
                "PRAZO_RESTANTE",
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_SAD_CODIGO,
                Columns.SAD_DESCRICAO,
                Columns.ADE_VLR,
                Columns.ADE_PERIODICIDADE
        };

        if (buscaContratoBeneficio) {
            String[] temp = Arrays.copyOf(retorno, retorno.length + 4);
            temp[retorno.length] = Columns.TLA_CODIGO_PAI;
            temp[retorno.length + 1] = Columns.CBE_VALOR_TOTAL;
            temp[retorno.length + 2] = Columns.BFC_CPF;
            temp[retorno.length + 3] = Columns.CBE_NUMERO;
            retorno = Arrays.copyOf(temp, temp.length);
        }

        return retorno;
    }
}

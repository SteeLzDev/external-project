package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioConfCadMargemQuery</p>
 * <p>Description: Consulta de relatório de conferência de cadastro de margens</p>
 * <p>Copyright: Copyright (c) 2008/p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioConfCadMargemQuery extends ReportHNativeQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioConfCadMargemQuery.class);

    private static int QUANTIDADE_MAX_MARGENS = 20;

    private List<String> orgCodigos;
    private String estCodigo;
    private String rseTipo;

    private List<String> marCodigos;
    private final List<String> margensExtra = new ArrayList<>();
    private final List<String> rseMargens = new ArrayList<>();
    private final ArrayList<String> fieldList = new ArrayList<>();

    private List<String> srsCodigos;
    private Map<Short, List<String>> sinalMargem;
    private List<String> comprometimentoMargem;
    private String percentualVariacaoMargemInicio;
    private String percentualVariacaoMargemFim;
    private Date penultimoPeriodoHisticoMargem;
    private boolean percentualVariacao = false;


    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
        estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
        rseTipo = (String) criterio.getAttribute(Columns.RSE_TIPO);
        marCodigos = (List<String>) criterio.getAttribute(Columns.MAR_CODIGO);
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
        sinalMargem = (Map<Short, List<String>>) criterio.getAttribute("SINAL_MARGEM");
        comprometimentoMargem = (List<String>) criterio.getAttribute("COMPROMETIMENTO_MARGEM");
        percentualVariacaoMargemInicio = (String) criterio.getAttribute("PERCENTUAL_VARIACAO_MARGEM_INICIO");
        percentualVariacaoMargemFim = (String) criterio.getAttribute("PERCENTUAL_VARIACAO_MARGEM_FIM");
        penultimoPeriodoHisticoMargem = (Date) criterio.getAttribute("PENULTIMO_PERIODO");

        // Margens presentes na tabela de registro servidor
        rseMargens.add(CodedValues.INCIDE_MARGEM_SIM.toString());
        rseMargens.add(CodedValues.INCIDE_MARGEM_SIM_2.toString());
        rseMargens.add(CodedValues.INCIDE_MARGEM_SIM_3.toString());

        // Recupera margens extra selecionadas
        if (marCodigos != null && !marCodigos.isEmpty()) {
            for (String marCodigo : marCodigos) {
                if (!rseMargens.contains(marCodigo)) {
                    margensExtra.add(marCodigo);
                }
            }
        }

        if (responsavel == null && criterio.getAttribute("responsavel") != null) {
            responsavel = (AcessoSistema) criterio.getAttribute("responsavel");
        }

        percentualVariacao = !TextHelper.isNull(percentualVariacaoMargemInicio) || !TextHelper.isNull(percentualVariacaoMargemFim);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        int contadorMargem = 0;
        boolean temStatus = (srsCodigos != null && srsCodigos.size() > 0);
        boolean temEstabelecimento = !TextHelper.isNull(estCodigo);
        List<String> filtroComprometimentoMargem = new ArrayList<>();
        List<String> filtroPercentualVariacaoMargem = new ArrayList<>();
        Map<Short, Integer> mapMarSinal = new HashMap<>();

        //Para calculos para variação de percentual existem mais de uma possibilidade para o usuário escolher (positiva, negativa ou ambos) com isso é necessário criar as condições baseada nos filtros
        StringBuilder sqlPercentualPositivo = new StringBuilder();
        StringBuilder sqlPercentualNegativo = new StringBuilder();
        if (percentualVariacao) {
            if (!TextHelper.isNull(percentualVariacaoMargemInicio) && !TextHelper.isNull(percentualVariacaoMargemFim)) {
                sqlPercentualPositivo.append(" OR ((PARAMETRO_1/X.hma_margem_folha-1)*100 >= :percentualVariacaoMargemInicio AND (PARAMETRO_1/X.hma_margem_folha-1)*100 <= :percentualVariacaoMargemFim and X.mar_codigo = PARAMETRO_2) ");
                sqlPercentualNegativo.append(" OR ((PARAMETRO_1/X.hma_margem_folha-1)*100 <=0 and -(PARAMETRO_1/X.hma_margem_folha-1)*100 >= :percentualVariacaoMargemInicio AND -(PARAMETRO_1/X.hma_margem_folha-1)*100 <= :percentualVariacaoMargemFim and X.mar_codigo = PARAMETRO_2) ");
            } else if (!TextHelper.isNull(percentualVariacaoMargemInicio) && TextHelper.isNull(percentualVariacaoMargemFim)) {
                sqlPercentualPositivo.append(" OR ((PARAMETRO_1/X.hma_margem_folha-1)*100 >= :percentualVariacaoMargemInicio and X.mar_codigo = PARAMETRO_2 ) ");
                sqlPercentualNegativo.append(" OR ((PARAMETRO_1/X.hma_margem_folha-1)*100 <=0 and -(PARAMETRO_1/X.hma_margem_folha-1)*100 >= :percentualVariacaoMargemInicio and X.mar_codigo = PARAMETRO_2) ");
            } else {
                sqlPercentualPositivo.append(" OR ((PARAMETRO_1/X.hma_margem_folha-1)*100 <= :percentualVariacaoMargemFim and X.mar_codigo = PARAMETRO_2 ) ");
                sqlPercentualNegativo.append(" OR ((PARAMETRO_1/X.hma_margem_folha-1)*100 <=0 and -(PARAMETRO_1/X.hma_margem_folha-1)*100 <= :percentualVariacaoMargemFim and X.mar_codigo = PARAMETRO_2) ");
            }
        }

        StringBuilder sql = new StringBuilder("select ");

        // Preenche fieds para gerar query
        getFields();

        Iterator<String> ite = fieldList.iterator();
        while (ite.hasNext()) {
            String field = ite.next();
            sql.append(field);
            if (ite.hasNext()) {
                sql.append(", ");
            }
        }
        sql.append(" from ( ");

        // Reseta o fields que serão reexecutados automaticamente
        fieldList.clear();

        StringBuilder campos = new StringBuilder(" ser.ser_nome as ser_nome, ser.ser_cpf as ser_cpf, rse.rse_matricula as rse_matricula, srs.srs_descricao as srs_descricao, rse.rse_tipo as rse_tipo, ");
        String aliasTabela = "";
        if (marCodigos.contains(CodedValues.INCIDE_MARGEM_SIM.toString())) {
            contadorMargem++;
            campos.append("rse.rse_margem as rse_margem_").append(contadorMargem).append(", rse.rse_margem_rest as rse_margem_rest_").append(contadorMargem).append(", ");
            // Compromentimento Margem 1
            campos.append("(CASE WHEN (coalesce(sum(CASE WHEN (ade.ade_inc_margem = 1 or (coalesce(psi091.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3)) or (coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + rse.rse_margem_rest) = 0 THEN 0 ELSE ((coalesce(sum(CASE WHEN (ade.ade_inc_margem = 1 or (coalesce(psi091.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3)) or (coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0)*100.00)/(coalesce(sum(CASE WHEN (ade.ade_inc_margem = 1 or (coalesce(psi091.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3)) or (coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + rse.rse_margem_rest)) END) AS perc_comp_margem_").append(contadorMargem).append(", ");
            // Filtra compromentimento Margem 1
            filtroComprometimentoMargem.add("perc_comp_margem_" + contadorMargem + ";" + CodedValues.INCIDE_MARGEM_SIM.toString());

            // Filtra percentual variacao Margem 1
            filtroPercentualVariacaoMargem.add("rse_margem_" + contadorMargem + ";" + CodedValues.INCIDE_MARGEM_SIM.toString());
            aliasTabela = "hma"+contadorMargem;
            if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
                campos.append("(rse.rse_margem/(select "+aliasTabela).append(".hma_margem_folha from tb_historico_margem_folha "+aliasTabela).append(" where rse.rse_codigo = "+aliasTabela).append(".rse_codigo AND "+aliasTabela+".hma_periodo = '"+penultimoPeriodoHisticoMargem +"' AND "+aliasTabela+".mar_codigo ='"+CodedValues.INCIDE_MARGEM_SIM.toString()+"')-1)*100 ").append(" as variacaoMargem"+contadorMargem+", ");
                campos.append("(select "+aliasTabela).append(".hma_margem_folha from tb_historico_margem_folha "+aliasTabela).append(" where rse.rse_codigo = "+aliasTabela).append(".rse_codigo AND "+aliasTabela+".hma_periodo = '"+penultimoPeriodoHisticoMargem +"' AND "+aliasTabela+".mar_codigo ='"+CodedValues.INCIDE_MARGEM_SIM.toString()+"')").append(" as margemAnterior"+contadorMargem+", ");
            }
        }

        if (marCodigos.contains(CodedValues.INCIDE_MARGEM_SIM_2.toString())) {
            contadorMargem++;
            campos.append("COALESCE(rse.rse_margem_2, 0.00) as rse_margem_").append(contadorMargem).append(", COALESCE(rse.rse_margem_rest_2, 0.00) as rse_margem_rest_").append(contadorMargem).append(", ");
            // Compromentimento Margem 2
            campos.append("(CASE WHEN (coalesce(sum(CASE WHEN (ade.ade_inc_margem = 2 or (coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,3))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + rse.rse_margem_rest_2) = 0 THEN 0 ELSE ((coalesce(sum(CASE WHEN (ade.ade_inc_margem = 2 or (coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,3))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0)*100.00)/(coalesce(sum(CASE WHEN (ade.ade_inc_margem = 2 or (coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,3))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + rse.rse_margem_rest_2)) END) as perc_comp_margem_").append(contadorMargem).append(", ");
            // Filtra compromentimento Margem 2
            filtroComprometimentoMargem.add("perc_comp_margem_" + contadorMargem + ";" + CodedValues.INCIDE_MARGEM_SIM_2.toString());

            // Filtra percentual variacao Margem 2
            filtroPercentualVariacaoMargem.add("rse_margem_" + contadorMargem + ";" + CodedValues.INCIDE_MARGEM_SIM_2.toString());
            aliasTabela = "hma"+contadorMargem;
            if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
                campos.append("(rse.rse_margem_2/(select "+aliasTabela).append(".hma_margem_folha from tb_historico_margem_folha "+aliasTabela).append(" where rse.rse_codigo = "+aliasTabela).append(".rse_codigo AND "+aliasTabela+".hma_periodo = '"+penultimoPeriodoHisticoMargem +"' AND "+aliasTabela+".mar_codigo ='"+CodedValues.INCIDE_MARGEM_SIM_2.toString()+"')-1)*100 ").append(" as variacaoMargem"+contadorMargem+", ");
                campos.append("(select "+aliasTabela).append(".hma_margem_folha from tb_historico_margem_folha "+aliasTabela).append(" where rse.rse_codigo = "+aliasTabela).append(".rse_codigo AND "+aliasTabela+".hma_periodo = '"+penultimoPeriodoHisticoMargem +"' AND "+aliasTabela+".mar_codigo ='"+CodedValues.INCIDE_MARGEM_SIM_2.toString()+"')").append(" as margemAnterior"+contadorMargem+", ");
            }
        }

        if (marCodigos.contains(CodedValues.INCIDE_MARGEM_SIM_3.toString())) {
            contadorMargem++;
            campos.append("COALESCE(rse.rse_margem_3, 0.00) as rse_margem_").append(contadorMargem).append(", COALESCE(rse.rse_margem_rest_3, 0.00) as rse_margem_rest_").append(contadorMargem).append(", ");
            // Compromentimento Margem 3
            campos.append("(CASE WHEN (coalesce(sum(CASE WHEN (ade.ade_inc_margem = 3 or (coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 1) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,2))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + rse.rse_margem_rest_3) = 0 THEN 0 ELSE ((coalesce(sum(CASE WHEN (ade.ade_inc_margem = 3 or (coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 1) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,2))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0)*100.00)/(coalesce(sum(CASE WHEN (ade.ade_inc_margem = 3 or (coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 1) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,2))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + rse.rse_margem_rest_3)) END) as perc_comp_margem_").append(contadorMargem).append(", ");
            // Filtra compromentimento Margem 3
            filtroComprometimentoMargem.add("perc_comp_margem_" + contadorMargem + ";" + CodedValues.INCIDE_MARGEM_SIM_3.toString());

            // Filtra percentual variacao Margem 3
            filtroPercentualVariacaoMargem.add("rse_margem_" + contadorMargem + ";" + CodedValues.INCIDE_MARGEM_SIM_3.toString());
            aliasTabela = "hma"+contadorMargem;
            if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
                campos.append("(rse.rse_margem_3/(select "+aliasTabela).append(".hma_margem_folha from tb_historico_margem_folha "+aliasTabela).append(" where rse.rse_codigo = "+aliasTabela).append(".rse_codigo AND "+aliasTabela+".hma_periodo = '"+penultimoPeriodoHisticoMargem +"' AND "+aliasTabela+".mar_codigo ='"+CodedValues.INCIDE_MARGEM_SIM_3.toString()+"')-1)*100 ").append(" as variacaoMargem"+contadorMargem+", ");
                campos.append("(select "+aliasTabela).append(".hma_margem_folha from tb_historico_margem_folha "+aliasTabela).append(" where rse.rse_codigo = "+aliasTabela).append(".rse_codigo AND "+aliasTabela+".hma_periodo = '"+penultimoPeriodoHisticoMargem +"' AND "+aliasTabela+".mar_codigo ='"+CodedValues.INCIDE_MARGEM_SIM_3.toString()+"')").append(" as margemAnterior"+contadorMargem+", ");
            }
        }

        if (!margensExtra.isEmpty()) {
            for (String margemExtra : margensExtra) {
                contadorMargem++;
                campos.append("MAX(CASE WHEN mar.mar_codigo = '").append(margemExtra).append("' THEN mrs_margem ELSE NULL END) AS rse_margem_").append(contadorMargem).append(", ");
                campos.append("MAX(CASE WHEN mar.mar_codigo = '").append(margemExtra).append("' THEN mrs_margem_rest ELSE NULL END) AS rse_margem_rest_").append(contadorMargem).append(", ");

                List<Short> margensAfetamDestino = CasamentoMargem.getInstance().getMargemOrigemAfetaDestino(Short.parseShort(margemExtra));
                campos.append("(CASE WHEN (coalesce(sum(CASE WHEN (ade.ade_inc_margem in ('").append(TextHelper.join(margensAfetamDestino, "', '")).append("')) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + mrs.mrs_margem_rest) = 0 THEN 0 ELSE ((coalesce(sum(CASE WHEN (ade.ade_inc_margem in ('").append(TextHelper.join(margensAfetamDestino, "', '")).append("')) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0)*100.00)/(coalesce(sum(CASE WHEN (ade.ade_inc_margem in ('").append(TextHelper.join(margensAfetamDestino, "', '")).append("')) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + mrs.mrs_margem_rest)) END) as perc_comp_margem_").append(contadorMargem).append(", ");

                // Filtra compromentimento Margem 1
                filtroComprometimentoMargem.add("perc_comp_margem_" + contadorMargem + ";" + margemExtra);

                // Filtra percentual variacao Margem 1
                filtroPercentualVariacaoMargem.add("rse_margem_" + contadorMargem + ";" + margemExtra);
                aliasTabela = "hma"+contadorMargem;
                if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
                    campos.append("(MAX(CASE WHEN mar.mar_codigo = '").append(margemExtra).append("' THEN mrs_margem ELSE NULL END)/(select "+aliasTabela).append(".hma_margem_folha from tb_historico_margem_folha "+aliasTabela).append(" where rse.rse_codigo = "+aliasTabela).append(".rse_codigo AND "+aliasTabela+".hma_periodo = '"+penultimoPeriodoHisticoMargem +"' AND "+aliasTabela+".mar_codigo ='"+margemExtra+"')-1)*100").append(" as variacaoMargem"+contadorMargem+", ");
                    campos.append("(select "+aliasTabela).append(".hma_margem_folha from tb_historico_margem_folha "+aliasTabela).append(" where rse.rse_codigo = "+aliasTabela).append(".rse_codigo AND "+aliasTabela+".hma_periodo = '"+penultimoPeriodoHisticoMargem +"' AND "+aliasTabela+".mar_codigo ='"+margemExtra+"')").append(" as margemAnterior"+contadorMargem+", ");
                }
            }
        }

        while (contadorMargem < QUANTIDADE_MAX_MARGENS) {
            contadorMargem++;
            campos.append("to_decimal(0, 2, 2) as rse_margem_").append(contadorMargem).append(", to_decimal(0, 2, 2) as rse_margem_rest_").append(contadorMargem).append(", ");
            campos.append("to_decimal(0, 2, 2) as perc_comp_margem_").append(contadorMargem).append(", ");
        }

        campos.append("concatenar(concatenar(est.est_identificador, ' - '), est.est_nome) as estabelecimento, concatenar(concatenar(org.org_identificador, ' - '), org.org_nome) as orgao");

        if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
            campos.append(",hma.hma_margem_folha, hma.mar_codigo");
        }

        if (!margensExtra.isEmpty()) {
            campos.append(",mrs.mrs_margem, mrs.mrs_margem_rest ");
        }

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append(campos);
        corpoBuilder.append(" FROM tb_servidor ser");
        corpoBuilder.append(" INNER JOIN tb_registro_servidor rse on (ser.ser_codigo = rse.ser_codigo)");
        corpoBuilder.append(" INNER JOIN tb_orgao org on (org.org_codigo = rse.org_codigo)");
        corpoBuilder.append(" INNER JOIN tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        corpoBuilder.append(" INNER JOIN tb_status_registro_servidor srs on (srs.srs_codigo = rse.srs_codigo) ");

        if(percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
            corpoBuilder.append(" LEFT OUTER JOIN tb_historico_margem_folha hma on (rse.rse_codigo = hma.rse_codigo and hma.hma_periodo ").append(criaClausulaNomeada("penultimoPeriodoHisticoMargem", penultimoPeriodoHisticoMargem)).append(") ");
        }

        // Se alguma margem extra foi selecionada, faz join com a tabela específica de margem extra
        if (!margensExtra.isEmpty()) {
            corpoBuilder.append(" LEFT OUTER JOIN tb_margem_registro_servidor mrs on (rse.rse_codigo = mrs.rse_codigo ");
            corpoBuilder.append(" AND mrs.mar_codigo ").append(criaClausulaNomeada("margensExtra", margensExtra)).append(") ");
            corpoBuilder.append(" LEFT OUTER JOIN tb_margem mar on (mar.mar_codigo = mrs.mar_codigo) ");
        }

        corpoBuilder.append(" LEFT OUTER JOIN tb_aut_desconto ade on (ade.rse_codigo = rse.rse_codigo ");
        corpoBuilder.append(" AND ade.sad_codigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')) ");

        corpoBuilder.append(" LEFT OUTER JOIN tb_param_sist_consignante psi091 on (psi091.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3).append("') ");
        corpoBuilder.append(" LEFT OUTER JOIN tb_param_sist_consignante psi173 on (psi173.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_2_3_CASADAS).append("') ");
        corpoBuilder.append(" LEFT OUTER JOIN tb_param_sist_consignante psi218 on (psi218.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA).append("') ");
        corpoBuilder.append(" LEFT OUTER JOIN tb_param_sist_consignante psi219 on (psi219.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA).append("') ");

        corpoBuilder.append(" WHERE 1 = 1 ");

        if (!TextHelper.isNull(rseTipo)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("rse.rse_tipo", "rseTipo", CodedValues.LIKE_MULTIPLO + rseTipo + CodedValues.LIKE_MULTIPLO));
        }
        if (temStatus) {
            corpoBuilder.append(" AND srs.srs_codigo").append(criaClausulaNomeada("srsCodigo", srsCodigos));
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND org.org_codigo").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (temEstabelecimento) {
            corpoBuilder.append(" AND est.est_codigo").append(criaClausulaNomeada("estCodigo", estCodigo));
        }
        if (responsavel.isSer()) {
            corpoBuilder.append(" AND rse.rse_codigo").append(criaClausulaNomeada("rseCodigo", responsavel.getRseCodigo()));
        }

        if (sinalMargem != null && !sinalMargem.isEmpty()) {
            for (Short marCodigo : sinalMargem.keySet()) {
                if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO) && marCodigos.contains(marCodigo.toString())) {
                    List<String> sinal = sinalMargem.get(marCodigo);
                    boolean temSinalMargem = (sinal != null && sinal.size() > 0);

                    if (temSinalMargem) {
                        if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            corpoBuilder.append(" AND sign(rse.rse_margem_rest) ").append(criaClausulaNomeada("sinalMargem" + marCodigo, sinal));
                        } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            corpoBuilder.append(" AND sign(rse.rse_margem_rest_2) ").append(criaClausulaNomeada("sinalMargem" + marCodigo, sinal));
                        } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                            corpoBuilder.append(" AND sign(rse.rse_margem_rest_3) ").append(criaClausulaNomeada("sinalMargem" + marCodigo, sinal));
                        } else {
                            corpoBuilder.append(" AND sign(mrs.mrs_margem_rest) ").append(criaClausulaNomeada("sinalMargem" + marCodigo, sinal));
                        }
                    }

                    boolean sinalPositivo = false;
                    boolean sinalNegativo = false;

                    for (String valorSinal : sinal) {
                        if(!sinalPositivo && (valorSinal.equals("1") || valorSinal.equals("0"))) {
                            sinalPositivo = true;
                        } else if(!sinalNegativo && valorSinal.equals("-1")) {
                            sinalNegativo = true;
                        }
                    }

                    if(sinalPositivo && !sinalNegativo) {
                        mapMarSinal.put(marCodigo, 1);
                    } else if (sinalPositivo && sinalNegativo) {
                        mapMarSinal.put(marCodigo, 2);
                    } else {
                        mapMarSinal.put(marCodigo, -1);
                    }
                }
            }
        }

        corpoBuilder.append(" GROUP BY ser.ser_nome, ser.ser_cpf, rse.rse_matricula, srs.srs_descricao, rse.rse_tipo, ");
        corpoBuilder.append("rse.rse_margem, rse.rse_margem_rest, rse.rse_margem_2, rse.rse_margem_rest_2, rse.rse_margem_3, rse.rse_margem_rest_3, ");

        if (!margensExtra.isEmpty()) {
            corpoBuilder.append("mrs.mrs_margem, mrs.mrs_margem_rest, ");
        }

        corpoBuilder.append("est.est_codigo, est.est_identificador, est.est_nome, org.org_codigo, org.org_identificador, org.org_nome");

        corpoBuilder.append(" ORDER BY est.est_codigo, org.org_codigo, ser.ser_nome ");

        sql.append(corpoBuilder);
        sql.append(") X ");
        sql.append(" WHERE 1 = 1 ");

        if (comprometimentoMargem != null && !comprometimentoMargem.isEmpty()) {
            sql.append(" AND ( 1 = 2 ");
            for (String comprometimento : comprometimentoMargem) {
                for (String filtro : filtroComprometimentoMargem) {
                    String[] sptFiltro = filtro.split(";");
                    Short marCodigo = Short.valueOf(sptFiltro[1]);
                    String campoCompMargem = sptFiltro[0];
                    boolean sinalNegativo = mapMarSinal.get(marCodigo) != null && mapMarSinal.get(marCodigo) == -1;

                    if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MENOR_ZERO)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" <= 0.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" <= 0.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" <= 0.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_0_A_10)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 0.00 ");
                            sql.append(" AND ").append("-(").append(campoCompMargem).append(")").append(" <= 10.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 0.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 10.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 0.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 10.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_10_A_20)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 10.00 ");
                            sql.append(" AND ").append("-(").append(campoCompMargem).append(")").append(" <= 20.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 10.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 20.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 10.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 20.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_20_A_30)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 20.00 ");
                            sql.append(" AND ").append("-(").append(campoCompMargem).append(")").append(" <= 30.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 20.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 30.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 20.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 30.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_30_A_40)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 30.00 ");
                            sql.append(" AND ").append("-(").append(campoCompMargem).append(")").append(" <= 40.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 30.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 40.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 30.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 40.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_40_A_50)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 40.00 ");
                            sql.append(" AND ").append("-(").append(campoCompMargem).append(")").append(" <= 50.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 40.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 50.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 40.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 50.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_50_A_60)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 50.00 ");
                            sql.append(" AND ").append("-(").append(campoCompMargem).append(")").append(" <= 60.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 50.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 60.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 50.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 60.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_60_A_70)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 60.00 ");
                            sql.append(" AND ").append("-(").append(campoCompMargem).append(")").append(" <= 70.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 60.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 70.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 60.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 70.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_70_A_80)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 70.00 ");
                            sql.append(" AND ").append("-(").append(campoCompMargem).append(")").append(" <= 80.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 70.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 80.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 70.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 80.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_80_A_90)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 80.00 ");
                            sql.append(" AND ").append("-(").append(campoCompMargem).append(")").append(" <= 90.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 80.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 90.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 80.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 90.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_90_A_100)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 90.00 ");
                            sql.append(" AND ").append("-(").append(campoCompMargem).append(")").append(" <= 100.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 90.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 100.00 end) ");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 90.00 ");
                            sql.append(" AND ").append(campoCompMargem).append(" <= 100.00) ");
                        }
                    } else if (comprometimento.equals(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MAIOR_CEM)) {
                        if(sinalNegativo) {
                            sql.append(" OR ( case when sign(").append(campoCompMargem).append(") = -1 then ").append("-(").append(campoCompMargem).append(")").append(" > 100.00 ");
                            sql.append(" else ").append(campoCompMargem).append(" > 100.00 end)");
                        } else {
                            sql.append(" OR (").append(campoCompMargem).append(" > 100.00) ");
                        }
                    }
                }
            }
            sql.append(" ) ");
        }

        if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
            sql.append(" AND ( 1 = 2 ");
            for (String filtro : filtroPercentualVariacaoMargem) {
                String[] sptFiltro = filtro.split(";");
                Short marCodigo = Short.valueOf(sptFiltro[1]);
                String campoMargem = sptFiltro[0];

                if(mapMarSinal.get(marCodigo) != null && mapMarSinal.get(marCodigo) == 1) {
                   sql.append(sqlPercentualPositivo.toString().replace("PARAMETRO_1",campoMargem).replace("PARAMETRO_2",String.valueOf(marCodigo)));
                } else if(mapMarSinal.get(marCodigo) != null && mapMarSinal.get(marCodigo) == 2) {
                    sql.append(sqlPercentualPositivo.toString().replace("PARAMETRO_1",campoMargem).replace("PARAMETRO_2",String.valueOf(marCodigo)));
                    sql.append(sqlPercentualNegativo.toString().replace("PARAMETRO_1",campoMargem).replace("PARAMETRO_2",String.valueOf(marCodigo)));
                } else if(mapMarSinal.get(marCodigo) != null && mapMarSinal.get(marCodigo) == -1) {
                    sql.append(sqlPercentualNegativo.toString().replace("PARAMETRO_1",campoMargem).replace("PARAMETRO_2",String.valueOf(marCodigo)));
                } else {
                    sql.append(sqlPercentualPositivo.toString().replace("PARAMETRO_1",campoMargem).replace("PARAMETRO_2",String.valueOf(marCodigo)));
                }
            }
            sql.append(" ) ");
            sql.append(" GROUP BY X.ser_nome, X.ser_cpf, X.rse_matricula, X.srs_descricao, X.rse_tipo, ");
            sql.append("X.rse_margem_1, X.rse_margem_rest_1, X.rse_margem_2, X.rse_margem_rest_2, X.rse_margem_3, X.rse_margem_rest_3 ");

            if (!margensExtra.isEmpty()) {
                sql.append(",X.mrs_margem, X.mrs_margem_rest ");
            }
        }

        LOG.debug("QUERY: " + sql.toString());
        Query<Object[]> queryInst = instanciarQuery(session, sql.toString());

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, queryInst);
        }

        if (temEstabelecimento) {
            defineValorClausulaNomeada("estCodigo", estCodigo, queryInst);
        }

        if (responsavel.isSer()) {
            defineValorClausulaNomeada("rseCodigo", responsavel.getRseCodigo(), queryInst);
        }

        if (temStatus) {
            defineValorClausulaNomeada("srsCodigo", srsCodigos, queryInst);
        }

        if (!TextHelper.isNull(rseTipo)) {
            defineValorClausulaNomeada("rseTipo", CodedValues.LIKE_MULTIPLO + rseTipo + CodedValues.LIKE_MULTIPLO, queryInst);
        }

        if (sinalMargem != null && !sinalMargem.isEmpty()) {
            for (Short marCodigo : sinalMargem.keySet()) {
                if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO) && marCodigos.contains(marCodigo.toString())) {
                    List<String> sinal = sinalMargem.get(marCodigo);
                    boolean temSinalMargem = (sinal != null && sinal.size() > 0);

                    if (temSinalMargem) {
                        if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            defineValorClausulaNomeada("sinalMargem" + marCodigo, sinal, queryInst);
                        } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            defineValorClausulaNomeada("sinalMargem" + marCodigo, sinal, queryInst);
                        } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                            defineValorClausulaNomeada("sinalMargem" + marCodigo, sinal, queryInst);
                        } else {
                            defineValorClausulaNomeada("sinalMargem" + marCodigo, sinal, queryInst);
                        }
                    }
                }
            }
        }

        if (!margensExtra.isEmpty()) {
            defineValorClausulaNomeada("margensExtra", margensExtra, queryInst);
        }

        if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
            defineValorClausulaNomeada("penultimoPeriodoHisticoMargem", penultimoPeriodoHisticoMargem, queryInst);
        }

        if (percentualVariacao) {
            if (!TextHelper.isNull(percentualVariacaoMargemInicio)) {
                defineValorClausulaNomeada("percentualVariacaoMargemInicio", percentualVariacaoMargemInicio, queryInst);
            }
            if (!TextHelper.isNull(percentualVariacaoMargemFim)) {
                defineValorClausulaNomeada("percentualVariacaoMargemFim", percentualVariacaoMargemFim, queryInst);
            }
        }

        return queryInst;
    }

    @Override
    protected String[] getFields() {
        int contadorMargem = 0;
        fieldList.add("ser_nome");
        fieldList.add("ser_cpf");
        fieldList.add("rse_matricula");
        fieldList.add("srs_descricao");
        fieldList.add("rse_tipo");

        if (marCodigos.contains(CodedValues.INCIDE_MARGEM_SIM.toString())) {
            contadorMargem++;
            fieldList.add("rse_margem_".concat(String.valueOf(contadorMargem)));
            fieldList.add("rse_margem_rest_".concat(String.valueOf(contadorMargem)));
            fieldList.add("perc_comp_margem_".concat(String.valueOf(contadorMargem)));
            if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
                fieldList.add("variacaoMargem".concat(String.valueOf(contadorMargem)));
                fieldList.add("margemAnterior".concat(String.valueOf(contadorMargem)));
            }

        }
        if (marCodigos.contains(CodedValues.INCIDE_MARGEM_SIM_2.toString())) {
            contadorMargem++;
            fieldList.add("rse_margem_".concat(String.valueOf(contadorMargem)));
            fieldList.add("rse_margem_rest_".concat(String.valueOf(contadorMargem)));
            fieldList.add("perc_comp_margem_".concat(String.valueOf(contadorMargem)));
            if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
                fieldList.add("variacaoMargem".concat(String.valueOf(contadorMargem)));
                fieldList.add("margemAnterior".concat(String.valueOf(contadorMargem)));
            }
        }
        if (marCodigos.contains(CodedValues.INCIDE_MARGEM_SIM_3.toString())) {
            contadorMargem++;
            fieldList.add("rse_margem_".concat(String.valueOf(contadorMargem)));
            fieldList.add("rse_margem_rest_".concat(String.valueOf(contadorMargem)));
            fieldList.add("perc_comp_margem_".concat(String.valueOf(contadorMargem)));
            if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
                fieldList.add("variacaoMargem".concat(String.valueOf(contadorMargem)));
                fieldList.add("margemAnterior".concat(String.valueOf(contadorMargem)));
            }
        }
        if (!margensExtra.isEmpty()) {
            for (int i = 0; i < margensExtra.size(); i++) {
                contadorMargem++;
                fieldList.add("rse_margem_".concat(String.valueOf(contadorMargem)));
                fieldList.add("rse_margem_rest_".concat(String.valueOf(contadorMargem)));
                fieldList.add("perc_comp_margem_".concat(String.valueOf(contadorMargem)));
                if (percentualVariacao && !TextHelper.isNull(penultimoPeriodoHisticoMargem)) {
                    fieldList.add("variacaoMargem".concat(String.valueOf(contadorMargem)));
                    fieldList.add("margemAnterior".concat(String.valueOf(contadorMargem)));
                }
            }
        }

        while (contadorMargem < QUANTIDADE_MAX_MARGENS) {
            contadorMargem++;
            fieldList.add("rse_margem_".concat(String.valueOf(contadorMargem)));
            fieldList.add("rse_margem_rest_".concat(String.valueOf(contadorMargem)));
            fieldList.add("perc_comp_margem_".concat(String.valueOf(contadorMargem)));
        }

        fieldList.add("estabelecimento");
        fieldList.add("orgao");

        return fieldList.toArray(new String[]{});
    }
}

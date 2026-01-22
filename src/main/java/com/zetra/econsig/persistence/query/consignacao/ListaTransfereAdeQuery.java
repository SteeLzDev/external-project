package com.zetra.econsig.persistence.query.consignacao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTransfereAdeQuery</p>
 * <p>Description: Listagem de transferências de autorização.</p>
 * <p>Copyright: Copyright (c) 2009-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Alexandre Goncalves, Igor Lucas, Leonel Martins
 */
public class ListaTransfereAdeQuery extends HNativeQuery {

    public String csaCodigoOrigem;
    public String csaCodigoDestino;
    public String svcCodigoOrigem;
    public String svcCodigoDestino;
    public String orgCodigo;
    public List<String> sadCodigo;
    public Date periodoIni;
    public Date periodoFim;
    public List<Long> adeNumero;
    public String rseMatricula;
    public String serCpf;
    public boolean count = false;
    public boolean somenteConveniosAtivos = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");


        if (count) {
            corpoBuilder.append("count(*) ");
        } else {
            corpoBuilder.append("ade.ade_codigo, ");
            corpoBuilder.append("ade.ade_numero, ");
            corpoBuilder.append("ade.ade_identificador, ");
            corpoBuilder.append("to_string(ade.ade_tipo_vlr), ");
            corpoBuilder.append("ade.ade_vlr, ");
            corpoBuilder.append("ade.ade_vlr_folha, ");
            corpoBuilder.append("ade.ade_prazo, ");
            corpoBuilder.append("ade.ade_prd_pagas, ");
            corpoBuilder.append("ade.ade_data, ");
            corpoBuilder.append("ade.ade_data_status, ");
            corpoBuilder.append("ade.ade_indice, ");
            corpoBuilder.append("ade.ade_cod_reg, ");
            corpoBuilder.append("sad.sad_codigo, ");
            corpoBuilder.append("sad.sad_descricao, ");

            corpoBuilder.append("rse.rse_codigo, ");
            corpoBuilder.append("rse.rse_matricula, ");
            corpoBuilder.append("ser.ser_nome, ");
            corpoBuilder.append("ser.ser_cpf, ");

            corpoBuilder.append("csa.csa_identificador, ");
            corpoBuilder.append("csa.csa_nome, ");
            corpoBuilder.append("csa.csa_nome_abrev, ");
            corpoBuilder.append("svc.svc_identificador, ");
            corpoBuilder.append("svc.svc_descricao, ");
            corpoBuilder.append("cnv.cnv_cod_verba, ");

            corpoBuilder.append("vco2.vco_codigo as VCO_CODIGO_NEW, ");
            corpoBuilder.append("coalesce(nullif(pseOld.pse_vlr, ''), '").append(CodedValues.INCIDE_MARGEM_SIM).append("') as INC_MARGEM_OLD, ");
            corpoBuilder.append("coalesce(nullif(pseNew.pse_vlr, ''), '").append(CodedValues.INCIDE_MARGEM_SIM).append("') as INC_MARGEM_NEW  ");
        }

        corpoBuilder.append(" from tb_aut_desconto ade");
        corpoBuilder.append(" inner join tb_status_autorizacao_desconto sad on (ade.sad_codigo = sad.sad_codigo)");
        corpoBuilder.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
        corpoBuilder.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)");
        corpoBuilder.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
        corpoBuilder.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo)");
        corpoBuilder.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo)");
        corpoBuilder.append(" inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo)");
        corpoBuilder.append(" inner join tb_convenio cnv2 on (cnv.org_codigo = cnv2.org_codigo)");
        corpoBuilder.append(" inner join tb_verba_convenio vco2 on (vco2.cnv_codigo = cnv2.cnv_codigo)");

        if (!count) {
            // Faz left para buscar os parâmetros de serviço de incidência de margem
            corpoBuilder.append(" left outer join tb_param_svc_consignante pseOld on (cnv.svc_codigo = pseOld.svc_codigo and pseOld.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("')");
            corpoBuilder.append(" left outer join tb_param_svc_consignante pseNew on (cnv2.svc_codigo = pseNew.svc_codigo and pseNew.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("')");
        }

        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(csaCodigoOrigem)) {
            corpoBuilder.append(" AND cnv.csa_codigo ").append(criaClausulaNomeada("csaCodigoOrigem", csaCodigoOrigem));
        }
        if (!TextHelper.isNull(svcCodigoOrigem)) {
            corpoBuilder.append(" AND cnv.svc_codigo ").append(criaClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem));
        }

        if (!TextHelper.isNull(csaCodigoDestino)) {
            corpoBuilder.append(" AND cnv2.csa_codigo ").append(criaClausulaNomeada("csaCodigoDestino", csaCodigoDestino));
        } else {
            corpoBuilder.append(" AND cnv2.csa_codigo = cnv.csa_codigo");
        }
        if (!TextHelper.isNull(svcCodigoDestino)) {
            corpoBuilder.append(" AND cnv2.svc_codigo ").append(criaClausulaNomeada("svcCodigoDestino", svcCodigoDestino));
        } else {
            corpoBuilder.append(" AND cnv2.svc_codigo = cnv.svc_codigo");
        }
        // DESENV-20772: Só permite transferências para convênios destino ativos.
        if (somenteConveniosAtivos) {
            corpoBuilder.append(" AND cnv2.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("' ");
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND cnv.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (periodoIni != null) {
            corpoBuilder.append(" AND ade.ade_data >= :periodoIni ");
        }
        if (periodoFim != null) {
            corpoBuilder.append(" AND ade.ade_data <= :periodoFim ");
        }

        if ((sadCodigo != null) && !sadCodigo.isEmpty()) {
            corpoBuilder.append(" AND ade.sad_codigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        }

        if ((adeNumero != null) && !adeNumero.isEmpty()) {
            corpoBuilder.append(" AND ade.ade_numero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }
        // Gera cláusula de Matricula e CPF
        corpoBuilder.append(ListaServidorQuery.gerarClausulaNativaMatriculaCpf(rseMatricula, serCpf, true));

        if (!count) {
            corpoBuilder.append(" ORDER BY ade.ade_numero");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, true, query);
        if (!TextHelper.isNull(csaCodigoOrigem)) {
            defineValorClausulaNomeada("csaCodigoOrigem", csaCodigoOrigem, query);
        }
        if (!TextHelper.isNull(svcCodigoOrigem)) {
            defineValorClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem, query);
        }
        if (!TextHelper.isNull(csaCodigoDestino)) {
            defineValorClausulaNomeada("csaCodigoDestino", csaCodigoDestino, query);
        }
        if (!TextHelper.isNull(svcCodigoDestino)) {
            defineValorClausulaNomeada("svcCodigoDestino", svcCodigoDestino, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if ((sadCodigo != null) && !sadCodigo.isEmpty()) {
            defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
        }
        if ((adeNumero != null) && !adeNumero.isEmpty()) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }
        if (periodoIni != null) {
            defineValorClausulaNomeada("periodoIni", periodoIni, query);
        }
        if (periodoFim != null) {
            defineValorClausulaNomeada("periodoFim", periodoFim, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_VLR,
                Columns.ADE_VLR_FOLHA,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_DATA,
                Columns.ADE_DATA_STATUS,
                Columns.ADE_INDICE,
                Columns.ADE_COD_REG,
                Columns.SAD_CODIGO,
                Columns.SAD_DESCRICAO,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.CNV_COD_VERBA,
                "VCO_CODIGO_NEW",
                "INC_MARGEM_OLD",
                "INC_MARGEM_NEW"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}

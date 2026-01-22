package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioOcorrenciaPermissionarioQuery</p>
 * <p>Description: Relatório de Ocorrência de Permissionário</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public class RelatorioOcorrenciaPermissionarioQuery extends ReportHNativeQuery {

    public String dataIni;
    public String dataFim;
    public String rseMatricula;
    public String serCpf;
    public String echCodigo;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> tocCodigos = new ArrayList<String>();
        tocCodigos.add(CodedValues.TOC_ALTERACAO_TIPO_REGISTRO_SERVIDOR);
        tocCodigos.add(CodedValues.TOC_ALTERACAO_POSTO_REGISTRO_SERVIDOR);

        StringBuilder corpoBuilder = new StringBuilder();

        // Recupera as ocorrências do permissionário
        corpoBuilder.append("select ").append(Columns.OPE_CODIGO).append(" as codigo_ocorrencia, ");
        corpoBuilder.append(Columns.TOC_DESCRICAO).append(" as toc_descricao, ");
        corpoBuilder.append(Columns.USU_LOGIN).append(" as usu_login, ");
        corpoBuilder.append(Columns.USU_NOME).append(" as usu_nome, ");
        corpoBuilder.append(Columns.RSE_MATRICULA).append(" as rse_matricula, ");
        corpoBuilder.append(Columns.SER_NOME).append(" as ser_nome, ");
        corpoBuilder.append(Columns.ECH_DESCRICAO).append(" as endereco, ");
        corpoBuilder.append(Columns.SRS_DESCRICAO).append(" as srs_descricao, ");
        corpoBuilder.append(Columns.OPE_DATA).append(" as data_ocorrencia, ");
        corpoBuilder.append(Columns.OPE_IP_ACESSO).append(" as ip_acesso, ");
        corpoBuilder.append(Columns.OPE_OBS).append(" as observacao, ");
        corpoBuilder.append(Columns.SER_CPF).append(" as ser_cpf, ");
        corpoBuilder.append(Columns.SER_COMPL).append(" as ser_compl ");

        corpoBuilder.append("from ").append(Columns.TB_OCORRENCIA_PERMISSIONARIO).append(" ");
        corpoBuilder.append("inner join ").append(Columns.TB_TIPO_OCORRENCIA).append(" on (").append(Columns.TOC_CODIGO).append(" = ").append(Columns.OPE_TOC_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_USUARIO).append(" on (").append(Columns.USU_CODIGO).append(" = ").append(Columns.OPE_USU_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_PERMISSIONARIO).append(" on (").append(Columns.PRM_CODIGO).append(" = ").append(Columns.OPE_PRM_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ENDERECO_CONJUNTO_HABITACIONAL).append(" on (").append(Columns.ECH_CODIGO).append(" = ").append(Columns.PRM_ECH_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_REGISTRO_SERVIDOR).append(" on (").append(Columns.RSE_CODIGO).append(" = ").append(Columns.PRM_RSE_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVIDOR).append(" on (").append(Columns.SER_CODIGO).append(" = ").append(Columns.RSE_SER_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_STATUS_REGISTRO_SERVIDOR).append(" on (").append(Columns.SRS_CODIGO).append(" = ").append(Columns.RSE_SRS_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_USUARIO_CSA).append(" on (").append(Columns.UCA_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");

        corpoBuilder.append(" where ").append(Columns.OPE_DATA).append(" between :dataIni and :dataFim");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and ").append(Columns.UCA_CSA_CODIGO).append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(echCodigo)) {
            corpoBuilder.append(" and ").append(Columns.ECH_CODIGO).append(criaClausulaNomeada("echCodigo", echCodigo));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada(Columns.RSE_MATRICULA, "rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" AND ").append(Columns.SER_CPF).append(criaClausulaNomeada("serCpf", serCpf));
        }

        corpoBuilder.append(" union all ");

        // Recupera as ocorrências do registro servidor ligado ao permissionário
        corpoBuilder.append("select ").append(Columns.ORS_CODIGO).append(" as codigo_ocorrencia, ");
        corpoBuilder.append(Columns.TOC_DESCRICAO).append(" as toc_descricao, ");
        corpoBuilder.append(Columns.USU_LOGIN).append(" as usu_login, ");
        corpoBuilder.append(Columns.USU_NOME).append(" as usu_nome, ");
        corpoBuilder.append(Columns.RSE_MATRICULA).append(" as rse_matricula, ");
        corpoBuilder.append(Columns.SER_NOME).append(" as ser_nome, ");
        corpoBuilder.append(Columns.ECH_DESCRICAO).append(" as endereco, ");
        corpoBuilder.append(Columns.SRS_DESCRICAO).append(" as srs_descricao, ");
        corpoBuilder.append(Columns.ORS_DATA).append(" as data_ocorrencia, ");
        corpoBuilder.append(Columns.ORS_IP_ACESSO).append(" as ip_acesso, ");
        corpoBuilder.append(Columns.ORS_OBS).append(" as observacao, ");
        corpoBuilder.append(Columns.SER_CPF).append(" as ser_cpf, ");
        corpoBuilder.append(Columns.SER_COMPL).append(" as ser_compl ");

        corpoBuilder.append("from ").append(Columns.TB_OCORRENCIA_REGISTRO_SERVIDOR).append(" ");
        corpoBuilder.append("inner join ").append(Columns.TB_REGISTRO_SERVIDOR).append(" on (").append(Columns.RSE_CODIGO).append(" = ").append(Columns.ORS_RSE_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVIDOR).append(" on (").append(Columns.SER_CODIGO).append(" = ").append(Columns.RSE_SER_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_STATUS_REGISTRO_SERVIDOR).append(" on (").append(Columns.SRS_CODIGO).append(" = ").append(Columns.RSE_SRS_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_TIPO_OCORRENCIA).append(" on (").append(Columns.TOC_CODIGO).append(" = ").append(Columns.ORS_TOC_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_USUARIO).append(" on (").append(Columns.USU_CODIGO).append(" = ").append(Columns.ORS_USU_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_PERMISSIONARIO).append(" on (").append(Columns.PRM_RSE_CODIGO).append(" = ").append(Columns.ORS_RSE_CODIGO).append(" ");
        corpoBuilder.append("and ").append(Columns.ORS_TOC_CODIGO).append(" in ('").append(TextHelper.join(tocCodigos, "','")).append("')) ");
        corpoBuilder.append("inner join ").append(Columns.TB_ENDERECO_CONJUNTO_HABITACIONAL).append(" on (").append(Columns.ECH_CODIGO).append(" = ").append(Columns.PRM_ECH_CODIGO).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_USUARIO_CSA).append(" on (").append(Columns.UCA_USU_CODIGO).append(" = ").append(Columns.USU_CODIGO).append(") ");

        corpoBuilder.append(" where ").append(Columns.ORS_DATA).append(" between :dataIni and :dataFim");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and ").append(Columns.UCA_CSA_CODIGO).append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(echCodigo)) {
            corpoBuilder.append(" and ").append(Columns.ECH_CODIGO).append(criaClausulaNomeada("echCodigo", echCodigo));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada(Columns.RSE_MATRICULA, "rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" AND ").append(Columns.SER_CPF).append(criaClausulaNomeada("serCpf", serCpf));
        }

        corpoBuilder.append(" order by data_ocorrencia desc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(echCodigo)) {
            defineValorClausulaNomeada("echCodigo", echCodigo, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        return query;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        echCodigo = (String) criterio.getAttribute("ENDERECO");
        rseMatricula = (String) criterio.getAttribute("MATRICULA");
        serCpf = (String) criterio.getAttribute("CPF");
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "codigo_ocorrencia",
                Columns.TOC_DESCRICAO,
                Columns.USU_LOGIN,
                Columns.USU_NOME,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.ECH_DESCRICAO,
                Columns.SRS_DESCRICAO,
                "data_ocorrencia",
                "ip_acesso",
                "observacao",
                Columns.SER_CPF,
                Columns.SER_COMPL
        };
    }
}

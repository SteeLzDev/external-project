package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioDescontosQuery</p>
 * <p>Description: Consulta de relatório de descontos</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioDescontosQuery extends RelatorioConsignacoesQuery {
    @Override
    public void setCriterios(TransferObject criterio) {
        super.setCriterios(criterio);
        super.relatorioDescontos = true;
        super.echCodigo = (String) criterio.getAttribute("ECH_CODIGO");
        super.plaCodigo = (String) criterio.getAttribute("PLA_CODIGO");
        super.cnvCodVerba = (String) criterio.getAttribute("CNV_COD_VERBA");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        return super.preparar(session);
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CONSIGNATARIA",
                "CORRESPONDENTE",
                "SERVIDOR",
                "ORGAO",
                "CODIGO_SERVICO",
                "SERVICO",
                "PRAZO",
                "PAGAS",
                "ADE_NUM",
                "OCORRENCIA",
                "VLR_ANT",
                "VLR_NOVO",
                Columns.ADE_DATA,
                Columns.ADE_VLR,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_TAXA_JUROS,
                Columns.OCA_DATA,
                Columns.SER_CPF,
                Columns.SRS_DESCRICAO,
                Columns.USU_LOGIN,
                Columns.SAD_DESCRICAO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.COR_IDENTIFICADOR,
                Columns.COR_NOME,
                Columns.RSE_MATRICULA,
                Columns.RSE_TIPO,
                Columns.SER_NOME,
                Columns.CFT_VLR,
                Columns.CDE_VLR_LIBERADO,
                Columns.CDE_VLR_LIBERADO_CALC,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.SVC_DESCRICAO
        };
    }
}
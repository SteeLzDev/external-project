package com.zetra.econsig.persistence.query.consignacao;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusAnexoConsignacaoEnum;

/**
 * <p>Title: ListaConsignacaoUsuCsaCorSemAnexosMinQuery</p>
 * <p>Description: Listagem de consignações realizadas por usuário CSA/COR sem o número mínimo de anexos definidos
 *                 pelo parâmetro de serviço 284</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26247 $
 * $Date: 2019-02-14 09:44:20 -0200 (qui, 14 fev 2019) $
 */
public class ListaConsignacaoUsuCsaCorSemAnexosMinQuery extends HQuery {

    public String csaCodigo;
    public Date dataIniVerificacao;
    public AcessoSistema responsavel;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "select ade.adeCodigo, ade.adeNumero, ade.adeIdentificador, svc.svcDescricao, csa.csaCodigo, csa.csaAtivo, csa.ncaCodigo, psvc.pseVlrRef, count(aad.aadNome) ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from AutDesconto ade ");
        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join svc.paramSvcConsignanteSet psvc ");
        corpoBuilder.append(" left outer join ade.anexoAutorizacaoDescontoSet aad with aad.aadAtivo = ").append(StatusAnexoConsignacaoEnum.ATIVO.getCodigo());

        corpoBuilder.append(" where ((ade.usuario.usuCodigo in (select ucsa.usuCodigo FROM UsuarioCsa ucsa)) or (ade.usuario.usuCodigo in (select ucor.usuCodigo FROM UsuarioCor ucor))) ");
        corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "' , '")).append("') ");
        corpoBuilder.append(" and psvc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR).append("' ");

        corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal");
        corpoBuilder.append(" WHERE cal.calDiaUtil = '").append(CodedValues.TPC_SIM).append("'");
        corpoBuilder.append(" AND cal.calData BETWEEN ade.adeData and data_corrente()) > to_numeric(COALESCE(NULLIF(TRIM(psvc.pseVlr), ''), '0'))");

        if (dataIniVerificacao != null) {
            corpoBuilder.append(" and ade.adeData >= :dataIniVerificacao ");
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        corpoBuilder.append(" group by ade.adeCodigo, csa.csaCodigo, csa.csaAtivo, psvc.pseVlrRef ");
        corpoBuilder.append(" having count(aad.aadNome) < to_numeric(psvc.pseVlrRef)");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (dataIniVerificacao != null) {
            defineValorClausulaNomeada("dataIniVerificacao", dataIniVerificacao, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.CSA_CODIGO,
                Columns.CSA_ATIVO,
                Columns.NCA_CODIGO,
                Columns.PSE_VLR_REF,
                "NUM_ANEXOS"
        };
    }
}

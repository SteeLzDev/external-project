package com.zetra.econsig.persistence.query.beneficios.contrato;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaContratosBeneficiosQuery</p>
 * <p>Description: Listagem de Ocorrências de Contrato Beneficios</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26246 $
 * $Date: 2019-02-14 09:27:49 -0200 (qui, 14 fev 2019) $
 */
public class ListaOcorrenciaContratosBeneficiosQuery extends HQuery {

    public String cbeCodigo;
    public Boolean motivoExclusao = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        corpo =  "select "
                + " ocb.ocbCodigo as OCB_CODIGO, "
                + " toc.tocDescricao as TOC_DESCRICAO , "
                + " usr.usuNome as  USU_NOME , "
                + " ocb.tipoMotivoOperacao.tmoCodigo as  TMO_CODIGO , "
                + " ocb.ocbData as  OCB_DATA , "
                + " ocb.ocbObs as  OCB_OBS , "
                + " ocb.ocbIpAcesso as  OCB_IP_ACESSO ";

        StringBuilder corpoBuilder = null;

        corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from OcorrenciaCttBeneficio ocb ");
        corpoBuilder.append(" join ocb.tipoOcorrencia toc ");
        corpoBuilder.append(" join ocb.usuario usr ");
        corpoBuilder.append(" where ocb.contratoBeneficio.cbeCodigo = :cbeCodigo ");
        if (motivoExclusao) {
            corpoBuilder.append(" and toc.tocCodigo = '").append(CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO).append("'");
            corpoBuilder.append(" and ocb.tipoMotivoOperacao.tmoCodigo is not null ");
        }
        corpoBuilder.append(" order by ocb.ocbData desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("cbeCodigo", cbeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OCB_CODIGO,
                Columns.TOC_DESCRICAO,
                Columns.USU_NOME,
                Columns.OCB_TMO_CODIGO,
                Columns.OCB_DATA,
                Columns.OCB_OBS,
                Columns.OCB_IP_ACESSO
        };
    }

}

package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaConvenioRelIntegracaoQuery</p>
 * <p>Description: Lista de convênios para geração do relatório de integração</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioRelIntegracaoQuery extends HQuery {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaConvenioRelIntegracaoQuery.class);

    public String csaCodigo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String separarRelIntegracao = CodedValues.SEPARA_REL_INTEGRACAO_NAO;
        try {
            ParametroDelegate paramDelegate = new ParametroDelegate();
            separarRelIntegracao = paramDelegate.getParamCsa(csaCodigo, CodedValues.TPA_SEPARAR_RELATORIO_INTEGRACAO, responsavel);
            separarRelIntegracao = !TextHelper.isNull(separarRelIntegracao) ? separarRelIntegracao : CodedValues.SEPARA_REL_INTEGRACAO_NAO;
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select distinct ");

        if (separarRelIntegracao.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_ESTABELECIMENTO)) {
            corpoBuilder.append("est.estIdentificador");
        } else if (separarRelIntegracao.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_ORGAO)) {
            corpoBuilder.append("org.orgIdentificador");
        } else if (separarRelIntegracao.equals(CodedValues.SEPARA_REL_INTEGRACAO_POR_VERBA)) {
            corpoBuilder.append("cnv.cnvCodVerba");
        } else {
            corpoBuilder.append("'N/D'");
        }
        corpoBuilder.append(" AS CODIGO");

        corpoBuilder.append(" from Convenio cnv");
        corpoBuilder.append(" inner join cnv.orgao org");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join cnv.consignataria csa");

        corpoBuilder.append(" where csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo",csaCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {"CODIGO"};
    }
}

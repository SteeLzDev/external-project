package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaConvenioSvcComTpsCodigoQuery</p>
 * <p>Description: lista consignatárias que tenham convênio com serviço com parâmetro (dado pelo tpsCodigo) configurado. </p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsaConvenioSvcComTpsCodigoQuery extends HQuery {

    private boolean count = false;
    private final String tpsCodigo;
    private final AcessoSistema responsavel;

    public ListaCsaConvenioSvcComTpsCodigoQuery(String tpsCodigo, AcessoSistema responsavel) {
        this.tpsCodigo = tpsCodigo;
        this.responsavel = responsavel;
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(tpsCodigo)) {
            throw new HQueryException("mensagem.erro.campo.nulo", responsavel, "tpsCodigo");
        }

        String corpo = "";

        if (!count) {
            corpo = "select csa.csaCodigo, csa.csaIdentificador, csa.csaNome, csa.csaNomeAbrev, csa.csaAtivo, csa.csaEmail, csa.csaResponsavel, csa.csaResponsavel2, csa.csaResponsavel3";
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Consignataria csa ");

        corpoBuilder.append(" where exists (select 1 from Convenio cnv");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join svc.paramSvcConsignanteSet pse ");
        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and pse.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigo));
        corpoBuilder.append(" and pse.pseVlr is not NULL");
        corpoBuilder.append(" and cnv.consignataria.csaCodigo = csa.csaCodigo");
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("tpsCodigo", tpsCodigo, query);

        return query;
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    public String getTpsCodigo() {
        return tpsCodigo;
    }

    public AcessoSistema getResponsavel() {
        return responsavel;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_ATIVO,
                Columns.CSA_EMAIL,
                Columns.CSA_RESPONSAVEL,
                Columns.CSA_RESPONSAVEL_2,
                Columns.CSA_RESPONSAVEL_3
        };
    }
}

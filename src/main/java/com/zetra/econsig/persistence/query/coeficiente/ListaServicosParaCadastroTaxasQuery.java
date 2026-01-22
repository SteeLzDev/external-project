package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaSvcCodigoSimulacaoQuery</p>
 * <p>Description: Listagem de Serviços que tem prazos cadastrados e ativos.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicosParaCadastroTaxasQuery extends HQuery {

    public String csaCodigo;
    public boolean apenasCodigo = true;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (apenasCodigo) {
            corpoBuilder.append("SELECT svc.svcCodigo");
        } else {
            corpoBuilder.append("SELECT svc.svcCodigo, svc.svcIdentificador, svc.svcDescricao, svc.svcAtivo");
        }

        corpoBuilder.append(" FROM Servico svc ");
        corpoBuilder.append(" WHERE EXISTS (");
        corpoBuilder.append("   SELECT 1 FROM svc.prazoSet prz");
        corpoBuilder.append("   INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append("   WHERE prz.przAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append("     AND pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND pzc.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        corpoBuilder.append(")");

        if (ParamSist.paramEquals(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) &&
                ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_CAD_TAXAS_COMPARTILHADAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // Se tem compartilhamento de cadastro de taxas com bloqueio de edição nos serviços que compartilham as taxas
            // lista somente os serviços que não sejam destino de relacionamento de cadastro de taxas
            corpoBuilder.append(" AND NOT EXISTS (");
            corpoBuilder.append("   SELECT 1 FROM svc.relacionamentoServicoByDestinoSet rsv ");
            corpoBuilder.append("   WHERE rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_COMPARTILHA_CADASTRO_TAXAS).append("'");
            corpoBuilder.append("     AND rsv.svcCodigoOrigem <> rsv.svcCodigoDestino");
            corpoBuilder.append(") ");
        }

        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.SVC_ATIVO
        };
    }
}

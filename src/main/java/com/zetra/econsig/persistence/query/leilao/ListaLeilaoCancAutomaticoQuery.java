package com.zetra.econsig.persistence.query.leilao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaLeilaoCancAutomaticoQuery</p>
 * <p>Description: Listagem de leilões para Cancelamento Automático</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaLeilaoCancAutomaticoQuery extends HQuery {

	public String rseCodigo;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> sadCodigos = new ArrayList<String>();
        sadCodigos.add(CodedValues.SAD_SOLICITADO);
        sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
        sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);

        String qtdDiasConcretizarLeilao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_PARA_SER_CONCRETIZAR_LEILAO, AcessoSistema.getAcessoUsuarioSistema());

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");

        if (ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CANC_AUTOMATICO_ADE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // OBS: O operador aqui deve ser ">" apenas, pois o cálculo do between entre duas datas retorna um valor maior que
            // a subtração dos dias entre as duas datas.
            corpoBuilder.append("WHERE (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData between TO_DATE(ade.adeData) and current_date()) > ");
        } else {
            corpoBuilder.append("WHERE (TO_DAYS(current_date()) - TO_DAYS(ade.adeData)) >= ");
        }

        corpoBuilder.append("(CASE ISNUMERIC('").append(qtdDiasConcretizarLeilao).append("') WHEN 1 THEN TO_NUMERIC('").append(qtdDiasConcretizarLeilao).append("') ELSE 99999 END) ");

        if (!TextHelper.isNull(rseCodigo)) {
            // Se os parâmetros são válidos, insere as ocorrências para um servidor, mas
            // se os parâmetros são nulos ou vazios, insere ocorrência para todos os servidores
            corpoBuilder.append(" AND ade.registroServidor.rseCodigo = :rseCodigo ");
        }
        if (!TextHelper.isNull(csaCodigo)) {
            // Se o usuário é de consignatária ou correspondente, não cancela as
            // consignações da consignatária do usuário
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo <> :csaCodigo ");
        }

        // Não deixa fazer o cancelamento de contratos derivados de leilão
        corpoBuilder.append(" AND EXISTS (");
        corpoBuilder.append(" SELECT rad3.adeCodigoDestino");
        corpoBuilder.append(" FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad3");
        corpoBuilder.append(" WHERE rad3.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_LEILAO_SOLICITACAO).append("'");
        corpoBuilder.append(") ");

        // Verifica de acordo com os sadCodigos informados, quais cancelamentos automáticos devem ser executados
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));

        // Agrupa o resultado para retornar apenas os distintos
        corpoBuilder.append(" GROUP BY ade.adeCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
        		Columns.ADE_CODIGO
         };
    }
}

package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaFiltroSimulacaoQuery</p>
 * <p>Description: Diz se uma consignatária pode aparecer na lista de simulação de acordo com o
 * relacionamento TNT_CONTRATO_PREEXISTENTE_LIBERA_SIMULACAO: se houver o relacionamento
 * entre os serviços e a consignatária tiver convênio destes serviços então ela só poderá
 * aparecer na simualação se o servidor tiver um contrato em aberto do serviço relacionado.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaFiltroSimulacaoQuery extends HQuery {

    public String svcCodigo;
    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // Se o simulador agrupa os serviços de natureza EMPRESTIMO e a simulação é para um servidor
        final boolean simuladorAgrupadoPorNaturezaServico = ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        final StringBuilder corpoBuilder = new StringBuilder("SELECT cnv.consignataria.csaCodigo ");

        corpoBuilder.append(" FROM Convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" INNER JOIN svc.relacionamentoServicoByDestinoSet rsv");
        corpoBuilder.append(" INNER JOIN cnv.orgao org");
        corpoBuilder.append(" INNER JOIN org.registroServidorSet rse");
        corpoBuilder.append(" WHERE 1=1");

        if (!simuladorAgrupadoPorNaturezaServico) {
            corpoBuilder.append(" AND rsv.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        } else {
            corpoBuilder.append(" AND svc.naturezaServico.nseCodigo = '").append(CodedValues.NSE_EMPRESTIMO).append("'");
        }

        corpoBuilder.append(" AND rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_SIMULACAO).append("'");
        corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        corpoBuilder.append(" AND NOT EXISTS(");
        corpoBuilder.append("  SELECT 1 ");
        corpoBuilder.append("  FROM VerbaConvenio vco");
        corpoBuilder.append("  INNER JOIN vco.autDescontoSet ade");
        corpoBuilder.append("  WHERE cnv.cnvCodigo = vco.convenio.cnvCodigo");
        corpoBuilder.append("  AND ade.registroServidor.rseCodigo = rse.rseCodigo");
        corpoBuilder.append("  AND ade.statusAutorizacaoDesconto.sadCodigo in ('").append(CodedValues.SAD_DEFERIDA).append("', '").append(CodedValues.SAD_EMANDAMENTO).append("')");
        corpoBuilder.append(" ) ");

        // Define os valores para os parâmetros nomeados
        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!simuladorAgrupadoPorNaturezaServico) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CSA_CODIGO
        };
    }
}

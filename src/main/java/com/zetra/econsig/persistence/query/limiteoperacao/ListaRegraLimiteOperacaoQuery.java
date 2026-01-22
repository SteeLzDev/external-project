package com.zetra.econsig.persistence.query.limiteoperacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaRegraLimiteOperacaoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("rlo.rloCodigo, ");
        corpoBuilder.append("rlo.usuCodigo, ");
        corpoBuilder.append("rlo.rloFaixaEtariaIni, ");
        corpoBuilder.append("rlo.rloFaixaEtariaFim, ");
        corpoBuilder.append("rlo.rloFaixaTempoServicoIni, ");
        corpoBuilder.append("rlo.rloFaixaTempoServicoFim, ");
        corpoBuilder.append("rlo.rloFaixaSalarioIni, ");
        corpoBuilder.append("rlo.rloFaixaSalarioFim, ");
        corpoBuilder.append("rlo.rloFaixaMargemFolhaIni, ");
        corpoBuilder.append("rlo.rloFaixaMargemFolhaFim, ");
        corpoBuilder.append("rlo.rloPadraoMatricula, ");
        corpoBuilder.append("rlo.rloPadraoCategoria, ");
        corpoBuilder.append("rlo.rloPadraoVerba, ");
        corpoBuilder.append("rlo.rloPadraoVerbaRef, ");
        corpoBuilder.append("rlo.rloMensagemErro, ");
        corpoBuilder.append("rlo.rloLimiteQuantidade, ");
        corpoBuilder.append("rlo.rloLimiteDataFimAde, ");
        corpoBuilder.append("rlo.rloLimitePrazo, ");
        corpoBuilder.append("rlo.rloLimiteValorParcela, ");
        corpoBuilder.append("rlo.rloLimiteValorLiberado, ");
        corpoBuilder.append("rlo.rloLimiteCapitalDevido, ");
        corpoBuilder.append("rlo.estCodigo, ");
        corpoBuilder.append("rlo.orgCodigo, ");
        corpoBuilder.append("rlo.sboCodigo, ");
        corpoBuilder.append("rlo.uniCodigo, ");
        corpoBuilder.append("rlo.svcCodigo, ");
        corpoBuilder.append("rlo.nseCodigo, ");
        corpoBuilder.append("rlo.ncaCodigo, ");
        corpoBuilder.append("rlo.csaCodigo, ");
        corpoBuilder.append("rlo.corCodigo, ");
        corpoBuilder.append("rlo.crsCodigo, ");
        corpoBuilder.append("rlo.capCodigo, ");
        corpoBuilder.append("rlo.prsCodigo, ");
        corpoBuilder.append("rlo.posCodigo, ");
        corpoBuilder.append("rlo.srsCodigo, ");
        corpoBuilder.append("rlo.trsCodigo, ");
        corpoBuilder.append("rlo.vrsCodigo, ");
        corpoBuilder.append("rlo.funCodigo ");
        corpoBuilder.append("from RegraLimiteOperacao rlo ");
        corpoBuilder.append("where rlo.rloDataVigenciaIni < current_timestamp() ");
        corpoBuilder.append("and (rlo.rloDataVigenciaFim is null or rlo.rloDataVigenciaFim > current_timestamp())");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RLO_CODIGO,
                Columns.USU_CODIGO,
                Columns.RLO_FAIXA_ETARIA_INI,
                Columns.RLO_FAIXA_ETARIA_FIM,
                Columns.RLO_FAIXA_TEMPO_SERVICO_INI,
                Columns.RLO_FAIXA_TEMPO_SERVICO_FIM,
                Columns.RLO_FAIXA_SALARIO_INI,
                Columns.RLO_FAIXA_SALARIO_FIM,
                Columns.RLO_FAIXA_MARGEM_FOLHA_INI,
                Columns.RLO_FAIXA_MARGEM_FOLHA_FIM,
                Columns.RLO_PADRAO_MATRICULA,
                Columns.RLO_PADRAO_CATEGORIA,
                Columns.RLO_PADRAO_VERBA,
                Columns.RLO_PADRAO_VERBA_REF,
                Columns.RLO_MENSAGEM_ERRO,
                Columns.RLO_LIMITE_QUANTIDADE,
                Columns.RLO_LIMITE_DATA_FIM_ADE,
                Columns.RLO_LIMITE_PRAZO,
                Columns.RLO_LIMITE_VALOR_PARCELA,
                Columns.RLO_LIMITE_VALOR_LIBERADO,
                Columns.RLO_LIMITE_CAPITAL_DEVIDO,
                Columns.EST_CODIGO,
                Columns.ORG_CODIGO,
                Columns.SBO_CODIGO,
                Columns.UNI_CODIGO,
                Columns.SVC_CODIGO,
                Columns.NSE_CODIGO,
                Columns.NCA_CODIGO,
                Columns.CSA_CODIGO,
                Columns.COR_CODIGO,
                Columns.CRS_CODIGO,
                Columns.CAP_CODIGO,
                Columns.PRS_CODIGO,
                Columns.POS_CODIGO,
                Columns.SRS_CODIGO,
                Columns.TRS_CODIGO,
                Columns.VRS_CODIGO,
                Columns.FUN_CODIGO,
        };
    }
}

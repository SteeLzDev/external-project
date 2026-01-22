package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.List;

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
 * <p>Title: PesquisaAdeLstIndiceQuery</p>
 * <p>Description: Pesquisa os contratos de um servidor, de acordo com o relacionamento de
 * unicidade de índices, para que um contrato de um serviço não utilize um
 * indice já informado por outros serviços.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PesquisaAdeLstIndiceQuery extends HQuery {

    public String rseCodigo;
    public String cnvCodigo;
    public List<String> sadCodigos;
    public List<String> adeCodigosRenegociacao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final boolean indiceUnicoRegistroSer = ParamSist.getBoolParamSist(CodedValues.TPC_INDICE_UNICO_REGISTRO_SER_INDEPENDENTE_CONVENIO, AcessoSistema.getAcessoUsuarioSistema());
        final String corpo = "select " +
                       "ade.adeCodigo, " +
                       "ade.statusAutorizacaoDesconto.sadCodigo, " +
                       "ade.adeCodReg, " +
                       "ade.adeIndice ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));
        }

        if ((adeCodigosRenegociacao != null) && !adeCodigosRenegociacao.isEmpty()) {
            final List<String> codigos = new ArrayList<>();
            codigos.add(CodedValues.NOT_EQUAL_KEY);
            codigos.addAll(adeCodigosRenegociacao);
            corpoBuilder.append(" and ade.adeCodigo ").append(criaClausulaNomeada("adeCodigosRenegociacao", codigos));
        }

        if (!indiceUnicoRegistroSer) {
            corpoBuilder.append(" and (cnv.cnvCodigo ").append(criaClausulaNomeada("cnvCodigo", cnvCodigo));
            corpoBuilder.append(" or exists (");
            corpoBuilder.append("select 1 from RelacionamentoServico rsv ");
            corpoBuilder.append("inner join rsv.servicoBySvcCodigoOrigem svc2 ");
            corpoBuilder.append("inner join svc2.convenioSet cnv2 ");
            corpoBuilder.append("where rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_UNICIDADE_CAD_INDICE).append("'");
            corpoBuilder.append(" and cnv2.cnvCodigo ").append(criaClausulaNomeada("cnvCodigo", cnvCodigo));
            corpoBuilder.append(" and rsv.servicoBySvcCodigoDestino.svcCodigo = cnv.servico.svcCodigo");
            corpoBuilder.append(" and cnv.consignataria.csaCodigo = cnv2.consignataria.csaCodigo");
            corpoBuilder.append(" and cnv.orgao.orgCodigo = cnv2.orgao.orgCodigo");
            corpoBuilder.append("))");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(cnvCodigo) && !indiceUnicoRegistroSer) {
            defineValorClausulaNomeada("cnvCodigo", cnvCodigo, query);
        }

        if ((sadCodigos != null) && (!sadCodigos.isEmpty())) {
            defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
        }

        if ((adeCodigosRenegociacao != null) && !adeCodigosRenegociacao.isEmpty()) {
            defineValorClausulaNomeada("adeCodigosRenegociacao", adeCodigosRenegociacao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_SAD_CODIGO,
                Columns.ADE_COD_REG,
                Columns.ADE_INDICE
         };
    }
}

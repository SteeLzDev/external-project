package com.zetra.econsig.persistence.query.usuario;

import java.util.Date;

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
 * <p>Title: ListaUsuarioInativoQuery</p>
 * <p>Description: Lista os usuários que devem ser bloqueados por inatividade de acordo com o parâmetro do seu papel.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuarioInativoQuery extends HQuery {

    public Date dataLimiteBloqueio = null;
    public String usuCodigo = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        final Integer diasSemAcessoCse = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSE_SEM_ACESSO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSE_SEM_ACESSO, responsavel).toString()) : 0;
        final Integer diasSemAcessoCsa = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSA_SEM_ACESSO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSA_SEM_ACESSO, responsavel).toString()) : 0;
        final Integer diasSemAcessoSer = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_SER_SEM_ACESSO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_SER_SEM_ACESSO, responsavel).toString()) : 0;
        final String statusAtivo = CodedValues.STU_ATIVO;
        final String tpcNao = CodedValues.TPC_NAO;

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ");
        corpoBuilder.append("usuario.usuCodigo ");
        corpoBuilder.append("from Usuario usuario ");
        corpoBuilder.append("left outer join usuario.usuarioCseSet usuarioCse ");
        corpoBuilder.append("left outer join usuario.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("left outer join usuario.usuarioCorSet usuarioCor ");
        corpoBuilder.append("left outer join usuario.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("left outer join usuario.usuarioSerSet usuarioSer ");

        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(usuCodigo)) {
        	corpoBuilder.append(" and usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }

        corpoBuilder.append(" and (");
        corpoBuilder.append("usuario.usuCentralizador ").append(criaClausulaNomeada("", CodedValues.IS_NULL_KEY));
        corpoBuilder.append(" or usuario.usuCentralizador ").append(criaClausulaNomeada("tpcNao", tpcNao));
        corpoBuilder.append(")");

        corpoBuilder.append(" and abs(date_diff(current_date(), coalesce(usuario.usuDataUltAcesso, usuario.usuDataCad))) > ");
        corpoBuilder.append(" (case when 1 = 2 then 99999 ");
        if (diasSemAcessoCse > 0) {
            corpoBuilder.append(" when usuarioCse.usuCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY));
            corpoBuilder.append(" or usuarioOrg.usuCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY)).append(" then ").append(diasSemAcessoCse);
        }
        if (diasSemAcessoCsa > 0) {
            corpoBuilder.append(" when usuarioCsa.usuCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY));
            corpoBuilder.append(" or usuarioCor.usuCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY)).append(" then ").append(diasSemAcessoCsa);
        }
        if (diasSemAcessoSer > 0) {
            corpoBuilder.append(" when usuarioSer.usuCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY)).append(" then ").append(diasSemAcessoSer);
        }
        corpoBuilder.append(" else 99999 end) ");

        corpoBuilder.append(" and ( 1 = 2 ");
        if (diasSemAcessoCse > 0) {
            corpoBuilder.append(" or usuarioCse.usuCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY));
            corpoBuilder.append(" or usuarioOrg.usuCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY));
        }
        if (diasSemAcessoCsa > 0) {
            corpoBuilder.append(" or usuarioCsa.usuCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY));
            corpoBuilder.append(" or usuarioCor.usuCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY));
        }
        if (diasSemAcessoSer > 0) {
            corpoBuilder.append(" or usuarioSer.usuCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY));
        }
        corpoBuilder.append(" ) ");

        corpoBuilder.append(" and usuario.statusLogin.stuCodigo ").append(criaClausulaNomeada("statusAtivo", statusAtivo));

        corpoBuilder.append(" and not exists ( select 1 from usuario.ocorrenciaUsuarioByUsuCodigoSet ous ");
        corpoBuilder.append(" where ous.tipoOcorrencia.tocCodigo='" + CodedValues.TOC_DESBLOQUEIO_USUARIO + "'");
        corpoBuilder.append(" and ous.ousData > :dataLimiteBloqueio )");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());


        if (!TextHelper.isNull(usuCodigo)) {
        	defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }
        defineValorClausulaNomeada("tpcNao", tpcNao, query);
        defineValorClausulaNomeada("statusAtivo", statusAtivo, query);
        defineValorClausulaNomeada("dataLimiteBloqueio", dataLimiteBloqueio, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO
        };
    }
}

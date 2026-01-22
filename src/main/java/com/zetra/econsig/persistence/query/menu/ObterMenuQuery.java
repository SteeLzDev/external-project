package com.zetra.econsig.persistence.query.menu;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObterMenuQuery</p>
 * <p>Description: Obtem conjunto de menus possíveis para o papel do usuário.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObterMenuQuery extends HQuery {

    public boolean count = false;
    public String usuCodigo;
    public String papCodigo;
    public String usuCentralizador;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String entidade = null;
        if (papCodigo != null) {
            if (papCodigo.equals(CodedValues.PAP_CONSIGNANTE)) { // CONSIGNANTE
                entidade = "FuncaoPerfilCse";
            } else if (papCodigo.equals(CodedValues.PAP_CONSIGNATARIA)) { // CONSIGNATARIA
                entidade = "FuncaoPerfilCsa";
            } else if (papCodigo.equals(CodedValues.PAP_ORGAO)) { // ORGAO
                entidade = "FuncaoPerfilOrg";
            } else if (papCodigo.equals(CodedValues.PAP_CORRESPONDENTE)) { // CORRESPONDENTE
                entidade = "FuncaoPerfilCor";
            } else if (papCodigo.equals(CodedValues.PAP_SUPORTE)) { // SUPORTE
                entidade = "FuncaoPerfilSup";
            } else if (!papCodigo.equals(CodedValues.PAP_SERVIDOR)) { // SERVIDOR
                throw new HQueryException("mensagem.menu.papel.invalido", (AcessoSistema) null);
            }
        } else {
            throw new HQueryException("mensagem.menu.papel.indefinido", (AcessoSistema) null);
        }

        String corpo = "";

        if (!count) {
            corpo = " select distinct mnu.mnuCodigo, mnu.mnuSequencia, mnu.mnuDescricao, mnu.mnuImagem, " +
                    " itm.itmCodigo, itm.itemMenu.itmCodigo, itm.itmSequencia, itm.itmDescricao, itm.itmSeparador, itm.itmImagem, tex.texChave, " +
                    " case when acr.acrParametro is null then acr.acrRecurso " +
                    "      else concatenar(acr.acrRecurso, concatenar('?', concatenar(acr.acrParametro, concatenar('=', acr.acrOperacao)))) " +
                    "      end as acrRecurso, acr.acrMetodoHttp, " +
                    " fun.funCodigo, fun.grupoFuncao.grfCodigo, " +
                    " itf.imfSequencia, itf.imfData, ";
            if (papCodigo.equals(CodedValues.PAP_SERVIDOR)) {
                corpo += " case when ( " +
                        "   exists (select 1 from Usuario usu " +
                        "              inner join usu.perfilUsuarioSet upe " +
                        "              inner join upe.perfil per " +
                        "              inner join per.funcaoSet fun " +
                        "              where fun.funCodigo = acr.funcao.funCodigo " +
                        "              and usu.usuCodigo = :usuCodigo " +
                        "             ) " +
                        "  OR acr.funcao.funCodigo is null " +
                        " ) then 1 else 0 end as permiteFuncao ";

            } else {
                corpo += " case when ( " +
                        "  exists (select 1 from " + entidade + " as fup " +
                        "           where fup.funcao.funCodigo = acr.funcao.funCodigo " +
                        "           and fup.usuCodigo = :usuCodigo " +
                        "          ) " +
                        "  OR exists (select 1 from Usuario usu " +
                        "              inner join usu.perfilUsuarioSet upe " +
                        "              inner join upe.perfil per " +
                        "              inner join per.funcaoSet fun " +
                        "              where fun.funCodigo = acr.funcao.funCodigo " +
                        "              and usu.usuCodigo = :usuCodigo " +
                        "             ) " +
                        "  OR acr.funcao.funCodigo is null " +
                        " ) then 1 else 0 end as permiteFuncao ";
            }
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from AcessoRecurso acr ");
        corpoBuilder.append(" left outer join acr.funcao fun ");
        corpoBuilder.append(" right outer join acr.itemMenu itm ");
        corpoBuilder.append(" right outer join itm.menu mnu ");
        corpoBuilder.append(" left outer join itm.textoSistema tex ");
        corpoBuilder.append(" left outer join itm.itemMenuFavoritoSet itf WITH (itf.usuario.usuCodigo = :usuCodigo)");
        corpoBuilder.append(" where mnu.mnuAtivo = 1 ");
        corpoBuilder.append(" and itm.itmAtivo = 1 ");
        if ("S".equals(usuCentralizador)) {
        	corpoBuilder.append(" and itm.itmCentralizador = 'S' ");
        }
        corpoBuilder.append(" and coalesce(acr.papel.papCodigo, :papCodigo)" ).append(criaClausulaNomeada("papCodigo", papCodigo));

        if (!count) {
            corpoBuilder.append(" ORDER BY mnu.mnuSequencia, itm.itmSequencia");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        defineValorClausulaNomeada("papCodigo", papCodigo, query);

        return query;
    }


    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.MNU_CODIGO,
                Columns.MNU_SEQUENCIA,
                Columns.MNU_DESCRICAO,
                Columns.MNU_IMAGEM,
                Columns.ITM_CODIGO,
                Columns.ITM_CODIGO_PAI,
                Columns.ITM_SEQUENCIA,
                Columns.ITM_DESCRICAO,
                Columns.ITM_SEPARADOR,
                Columns.ITM_IMAGEM,
                Columns.ITM_TEX_CHAVE,
                Columns.ACR_RECURSO,
                Columns.ACR_METODO_HTTP,
                Columns.FUN_CODIGO,
                Columns.FUN_GRF_CODIGO,
                Columns.IMF_SEQUENCIA,
                Columns.IMF_DATA,
                "permiteFuncao",
        };
    }
}

package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAcessoRecursoQuery</p>
 * <p>Description: Listagem de configuração de segurança</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAcessoRecursoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("select distinct trim(acessoRecurso.acrCodigo), ");
        corpo.append("    trim(papel.papCodigo), ");
        corpo.append("    papel.papDescricao, ");
        corpo.append("    trim(funcao.funCodigo), ");
        corpo.append("    funcao.funDescricao, ");
        corpo.append("    funcao.funPermiteBloqueio, ");
        corpo.append("    funcao.funExigeTmo, ");
        corpo.append("    funcao.funRestritaNca, ");
        corpo.append("    funcao.funExigeSegundaSenhaCse, ");
        corpo.append("    funcao.funExigeSegundaSenhaSup, ");
        corpo.append("    funcao.funExigeSegundaSenhaOrg, ");
        corpo.append("    funcao.funExigeSegundaSenhaCsa, ");
        corpo.append("    funcao.funExigeSegundaSenhaCor, ");
        corpo.append("    funcao.funExigeSegundaSenhaSer, ");

        corpo.append("    acessoRecurso.acrRecurso, ");
        corpo.append("    acessoRecurso.acrParametro, ");
        corpo.append("    acessoRecurso.acrOperacao, ");
        corpo.append("    acessoRecurso.acrSessao, ");
        corpo.append("    acessoRecurso.acrBloqueio, ");
        corpo.append("    acessoRecurso.acrAtivo, ");
        corpo.append("    acessoRecurso.acrFimFluxo, ");
        corpo.append("    acessoRecurso.acrMetodoHttp, ");
        corpo.append("    acessoRecurso.itemMenu.itmCodigo, ");
        corpo.append("    case when exists (select 1 from Ajuda ajuda where ajuda.acrCodigo = acessoRecurso.acrCodigo) then 1 else 0 end as possui_ajuda ");

        corpo.append("from AcessoRecurso acessoRecurso ");
        corpo.append("left outer join acessoRecurso.funcao funcao ");
        corpo.append("left outer join acessoRecurso.papel papel ");
        corpo.append("where 1 = 1");

        return instanciarQuery(session, corpo.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {Columns.ACR_CODIGO,
                Columns.ACR_PAP_CODIGO,
                Columns.PAP_DESCRICAO,
                Columns.ACR_FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.FUN_PERMITE_BLOQUEIO,
                Columns.FUN_EXIGE_TMO,
                Columns.FUN_RESTRITA_NCA,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_COR,
                Columns.FUN_EXIGE_SEGUNDA_SENHA_SER,
                Columns.ACR_RECURSO,
                Columns.ACR_PARAMETRO,
                Columns.ACR_OPERACAO,
                Columns.ACR_SESSAO,
                Columns.ACR_BLOQUEIO,
                Columns.ACR_ATIVO,
                Columns.ACR_FIM_FLUXO,
                Columns.ACR_METODO_HTTP,
                Columns.ACR_ITM_CODIGO,
                "possui_ajuda"};
    }
}
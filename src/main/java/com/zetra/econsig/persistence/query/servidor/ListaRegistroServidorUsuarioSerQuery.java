package com.zetra.econsig.persistence.query.servidor;

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
 * <p>Title: ListaRegistroServidorUsuarioSerQuery</p>
 * <p>Description: Lista registros servidor do usuário servidor dado pelo login.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegistroServidorUsuarioSerQuery extends HQuery {
    public String usuLogin;
    public boolean recuperaRseExcluido = true;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select rse.rseMatricula, rse.rseCodigo, ser.serCpf, "
                        + "usuarioCsa.csaCodigo, "
                        + "usuarioCse.cseCodigo, "
                        + "usuarioCor.corCodigo, "
                        + "usuarioOrg.orgCodigo, "
                        + "usuarioSer.serCodigo, "
                        + "usuarioSup.cseCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from RegistroServidor rse ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join ser.usuarioSerSet usuSer ");
        corpoBuilder.append(" inner join usuSer.usuario usu ");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" WHERE 1=1 ");

        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula) ");
        } else  {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula) ");
        }

        corpoBuilder.append(" and usu.usuLogin").append(criaClausulaNomeada("usuLogin", usuLogin));

        boolean ignoraServExcluidos = ParamSist.paramEquals(CodedValues.TPC_IGNORA_SERVIDORES_EXCLUIDOS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        if (ignoraServExcluidos || !recuperaRseExcluido) {
            corpoBuilder.append(" AND srs.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("usuLogin", usuLogin, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_MATRICULA,
                Columns.RSE_CODIGO,
                Columns.SER_CPF,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO
        };
    }

}

package com.zetra.econsig.persistence.query.consignataria;

import java.text.ParseException;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaAExpirarQuery</p>
 * <p>Description: Lista consignatárias com data de expiração compreendido no intervalo do dia dado</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaAExpirarQuery extends HQuery {

    public Date dataExpiracao;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        corpo = "select consignataria.csaCodigo,"
              + "consignataria.csaIdentificador,"
              + "consignataria.csaNome,"
              + "consignataria.csaNomeAbrev,"
              + "consignataria.csaAtivo, "
              + "consignataria.csaCnpj,"
              + "consignataria.csaTel,"
              + "consignataria.csaEmail, "
              + "consignataria.csaEmailExpiracao "
              ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Consignataria consignataria ");
        corpoBuilder.append(" where consignataria.csaDataExpiracao between :dataIniExpiracao and :dataFimExpiracao ");
        corpoBuilder.append(" or consignataria.csaDataExpiracaoCadastral between :dataIniExpiracao and :dataFimExpiracao ");

        if (dataExpiracao == null) {
            dataExpiracao = DateHelper.getSystemDatetime();
        }
        Date dataIniExpiracao;
        Date dataFimExpiracao;
        try {
            dataIniExpiracao = DateHelper.parse(DateHelper.format(dataExpiracao, "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
            dataFimExpiracao = DateHelper.parse(DateHelper.format(dataExpiracao, "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException ex) {
            throw new HQueryException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("dataIniExpiracao", dataIniExpiracao, query);
        defineValorClausulaNomeada("dataFimExpiracao", dataFimExpiracao, query);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_ATIVO,
                Columns.CSA_CNPJ,
                Columns.CSA_TEL,
                Columns.CSA_EMAIL,
                Columns.CSA_EMAIL_EXPIRACAO
        };
    }
}

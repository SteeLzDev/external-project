package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoBeneficiarioEnum;

/**
 * <p>Title: ListaServidorNaoBeneficiarioQuery</p>
 * <p>Description: Retornar servidores que ainda não estão na tabela de beneficiarios.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidorNaoBeneficiarioQuery extends HQuery {

    public boolean count = false;
    public List<TransferObject> excecoes = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

    	StringBuilder corpoBuilder = new StringBuilder(" select ");

    	corpoBuilder.append("ser.serCodigo, ");
    	corpoBuilder.append("ser.serNome, ");
    	corpoBuilder.append("ser.serCpf, ");
    	corpoBuilder.append("ser.serNroIdt, ");
    	corpoBuilder.append("ser.serSexo, ");
    	corpoBuilder.append("ser.serTel, ");
    	corpoBuilder.append("ser.serDataNasc, ");
    	corpoBuilder.append("ser.serEstCivil, ");
    	corpoBuilder.append("ser.serCelular, ");
        corpoBuilder.append("ser.serNomeMae, ");
        corpoBuilder.append("rse.rseCodigo ");
        corpoBuilder.append("from RegistroServidor rse ");
        corpoBuilder.append("inner join rse.servidor ser ");
        corpoBuilder.append("where rse.statusRegistroServidor.srsCodigo not in ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
        corpoBuilder.append("and (ser.serCodigo not in (select bfc.servidor.serCodigo from Beneficiario bfc where bfc.tipoBeneficiario.tibCodigo = '").append(TipoBeneficiarioEnum.TITULAR.tibCodigo).append("') ");
        corpoBuilder.append("or rse.rseCodigo not in (select bfc.rseCodigo from Beneficiario bfc where bfc.tipoBeneficiario.tibCodigo = '").append(TipoBeneficiarioEnum.TITULAR.tibCodigo).append("')) ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
       return new String[] {
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SER_NRO_IDT,
                Columns.SER_SEXO,
                Columns.SER_TEL,
                Columns.SER_DATA_NASC,
                Columns.SER_EST_CIVIL,
                Columns.SER_CELULAR,
                Columns.SER_NOME_MAE,
                Columns.RSE_CODIGO
        };
    }
}

package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CanalEnum;

/**
 * Home object for domain model class ControleProcessamentoLote.
 * @see com.zetra.econsig.persistence.entity.ControleProcessamentoLote
 * @author Hibernate Tools
 */
public class ControleProcessamentoLoteHome extends AbstractEntityHome{

    public static ControleProcessamentoLote findProcessamentoByArquivoCentralizador(String arquivoCentralizador) {
        List<ControleProcessamentoLote> result = findProcessamento(arquivoCentralizador, null, null);
        if (result == null || result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public static ControleProcessamentoLote findProcessamentoByArquivoeConsig(String arquivoeConsig) {
        List<ControleProcessamentoLote> result = findProcessamento(null, arquivoeConsig, null);
        if (result == null || result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    private static List<ControleProcessamentoLote> findProcessamento(String arquivoCentralizador, String arquivoeConsig, Short status) {

        Map<String, Object> parameters = new HashMap<>();
        String query = "FROM ControleProcessamentoLote cpl WHERE 1=1 ";

        if (arquivoCentralizador != null) {
            query += " and cpl.cplArquivoCentralizador = :cplArquivoCentralizador";
            parameters.put("cplArquivoCentralizador", arquivoCentralizador);
        }

        if (arquivoeConsig != null) {
            query += " and cpl.cplArquivoEconsig = :cplArquivoEconsig";
            parameters.put("cplArquivoEconsig", arquivoeConsig);
        }

        if (status != null) {
            query += " and cpl.cplStatus = :cplStatus";
            parameters.put("cplStatus", status);
        }

        try {
            return findByQuery(query, parameters);
        } catch (FindException e) {
            return null;
        }
    }

    public static ControleProcessamentoLote create(String arquivoCentralizador, String arquivoeConsig, Short status, CanalEnum canal, String usuCodigo) throws CreateException {
        ControleProcessamentoLote controleProcessamentoLote = new ControleProcessamentoLote();
        controleProcessamentoLote.setCplArquivoEconsig(arquivoeConsig);
        controleProcessamentoLote.setCplArquivoCentralizador(arquivoCentralizador);
        controleProcessamentoLote.setCplStatus(status);
        controleProcessamentoLote.setCplCanal(canal.getCodigo());
        controleProcessamentoLote.setCplData(DateHelper.getSystemDatetime());
        controleProcessamentoLote.setUsuCodigo(usuCodigo);

        return create(controleProcessamentoLote);
    }
}

package com.zetra.econsig.folha.margem.impl;

import java.util.List;

import com.zetra.econsig.exception.ImportaMargemException;
import com.zetra.econsig.folha.margem.ImportaMargemBase;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImportaMargemDivinopolis</p>
 * <p>Description: Implementação específica para o sistema de Divinópolis para
 * rotinas relacionadas a carga de margem e geração de transferidos.</p>
 *
 * <p>Classe específica de importação de margem para o sistema de DIVIPREV:
 * não permite a transferência de contratos
 * quando o campo RSE_TIPO do registro servidor de destino for igual a "afastado"
 * e o estabelecimento do registro servidor de destino for 002 (DIVIPREV). </p>
 *
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaMargemDivinopolis extends ImportaMargemBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaMargemDivinopolis.class);

    /**
     * Processo executado imediatamente antes da geração do arquivo de transferidos,
     * logo após já ter selecionado os registros de transferência
     * @param query
     * @return
     * @throws ImportaMargemException
     */
    @Override
    public String preGeracaoArqTransferidos(String query, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        if (query != null) {
            StringBuilder novaQuery = new StringBuilder();
            novaQuery.append("SELECT arq.ACAO, arq.EST_IDENTIFICADOR, arq.ORG_IDENTIFICADOR, arq.RSE_MATRICULA, arq.SER_NOME, ");
            novaQuery.append("arq.NOVO_ESTABELECIMENTO, arq.NOVO_ORGAO, arq.NOVA_MATRICULA, arq.DATA_MUDANCA, ade.ADE_NUMERO ");
            novaQuery.append("FROM tb_arq_transferidos arq ");
            novaQuery.append("INNER JOIN tb_registro_servidor rseAtv on (arq.RSE_CODIGO_ATIVO = rseAtv.RSE_CODIGO) ");
            novaQuery.append("INNER JOIN tb_registro_servidor rseExc on (arq.RSE_CODIGO_EXCLUIDO = rseExc.RSE_CODIGO) ");
            novaQuery.append("INNER JOIN tb_aut_desconto ade on (ade.RSE_CODIGO = rseExc.RSE_CODIGO) ");
            novaQuery.append("WHERE 1=1 ");
            novaQuery.append("AND (rseAtv.RSE_TIPO != 'AFASTADO' OR arq.NOVO_ESTABELECIMENTO != '002')");
            novaQuery.append("ORDER BY ADE_NUMERO");

            LOG.debug(novaQuery.toString());
            return novaQuery.toString();
        }
        return query;
    }
}

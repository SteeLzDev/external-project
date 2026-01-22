package com.zetra.econsig.helper.folha;

import java.io.File;
import java.util.Date;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ImportaContracheques</p>
 * <p>Description: Classe para execução da importação de contracheque</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContracheques implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContracheques.class);
    private static final String NOME_CLASSE = ImportaContracheques.class.getName();

    @Override
    public int executar(String[] args) {
        final String textoAjuda = "USE: java " + NOME_CLASSE + " I [ARQUIVO] [PERIODO](MM/YYYY) [ORGAO](Opcional) [ESTABELECIMENTO](Opcional) [SOBREPOR](true|false)(Opcional) [ATIVO](true|false)(Opcional)";
        try {
            if (args.length < 3) {
                LOG.error(textoAjuda);
                return -1;
            } else if (args[0].equals("I")) {
                String nomeArquivo = args[1];
                Date ccqPeriodo = DateHelper.parse(args[2], "MM/yyyy");
                String orgCodigo = args.length >= 4 ? args[3] : null;
                String estCodigo = args.length >= 5 ? args[4] : null;
                Boolean sobrepoe = args.length >= 6 ? args[5].equalsIgnoreCase("true") : true;
                Boolean ativo = args.length >= 7 ? args[6].equalsIgnoreCase("true") : false;

                String absolutePath = ParamSist.getDiretorioRaizArquivos();

                String arquivo = absolutePath
                               + File.separatorChar + "contracheque"
                               + File.separatorChar + "cse"
                               + (!TextHelper.isNull(orgCodigo) ? File.separatorChar + orgCodigo : "")
                               + File.separatorChar + nomeArquivo;

                String tipoEntidade = !TextHelper.isNull(estCodigo) ? "EST" : !TextHelper.isNull(orgCodigo) ? "ORG" : "CSE";
                String codigoEntidade = !TextHelper.isNull(estCodigo) ? estCodigo : !TextHelper.isNull(orgCodigo) ? orgCodigo : CodedValues.CSE_CODIGO_SISTEMA;

                LOG.info("INÍCIO - IMPORTA ARQUIVO CONTRACHEQUES: " + DateHelper.getSystemDatetime());

                ServidorDelegate serDelegate = new ServidorDelegate();
                serDelegate.importaArquivoContracheques(arquivo, ccqPeriodo, tipoEntidade, codigoEntidade, sobrepoe, ativo, AcessoSistema.getAcessoUsuarioSistema());

                LOG.info("FIM - IMPORTA ARQUIVO CONTRACHEQUES: " + DateHelper.getSystemDatetime());
                return 0;
            } else {
                LOG.error(textoAjuda);
                return -1;
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }
}

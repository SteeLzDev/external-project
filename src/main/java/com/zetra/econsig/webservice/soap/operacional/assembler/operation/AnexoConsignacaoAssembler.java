package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.DESCRICAO_TIPO_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.NOME_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v7.AnexoConsignacao;
import com.zetra.econsig.webservice.soap.operacional.v7.ObjectFactory;

/**
 * <p>Title: AnexoConsignacaoAssembler</p>
 * <p>Description: Assembler para AnexoConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class AnexoConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AnexoConsignacaoAssembler.class);


    private AnexoConsignacaoAssembler() {
        //
    }

    public static AnexoConsignacao toAnexoConsignacaoV7(Map<CamposAPI, Object> paramResposta, AcessoSistema responsavel) {
        final ObjectFactory factory = new ObjectFactory();
        final AnexoConsignacao anexo = new AnexoConsignacao();

        anexo.setNomeArquivo((String) paramResposta.get(NOME_ARQUIVO));
        anexo.setTipoArquivo((String) paramResposta.get(TIPO_ARQUIVO));
        anexo.setDescricaoTipoArquivo((String) paramResposta.get(DESCRICAO_TIPO_ARQUIVO));

        if (paramResposta.get(ARQUIVO) != null) {
            final File arquivoAnexo = (File) paramResposta.get(ARQUIVO);
            try {
                anexo.setArquivo(factory.createAnexoConsignacaoArquivo(Files.readAllBytes(arquivoAnexo.toPath())));
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }

        }

        return anexo;
    }
}

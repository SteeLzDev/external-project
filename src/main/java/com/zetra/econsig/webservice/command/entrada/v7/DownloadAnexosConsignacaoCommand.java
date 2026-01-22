package com.zetra.econsig.webservice.command.entrada.v7;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ANEXOS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.APENAS_ANEXO_COMPRAS;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.NOME_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.TODOS_ANEXOS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;

/**
 * <p>Title: DownloadAnexosConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de download de anexos da consignação</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DownloadAnexosConsignacaoCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DownloadAnexosConsignacaoCommand.class);

    public DownloadAnexosConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        Long adeNumero = (Long) parametros.get(ADE_NUMERO);
        String adeIdentificador = (String) parametros.get(ADE_IDENTIFICADOR);
        if (adeNumero == null && TextHelper.isNull(adeIdentificador)) {
            throw new ZetraException("mensagem.informe.ade.numero.ou.identificador", responsavel);
        }

    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<TransferObject> consignacao = (List<TransferObject>) parametros.get(CONSIGNACAO);

        if (consignacao != null && !consignacao.isEmpty()) {
            TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);
            String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
            String dataReserva = DateHelper.format((Date) autorizacao.getAttribute(Columns.ADE_DATA), "yyyyMMdd");

            String diretorioRaiz = ParamSist.getDiretorioRaizArquivos();
            String caminhoAnexo = diretorioRaiz + File.separatorChar + "anexo" + File.separatorChar + dataReserva + File.separatorChar + adeCodigo + File.separatorChar;

            String nomeAnexo = (String) parametros.get(NOME_ARQUIVO);
            boolean apenasAnexosCompra = (parametros.get(APENAS_ANEXO_COMPRAS) != null && (boolean) parametros.get(APENAS_ANEXO_COMPRAS));
            boolean todosAnexos = (parametros.get(TODOS_ANEXOS) != null && (boolean) parametros.get(TODOS_ANEXOS));

            EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);
            List<TransferObject> resultado = new ArrayList<>();

            List<String> tarCodigos = null;
            if (apenasAnexosCompra) {
                // Quando enviado "compras" igual a true, deve retornar somente os anexos de compras, ou seja,
                // os anexos de tipo arquivo igual a:
                // "14 - Arquivo Anexo Autorização Boleto Bancário",
                // "15 - Arquivo Anexo Autorização Demonstrativo Saldo Devedor - DSD",
                // "19 - Arquivo Anexo Autorização Comprovante Pagament"
                // "22 - Arquivo Anexo Autorização Documentação Adicional Compra".
                tarCodigos = new ArrayList<>();
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_COMPROVANTE_PAGAMENTO.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DOC_ADICIONAL_COMPRA.getCodigo());
            }

            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
            cto.setAttribute(Columns.AAD_TAR_CODIGO, tarCodigos);
            cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);

            if (!TextHelper.isNull(nomeAnexo)) {
                cto.setAttribute(Columns.AAD_NOME, nomeAnexo);
            }

            List<TransferObject> anexos = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);
            if (anexos == null || anexos.isEmpty()) {
                throw new ZetraException("mensagem.anexo.erro.nenhum.registro", responsavel);
            } else {
                for (TransferObject anexo : anexos) {
                    String aadNome = anexo.getAttribute(Columns.AAD_NOME).toString();

                    String nomeArqAnexo = caminhoAnexo + aadNome;
                    File arquivoAnexo = new File(nomeArqAnexo);
                    if (!arquivoAnexo.exists() || !arquivoAnexo.canRead()) {
                        LOG.warn(String.format("Anexo de nome '%s' existe para a ADE '%s' porém não encontrado no diretório de arquivos.", aadNome, adeCodigo));
                    } else {
                        // Se a operação não informou a opção TODOS, nem COMPRAS, nem o NOME_ARQUIVO, então não retorna
                        // a referência ao arquivo, pois o resultado não deverá ter o conteúdo, somente a listagem
                        // dos arquivos existentes para a consignação informada.
                        if (todosAnexos || apenasAnexosCompra || !TextHelper.isNull(nomeAnexo)) {
                            anexo.setAttribute("ARQUIVO", arquivoAnexo);

                            // Verifica se o arquivo está compactado, e caso não esteja, gera uma versão Zip
                            // em uma pasta temporária para retorno da operação.
                            try {
                                if (!FileHelper.isZip(arquivoAnexo.getAbsolutePath())) {
                                    String diretorioTemp = diretorioRaiz + File.separatorChar + "temp" + File.separatorChar + "download";
                                    new File(diretorioTemp).mkdirs();
                                    File arquivoAnexoZip = new File(diretorioTemp + File.separatorChar + arquivoAnexo.getName() + ".zip");
                                    if (arquivoAnexoZip.exists()) {
                                        arquivoAnexoZip.delete();
                                    }
                                    FileHelper.zip(arquivoAnexo.getAbsolutePath(), arquivoAnexoZip.getAbsolutePath());
                                    anexo.setAttribute("ARQUIVO", arquivoAnexoZip);
                                }
                            } catch (IOException ex) {
                                LOG.error(ex.getMessage(), ex);
                            }
                        }

                        resultado.add(anexo);
                    }
                }
                if (resultado.isEmpty()) {
                    throw new ZetraException("mensagem.anexo.erro.nenhum.registro", responsavel);
                }

                parametros.put(ANEXOS_CONSIGNACAO, resultado);
            }
        }
    }
}

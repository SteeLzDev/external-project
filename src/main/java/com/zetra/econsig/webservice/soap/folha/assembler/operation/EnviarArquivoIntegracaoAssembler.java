package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.NOME_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.OBSERVACAO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO_ENUM;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.EnviarArquivoIntegracao;
import com.zetra.econsig.webservice.soap.folha.v1.TipoArquivo;

/**
 * <p>Title: EnviarArquivoIntegracaoAssembler</p>
 * <p>Description: Assembler para EnviarArquivoIntegracao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class EnviarArquivoIntegracaoAssembler extends BaseAssembler {

    private EnviarArquivoIntegracaoAssembler() {
    }

    @SuppressWarnings("java:S4165")
    public static Map<CamposAPI, Object> toMap(EnviarArquivoIntegracao enviarArquivoIntegracao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        String tipo = "";
        TipoArquivoEnum tipoEnum = null;
        final TipoArquivo tipoArquivo = enviarArquivoIntegracao.getTipoArquivo();

        if (tipoArquivo != null) {
            if (Boolean.TRUE.equals(tipoArquivo.getBloqueioServidor())) {
                tipo = "bloqueio_ser";
                tipoEnum = TipoArquivoEnum.ARQUIVO_BLOQUEIO_SERVIDOR;
            } else if (Boolean.TRUE.equals(tipoArquivo.getContracheque())) {
                tipo = "contracheque";
                tipoEnum = TipoArquivoEnum.ARQUIVO_CONTRACHEQUES;
            } else if (Boolean.TRUE.equals(tipoArquivo.getCritica())) {
                tipo = "critica";
                tipoEnum = TipoArquivoEnum.ARQUIVO_CRITICA;
            } else if (Boolean.TRUE.equals(tipoArquivo.getDesligado())) {
                tipo = "desligado";
                tipoEnum = TipoArquivoEnum.ARQUIVO_DESLIGADO_BLOQUEADO;
            } else if (Boolean.TRUE.equals(tipoArquivo.getDirf())) {
                tipo = "dirf";
                tipoEnum = TipoArquivoEnum.ARQUIVO_DIRF_SERVIDOR;
            } else if (Boolean.TRUE.equals(tipoArquivo.getFalecido())) {
                tipo = "falecido";
                tipoEnum = TipoArquivoEnum.ARQUIVO_FALECIDO;
            } else if (Boolean.TRUE.equals(tipoArquivo.getMargem())) {
                tipo = "margem";
                tipoEnum = TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS;
            } else if (Boolean.TRUE.equals(tipoArquivo.getMargemComplementar())) {
                tipo = "margemcomplementar";
                tipoEnum = TipoArquivoEnum.ARQUIVO_CADASTRO_MARGEM_COMPLEMENTAR;
            } else if (Boolean.TRUE.equals(tipoArquivo.getMovimento())) {
                tipo = "movimento";
                tipoEnum = null;
            } else if (Boolean.TRUE.equals(tipoArquivo.getRelatorioIntegracao())) {
                tipo = "integracao";
                tipoEnum = null;
            } else if (Boolean.TRUE.equals(tipoArquivo.getRetorno())) {
                tipo = "retorno";
                tipoEnum = TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO;
            } else if (Boolean.TRUE.equals(tipoArquivo.getRetornoAtrasado())) {
                tipo = "retornoatrasado";
                tipoEnum = TipoArquivoEnum.ARQUIVO_RETORNO_ATRASADO;
            } else if (Boolean.TRUE.equals(tipoArquivo.getTransferidos())) {
                tipo = "transferidos";
                tipoEnum = TipoArquivoEnum.ARQUIVO_TRANSFERIDOS;
            }
        }

        parametros.put(USUARIO, enviarArquivoIntegracao.getUsuario());
        parametros.put(SENHA, enviarArquivoIntegracao.getSenha());
        parametros.put(TIPO_ARQUIVO, tipo);
        parametros.put(TIPO_ARQUIVO_ENUM, tipoEnum);
        parametros.put(NOME_ARQUIVO, enviarArquivoIntegracao.getNomeArquivo());
        parametros.put(ARQUIVO, enviarArquivoIntegracao.getArquivo());
        parametros.put(OBSERVACAO, enviarArquivoIntegracao.getObservacao());
        parametros.put(CODIGO_ORGAO, enviarArquivoIntegracao.getCodigoOrgao());
        parametros.put(CODIGO_ESTABELECIMENTO, getValue(enviarArquivoIntegracao.getCodigoEstabelecimento()));

        return parametros;
    }
}
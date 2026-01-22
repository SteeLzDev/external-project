package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ListarArquivoIntegracao;
import com.zetra.econsig.webservice.soap.folha.v1.TipoArquivo;

/**
 * <p>Title: ListarArquivoIntegracaoAssembler</p>
 * <p>Description: Assembler para ListarArquivoIntegracao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ListarArquivoIntegracaoAssembler extends BaseAssembler {

    private ListarArquivoIntegracaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ListarArquivoIntegracao listarArquivoIntegracao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        String tipo = "";
        final TipoArquivo tipoArquivo = listarArquivoIntegracao.getTipoArquivo();

        if (tipoArquivo != null) {
            if (Boolean.TRUE.equals(tipoArquivo.getBloqueioServidor())) {
            	tipo = "bloqueio_ser";
            } else if (Boolean.TRUE.equals(tipoArquivo.getContracheque())) {
            	tipo = "contracheque";
            } else if (Boolean.TRUE.equals(tipoArquivo.getCritica())) {
            	tipo = "critica";
            } else if (Boolean.TRUE.equals(tipoArquivo.getDesligado())) {
            	tipo = "desligado";
            } else if (Boolean.TRUE.equals(tipoArquivo.getDirf())) {
            	tipo = "dirf";
            } else if (Boolean.TRUE.equals(tipoArquivo.getFalecido())) {
            	tipo = "falecido";
            } else if (Boolean.TRUE.equals(tipoArquivo.getMargem())) {
            	tipo = "margem";
            } else if (Boolean.TRUE.equals(tipoArquivo.getMargemComplementar())) {
            	tipo = "margemcomplementar";
            } else if (Boolean.TRUE.equals(tipoArquivo.getMovimento())) {
            	tipo = "movimento";
            } else if (Boolean.TRUE.equals(tipoArquivo.getRelatorioIntegracao())) {
            	tipo = "integracao";
            } else if (Boolean.TRUE.equals(tipoArquivo.getRetorno())) {
            	tipo = "retorno";
            } else if (Boolean.TRUE.equals(tipoArquivo.getRetornoAtrasado())) {
            	tipo = "retornoatrasado";
            } else if (Boolean.TRUE.equals(tipoArquivo.getTransferidos())) {
            	tipo = "transferidos";
            }
        }

        parametros.put(USUARIO, listarArquivoIntegracao.getUsuario());
        parametros.put(SENHA, listarArquivoIntegracao.getSenha());
        parametros.put(CODIGO_ORGAO, listarArquivoIntegracao.getCodigoOrgao());
        parametros.put(CODIGO_ESTABELECIMENTO, getValue(listarArquivoIntegracao.getCodigoEstabelecimento()));
        parametros.put(TIPO_ARQUIVO, tipo);

        return parametros;
    }
}
package com.zetra.econsig.web.controller.sistema;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(value = { "/v3/reconhecimentoFacial" })
public class ReconhecimentoFacialWebController extends AbstractWebController {

    @Autowired
    private ArquivoController arquivoController;

    @Autowired
    private ServidorController servidorController;

    public static final String FOTO_FRONTAL = "fotoFrontal";

    public static final String FOTO_PERFIL_DIREITO = "fotoPerfilDireito";

    public static final String FOTO_PERFIL_ESQUERDO = "fotoPerfilEsquerdo";

    public static final String EXIGE_RECONHECIMENTO_FACIAL_PRIMEIRO_ACESSO = "ExigeReconhecimentoFacialPrimeiroAcesso";

    public static final List<String> TAR_CODIGOS = List.of(TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_FRONTAL_SERVIDOR.getCodigo(),
                                                           TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_PERFIL_DIREITO_SERVIDOR.getCodigo(),
                                                           TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_PERFIL_ESQUERDO_SERVIDOR.getCodigo());

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReconhecimentoFacialWebController.class);

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!TextHelper.isNull(session.getAttribute(CodedValues.MSG_ERRO)) || !TextHelper.isNull(session.getAttribute(CodedValues.MSG_ALERT)) || !TextHelper.isNull(session.getAttribute(CodedValues.MSG_INFO))) {
            model.addAttribute("possuiMensagemSessao", "true");
        }

        try {
            removerFotosCapturadas(responsavel);
        } catch (final ArquivoControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.erro.validacao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/reconhecimentoFacialPrimeiroAcesso/reconhecimentoFacialPrimeiroAcesso", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=registrar" })
    @ResponseBody
    public ResponseEntity<String> reconhecimentoFacialRegistro(@RequestBody(required = true) Map<String, Object> corpo, HttpServletRequest request, HttpSession session) throws ViewHelperException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final RestTemplate restTemplate = new RestTemplate();
        final String base64Data = String.valueOf(corpo.get("fotoFace"));
        final String cpf = String.valueOf(corpo.get("cpf"));
        final byte[] fileData = Base64.getDecoder().decode(base64Data);

        String baseurl = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_SERVICO_FACES_WEB, responsavel);
        final String apiKey = (String) ParamSist.getInstance().getParam(CodedValues.TPC_API_KEY_FACES_WEB, responsavel);

        if (TextHelper.isNull(baseurl) || TextHelper.isNull(apiKey)) {
            // Erro parâmetros não configurados
            LOG.error("Parâmetros de sistema " + CodedValues.TPC_URL_SERVICO_FACES_WEB + " e " + CodedValues.TPC_API_KEY_FACES_WEB + " devem estar configurados para integração com o FacesWeb.");
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel);
        }

        baseurl = baseurl.endsWith("/") ? baseurl.substring(0, baseurl.length() - 1) : baseurl;

        final MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("files", new ByteArrayResource(fileData) {
            @Override
            public String getFilename() {
                return "face_" + cpf.replace(".", "") + ".jpg";
            }
        });
        formData.add("id", cpf);
        formData.add("validateOnlyPhoto", "y");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        final ResponseEntity<String> response = restTemplate.exchange(baseurl + "/api/register", HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            LOG.error("O retorno do envio da foto ao FacesWeb para validação não foi realizado com sucesso, o Status foi: " + response.getStatusCode());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(params = { "acao=verificar" })
    @ResponseBody
    public ResponseEntity<String> reconhecimentoFacialVerificacao(HttpServletRequest request) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String baseurl = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_SERVICO_FACES_WEB, responsavel);
        baseurl = baseurl.endsWith("/") ? baseurl.substring(0, baseurl.length() - 1) : baseurl;
        final String apiKey = (String) ParamSist.getInstance().getParam(CodedValues.TPC_API_KEY_FACES_WEB, responsavel);

        final RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("ApiKey", apiKey);

        final HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(baseurl + "/api/verify?id=" + responsavel.getSerCpf(), HttpMethod.GET, httpEntity, String.class);
    }

    @RequestMapping(params = { "acao=registrarFotosPrimeiroAcesso" })
    @ResponseBody
    public ResponseEntity<String> registrarFotosPrimeiroAcesso(@RequestBody(required = true) Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final String fotoFrontal = String.valueOf(body.get(FOTO_FRONTAL));
            final String fotoPerfilDireito = String.valueOf(body.get(FOTO_PERFIL_DIREITO));
            final String fotoPerfilEsquerdo = String.valueOf(body.get(FOTO_PERFIL_ESQUERDO));

            if ((session.getAttribute(EXIGE_RECONHECIMENTO_FACIAL_PRIMEIRO_ACESSO) != null) && "1".equals(session.getAttribute(EXIGE_RECONHECIMENTO_FACIAL_PRIMEIRO_ACESSO)) &&
                    (!TextHelper.isNull(fotoFrontal) && !TextHelper.isNull(fotoPerfilDireito) && !TextHelper.isNull(fotoPerfilEsquerdo))) {
                        salvarCapturaFotos(fotoFrontal, fotoPerfilDireito, fotoPerfilEsquerdo, responsavel);
                        if (validarCapturasReconhecimentoFacial(responsavel)) {
                            session.removeAttribute(EXIGE_RECONHECIMENTO_FACIAL_PRIMEIRO_ACESSO);
                        } else {
                            removerFotosCapturadas(responsavel);
                            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                        }

            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (final ArquivoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=erroReconhecimentoFacial" })
    public String erroCapturaReconhecimentoFacial(HttpServletRequest request, HttpSession session, Model model) throws ArquivoControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!validarCapturasReconhecimentoFacial(responsavel)) {
            removerFotosCapturadas(responsavel);
        }

        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.erro.validacao", responsavel));
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
    }

    private void salvarCapturaFotos(String fotoFrontal, String fotoPerfilDireito, String fotoPerfilEsquerdo, AcessoSistema responsavel) {
        try {
            // Transforma o conteúdo em Base64 para gravação no banco de dados
            final byte[] fotoFrontalBase64 = Base64.getEncoder().encode(fotoFrontal.getBytes());
            final byte[] fotoPerfilDireitoBase64 = Base64.getEncoder().encode(fotoPerfilDireito.getBytes());
            final byte[] fotoPerfilEsquerdoBase64 = Base64.getEncoder().encode(fotoPerfilEsquerdo.getBytes());

            final ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);

            final TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.SER_CODIGO, servidor.getSerCodigo());

            for (final String tarCodigo : TAR_CODIGOS) {
                if(tarCodigo.equals(TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_FRONTAL_SERVIDOR.getCodigo())) {
                    criterio.setAttribute(Columns.ARQ_CONTEUDO, fotoFrontalBase64);
                    criterio.setAttribute(Columns.ARQ_TAR_CODIGO, tarCodigo);
                    criterio.setAttribute(Columns.ASE_NOME, FOTO_FRONTAL);
                } else if (tarCodigo.equals(TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_PERFIL_DIREITO_SERVIDOR.getCodigo())) {
                    criterio.setAttribute(Columns.ARQ_CONTEUDO, fotoPerfilDireitoBase64);
                    criterio.setAttribute(Columns.ARQ_TAR_CODIGO, TAR_CODIGOS.get(1));
                    criterio.setAttribute(Columns.ASE_NOME, FOTO_PERFIL_DIREITO);
                } else if (tarCodigo.equals(TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_PERFIL_ESQUERDO_SERVIDOR.getCodigo())) {
                    criterio.setAttribute(Columns.ARQ_CONTEUDO, fotoPerfilEsquerdoBase64);
                    criterio.setAttribute(Columns.ARQ_TAR_CODIGO, TAR_CODIGOS.get(2));
                    criterio.setAttribute(Columns.ASE_NOME, FOTO_PERFIL_ESQUERDO);
                }
                arquivoController.createArquivoServidor(criterio, responsavel);
            }
        } catch (final ArquivoControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void removerFotosCapturadas(AcessoSistema responsavel) throws ArquivoControllerException {
            final String serCodigo = responsavel.getCodigoEntidade();
            final List<TransferObject> imagensReconhecimentoFacial = arquivoController.listArquivoServidor(serCodigo, TAR_CODIGOS, responsavel);
            for (final TransferObject imagem : imagensReconhecimentoFacial) {
                if (imagem.getAttribute(Columns.TAR_CODIGO).equals(TAR_CODIGOS.get(0)) ||
                    imagem.getAttribute(Columns.TAR_CODIGO).equals(TAR_CODIGOS.get(1)) ||
                    imagem.getAttribute(Columns.TAR_CODIGO).equals(TAR_CODIGOS.get(2))) {
                    arquivoController.removeArquivoReconhecimentoFacial((String) imagem.getAttribute(Columns.ARQ_CODIGO), serCodigo, responsavel);
                }
            }
    }

    private boolean validarCapturasReconhecimentoFacial(AcessoSistema responsavel) throws ArquivoControllerException {
        boolean imagemRostoFrontal = false;
        boolean imagemRostoPerfilDireito = false;
        boolean imagemRostoPerfilEsquerdo = false;

        final String serCodigo = responsavel.getCodigoEntidade();
        final List<TransferObject> imagensReconhecimentoFacial = arquivoController.listArquivoServidor(serCodigo, TAR_CODIGOS, responsavel);
        for (final TransferObject imagem : imagensReconhecimentoFacial) {
            if (imagem.getAttribute(Columns.TAR_CODIGO).equals(TAR_CODIGOS.get(0))) {
                imagemRostoFrontal = true;
            } else if (imagem.getAttribute(Columns.TAR_CODIGO).equals(TAR_CODIGOS.get(1))) {
                imagemRostoPerfilDireito = true;
            } else if (imagem.getAttribute(Columns.TAR_CODIGO).equals(TAR_CODIGOS.get(2))) {
                imagemRostoPerfilEsquerdo = true;
            }
        }
        return imagemRostoFrontal && imagemRostoPerfilDireito && imagemRostoPerfilEsquerdo && (imagensReconhecimentoFacial.size() == 3);

    }
}
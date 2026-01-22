package com.zetra.econsig.web.controller.sistema;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.MimeDetector;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

import eu.medsea.mimeutil.MimeType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: CarregarStreamWebController</p>
 * <p>Description: Controlador para buscar audio dinâmicamente via ajax</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 27276 $
 * $Date: 2019-07-18 09:44:31 -0300 (qui, 18 jul 2019) $
 */
@Controller
@RequestMapping(value = { "/v3/carregarStream" })
public class CarregarStreamWebController extends AbstractWebController {

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CarregarStreamWebController.class);

    private static final String ANEXO = "anexo";
    private static final String NOME_ARQUIVO = "nomeArquivo";
    private static final String DIRETORIO_ARQUIVOS_CONVERTIDOS = "arquivos_convertidos";
    private static final String JSP_VISUALIZAR_DOCUMENTOS = "jsp/validarDocumentos/visualizarDocumento";
    private static final String JSP_VISUALIZAR_PAG_ERRO = "jsp/visualizarPaginaErro/visualizarMensagem";

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=ouvir" }, produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public String playAudio(@RequestParam(value="adeCodigo") String adeCodigo, @RequestParam(value="adeData") String adeData, @RequestParam(value="aadNome") String aadNome, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException {
	    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
	    String hash = UUID.randomUUID().toString();
		try {
            Path anexoACopiarPath = FileSystems.getDefault().getPath(ParamSist.getDiretorioRaizArquivos()
                    + File.separatorChar + ANEXO + File.separatorChar + adeData
                    + File.separatorChar + adeCodigo);

			String pathFile = anexoACopiarPath.toString() + File.separatorChar + aadNome;
			String pathFileConvertido = anexoACopiarPath.toString() + File.separatorChar + DIRETORIO_ARQUIVOS_CONVERTIDOS + File.separatorChar + aadNome.substring(0, aadNome.lastIndexOf(".")) + "_convertido.mp3";

			File source = new File(pathFile);
			File sourceConvertido = new File(pathFileConvertido);

			if(!source.exists()) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
                return viewRedirect(JSP_VISUALIZAR_PAG_ERRO, request, session, model, responsavel);
            }

            Set<MimeType> mimeCollection = FileHelper.detectContentType(source);
            String contentType = mimeCollection != null && !mimeCollection.isEmpty() ? mimeCollection.toArray()[0].toString() : "audio/x-ms-wma";

            if ((!sourceConvertido.exists() && !contentType.equalsIgnoreCase("audio/x-ms-wma")) || (sourceConvertido.exists() && !contentType.equalsIgnoreCase("audio/x-ms-wma"))) {
                session.setAttribute(hash, pathFile);
            } else {
                if (!sourceConvertido.exists()) {
                    String arquivoConvertido = converterAudioMp3DocumentosPdf(pathFile, null, true, responsavel);

                    if (TextHelper.isNull(arquivoConvertido)) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
                        throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
                    }

                    Files.createDirectories(Paths.get(anexoACopiarPath + File.separator + DIRETORIO_ARQUIVOS_CONVERTIDOS));
                    OutputStream stream = new FileOutputStream(pathFileConvertido);
                    stream.write(Base64.decodeBase64(arquivoConvertido.getBytes()));
                    stream.close();
                }
                session.setAttribute(hash, pathFileConvertido);
            }

            model.addAttribute("hash",hash);
            model.addAttribute(NOME_ARQUIVO,aadNome);
            model.addAttribute("audio",Boolean.TRUE);
            return viewRedirect(JSP_VISUALIZAR_DOCUMENTOS, request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
            return viewRedirect(JSP_VISUALIZAR_PAG_ERRO, request, session, model, responsavel);
        }
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=visualizar" }, produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public String playVideo(@RequestParam(value="adeCodigo") String adeCodigo, @RequestParam(value="adeData") String adeData, @RequestParam(value="aadNome") String aadNome, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
	    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
	    String hash = UUID.randomUUID().toString();
		try {
            Path anexoACopiarPath = FileSystems.getDefault().getPath(ParamSist.getDiretorioRaizArquivos()
                    + File.separatorChar + ANEXO + File.separatorChar + adeData
                    + File.separatorChar + adeCodigo);

			String pathFile = anexoACopiarPath.toString() + File.separatorChar + aadNome;
			File source = new File(pathFile);
			String video = JspHelper.verificaVarQryStr(request, "video");

			if (!source.exists()) {
			    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
                return viewRedirect(JSP_VISUALIZAR_PAG_ERRO, request, session, model, responsavel);
            }



            model.addAttribute(NOME_ARQUIVO,aadNome);
            if(TextHelper.isNull(video)) {
                //Passando os atributos adeCodigo, adeData e aadNome,
                //pois no caso da imagem é utilizada a biblioteca ViewerJS que irá chamar a função de download mais de uma vez,
                //impossibilitando remover o hash da sessão como nos outros casos
                model.addAttribute("imagem",Boolean.TRUE);
                model.addAttribute("adeCodigo", adeCodigo);
                model.addAttribute("adeData", adeData);
                model.addAttribute("aadNome", aadNome);
            } else {
                session.setAttribute(hash, pathFile);
                model.addAttribute("video",Boolean.TRUE);
                model.addAttribute("hash",hash);
            }
            return viewRedirect(JSP_VISUALIZAR_DOCUMENTOS, request, session, model, responsavel);
		} catch (Exception ex) {
		    LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
            return viewRedirect(JSP_VISUALIZAR_PAG_ERRO, request, session, model, responsavel);
		}
	}

   @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=pdf" })
    public String pdf(@RequestParam(value="adeCodigo") String adeCodigo, @RequestParam(value="adeData") String adeData, @RequestParam(value="aadNome") String aadNome, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String hash = UUID.randomUUID().toString();

        try {
            Path anexoACopiarPath = FileSystems.getDefault().getPath(ParamSist.getDiretorioRaizArquivos() + File.separatorChar + ANEXO + File.separatorChar + adeData + File.separatorChar + adeCodigo);

            String pathFile = anexoACopiarPath.toString() + File.separatorChar + aadNome;
            String pathFileConvertido = anexoACopiarPath.toString() + File.separatorChar + DIRETORIO_ARQUIVOS_CONVERTIDOS + File.separatorChar + aadNome.substring(0, aadNome.lastIndexOf(".")) + "_convertido.pdf";

            File source = new File(pathFile);
            File sourceConvertido = new File(pathFileConvertido);

            if (!source.exists()) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
                return viewRedirect(JSP_VISUALIZAR_PAG_ERRO, request, session, model, responsavel);
            }

            boolean permiteConverterPdf = false;
            String[] extensoesPermitidasPdf = {".rtf", ".doc", ".docx", ".xls", ".xlsx", ".txt", ".csv"};
            for (String extensao : extensoesPermitidasPdf) {
                if(aadNome.endsWith(extensao)) {
                    permiteConverterPdf = true;
                    break;
                }
            }

            if((!sourceConvertido.exists() && !permiteConverterPdf) || (sourceConvertido.exists() && !permiteConverterPdf)) {
                session.setAttribute(hash, pathFile);
            }else {
                    if(!sourceConvertido.exists()) {
                String extensao = "." + aadNome.substring(aadNome.lastIndexOf(".") + 1);

                String arquivoConvertido = converterAudioMp3DocumentosPdf(pathFile, extensao, false, responsavel);

                if (TextHelper.isNull(arquivoConvertido)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
                    return viewRedirect(JSP_VISUALIZAR_PAG_ERRO, request, session, model, responsavel);
                }

                Files.createDirectories(Paths.get(anexoACopiarPath + File.separator + DIRETORIO_ARQUIVOS_CONVERTIDOS));
                OutputStream stream = new FileOutputStream(pathFileConvertido);
                stream.write(Base64.decodeBase64(arquivoConvertido.getBytes()));
                stream.close();
                    }
                session.setAttribute(hash, pathFileConvertido);
            }

            model.addAttribute("hash", hash);
            model.addAttribute(NOME_ARQUIVO,aadNome);
            model.addAttribute("pdf", Boolean.TRUE);
            return viewRedirect(JSP_VISUALIZAR_DOCUMENTOS, request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
            return viewRedirect(JSP_VISUALIZAR_PAG_ERRO, request, session, model, responsavel);
        }
    }

   private String converterAudioMp3DocumentosPdf(String path, String extensao,  boolean audio, AcessoSistema responsavel) throws IOException, InterruptedException {
       String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_CONVERSOR_AUDIO_MP3_DOCUMENT_PDF, responsavel);

       if (TextHelper.isNull(urlBase)) {
           LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.conversor", responsavel));
           return null;
       }

       UrlResource urlResource = new UrlResource("file://" + path);

       MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
       bodyMap.add("file", urlResource);
       bodyMap.add("audio", String.valueOf(audio));
       bodyMap.add("extensao", extensao);
       RequestEntity<MultiValueMap<String, Object>> request = RequestEntity.post(urlBase + "/api/converter/v1/binario")
               .contentType(MediaType.MULTIPART_FORM_DATA)
               .body(bodyMap);

       RestTemplate restTemplate = new RestTemplate();
       HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
       restTemplate.setRequestFactory(requestFactory);

       ResponseEntity<String> response = restTemplate.exchange(request, String.class);

       return response.getStatusCode() != HttpStatus.OK || response.getBody() == null ? null : response.getBody();
   }

   @RequestMapping(params = { "acao=download" })
   public void download(@RequestParam(value="hash") String hash, HttpServletResponse response, HttpServletRequest request, HttpSession session, Model model) throws Exception {
       AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
       String path = (String) session.getAttribute(hash);

       if(TextHelper.isNull(path)) {
           LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.session.key.invalido", responsavel));
           throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.session.key.invalido", responsavel));
       }else {
           File arquivo = new File(path);
           if(path.toLowerCase().endsWith(".pdf")) {
               String mime = "application/pdf";
               response.setContentType(mime);
           }
           else {
               Set<MimeType> mimeSet = MimeDetector.MIMEUTIL.detect(arquivo.getAbsolutePath());
               String mime = mimeSet != null && mimeSet.size() > 0 ? mimeSet.toArray()[0].toString() : "APPLICATION/OCTET-STREAM";
               response.setContentType(mime);
           }

           long tamanhoArquivoBytes = arquivo.length();
           BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(arquivo));
           if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
               response.addHeader("Content-Length", Long.toString(tamanhoArquivoBytes));
               IOUtils.copyLarge(entrada, response.getOutputStream());
           } else {
               response.setContentLength((int) tamanhoArquivoBytes);
               IOUtils.copy(entrada, response.getOutputStream());
           }

           response.flushBuffer();
           entrada.close();
           session.removeAttribute(hash);
       }
   }

   //Função criada para download da imagem onde o hash na sessão do usuário não é utilizado, por conta da biblioteca ViewerJS
   @RequestMapping(params = { "acao=downloadImg" })
   public void downloadImg(@RequestParam(value="adeCodigo") String adeCodigo, @RequestParam(value="adeData") String adeData, @RequestParam(value="aadNome") String aadNome, HttpServletResponse response, HttpServletRequest request, HttpSession session, Model model) throws Exception {
       AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
       Path anexoACopiarPath = FileSystems.getDefault().getPath(ParamSist.getDiretorioRaizArquivos()
               + File.separatorChar + ANEXO + File.separatorChar + adeData
               + File.separatorChar + adeCodigo);

       String path = anexoACopiarPath.toString() + File.separatorChar + aadNome;

       if(TextHelper.isNull(path)) {
           LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
           throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, aadNome));
       }else {
           File arquivo = new File(path);
           Set<MimeType> mimeSet = MimeDetector.MIMEUTIL.detect(arquivo.getAbsolutePath());
           String mime = mimeSet != null && mimeSet.size() > 0 ? mimeSet.toArray()[0].toString() : "APPLICATION/OCTET-STREAM";
           response.setContentType(mime);

           long tamanhoArquivoBytes = arquivo.length();
           BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(arquivo));
           if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
               response.addHeader("Content-Length", Long.toString(tamanhoArquivoBytes));
               IOUtils.copyLarge(entrada, response.getOutputStream());
           } else {
               response.setContentLength((int) tamanhoArquivoBytes);
               IOUtils.copy(entrada, response.getOutputStream());
           }

           response.flushBuffer();
           entrada.close();
       }
   }

   @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=visualizarTermo" })
   public String visualizarTermo(@RequestParam(value="csaCodigo") String csaCodigo, @RequestParam(value="nomeArquivo") String nomeArquivo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
       AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
       String hash = UUID.randomUUID().toString();

       try {
           String absolutePath = ParamSist.getDiretorioRaizArquivos();
           String arquivo = absolutePath + File.separatorChar + "credenciamento" + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar + nomeArquivo;

           File source = new File(arquivo);

           if (!source.exists()) {
               LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, nomeArquivo));
               session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, nomeArquivo));
               return viewRedirect(JSP_VISUALIZAR_PAG_ERRO, request, session, model, responsavel);
           }

           String extensao = "." + nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1);

           String arquivoConvertido = converterAudioMp3DocumentosPdf(arquivo, extensao, false, responsavel);

           if (TextHelper.isNull(arquivoConvertido)) {
               session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, nomeArquivo));
               return viewRedirect(JSP_VISUALIZAR_PAG_ERRO, request, session, model, responsavel);
           }

           OutputStream stream = new FileOutputStream(arquivo);
           stream.write(Base64.decodeBase64(arquivoConvertido.getBytes()));
           stream.close();
           session.setAttribute(hash, arquivo);

           model.addAttribute("hash", hash);
           model.addAttribute(NOME_ARQUIVO,nomeArquivo);
           model.addAttribute("pdf", Boolean.TRUE);
           return viewRedirect(JSP_VISUALIZAR_DOCUMENTOS, request, session, model, responsavel);
       } catch (Exception ex) {
           LOG.error(ex.getMessage(), ex);
           session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
           return viewRedirect(JSP_VISUALIZAR_PAG_ERRO, request, session, model, responsavel);
       }
   }

   @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=buscar" })
   public ResponseEntity<String> buscarArquivo(@RequestParam("csaCodigo") String csaCodigo, @RequestParam("nomeArquivo") String nomeArquivo) {
	   try {
		   String absolutePath = ParamSist.getDiretorioRaizArquivos();
	       String arquivo = absolutePath + File.separatorChar + "credenciamento" + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar + nomeArquivo;
	       File source = new File(arquivo);

	       String arquivoBase64 = Base64.encodeBase64String(FileUtils.readFileToByteArray(source));

	       return ResponseEntity.ok(arquivoBase64);
	   } catch (Exception ex) {
		  LOG.error(ex.getMessage(), ex);
		  return new ResponseEntity<>(HttpStatus.CONFLICT);
	  }
   }
}

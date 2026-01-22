package com.zetra.econsig.web.controller.dashboardflex;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.exception.DashboardFlexControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.DashboardFlex;
import com.zetra.econsig.persistence.entity.DashboardFlexConsulta;
import com.zetra.econsig.persistence.entity.DashboardFlexToolbar;
import com.zetra.econsig.service.dashboardflex.DashboardFlexController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

import br.com.nostrum.simpletl.util.TextHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/dashboardFlex" })
public class DashboardFlexWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DashboardFlexWebController.class);

    @Value("${licenca.flexmonster}")
    private String licencaFlexMonster;

    @Autowired
    private DashboardFlexController dashboardFlexController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        if (responsavel.isCor() || responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            List<DashboardFlex> listDashboardFlex = dashboardFlexController.listarDashboardFlex(true, responsavel.getPapCodigo(), new ArrayList<>(responsavel.getPermissoes().keySet()), responsavel);

            if (listDashboardFlex == null || listDashboardFlex.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.dashboardflex.nao.existe", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<String> dflCodigos = listDashboardFlex.stream().map(DashboardFlex::getDflCodigo).toList();
            List<DashboardFlexConsulta> listDashboardFlexConsultas = dashboardFlexController.listarDashboardFlexConsulta(dflCodigos, true, responsavel);

            if (listDashboardFlexConsultas == null || listDashboardFlexConsultas.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.dashboardflex.consulta.nao.existe", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<String> dfoCodigos = listDashboardFlexConsultas.stream().map(DashboardFlexConsulta::getDfoCodigo).toList();
            List<DashboardFlexToolbar> listDashboardFlexToolbars = dashboardFlexController.listarDashboardFlexToolbar(dfoCodigos, responsavel);

            HashMap<String, List<DashboardFlexConsulta>> hashDashFlexConsulta = listDashboardFlexConsultas.stream().collect(Collectors.groupingBy(DashboardFlexConsulta::getDflCodigo, HashMap::new,Collectors.toList()));

            HashMap<String, List<DashboardFlexToolbar>> hashDashFlexToolbar = new HashMap<>();

            if (listDashboardFlexToolbars != null && !listDashboardFlexToolbars.isEmpty()) {
                hashDashFlexToolbar = listDashboardFlexToolbars.stream().collect(Collectors.groupingBy(DashboardFlexToolbar::getDfoCodigo, HashMap::new, Collectors.toList()));
            }

            model.addAttribute("listDashboardFlex", listDashboardFlex);
            model.addAttribute("hashDashFlexConsulta", hashDashFlexConsulta);
            model.addAttribute("hashDashFlexToolbar", hashDashFlexToolbar);
            model.addAttribute("listDashboardFlexConsultasTodas", listDashboardFlexConsultas);
            model.addAttribute("licencaFlexMonster", licencaFlexMonster);

        } catch (DashboardFlexControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/dashboardFlex/dashboardFlex", request, session, model, responsavel);
    }

    @RequestMapping(value = "/handshake")
    @ResponseBody
    public ResponseEntity<byte[]> proxyHandshake(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) byte[] body) {
        return proxyConexaoFlexServer(request, response, body);
    }

    @RequestMapping(value = "/fields")
    @ResponseBody
    public ResponseEntity<byte[]> proxyFields(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) byte[] body) {
        return proxyConexaoFlexServer(request, response, body);
    }

    @RequestMapping(value = "/members")
    @ResponseBody
    public ResponseEntity<byte[]> proxyMembers(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) byte[] body) {
        return proxyConexaoFlexServer(request, response, body);
    }

    @RequestMapping(value = "/select")
    @ResponseBody
    public ResponseEntity<byte[]> proxySelect(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) byte[] body) {
        return proxyConexaoFlexServer(request, response, body);
    }

    @ResponseBody
    public ResponseEntity<byte[]> proxyConexaoFlexServer(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) byte[] body) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String urlPlataformaBI = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_PLATAFORMA_BI, responsavel);

        // Na lógica abaixo precisamos repassar para o flexmonster data server todas as requisições, headers, uri etc
        // Isso se faz necessário para replicarmos a chamada que seria pelo navegador do cliente para ser pelo backend.
        try {
            final String prefixoProxy = request.getContextPath() + "/v3/dashboardFlex";

            final String requestUri = request.getRequestURI();
            String targetPath = requestUri.substring(prefixoProxy.length());
            if (targetPath.isEmpty()) {
                targetPath = "/";
            }

            final StringBuilder target = new StringBuilder(urlPlataformaBI).append(targetPath);
            final String query = request.getQueryString();
            if ((query != null) && !query.isBlank()) {
                target.append("?").append(query);
            }
            final URI targetUri = URI.create(target.toString());

            final HttpMethod method;
            try {
                method = HttpMethod.valueOf(request.getMethod());
            } catch (final IllegalArgumentException ex) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }

            final HttpHeaders outHeaders = new HttpHeaders();
            for (final Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
                final String h = e.nextElement();
                final String lower = h.toLowerCase(Locale.ROOT);
                if ("authorization".equalsIgnoreCase(lower) || "cookie".equalsIgnoreCase(lower)) {
                    continue;
                }

                final List<String> values = Collections.list(request.getHeaders(h));
                if (!values.isEmpty()) {
                    outHeaders.put(h, values);
                }
            }

            if (request.getContentType() != null) {
                outHeaders.setContentType(MediaType.parseMediaType(request.getContentType()));
            }

            final var reqFactory = new SimpleClientHttpRequestFactory();
            reqFactory.setConnectTimeout(10_000);
            reqFactory.setReadTimeout(600_000);
            final RestTemplate rt = new RestTemplate(reqFactory);

            final HttpEntity<byte[]> entity = new HttpEntity<>(body, outHeaders);
            final ResponseEntity<byte[]> fdsResp = rt.exchange(targetUri, method, entity, byte[].class);

            final HttpHeaders returnHeaders = new HttpHeaders();
            fdsResp.getHeaders().forEach(returnHeaders::put);

            return new ResponseEntity<>(fdsResp.getBody(), returnHeaders, fdsResp.getStatusCode());
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

    @GetMapping(params = { "acao=recuperaArquivo" })
    @ResponseBody
    public ResponseEntity<Resource> recuperaArquivosStream(HttpServletRequest request) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String dfoCodigo = JspHelper.verificaVarQryStr(request, "dfoCodigo");
        if (TextHelper.isNull(dfoCodigo)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            DashboardFlexConsulta dashboardFlexConsulta = dashboardFlexController.getDashboardFlexConsulta(dfoCodigo, responsavel);
            String nomeArquivo = dashboardFlexConsulta.getDfoIndex();

            String raiz = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DIR_RAIZ_ARQUIVOS, responsavel);
            String caminho = raiz + File.separatorChar + "dashboardBI" + File.separatorChar + dashboardFlexConsulta.getDflCodigo();

            File diretorio = new File(caminho);
            if (!diretorio.exists() || !diretorio.isDirectory()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            File arquivo = new File(caminho + File.separatorChar + nomeArquivo);

            if (!arquivo.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            MediaType mediaType;
            if (nomeArquivo.endsWith(".json")) {
                mediaType = MediaType.APPLICATION_JSON;
            } else if (nomeArquivo.endsWith(".csv")) {
                mediaType = new MediaType("text", "csv", StandardCharsets.UTF_8);
            } else {
                mediaType = new MediaType("text", "plain", StandardCharsets.UTF_8);
            }

            InputStreamResource resource = new InputStreamResource( new BufferedInputStream(new FileInputStream(arquivo)));

            return ResponseEntity.ok().contentType(mediaType).contentLength(arquivo.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.inline().filename(arquivo.getName(), StandardCharsets.UTF_8).toString())
                    .body(resource);

        } catch (IOException | DashboardFlexControllerException ex) {
            LOG.error(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
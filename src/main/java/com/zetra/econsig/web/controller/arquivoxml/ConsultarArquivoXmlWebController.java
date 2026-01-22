package com.zetra.econsig.web.controller.arquivoxml;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.arquivo.DownloadWebController;

/**
 * <p>Title: ConsultarArquivoXmlWebController</p>
 * <p>Description: Controlador Web para o caso de uso Consultar Arquivo XML de Configuração.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: anderson.assis $
 * $Revision: 28565 $
 * $Date: 2020-05-13 16:23:03 -0200 (Qua, 13 mai 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarArquivoXml" })
public class ConsultarArquivoXmlWebController extends DownloadWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarArquivoXmlWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException, ParserException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        boolean usuExportaXML = responsavel.temPermissao(CodedValues.FUN_EXPORTA_XML_LAYOUT);

        String confPath = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "conf";
        List<File> listFiles = null;
        List<ArquivoDownload> listFilesOffset =null;
        HashMap<Object, Object> listaCsa = null;
        HashMap<Object, Object> xmlIds = null;
        List<File> listFilesLote = null;
        List<String> codigosCsa = null;
        String confPathLote = null;

        FileFilter filtro = new FileFilter() {
            @Override
            public boolean accept(File arq) {
                AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

                ParamSist parametros = ParamSist.getInstance();

                String entImpMargem = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_MARGEM, responsavel);
                String saiImpMargem = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_IMP_MARGEM, responsavel);
                String tradImpMargem = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_MARGEM, responsavel);

                String entExpMovFin = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_EXP_MOV_FIN, responsavel);
                String saiExpMovFin = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_EXP_MOV_FIN, responsavel);
                String tradExpMovFin = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_EXP_MOV_FIN, responsavel);

                String entImpRet = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_RETORNO, responsavel);
                String tradImpRet = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_RETORNO, responsavel);

                String entImpCrit = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_CRITICA, responsavel);
                String tradImpCrit = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_CRITICA, responsavel);

                String entExpTransf = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_EXP_TRANSF, responsavel);
                String tradExpTransf = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_EXP_TRANSF, responsavel);
                String saiExpTransf = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_EXP_TRANSF, responsavel);

                String entImpTransf = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_TRANSF, responsavel);
                String tradImpTransf = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_TRANSF, responsavel);

                String entRelIntRet = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_REL_INTEGRACAO_RETORNO, responsavel);
                String saiRelIntRet = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_REL_INTEGRACAO_RETORNO, responsavel);
                String tradRelIntRet = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_REL_INTEGRACAO_RETORNO, responsavel);

                String entImpMovFin = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_MOV_FIN, responsavel);
                String saiImpMovFin = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_IMP_MOV_FIN, responsavel);

                String entImpBloqSer = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_BLOQ_SERVIDOR, responsavel);
                String tradImpBloqSer = (String) parametros.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_BLOQ_SERVIDOR, responsavel);

                String arq_name = arq.getName().toLowerCase();

                return (arq_name.equals(entImpMargem) || arq_name.equals(saiImpMargem) || arq_name.equals(tradImpMargem) || arq_name.equals(entExpMovFin) || arq_name.equals(saiExpMovFin) || arq_name.equals(tradExpMovFin) || arq_name.equals(entImpRet) || arq_name.equals(tradImpRet) || arq_name.equals(entImpCrit) || arq_name.equals(tradImpCrit) || arq_name.equals(entExpTransf) || arq_name.equals(tradExpTransf) || arq_name.equals(saiExpTransf) || arq_name.equals(entImpTransf) || arq_name.equals(tradImpTransf) || arq_name.equals(entRelIntRet) || arq_name.equals(saiRelIntRet)
                        || arq_name.equals(tradRelIntRet) || arq_name.equals(entImpMovFin) || arq_name.equals(saiImpMovFin) || arq_name.equals(entImpBloqSer) || arq_name.equals(tradImpBloqSer));
            }
        };

        List<String> listFilesNames = FileHelper.getFilesInDir(confPath, filtro);
        listFiles = new ArrayList<>();
        listFilesOffset = new ArrayList<>();

        for (int i = 0; i < listFilesNames.size(); i++) {
            File next = new File(confPath + File.separatorChar + listFilesNames.get(i).toString());
            if (next != null && next.exists() && next.isFile() && next.canRead()) {
                listFiles.add(next);
            }
        }

        // lista arquivos XML de lote
        listFilesLote = new ArrayList<>();
        codigosCsa = new ArrayList<>();
        listaCsa = new HashMap<>();
        xmlIds = new HashMap<>();
        confPathLote = confPath + File.separatorChar + "lote";
        // filtro de arquivos de lote
        filtro = new FileFilter() {
            @Override
            public boolean accept(File arq) {
                String arq_name = arq.getName().toLowerCase();
                return (arq_name.endsWith("_entrada.xml") || arq_name.endsWith("_tradutor.xml"));
            }
        };

        File dirLote = new File(confPathLote);
        if (dirLote.exists()) {
            //Lista os arquivos
            File[] temp = dirLote.listFiles(filtro);
            if (temp != null) {
                listFilesLote.addAll(Arrays.asList(temp));
            }
        }

        String[] subdirLote = dirLote.list();
        if (subdirLote != null) {
            for (String element : subdirLote) {
                File arq = new File(confPathLote + File.separatorChar + element);
                if (arq.isDirectory()) {
                    File[] temp = arq.listFiles(filtro);
                    if (temp != null && temp.length > 0) {
                        listFilesLote.addAll(Arrays.asList(temp));
                        codigosCsa.add(element);
                    }
                }
            }
        }
        // verificações de arquivos de lote
        FileInputStream fileIn = null;
        if (listFilesLote != null && !listFilesLote.isEmpty()) {
            String nomeArq = null;
            Iterator<File> it = listFilesLote.iterator();
            while (it.hasNext()) {
                nomeArq = it.next().toString();

                // recupera ID dos arquivos
                fileIn = new FileInputStream(nomeArq);
                DocumentoTipo doc = XmlHelper.unmarshal(fileIn);

                // verifica se os arquivos entrada e tradutor estão em pares
                String nomeArqPar = null;
                if (nomeArq.indexOf("_entrada.xml") != -1) {
                    nomeArqPar = nomeArq.replace("_entrada.xml", "_tradutor.xml");
                    if (!TextHelper.isNull(doc.getID())) {
                        xmlIds.put(nomeArq, new String(doc.getID().toString() + " - Entrada"));
                        xmlIds.put(nomeArqPar, new String(doc.getID().toString() + " - Tradutor"));
                    }
                } else if (nomeArq.indexOf("_tradutor.xml") != -1) {
                    nomeArqPar = nomeArq.replace("_tradutor.xml", "_entrada.xml");
                    if (!TextHelper.isNull(doc.getID())) {
                        xmlIds.put(nomeArq, new String(doc.getID().toString() + " - Tradutor"));
                        xmlIds.put(nomeArqPar, new String(doc.getID().toString() + " - Entrada"));
                    }
                }
                if (!TextHelper.isNull(nomeArqPar)) {
                    File arqTmp = new File(nomeArqPar);
                    if (!arqTmp.exists()) {
                        it.remove();
                    }
                } else {
                    it.remove();
                }
            }
        }
        // unifica lista arquivos de configuração com arquivos de lote
        listFiles.addAll(listFilesLote);

        Collections.sort(listFiles, (f1, f2) -> {
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        });

        if (codigosCsa.size() > 0) {
            List<TransferObject> csaTO = null;
            try {
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CSA_CODIGO, codigosCsa);
                csaTO = consignatariaController.lstConsignatarias(criterio, responsavel);
                Iterator<TransferObject> it = csaTO.iterator();
                while (it.hasNext()) {
                    criterio = (CustomTransferObject) it.next();
                    listaCsa.put(criterio.getAttribute(Columns.CSA_CODIGO), criterio);
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        // Cria lista objetos tratados dos arquivos que podem ser listados
        List<ArquivoDownload> arquivosDownload = new ArrayList<>();
        if (listFiles != null && !listFiles.isEmpty()) {
            for (File file : listFiles) {
                String fileName = file.getAbsolutePath().substring(confPath.length() + 1);
                String fileNameAbrev = null;
                if (xmlIds != null && !xmlIds.isEmpty() && xmlIds.get(file.getAbsolutePath()) != null) {
                    fileNameAbrev = xmlIds.get(file.getAbsolutePath()).toString();
                } else {
                    fileNameAbrev = file.getName();
                }

                // link para download do arquivo
                String linkFileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                String tamanho = "";
                if (file.length() > 1024.00) {
                    tamanho = Math.round(file.length() / 1024.00) + " KB";
                } else {
                    tamanho = file.length() + " B";
                }
                String data = DateHelper.toDateTimeString(new java.util.Date(file.lastModified()));

                // recupera dados da consignatária
                String nome = file.getAbsolutePath();
                nome = nome.replace(confPathLote + File.separatorChar, "");
                nome = nome.replace(confPath + File.separatorChar, "");
                CustomTransferObject csa = (nome.indexOf(File.separatorChar) != -1) ? (CustomTransferObject) listaCsa.get(nome.substring(0, nome.indexOf(File.separatorChar))) : null;
                String entidade = (csa != null ? csa.getAttribute(Columns.CSA_NOME_ABREV).toString() : "");

                arquivosDownload.add(new ArquivoDownload(file, linkFileName, tamanho, data, entidade, fileNameAbrev));
            }
        }

        int total = arquivosDownload.size();
        int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception ex) {
        }

        int parcial = total - offset;

        if (parcial > size) {
            for (int i = offset; i < offset + size; i++) {
                listFilesOffset.add(arquivosDownload.get(i));
            }
        } else {
            for (int i = offset; i < offset + parcial; i++) {
                listFilesOffset.add(arquivosDownload.get(i));
            }
        }

        model.addAttribute("usuExportaXML", usuExportaXML);
        model.addAttribute("listFilesOffset", listFilesOffset);

        return viewRedirect("jsp/consultarArquivoXmlConfiguracao/downloadArquivoXml", request, session, model, responsavel);
    }

    @Override
    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=downloadArquivo" })
    public void downloadArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException {
        super.downloadArquivo(request, response, session, model);
    }
}


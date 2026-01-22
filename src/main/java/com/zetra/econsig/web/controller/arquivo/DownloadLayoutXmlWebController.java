package com.zetra.econsig.web.controller.arquivo;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.MimeDetector;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.config.AtributoTipo;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.parser.config.HeaderTipo;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.FileAbstractWebController;

import eu.medsea.mimeutil.MimeType;

/**
 * <p>Title: DownloadLayoutXmlWebController</p>
 * <p>Description: Controlador Web para realização de download de arquivos layout xml.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 26267 $
 * $Date: 2020-05-28 14:15:13 -0300 (qui, 28 may 2020) $
 */
@Controller
public class DownloadLayoutXmlWebController extends FileAbstractWebController {

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/downloadLayoutXml" }, params = { "acao=downloadLayoutXml" })
    public void downloadArquivoXml(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException, ParserException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        FileStatus status = processar(request, response, session, model);

        if (!status.getResultado()) {
            session.setAttribute(CodedValues.MSG_ERRO, status.getMensagemErro());
        } else {
            File arquivo = status.getArquivo();
            DocumentoTipo documento = XmlHelper.unmarshal(new FileInputStream(arquivo));

            if (documento != null) {
                List<?> atributos = documento.getAtributo();
                if (atributos.isEmpty()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.xmllayout.definicao.layout.arquivo", responsavel));
                    response.sendRedirect("../v3/exibirMensagem?acao=exibirMsgSessao&tipo=popup");
                    return;

                } else {
                    String absolutePath = new File(ParamSist.getDiretorioRaizArquivos()).getCanonicalPath();
                    String outputPath = absolutePath + File.separatorChar + "temp";
                    File layoutPah = new File(outputPath);
                    if (!layoutPah.exists() && !layoutPah.mkdirs()) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.criacao.diretorio", responsavel, outputPath));
                        response.sendRedirect("../v3/exibirMensagem?acao=exibirMsgSessao&tipo=popup");
                        return;
                    }

                    String filename = outputPath + File.separatorChar + "layout_" + arquivo.getName().replace("xml", "txt");
                    File layout = new File(filename);

                    String linha = "";
                    BufferedWriter saida = new BufferedWriter(new FileWriter(layout));
                    saida.write(ApplicationResourcesHelper.getMessage("roulo.xmllayout.cabecalho", responsavel));
                    saida.newLine();
                    saida.newLine();

                    // Insere os dados do HEADER
                    HeaderTipo header = documento.getHeader();
                    if (header != null) {
                        List<?> hAtributos = header.getAtributo();
                        if (!hAtributos.isEmpty()) {
                            Iterator<?> it = hAtributos.iterator();
                            int inicio = 1;
                            while (it.hasNext()) {
                                linha = "header.atributo;";
                                AtributoTipo proximo = (AtributoTipo) it.next();

                                linha += proximo.getNome() + ";";

                                if (proximo.isIndiceDefinido()) {
                                    linha += Integer.toString(inicio) + ";";
                                    inicio += proximo.getTamanho();
                                    linha += Integer.toString(inicio - 1) + ";";

                                } else {
                                    linha += Integer.toString(proximo.getInicio() + 1) + ";";
                                    linha += Integer.toString(proximo.getInicio() + proximo.getTamanho()) + ";";
                                }

                                linha += Integer.toString(proximo.getTamanho()) + ";";

                                if (proximo.getComplemento() != null) {
                                    linha += proximo.getComplemento() + ";";
                                } else {
                                    linha += ";";
                                }

                                if (proximo.getAlinhamento() != null) {
                                    linha += proximo.getAlinhamento() + ";";
                                } else {
                                    linha += ";";
                                }

                                if (proximo.getDefault() != null) {
                                    linha += proximo.getDefault() + ";";
                                } else {
                                    linha += ";";
                                }

                                saida.write(linha);
                                saida.newLine();
                            }
                        }
                    }

                    // Insere os dados dos Atributos
                    Iterator<?> it = atributos.iterator();
                    int inicio = 1;
                    while (it.hasNext()) {
                        linha = "atributo;";
                        AtributoTipo proximo = (AtributoTipo) it.next();
                        linha += proximo.getNome() + ";";

                        if (proximo.isIndiceDefinido()) {
                            linha += Integer.toString(inicio) + ";";
                            inicio += proximo.getTamanho();
                            linha += Integer.toString(inicio - 1) + ";";

                        } else {
                            linha += Integer.toString(proximo.getInicio() + 1) + ";";
                            linha += Integer.toString(proximo.getInicio() + proximo.getTamanho()) + ";";
                        }

                        linha += Integer.toString(proximo.getTamanho()) + ";";

                        if (proximo.getComplemento() != null) {
                            linha += proximo.getComplemento() + ";";
                        } else {
                            linha += ";";
                        }

                        if (proximo.getAlinhamento() != null) {
                            linha += proximo.getAlinhamento() + ";";
                        } else {
                            linha += ";";
                        }

                        if (proximo.getDefault() != null) {
                            linha += proximo.getDefault() + ";";
                        } else {
                            linha += ";";
                        }

                        saida.write(linha);
                        saida.newLine();
                    }

                    // Insere os dados do FOOTER
                    HeaderTipo footer = documento.getFooter();
                    if (footer != null) {
                        List<?> fAtributos = footer.getAtributo();
                        if (!fAtributos.isEmpty()) {
                            it = fAtributos.iterator();
                            inicio = 1;
                            while (it.hasNext()) {
                                linha = "footer.atributo;";
                                AtributoTipo proximo = (AtributoTipo) it.next();
                                linha += proximo.getNome() + ";";

                                if (proximo.isIndiceDefinido()) {
                                    linha += Integer.toString(inicio) + ";";
                                    inicio += proximo.getTamanho();
                                    linha += Integer.toString(inicio - 1) + ";";

                                } else {
                                    linha += Integer.toString(proximo.getInicio() + 1) + ";";
                                    linha += Integer.toString(proximo.getInicio() + proximo.getTamanho()) + ";";
                                }

                                linha += Integer.toString(proximo.getTamanho()) + ";";

                                if (proximo.getComplemento() != null) {
                                    linha += proximo.getComplemento() + ";";
                                } else {
                                    linha += ";";
                                }

                                if (proximo.getAlinhamento() != null) {
                                    linha += proximo.getAlinhamento() + ";";
                                } else {
                                    linha += ";";
                                }

                                if (proximo.getDefault() != null) {
                                    linha += proximo.getDefault() + ";";
                                } else {
                                    linha += ";";
                                }

                                saida.write(linha);
                                saida.newLine();
                            }
                        }
                    }

                    saida.close();

                    long size = layout.length();

                    if (size == 0) {
                        size = 1;
                    }

                    Set<MimeType> mimeSet = MimeDetector.MIMEUTIL.detect(layout.getAbsolutePath());
                    String mime = mimeSet != null && mimeSet.size() > 0 ? mimeSet.toArray()[0].toString() : "APPLICATION/OCTET-STREAM";
                    response.setContentType(mime);
                    response.setContentLength((int) size);
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + layout.getName() + "\"");

                    BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(layout));
                    if (size > Integer.MAX_VALUE) {
                        org.apache.commons.io.IOUtils.copyLarge(entrada, response.getOutputStream());
                    } else {
                        org.apache.commons.io.IOUtils.copy(entrada, response.getOutputStream());
                    }
                    response.flushBuffer();
                    entrada.close();
                }
            }
        }
    }
}

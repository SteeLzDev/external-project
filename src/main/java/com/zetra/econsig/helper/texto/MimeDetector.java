package com.zetra.econsig.helper.texto;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import eu.medsea.mimeutil.detector.OpendesktopMimeDetector;
import eu.medsea.util.EncodingGuesser;

/**
 * <p>Title: MimeDetector</p>
 * <p>Description: Enum para implementação de singleton de detecção de content-type</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 */

public enum MimeDetector {
    MIMEUTIL;

    /**
     * Construtor
     */
    MimeDetector() {
        Set<String> enc = new HashSet<>(0);
        enc.add("ISO-8859-1");
        enc.add("UTF-8");
        enc.add("windows-1252");
        enc.add(EncodingGuesser.getDefaultEncoding());
        EncodingGuesser.setSupportedEncodings(enc);

        magicDetector.registerMimeDetector(MagicMimeMimeDetector.class.getName());
        extensionDetector.registerMimeDetector(ExtensionMimeDetector.class.getName());
        opendesktopDetector.registerMimeDetector(OpendesktopMimeDetector.class.getName());
    }

    private final MimeUtil2 magicDetector = new MimeUtil2();
    private final MimeUtil2 extensionDetector = new MimeUtil2();
    private final MimeUtil2 opendesktopDetector = new MimeUtil2();

    /**
     * Detecta o content-type do arquivo
     * @param fileName
     * @return
     * @throws IOException
     */
    public Set<MimeType> detect(String fileName) throws IOException {
        Set<MimeType> result = new HashSet<>();

        Collection<MimeType> resultMagic = magicDetector.getMimeTypes(fileName);
        Collection<MimeType> resultExtension = extensionDetector.getMimeTypes(fileName);
        Collection<MimeType> resultOpendesktop = opendesktopDetector.getMimeTypes(fileName);

        result.addAll(new HashSet<>(resultMagic));
        result.addAll(new HashSet<>(resultExtension));
        result.addAll(new HashSet<>(resultOpendesktop));

        return new HashSet<>(result);
    }

    /**
     * Valida a extensão com seu content-type.
     * @param fileName
     * @return
     * @throws IOException
     */
    public Boolean validaExtensao(String fileName) throws IOException {

        /*
         * Se o conjunto de mime-types por extensão possui alguns dos mesmos elementos
         * do conjuto de mime-types por conteúdo, a extensão, provavelmente, corresponde
         * ao conteúdo.
         */

        HashSet<MimeType> resultMagic = null;
        HashSet<MimeType> resultExtension = null;
        try {
            resultMagic = new HashSet<MimeType>(magicDetector.getMimeTypes(fileName));
            resultExtension = new HashSet<MimeType>(extensionDetector.getMimeTypes(fileName));
            resultExtension.addAll(new HashSet<MimeType>(opendesktopDetector.getMimeTypes(fileName)));
        } catch (Exception e) {
            throw new IOException (ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo", (AcessoSistema) null, fileName));
        }

        /* Nos testes, o MagicMimeMimeDetector retorna application/msword tanto para xls quanto para doc
         * No caso do xls, isto ocasionou um problema pois os outros retornam
         * [application/vndms-excel, application/excel, application/x-excel, application/x-msexcel]
         *
         * Caso seja um arquivo xlsx/docx ou extensões OpenDocument, o contentType retornado é application/zip,
         * sendo assim a extensão será retornada válida caso o contentType seja application/zip e a extensão .xlsx/.docx ou extensões OpenDocument.
         */
        //wma formato novo
        if(resultMagic.contains(new MimeType("application/octet-stream")) && (
        		resultExtension.contains(new MimeType("audio/x-ms-wma")) ||
        		resultExtension.contains(new MimeType("video/x-ms-asf")) ||
        		resultExtension.contains(new MimeType("video/x-ms-wmv")))) {

        	return true;
        }

        //wma antigo
        if(resultMagic.contains(new MimeType("audio/x-wav")))
        {return true;}

        //word/excel
        if((resultMagic.contains(new MimeType("application/msword")) || resultMagic.contains(new MimeType("application/excel")) || resultMagic.contains(new MimeType("application/zip"))) &&
        		( fileName.toLowerCase().endsWith(".docx") || fileName.toLowerCase().endsWith(".doc") || fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls") ||
        				( fileName.indexOf(".") != -1 &&
        				FileHelper.OPEN_DOCUMENT_FORMATS.contains(fileName.substring(fileName.lastIndexOf("."), fileName.length()))
        						)
        				)
        		)
        {return true;}

        //jfi é um jpeg
        if(resultExtension.contains(new MimeType("application/octet-stream")) && resultMagic.contains(new MimeType("image/jpeg"))){
            return true;
        }

        //se não nenhum entra aqui
        resultExtension.retainAll(resultMagic);
        return !resultExtension.isEmpty();
    }
}

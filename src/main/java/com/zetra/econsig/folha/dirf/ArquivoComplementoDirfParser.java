package com.zetra.econsig.folha.dirf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.helper.texto.CharsetDetector;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: ArquivoComplementoDirfParser</p>
 * <p>Description: Parser para ler dados de arquivo de complemento de DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26174 $
 * $Date: 2019-02-06 18:29:53 -0200 (Qua, 06 fev 2019) $
 */
public class ArquivoComplementoDirfParser {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ArquivoComplementoDirfParser.class);

    public Map<String, String> parse(String nomeArquivo) {

    	Map <String, String> result = new HashMap<>();

    	if (nomeArquivo == null) {
    		return result;
    	}

        // Tenta identificar o charset do arquivo de entrada, para que a leitura seja feita sem inconsistências
        String charsetFile = null;
        try {
            charsetFile = CharsetDetector.detect(nomeArquivo);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Caso não tenha detectado o charset do arquivo, assume ISO-8859-1 como padrão
        if (charsetFile == null) {
            charsetFile = "ISO-8859-1".intern();
        }

        BufferedReader input = null;
        try {
            // Abro o arquivo de entrada usando o charset identificado
            input = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo), charsetFile));

            // Para cada linha de entrada, identifica o registro a que se refere a linha
            String line = null;
            while ((line = input.readLine()) != null) {

            	if (!TextHelper.isNull(line)) {
            	// Character delimitador é ;
            		String[] lineValues = line.split(";");

            		if (lineValues.length > 0) {

            			// O primeiro campo é o "CPF"
            			String cpf = lineValues[0];

            			// remove qualquer mascara do "CPF"
            			cpf = cpf.replaceAll("[^a-zA-Z0-9]", "");

            			// O valor estará na posicao 3
            			String valor = lineValues[2];

            			// O map será chaveado pelo cpf do servidor
            			result.put(cpf, valor);

            		}
            	}
            }

            return result;

        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }
}

package com.zetra.econsig.report.jasper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.texto.TextHelper;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.util.JRStyledText;

/**
 * <p>Title: CsvExporter</p>
 * <p>Description: Exporta um documento JasperReports document para o formato CSV padrão eConsig.
 * Utiliza a estrutura da classe JRCsvExporter da JasperReports. Método exportPage(...) totalmente
 * adaptado.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CsvExporter extends JRCsvExporter {

    public CsvExporter() {
    }

    @Override
    protected void exportPage(JRPrintPage page) throws IOException {
        final List<JRPrintElement> elements = new ArrayList<>();
        final List<String> texts = new ArrayList<>();
        final List<String> sumary = new ArrayList<>();
        int coordYUltimoElemento = -1;
        boolean printHeadersValues = true;
        boolean printHeadersTitles = true;

        // Mapeia o nome do campo de cabeçalho de grupo com seu valor
        final Map<String, String> groupHeadersValues = new HashMap<>();

        // Mapeia o nome do campo de cabeçalho de grupo com seu título
        final Map<String, String> groupHeadersTitles = new HashMap<>();

        // Determina a ordem de impressão dos itens de valores de cabeçalho de grupo
        final List<JRPrintElement> groupHeadersValuesOrder = new ArrayList<>();

        // Determina a ordem de impressão dos itens de títulos de cabeçalho de grupo
        final List<JRPrintElement> groupHeadersTitlesOrder = new ArrayList<>();

        for (final JRPrintElement element : page.getElements()) {

            // Trata os itens de cabeçalho de grupo.
            if ((element instanceof JRPrintText) && (element.getKey() != null) &&
                   ((element.getOrigin().getBandType() == BandTypeEnum.GROUP_HEADER) || (element.getOrigin().getBandType() == BandTypeEnum.COLUMN_HEADER))) {
                final JRStyledText styledText = getStyledText((JRPrintText) element);
                String elementTextValue = "";
                String elementTitle = "";

                // Separa o título do elemento do seu valor, caso haja.
                if (styledText != null) {
                    elementTitle = styledText.getText().replace('\n', ' ');
                    final int colonPos = elementTitle.indexOf(": ");

                    // Se se trata de um elemento que pode possuir valor além de título.
                    // Se existe dois pontos no texto do elemento, quebra em título e valor.
                    if (element.getKey().toLowerCase().startsWith("textfield") && (colonPos != -1)) {
                        elementTextValue = ((colonPos + 2) < elementTitle.length()) ? elementTitle.substring(colonPos + 2) : "";
                        elementTitle = elementTitle.substring(0, colonPos);
                    }

                    // Retira os dois pontos finais caso existam.
                    if (elementTitle.endsWith(":")) {
                        elementTitle = elementTitle.substring(0, elementTitle.length() - 1);
                    }
                }

                // Se há título no elemento, salva na lista de títulos
                if (!TextHelper.isNull(elementTitle)) {
                    groupHeadersTitles.put(element.getKey().toLowerCase(), elementTitle);

                    // Verifica se a ordem do item de cabeçalho já foi determinada na lista de cabeçalhos.
                    boolean orderedHeaderTitle = false;
                    for (final JRPrintElement printElement : groupHeadersTitlesOrder) {
                        if (element.getKey().toLowerCase().equals(printElement.getKey().toLowerCase())) {
                            orderedHeaderTitle = true;
                            break;
                        }
                    }

                    // Se o item de cabeçalho ainda não foi ordenado.
                    if (!orderedHeaderTitle) {
                        int headerTitleIndex = 0;
                        // Percorre a lista de itens já ordenados.
                        while (headerTitleIndex < groupHeadersTitlesOrder.size()) {
                            // Se o item está mais abaixo que o item já ordenado
                            if (element.getY() > groupHeadersTitlesOrder.get(headerTitleIndex).getY()) {
                                headerTitleIndex++;
                            } else if (element.getY() == groupHeadersTitlesOrder.get(headerTitleIndex).getY()) {
                                // Se o item está na mesma altura do item já ordenado, calcula a posição baseando-se na coordenada X.
                                while ((headerTitleIndex < groupHeadersTitlesOrder.size())
                                        && (element.getY() == groupHeadersTitlesOrder.get(headerTitleIndex).getY())
                                        && (element.getX() > groupHeadersTitlesOrder.get(headerTitleIndex).getX())) {
                                    headerTitleIndex++;
                                }
                                break;
                            } else {
                                break;
                            }
                        }
                        groupHeadersTitlesOrder.add(headerTitleIndex, element);
                    }
                }

                if ((elementTextValue != null) && !elementTextValue.isEmpty()) {
                    groupHeadersValues.put(element.getKey().toLowerCase(), elementTextValue);

                    // Verifica se a ordem do valor de item de cabeçalho já foi determinada
                    boolean orderedHeaderValue = false;
                    for (final JRPrintElement printElement : groupHeadersValuesOrder) {
                        if (element.getKey().toLowerCase().equals(printElement.getKey().toLowerCase())) {
                            orderedHeaderValue = true;
                            break;
                        }
                    }

                    // Determina a posição do texto de cabeçalho, baseando-se na coordenada Y do elemento.
                    if (!orderedHeaderValue) {
                        int headerTextIndex = 0;
                        while ((headerTextIndex < groupHeadersValuesOrder.size()) && (element.getY() > groupHeadersValuesOrder.get(headerTextIndex).getY())) {
                            headerTextIndex++;
                        }
                        groupHeadersValuesOrder.add(headerTextIndex, element);
                    }
                }
            }

            // Trata os itens de detalhe do relatório
            if ((element.getOrigin().getBandType() == BandTypeEnum.DETAIL) && (element instanceof JRPrintText) && ((element.getKey() == null) || !element.getKey().toLowerCase().startsWith("fundolinha"))) {
                if (printHeadersTitles && (groupHeadersTitles.size() > 0)) {
                    printOrderedItens(groupHeadersTitles, groupHeadersTitlesOrder, true);
                    printHeadersTitles = false;
                }

                if (printHeadersValues && (groupHeadersValues.size() > 0)) {
                    printOrderedItens(groupHeadersValues, groupHeadersValuesOrder, false);
                    printHeadersValues = false;
                }

                // Se é uma nova linha, imprime os valores de detalhe
                // já armazenados (correspondentes à linha anterior) e
                // marca para imprimir o cabeçalho.
                if ((coordYUltimoElemento >= 0) && (coordYUltimoElemento != element.getY())) {
                    writer.write(TextHelper.join(texts, fieldDelimiter));
                    writer.write(recordDelimiter);
                    texts.clear();
                    elements.clear();
                    printHeadersValues = true;
                }

                // Determina a posição do texto de detalhe, baseando-se na coordenada X do elemento.
                int textIndex = 0;
                while ((textIndex < elements.size()) && (element.getX() > elements.get(textIndex).getX())) {
                    textIndex++;
                }
                elements.add(textIndex, element);

                coordYUltimoElemento = element.getY();
                final JRStyledText styledText = getStyledText((JRPrintText) element);
                texts.add(textIndex, styledText != null ? styledText.getText() : "");
            }

            // Trata os itens de cabeçalho de grupo.
            if ((element instanceof JRPrintText) && (element.getKey() != null) &&
                   ((element.getOrigin().getBandType() == BandTypeEnum.PAGE_FOOTER) || (element.getOrigin().getBandType() == BandTypeEnum.COLUMN_FOOTER))) {
                final JRStyledText styledText = getStyledText((JRPrintText) element);
                sumary.add(styledText != null ? styledText.getText() : "");
            }


            // Trata os campos comentários da banda sumário que serão impressos
            if ((element.getOrigin().getBandType() == BandTypeEnum.SUMMARY) && (element instanceof JRPrintText) && ((element.getKey() == null) || element.getKey().toLowerCase().startsWith("comentario"))) {
                final JRStyledText styledText = getStyledText((JRPrintText) element);
                sumary.add(styledText != null ? styledText.getText() : "");
            }
        }

        // Se sobrou algum título de cabeçalho a imprimir.
        if (printHeadersTitles && (groupHeadersTitles.size() > 0)) {
            printOrderedItens(groupHeadersTitles, groupHeadersTitlesOrder, true);
            printHeadersTitles = false;
        }

        // Se sobrou algum valor de cabeçalho a imprimir.
        if (printHeadersValues && (groupHeadersValues.size() > 0)) {
            printOrderedItens(groupHeadersValues, groupHeadersValuesOrder, false);
            printHeadersValues = false;
        }

        // Imprime os campos remanescentes.
        if (texts.size() > 0) {
            writer.write(TextHelper.join(texts, fieldDelimiter));
            writer.write(recordDelimiter);
            texts.clear();
            elements.clear();
        }

        // Imprime comentários
        if (sumary.size() > 0) {
            writer.write(TextHelper.join(sumary, recordDelimiter));
            writer.write(recordDelimiter);
            sumary.clear();
            elements.clear();
        }
    }

    /**
     * Imprime o conjunto de itens de acordo com uma orderm pré-determinada
     * @param itens
     * @param itensOrder
     * @param secondItensOrder
     * @throws IOException
     */
    private void printOrderedItens(Map<String, String> itens, List<JRPrintElement> itensOrder, boolean breakLine) throws IOException {
        final List<String> orderedItens = new ArrayList<>();
        if (itensOrder != null) {
            for (final JRPrintElement itenElement : itensOrder) {
                orderedItens.add(itens.get(itenElement.getKey().toLowerCase()));
            }
        }

        writer.write(TextHelper.join(orderedItens, fieldDelimiter));

        if (orderedItens.size() > 0) {
            if (breakLine) {
                writer.write(recordDelimiter);
            } else {
                writer.write(fieldDelimiter);
            }
        }
    }
}

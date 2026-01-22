package com.zetra.econsig.helper.sistema;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CreateImageHelper {

    public static String gerarImagemTransparente(String caminhoArquivoBase) throws IOException {
        final File arquivoBase = new File(caminhoArquivoBase);

        if (arquivoBase.exists()) {
            // Se o arquivo base de entrada existe, define o nome do arquivo de saída, na mesma
            // pasta do arquivo de entrada, com mesmo nome e com o sufixo "_transparente".
            final String[] partesNomeArquivoBase = arquivoBase.getName().split("\\.");
            final String caminhoArquivoTransparente = arquivoBase.getParent() + File.separatorChar + partesNomeArquivoBase[0] + "_transparente." + partesNomeArquivoBase[1];
            final File arquivoTransparente = new File(caminhoArquivoTransparente);

            // Se o arquivo ainda não foi gerado, gera.
            if (!arquivoTransparente.exists()) {
                final BufferedImage original = ImageIO.read(arquivoBase);
                final int width = original.getWidth();
                final int height = original.getHeight();
                final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                final float opacity = 0.3f;
                final Graphics2D g = image.createGraphics();

                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g.drawImage(original, 0, 0, width, height, null);

                ImageIO.write(image, "png", arquivoTransparente);
            }

            return caminhoArquivoTransparente;
        } else {
            return "";
        }
    }
}

package com.zetra.econsig.helper;

import java.util.Random;

public class GeradorDocumentoHelper {

	private static final Random random = new Random();

	// Gera CPF válido
	public static String gerarCPF(boolean comMascara) {
		int n1 = random.nextInt(10);
		int n2 = random.nextInt(10);
		int n3 = random.nextInt(10);
		int n4 = random.nextInt(10);
		int n5 = random.nextInt(10);
		int n6 = random.nextInt(10);
		int n7 = random.nextInt(10);
		int n8 = random.nextInt(10);
		int n9 = random.nextInt(10);

		int d1 = calcularDigitoVerificador(new int[] { n1, n2, n3, n4, n5, n6, n7, n8, n9 });
		int d2 = calcularDigitoVerificador(new int[] { n1, n2, n3, n4, n5, n6, n7, n8, n9, d1 });

		String cpf = String.format("%d%d%d%d%d%d%d%d%d%d%d", n1, n2, n3, n4, n5, n6, n7, n8, n9, d1, d2);

		return comMascara ? cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4") : cpf;
	}

	private static int calcularDigitoVerificador(int[] numeros) {
		int soma = 0;
		for (int i = 0; i < numeros.length; i++) {
			soma += numeros[i] * ((numeros.length + 1) - i);
		}
		int resto = 11 - (soma % 11);
		return (resto > 9) ? 0 : resto;
	}
}

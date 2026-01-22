-- DESENV-16435
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('865', 'Categoria exibida no Relatório Gerencial Geral corresponde à categoria específica do cargo do registro do servidor', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('865', 1, 'N');

UPDATE tb_relatorio SET REL_TEMPLATE_SUBRELATORIO = 'GerencialGeralCapa.jasper, GerencialGeralSumario.jasper, GerencialGeralObjetivo.jasper, GerencialGeralZetraSoft.jasper, GerencialGeraleConsig.jasper, GerencialGeraleConsig1.jasper, GerencialGeralOrgaoPorServidor.jasper, GerencialGeralServidores.jasper, GerencialGeralServidoresPorCargo.jasper, GerencialGeralMargem.jasper, GerencialGeralMargem1.jasper, GerencialGeralFaixaMargem1.jasper, GerencialGeralComprometimentoMargem1.jasper, GerencialGeralMargem2.jasper, GerencialGeralFaixaMargem2.jasper, GerencialGeralComprometimentoMargem2.jasper, GerencialGeralMargem3.jasper, GerencialGeralFaixaMargem3.jasper, GerencialGeralComprometimentoMargem3.jasper, GerencialGeralCsa.jasper, GerencialGeralCor.jasper, GerencialGeralContratosCategoria.jasper, GerencialGeralContratosPorCargo.jasper, GerencialGeralContratosServico.jasper, GerencialGeralInadimplencia.jasper, GerencialGeralConsideracoes.jasper, GerencialGeralTaxas.jasper, GerencialGeralTaxasEfetivas.jasper' where REL_CODIGO = 'gerencial';


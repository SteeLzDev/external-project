-- DESENV-17973
UPDATE tb_relatorio SET REL_TEMPLATE_SUBRELATORIO = REL_TEMPLATE_SUBRELATORIO || ', GerencialGeralCsaGraficoInternacional.jasper, GerencialGeralCorGraficoInternacional.jasper' WHERE REL_CODIGO = 'gerencial_internacional';


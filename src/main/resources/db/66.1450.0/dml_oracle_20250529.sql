-- DESENV-23007
UPDATE tb_relatorio SET REL_TEMPLATE_SUBRELATORIO = (REL_TEMPLATE_SUBRELATORIO || ', RegrasConvenioOrgaos.jasper, RegrasConvenioMargens.jasper') WHERE REL_CODIGO = 'regra_convenio';


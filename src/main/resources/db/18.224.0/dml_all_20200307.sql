-- DESENV-13454

-- Manutenções >> Coeficientes de correção: 
UPDATE tb_texto_sistema SET TEX_TEXTO = CONCAT(UCASE(LEFT(TEX_TEXTO, 1)), LCASE(SUBSTRING(TEX_TEXTO, 2))) WHERE TEX_CHAVE = 'rotulo.lst.coeficiente.correcao.titulo';
UPDATE tb_texto_sistema SET TEX_TEXTO = CONCAT(UCASE(LEFT(TEX_TEXTO, 1)), LCASE(SUBSTRING(TEX_TEXTO, 2))) WHERE TEX_CHAVE = 'rotulo.coeficiente.correcao.editar';
UPDATE tb_texto_sistema SET TEX_TEXTO = CONCAT(UCASE(LEFT(TEX_TEXTO, 1)), LCASE(SUBSTRING(TEX_TEXTO, 2))) WHERE TEX_CHAVE = 'rotulo.coeficiente.correcao.listar';

-- Manutenções >> Consignante >> Alterar configurações de margem:
UPDATE tb_texto_sistema SET TEX_TEXTO = CONCAT(UCASE(LEFT(TEX_TEXTO, 1)), LCASE(SUBSTRING(TEX_TEXTO, 2))) WHERE TEX_CHAVE = 'rotulo.margem.exibicao.titulo';

-- Manutenções >> Consignante >> Alterar os parâmetros:
UPDATE tb_texto_sistema SET TEX_TEXTO = CONCAT(UCASE(LEFT(TEX_TEXTO, 1)), LCASE(SUBSTRING(TEX_TEXTO, 2))) WHERE TEX_CHAVE = 'rotulo.parametro.consignante.titulo';
UPDATE tb_texto_sistema SET TEX_TEXTO = REPLACE(TEX_TEXTO, "upper", "lower") where TEX_CHAVE = 'rotulo.parametro.consignante.titulo';

-- Sistema >> Usuários Suporte:
UPDATE tb_texto_sistema SET TEX_TEXTO = CONCAT(UCASE(LEFT(TEX_TEXTO, 1)), LCASE(SUBSTRING(TEX_TEXTO, 2))) WHERE TEX_CHAVE = 'rotulo.usuario.papel.titulo';
UPDATE tb_texto_sistema SET TEX_TEXTO = REPLACE(TEX_TEXTO, "upper", "lower") where TEX_CHAVE = 'rotulo.usuario.papel.titulo';

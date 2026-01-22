-- DESENV-15251
SET @ordenacao:=0;
UPDATE tb_tipo_dado_adicional SET TDA_ORDENACAO = @ordenacao:=@ordenacao+1 ORDER BY TDA_DESCRICAO;

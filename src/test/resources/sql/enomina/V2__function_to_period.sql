-- MySQL para rodar no eConsig: ESCOLHA[M=Mensal;Q=Quinzenal;G=Quatorzenal;S=Semanal]
update tb_tipo_param_sist_consignante set tpc_dominio = concat('ESCOLHA[M=Mensal', cast(0x3b as char), 'Q=Quinzenal', cast(0x3b as char), 'G=Quatorzenal', cast(0x3b as char), 'S=Semanal]') where tpc_codigo = '465';

-- FUNCAO MYSQL PARA FORMATAR UMA DATA QUE REPRESENTA UM PERIODO DE
-- ACORDO COM A PERIODICIDADE DA FOLHA (PARAM SIST 465)

DELIMITER /

CREATE FUNCTION to_period(dt date)
RETURNS CHAR(7) DETERMINISTIC
BEGIN
  DECLARE p CHAR(1); -- Periodicidade
  DECLARE d INTEGER; -- Dia da data informada
  DECLARE m INTEGER; -- Mes da data informada
  DECLARE y INTEGER; -- Ano da data informada
  DECLARE n INTEGER; -- Numero do periodo

  SET p = (SELECT PSI_VLR FROM tb_param_sist_consignante WHERE TPC_CODIGO = '465');

  IF p = 'M' THEN -- Mensal
    -- MM/YYYY
    RETURN DATE_FORMAT(dt, '%m/%Y');
  ELSE
    SET d = day(dt);
    SET m = month(dt);
    SET y = year(dt);

    IF p = 'Q' OR p = 'G' THEN -- Quinzenal/Quatorzenal
      -- (M*2 + D - 2)
      SET n = m * 2 + d - 2;
    ELSEIF p = 'S' THEN -- Semanal
      -- (M*4 + D - 4)
      SET n = m * 4 + d - 4;
    ELSE
      SET n = m;
    END IF;

    RETURN CONCAT(LPAD(n, 2, '0'), '/', y);
  END IF;
END
/

DELIMITER ;

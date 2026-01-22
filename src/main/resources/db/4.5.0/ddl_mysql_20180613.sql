/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     13/06/2018 15:01:11                          */
/*==============================================================*/

INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('1',  'Cônjuge');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('2',  'Filho');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('3',  'Companheiro');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('4',  'Pai');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('5',  'Mãe');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('6',  'Tutelado');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('15', 'Menor sob guarda');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('16', 'Irmão');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('18', 'Padrasto');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('28', 'Madrasta');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('29', 'Enteado');
INSERT INTO tb_grau_parentesco (GRP_CODIGO, GRP_DESCRICAO) VALUES ('30', 'Curatelado');

alter table ta_beneficiario
   add column GRP_CODIGO varchar(32);

alter table tb_beneficiario
   add column GRP_CODIGO varchar(32);

update ta_beneficiario 
   set GRP_CODIGO = BFC_GRAU_PARENTESCO;

update tb_beneficiario 
   set GRP_CODIGO = BFC_GRAU_PARENTESCO;

alter table ta_beneficiario
   drop column BFC_GRAU_PARENTESCO;

alter table tb_beneficiario
   drop column BFC_GRAU_PARENTESCO;

alter table ta_beneficiario add constraint FK_R_705 foreign key (GRP_CODIGO)
      references tb_grau_parentesco (GRP_CODIGO) on delete restrict on update restrict;

alter table tb_beneficiario add constraint FK_R_704 foreign key (GRP_CODIGO)
      references tb_grau_parentesco (GRP_CODIGO) on delete restrict on update restrict;


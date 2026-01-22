/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/08/2018 14:51:00                          */
/*==============================================================*/


alter table ta_beneficiario
   add NAC_CODIGO varchar(32);

alter table tb_beneficiario
   add NAC_CODIGO varchar(32);

/*==============================================================*/
/* Table: tb_nacionalidade                                      */
/*==============================================================*/
create table tb_nacionalidade
(
   NAC_CODIGO           varchar(32) not null,
   NAC_DESCRICAO        varchar(100) not null,
   primary key (NAC_CODIGO)
) ENGINE=InnoDB;

alter table tb_natureza_consignataria
   add NCA_EXIBE_SER char(1) not null default 'S';

alter table ta_beneficiario add constraint FK_R_721 foreign key (NAC_CODIGO)
      references tb_nacionalidade (NAC_CODIGO) on delete restrict on update restrict;

alter table tb_beneficiario add constraint FK_R_720 foreign key (NAC_CODIGO)
      references tb_nacionalidade (NAC_CODIGO) on delete restrict on update restrict;

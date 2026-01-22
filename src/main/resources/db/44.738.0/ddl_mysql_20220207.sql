/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     28/01/2022 10:05:28                          */
/*==============================================================*/


alter table ta_beneficio
   add BEN_TEXTO_COR text;

alter table ta_beneficio
   add BEN_IMAGEM_BENEFICIO longblob;

alter table ta_beneficio
   add BEN_LINK_BENEFICIO varchar(255);

alter table ta_beneficio
   add BEN_TEXTO_LINK_BENEFICIO varchar(255);

alter table tb_beneficio
   add BEN_TEXTO_COR text;

alter table tb_beneficio
   add BEN_IMAGEM_BENEFICIO longblob;

alter table tb_beneficio
   add BEN_LINK_BENEFICIO varchar(255);

alter table tb_beneficio
   add BEN_TEXTO_LINK_BENEFICIO varchar(255);

alter table tb_provedor_beneficio
   add PRO_AGRUPA char(1) not null default 'N';


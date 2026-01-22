#!/bin/bash

ARQUIVO=$1
DIR=$2

echo "Separating file ${ARQUIVO} into directory ${DIR}"
for i in $(grep '<xsd:schema ' ${ARQUIVO} | cut -d= -f 5 | sed s/[^[:alpha:]]//g); do 
  echo "Processing ${i} ..."
  ggrep -Pzo "(?s)<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" attributeFormDefault=\"qualified\" elementFormDefault=\"qualified\" targetNamespace=\"${i}\".*?</xsd:schema>" ${ARQUIVO} | xmllint -format - > ${DIR}/${i}.xsd;
done
echo "done"


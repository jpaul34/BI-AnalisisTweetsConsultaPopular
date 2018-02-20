import sys
import json
import urllib2
import re
from elasticsearch import Elasticsearch
import pandas as pd
import matplotlib.pyplot as plt
from textblob import TextBlob
from pprint import pprint

#url = 'http://127.0.0.1:5984/consultapopulainge/_design/consultapopularEC/_view/consultaquito'
#url = 'http://127.0.0.1:5984/consultapopulainge/_design/consultapopularEC/_view/consultacuenca'
url = 'http://127.0.0.1:5984/consultapopulainge/_design/consultapopularEC/_view/consultaguayaquil'


req = urllib2.Request(url)
f = urllib2.urlopen(req)
d = json.loads(f.read())

es = Elasticsearch([{'host': 'localhost', 'port': 9200}])

i=1
si=0
no=0
neu=0

archivo = open("E:\\json\\resultado_gua.json","a")
archivo.write('[')

for x in d['rows']:
    a = x['value']
    text = ''
    for letra in a:
    	if re.match('([A-Za-z0-9.#;\s])', letra):
    		text += letra

    sentiment = ""

    if "NO a esta consulta"in text or "#EcuadorDiceNO" in text or "#DilesNO" in text or "#TodoNO" in text or "#Todono" in text or "Nooo" in text or "7 veces n" in text or "#YoVotoNO" in text or "#7VecesNO" in text or "#PorLaPatriaDilesNO" in text or "#YoVotNO" in text or "vota no" in text or "MoreNO" in text or "#PichinchaDiceN" in text or "#PichinchaConElNo" in text or "NON" in text or "#DilesN" in text or "#SieteVecesN" in text or "NoN" in text or "#ConsultaMentirosa" in text or "Todito N" in text or "Todo N" in text or "#NoALaConsultaMa" in text or "#ElPuebloContigoRafael" in text or "#GuayasDiceN" in text or "traidor" in text:
            sentiment = "neg"
            no = no + 1
            data = {}
    else:
        if  "#ConsultaPopular2018" in text or "#ReferendumConsultaPopular2018febrero04" in text or "@Lenin" in text or "#VotacionesEcuador" in text or "#consulta2018" in text or "#votonacional" in text or "#votaciones" in text or "@MashiRafael" in text or "#GuayasDiceN" in text or "#PichinchaDiceN" in text or "#PichinchaConElNo" in text or "#votaconresponsabilidad" in text or "#votaconconciencia" in text or "#EcuadorSaleAVotar" in text:
            if "NO a esta consulta"in text or "#EcuadorDiceNO" in text or "#DilesNO" in text or "#TodoNO" in text or "#Todono" in text or "Nooo" in text or "7 veces n" in text or "#YoVotoNO" in text or "#7VecesNO" in text or "#PorLaPatriaDilesNO" in text or "#YoVotNO" in text or "vota no" in text or "MoreNO" in text or "#PichinchaDiceN" in text or "#PichinchaConElNo" in text or "NON" in text or "#DilesN" in text or "#SieteVecesN" in text or "NoN" in text or "#ConsultaMentirosa" in text or "Todito N" in text or "Todo N" in text or "#NoALaConsultaMa" in text or "#ElPuebloContigoRafael" in text or "#GuayasDiceN" in text or "traidor" in text:
                archivo.write("\n" + "\t")
                sentiment = "neg"
                no = no + 1
            else:
                if "Si y solo Si" in text or "Si7VecesSi"in text or"SI al cambio" in text or"Ecuador decide si" in text or "Dile SI a la consulta" in text or "S a la consulta" in text or "S a todas las preguntas" in text or "S en la consulta"in text or "S en consulta"in text or "Si a la consulta" in text or "si en la consulta"in text or "7 veces Si" in text or "respaldo Lenin" in text or "SI a la consulta"in text or "#7VecesS" in text or "#7VecesSi" in text or "#VotaS" in text or "#votaciones2018S" in text or "#Votar" in text or "#votoreflexivo" in text or "#EcuadorDiceSi" in text or "#TerceraVaECUADOR" in text or "#SRotundo" in text or "#EcuadorVotaS" in text or "#Vota7vecesS." in text or "#TerceraVa" in text or "#VotoEnCasa" in text or "#votaciones2018SI" in text or "#VotaTodoS" in text or "#votaciones2018s" in text or "#consulta2018" in text or "#VotaTodoSi" in text or "#VotaNulo" in text or "#ATuFuturoDileSi" in text or "#EcuadorDiceS" in text or "#GuayasDiceS" in text or "#TodoSi" in text or "#TodoSI" in text or "#TodoS" in text or "#Si" in text or "#NOALaViolencia" in text or "#JuntosPorElS" in text or "#ObvioQueS" in text or "vota si" in text or "7 veces s" in text or "#NoBotesTuVoto" in text or "#PichinchaDiceS" in text or "Todito S":
                    archivo.write("\n" + "\t")
                    sentiment = "pos"
                    si = si + 1
                else:
                    sentiment="neu"
                    neu = neu + 1
        else:
            if  "Si y solo Si" in text or  "Si7VecesSi"in text or "S en las 7 preguntas" in text or "SI al cambio" in text or "Ecuador decide si" in text or  "Dile SI a la consulta" in text or "S a la consulta" in text or "S a todas las preguntas" in text or "S en la consulta"in text or "S en consulta"in text or "Si a la consulta" in text or "si en la consulta"in text or "7 veces Si" in text or"respaldo Lenin" in text or "por el si" in text or "SI a la consulta"in text or "#7VecesS" in text or "#7VecesSi" in text or "#VotaS" in text or "#votaciones2018S" in text or "#Votar" in text or "#votoreflexivo" in text or "#EcuadorDiceSi" in text or "#TerceraVaECUADOR" in text or "#SRotundo" in text or "#EcuadorVotaS" in text or "#Vota7vecesS." in text or "#TerceraVa" in text or "#VotoEnCasa" in text or "#votaciones2018SI" in text or "#VotaTodoS" in text or "#votaciones2018s" in text or "#consulta2018" in text or "#VotaTodoSi" in text or "#VotaNulo" in text or "#ATuFuturoDileSi" in text or "#EcuadorDiceS" in text or "#GuayasDiceS" in text or "#TodoSi" in text or "#TodoSI" in text or "#TodoS" in text or "#Si" in text or "#NOALaViolencia"in text or "#JuntosPorElS" in text or "#ObvioQueS" in text or "vota si" in text or "7 veces s" in text or "#NoBotesTuVoto" in text or "#PichinchaDiceS"in text or "Todito S"in text:
                archivo.write("\n" + "\t")
                sentiment = "pos"
                si = si + 1
    archivo.write("\n" + "\t")
    data = {}
    data['text'] = text
    data['label'] = sentiment
    json_data = json.dumps(data)
    archivo.write(json_data)
    archivo.write(",")
    i=i+1
    sentiment=""

archivo.write("\n"+']')
archivo.close

total =0.0
total = float(si) + float(no)
sii = float("{0:.2f}".format(si/total))
noo = float("{0:.2f}".format(no/total))
print("Si: "+str(sii)+"\nNo: "+str(noo))

raw_data = {'Sentimientos': ['SI', 'NO'],
'Cantidad': [si, no]}

df = pd.DataFrame(raw_data, columns = ['Sentimientos','Cantidad'])

plt.figure(figsize=(16,8))

ax1 = plt.subplot(121, aspect='equal')
df.plot(kind='pie', y = 'Cantidad', ax=ax1, autopct='%1.1f%%', startangle=90, shadow=False, labels=df['Sentimientos'], legend = False, fontsize=14)



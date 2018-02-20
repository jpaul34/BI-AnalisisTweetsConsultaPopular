from textblob.classifiers import NaiveBayesClassifier

ftrain=open('E:\\json\\resultado_uio.json', 'r')
cl = NaiveBayesClassifier(ftrain,format="json")
ftrain.close
print("Testeando")
ftest=open('E:\\json\\resultado_cue.json','r')
a=cl.accuracy(ftest, format="json")
ftest.close
print("Presision: "+str(a))


import os
import shutil
import sys
import time
import traceback

# Argumentos del programa
# LOG_FILE = os.path.normpath(sys.argv[0])

# Redirigimos la salida estandar a un fichero de log para poderlo procesar desde fuera
#logger = open(LOG_FILE, 'w')
#sys.stdout = logger
#sys.stderr = logger
	
try:
	
	# WAS:  Nombres de celula, nodo y servidor
	cellName = AdminConfig.showAttribute(AdminConfig.list('Cell'),"name")
	nodeName = AdminConfig.showAttribute(AdminConfig.list('Node'),"name")
	serverName = AdminConfig.showAttribute(AdminConfig.list('Server'),"name")
	cell = AdminConfig.getid("/Cell:"+cellName+"/")
	node = AdminConfig.getid("/Node:"+nodeName+"/")
	server = AdminConfig.getid("/Server:"+serverName+"/")
	
	#############################################################################
	#
	#	Funciones Helper
	#
	#############################################################################
	
	def ListPort():
		NamedEndPoints = AdminConfig.list( "NamedEndPoint" , node).split(lineSeparator)
		print "#EndPoints:"
		for namedEndPoint in NamedEndPoints:
			endPointName = AdminConfig.showAttribute(namedEndPoint, "endPointName" )
			endPoint = AdminConfig.showAttribute(namedEndPoint, "endPoint" )
			host = AdminConfig.showAttribute(endPoint, "host" )
			port = AdminConfig.showAttribute(endPoint, "port" )
			print "EndPoint_" +  endPointName + "=" + host + ":" + port
		
	ListPort()

except:
	print "Se ha producido una excepción: "
	traceback.print_exc()
	# logger.close()

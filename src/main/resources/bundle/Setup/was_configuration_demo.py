import os
import sys

# Redirigimos la salida estándar
#LOG_FILE = os.path.normpath(sys.argv[0])
#logger = open(LOG_FILE, 'w')
#sys.stdout = logger
#sys.stderr = logger

try:
	

	# Configuración del WAS de arquitectura

	# Constantes
	oraJdbcProvName = "ARQ - Oracle JDBC Driver (XA)"
	db2JdbcProvName = "ARQ - DB2 Universal JDBC Driver Provider (XA)"

	demoWebDataSource1 = "ARQ - DEMO - dsi_controlBPM ds1"
	demoWebDataSource2 = "ARQ - DEMO - dsi_controlBPM ds2"

	# Recuperar los nombres de celula, nodo y servidor
	cellName = AdminConfig.showAttribute(AdminConfig.list('Cell'),"name")
	nodeName = AdminConfig.showAttribute(AdminConfig.list('Node'),"name")
	serverName = AdminConfig.showAttribute(AdminConfig.list('Server'),"name")

	# Recuperar los objetos de celula, nodo y servidor
	cell = AdminConfig.getid("/Cell:"+cellName+"/")
	node = AdminConfig.getid("/Node:"+nodeName+"/")

	# Debug info
	print "Cell:"+cellName+",Node:"+nodeName+",Server:"+serverName

	#############################################################################
	#
	#	Instalar demo de Arquitectura
	#
	#############################################################################

	print "Instalando demo de arquitectura..."

	# Desinstalar Provider Oracle
	oracleJDBCProv = AdminConfig.getid('/Cell:'+cellName+'/JDBCProvider:'+oraJdbcProvName+'/')
	db2JDBCProv = AdminConfig.getid('/Cell:'+cellName+'/JDBCProvider:'+db2JdbcProvName+'/')

	#es.generali.dsi_controlBPM.datasource.PRC=jdbc/PrcXA
	#jdbc:oracle:thin:@10.232.235.209:1521:bovbpm4
	#es.generali.dsi_controlBPM.datasource.SGP=jdbc/SgpXA
	#jdbc:oracle:thin:@10.232.235.209:1521:bovbpm4

	ds = AdminConfig.getid("/DataSource:"+demoWebDataSource1+"/")
	if len(ds) > 0:
		print "Uninstalling "+demoWebDataSource1+" DataSource..."
		AdminConfig.remove(ds)

	print "Installing "+demoWebDataSource1+" DataSource..."
	dsAttrs = [['name', demoWebDataSource1],['jndiName','jdbc/PrcXA'],['datasourceHelperClassname','com.ibm.websphere.rsadapter.Oracle11gDataStoreHelper'],['authDataAlias',nodeName+'/OWBPM01'],['xaRecoveryAuthAlias',nodeName+'/OWBPM01']]
	ds = AdminConfig.create('DataSource', oracleJDBCProv, dsAttrs)
	ds_props = AdminConfig.create('J2EEResourcePropertySet', ds, [])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','URL'],['type','java.lang.String'],['value','jdbc:oracle:thin:@10.232.235.209:1521:bovbpm4']])

	ds = AdminConfig.getid("/DataSource:"+demoWebDataSource2+"/")
	if len(ds) > 0:
		print "Uninstalling "+demoWebDataSource2+" DataSource..."
		AdminConfig.remove(ds)

	print "Installing "+demoWebDataSource2+" DataSource..."
	dsAttrs = [['name', demoWebDataSource2],['jndiName','jdbc/SgpXA'],['datasourceHelperClassname','com.ibm.websphere.rsadapter.Oracle11gDataStoreHelper'],['authDataAlias',nodeName+'/OWBPM02'],['xaRecoveryAuthAlias',nodeName+'/OWBPM02']]
	ds = AdminConfig.create('DataSource', oracleJDBCProv, dsAttrs)
	ds_props = AdminConfig.create('J2EEResourcePropertySet', ds, [])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','URL'],['type','java.lang.String'],['value','jdbc:oracle:thin:@10.232.235.209:1521:bovbpm4']])

	#############################################################################
	#
	#	SALVAR CONFIGURACIÓN
	#
	#############################################################################

	# Informar al usuario que hemos acabado de configurar
	print "Demo instalada"

	# Guardar configuración
	print "Salvando configuración..."
	AdminConfig.save()
	print "Configuración salvada"
except:
	print "Se ha producido una excepción"
	traceback.print_exc()
	#logger.close()

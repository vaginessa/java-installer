import os
import shutil
import sys
import time
import traceback

# Argumentos del programa
INSTALLER_DIR = sys.argv[0]
WAS_DIR = sys.argv[1]
#LOG_FILE = os.path.normpath(sys.argv[2])
CONFIG_DIR = sys.argv[2]
LIB_GENERALI_PATH = sys.argv[3]

# Redirigimos la salida estandar a un fichero de log para poderlo procesar desde fuera
#logger = open(LOG_FILE, 'w')
#sys.stdout = logger
#sys.stderr = logger
	
try:
	
	# CICS
	cicsRAName = "ECIXAResourceAdapter"
	CICS_JCA_DRIVER_PATH_FROM = os.path.join(INSTALLER_DIR,"Libraries","Drivers","CICS","cicseciXA.rar")
	CICS_CF_CICSKW = "ARQ - CICS - KW"
	CICS_CF_CICSMW = "ARQ - CICS - MW"
	CICS_CF_CICSCW = "ARQ - CICS - CW"

	# Oracle
	oraJdbcProvName = "ARQ - Oracle JDBC Driver (XA)"
	ORACLE_JDBC_DRIVER_PATH_FROM = os.path.join(INSTALLER_DIR,"Libraries","Drivers","Oracle","ojdbc6.jar")
	ORACLE_JDBC_DRIVER_PATH_TO = os.path.join(WAS_DIR,"drivers","oracle","ojdbc6.jar")
	ORACLE_JDBC_DRIVER_PATH_ENV_ENTRY_NAME = "ORACLE_JDBC_DRIVER_PATH"
	ORACLE_JDBC_DRIVER_PATH = os.path.join(WAS_DIR,"drivers","oracle")
	ORACLE_JDBC_PROV4APP_NAME = "APP - Oracle JDBC Driver (XA)"

	# DB2
	db2JdbcProvName = "ARQ - DB2 Universal JDBC Driver Provider (XA)"
	dataSourceCajamarName = "ARQ - DB2 cajamar"
	dataSourceCajamarSGName = "ARQ - DB2 cajamar seguros generales"
	dataSourceGeneraliName = "ARQ - DB2 generali"
	DB2_JDBC_PROV4APP_NAME = "APP - DB2 Universal JDBC Driver Provider (XA)"
	APP_ORACLE_DATASOURCE_EXAMPLE = "APP - Oracle Application Datasource Example"
	APP_DB2_DATASOURCE_EXAMPLE = "APP - DB2 Application Datasource Example"
	DB2_JDBC_DRIVER_PATH_FROM = os.path.join(INSTALLER_DIR,"Libraries","Drivers","DB2","db2jcc.jar")
	DB2_JDBC_DRIVER_PATH_TO = os.path.join(WAS_DIR,"drivers","db2","db2jcc.jar")
	DB2_LICENSE_PATH_FROM = os.path.join(INSTALLER_DIR,"Libraries","Drivers","DB2","db2jcc_license_cisuz.jar")
	# DB2_LICENSE_PATH_TO = os.path.join(WAS_DIR,"universalDriver","lib","db2jcc_license_cisuz.jar")
	DB2_LICENSE_PATH_TO = os.path.join(WAS_DIR,"drivers","db2","db2jcc_license_cisuz.jar")
	DB2_JDBC_DRIVER_PATH_ENV_ENTRY_NAME = "DB2UNIVERSAL_JDBC_DRIVER_PATH"
	DB2_JDBC_DRIVER_PATH = os.path.join(WAS_DIR,"drivers","db2")

	# JMS
	JMS_PROVIDER_DEFAULT = "WebSphere MQ JMS Provider"
	JMS_MQCFName = "qcf"

	# JVM
	JVM_SERVER_HEAPSIZE_PROPERTY_NAME = "-maximumHeapSize"
	JVM_SERVER_HEAPSIZE = "1024"
	ARQ_SPRING_PROFILE_PROPERTY_NAME = "spring.profiles.active"
	ARQ_SPRING_PROFILE = "test"

	# WAS:  Nombres de celula, nodo y servidor
	cellName = AdminConfig.showAttribute(AdminConfig.list('Cell'),"name")
	nodeName = AdminConfig.showAttribute(AdminConfig.list('Node'),"name")
	serverName = AdminConfig.showAttribute(AdminConfig.list('Server'),"name")
	cell = AdminConfig.getid("/Cell:"+cellName+"/")
	node = AdminConfig.getid("/Node:"+nodeName+"/")
	#server = AdminConfig.getid("/Cell:"+cellName+"/Node:"+nodeName+"/Server:"+serverName+"/")
	server = AdminConfig.getid("/Server:"+serverName+"/")
	
	

	# Debug info
	print "INSTALLER DIR: " + INSTALLER_DIR
	print "WAS DIR: " + WAS_DIR
	print "Cell:"+cellName+",Node:"+nodeName+",Server:"+serverName
	print "sys.version: "+sys.version
	
	lineSeparator = java.lang.System.getProperty('line.separator')
	
	#############################################################################
	#
	#	Init logger
	#
	#############################################################################
	
	#logger = logging.getLogger('arq')
	#hdlr = logging.FileHandler(INSTALLER_DIR+".was_configuration.log")
	#formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
	#hdlr.setFormatter(formatter)
	#logger.addHandler(hdlr) 
	#logger.setLevel(logging.DEBUG)

	#############################################################################
	#
	#	Funciones Helper
	#
	#############################################################################

	def removeObject( path ):
		objs = AdminConfig.getid(path)
		if len(objs) > 0:
			for obj in objs.split(java.lang.System.getProperty("line.separator")):
				print "Uninstalling '"+obj+"'..."
				AdminConfig.remove(obj)
				print "'"+obj+"' uninstalled"

	def setEnvVar( envEntryName, envEntryValue ):
		varSubstitutions = AdminConfig.list("VariableSubstitutionEntry",cell).split(java.lang.System.getProperty("line.separator"))
		for varSubst in varSubstitutions:
			getVarName = AdminConfig.showAttribute(varSubst, "symbolicName")
			getVarValue = AdminConfig.showAttribute(varSubst, "value")
			#print "Environment variable '"+repr(getVarValue)
			if getVarName == envEntryName:
				print "Setting environment variable '"+varSubst+"' '"+envEntryName+"' to '"+envEntryValue+"'..."
				# TODO: Validar que con el doble backslash funciona. Sino, intentar hacer un replace
				AdminConfig.modify(varSubst,[["value",envEntryValue]])
				break
				
	
	def modifyJ2EEResourceProperty( propertySet, name, newValue ):
		rps = AdminConfig.list("J2EEResourceProperty", propertySet).splitlines() 
		for rp in rps:
			if (AdminConfig.showAttribute(rp, "name") ==  name):
				print "Modifying J2EEResourceProperty '"+name+"'='"+newValue+"'..."
				AdminConfig.modify(rp, [["value", newValue]]) 
				break
	
	def setJVMEnvVar( envEntryName, envEntryValue ):
		processDef = AdminConfig.list('JavaProcessDef', server)
		processEnv = AdminConfig.showAttribute(processDef, 'environment').replace('[','').replace(']','')
		propSet = processEnv.split(' ')
		for prop in propSet:
			if (prop == ""):
				continue
			print AdminConfig.showAttribute(prop, "name")
			if (AdminConfig.showAttribute(prop, "name") == envEntryName):
				print "Setting environment variable '"+envEntryName+"'='"+envEntryValue+"'..."
				AdminConfig.modify(prop, [["value",envEntryValue]])
				return
		print "Adding environment variable '"+envEntryName+"'='"+envEntryValue+"'..."
		AdminConfig.modify(processDef, [["environment",[[['name',envEntryName],['value',envEntryValue],['description','']]]]])
		
		
	
	#############################################################################
	#
	#	Espacio reservado para ejecutar pruebas rápidas.  Borrar después!
	#
	#############################################################################
	
	#############################################################################
	#
	#	Configuración Contenedor Web
	#
	#############################################################################
	
	print "Configurando contenedor web..."
	AdminServerManagement.configureCookieForServer(nodeName, serverName, 'JSESSIONID', ' ', -1, 'false', [['httpOnly', 'false']])
	
	print "Contenedor web configurado."
	
	#############################################################################
	#
	#	Autenticación JAAS
	#
	#############################################################################
	
	print "Configurando autenticación JAAS: kwasdb4"
	authEntries = AdminTask.listAuthDataEntries().splitlines()
	for authEntry in authEntries:
		if authEntry.count("alias kwasdb4")>0:
			AdminTask.deleteAuthDataEntry('-alias kwasdb4')
	AdminResources.createJAASAuthenticationAlias("kwasdb4","kwasdb4", "db4kwas")
	
	print "Configurando autenticación JAAS: mq"
	authEntries = AdminTask.listAuthDataEntries().splitlines()
	for authEntry in authEntries:
		if authEntry.count("alias mq")>0:
			AdminTask.deleteAuthDataEntry('-alias mq')
	AdminResources.createJAASAuthenticationAlias("mq","mqm", "mqm")


	#############################################################################
	#
	#	Configuración JMS
	#
	#############################################################################

	jmsProv = AdminConfig.getid("/Cell:"+cellName+"/JMSProvider:"+JMS_PROVIDER_DEFAULT)
	print "JMSProvider: "+jmsProv
	removeObject("/Cell:"+cellName+"/JMSProvider:"+JMS_PROVIDER_DEFAULT+"/MQQueueConnectionFactory:"+JMS_MQCFName)

	print "Installing "+JMS_MQCFName+" JMS Connection..."
	jmsCF = AdminJMS.createWMQConnectionFactory(cell, JMS_MQCFName, "jms/qcf", [["type", "QCF"], ["description", "Descripción"], ["qmgrHostname", "mqs4v1.si.generali.es"], ["qmgrPortNumber", "1414"]])  
	print "JMS Connection factory '"+JMS_MQCFName+"' installed"
	
	print "Installing MQ Activation Specification..."
	activationSpecs = AdminTask.listWMQActivationSpecs(cell).splitlines()
	for activationSpec in activationSpecs:
		if activationSpec.count("ClausulaMDB")>0:
			print "Deleting WMQ ActivationSpec '"+activationSpec+"'..."
			AdminTask.deleteWMQActivationSpec(activationSpec)
	print "Especificación WMQ '" + AdminJMS.createWMQActivationSpec(cell, "ClausulaMDB" , "ClausulaMDB", "jms/clausulas", "javax.jms.Queue", "qmgrName=INEMQS4,authAlias=mq,qmgrHostname=mqs4v1.si.generali.es,qmgrPortNumber=1414,qmgrSvrconnChannel=CPZMQS4.INEMQS4") + "' creada'"
	
	print "Installing MQ Queue..."
	wmqQueues = AdminTask.listWMQQueues(cell).splitlines()
	for wmqQueue in wmqQueues:
		if wmqQueue.count("cola_clausulas")>0:
			print "Deleting WMQ Queue '"+wmqQueue+"'..."
			AdminTask.deleteWMQQueue(wmqQueue)
	print "Cola WMQ '" + AdminJMS.createWMQQueue(cell, "cola_clausulas", "jms/clausulas", "INEMQS4.ARQ.TRADUCIO.LITERALS", "qmgr=INEMQS4") + "' creada"

	#############################################################################
	#
	#	INSTALAR JCA Providers & resources
	#
	#############################################################################

	# Desinstalar el conector JCA de CICs
	removeObject("/Cell:"+cellName+"/Node:"+nodeName+"/J2CResourceAdapter:"+cicsRAName+"/")

	# Instalar el conector JCA de CICs
	print "Installing CICs JCA Connector..."
	cicsRA = AdminJ2C.installJ2CResourceAdapter(nodeName, CICS_JCA_DRIVER_PATH_FROM, cicsRAName)

	# Instalar el JCA Connection Factory
	print "Installing CICs Connection Factory "+CICS_CF_CICSKW+"..."
	cicsCF = AdminTask.createJ2CConnectionFactory(cicsRA, ['-name', CICS_CF_CICSKW, '-jndiName', 'eis/ctg/CICSKW', '-connectionFactoryInterface', 'javax.resource.cci.ConnectionFactory','-description','CICSKW Connection Factory'])
	cicsCF_props = AdminConfig.showAttribute(cicsCF, 'propertySet')
	modifyJ2EEResourceProperty(cicsCF_props,"ConnectionURL","tcp://host/")
	modifyJ2EEResourceProperty(cicsCF_props,"ServerName","CICS4KW")
	modifyJ2EEResourceProperty(cicsCF_props,"PortNumber","15400")
	
	print "Installing CICs Connection Factory "+CICS_CF_CICSMW+"..."
	cicsCF = AdminTask.createJ2CConnectionFactory(cicsRA, ['-name', CICS_CF_CICSMW, '-jndiName', 'eis/ctg/CICSMW', '-connectionFactoryInterface', 'javax.resource.cci.ConnectionFactory','-description','CICSMW Connection Factory'])
	cicsCF_props = AdminConfig.showAttribute(cicsCF, 'propertySet')
	modifyJ2EEResourceProperty(cicsCF_props,"ConnectionURL","tcp://host/")
	modifyJ2EEResourceProperty(cicsCF_props,"ServerName","CICS4MW")
	modifyJ2EEResourceProperty(cicsCF_props,"PortNumber","15400")
	
	print "Installing CICs Connection Factory "+CICS_CF_CICSCW+"..."
	cicsCF = AdminTask.createJ2CConnectionFactory(cicsRA, ['-name', CICS_CF_CICSCW, '-jndiName', 'eis/ctg/CICSCW', '-connectionFactoryInterface', 'javax.resource.cci.ConnectionFactory','-description','CICSCW Connection Factory'])
	cicsCF_props = AdminConfig.showAttribute(cicsCF, 'propertySet')
	modifyJ2EEResourceProperty(cicsCF_props,"ConnectionURL","tcp://host/")
	modifyJ2EEResourceProperty(cicsCF_props,"ServerName","CICS4CW")
	modifyJ2EEResourceProperty(cicsCF_props,"PortNumber","15400")

	#############################################################################
	#
	#	INSTALAR JDBC Providers & resources de Arquitectura
	#
	#############################################################################

	# Desinstalar Provider Oracle
	removeObject("/Cell:"+cellName+"/JDBCProvider:"+oraJdbcProvName+"/");

	# Copiar el driver en la carpeta AppServer\drivers\oracle
	print "Copying file '"+ORACLE_JDBC_DRIVER_PATH_FROM+"' to '"+ORACLE_JDBC_DRIVER_PATH_TO+"'"
	if not os.path.exists(os.path.dirname(ORACLE_JDBC_DRIVER_PATH_TO)):
		print "Creating dir '"+os.path.dirname(ORACLE_JDBC_DRIVER_PATH_TO)+"'..." 
		os.makedirs(os.path.dirname(ORACLE_JDBC_DRIVER_PATH_TO))
		
	shutil.copyfile(ORACLE_JDBC_DRIVER_PATH_FROM, ORACLE_JDBC_DRIVER_PATH_TO)

	# Establecer la variable de entorno del WAS
	setEnvVar(ORACLE_JDBC_DRIVER_PATH_ENV_ENTRY_NAME,ORACLE_JDBC_DRIVER_PATH)
			
	# Si hace falta el nativePath añadir esto: '-nativePath', '${DB2UNIVERSAL_JDBC_DRIVER_NATIVEPATH}'
	print "Installing '"+oraJdbcProvName+"'..."
	oracleJDBCProv = AdminTask.createJDBCProvider(['-scope', 'Cell='+cellName, '-databaseType', 'Oracle', '-providerType', 'Oracle JDBC Driver', '-implementationType', 'Origen de datos XA', '-name', oraJdbcProvName, '-description', oraJdbcProvName, '-classpath', '${ORACLE_JDBC_DRIVER_PATH}/ojdbc6.jar']) 

	############################################################################
			
	# Desinstalar Provider DB2
	print "Uninstalling '"+db2JdbcProvName+"'..."
	removeObject('/Cell:'+cellName+'/JDBCProvider:'+db2JdbcProvName+'/')
	
	# Copiar el driver en la carpeta AppServer\drivers\db2
	print "Copying file '"+DB2_JDBC_DRIVER_PATH_FROM+"' to '"+DB2_JDBC_DRIVER_PATH_TO+"'"
	
	if not os.path.exists(os.path.dirname(DB2_JDBC_DRIVER_PATH_TO)):
		print "Creating dir '"+os.path.dirname(DB2_JDBC_DRIVER_PATH_TO)+"'..." 
		os.makedirs(os.path.dirname(DB2_JDBC_DRIVER_PATH_TO))
		
	shutil.copyfile(DB2_JDBC_DRIVER_PATH_FROM, DB2_JDBC_DRIVER_PATH_TO)
	
	# Copiar la licencia en la carpeta AppServer\universalDriver\lib
	print "Copying file '"+DB2_LICENSE_PATH_FROM+"' to '"+DB2_LICENSE_PATH_TO+"'"
	shutil.copyfile(DB2_LICENSE_PATH_FROM, DB2_LICENSE_PATH_TO)
	
	# Establecer la variable de entorno del WAS
	setEnvVar(DB2_JDBC_DRIVER_PATH_ENV_ENTRY_NAME,DB2_JDBC_DRIVER_PATH)

	# Instalar
	db2JDBCProv = AdminTask.createJDBCProvider(['-scope', 'Cell='+cellName, '-databaseType', 'DB2', '-providerType', 'DB2 Universal JDBC Driver Provider', '-implementationType', 'Origen de datos XA', '-name', db2JdbcProvName, '-description', db2JdbcProvName, '-classpath', '${DB2UNIVERSAL_JDBC_DRIVER_PATH}/db2jcc.jar;${UNIVERSAL_JDBC_DRIVER_PATH}/db2jcc_license_cu.jar;${DB2UNIVERSAL_JDBC_DRIVER_PATH}/db2jcc_license_cisuz.jar', '-nativePath', '${DB2UNIVERSAL_JDBC_DRIVER_NATIVEPATH}'])

	#############################################################################
	#
	#	INSTALAR JDBC Datasources
	#
	#############################################################################
		
	# Crear orígenes de datos
	# Creación mediante AdminTask. Se deja comentado con el objeto de documentarlo
	#print "Installing "+dataSourceCajamarName+" DataSource..."
	#removeObject("/DataSource:"+dataSourceCajamarName+"/")
	#jdbcAttrs = ['-name', 'DB2 cajamar', '-jndiName','jdbc/mdb2was', '-dataStoreHelperClassName','com.ibm.websphere.rsadapter.DB2UniversalDataStoreHelper', '-configureResourceProperties', '[[databaseName java.lang.String DB2TEST] [driverType java.lang.Integer 4] [serverName java.lang.String host] [portNumber java.lang.Integer 11400]]']
	#ds = AdminTask.createDatasource(db2JDBCProv, jdbcAttrs)

	print "Installing "+dataSourceCajamarName+" DataSource..."
	removeObject("/DataSource:"+dataSourceCajamarName+"/")
	dsAttrs = [['name', dataSourceCajamarName],['jndiName','jdbc/mdb2was'],['datasourceHelperClassname','com.ibm.websphere.rsadapter.DB2UniversalDataStoreHelper'],['authDataAlias','kwasdb4'],['xaRecoveryAuthAlias','kwasdb4']]
	ds = AdminConfig.create('DataSource', db2JDBCProv, dsAttrs)
	ds_props = AdminConfig.create('J2EEResourcePropertySet', ds, [])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','databaseName'],['type','java.lang.String'],['value','DB2TEST']])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','driverType'],['type','java.lang.Integer'],['value','4']])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','serverName'],['type','java.lang.String'],['value','host']])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','portNumber'],['type','java.lang.Integer'],['value','11400']])	

	print "Installing "+dataSourceCajamarSGName+" DataSource..."
	removeObject("/DataSource:"+dataSourceCajamarSGName+"/")
	dsAttrs = [['name', dataSourceCajamarSGName],['jndiName','jdbc/cdb2was'],['datasourceHelperClassname','com.ibm.websphere.rsadapter.DB2UniversalDataStoreHelper'],['authDataAlias','kwasdb4'],['xaRecoveryAuthAlias','kwasdb4']]
	ds = AdminConfig.create('DataSource', db2JDBCProv, dsAttrs)
	ds_props = AdminConfig.create('J2EEResourcePropertySet', ds, [])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','databaseName'],['type','java.lang.String'],['value','DB2TEST']])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','driverType'],['type','java.lang.Integer'],['value','4']])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','serverName'],['type','java.lang.String'],['value','host']])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','portNumber'],['type','java.lang.Integer'],['value','11400']])

	print "Installing "+dataSourceGeneraliName+" DataSource..."
	removeObject("/DataSource:"+dataSourceGeneraliName+"/")
	dsAttrs = [['name', dataSourceGeneraliName],['jndiName','jdbc/kdb2was'],['datasourceHelperClassname','com.ibm.websphere.rsadapter.DB2UniversalDataStoreHelper'],['authDataAlias','kwasdb4'],['xaRecoveryAuthAlias','kwasdb4']]	
	ds = AdminConfig.create('DataSource', db2JDBCProv, dsAttrs)
	ds_props = AdminConfig.create('J2EEResourcePropertySet', ds, [])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','databaseName'],['type','java.lang.String'],['value','DB2TEST']])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','driverType'],['type','java.lang.Integer'],['value','4']])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','serverName'],['type','java.lang.String'],['value','host']])
	AdminConfig.create('J2EEResourceProperty', ds_props, [['name','portNumber'],['type','java.lang.Integer'],['value','11400']])

	#############################################################################
	#
	#	INSTALAR JDBC Providers & resources Aplicativos
	#
	#############################################################################

	oracleJDBCProv4App = AdminConfig.getid('/Cell:'+cellName+'/JDBCProvider:'+ORACLE_JDBC_PROV4APP_NAME+'/')
	if not oracleJDBCProv4App:
		print "Installing "+ORACLE_JDBC_PROV4APP_NAME+" DataSource..."
		oracleJDBCProv4App = AdminTask.createJDBCProvider(['-scope', 'Cell='+cellName, '-databaseType', 'Oracle', '-providerType', 'Oracle JDBC Driver', '-implementationType', 'Origen de datos XA', '-name', ORACLE_JDBC_PROV4APP_NAME, '-description', ORACLE_JDBC_PROV4APP_NAME, '-classpath', '${ORACLE_JDBC_DRIVER_PATH}/ojdbc6.jar']) 
	else:
		print "Not installing "+ORACLE_JDBC_PROV4APP_NAME+" DataSource..."
		
	db2JDBCProv4App = AdminConfig.getid('/Cell:'+cellName+'/JDBCProvider:'+DB2_JDBC_PROV4APP_NAME+'/')
	if not db2JDBCProv4App:
		print "Installing "+DB2_JDBC_PROV4APP_NAME+" DataSource..."	
		db2JDBCProv4App = AdminTask.createJDBCProvider(['-scope', 'Cell='+cellName, '-databaseType', 'DB2', '-providerType', 'DB2 Universal JDBC Driver Provider', '-implementationType', 'Origen de datos XA', '-name', DB2_JDBC_PROV4APP_NAME, '-description', DB2_JDBC_PROV4APP_NAME, '-classpath', '${DB2UNIVERSAL_JDBC_DRIVER_PATH}/db2jcc.jar;${UNIVERSAL_JDBC_DRIVER_PATH}/db2jcc_license_cu.jar;${DB2UNIVERSAL_JDBC_DRIVER_PATH}/db2jcc_license_cisuz.jar', '-nativePath', '${DB2UNIVERSAL_JDBC_DRIVER_NATIVEPATH}'])			
		#db2JDBCProv4App = AdminTask.createJDBCProvider(['-scope', 'Cell='+cellName, '-databaseType', 'Oracle', '-providerType', 'Oracle JDBC Driver', '-implementationType', 'Origen de datos XA', '-name', DB2_JDBC_PROV4APP_NAME, '-description', DB2_JDBC_PROV4APP_NAME, '-classpath', '${ORACLE_JDBC_DRIVER_PATH}/ojdbc6.jar']) 
	else:
		print "Not installing "+DB2_JDBC_PROV4APP_NAME+" DataSource..."

	#jdbc:oracle:thin:@myhost:1521:orcl
	if not AdminConfig.getid("/DataSource:"+APP_DB2_DATASOURCE_EXAMPLE+"/"):
		print "Installing "+APP_DB2_DATASOURCE_EXAMPLE+" DataSource..."
		dsAttrs = [['name',APP_DB2_DATASOURCE_EXAMPLE ],['jndiName','jdbc/dsapp1'],['datasourceHelperClassname','com.ibm.websphere.rsadapter.Oracle11gDataStoreHelper']]
		ds = AdminConfig.create('DataSource', db2JDBCProv4App, dsAttrs)
		ds_props = AdminConfig.create('J2EEResourcePropertySet', ds, [])
		AdminConfig.create('J2EEResourceProperty', ds_props, [['name','URL'],['type','java.lang.String'],['value','jdbc:oracle:oci:@//localhost:1521/sample']])
	else:
		print "Not installing "+APP_DB2_DATASOURCE_EXAMPLE+" DataSource..."
		
	if not AdminConfig.getid("/DataSource:"+APP_ORACLE_DATASOURCE_EXAMPLE+"/"):
		print "Installing "+APP_ORACLE_DATASOURCE_EXAMPLE+" DataSource..."
		dsAttrs = [['name', APP_ORACLE_DATASOURCE_EXAMPLE],['jndiName','jdbc/dsapp2'],['datasourceHelperClassname','com.ibm.websphere.rsadapter.DB2UniversalDataStoreHelper']]
		ds = AdminConfig.create('DataSource', oracleJDBCProv4App, dsAttrs)
		ds_props = AdminConfig.create('J2EEResourcePropertySet', ds, [])
		AdminConfig.create('J2EEResourceProperty', ds_props, [['name','databaseName'],['type','java.lang.String'],['value','DB2TEST']])
		AdminConfig.create('J2EEResourceProperty', ds_props, [['name','driverType'],['type','java.lang.Integer'],['value','4']])
		AdminConfig.create('J2EEResourceProperty', ds_props, [['name','serverName'],['type','java.lang.String'],['value','host']])
		AdminConfig.create('J2EEResourceProperty', ds_props, [['name','portNumber'],['type','java.lang.Integer'],['value','11400']])
	else:
		print "Not installing "+APP_ORACLE_DATASOURCE_EXAMPLE+" DataSource..."
		
	#############################################################################
	#
	#	LIBRARIES / ENVIRONMENT VARIABLES / JAVA SYSTEM PROPERTIES
	#
	#############################################################################
	
	print "Configurando biblioteca (librerías) de Generali..."
	GENERALI_LIB = 'Generali'
	removeObject('/Cell:'+cellName+'/Library:'+GENERALI_LIB+'/')
	# bug al añadir path con espacios: http://www-01.ibm.com/support/docview.wss?uid=swg1PM38888
	library = AdminConfig.create('Library', cell, [['name', GENERALI_LIB],['classPath', LIB_GENERALI_PATH] ])

	print "Configurando definición de proceso JVM..."
	
	# JVM Server properties
	print "Estableciendo propiedad de arquitectura '"+ARQ_SPRING_PROFILE_PROPERTY_NAME+"'==>'"+ARQ_SPRING_PROFILE+"'..."
	AdminTask.setJVMSystemProperties (['-serverName', serverName,  '-nodeName', nodeName, '-propertyName', ARQ_SPRING_PROFILE_PROPERTY_NAME,  '-propertyValue', ARQ_SPRING_PROFILE])
	
	print "Configurando cargador de clases del servidor..."
	AdminServerManagement.configureApplicationServerClassloader(nodeName, serverName, "MULTIPLE", "PARENT_FIRST", GENERALI_LIB)

	# TODO: Validar esto
	print "Estableciendo propiedad de proceso JVM '"+JVM_SERVER_HEAPSIZE_PROPERTY_NAME+"'==>'"+JVM_SERVER_HEAPSIZE+"'..."
	AdminTask.setJVMMaxHeapSize(['-serverName', serverName,  '-nodeName', nodeName, JVM_SERVER_HEAPSIZE_PROPERTY_NAME ,JVM_SERVER_HEAPSIZE])
	
	setJVMEnvVar('FRAMEWORK_CONFIGURATION_PATH',CONFIG_DIR)		

	#############################################################################
	#
	#	SALVAR CONFIGURACIÓN
	#
	#############################################################################

	# Informar al usuario que hemos acabado de configurar
	print "Configuración finalizada"

	# Guardar configuración
	print "Salvando configuración..."
	AdminConfig.save()
	print "Configuración salvada"
except:
	print "Se ha producido una excepción: "
	traceback.print_exc()
	#logger.close()

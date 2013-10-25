package es.generali.arq_sdk.model;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class ModelTest {

	@Test
	public void testGetIBMInstalledPackages() throws Exception {
		List<String> installedPackages = Model.getIBMInstalledPackages();
		if (installedPackages.isEmpty()) {
			Assert.fail("Prueba de integracion fallida. Para que esta prueba funciona tiene que tener instalado un IBM Installation Manager instalado");
		}
	}
}

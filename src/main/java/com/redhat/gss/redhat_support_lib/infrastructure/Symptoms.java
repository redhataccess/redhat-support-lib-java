package com.redhat.gss.redhat_support_lib.infrastructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import com.redhat.gss.redhat_support_lib.errors.RequestException;
import com.redhat.gss.redhat_support_lib.parsers.ExtractedSymptom;
import com.redhat.gss.redhat_support_lib.web.ConnectionManager;

public class Symptoms extends BaseQuery {
    private final static Logger LOGGER = Logger
            .getLogger(Symptoms.class.getName());
	ConnectionManager connectionManager = null;
	static String url = "/rs/symptoms/extractor/";

	public Symptoms(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * Queries the problems RESTful interface with a given file. RESTful method:
	 * https://api.access.redhat.com/rs/problems
	 * 
	 * @param fileName
	 *            File name whose content you want diagnosed.
	 * @return An array of problems.
	 * @throws Exception 
	 */
	public List<ExtractedSymptom> retrieveSymptoms(String fileName) throws Exception {
		WebResource webResource = connectionManager.getConnection().resource(
				connectionManager.getConfig().getUrl() + url);

		ClientResponse resp = upload(webResource, new File(fileName), fileName);
		com.redhat.gss.redhat_support_lib.parsers.ExtractedSymptoms symptoms = resp
				.getEntity(com.redhat.gss.redhat_support_lib.parsers.ExtractedSymptoms.class);
		return symptoms.getExtractedSymptom();
	}

}

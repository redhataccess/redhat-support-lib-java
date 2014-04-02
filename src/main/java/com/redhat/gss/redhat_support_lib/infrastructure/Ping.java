package com.redhat.gss.redhat_support_lib.infrastructure;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

import com.redhat.gss.redhat_support_lib.errors.RequestException;
import com.redhat.gss.redhat_support_lib.helpers.FilterHelper;
import com.redhat.gss.redhat_support_lib.helpers.QueryBuilder;
import com.redhat.gss.redhat_support_lib.parsers.Case;
import com.redhat.gss.redhat_support_lib.web.ConnectionManager;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Ping extends BaseQuery {
	private final static Logger LOGGER = Logger
			.getLogger(Ping.class.getName());
	ConnectionManager connectionManager = null;
	static String url = "/rs/";

	public Ping(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * Queries the API for the given case number. RESTful method:
	 * https://api.access.redhat.com/rs/cases/<caseNumber>
	 * 
	 * @param caseNum
	 *            The exact caseNumber you are interested in.
	 * @return A case object that represents the given case number.
	 * @throws RequestException
	 *             An exception if there was a connection related issue.
	 * @throws MalformedURLException 
	 */
	public String ping() throws RequestException, MalformedURLException {
		WebResource webResource = connectionManager.getConnection().resource(
				connectionManager.getConfig().getUrl() + url);
		return get(webResource, String.class);
	}
}

package com.redhat.gss.redhat_support_lib.infrastructure;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.redhat.gss.redhat_support_lib.api.API;
import com.redhat.gss.redhat_support_lib.errors.RequestException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.Boundary;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

public class BaseQuery {
	private final static Logger LOGGER = Logger.getLogger(API.class.getName());

	protected <T> T get(WebResource webResource, Class<T> c)
			throws RequestException {
		ClientResponse response = webResource.accept(MediaType.APPLICATION_XML).get(
				ClientResponse.class);
		if (response.getStatus() != 200) {
			LOGGER.debug("Failed : HTTP error code : " + response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
			throw new RequestException(response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
		}
		return response.getEntity(c);

	}

	protected ClientResponse add(WebResource webResource, Object object)
			throws RequestException {
		ClientResponse response = (ClientResponse) webResource.accept(
				MediaType.APPLICATION_XML).post(ClientResponse.class, object);
		if (response.getStatus() > 399) {
			LOGGER.debug("Failed : HTTP error code : " + response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
			throw new RequestException(response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
		}
		return response;

	}
	
	protected <T> T add(WebResource webResource, Object object, Class<T> c)
			throws RequestException {
		ClientResponse response = (ClientResponse) webResource.type(MediaType.APPLICATION_XML).accept(
				MediaType.APPLICATION_XML).post(ClientResponse.class, object);
		if (response.getStatus() > 399) {
			LOGGER.debug("Failed : HTTP error code : " + response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
			throw new RequestException(response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
		}
		return response.getEntity(c);

	}

	protected ClientResponse update(WebResource webResource, Object object)
			throws RequestException {
		ClientResponse response = (ClientResponse) webResource.accept(
				MediaType.APPLICATION_XML).put(ClientResponse.class, object);
		if (response.getStatus() > 399) {
			LOGGER.debug("Failed : HTTP error code : " + response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
			throw new RequestException(response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
		}
		return response;

	}

	protected boolean delete(WebResource webResource) throws RequestException {
		ClientResponse response = (ClientResponse) webResource.accept(
				MediaType.APPLICATION_XML).delete(ClientResponse.class);
		if (response.getStatus() > 399) {
			LOGGER.debug("Failed : HTTP error code : " + response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
			throw new RequestException(response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
		}
		return true;
	}

	protected ClientResponse upload(WebResource webResource, File file,
			String description) throws UniformInterfaceException,
			ClientHandlerException, FileNotFoundException, ParseException,
			RequestException {
		FormDataMultiPart part = new FormDataMultiPart();
		if (description != null) {
			part.bodyPart(new FormDataBodyPart("description", description));
		}
		part.bodyPart(new FileDataBodyPart("file", file,
				MediaType.APPLICATION_OCTET_STREAM_TYPE));
		ClientResponse response = (ClientResponse) webResource
				.accept(MediaType.APPLICATION_XML)
				.type(Boundary.addBoundary(MediaType.MULTIPART_FORM_DATA_TYPE))
				.header("description", description)
				.post(ClientResponse.class, part);
		if (response.getStatus() > 399) {
			LOGGER.debug("Failed : HTTP error code : " + response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
			throw new RequestException(response.getClientResponseStatus()
					.getStatusCode()
					+ " - "
					+ response.getClientResponseStatus().getReasonPhrase());
		}
		return response;
	}
}